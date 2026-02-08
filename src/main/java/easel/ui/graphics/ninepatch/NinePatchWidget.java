package easel.ui.graphics.ninepatch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;

public class NinePatchWidget
   extends AbstractWidget<NinePatchWidget>
{
   private NinePatch np;
   private float prefWidth;
   private float prefHeight;
   private Color renderColor = Color.WHITE;

   public NinePatchWidget(float width, float height, TextureRegion texRegion) {
     this(width, height, 32, 32, 32, 32, texRegion);
   }

   public NinePatchWidget(float width, float height, int patchOffset, TextureRegion texRegion) {
     this(width, height, patchOffset, patchOffset, patchOffset, patchOffset, texRegion);
   }

   public NinePatchWidget(float width, float height, int patchOffset, Texture texture) {
     this(width, height, patchOffset, patchOffset, patchOffset, patchOffset, texture);
   }

   public NinePatchWidget(float width, float height, Texture texture) {
     this(width, height, 32, 32, 32, 32, texture);
   }

   public NinePatchWidget(float width, float height, int patchLeft, int patchRight, int patchTop, int patchBottom, TextureRegion texRegion) {
     this.np = new NinePatch(texRegion, patchLeft, patchRight, patchTop, patchBottom);

     this.prefWidth = Math.round(width);
     this.prefHeight = Math.round(height);
   }

   public NinePatchWidget(float width, float height, int patchLeft, int patchRight, int patchTop, int patchBottom, Texture texture) {
     this.np = new NinePatch(texture, patchLeft, patchRight, patchTop, patchBottom);

     this.prefWidth = Math.round(width);
     this.prefHeight = Math.round(height);
   }

   public NinePatchWidget withColor(Color renderColor) {
     this.renderColor = renderColor;
     return this;
   }

   public NinePatchWidget withDimensions(float width, float height) {
     this.prefWidth = Math.round(width);
     this.prefHeight = Math.round(height);

     scaleHitboxToContent();

     return this;
   }

   public NinePatchWidget withWidth(float width) {
     this.prefWidth = Math.round(width);

     scaleHitboxToContent();

     return this;
   }

   public NinePatchWidget withHeight(float height) {
     this.prefHeight = Math.round(height);

     scaleHitboxToContent();

     return this;
   }

   public NinePatchWidget scaleToFullWidget(AbstractWidget widget) {
     return withDimensions(widget.getWidth(), widget.getHeight());
   }

   public float getContentWidth() { return this.prefWidth; } public float getContentHeight() {
     return this.prefHeight;
   }

   public NinePatchWidget anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     return (NinePatchWidget)super.anchoredAt(Math.round(x), Math.round(y), anchorPosition, movementSpeed);
   }

   protected void renderWidget(SpriteBatch sb) {
     sb.setColor(this.renderColor);
     this.np.draw((Batch)sb,
         getContentLeft() * Settings.xScale,
         getContentBottom() * Settings.yScale,
         getContentWidth() * Settings.xScale,
         getContentHeight() * Settings.yScale);
   }
}
