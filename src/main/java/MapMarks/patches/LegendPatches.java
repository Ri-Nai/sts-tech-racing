package MapMarks.patches;

import MapMarks.MapMarks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.Legend;

public class LegendPatches
{
   @SpirePatch(clz = Legend.class, method = "render")
   public static class LegendObjectPatch
   {
     @SpirePostfixPatch
     public static void Postfix(Legend legend, SpriteBatch sb) {
       if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP) {

         MapMarks.legendObject.setAlphaFromLegend(legend.c.a);
         MapMarks.legendObject.render(sb);
       }
     }
   }

   @SpirePatch(clz = Legend.class, method = "update")
   public static class LegendUpdatePatch
   {
     @SpirePostfixPatch
     public static void Postfix(Legend _legend, float mapAlpha, boolean isMapScreen) {
       if (mapAlpha >= 0.8F && isMapScreen) {
         MapMarks.legendObject.update();
       }
     }
   }

   @SpirePatch(clz = Legend.class, method = "render")
   public static class LegendRenderPaintPatch
   {
     @SpirePostfixPatch
     public static void Postfix(Legend _legend, SpriteBatch sb) {
       MapMarks.paintContainer.render(sb);
     }
   }
}
