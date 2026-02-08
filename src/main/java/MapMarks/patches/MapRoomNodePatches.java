package MapMarks.patches;

import MapMarks.MapMarks;
import MapMarks.MapTileManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class MapRoomNodePatches
{
   @SpirePatch(clz = AbstractDungeon.class, method = "generateMap")
   public static class PostGenerateDungeonPatch
   {
     @SpirePostfixPatch
     public static void Postfix() {
       MapTileManager.clear();
       MapMarks.paintContainer.clear();

       AbstractDungeon.map.forEach(list -> list.forEach(MapTileManager::track));

       MapTileManager.initializeReachableMap();
       MapTileManager.computeReachable();
     }
   }

   @SpirePatch(clz = AbstractDungeon.class, method = "populatePathTaken")
   public static class PostPopulatePathTakenPatch
   {
     @SpirePostfixPatch
     public static void Postfix() {
       MapTileManager.computeReachable();
     }
   }

   @SpirePatch(clz = TheEnding.class, method = "generateSpecialMap")
   public static class PostEndingGenerateDungeonPatch
   {
     public static void Postfix() {
       MapTileManager.clear();
       MapMarks.paintContainer.clear();

       AbstractDungeon.map.forEach(list -> list.forEach(MapTileManager::track));
     }
   }

   @SpirePatch(clz = MapRoomNode.class, method = "render")
   public static class MapRoomNodeRenderRecolorPatch
   {
     public static void recolorOutline(SpriteBatch sb, MapRoomNode node) {
       if (MapTileManager.isNodeHighlighted(node) && (
         MapMarks.legendObject.isMouseInBounds() || MapTileManager.isNodeReachable(node))) {
         Color color = MapTileManager.getHighlightedNodeColor(node);
         sb.setColor(color);
       }
     }

     public static void recolorBase(SpriteBatch sb, MapRoomNode node) {
       if (MapTileManager.isNodeHighlighted(node) && (
         MapMarks.legendObject.isMouseInBounds() || MapTileManager.isNodeReachable(node))) {
         sb.setColor(Color.BLACK);
       }
     }

     public static String REPLACEMENT = "{ if ($1 == this.room.getMapImgOutline()) {" + MapRoomNodeRenderRecolorPatch.class

       .getName() + ".recolorOutline(sb, this);}else if ($1 == this.room.getMapImg()) {" + MapRoomNodeRenderRecolorPatch.class

       .getName() + ".recolorBase(sb, this);}$_ = $proceed($$); }";

     public static ExprEditor Instrument() {
       return new ExprEditor()
         {
           public void edit(MethodCall m) throws CannotCompileException {
             if (m.getClassName().equals(SpriteBatch.class.getName()) && m.getMethodName().equals("draw")) {
               m.replace(MapRoomNodePatches.MapRoomNodeRenderRecolorPatch.REPLACEMENT);
             }
           }
         };
     }
   }

   @SpirePatch(clz = MapRoomNode.class, method = "render")
   public static class MapRoomNodeRenderPatch
   {
     private static final int IMG_WIDTH = (int)(Settings.xScale * 64.0F);
     private static final float SPACING_X = Settings.isMobile ? (IMG_WIDTH * 2.2F) : (IMG_WIDTH * 2.0F);
     private static final float OFFSET_X = Settings.isMobile ? (496.0F * Settings.xScale) : (560.0F * Settings.xScale);
     private static final float OFFSET_Y = 180.0F * Settings.scale;

     public static float computeXFromNode(MapRoomNode node) {
       return (node.x * SPACING_X + OFFSET_X + node.offsetX) / Settings.xScale - 64.0F;
     }

     public static float computeYFromNode(MapRoomNode node) {
       return (node.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY + node.offsetY) / Settings.yScale - 64.0F;
     }

     public static void handlePreEmeraldRender(SpriteBatch sb, MapRoomNode node) {
       MapTileManager.tryRender(sb, node,

           computeXFromNode(node) + 32.0F,
           computeYFromNode(node) + 32.0F);
     }

     public static String PRE_EMERALD_VFX = "{ " + MapRoomNodeRenderPatch.class

       .getName() + ".handlePreEmeraldRender(sb, this);$_ = $proceed($$); }";

     public static ExprEditor Instrument() {
       return new ExprEditor()
         {
           public void edit(MethodCall m) throws CannotCompileException {
             if (m.getClassName().equals(MapRoomNode.class.getName()) && m.getMethodName().equals("renderEmeraldVfx")) {
               m.replace(MapRoomNodePatches.MapRoomNodeRenderPatch.PRE_EMERALD_VFX);
             }
           }
         };
     }
   }

   @SpirePatch(clz = AbstractDungeon.class, method = "setCurrMapNode")
   public static class MapRoomNodeReachabilityPatch
   {
     @SpirePostfixPatch
     public static void Postfix(MapRoomNode _currMapNode) {
       MapTileManager.computeReachable();
     }
   }
}
