package easel.ui.layouts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import java.util.stream.Stream;

public class HorizontalLayout
   extends AbstractOneDimensionalLayout<HorizontalLayout>
{
   public HorizontalLayout(float desiredHeight, float spacing) {
     super(spacing);
     this.totalHeight = desiredHeight;
   }

   public HorizontalLayout(float spacing) {
     super(spacing);
     this.shouldAutoScaleToContent = true;
   }

   public float getContentWidth() { return this.totalWidth - this.spacing; } public float getContentHeight() {
     return this.totalHeight;
   }

   protected void updateSize(AbstractWidget newChild) {
     this.totalWidth += this.spacing + newChild.getWidth();
   }
   protected void autoscale() {
     scaleToTallestChild();
   }

   protected void anchorChildren(InterpolationSpeed withDelay) {
     float top = getContentTop();
     float currX = getContentLeft();

     for (LayoutItem child : this.children) {
       AbstractWidget widget = child.widget;
       AnchorPosition anchor = child.anchor;

       float widgetWidth = widget.getWidth();

       float x = anchor.getXFromLeft(currX, widgetWidth);
       float y = anchor.getYFromTop(top, this.totalHeight);

       widget.anchoredAt(x, y, anchor, withDelay);

       currX += widgetWidth + this.spacing;
     }
   }

   public void clear() {
     super.clear();
     this.totalWidth = 0.0F;
   }

   public HorizontalLayout scaleToTallestChild() {
     this

       .totalHeight = ((Float)this.children.stream().map(item -> Float.valueOf(item.widget.getHeight())).max(Float::compareTo).orElse(Float.valueOf(0.0F))).floatValue();

     return this;
   }
}
