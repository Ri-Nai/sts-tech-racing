package easel.ui.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import easel.ui.AbstractWidget;
import easel.utils.EaselGraphicsHelper;

public class DebugWidget extends AbstractWidget<DebugWidget> {
   public static final Color DEBUG_COLOR_0 = new Color(0.384F, 0.69F, 0.388F, 0.5F);
   public static final Color DEBUG_COLOR_1 = new Color(0.384F, 0.388F, 0.69F, 0.5F);
   public static final Color DEBUG_COLOR_2 = new Color(0.69F, 0.384F, 0.388F, 0.5F);
   public static final Color DEBUG_COLOR_3 = new Color(0.69F, 0.684F, 0.688F, 0.5F);

   private float width;
   private float height;
   private Color color;

   public DebugWidget() {
     this(100.0F, 100.0F, DEBUG_COLOR_0);
   }

   public DebugWidget(Color color) {
     this(100.0F, 100.0F, color);
   }

   public DebugWidget(float width, float height) {
     this(width, height, DEBUG_COLOR_0);
   }

   public DebugWidget(float width, float height, Color color) {
     this.width = width;
     this.height = height;
     this.color = color;
   }

   public void setColor(Color color) {
     this.color = color;
   }

   public float getContentWidth() { return this.width; } public float getContentHeight() {
     return this.height;
   }

   protected void renderWidget(SpriteBatch sb) {
     EaselGraphicsHelper.drawRect(sb, this, false, this.color);
   }
}
