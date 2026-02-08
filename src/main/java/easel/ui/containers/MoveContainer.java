package easel.ui.containers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.layouts.GridLayout;
import easel.ui.layouts.HorizontalLayout;
import easel.ui.layouts.VerticalLayout;
import easel.utils.EaselInputHelper;
import easel.utils.EaselMathHelper;
import easel.utils.EaselSoundHelper;
import easel.utils.UpdateSuppressor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

public class MoveContainer
   extends AbstractWidget<MoveContainer>
{
   private final float width;
   private final float height;
   private final TreeMap<Integer, MapItem> map = new TreeMap<>();

   private int addOrder = 0; private boolean moving; private AbstractWidget moveTarget; private float startingWidgetLeft; private float startingWidgetTop;
   private int startingMouseX;
   private int startingMouseY;

   private static class MapItem { AbstractWidget widget;

     public MapItem(AbstractWidget widget, int addOrder) {
       this.widget = widget;
       this.addOrder = addOrder;
     }
     int addOrder; }

   public MoveContainer() {
     this.width = Settings.WIDTH;
     this.height = Settings.HEIGHT;
   }

   public MoveContainer withChild(AbstractWidget child) {
     this.map.put(Integer.valueOf(getTopMostIndex() + 1), new MapItem(child, this.addOrder++));
     return this;
   }

   public MoveContainer withAllChildrenOfLayout(VerticalLayout layout) {
     layout.iterator().forEach(child -> this.map.put(Integer.valueOf(getTopMostIndex() + 1), new MapItem(child, this.addOrder++)));

     layout.clear();

     return this;
   }

   public MoveContainer withAllChildrenOfLayout(HorizontalLayout layout) {
     layout.iterator().forEach(child -> this.map.put(Integer.valueOf(getTopMostIndex() + 1), new MapItem(child, this.addOrder++)));

     layout.clear();
     return this;
   }

   public MoveContainer withAllChildrenOfLayout(GridLayout layout) {
     layout.iterator().forEach(child -> this.map.put(Integer.valueOf(getTopMostIndex() + 1), new MapItem(child, this.addOrder++)));

     layout.clear();

     return this;
   }

   private int getBottomMostIndex() {
     return this.map.isEmpty() ? 0 : ((Integer)this.map.firstKey()).intValue();
   }

   private int getTopMostIndex() {
     return this.map.isEmpty() ? 0 : ((Integer)this.map.lastKey()).intValue();
   }

   private void bringIndexToBottom(int index) {
     MapItem item = this.map.get(Integer.valueOf(index));
     this.map.remove(Integer.valueOf(index));
     this.map.put(Integer.valueOf(getBottomMostIndex() - 1), item);
   }

   private void bringIndexToTop(int index) {
     MapItem item = this.map.get(Integer.valueOf(index));
     this.map.remove(Integer.valueOf(index));
     this.map.put(Integer.valueOf(getTopMostIndex() + 1), item);
   }

   public float getContentWidth()
   {
     return this.width; } public float getContentHeight() {
     return this.height;
   }

   public Stream<AbstractWidget> iterator() {
     return this.map.values().stream().map(item -> item.widget);
   }

   public <T> Stream<T> iteratorOfType(Class<T> clz) {
     return iterator().filter(clz::isInstance).map(clz::cast);
   }

   private Optional<Map.Entry<Integer, MapItem>> findTopMostWidgetUnderMouse() {
     for (Map.Entry<Integer, MapItem> item : (Iterable<Map.Entry<Integer, MapItem>>)this.map.descendingMap().entrySet()) {
       if (((MapItem)item.getValue()).widget.isMouseInContentBounds()) {
         return Optional.of(item);
       }
     }
     return Optional.empty();
   }

   private void updateCurrentlyMoving() {
     int currMouseX = EaselInputHelper.getMouseX();
     int currMouseY = EaselInputHelper.getMouseY();

     int deltaX = this.startingMouseX - currMouseX;
     int deltaY = this.startingMouseY - currMouseY;

     float newWidgetLeft = this.startingWidgetLeft - deltaX;
     float newWidgetTop = this.startingWidgetTop - deltaY;

     if (EaselInputHelper.isShiftPressed()) {
       newWidgetLeft = EaselMathHelper.roundToMultipleOf(newWidgetLeft, 10.0F);
       newWidgetTop = EaselMathHelper.roundToMultipleOf(newWidgetTop, 10.0F);
     }

     this.moveTarget.anchoredAtClamped(newWidgetLeft, newWidgetTop, AnchorPosition.LEFT_TOP, 20.0F);

     if (InputHelper.justReleasedClickLeft) {
       this.moving = false;

       if (this.moveTarget instanceof StyledContainer) {
         ((StyledContainer)this.moveTarget).withShadows(false);
       }

       UpdateSuppressor.suppressUpdates(false);
     }
   }

   protected void updateWidget() {
     iterator().forEach(AbstractWidget::update);

     if (this.moving) {
       updateCurrentlyMoving();
     } else {

       Optional<Map.Entry<Integer, MapItem>> target = findTopMostWidgetUnderMouse();

       if (!target.isPresent()) {
         UpdateSuppressor.suppressAll(false);

         return;
       }

       if (InputHelper.justClickedLeft) {
         UpdateSuppressor.suppressAll(true);

         Map.Entry<Integer, MapItem> validTarget = target.get();
         this.moveTarget = ((MapItem)validTarget.getValue()).widget;

         this.moveTarget.cancelMovementQueue(true);

         if (this.moveTarget instanceof StyledContainer) {
           ((StyledContainer)this.moveTarget).withShadows(true);
         }

         bringIndexToTop(((Integer)validTarget.getKey()).intValue());

         EaselSoundHelper.uiClick1();
         this.moving = true;

         this.startingMouseX = EaselInputHelper.getMouseX();
         this.startingMouseY = EaselInputHelper.getMouseY();

         this.startingWidgetLeft = this.moveTarget.getLeft();
         this.startingWidgetTop = this.moveTarget.getTop();
       } else {

         UpdateSuppressor.suppressTips(true);
       }
     }

     updateInteractivity();
   }

   protected void renderWidget(SpriteBatch sb) {
     iterator().forEach(w -> w.render(sb));
   }

   private boolean forcePosition(int orderedPosition, float newLeft, float newBottom) {
     for (Map.Entry<Integer, MapItem> entry : this.map.entrySet()) {
       MapItem item = entry.getValue();

       if (item.addOrder == orderedPosition) {
         item.widget.anchoredAtClamped(newLeft, newBottom, AnchorPosition.LEFT_BOTTOM, 20.0F);

         bringIndexToTop(((Integer)entry.getKey()).intValue());
         return true;
       }
     }

     return false;
   }

   private static class SerializationHelperWidget {
     int addOrder;
     float left;
     float bottom;

     public SerializationHelperWidget(int addOrder, AbstractWidget widget) {
       this.addOrder = addOrder;
       this.left = widget.getLeft();
       this.bottom = widget.getBottom();
     }
   }

   private static class SerializationHelperContainer
   {
     List<MoveContainer.SerializationHelperWidget> widgets;

     private SerializationHelperContainer() {}
   }

   public boolean loadFromJsonString(String jsonString) {
     if (jsonString.isEmpty()) {
       return (this.map.size() == 0);
     }
     int numValuesUpdated = 0;

     Gson gson = new Gson();
     SerializationHelperContainer container = (SerializationHelperContainer)gson.fromJson(jsonString, SerializationHelperContainer.class);

     if (container != null && container.widgets != null) {
       for (SerializationHelperWidget w : container.widgets) {
         if (forcePosition(w.addOrder, w.left, w.bottom)) {
           numValuesUpdated++;
         }
       }
     }
     return (numValuesUpdated == this.map.size());
   }

   public String toJsonString() {
     SerializationHelperContainer container = new SerializationHelperContainer();
     container.widgets = new ArrayList<>(this.map.size());

     for (MapItem item : this.map.values()) {
       container.widgets.add(new SerializationHelperWidget(item.addOrder, item.widget));
     }

     return (new Gson()).toJson(container);
   }
}
