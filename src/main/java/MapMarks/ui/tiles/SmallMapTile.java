package MapMarks.ui.tiles;

import MapMarks.utils.ColorDatabase;
import MapMarks.utils.ColorEnum;
import MapMarks.utils.MapMarksTextureDatabase;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import easel.ui.graphics.LayeredTextureWidget;

public class SmallMapTile
   extends AbstractWidget<SmallMapTile>
{
   public final float WIDTH;
   public final float HEIGHT;
   private LayeredTextureWidget ltw;
   private Color baseColor = ColorEnum.RED.get();
   private static final Color trimColor = ColorDatabase.UI_TRIM;

   public SmallMapTile() {
     Texture base = MapMarksTextureDatabase.SMALL_TILE_BASE.getTexture();

     this.WIDTH = base.getWidth();
     this.HEIGHT = base.getHeight();

     this

       .ltw = (new LayeredTextureWidget(this.WIDTH, this.HEIGHT)).withLayer(MapMarksTextureDatabase.SMALL_TILE_SHADOW.getTexture()).withLayer(MapMarksTextureDatabase.SMALL_TILE_BASE.getTexture(), this.baseColor).withLayer(MapMarksTextureDatabase.SMALL_TILE_TRIM.getTexture(), trimColor);
   }

   public void setBaseColor(Color baseColor) {
     if (this.baseColor != baseColor) {
       this.baseColor = baseColor;
       this.ltw.withLayerColor(1, baseColor);
     }
   }

   public Color getBaseColor() {
     return this.baseColor;
   }

   public float getContentWidth() { return this.WIDTH; } public float getContentHeight() {
     return this.HEIGHT;
   }

   public SmallMapTile anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     super.anchoredAt(x, y, anchorPosition, movementSpeed);
     this.ltw.anchoredAt(x, y, anchorPosition, movementSpeed);
     return this;
   }

   protected void renderWidget(SpriteBatch sb) {
     this.ltw.render(sb);
   }
}
