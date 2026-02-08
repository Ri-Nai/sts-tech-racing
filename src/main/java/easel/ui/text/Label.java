package easel.ui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import easel.ui.AbstractWidget;

public class Label
   extends AbstractWidget<Label>
{
   private String text;
   private BitmapFont font;
   private Color color;
   private float textWidth;
   private float textHeight;

   public Label(String text) {
     this(text, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
   }

   public Label(String text, Color color) {
     this(text, FontHelper.tipBodyFont, color);
   }

   public Label(String text, BitmapFont font, Color color) {
     this.font = font;
     this.color = color;
     withText(text);
   }

   public Label withText(String text) {
     this.text = text;

     this.textWidth = FontHelper.getWidth(this.font, text, 1.0F);
     this.textHeight = this.font.getLineHeight();

     scaleHitboxToContent();

     return this;
   }

   public Label withColor(Color color) {
     this.color = color;
     return this;
   }

   public String getText() {
     return this.text;
   }

   public float getContentWidth() { return this.textWidth; } public float getContentHeight() {
     return this.textHeight;
   }

   protected void renderWidget(SpriteBatch sb) {
     FontHelper.renderFontLeftDownAligned(sb, this.font, this.text,

         getContentLeft() * Settings.xScale,
         getContentBottom() * Settings.yScale, this.color);
   }
}
