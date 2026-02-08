package easel.utils;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class EaselInputHelper
{
   public static boolean isShiftPressed() {
     return (Gdx.input.isKeyPressed(59) || Gdx.input.isKeyPressed(60));
   }

   public static boolean isAltPressed() {
     return (Gdx.input.isKeyPressed(57) || Gdx.input.isKeyPressed(58));
   }

   public static boolean isControlPressed() {
     return (Gdx.input.isKeyPressed(129) || Gdx.input.isKeyPressed(130));
   }

   public static int getMouseX() {
     return (int)(InputHelper.mX / Settings.xScale);
   }

   public static int getMouseY() {
     return (int)(InputHelper.mY / Settings.yScale);
   }
}
