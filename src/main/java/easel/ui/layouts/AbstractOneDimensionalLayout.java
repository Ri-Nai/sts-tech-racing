package easel.ui.layouts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import java.util.ArrayList;
import java.util.stream.Stream;

abstract class AbstractOneDimensionalLayout<T extends AbstractOneDimensionalLayout<T>>
   extends AbstractWidget<T> {
   protected ArrayList<LayoutItem> children = new ArrayList<>();
   protected AnchorPosition defaultChildAnchorPosition = AnchorPosition.LEFT_TOP;

   protected float totalWidth;
   protected float totalHeight;
   protected float spacing;
   protected boolean shouldAutoScaleToContent = false;
   private boolean hasAlreadyScaledToContent = false;

   public AbstractOneDimensionalLayout(float spacing) {
     this.spacing = spacing;
   }

   public T withDefaultChildAnchorPosition(AnchorPosition defaultChildAnchorPosition) {
     this.defaultChildAnchorPosition = defaultChildAnchorPosition;
     return (T)this;
   }

   public T forceChildAnchors(AnchorPosition forcedChildAnchorPosition) {
     for (LayoutItem child : this.children)
       child.anchor = forcedChildAnchorPosition;
     return (T)this;
   }

   public void clear() {
     this.children.clear();
   }

   public final T withChild(AbstractWidget child, AnchorPosition anchor) {
     this.children.add(new LayoutItem(child, anchor));
     updateSize(child);
     return (T)this;
   }

   public final T withChild(AbstractWidget child) {
     return withChild(child, this.defaultChildAnchorPosition);
   }

   public T anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     if (this.shouldAutoScaleToContent && !this.hasAlreadyScaledToContent) {
       this.shouldAutoScaleToContent = false;
       this.hasAlreadyScaledToContent = true;
       autoscale();
     }

     super.anchoredAt(x, y, anchorPosition, movementSpeed);
     anchorChildren(movementSpeed);
     return (T)this;
   }

   protected void setChildrenDelayedMovement(float deltaX, float deltaY, InterpolationSpeed movementSpeed, long startingTimeMillis) {
     iterator().forEach(child -> child.setAllDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis));
   }

   protected void cancelMovementQueueForAllChildren(boolean shouldTryAndResolveOneLastTime) {
     iterator().forEach(child -> child.cancelMovementQueue(shouldTryAndResolveOneLastTime));
   }

   public Stream<AbstractWidget> iterator() {
     return this.children.stream().map(item -> item.widget);
   }

   public <T> Stream<T> iteratorOfType(Class<T> clz) {
     return iterator().filter(clz::isInstance).map(clz::cast);
   }

   protected void renderWidget(SpriteBatch sb) {
     this.children.forEach(w -> w.widget.render(sb)); } public void renderTopLevel(SpriteBatch sb) {
     this.children.forEach(w -> w.widget.renderTopLevel(sb));
   }
   public void hide() { this.children.forEach(w -> w.widget.hide()); } public void show() {
     this.children.forEach(w -> w.widget.show());
   }

   protected abstract void updateSize(AbstractWidget paramAbstractWidget);

   protected abstract void anchorChildren(InterpolationSpeed paramInterpolationSpeed);

   protected abstract void autoscale();
}
