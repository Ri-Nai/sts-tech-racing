package easel.ui.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import easel.Easel;
import easel.ui.AbstractWidget;

public abstract class ShaderWidget<T extends ShaderWidget<T>>
   extends AbstractWidget<T>
{
   protected Texture tex = ImageMaster.WHITE_SQUARE_IMG;

   protected ShaderProgram shaderProgram;
   protected float width;
   protected float height;

   public ShaderWidget(float width, float height, String vertexShaderPath, String fragmentShaderPath) {
     this.width = width;
     this.height = height;

     this

       .shaderProgram = new ShaderProgram(Gdx.files.internal(vertexShaderPath).readString(), Gdx.files.internal(fragmentShaderPath).readString());

     if (this.shaderProgram.isCompiled()) {
       Easel.logger.info("Shaders compiled successfully.");
     } else {

       Easel.logger.error("ERROR: shaders failed to compile");
       Easel.logger.error(this.shaderProgram.getLog());
     }
   }

   public float getContentWidth() { return this.width; } public float getContentHeight() {
     return this.height;
   }

   public T withWidth(float newWidth) {
     this.width = newWidth;
     scaleHitboxToContent();
     return (T)this;
   }

   public T withHeight(float newHeight) {
     this.height = newHeight;
     scaleHitboxToContent();
     return (T)this;
   }

   public T withDimensions(float newWidth, float newHeight) {
     this.width = newWidth;
     this.height = newHeight;
     return (T)this;
   }

   protected void setUniforms() {}

   protected void renderTexture(SpriteBatch sb) {
     sb.setColor(Color.WHITE);
     sb.draw(this.tex,
         getContentLeft() * Settings.xScale,
         getContentBottom() * Settings.yScale,
         getContentWidth() * Settings.xScale,
         getContentHeight() * Settings.yScale);
   }

   protected void renderWidget(SpriteBatch sb) {
     sb.end();

     ShaderProgram oldShader = sb.getShader();
     sb.setShader(this.shaderProgram);

     sb.begin();

     setUniforms();
     renderTexture(sb);

     sb.end();

     sb.setShader(oldShader);
     sb.begin();
   }
}
