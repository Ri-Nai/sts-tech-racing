package easel.ui.graphics.pie;

import com.badlogic.gdx.graphics.Color;
import easel.ui.graphics.ShaderWidget;
import java.util.ArrayList;

public class PieChartWidget
   extends ShaderWidget<PieChartWidget>
{
   private static final float TWO_PI = 6.2831855F;
   private boolean shouldRebuildThetas = true;
   private boolean shouldRebuildColors = true;
   private final ArrayList<Integer> countsList = new ArrayList<>();
   private final ArrayList<Float> colorsList = new ArrayList<>();

   private float[] thetasArray;

   private float[] colorsArray;

   public PieChartWidget(float width, float height) {
     super(width, height, "easel/shaders/pie/vert.glsl", "easel/shaders/pie/frag.glsl");
   }

   public PieChartWidget withCounts(int... counts) {
     this.countsList.clear();

     for (int c : counts) {
       this.countsList.add(Integer.valueOf(c));
     }
     rebuildThetaArray();
     return this;
   }

   public PieChartWidget withColors(Color... colors) {
     this.colorsList.clear();

     for (Color c : colors) {
       this.colorsList.add(Float.valueOf(c.r));
       this.colorsList.add(Float.valueOf(c.g));
       this.colorsList.add(Float.valueOf(c.b));
       this.colorsList.add(Float.valueOf(c.a));
     }

     rebuildColorArray();
     return this;
   }

   public PieChartWidget clear() {
     this.countsList.clear();
     this.colorsList.clear();

     this.shouldRebuildColors = true;
     this.shouldRebuildThetas = true;

     return this;
   }

   public PieChartWidget withRegion(int count, Color color) {
     this.countsList.add(Integer.valueOf(count));

     this.colorsList.add(Float.valueOf(color.r));
     this.colorsList.add(Float.valueOf(color.g));
     this.colorsList.add(Float.valueOf(color.b));
     this.colorsList.add(Float.valueOf(color.a));

     this.shouldRebuildColors = true;
     this.shouldRebuildThetas = true;

     return this;
   }

   public boolean updateRegionCount(int index, int count) {
     if (index < 0 || index >= this.countsList.size()) {
       return false;
     }
     this.countsList.set(index, Integer.valueOf(count));

     this.shouldRebuildThetas = true;

     return true;
   }

   public boolean updateRegionColor(int index, Color color) {
     if (index >= this.colorsList.size() / 4) {
       return false;
     }

     index *= 4;

     this.colorsList.set(index, Float.valueOf(color.r));
     this.colorsList.set(index + 1, Float.valueOf(color.g));
     this.colorsList.set(index + 2, Float.valueOf(color.b));
     this.colorsList.set(index + 3, Float.valueOf(color.a));

     if (!this.shouldRebuildColors) {
       this.colorsArray[index] = color.r;
       this.colorsArray[index + 1] = color.g;
       this.colorsArray[index + 2] = color.b;
       this.colorsArray[index + 3] = color.a;
     }

     return true;
   }

   private void rebuildThetaArray() {
     this.shouldRebuildThetas = false;

     this.thetasArray = new float[this.countsList.size()];

     float sum = ((Integer)this.countsList.stream().reduce(Integer.valueOf(0), Integer::sum)).intValue();
     float sumThetaSoFar = 0.0F;

     for (int i = 0; i < this.countsList.size(); i++) {
       float currTheta = ((Integer)this.countsList.get(i)).intValue() / sum * 6.2831855F;
       sumThetaSoFar += currTheta;
       this.thetasArray[i] = sumThetaSoFar;
     }
   }

   private void rebuildColorArray() {
     this.shouldRebuildColors = false;

     this.colorsArray = new float[this.colorsList.size()];
     for (int i = 0; i < this.colorsList.size(); i++) {
       this.colorsArray[i] = ((Float)this.colorsList.get(i)).floatValue();
     }
   }

   protected void setUniforms() {
     if (this.shouldRebuildColors) {
       rebuildColorArray();
     }
     if (this.shouldRebuildThetas) {
       rebuildThetaArray();
     }

     this.shaderProgram.setUniformf("borderSize", 2.0F / Math.min(getContentWidth(), getContentHeight()));

     this.shaderProgram.setUniformi("numThetas", this.thetasArray.length);
     this.shaderProgram.setUniform1fv("thetas", this.thetasArray, 0, this.thetasArray.length);

     this.shaderProgram.setUniformi("numColors", this.colorsArray.length / 4);
     this.shaderProgram.setUniform4fv("colors", this.colorsArray, 0, this.colorsArray.length);
   }
}
