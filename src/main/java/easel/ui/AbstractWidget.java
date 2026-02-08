package easel.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import easel.utils.EaselInputHelper;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Consumer;
import org.apache.commons.lang3.tuple.Pair;

public abstract class AbstractWidget<T extends AbstractWidget<T>>
{
   private float marginLeft;
   private float marginRight;
   private float marginTop;
   private float marginBottom;
   private float x;
   private float y;
   protected boolean hasInteractivity;
   protected Hitbox hb;
   protected boolean leftClickStarted;
   protected boolean rightClickStarted;
   protected boolean isHovered;
   private final Consumer<T> NOOP = x -> {

     };
   protected Consumer<T> onLeftClick = this.NOOP;
   protected Consumer<T> onRightClick = this.NOOP;
   protected Consumer<T> onMouseEnter = this.NOOP;
   protected Consumer<T> onMouseLeave = this.NOOP;

   protected Consumer<T> onRightMouseDown = this.NOOP;
   protected Consumer<T> onRightMouseUp = this.NOOP;
   protected Consumer<T> onLeftMouseDown = this.NOOP;
   protected Consumer<T> onLeftMouseUp = this.NOOP;

   private static final class DelayedMovement
   {
     private boolean relative;

     private boolean started = false;

     private float x;
     private float y;
     private float destX;
     private float destY;
     private float currX;
     private float currY;
     private InterpolationSpeed delayedSpeed;

     private DelayedMovement(float x, float y, InterpolationSpeed delayedSpeed, boolean isRelative) {
       this.x = x;
       this.y = y;
       this.delayedSpeed = delayedSpeed;
       this.relative = isRelative;
     }

     public static DelayedMovement absolute(float destinationX, float destinationY, InterpolationSpeed delayedSpeed) {
       return new DelayedMovement(destinationX, destinationY, delayedSpeed, false);
     }

     public static DelayedMovement relative(float deltaX, float deltaY, InterpolationSpeed delayedSpeed) {
       return new DelayedMovement(deltaX, deltaY, delayedSpeed, true);
     }

     public float getX() {
       return this.x;
     }

     public float getY() {
       return this.y;
     }

     public boolean isRelative() {
       return this.relative;
     }

     public boolean isStarted() {
       return this.started;
     }

     public boolean isFinished() {
       return (this.currX == this.destX && this.currY == this.destY);
     }

     public void start(float currX, float currY) {
       this.started = true;

       this.currX = currX;
       this.currY = currY;

       if (this.relative) {
         this.destX = currX + this.x;
         this.destY = currY + this.y;
       } else {

         this.destX = this.x;
         this.destY = this.y;
       }
     }

     public float interpolatedX() {
       this.currX = this.delayedSpeed.interpolate(this.currX, this.destX);
       return this.currX;
     }

     public float interpolatedY() {
       this.currY = this.delayedSpeed.interpolate(this.currY, this.destY);
       return this.currY;
     }
   }

   private final TreeSet<Pair<Long, DelayedMovement>> delayedMovementQueue = new TreeSet<>(new Comparator<Pair<Long, DelayedMovement>>()
       {
         public int compare(Pair<Long, AbstractWidget.DelayedMovement> a, Pair<Long, AbstractWidget.DelayedMovement> b)
         {
           return ((Long)a.getKey()).compareTo((Long)b.getKey());
         }
       });

   public T withMargins(float all) {
     this.marginLeft = this.marginBottom = this.marginRight = this.marginTop = all;
     return (T)this;
   }

   public T withMargins(float horizontal, float vertical) {
     this.marginLeft = this.marginRight = horizontal;
     this.marginBottom = this.marginTop = vertical;
     return (T)this;
   }

   public T withMargins(float left, float right, float bottom, float top) {
     this.marginLeft = left;
     this.marginRight = right;
     this.marginBottom = bottom;
     this.marginTop = top;
     return (T)this;
   }

   public T anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     this.delayedMovementQueue.add(Pair.of(Long.valueOf(System.currentTimeMillis() - 10L), DelayedMovement.absolute(anchorPosition
             .getLeft(x, getWidth()), anchorPosition
             .getBottom(y, getHeight()), movementSpeed)));

     resolveMovementQueue();

     anchorHitboxOnTarget();

     return (T)this;
   }

   public final T anchoredAt(float x, float y, AnchorPosition anchorPosition) {
     return anchoredAt(x, y, anchorPosition, InterpolationSpeed.INSTANT);
   }

   public final T anchoredAtClamped(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed, float clampedBorder) {
     float tx = anchorPosition.getLeft(x, getWidth());
     float ty = anchorPosition.getBottom(y, getHeight());

     if (tx < clampedBorder)
       tx = clampedBorder;
     if (ty < clampedBorder) {
       ty = clampedBorder;
     }

     if (tx + getWidth() > Settings.WIDTH - clampedBorder)
       tx = Settings.WIDTH - clampedBorder - getWidth();
     if (ty + getHeight() > Settings.HEIGHT - clampedBorder) {
       ty = Settings.HEIGHT - clampedBorder - getHeight();
     }

     return anchoredAt(tx, ty, AnchorPosition.LEFT_BOTTOM, movementSpeed);
   }

   public final T anchoredAtClamped(float x, float y, AnchorPosition anchorPosition, float clampedBorder) {
     return anchoredAtClamped(x, y, anchorPosition, InterpolationSpeed.INSTANT, clampedBorder);
   }

   public final T anchoredCenteredOnScreen() {
     return anchoredCenteredOnScreen(InterpolationSpeed.INSTANT);
   }

   public final T anchoredCenteredOnScreen(InterpolationSpeed movementSpeed) {
     float screenCenterX = Settings.WIDTH / 2.0F / Settings.xScale;
     float screenCenterY = Settings.HEIGHT / 2.0F / Settings.yScale;

     return anchoredAt(screenCenterX, screenCenterY, AnchorPosition.CENTER, movementSpeed);
   }

   public final T anchoredCenteredOnMouse() {
     return anchoredCenteredOnMouse(0.0F, 0.0F, AnchorPosition.CENTER);
   }

   public final T anchoredCenteredOnMouseClamped(float clampedBorder) {
     return anchoredCenteredOnMouseClamped(0.0F, 0.0F, AnchorPosition.CENTER, clampedBorder);
   }

   public final T anchoredCenteredOnMouse(float offsetX, float offsetY, AnchorPosition anchorPosition) {
     float scaledX = InputHelper.mX / Settings.xScale;
     float scaledY = InputHelper.mY / Settings.yScale;

     return anchoredAt(scaledX + offsetX, scaledY + offsetY, anchorPosition);
   }

   public final T anchoredCenteredOnMouseClamped(float offsetX, float offsetY, AnchorPosition anchorPosition, float clampedBorder) {
     float scaledX = InputHelper.mX / Settings.xScale;
     float scaledY = InputHelper.mY / Settings.yScale;

     return anchoredAtClamped(scaledX + offsetX, scaledY + offsetY, anchorPosition, clampedBorder);
   }

   public final T refreshAnchor() {
     return anchoredAt(getLeft(), getBottom(), AnchorPosition.LEFT_BOTTOM);
   }

   private final void setPersonalDelayedMovement(float deltaX, float deltaY, InterpolationSpeed movementSpeed, long startingTimeMillis) {
     this.delayedMovementQueue.add(
         Pair.of(Long.valueOf(startingTimeMillis), DelayedMovement.relative(deltaX, deltaY, movementSpeed)));
   }

   protected void setChildrenDelayedMovement(float deltaX, float deltaY, InterpolationSpeed movementSpeed, long startingTimeMillis) {}

   public final void setAllDelayedMovement(float deltaX, float deltaY, InterpolationSpeed movementSpeed, long startingTimeMillis) {
     setPersonalDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis);

     resolveMovementQueue();

     setChildrenDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis);
   }

   public final void cancelMovementQueue(boolean shouldTryAndResolveOneLastTime) {
     if (shouldTryAndResolveOneLastTime) {
       resolveMovementQueue();
     }
     this.delayedMovementQueue.clear();

     cancelMovementQueueForAllChildren(shouldTryAndResolveOneLastTime);
   }

   protected void cancelMovementQueueForAllChildren(boolean shouldTryAndResolveOneLastTime) {}

   public final T delayedTranslate(float deltaX, float deltaY, InterpolationSpeed movementSpeed, long delayTimeMillis) {
     setAllDelayedMovement(deltaX, deltaY, movementSpeed, System.currentTimeMillis() + delayTimeMillis);
     return (T)this;
   }

   public final T translate(float deltaX, float deltaY, InterpolationSpeed movementSpeed) {
     return anchoredAt(getLeft() + deltaX, getBottom() + deltaY, AnchorPosition.LEFT_BOTTOM, movementSpeed);
   }

   public float getWidth() {
     return this.marginLeft + getContentWidth() + this.marginRight;
   }

   public float getHeight() {
     return this.marginBottom + getContentHeight() + this.marginTop;
   }

   public float getContentLeft() {
     return this.x + this.marginLeft;
   }

   public float getContentRight() {
     return this.x + this.marginLeft + getContentWidth();
   }

   public float getContentBottom() {
     return this.y + this.marginBottom;
   }

   public float getContentTop() {
     return this.y + this.marginBottom + getContentHeight();
   }

   public float getContentCenterX() {
     return this.x + this.marginLeft + 0.5F * getContentWidth();
   }

   public float getContentCenterY() {
     return this.y + this.marginBottom + 0.5F * getContentHeight();
   }

   public float getLeft() {
     return this.x;
   }

   public float getBottom() {
     return this.y;
   }

   public float getTop() {
     return this.y + getHeight();
   }

   public float getRight() {
     return this.x + getWidth();
   }

   protected void resolveMovementQueue() {
     while (!this.delayedMovementQueue.isEmpty()) {
       Pair<Long, DelayedMovement> queueEntry = this.delayedMovementQueue.first();

       long startTime = ((Long)queueEntry.getLeft()).longValue();

       if (System.currentTimeMillis() >= startTime) {
         DelayedMovement movement = (DelayedMovement)queueEntry.getRight();

         if (!movement.isStarted()) {
           movement.start(this.x, this.y);
         }

         this.x = movement.interpolatedX();
         this.y = movement.interpolatedY();

         if (movement.isFinished())
         {
           this.delayedMovementQueue.pollFirst();
         }
       }
     }
   }

   public final void render(SpriteBatch sb) {
     resolveMovementQueue();
     renderWidget(sb);

     if (this.hasInteractivity) {
       this.hb.render(sb);
     }
   }

   public void renderTopLevel(SpriteBatch sb) {}

   protected void scaleHitboxToContent() {
     if (this.hasInteractivity)
       if (this.hb == null) {
         this.hb = new Hitbox(getContentWidth() * Settings.xScale, getContentHeight() * Settings.yScale);
       } else {
         this.hb.resize(getContentWidth() * Settings.xScale, getContentHeight() * Settings.yScale);
       }
   }

   protected void initializeInteractivity() {
     this.hasInteractivity = true;
     scaleHitboxToContent();
   }

   public T onLeftClick(Consumer<T> onLeftClick) {
     this.onLeftClick = onLeftClick;
     initializeInteractivity();
     return (T)this;
   }

   public T onRightClick(Consumer<T> onRightClick) {
     this.onRightClick = onRightClick;
     initializeInteractivity();
     return (T)this;
   }

   public T onMouseEnter(Consumer<T> onMouseEnter) {
     this.onMouseEnter = onMouseEnter;
     initializeInteractivity();
     return (T)this;
   }

   public T onMouseLeave(Consumer<T> onMouseLeave) {
     this.onMouseLeave = onMouseLeave;
     initializeInteractivity();
     return (T)this;
   }

   public T onRightMouseDown(Consumer<T> onRightMouseDown) {
     this.onRightMouseDown = onRightMouseDown;
     initializeInteractivity();
     return (T)this;
   }

   public T onLeftMouseDown(Consumer<T> onLeftMouseDown) {
     this.onLeftMouseDown = onLeftMouseDown;
     initializeInteractivity();
     return (T)this;
   }

   public T onRightMouseUp(Consumer<T> onRightMouseUp) {
     this.onRightMouseUp = onRightMouseUp;
     initializeInteractivity();
     return (T)this;
   }

   public T onLeftMouseUp(Consumer<T> onLeftMouseUp) {
     this.onLeftMouseUp = onLeftMouseUp;
     initializeInteractivity();
     return (T)this;
   }

   public final void update() {
     updateInteractivity();
     updateWidget();
   }

   private void anchorHitboxOnTarget() {
     if (!this.hasInteractivity) {
       return;
     }

     float tx = this.x;
     float ty = this.y;

     for (Pair<Long, DelayedMovement> pair : this.delayedMovementQueue) {
       DelayedMovement movement = (DelayedMovement)pair.getRight();
       if (movement.isRelative()) {
         tx += movement.getX();
         ty += movement.getY();
         continue;
       }
       tx = movement.getX();
       ty = movement.getY();
     }

     float cx = tx + this.marginLeft + 0.5F * getContentWidth();
     float cy = ty + this.marginBottom + 0.5F * getContentHeight();

     this.hb.move(cx * Settings.xScale, cy * Settings.yScale);
   }

   protected void mouseEnter() {
     this.onMouseEnter.accept((T)this);
     this.isHovered = true;
   }

   protected void mouseLeave() {
     this.onMouseLeave.accept((T)this);
     this.isHovered = false;
   }

   protected void leftMouseClick() {
     this.onLeftClick.accept((T)this);
   }

   protected void rightMouseClick() {
     this.onRightClick.accept((T)this);
   }

   protected void rightMouseDown() {
     this.onRightMouseDown.accept((T)this);
   }

   protected void rightMouseReleased() {
     this.onRightMouseUp.accept((T)this);
   }

   protected void leftMouseDown() {
     this.onLeftMouseDown.accept((T)this);
   }

   protected void leftMouseReleased() {
     this.onLeftMouseUp.accept((T)this);
   }

   protected void updateInteractivity() {
     if (this.hasInteractivity) {
       this.hb.update();

       if (this.hb.hovered && !this.isHovered) {
         mouseEnter();
       } else if (!this.hb.hovered && this.isHovered) {
         mouseLeave();
       }

       updateLeftClicks();
       updateRightClicks();
     }
   }

   private void updateLeftClicks() {
     if (this.isHovered && InputHelper.justClickedLeft) {
       this.leftClickStarted = true;
       leftMouseDown();
     }
     else if (this.hb.hovered && CInputActionSet.select.isJustPressed()) {
       CInputActionSet.select.unpress();

       leftMouseReleased();
       leftMouseClick();
     }

     if (this.leftClickStarted && InputHelper.justReleasedClickLeft) {
       if (this.isHovered) {
         leftMouseReleased();
         leftMouseClick();
       }

       this.leftClickStarted = false;
     }
   }

   private void updateRightClicks() {
     if (this.isHovered && InputHelper.justClickedRight) {
       this.rightClickStarted = true;
       rightMouseDown();
     }

     if (this.rightClickStarted && InputHelper.justReleasedClickRight) {
       if (this.isHovered) {
         rightMouseReleased();
         rightMouseClick();
       }

       this.rightClickStarted = false;
     }
   }

   public boolean isMouseInContentBounds() {
     int mx = EaselInputHelper.getMouseX();
     int my = EaselInputHelper.getMouseY();

     return (mx >= getContentLeft() && mx <= getContentRight() && my >=
       getContentBottom() && my <= getContentTop());
   }

   public boolean isMouseInBounds() {
     int mx = EaselInputHelper.getMouseX();
     int my = EaselInputHelper.getMouseY();

     return (mx >= getLeft() && mx <= getRight() && my >=
       getBottom() && my <= getTop());
   }

   protected void updateWidget() {}

   public void show() {}

   public void hide() {}

   public String toString() {
     return getClass().getName() + "{ getContentLeft() = " +
       getContentLeft() + ", getContentBottom() = " +
       getContentBottom() + ", getContentRight() = " +
       getContentRight() + ", getContentTop() = " +
       getContentTop() + ", getContentWidth() = " +
       getContentWidth() + ", getContentHeight() = " +
       getContentHeight() + "  }";
   }

   public abstract float getContentWidth();

   public abstract float getContentHeight();

   protected abstract void renderWidget(SpriteBatch paramSpriteBatch);
}
