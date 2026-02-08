package easel.ui.layouts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.Easel;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class GridLayout
   extends AbstractWidget<GridLayout>
{
   private HashMap<GridLocation, LayoutItem> children = new HashMap<>();

   private float totalWidth;
   private float totalHeight;
   private ArrayList<Float> rowHeights = new ArrayList<>();
   private ArrayList<Float> colWidths = new ArrayList<>();

   private AnchorPosition defaultChildAnchor = AnchorPosition.LEFT_TOP;

   public GridLayout withDefaultChildAnchorPosition(AnchorPosition anchorPosition) {
     this.defaultChildAnchor = anchorPosition;
     return this;
   }

   public void clear() {
     this.children.clear();
   }

   private void updateTotalHeight() {
     this.totalHeight = ((Float)this.rowHeights.stream().reduce(Float::sum).orElse(Float.valueOf(0.0F))).floatValue();
   }

   private void updateTotalWidth() {
     this.totalWidth = ((Float)this.colWidths.stream().reduce(Float::sum).orElse(Float.valueOf(0.0F))).floatValue();
   }

   private ArrayList<Float> buildExactSizeArray(float... values) {
     ArrayList<Float> sizes = new ArrayList<>();

     for (float v : values) {
       sizes.add(Float.valueOf(v));
     }
     return sizes;
   }

   private ArrayList<Float> buildRelativeSizeArray(float total, float... values) {
     ArrayList<Float> sizes = new ArrayList<>();

     if (values.length == 0) {
       return sizes;
     }
     float sum = 0.0F;
     for (float v : values) {
       sum += v;
     }
     if (sum == 0.0F) {
       return sizes;
     }
     for (float v : values) {
       sizes.add(Float.valueOf(v / sum * total));
     }
     return sizes;
   }

   private ArrayList<Float> buildNSizeArray(float total, int count) {
     ArrayList<Float> sizes = new ArrayList<>();

     for (int i = 0; i < count; i++) {
       sizes.add(Float.valueOf(total / count));
     }
     return sizes;
   }

   public GridLayout withExactRows(float... heights) {
     this.rowHeights = buildExactSizeArray(heights);
     updateTotalHeight();
     return this;
   }

   public GridLayout withExactCols(float... widths) {
     this.colWidths = buildExactSizeArray(widths);
     updateTotalWidth();
     return this;
   }

   public GridLayout withRelativeRows(float totalHeight, float... heightRatios) {
     this.rowHeights = buildRelativeSizeArray(totalHeight, heightRatios);
     updateTotalHeight();
     return this;
   }

   public GridLayout withRelativeCols(float totalWidth, float... widthRatios) {
     this.colWidths = buildRelativeSizeArray(totalWidth, widthRatios);
     updateTotalWidth();
     return this;
   }

   public GridLayout withNEvenlySizedRows(float totalHeight, int numRows) {
     this.rowHeights = buildNSizeArray(totalHeight, numRows);
     updateTotalHeight();
     return this;
   }

   public GridLayout withNEvenlySizedCols(float totalWidth, int numCols) {
     this.colWidths = buildNSizeArray(totalWidth, numCols);
     updateTotalWidth();
     return this;
   }

   public GridLayout withRowsCols(float totalWidth, float totalHeight, int numRows, int numCols) {
     withNEvenlySizedRows(totalHeight, numRows);
     withNEvenlySizedCols(totalWidth, numCols);
     return this;
   }

   public float getContentWidth() {
     return this.totalWidth; } public float getContentHeight() {
     return this.totalHeight;
   }

   public Stream<LayoutItem> iteratorByRow(int row) {
     return this.children.entrySet()
       .stream()
       .filter(a -> (((GridLocation)a.getKey()).row == row))
       .map(Map.Entry::getValue);
   }

   public Stream<LayoutItem> iteratorByCol(int col) {
     return this.children.entrySet()
       .stream()
       .filter(a -> (((GridLocation)a.getKey()).col == col))
       .map(Map.Entry::getValue);
   }

   public <T> Stream<T> iteratorByColOfType(int col, Class<T> clz) {
     return iteratorByCol(col).filter(clz::isInstance).map(clz::cast);
   }

   public <T> Stream<T> iteratorByRowOfType(int row, Class<T> clz) {
     return iteratorByRow(row).filter(clz::isInstance).map(clz::cast);
   }

   public GridLayout withChild(int row, int col, AbstractWidget widget, AnchorPosition anchorPosition) {
     this.children.put(new GridLocation(row, col), new LayoutItem(widget, anchorPosition));
     return this;
   }

   public GridLayout withChild(int row, int col, AbstractWidget widget) {
     return withChild(row, col, widget, this.defaultChildAnchor);
   }

   public GridLayout withChildrenInRow(int row, AbstractWidget... widgets) {
     for (int col = 0; col < widgets.length; col++) {
       withChild(row, col, widgets[col]);
     }

     return this;
   }

   public GridLayout withChildrenInCol(int col, AbstractWidget... widgets) {
     for (int row = 0; row < widgets.length; row++) {
       withChild(row, col, widgets[row]);
     }

     return this;
   }

   private float getColLeft(int col) {
     float pos = 0.0F;
     for (int i = 0; i < col && i < this.colWidths.size(); i++)
       pos += ((Float)this.colWidths.get(i)).floatValue();
     return pos;
   }

   private float getRowTop(int row) {
     float pos = 0.0F;
     for (int i = 0; i < row && i < this.rowHeights.size(); i++)
       pos -= ((Float)this.rowHeights.get(i)).floatValue();
     return pos;
   }

   private float getColWidth(int col) {
     return (col < this.colWidths.size()) ? ((Float)this.colWidths.get(col)).floatValue() : 0.0F;
   }

   private float getRowHeight(int row) {
     return (row < this.rowHeights.size()) ? ((Float)this.rowHeights.get(row)).floatValue() : 0.0F;
   }

   public GridLayout resizeRowToFitTallestChild(int row) {
     if (row > this.rowHeights.size()) {
       return this;
     }

     float maxHeightInRow = ((Float)this.children.entrySet().stream().filter(entry -> (((GridLocation)entry.getKey()).row == row)).map(entry -> Float.valueOf(((LayoutItem)entry.getValue()).widget.getHeight())).max(Float::compareTo).orElse(Float.valueOf(0.0F))).floatValue();

     this.rowHeights.set(row, Float.valueOf(maxHeightInRow));
     updateTotalHeight();

     return this;
   }

   public GridLayout resizeRowsToFitTallestChildren() {
     for (int row = 0; row < this.rowHeights.size(); row++) {
       resizeRowToFitTallestChild(row);
     }
     return this;
   }

   public GridLayout resizeColToFitWidestChild(int col) {
     if (col > this.colWidths.size()) {
       return this;
     }

     float maxWidthInCol = ((Float)this.children.entrySet().stream().filter(entry -> (((GridLocation)entry.getKey()).col == col)).map(entry -> Float.valueOf(((LayoutItem)entry.getValue()).widget.getWidth())).max(Float::compareTo).orElse(Float.valueOf(0.0F))).floatValue();

     this.colWidths.set(col, Float.valueOf(maxWidthInCol));
     updateTotalWidth();

     return this;
   }

   public GridLayout resizeColsToFitWidestChildren() {
     for (int col = 0; col < this.colWidths.size(); col++) {
       resizeColToFitWidestChild(col);
     }
     return this;
   }

   public Stream<AbstractWidget> iterator() {
     return this.children.values().stream().map(item -> item.widget);
   }

   public <T> Stream<T> iteratorOfType(Class<T> clz) {
     return iterator().filter(clz::isInstance).map(clz::cast);
   }

   private void anchorChild(AbstractWidget child, int row, int col, AnchorPosition target, InterpolationSpeed withDelay) {
     if (row >= this.rowHeights.size() || col >= this.colWidths.size()) {
       Easel.logger.warn("Warning: attempt to anchor child " + child + " to GridLayout " + this + " failed: (row, col) index out of bounds.");
     }

     float colLeft = getColLeft(col);
     float rowTop = getRowTop(row);

     float colWidth = getColWidth(col);
     float rowHeight = getRowHeight(row);

     float x = colLeft;
     if (target.isCenterX()) {
       x += colWidth * 0.5F;
     } else if (target.isRight()) {
       x += colWidth;
     }
     float y = rowTop;
     if (target.isCenterY()) {
       y -= rowHeight * 0.5F;
     } else if (target.isBottom()) {
       y -= rowHeight;
     }
     child.anchoredAt(x + getContentLeft(), y + getContentTop(), target, withDelay);
   }

   private void anchorAllChildren(InterpolationSpeed withDelay) {
     for (Map.Entry<GridLocation, LayoutItem> gridEntry : this.children.entrySet()) {
       GridLocation location = gridEntry.getKey();
       LayoutItem item = gridEntry.getValue();

       anchorChild(item.widget, location.row, location.col, item.anchor, withDelay);
     }
   }

   protected void cancelMovementQueueForAllChildren(boolean shouldTryAndResolveOneLastTime) {
     iterator().forEach(child -> child.cancelMovementQueue(shouldTryAndResolveOneLastTime));
   }

   public GridLayout anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     super.anchoredAt(x, y, anchorPosition, movementSpeed);
     anchorAllChildren(movementSpeed);
     return this;
   }

   protected void setChildrenDelayedMovement(float deltaX, float deltaY, InterpolationSpeed movementSpeed, long startingTimeMillis) {
     iterator().forEach(child -> child.setAllDelayedMovement(deltaX, deltaY, movementSpeed, startingTimeMillis));
   }

   protected void renderWidget(SpriteBatch sb) {
     this.children.values().forEach(w -> w.widget.render(sb));
   }

   public void renderTopLevel(SpriteBatch sb) {
     this.children.values().forEach(c -> c.widget.renderTopLevel(sb));
   }

   public void updateWidget() {
     this.children.values().forEach(w -> w.widget.update());
   }

   public void show() {
     this.children.values().forEach(w -> w.widget.show());
   }

   public void hide() {
     this.children.values().forEach(w -> w.widget.hide());
   }
}
