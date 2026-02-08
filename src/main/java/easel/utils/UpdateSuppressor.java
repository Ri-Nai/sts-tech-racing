package easel.utils;

public class UpdateSuppressor
{
   private static boolean suppressingTips = false;
   private static boolean suppressingUpdates = false;

   public static void suppressTips(boolean shouldSuppressTips) {
     suppressingTips = shouldSuppressTips;
   }

   public static void suppressUpdates(boolean shouldSuppressUpdates) {
     suppressingUpdates = shouldSuppressUpdates;
   }

   public static void suppressAll(boolean shouldSuppressAll) {
     suppressTips(shouldSuppressAll);
     suppressUpdates(shouldSuppressAll);
   }

   public static boolean isSuppressingTips() {
     return suppressingTips;
   }

   public static boolean isSuppressingUpdates() {
     return suppressingUpdates;
   }
}
