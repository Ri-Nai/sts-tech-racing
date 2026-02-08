package MapMarks;

import MapMarks.ui.tiles.LargeMapTile;
import MapMarks.ui.tiles.SmallMapTile;
import MapMarks.utils.ColorEnum;
import MapMarks.utils.SoundHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import easel.ui.AnchorPosition;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public class MapTileManager
{
   private static HashMap<MapRoomNode, MapTileMapObject> tracked = new HashMap<>();

   private static int trackedActNum = -1;

   private static MapRoomNode inboundsNode = null;
   private static MapTileMapObject inboundsMapTileMapObject = null;
   private static HashMap<MapRoomNode, HashSet<MapRoomNode>> reachableMap = new HashMap<>();

   private static Color highlightingColor;
   private static boolean reflectionInitialized = false;
   private static boolean isMintySpireLoaded = false;
   private static Field renderingMiniMapField = null;
   private static Field saveOffsetYField = null;

   private static void initReflection() {
     if (reflectionInitialized)
       return;  reflectionInitialized = true;

     if (Loader.isModLoaded("mintyspire")) {
       try {
         Class<?> clazz = Class.forName("mintySpire.patches.map.MiniMapDisplay");
         renderingMiniMapField = clazz.getField("renderingMiniMap");
         saveOffsetYField = clazz.getDeclaredField("saveOffsetY");
         saveOffsetYField.setAccessible(true);
         isMintySpireLoaded = true;
       } catch (Exception exception) {
         System.out.println("MapMarks: Failed to reflect MintySpire fields. " + exception.getMessage());
         isMintySpireLoaded = false;
       }
     }
   }

   private static boolean isRenderingMiniMap() {
     initReflection();
     if (isMintySpireLoaded && renderingMiniMapField != null) {
       try {
         return renderingMiniMapField.getBoolean((Object)null);
       } catch (Exception exception) {
         return false;
       }
     }
     return false;
   }

   private static float getRealOffsetY() {
     if (isMintySpireLoaded && saveOffsetYField != null) {
       try {
         return saveOffsetYField.getFloat((Object)null);
       } catch (Exception exception) {
         return DungeonMapScreen.offsetY;
       }
     }
     return DungeonMapScreen.offsetY;
   }

   private static String getColorName(Color paramColor) {
     for (ColorEnum colorEnum : ColorEnum.values()) {
       if (colorEnum.get().equals(paramColor)) {
         return colorEnum.name();
       }
     }
     return "RED";
   }

   public static ArrayList<String> getCurrentVisualHighlights() {
     ArrayList<String> arrayList = new ArrayList();
     for (Map.Entry<MapRoomNode, MapTileMapObject> entry : tracked.entrySet()) {
       MapTileMapObject mapTileMapObject = (MapTileMapObject)entry.getValue();
       if (mapTileMapObject != null && mapTileMapObject.isHighlighted) {
         MapRoomNode mapRoomNode = (MapRoomNode)entry.getKey();
         String str = getColorName(mapTileMapObject.smallTile.getBaseColor());
         arrayList.add(mapRoomNode.x + "," + mapRoomNode.y + "," + str);
       }
     }
     return arrayList;
   }

   public static ArrayList<String> getSavedHighlights() {
     return new ArrayList<>();
   }

   public static void track(MapRoomNode paramMapRoomNode) {
     int i = AbstractDungeon.actNum;
     if (i != trackedActNum) {
       System.out.println("MapMarks: 检测到层数变化 (" + trackedActNum + " -> " + i + ")，清空旧地图缓存。");
       clear();
       trackedActNum = i;
     }

     MapTileMapObject mapTileMapObject = new MapTileMapObject(paramMapRoomNode);

     String str = MapMarksSaver.getSavedColor(i, paramMapRoomNode.x, paramMapRoomNode.y);

     if (str != null) {
       mapTileMapObject.isHighlighted = true;
       try {
         ColorEnum colorEnum = ColorEnum.valueOf(str);
         Color color = colorEnum.get();
         mapTileMapObject.smallTile.setBaseColor(color);
         mapTileMapObject.largeTile.setBaseColor(color);
       } catch (IllegalArgumentException illegalArgumentException) {
         mapTileMapObject.smallTile.setBaseColor(ColorEnum.RED.get());
         mapTileMapObject.largeTile.setBaseColor(ColorEnum.RED.get());
       }
     }

     tracked.put(paramMapRoomNode, mapTileMapObject);
   }

   public static boolean shouldRenderLarge(MapRoomNode paramMapRoomNode) {
     if (paramMapRoomNode.equals(AbstractDungeon.getCurrMapNode())) {
       return false;
     }
     boolean bool1 = (AbstractDungeon.getCurrRoom()).phase.equals(AbstractRoom.RoomPhase.COMPLETE);
     boolean bool2 = AbstractDungeon.getCurrMapNode().isConnectedTo(paramMapRoomNode);
     if (bool1 && bool2)
       return true;
     if (paramMapRoomNode.hb.hovered)
       return true;
     if (!AbstractDungeon.firstRoomChosen && paramMapRoomNode.y == 0 && (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMPLETE) {
       return true;
     }
     return AbstractDungeon.dungeonMapScreen.map.legend.isIconHovered(paramMapRoomNode.getRoomSymbol(Boolean.valueOf(true)));
   }

   public static boolean isNodeHighlighted(MapRoomNode paramMapRoomNode) {
     MapTileMapObject mapTileMapObject = tracked.get(paramMapRoomNode);
     return (mapTileMapObject != null) ? mapTileMapObject.isHighlighted : false;
   }

   public static boolean isNodeReachable(MapRoomNode paramMapRoomNode) {
     MapTileMapObject mapTileMapObject = tracked.get(paramMapRoomNode);
     return (mapTileMapObject != null) ? mapTileMapObject.isReachable : false;
   }

   public static Color getHighlightedNodeColor(MapRoomNode paramMapRoomNode) {
     MapTileMapObject mapTileMapObject = tracked.get(paramMapRoomNode);
     return (mapTileMapObject != null) ? mapTileMapObject.smallTile.getBaseColor() : Color.WHITE;
   }

   public static void tryRender(SpriteBatch paramSpriteBatch, MapRoomNode paramMapRoomNode, float paramFloat1, float paramFloat2) {
     MapTileMapObject mapTileMapObject = tracked.get(paramMapRoomNode);
     if (mapTileMapObject != null) {
       mapTileMapObject.largeTile.anchoredAt(paramFloat1 - 33.0F, paramFloat2 - 13.0F - 21.0F, AnchorPosition.LEFT_BOTTOM);
       mapTileMapObject.smallTile.anchoredAt(paramFloat1 - 5.0F, paramFloat2 - 13.0F, AnchorPosition.LEFT_BOTTOM);

       if (mapTileMapObject.isHighlighted && mapTileMapObject.isReachable) {
         if (shouldRenderLarge(paramMapRoomNode)) {
           mapTileMapObject.largeTile.render(paramSpriteBatch);
         } else {
           mapTileMapObject.smallTile.render(paramSpriteBatch);
         }
       }

       if (isRenderingMiniMap()) {
         float f1 = DungeonMapScreen.offsetY;
         float f2 = getRealOffsetY();
         float f3 = paramFloat2 - f1 + f2;

         mapTileMapObject.largeTile.anchoredAt(paramFloat1 - 33.0F, f3 - 13.0F - 21.0F, AnchorPosition.LEFT_BOTTOM);
         mapTileMapObject.smallTile.anchoredAt(paramFloat1 - 5.0F, f3 - 13.0F, AnchorPosition.LEFT_BOTTOM);
       }
     }
   }

   public static void updateAllTracked() {
     inboundsNode = null;
     inboundsMapTileMapObject = null;
     Iterator<Map.Entry<MapRoomNode, MapTileMapObject>> iterator = tracked.entrySet().iterator();

     while (iterator.hasNext()) {
       Map.Entry<MapRoomNode, MapTileMapObject> entry = iterator.next();
       ((MapTileMapObject)entry.getValue()).smallTile.update();
       ((MapTileMapObject)entry.getValue()).largeTile.update();
       if (((MapTileMapObject)entry.getValue()).smallTile.isMouseInContentBounds()) {
         inboundsNode = (MapRoomNode)entry.getKey();
         inboundsMapTileMapObject = (MapTileMapObject)entry.getValue();
       }
     }
   }

   private static ArrayList<MapRoomNode> collectDirectChildren(MapRoomNode paramMapRoomNode, HashMap<Pair<Integer, Integer>, MapRoomNode> paramHashMap) {
     ArrayList<MapRoomNode> arrayList = new ArrayList();
     Iterator<MapEdge> iterator = paramMapRoomNode.getEdges().iterator();

     while (iterator.hasNext()) {
       MapEdge mapEdge = iterator.next();
       MapRoomNode mapRoomNode = paramHashMap.get(Pair.of(Integer.valueOf(mapEdge.dstX), Integer.valueOf(mapEdge.dstY)));
       if (mapRoomNode != null) {
         arrayList.add(mapRoomNode);
       }
     }
     return arrayList;
   }

   public static void initializeReachableMap() {
     reachableMap.clear();
     HashMap<Pair<Integer, Integer>, MapRoomNode> hashMap = new HashMap<>();
     tracked.keySet().forEach(paramMapRoomNode -> {
           hashMap.put(Pair.of(Integer.valueOf(paramMapRoomNode.x), Integer.valueOf(paramMapRoomNode.y)), paramMapRoomNode);
         });
     Iterator<MapRoomNode> iterator = tracked.keySet().iterator();

     while (iterator.hasNext()) {
       MapRoomNode mapRoomNode = iterator.next();
       reachableMap.putIfAbsent(mapRoomNode, new HashSet<>());
       HashSet<MapRoomNode> hashSet = reachableMap.get(mapRoomNode);
       ArrayDeque<MapRoomNode> arrayDeque = new ArrayDeque<>(collectDirectChildren(mapRoomNode, (HashMap)hashMap));

       while (!arrayDeque.isEmpty()) {
         MapRoomNode mapRoomNode1 = arrayDeque.remove();
         if (!hashSet.contains(mapRoomNode1)) {
           hashSet.add(mapRoomNode1);
           arrayDeque.addAll(collectDirectChildren(mapRoomNode1, (HashMap)hashMap));
         }
       }
     }
   }

   public static void computeReachable() {
     MapRoomNode mapRoomNode = AbstractDungeon.getCurrMapNode();
     if (mapRoomNode != null) {
       HashSet hashSet = reachableMap.get(mapRoomNode);
       if (hashSet != null && !hashSet.isEmpty()) {
         Iterator<Map.Entry<MapRoomNode, MapTileMapObject>> iterator = tracked.entrySet().iterator();

         while (iterator.hasNext()) {
           Map.Entry<MapRoomNode, MapTileMapObject> entry = iterator.next();
           if (entry.getKey() == mapRoomNode) {
             ((MapTileMapObject)entry.getValue()).isReachable = false; continue;
           }
           ((MapTileMapObject)entry.getValue()).isReachable = hashSet.contains(entry.getKey());
         }
       }
     }
   }

   public static boolean isAnyTileHovered() {
     return (inboundsMapTileMapObject != null);
   }

   public static boolean hoveredTileIsHighlighted() {
     return (inboundsMapTileMapObject != null && inboundsMapTileMapObject.isHighlighted);
   }

   public static void setHoveredTileHighlightStatus(boolean paramBoolean) {
     if (inboundsMapTileMapObject != null) {
       if (inboundsMapTileMapObject.isHighlighted != paramBoolean) {
         inboundsMapTileMapObject.isHighlighted = paramBoolean;
         inboundsMapTileMapObject.smallTile.setBaseColor(highlightingColor);
         inboundsMapTileMapObject.largeTile.setBaseColor(highlightingColor);
         SoundHelper.playMapScratchSound();
       } else if (paramBoolean && isARepaint()) {
         inboundsMapTileMapObject.smallTile.setBaseColor(highlightingColor);
         inboundsMapTileMapObject.largeTile.setBaseColor(highlightingColor);
         SoundHelper.playMapScratchSound();
       }
     }
   }

   public static boolean isARepaint() {
     if (inboundsMapTileMapObject != null) {
       return (inboundsMapTileMapObject.smallTile.getBaseColor() != highlightingColor);
     }
     return false;
   }

   public static void setHighlightingColor(Color paramColor) {
     highlightingColor = paramColor;
   }

   public static void clearAllHighlights() {
     for (MapTileMapObject mapTileMapObject : tracked.values()) mapTileMapObject.isHighlighted = false;

   }

   public static void removeHighlightsFromUnreachableNodes() {
     MapRoomNode mapRoomNode = inboundsNode;
     if (mapRoomNode != null) {
       HashSet hashSet = reachableMap.get(mapRoomNode);
       Iterator<Map.Entry<MapRoomNode, MapTileMapObject>> iterator = tracked.entrySet().iterator();

       while (true) {
         if (!iterator.hasNext()) {
           return;
         }

         Map.Entry<MapRoomNode, MapTileMapObject> entry = iterator.next();
         if (((MapTileMapObject)entry.getValue()).isHighlighted && (
           hashSet == null || !hashSet.contains(entry.getKey()))) {

           HashSet hashSet1 = reachableMap.get(entry.getKey());
           if (hashSet1 == null || !hashSet1.contains(mapRoomNode))
           {
             ((MapTileMapObject)entry.getValue()).isHighlighted = false; }
         }
       }
     }
   }
   public static void clear() {
     tracked.clear();
   }

   private static boolean hasHighlightType(RoomType paramRoomType) {
     for (MapTileMapObject mapTileMapObject : tracked.values()) {
       if (mapTileMapObject.type == paramRoomType && mapTileMapObject.isHighlighted) {
         return true;
       }
     }
     return false;
   }

   private static void setHighlightByType(RoomType paramRoomType, boolean paramBoolean) {
     for (MapTileMapObject mapTileMapObject : tracked.values()) {
       if (mapTileMapObject.type == paramRoomType) {
         mapTileMapObject.isHighlighted = paramBoolean;
         if (paramBoolean) {
           mapTileMapObject.smallTile.setBaseColor(highlightingColor);
           mapTileMapObject.largeTile.setBaseColor(highlightingColor);
         }
       }
     }
   }

   public static boolean hasHighlightEvent() {
     return hasHighlightType(RoomType.EVENT);
   }
   public static void highlightAllEvents(boolean paramBoolean) {
     setHighlightByType(RoomType.EVENT, paramBoolean);
   }

   public static boolean hasHighlightMerchant() {
     return hasHighlightType(RoomType.SHOP);
   }
   public static void highlightAllMerchant(boolean paramBoolean) {
     setHighlightByType(RoomType.SHOP, paramBoolean);
   }

   public static boolean hasHighlightTreasure() {
     return hasHighlightType(RoomType.TREASURE);
   }
   public static void highlightAllTreasure(boolean paramBoolean) {
     setHighlightByType(RoomType.TREASURE, paramBoolean);
   }

   public static boolean hasHighlightRest() {
     return hasHighlightType(RoomType.REST);
   }
   public static void highlightAllRest(boolean paramBoolean) {
     setHighlightByType(RoomType.REST, paramBoolean);
   }

   public static boolean hasHighlightEnemy() {
     return hasHighlightType(RoomType.MONSTER);
   }
   public static void highlightAllEnemy(boolean paramBoolean) {
     setHighlightByType(RoomType.MONSTER, paramBoolean);
   }

   public static boolean hasHighlightElite() {
     return hasHighlightType(RoomType.ELITE);
   }
   public static void highlightAllElite(boolean paramBoolean) {
     setHighlightByType(RoomType.ELITE, paramBoolean);
   }

   static {
     highlightingColor = ColorEnum.RED.get();
   }

   private static class MapTileMapObject {
     private SmallMapTile smallTile;
     private LargeMapTile largeTile;
     MapTileManager.RoomType type;
     boolean isHighlighted = false;
     boolean isReachable = true;

     public MapTileMapObject(MapRoomNode param1MapRoomNode) {
       this.type = MapTileManager.RoomType.fromSymbol(param1MapRoomNode.getRoomSymbol(Boolean.valueOf(true)));
       this.smallTile = new SmallMapTile();
       this.largeTile = new LargeMapTile();
     }
   }

   private enum RoomType {
     MONSTER, ELITE, EVENT, BOSS, SHOP, TREASURE, REST, UNKNOWN_TYPE;

     static RoomType fromSymbol(String param1String) {
       if (param1String == null) return UNKNOWN_TYPE;
       if (param1String.equals("M")) return MONSTER;
       if (param1String.equals("?")) return EVENT;
       if (param1String.equals("B")) return BOSS;
       if (param1String.equals("E")) return ELITE;
       if (param1String.equals("R")) return REST;
       if (param1String.equals("$")) return SHOP;
       return param1String.equals("T") ? TREASURE : UNKNOWN_TYPE;
     }
   }
}
