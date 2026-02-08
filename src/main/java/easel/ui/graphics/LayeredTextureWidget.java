package easel.ui.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import java.util.ArrayList;

public class LayeredTextureWidget
   extends AbstractWidget<LayeredTextureWidget>
{
   private float width;
   private float height;
   private ArrayList<SimpleTextureWidget> layers = new ArrayList<>();

   public LayeredTextureWidget(float width, float height) {
     this.width = width;
     this.height = height;
   }

   public LayeredTextureWidget withLayer(TextureRegion textureRegion) {
     this.layers.add(new SimpleTextureWidget(this.width, this.height, textureRegion));
     return this;
   }

   public LayeredTextureWidget withLayer(TextureRegion textureRegion, Color renderColor) {
     this.layers.add((new SimpleTextureWidget(this.width, this.height, textureRegion)).withColor(renderColor));
     return this;
   }

   public LayeredTextureWidget withLayer(Texture texture) {
     this.layers.add(new SimpleTextureWidget(this.width, this.height, texture));
     return this;
   }

   public LayeredTextureWidget withLayer(Texture texture, Color renderColor) {
     this.layers.add((new SimpleTextureWidget(this.width, this.height, texture)).withColor(renderColor));
     return this;
   }

   public LayeredTextureWidget withLayerColor(int index, Color renderColor) {
     if (index >= 0 && index < this.layers.size()) {
       ((SimpleTextureWidget)this.layers.get(index)).withColor(renderColor);
     }

     return this;
   }

   public LayeredTextureWidget withWidth(float newWidth) {
     this.width = newWidth;

     scaleHitboxToContent();

     for (SimpleTextureWidget layer : this.layers) {
       layer.withWidth(newWidth);
     }

     return this;
   }

   public LayeredTextureWidget withHeight(float newHeight) {
     this.height = newHeight;

     scaleHitboxToContent();

     for (SimpleTextureWidget layer : this.layers) {
       layer.withHeight(newHeight);
     }

     return this;
   }

   public LayeredTextureWidget withDimensions(float newWidth, float newHeight) {
     withWidth(newWidth);
     return withHeight(newHeight);
   }

   public LayeredTextureWidget anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     super.anchoredAt(x, y, anchorPosition, movementSpeed);
     this.layers.forEach(layer -> layer.anchoredAt(x, y, anchorPosition, movementSpeed));
     return this;
   }

   public float getContentWidth() {
     return this.width; } public float getContentHeight() {
     return this.height;
   }

   protected void renderWidget(SpriteBatch sb) {
     this.layers.forEach(layer -> layer.render(sb));
   }
}
