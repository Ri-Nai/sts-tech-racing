package MapMarks.utils;

import com.badlogic.gdx.graphics.Color;
import easel.utils.colors.EaselColors;

public enum ColorEnum {
   RED("cc6464"),
   GREEN("81c05a"),
   BLUE("65c4c9"),
   PURPLE("ae78c5"),
   YELLOW("c5a741"),
   WHITE("cbc7cc");

   private Color mainColor;
   private Color dimmedColor;

   ColorEnum(String mainColorHex) {
     this.mainColor = Color.valueOf(mainColorHex);
     this.dimmedColor = EaselColors.darken(this.mainColor, 0.25F);
   }

   public Color get() { return this.mainColor; } public Color getDimmed() {
     return this.dimmedColor;
   }
}
