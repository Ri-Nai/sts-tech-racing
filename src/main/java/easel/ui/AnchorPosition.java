package easel.ui;

import java.util.Random;

public enum AnchorPosition
{
   LEFT_TOP, CENTER_TOP, RIGHT_TOP,
   LEFT_CENTER, CENTER, RIGHT_CENTER,
   LEFT_BOTTOM, CENTER_BOTTOM, RIGHT_BOTTOM;

   public boolean isLeft() {
     return (this == LEFT_BOTTOM || this == LEFT_CENTER || this == LEFT_TOP);
   }

   public boolean isRight() {
     return (this == RIGHT_BOTTOM || this == RIGHT_CENTER || this == RIGHT_TOP);
   }

   public boolean isBottom() {
     return (this == LEFT_BOTTOM || this == CENTER_BOTTOM || this == RIGHT_BOTTOM);
   }

   public boolean isTop() {
     return (this == LEFT_TOP || this == CENTER_TOP || this == RIGHT_TOP);
   }

   public boolean isCenterX() {
     return (this == CENTER_TOP || this == CENTER || this == CENTER_BOTTOM);
   }

   public boolean isCenterY() {
     return (this == LEFT_CENTER || this == CENTER || this == RIGHT_CENTER);
   }

   public float getXFromLeft(float left, float width) {
     if (isLeft())
       return left;
     if (isCenterX()) {
       return left + 0.5F * width;
     }
     return left + width;
   }

   public float getXFromRight(float right, float width) {
     return getXFromLeft(right - width, width);
   }

   public float getYFromBottom(float bottom, float height) {
     if (isBottom())
       return bottom;
     if (isCenterY()) {
       return bottom + 0.5F * height;
     }
     return bottom + height;
   }

   public float getYFromTop(float top, float height) {
     return getYFromBottom(top - height, height);
   }

   public float getLeft(float x, float width) {
     return isLeft() ? x : (isCenterX() ? (x - 0.5F * width) : (x - width));
   }

   public float getBottom(float y, float height) {
     return isBottom() ? y : (isCenterY() ? (y - 0.5F * height) : (y - height));
   }

   public static float deltaX(float startX, AnchorPosition startAnchor, float newX, AnchorPosition newAnchor, float width) {
     float oldLeft = startAnchor.getLeft(startX, width);
     float newLeft = newAnchor.getLeft(newX, width);
     return newLeft - oldLeft;
   }

   public static float deltaY(float startY, AnchorPosition startAnchor, float newY, AnchorPosition newAnchor, float height) {
     float oldBottom = startAnchor.getBottom(startY, height);
     float newBottom = newAnchor.getBottom(newY, height);
     return newBottom - oldBottom;
   }

   public static AnchorPosition combine(AnchorPosition horizontal, AnchorPosition vertical) {
     if (horizontal.isLeft()) {
       if (vertical.isBottom())
         return LEFT_BOTTOM;
       if (vertical.isCenterY())
         return LEFT_CENTER;
       return LEFT_TOP;
     }
     if (horizontal.isCenterX()) {
       if (vertical.isBottom())
         return CENTER_BOTTOM;
       if (vertical.isCenterY())
         return CENTER;
       return CENTER_TOP;
     }

     if (vertical.isBottom())
       return RIGHT_BOTTOM;
     if (vertical.isCenterY())
       return RIGHT_CENTER;
     return RIGHT_TOP;
   }

   public static AnchorPosition randomAnchor() {
     Random random = new Random();
     int index = random.nextInt((values()).length);
     return values()[index];
   }

   public static AnchorPosition randomAnchor(AnchorPosition previous) {
     Random random = new Random();

     while (true) {
       int index = random.nextInt((values()).length);
       AnchorPosition next = values()[index];

       if (next != previous) {
         return next;
       }
     }
   }

   public AnchorPosition next() {
     return values()[(ordinal() + 1) % (values()).length];
   }
}
