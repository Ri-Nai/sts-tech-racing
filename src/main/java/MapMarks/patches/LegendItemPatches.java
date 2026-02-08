package MapMarks.patches;

import MapMarks.MapTileManager;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.LegendItem;
import java.util.HashMap;

public class LegendItemPatches
{
   public static HashMap<LegendItem, LegendItemHandler> handler = new HashMap<>();

   private static class LegendItemHandler
   {
     Runnable rightClickCallback;
     boolean isRightClickStarted = false;
     boolean isRightClickBlocked = false;

     public LegendItemHandler(Runnable callback) {
       this.rightClickCallback = callback;
     }
   }

   @SpirePatch(clz = LegendItem.class, method = "<ctor>")
   public static class LegendItemConstructorPatch
   {
     @SpirePostfixPatch
     public static void Postfix(LegendItem item, String _label, Texture _img, String _tipHeader, String _tipBody, int index) {
       if (index == 0) {
         LegendItemPatches.handler.put(item, new LegendItemPatches.LegendItemHandler(() -> { if (MapTileManager.hasHighlightEvent()) {
                   MapTileManager.highlightAllEvents(false);
                 } else {
                   MapTileManager.highlightAllEvents(true);
                 }
               }));
       } else if (index == 1) {
         LegendItemPatches.handler.put(item, new LegendItemPatches.LegendItemHandler(() -> { if (MapTileManager.hasHighlightMerchant()) {
                   MapTileManager.highlightAllMerchant(false);
                 } else {
                   MapTileManager.highlightAllMerchant(true);
                 }
               }));
       } else if (index == 2) {
         LegendItemPatches.handler.put(item, new LegendItemPatches.LegendItemHandler(() -> { if (MapTileManager.hasHighlightTreasure()) {
                   MapTileManager.highlightAllTreasure(false);
                 } else {
                   MapTileManager.highlightAllTreasure(true);
                 }
               }));
       } else if (index == 3) {
         LegendItemPatches.handler.put(item, new LegendItemPatches.LegendItemHandler(() -> { if (MapTileManager.hasHighlightRest()) {
                   MapTileManager.highlightAllRest(false);
                 } else {
                   MapTileManager.highlightAllRest(true);
                 }
               }));
       } else if (index == 4) {
         LegendItemPatches.handler.put(item, new LegendItemPatches.LegendItemHandler(() -> { if (MapTileManager.hasHighlightEnemy()) {
                   MapTileManager.highlightAllEnemy(false);
                 } else {
                   MapTileManager.highlightAllEnemy(true);
                 }
               }));
       } else if (index == 5) {
         LegendItemPatches.handler.put(item, new LegendItemPatches.LegendItemHandler(() -> {
                 if (MapTileManager.hasHighlightElite()) {
                   MapTileManager.highlightAllElite(false);
                 } else {
                   MapTileManager.highlightAllElite(true);
                 }
               }));
       }
     }
   }

   @SpirePatch(clz = LegendItem.class, method = "update")
   public static class LegendItemUpdatePatch
   {
     public static void Postfix(LegendItem item) {
       if (CardCrawlGame.isInARun() && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP) {
         LegendItemPatches.LegendItemHandler h = LegendItemPatches.handler.get(item);

         if (h == null) {
           return;
         }
         if (item.hb.hovered) {
           if (InputHelper.isMouseDown_R && !h.isRightClickStarted) {
             h.isRightClickStarted = true;
           } else if (!InputHelper.isMouseDown_R && h.isRightClickStarted) {
             h.isRightClickStarted = false;

             if (!h.isRightClickBlocked) {
               h.rightClickCallback.run();
             }
             else {

               h.isRightClickBlocked = false;
             }

           }
         } else {

           h.isRightClickStarted = false;

           if (InputHelper.isMouseDown_R) {
             h.isRightClickBlocked = true;
           } else {
             h.isRightClickBlocked = false;
           }
         }
       }
     }
   }
}
