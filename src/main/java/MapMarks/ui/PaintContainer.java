package MapMarks.ui;

import MapMarks.MapMarks;
import MapMarks.utils.MapMarksTextureDatabase;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;
import easel.utils.EaselInputHelper;
import java.util.ArrayList;

public class PaintContainer
   extends AbstractWidget<PaintContainer> {
   private static final Texture TEX_CIRCLE = MapMarksTextureDatabase.PAINT_CIRCLE.getTexture();

   private static final float BLOB_SIZE = 18.0F;

   private static class PaintBlob
   {
     float x;
     float y;
     float dungeonMapOffsetY;
     Color color;

     public PaintBlob(float x, float y, float dungeonMapOffsetY, Color color) {
       this.x = x - 11.0F;
       this.y = y - 2.0F;
       this.dungeonMapOffsetY = dungeonMapOffsetY;

       this.color = color;
     }

     public void render(SpriteBatch sb, float currentDungeonMapOffsetY) {
       sb.setColor(this.color);

       float sx = this.x;
       float sy = this.y - this.dungeonMapOffsetY - currentDungeonMapOffsetY;

       sb.draw(PaintContainer.TEX_CIRCLE, sx * Settings.xScale, sy * Settings.yScale, 18.0F * Settings.xScale, 18.0F * Settings.yScale);
     }
   }

   private ArrayList<PaintBlob> blobs = new ArrayList<>();
   private float lastX; private float lastY; private static final float INTERPOLATION_DIST_THRESHOLD = 10.0F; private long previousUpdateTime; private static final long UPDATE_THRESHOLD_MS = 0L; private void addBlob(Color color) { float cx = EaselInputHelper.getMouseX(); float cy = EaselInputHelper.getMouseY(); if (this.lastX >= 0.0F) { float a = cx - this.lastX; float b = cy - this.lastY; float distance = (float)Math.sqrt((a * a + b * b)); if (distance > 10.0F) {
         int numberOfBlobsToAdd = Math.round(distance / 18.0F) * 3; for (int i = 0; i < numberOfBlobsToAdd; i++) {
           float percentAlongVector = i / numberOfBlobsToAdd; float targetX = this.lastX + percentAlongVector * a; float targetY = this.lastY + percentAlongVector * b; this.blobs.add(new PaintBlob(targetX, targetY, DungeonMapScreen.offsetY / Settings.scale, color));
         }
       }  }
      this.blobs.add(new PaintBlob(cx, cy, DungeonMapScreen.offsetY / Settings.scale, color)); this.lastX = cx; this.lastY = cy; } public PaintContainer() { this.lastX = -1.0F;
     this.lastY = -1.0F;

     this.previousUpdateTime = 0L;
     anchoredAt(0.0F, 0.0F, AnchorPosition.LEFT_BOTTOM); }
    public void clear() {
     this.blobs.clear();
   } protected void updateWidget() {
     if (CardCrawlGame.isInARun() && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP) {

       if (!EaselInputHelper.isAltPressed() || !InputHelper.isMouseDown_R) {

         this.lastX = -1.0F;

         return;
       }
       long currTime = System.currentTimeMillis();

       if (currTime - this.previousUpdateTime > 0L) {
         this.previousUpdateTime = currTime;

         addBlob(MapMarks.legendObject.getColor().get());
       }
     }  } public float getContentWidth() {
     return Settings.WIDTH;
   } public float getContentHeight() {
     return Settings.HEIGHT;
   } protected void renderWidget(SpriteBatch sb) {
     if (CardCrawlGame.isInARun() && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP) {
       float currOffset = DungeonMapScreen.offsetY;

       for (PaintBlob blob : this.blobs)
         blob.render(sb, currOffset / Settings.scale);
     }
   }
}
