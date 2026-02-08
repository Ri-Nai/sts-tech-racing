package MapMarks.ui;

import MapMarks.utils.ColorDatabase;
import MapMarks.utils.ColorEnum;
import MapMarks.utils.MapMarksTextureDatabase;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.ui.InterpolationSpeed;
import easel.ui.graphics.LayeredTextureWidget;
import easel.utils.EaselInputHelper;
import easel.utils.colors.EaselColors;

public class LegendObject
   extends AbstractWidget<LegendObject>
{
   private static final float WIDTH = 70.0F;
   private static final float HEIGHT = 70.0F;
   public static final String TIP_HEADER = "Map Marks: Controls";
   public static final String TIP_BODY = "#gRight+Click a node to toggle its highlight. #gRight+Click+Drag to toggle many nodes at once. #gRight+Click the legend items to toggle all of a certain type. #gRight+Click this legend button to clear all highlights. NL NL #pRight+Click+Drag outside nodes to open a radial menu to select the color. NL NL #rALT+Right+Click to start painting with the current color. #rALT+Right+Click this legend button to clear all pen drawings. NL NL #bControl+Right+Click a node to clear all highlights from nodes unreachable to it (i.e. no path between another previously highlighted node and the current target).";
   public static final String TIP_HEADER_SUCCINCT = "Map Marks";
   public static final String TIP_BODY_SUCCINCT = "Hold #gShift to show instructions and controls for this mod.";
   private static Color SHADOW_COLOR = Color.valueOf("94abb2ff");

   private static Color INVIS_COLOR = Color.valueOf("ffffff00");
   private static Color DIM_COLOR = Color.valueOf("ffffff47");
   private static Color HIGHLIGHT_COLOR = Color.valueOf("ffffff3d");

   private ColorEnum color = ColorEnum.RED;
   private float previousAlpha = 0.0F;

   private LayeredTextureWidget ltw;

   public LegendObject() {
     this

       .ltw = (new LayeredTextureWidget(70.0F, 70.0F)).withLayer(MapMarksTextureDatabase.LEGEND_SHADOW.getTexture(), SHADOW_COLOR).withLayer(MapMarksTextureDatabase.LEGEND_BASE.getTexture(), this.color.get()).withLayer(MapMarksTextureDatabase.LEGEND_DIM.getTexture(), INVIS_COLOR).withLayer(MapMarksTextureDatabase.LEGEND_HIGHLIGHT.getTexture(), INVIS_COLOR).withLayer(MapMarksTextureDatabase.LEGEND_TRIM.getTexture(), ColorDatabase.UI_TRIM);

     onRightMouseDown(me -> me.setDimHighlight(DIM_COLOR, INVIS_COLOR));
     onRightMouseUp(me -> me.setDimHighlight(INVIS_COLOR, HIGHLIGHT_COLOR));

     onMouseEnter(me -> me.setDimHighlight(INVIS_COLOR, HIGHLIGHT_COLOR));
     onMouseLeave(me -> me.setDimHighlight(INVIS_COLOR, INVIS_COLOR));
   }

   protected void updateWidget() {
     super.updateWidget();
     if (this.hb.hovered) {
       if (EaselInputHelper.isShiftPressed()) {
         TipHelper.renderGenericTip(1500.0F * Settings.xScale, (
             getBottom() - 20.0F) * Settings.scale, "Map Marks: Controls", "#gRight+Click a node to toggle its highlight. #gRight+Click+Drag to toggle many nodes at once. #gRight+Click the legend items to toggle all of a certain type. #gRight+Click this legend button to clear all highlights. NL NL #pRight+Click+Drag outside nodes to open a radial menu to select the color. NL NL #rALT+Right+Click to start painting with the current color. #rALT+Right+Click this legend button to clear all pen drawings. NL NL #bControl+Right+Click a node to clear all highlights from nodes unreachable to it (i.e. no path between another previously highlighted node and the current target).");

       }
       else {

         TipHelper.renderGenericTip(1500.0F * Settings.xScale, 270.0F * Settings.scale, "Map Marks", "Hold #gShift to show instructions and controls for this mod.");
       }
     }
   }

   private void setDimHighlight(Color dim, Color highlight) {
     this.ltw.withLayerColor(2, dim);
     this.ltw.withLayerColor(3, highlight);
   }

   public float getContentWidth() { return 70.0F; } public float getContentHeight() {
     return 70.0F;
   } public ColorEnum getColor() {
     return this.color;
   }

   public LegendObject anchoredAt(float x, float y, AnchorPosition anchorPosition, InterpolationSpeed movementSpeed) {
     super.anchoredAt(x, y, anchorPosition, movementSpeed);
     this.ltw.anchoredAt(x, y, anchorPosition, movementSpeed);
     return this;
   }

   public void setAlphaFromLegend(float alpha) {
     if (this.previousAlpha != alpha) {
       this.ltw.withLayerColor(1, EaselColors.withOpacity(this.color.get(), alpha));
       this.previousAlpha = alpha;
     }
   }

   protected void renderWidget(SpriteBatch sb) {
     this.ltw.render(sb);
   }

   public void setColor(ColorEnum color) {
     this.color = color;
     this.ltw.withLayerColor(1, this.color.get());
   }
}
