package MapMarks.ui;

import MapMarks.MapMarks;
import MapMarks.utils.ColorEnum;
import MapMarks.utils.SoundHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import easel.utils.EaselInputHelper;
import java.util.ArrayList;
import java.util.Arrays;

public class RadialMenu
   extends AbstractWidget<RadialMenu>
{
   private static final float WIDTH = 500.0F;
   private static final float HEIGHT = 500.0F;
   private static final float THRESHOLD = 59.0F;
   private static final float PI = 3.1415927F;
   private final float thetaStart;
   private final float thetaDelta;
   private ArrayList<RadialMenuObject> objects;
   private RadialMenuObject centerObject;
   private static Color centerDefaultColor = Color.valueOf("8d8c80");

   int selectedIndex = -1;

   private boolean isOpen = false;

   private int startX;
   private int startY;
   private ColorEnum initialColorWhenOpened = ColorEnum.RED;

   public RadialMenu() {
     this

       .objects = new ArrayList<>(Arrays.asList(new RadialMenuObject[] { new RadialMenuObject(ColorEnum.WHITE), new RadialMenuObject(ColorEnum.RED), new RadialMenuObject(ColorEnum.GREEN), new RadialMenuObject(ColorEnum.BLUE), new RadialMenuObject(ColorEnum.PURPLE), new RadialMenuObject(ColorEnum.YELLOW) }));

     this.thetaDelta = 6.2831855F / this.objects.size();
     this.thetaStart = 1.5707964F - this.thetaDelta;

     this.centerObject = new RadialMenuObject(centerDefaultColor);
   }

   public void open() {
     this.isOpen = true;

     if (this.selectedIndex != -1) {
       this.initialColorWhenOpened = ((RadialMenuObject)this.objects.get(this.selectedIndex)).getColor();
     }

     this.selectedIndex = -1;

     this.startX = EaselInputHelper.getMouseX();
     this.startY = EaselInputHelper.getMouseY();

     anchoredCenteredOnMouse();
   }

   public void close() {
     this.isOpen = false;
   }

   public boolean isMenuOpen() {
     return this.isOpen;
   }

   public int getSelectedIndex() {
     return this.selectedIndex;
   }

   public ColorEnum getSelectedColorOrDefault() {
     if (this.selectedIndex != -1) {
       return ((RadialMenuObject)this.objects.get(this.selectedIndex)).getColor();
     }

     return this.initialColorWhenOpened;
   }

   public float getContentWidth() {
     return 500.0F;
   }

   public float getContentHeight() {
     return 500.0F;
   }

   public RadialMenu anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     super.anchoredAt(x, y, anchorPosition, movementSpeed);

     float cx = getContentCenterX();
     float cy = getContentCenterY();

     float distance = 70.0F;

     float theta = this.thetaStart;

     for (RadialMenuObject obj : this.objects) {
       float dx = (float)(70.0D * Math.cos(theta));
       float dy = (float)(70.0D * Math.sin(theta));

       obj.anchoredAt(cx + dx, cy + dy, AnchorPosition.CENTER, movementSpeed);

       theta += this.thetaDelta;
     }

     this.centerObject.anchoredAt(cx, cy, AnchorPosition.CENTER, movementSpeed);

     return this;
   }

   private int computeClosestObjectIndex(float theta) {
     if (theta < 0.0F) {
       theta += 6.2831855F;
     }
     float targetTheta = this.thetaStart;

     int target = -1;
     float thetaDifference = 6.2831855F;

     for (int i = 0; i < this.objects.size(); i++) {
       float potentialThetaDifference = Math.abs(targetTheta - theta);

       if (potentialThetaDifference < thetaDifference) {
         target = i;
         thetaDifference = potentialThetaDifference;
       }

       targetTheta += this.thetaDelta;
     }

     return target;
   }

   protected void updateWidget() {
     super.updateWidget();

     if (this.isOpen) {

       int currX = EaselInputHelper.getMouseX();
       int currY = EaselInputHelper.getMouseY();

       int dx = currX - this.startX;
       int dy = currY - this.startY;

       float distanceFromStart = (float)Math.sqrt((dx * dx + dy * dy));

       if (distanceFromStart > 59.0F) {
         int nextSelectedIndex = computeClosestObjectIndex((float)Math.atan2(dy, dx));

         if (nextSelectedIndex != this.selectedIndex) {
           this.selectedIndex = nextSelectedIndex;

           if (this.selectedIndex != -1) {
             SoundHelper.playRadialChangeSound(this.selectedIndex, this.objects.size());

             RadialMenuObject selected = this.objects.get(this.selectedIndex);
             selected.setDimmed(false);
             MapMarks.legendObject.setColor(selected.getColor());

             for (int i = 0; i < this.objects.size(); i++) {
               if (i != this.selectedIndex) {
                 ((RadialMenuObject)this.objects.get(i)).setDimmed(true);
               }
             }
           }
         }
       } else {

         this.objects.forEach(object -> object.setDimmed(false));
         this.selectedIndex = -1;

         MapMarks.legendObject.setColor(this.initialColorWhenOpened);
       }
     }
   }

   protected void renderWidget(SpriteBatch sb) {
     if (this.isOpen) {
       for (int i = 0; i < this.objects.size(); i++) {
         if (i != this.selectedIndex) {
           ((RadialMenuObject)this.objects.get(i)).render(sb);
         }
       }

       this.centerObject.render(sb);

       if (this.selectedIndex != -1)
         ((RadialMenuObject)this.objects.get(this.selectedIndex)).render(sb);
     }
   }
}
