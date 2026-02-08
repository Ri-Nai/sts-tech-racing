package easel.ui.misc;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.ui.AbstractWidget;
import easel.ui.debug.DebugWidget;
import easel.utils.EaselGraphicsHelper;

public class PlaceholderWidget
   extends AbstractWidget<PlaceholderWidget>
{
   private float width;
   private float height;

   public PlaceholderWidget(float width, float height) {
     this.width = width;
     this.height = height;
   }

   public float getContentWidth() { return this.width; } public float getContentHeight() {
     return this.height;
   }

   protected void renderWidget(SpriteBatch sb) {
     EaselGraphicsHelper.drawRect(sb, this, false, DebugWidget.DEBUG_COLOR_0);
   }
}
