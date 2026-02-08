package easel.ui.layouts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import java.util.stream.Stream;

public class VerticalLayout
   extends AbstractOneDimensionalLayout<VerticalLayout>
{
   public VerticalLayout(float desiredWidth, float spacing) {
     super(spacing);
     this.totalWidth = desiredWidth;
   }

   public VerticalLayout(float spacing) {
     super(spacing);
     this.shouldAutoScaleToContent = true;
   }

   public float getContentWidth() { return this.totalWidth; } public float getContentHeight() {
     return this.totalHeight - this.spacing;
   }

   protected void updateSize(AbstractWidget newChild) {
     this.totalHeight += this.spacing + newChild.getHeight();
   }

   public void clear() {
     super.clear();
     this.totalHeight = 0.0F;
   }
   protected void autoscale() {
     scaleToWidestChild();
   }

   protected void anchorChildren(InterpolationSpeed withDelay) {
     float left = getContentLeft();
     float currY = getContentTop();

     for (LayoutItem child : this.children) {
       AbstractWidget widget = child.widget;
       AnchorPosition anchor = child.anchor;

       float widgetHeight = widget.getHeight();

       float x = anchor.getXFromLeft(left, this.totalWidth);
       float y = anchor.getYFromTop(currY, widgetHeight);

       widget.anchoredAt(x, y, anchor, withDelay);

       currY -= widgetHeight + this.spacing;
     }
   }

   public VerticalLayout scaleToWidestChild() {
     this

       .totalWidth = ((Float)this.children.stream().map(item -> Float.valueOf(item.widget.getWidth())).max(Float::compareTo).orElse(Float.valueOf(0.0F))).floatValue();

     return this;
   }
}
