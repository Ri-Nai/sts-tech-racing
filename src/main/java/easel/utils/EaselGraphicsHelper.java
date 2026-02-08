package easel.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import easel.ui.AbstractWidget;
import easel.ui.debug.DebugWidget;

public class EaselGraphicsHelper
{
   public static void drawRect(SpriteBatch sb, float left, float bottom, float width, float height, Color color) {
     sb.setColor(color);
     sb.draw(ImageMaster.WHITE_SQUARE_IMG, left * Settings.xScale, bottom * Settings.yScale, width * Settings.scale, height * Settings.scale);
   }

   public static void drawRect(SpriteBatch sb, AbstractWidget widget, boolean useFull, Color color) {
     if (useFull) {
       drawRect(sb, widget
           .getLeft(), widget
           .getBottom(), widget
           .getWidth(), widget
           .getHeight(), color);
     }
     else {

       drawRect(sb, widget
           .getContentLeft(), widget
           .getContentBottom(), widget
           .getContentWidth(), widget
           .getContentHeight(), color);
     }
   }

   public static void drawDebugRects(SpriteBatch sb, AbstractWidget widget) {
     drawRect(sb, widget, true, DebugWidget.DEBUG_COLOR_0);
     drawRect(sb, widget, false, DebugWidget.DEBUG_COLOR_1);
   }

   private static final Color DEBUG_DIM_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.6F);

   public static void dimFullScreen(SpriteBatch sb, boolean fullyDark) {
     Color color = fullyDark ? Color.BLACK : DEBUG_DIM_COLOR;
     drawRect(sb, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT, color);
   }
}
