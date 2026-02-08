package MapMarks.ui;

import MapMarks.utils.ColorDatabase;
import MapMarks.utils.ColorEnum;
import MapMarks.utils.MapMarksTextureDatabase;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import easel.ui.graphics.LayeredTextureWidget;

public class RadialMenuObject
   extends AbstractWidget<RadialMenuObject>
{
   private LayeredTextureWidget ltw;
   protected static final float WIDTH = 118.0F;
   protected static final float HEIGHT = 108.0F;
   private ColorEnum color;
   private Color baseColor;
   private Color dimColor;
   private boolean isDimmed = false;

   public RadialMenuObject(Color special) {
     this.color = ColorEnum.WHITE;

     this.baseColor = special;
     this.dimColor = special;

     this

       .ltw = (new LayeredTextureWidget(118.0F, 108.0F)).withLayer(MapMarksTextureDatabase.RADIAL_BASE.getTexture(), this.baseColor).withLayer(MapMarksTextureDatabase.RADIAL_TRIM.getTexture(), ColorDatabase.UI_TRIM);
   }

   public RadialMenuObject(ColorEnum color) {
     this.color = color;

     this.baseColor = color.get();
     this.dimColor = color.getDimmed();

     this

       .ltw = (new LayeredTextureWidget(118.0F, 108.0F)).withLayer(MapMarksTextureDatabase.RADIAL_BASE.getTexture(), this.baseColor).withLayer(MapMarksTextureDatabase.RADIAL_TRIM.getTexture(), ColorDatabase.UI_TRIM);
   }

   public ColorEnum getColor() {
     return this.color;
   }

   public void setDimmed(boolean val) {
     if (this.isDimmed != val) {
       this.isDimmed = val;

       if (this.isDimmed) {
         this.ltw.withLayerColor(0, this.dimColor);
       } else {

         this.ltw.withLayerColor(0, this.baseColor);
       }
     }
   }

   public float getContentWidth() { return 118.0F; } public float getContentHeight() {
     return 108.0F;
   }

   protected void renderWidget(SpriteBatch sb) {
     this.ltw.render(sb);
   }

   public RadialMenuObject anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     super.anchoredAt(x, y, anchorPosition, movementSpeed);
     this.ltw.anchoredAt(x, y, anchorPosition, movementSpeed);
     return this;
   }
}
