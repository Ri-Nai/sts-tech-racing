package easel.ui.containers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.Easel;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;

public class SwapContainer<T extends Enum<T>>
   extends AbstractWidget<SwapContainer<T>>
{
   private AbstractWidget[] widgets;
   private AnchorPosition[] internalAnchors;
   private AnchorPosition defaultChildAnchor = AnchorPosition.CENTER;

   private Class<T> clz;
   private T currentView;
   private AbstractWidget activeWidget = null;

   private boolean isShowing = false;

   private float maxWidth = 0.0F;
   private float maxHeight = 0.0F;

   public SwapContainer(Class<T> clz) {
     int numItems = ((Enum[])clz.getEnumConstants()).length;
     this.widgets = new AbstractWidget[numItems];
     this.internalAnchors = new AnchorPosition[numItems];

     for (int i = 0; i < numItems; i++) {
       this.internalAnchors[i] = this.defaultChildAnchor;
     }

     this.clz = clz;
   }

   public SwapContainer<T> withWidget(T option, AbstractWidget widget) {
     return withWidget(option, widget, false, this.defaultChildAnchor);
   }

   public SwapContainer<T> withWidget(T option, AbstractWidget widget, boolean activeView) {
     return withWidget(option, widget, activeView, this.defaultChildAnchor);
   }

   public SwapContainer<T> withWidget(T view, AbstractWidget widget, boolean activeView, AnchorPosition childAnchor) {
     this.widgets[view.ordinal()] = widget;
     this.internalAnchors[view.ordinal()] = childAnchor;

     if (widget.getWidth() > this.maxWidth)
       this.maxWidth = widget.getWidth();
     if (widget.getHeight() > this.maxHeight) {
       this.maxHeight = widget.getHeight();
     }
     if (activeView) {
       this.activeWidget = widget;
       this.currentView = view;
     }

     scaleHitboxToContent();

     return this;
   }

   public SwapContainer<T> withDefaultChildAnchor(AnchorPosition defaultChildAnchor) {
     this.defaultChildAnchor = defaultChildAnchor;
     return this;
   }

   public SwapContainer<T> forceChildAnchors(AnchorPosition forcedChildAnchorPosition) {
     for (int i = 0; i < this.widgets.length; i++)
       this.internalAnchors[i] = forcedChildAnchorPosition;
     return this;
   }

   public SwapContainer<T> updateAnchorAt(T view, AnchorPosition newAnchor) {
     this.internalAnchors[view.ordinal()] = newAnchor;
     return this;
   }

   public SwapContainer<T> withView(T choice) {
     AbstractWidget target = this.widgets[choice.ordinal()];

     if (this.isShowing && target != this.activeWidget && this.activeWidget != null) {
       this.activeWidget.hide();
     }
     this.activeWidget = target;
     this.currentView = choice;

     if (this.isShowing && this.activeWidget != null) {
       this.activeWidget.show();
     }
     return this;
   }

   public SwapContainer<T> nextView() {
     int next = (this.currentView.ordinal() + 1) % this.widgets.length;
     return withView((T)((Enum[])this.clz.getEnumConstants())[next]);
   }

   public SwapContainer<T> anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     super.anchoredAt(x, y, anchorPosition, movementSpeed);

     for (int i = 0; i < this.widgets.length; i++) {
       AbstractWidget w = this.widgets[i];

       if (w == null) {
         Easel.logger.warn("Trying to anchor a null widget?");
       } else {

         AnchorPosition anchor = this.internalAnchors[i];

         float wx = anchor.getXFromLeft(getContentLeft(), getContentWidth());
         float wy = anchor.getYFromBottom(getContentBottom(), getContentHeight());

         w.anchoredAt(wx, wy, anchor, movementSpeed);
       }
     }

     return this;
   }

   public float getContentWidth() {
     return this.maxWidth; } public float getContentHeight() {
     return this.maxHeight;
   }

   protected void renderWidget(SpriteBatch sb) {
     if (this.activeWidget != null) {
       this.activeWidget.render(sb);
     }
   }

   protected void updateWidget() {
     if (this.activeWidget != null) {
       this.activeWidget.update();
     }
   }

   public void show() {
     if (this.activeWidget != null) {
       this.activeWidget.show();
     }
     this.isShowing = true;
   }

   public void hide() {
     if (this.activeWidget != null) {
       this.activeWidget.hide();
     }
     this.isShowing = false;
   }
}
