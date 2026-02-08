package easel.utils;

public class EaselMathHelper
{
   public static int roundToMultipleOf(int numberToRound, int multiple) {
     return Math.round(roundToMultipleOf(numberToRound, multiple));
   }

   public static float roundToMultipleOf(float numberToRound, float multiple) {
     return Math.round(numberToRound / multiple) * multiple;
   }
}
