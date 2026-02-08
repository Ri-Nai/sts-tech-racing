package easel.utils.colors;

import com.badlogic.gdx.graphics.Color;

public class EaselDark
   implements ColorPalette
{
   private static final Color TOOLTIP_BASE = new Color(0.125F, 0.153F, 0.169F, 1.0F);
   private static final Color TOOLTIP_TRIM = new Color(0.318F, 0.341F, 0.365F, 1.0F);
   private static final Color TOOLTIP_TRIM_HIGHLIGHT = new Color(0.435F, 0.518F, 0.537F, 0.4F);

   private static final Color QUAL_RED = new Color(0.839F, 0.4F, 0.443F, 1.0F);
   private static final Color QUAL_GREEN = new Color(0.329F, 0.451F, 0.349F, 1.0F);
   private static final Color QUAL_BLUE = new Color(0.475F, 0.631F, 0.804F, 1.0F);
   private static final Color QUAL_PURPLE = new Color(0.6F, 0.478F, 0.675F, 1.0F);
   private static final Color QUAL_YELLOW = new Color(0.765F, 0.729F, 0.42F, 1.0F);

   private static final Color SEQ_RED_0 = new Color(0.839F, 0.706F, 0.69F, 1.0F);
   private static final Color SEQ_RED_1 = new Color(0.847F, 0.573F, 0.514F, 1.0F);
   private static final Color SEQ_RED_2 = new Color(0.851F, 0.447F, 0.345F, 1.0F);
   private static final Color SEQ_RED_3 = new Color(0.859F, 0.333F, 0.192F, 1.0F);
   private static final Color SEQ_RED_4 = new Color(0.867F, 0.208F, 0.024F, 1.0F);

   private static final Color SEQ_BLUE_0 = new Color(0.69F, 0.839F, 0.765F, 1.0F);
   private static final Color SEQ_BLUE_1 = new Color(0.62F, 0.804F, 0.804F, 1.0F);
   private static final Color SEQ_BLUE_2 = new Color(0.525F, 0.678F, 0.753F, 1.0F);
   private static final Color SEQ_BLUE_3 = new Color(0.424F, 0.529F, 0.698F, 1.0F);
   private static final Color SEQ_BLUE_4 = new Color(0.259F, 0.325F, 0.616F, 1.0F);

   private static final Color SEQ_GREEN_0 = new Color(0.788F, 0.839F, 0.69F, 1.0F);
   private static final Color SEQ_GREEN_1 = new Color(0.616F, 0.725F, 0.51F, 1.0F);
   private static final Color SEQ_GREEN_2 = new Color(0.494F, 0.647F, 0.38F, 1.0F);
   private static final Color SEQ_GREEN_3 = new Color(0.376F, 0.569F, 0.259F, 1.0F);
   private static final Color SEQ_GREEN_4 = new Color(0.184F, 0.494F, 0.137F, 1.0F);

   private static final Color SEQ_PURPLE_0 = new Color(0.761F, 0.698F, 0.776F, 1.0F);
   private static final Color SEQ_PURPLE_1 = new Color(0.678F, 0.573F, 0.737F, 1.0F);
   private static final Color SEQ_PURPLE_2 = new Color(0.663F, 0.463F, 0.82F, 1.0F);
   private static final Color SEQ_PURPLE_3 = new Color(0.443F, 0.298F, 0.624F, 1.0F);
   private static final Color SEQ_PURPLE_4 = new Color(0.306F, 0.192F, 0.502F, 1.0F);

   private static final Color SEQ_YELLOW_0 = new Color(0.694F, 0.678F, 0.565F, 1.0F);
   private static final Color SEQ_YELLOW_1 = new Color(0.761F, 0.678F, 0.427F, 1.0F);
   private static final Color SEQ_YELLOW_2 = new Color(0.8F, 0.635F, 0.322F, 1.0F);
   private static final Color SEQ_YELLOW_3 = new Color(0.882F, 0.6F, 0.094F, 1.0F);
   private static final Color SEQ_YELLOW_4 = new Color(0.91F, 0.549F, 0.145F, 1.0F);

   private static final Color HEADER_RED = new Color(0.231F, 0.137F, 0.176F, 1.0F);
   private static final Color HEADER_BLUE = new Color(0.125F, 0.161F, 0.208F, 1.0F);
   private static final Color HEADER_GREEN = new Color(0.145F, 0.192F, 0.145F, 1.0F);
   private static final Color HEADER_PURPLE = new Color(0.216F, 0.176F, 0.235F, 1.0F);

   private static final Color HEADER_STRONG_RED = new Color(0.333F, 0.157F, 0.157F, 1.0F);
   private static final Color HEADER_STRONG_BLUE = new Color(0.173F, 0.2F, 0.318F, 1.0F);
   private static final Color HEADER_STRONG_GREEN = new Color(0.2F, 0.29F, 0.2F, 1.0F);
   private static final Color HEADER_STRONG_PURPLE = new Color(0.212F, 0.149F, 0.235F, 1.0F);

   private static final Color HEADER_DEEP_BLUE = new Color(0.102F, 0.122F, 0.157F, 1.0F);
   private static final Color HEADER_SLATE = new Color(0.176F, 0.212F, 0.235F, 1.0F);
   private static final Color HEADER_SEA_GLASS = new Color(0.169F, 0.231F, 0.231F, 1.0F);
   private static final Color HEADER_WOOD = new Color(0.235F, 0.176F, 0.176F, 1.0F);
   private static final Color HEADER_LIGHT_ALGAE = new Color(0.286F, 0.294F, 0.188F, 1.0F);
   private static final Color HEADER_DARK_ALGAE = new Color(0.224F, 0.235F, 0.18F, 1.0F);

   public Color TOOLTIP_BASE() {
     return TOOLTIP_BASE; }
   public Color TOOLTIP_TRIM() { return TOOLTIP_TRIM; } public Color TOOLTIP_TRIM_HIGHLIGHT() {
     return TOOLTIP_TRIM_HIGHLIGHT;
   }

   public Color QUAL_RED() {
     return QUAL_RED;
   } public Color QUAL_GREEN() { return QUAL_GREEN; }
   public Color QUAL_BLUE() { return QUAL_BLUE; }
   public Color QUAL_PURPLE() { return QUAL_PURPLE; } public Color QUAL_YELLOW() {
     return QUAL_YELLOW;
   }

   public Color SEQ_RED_0() {
     return SEQ_RED_0;
   } public Color SEQ_RED_1() { return SEQ_RED_1; }
   public Color SEQ_RED_2() { return SEQ_RED_2; }
   public Color SEQ_RED_3() { return SEQ_RED_3; } public Color SEQ_RED_4() {
     return SEQ_RED_4;
   }
   public Color SEQ_BLUE_0() { return SEQ_BLUE_0; }
   public Color SEQ_BLUE_1() { return SEQ_BLUE_1; }
   public Color SEQ_BLUE_2() { return SEQ_BLUE_2; }
   public Color SEQ_BLUE_3() { return SEQ_BLUE_3; } public Color SEQ_BLUE_4() {
     return SEQ_BLUE_4;
   }
   public Color SEQ_GREEN_0() { return SEQ_GREEN_0; }
   public Color SEQ_GREEN_1() { return SEQ_GREEN_1; }
   public Color SEQ_GREEN_2() { return SEQ_GREEN_2; }
   public Color SEQ_GREEN_3() { return SEQ_GREEN_3; } public Color SEQ_GREEN_4() {
     return SEQ_GREEN_4;
   }
   public Color SEQ_PURPLE_0() { return SEQ_PURPLE_0; }
   public Color SEQ_PURPLE_1() { return SEQ_PURPLE_1; }
   public Color SEQ_PURPLE_2() { return SEQ_PURPLE_2; }
   public Color SEQ_PURPLE_3() { return SEQ_PURPLE_3; } public Color SEQ_PURPLE_4() {
     return SEQ_PURPLE_4;
   }
   public Color SEQ_YELLOW_0() { return SEQ_YELLOW_0; }
   public Color SEQ_YELLOW_1() { return SEQ_YELLOW_1; }
   public Color SEQ_YELLOW_2() { return SEQ_YELLOW_2; }
   public Color SEQ_YELLOW_3() { return SEQ_YELLOW_3; } public Color SEQ_YELLOW_4() {
     return SEQ_YELLOW_4;
   }

   public Color HEADER_RED() {
     return HEADER_RED;
   } public Color HEADER_BLUE() { return HEADER_BLUE; }
   public Color HEADER_GREEN() { return HEADER_GREEN; } public Color HEADER_PURPLE() {
     return HEADER_PURPLE;
   }
   public Color HEADER_STRONG_RED() { return HEADER_STRONG_RED; }
   public Color HEADER_STRONG_BLUE() { return HEADER_STRONG_BLUE; }
   public Color HEADER_STRONG_GREEN() { return HEADER_STRONG_GREEN; } public Color HEADER_STRONG_PURPLE() {
     return HEADER_STRONG_PURPLE;
   }
   public Color HEADER_DEEP_BLUE() { return HEADER_DEEP_BLUE; }
   public Color HEADER_SLATE() { return HEADER_SLATE; }
   public Color HEADER_SEA_GLASS() { return HEADER_SEA_GLASS; }
   public Color HEADER_WOOD() { return HEADER_WOOD; }
   public Color HEADER_LIGHT_ALGAE() { return HEADER_LIGHT_ALGAE; } public Color HEADER_DARK_ALGAE() {
     return HEADER_DARK_ALGAE;
   }
}
