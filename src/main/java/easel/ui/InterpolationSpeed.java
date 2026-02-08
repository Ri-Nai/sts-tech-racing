package easel.ui;

import com.megacrit.cardcrawl.helpers.MathHelper;

public enum InterpolationSpeed {
   INSTANT,
   FAST,
   MEDIUM,
   SLOW;

   public float interpolate(float start, float target) {
     switch (this) {
       case INSTANT:
         return target;
       case FAST:
         return MathHelper.mouseLerpSnap(start, target);
       case MEDIUM:
         return MathHelper.fadeLerpSnap(start, target);
     }
     return MathHelper.cardLerpSnap(start, target);
   }
}
