package easel.ui.graphics.ninepatch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import java.util.ArrayList;

public class LayeredNinePatch
   extends AbstractWidget<LayeredNinePatch>
{
   private float width;
   private float height;
   private int patchLeft;
   private int patchRight;
   private int patchTop;
   private int patchBottom;
   protected ArrayList<NinePatchWidget> layers = new ArrayList<>();

   public LayeredNinePatch(float width, float height) {
     this(width, height, 32, 32, 32, 32);
   }

   public LayeredNinePatch(float width, float height, int patchLeft, int patchRight, int patchTop, int patchBottom) {
     this.width = width;
     this.height = height;

     this.patchLeft = patchLeft;
     this.patchRight = patchRight;
     this.patchTop = patchTop;
     this.patchBottom = patchBottom;
   }

   public LayeredNinePatch withLayer(TextureRegion textureRegion) {
     this.layers.add(new NinePatchWidget(this.width, this.height, this.patchLeft, this.patchRight, this.patchTop, this.patchBottom, textureRegion));
     return this;
   }

   public LayeredNinePatch withLayer(TextureRegion textureRegion, Color color) {
     this.layers.add((new NinePatchWidget(this.width, this.height, this.patchLeft, this.patchRight, this.patchTop, this.patchBottom, textureRegion)).withColor(color));
     return this;
   }

   public LayeredNinePatch withLayer(Texture texture) {
     this.layers.add(new NinePatchWidget(this.width, this.height, this.patchLeft, this.patchRight, this.patchTop, this.patchBottom, texture));
     return this;
   }

   public LayeredNinePatch withLayer(Texture texture, Color color) {
     this.layers.add((new NinePatchWidget(this.width, this.height, this.patchLeft, this.patchRight, this.patchTop, this.patchBottom, texture)).withColor(color));
     return this;
   }

   public LayeredNinePatch withLayerColor(int index, Color color) {
     if (index >= 0 && index < this.layers.size()) {
       ((NinePatchWidget)this.layers.get(index)).withColor(color);
     }

     return this;
   }

   public LayeredNinePatch scaleToFullWidget(AbstractWidget widget) {
     withDimensions(widget.getWidth(), widget.getHeight());
     return this;
   }

   public LayeredNinePatch withDimensions(float width, float height) {
     this.width = width;
     this.height = height;

     scaleHitboxToContent();

     this.layers.forEach(layer -> layer.withDimensions(width, height));

     return this;
   }

   public LayeredNinePatch withWidth(float width) {
     this.width = width;
     scaleHitboxToContent();
     this.layers.forEach(layer -> layer.withWidth(width));
     return this;
   }

   public LayeredNinePatch withHeight(float height) {
     this.height = height;
     scaleHitboxToContent();
     this.layers.forEach(layer -> layer.withHeight(height));
     return this;
   }

   public LayeredNinePatch anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
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
