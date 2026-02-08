package easel.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import easel.Easel;
import easel.utils.colors.EaselColors;

public class EaselFonts
{
   public static BitmapFont SMALLER_TIP_BODY;
   public static BitmapFont MEDIUM_ITALIC;

   public static void loadFonts() {
     SMALLER_TIP_BODY = (new BitmapFontBuilder()).withSize(18.0F).build();

     MEDIUM_ITALIC = (new BitmapFontBuilder()).withSize(18.0F).withItalic().withShadow(EaselColors.ONE_TENTH_TRANSPARENT_BLACK).build();

     Easel.logger.info("Initialized 2 extra font(s).");
   }
}
