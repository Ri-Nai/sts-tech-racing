package easel.utils.colors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import easel.ui.InterpolationSpeed;

public class EaselColors
{
   public static Color withOpacity(Color color, float alpha) {
     Color c = color.cpy();
     c.a = alpha;
     return c;
   }

   public static Color withOpacity(Color color, float targetAlpha, InterpolationSpeed withDelay) {
     Color c = color.cpy();

     c.a = withDelay.interpolate(c.a, targetAlpha);

     return c;
   }

   public static Color lighten(Color color, float amt) {
     Color c = color.cpy();

     color.r += amt;
     color.g += amt;
     color.b += amt;

     c.clamp();

     return c;
   }

   public static Color lighten(Color color) {
     return lighten(color, 0.1F);
   }

   public static Color darken(Color color, float amt) {
     return lighten(color, -amt);
   }

   public static Color darken(Color color) {
     return darken(color, 0.1F);
   }

   public static final Color ONE_TENTH_TRANSPARENT_BLACK = new Color(0.0F, 0.0F, 0.0F, 0.1F);
   public static final Color HALF_TRANSPARENT_WHITE = new Color(1.0F, 1.0F, 1.0F, 0.5F);
   public static final Color EIGHTH_TRANSPARENT_WHITE = new Color(1.0F, 1.0F, 1.0F, 0.125F);

   private static ColorPalette activePalette = new EaselDark();

   static void setActivePalette(ColorPalette palette) {
     activePalette = palette;
   }

   public static Color rainbow() {
     float r = (MathUtils.cosDeg((float)(System.currentTimeMillis() / 10L % 360L)) + 1.25F) / 2.3F;
     float g = (MathUtils.cosDeg((float)((System.currentTimeMillis() + 1000L) / 10L % 360L)) + 1.25F) / 2.3F;
     float b = (MathUtils.cosDeg((float)((System.currentTimeMillis() + 2000L) / 10L % 360L)) + 1.25F) / 2.3F;
     return new Color(r, g, b, 1.0F);
   }

   public static Color TOOLTIP_BASE() {
     return activePalette.TOOLTIP_BASE();
   }

   public static Color TOOLTIP_TRIM() {
     return activePalette.TOOLTIP_TRIM();
   } public static Color TOOLTIP_TRIM_HIGHLIGHT() {
     return activePalette.TOOLTIP_TRIM_HIGHLIGHT();
   }

   public static Color QUAL_RED() {
     return activePalette.QUAL_RED();
   }

   public static Color QUAL_GREEN() {
     return activePalette.QUAL_GREEN();
   }

   public static Color QUAL_BLUE() {
     return activePalette.QUAL_BLUE();
   }

   public static Color QUAL_PURPLE() {
     return activePalette.QUAL_PURPLE();
   }

   public static Color QUAL_YELLOW() {
     return activePalette.QUAL_YELLOW();
   }

   public static Color HEADER_STRONG_RED() {
     return activePalette.HEADER_STRONG_RED();
   }

   public static Color HEADER_STRONG_BLUE() {
     return activePalette.HEADER_STRONG_BLUE();
   }

   public static Color HEADER_STRONG_GREEN() {
     return activePalette.HEADER_STRONG_GREEN();
   }

   public static Color HEADER_STRONG_PURPLE() {
     return activePalette.HEADER_STRONG_PURPLE();
   }

   public static Color HEADER_RED() {
     return activePalette.HEADER_RED();
   }

   public static Color HEADER_BLUE() {
     return activePalette.HEADER_BLUE();
   }

   public static Color HEADER_GREEN() {
     return activePalette.HEADER_GREEN();
   }

   public static Color HEADER_PURPLE() {
     return activePalette.HEADER_PURPLE();
   }

   public static Color HEADER_DEEP_BLUE() {
     return activePalette.HEADER_DEEP_BLUE();
   }

   public static Color HEADER_SLATE() {
     return activePalette.HEADER_SLATE();
   }

   public static Color HEADER_SEA_GLASS() {
     return activePalette.HEADER_SEA_GLASS();
   }

   public static Color HEADER_WOOD() {
     return activePalette.HEADER_WOOD();
   }

   public static Color HEADER_LIGHT_ALGAE() {
     return activePalette.HEADER_LIGHT_ALGAE();
   }

   public static Color HEADER_DARK_ALGAE() {
     return activePalette.HEADER_DARK_ALGAE();
   }

   public static Color SEQ_RED_0() {
     return activePalette.SEQ_RED_0();
   }

   public static Color SEQ_RED_1() {
     return activePalette.SEQ_RED_1();
   }

   public static Color SEQ_RED_2() {
     return activePalette.SEQ_RED_2();
   }

   public static Color SEQ_RED_3() {
     return activePalette.SEQ_RED_3();
   }

   public static Color SEQ_RED_4() {
     return activePalette.SEQ_RED_4();
   }

   public static Color SEQ_BLUE_0() {
     return activePalette.SEQ_BLUE_0();
   }

   public static Color SEQ_BLUE_1() {
     return activePalette.SEQ_BLUE_1();
   }

   public static Color SEQ_BLUE_2() {
     return activePalette.SEQ_BLUE_2();
   }

   public static Color SEQ_BLUE_3() {
     return activePalette.SEQ_BLUE_3();
   }

   public static Color SEQ_BLUE_4() {
     return activePalette.SEQ_BLUE_4();
   }

   public static Color SEQ_GREEN_0() {
     return activePalette.SEQ_GREEN_0();
   }

   public static Color SEQ_GREEN_1() {
     return activePalette.SEQ_GREEN_1();
   }

   public static Color SEQ_GREEN_2() {
     return activePalette.SEQ_GREEN_2();
   }

   public static Color SEQ_GREEN_3() {
     return activePalette.SEQ_GREEN_3();
   }

   public static Color SEQ_GREEN_4() {
     return activePalette.SEQ_GREEN_4();
   }

   public static Color SEQ_PURPLE_0() {
     return activePalette.SEQ_PURPLE_0();
   }

   public static Color SEQ_PURPLE_1() {
     return activePalette.SEQ_PURPLE_1();
   }

   public static Color SEQ_PURPLE_2() {
     return activePalette.SEQ_PURPLE_2();
   }

   public static Color SEQ_PURPLE_3() {
     return activePalette.SEQ_PURPLE_3();
   }

   public static Color SEQ_PURPLE_4() {
     return activePalette.SEQ_PURPLE_4();
   }

   public static Color SEQ_YELLOW_0() {
     return activePalette.SEQ_YELLOW_0();
   }

   public static Color SEQ_YELLOW_1() {
     return activePalette.SEQ_YELLOW_1();
   }

   public static Color SEQ_YELLOW_2() {
     return activePalette.SEQ_YELLOW_2();
   }

   public static Color SEQ_YELLOW_3() {
     return activePalette.SEQ_YELLOW_3();
   }

   public static Color SEQ_YELLOW_4() {
     return activePalette.SEQ_YELLOW_4();
   }
}
