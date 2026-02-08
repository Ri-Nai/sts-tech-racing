package easel.ui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import easel.ui.AbstractWidget;
import java.util.ArrayList;
import java.util.function.Supplier;

public class SmartLabel
   extends AbstractWidget<SmartLabel>
{
   private float textHeight;
   private float textWidth;
   private float lineWidth;
   private float lineSpacing;
   private BitmapFont font;
   private Supplier<Color> colorSupplier = () -> Settings.CREAM_COLOR;

   private static final class TextGroup
   {
     String text;
     int line;
     Supplier<Color> colorSupplier;
     float width;

     public TextGroup(String text, int line, Supplier<Color> colorSupplier, float width) {
       this.text = text;
       this.line = line;
       this.colorSupplier = colorSupplier;
       this.width = width;
     }
   }

   private ArrayList<TextGroup> groups = new ArrayList<>();

   private float spaceWidth;

   private float lastLeft;

   private int lastLine;
   private float fontLineHeight;
   private boolean startOfLine = true;
   private StringBuilder stringBuilder = new StringBuilder();

   public SmartLabel() {
     this(FontHelper.tipBodyFont);
   }

   public SmartLabel(float lineWidth) {
     this(FontHelper.tipBodyFont, lineWidth);
   }

   public SmartLabel(BitmapFont font) {
     this(font, 1000000.0F, 10.0F);
   }

   public SmartLabel(BitmapFont font, float lineWidth) {
     this(font, lineWidth, 10.0F);
   }

   public SmartLabel(BitmapFont font, float lineWidth, float lineSpacing) {
     this.font = font;

     this.spaceWidth = FontHelper.getWidth(font, " ", 1.0F) / Settings.scale;
     this.fontLineHeight = FontHelper.getHeight(font) / Settings.scale;

     this.lineWidth = lineWidth;
     this.lineSpacing = lineSpacing;
   }

   public SmartLabel withText(String text) {
     appendTextByWords(text);
     return this;
   }

   public SmartLabel withText(Color textColor, String text) {
     Supplier<Color> previousSupplier = this.colorSupplier;
     this.colorSupplier = (() -> textColor);

     appendTextByWords(text);

     this.colorSupplier = previousSupplier;
     return this;
   }

   public SmartLabel withText(Supplier<Color> colorSupplier, String text) {
     Supplier<Color> previousSupplier = this.colorSupplier;
     this.colorSupplier = colorSupplier;

     appendTextByWords(text);

     this.colorSupplier = previousSupplier;
     return this;
   }

   public SmartLabel withTextColor(Color color) {
     this.colorSupplier = (() -> color);
     return this;
   }

   public SmartLabel withTextColor(Supplier<Color> colorSupplier) {
     this.colorSupplier = colorSupplier;
     return this;
   }

   public SmartLabel withNewlines(int count) {
     this.lastLine += count;
     this.lastLeft = 0.0F;

     this.startOfLine = true;

     return this;
   }

   private void finalizeTextGroup() {
     String contents = this.stringBuilder.toString();

     if (!contents.isEmpty()) {
       float width = FontHelper.getWidth(this.font, contents, 1.0F) / Settings.scale;

       this.groups.add(new TextGroup(contents, this.lastLine, this.colorSupplier, width));

       if (this.lastLeft > this.textWidth) {
         this.textWidth = this.lastLeft;
       }

       this.stringBuilder = new StringBuilder();
     }
   }

   private void appendTextByWords(String text) {
     for (String word : text.split(" ")) {
       if (!word.isEmpty()) {

         float wordWidth = FontHelper.getWidth(this.font, word, 1.0F);

         if (this.lastLeft + this.spaceWidth + wordWidth > this.lineWidth) {
           finalizeTextGroup();

           this.stringBuilder.append(word);
           this.lastLeft = wordWidth;
           this.lastLine++;
         }
         else {

           if (this.startOfLine) {
             this.startOfLine = false;
           } else {
             this.stringBuilder.append(" ");
           }
           this.stringBuilder.append(word);

           this.lastLeft += wordWidth + this.spaceWidth;
         }
       }
     }
     finalizeTextGroup();
     recomputeTextHeight();
   }

   private void recomputeTextHeight() {
     if (this.groups.isEmpty()) {
       this.textHeight = 0.0F;
     } else {
       int numSpacing = ((TextGroup)this.groups.get(this.groups.size() - 1)).line;
       this.textHeight = numSpacing * this.lineSpacing + (numSpacing + 1) * this.fontLineHeight;
     }
   }

   public float getContentWidth() {
     return this.textWidth; } public float getContentHeight() {
     return this.textHeight;
   }

   protected void renderWidget(SpriteBatch sb) {
     int currLine = 0;

     float contentLeft = getContentLeft();

     float left = contentLeft;
     float top = getContentTop();

     for (TextGroup group : this.groups) {
       String text = group.text;
       Color color = group.colorSupplier.get();

       while (currLine < group.line) {
         top -= this.fontLineHeight + this.lineSpacing;
         currLine++;

         left = contentLeft;
       }

       FontHelper.renderFontLeftTopAligned(sb, this.font, text, left * Settings.xScale, top * Settings.yScale, color);

       left += group.width;
     }
   }
}
