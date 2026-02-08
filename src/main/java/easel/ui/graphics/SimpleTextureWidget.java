package easel.ui.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import easel.ui.AbstractWidget;

public class SimpleTextureWidget
   extends AbstractWidget<SimpleTextureWidget>
{
   private float width;
   private float height;
   private final TextureRegion textureRegion;
   private Color renderColor = Color.WHITE;

   public SimpleTextureWidget(float width, float height, Texture texture) {
     this.width = width;
     this.height = height;
     this.textureRegion = new TextureRegion(texture);
   }

   public SimpleTextureWidget(float width, float height, TextureRegion textureRegion) {
     this.width = width;
     this.height = height;
     this.textureRegion = textureRegion;
   }

   public SimpleTextureWidget withColor(Color renderColor) {
     this.renderColor = renderColor;
     return this;
   }

   public SimpleTextureWidget withWidth(float width) {
     this.width = width;
     scaleHitboxToContent();
     return this;
   }

   public SimpleTextureWidget withHeight(float height) {
     this.height = height;
     scaleHitboxToContent();
     return this;
   }

   public SimpleTextureWidget withDimensions(float width, float height) {
     this.width = width;
     this.height = height;
     scaleHitboxToContent();
     return this;
   }

   public float getContentWidth() {
     return this.width; } public float getContentHeight() {
     return this.height;
   }

   protected void renderWidget(SpriteBatch sb) {
     sb.setColor(this.renderColor);
     sb.draw(this.textureRegion,
         Math.round(getContentLeft() * Settings.xScale),
         Math.round(getContentBottom() * Settings.yScale),

         Math.round(getContentWidth() * Settings.scale),
         Math.round(getContentHeight() * Settings.scale));
   }
}
