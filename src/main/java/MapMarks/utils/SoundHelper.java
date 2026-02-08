package MapMarks.utils;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class SoundHelper
{
   public static void playRadialOpenSound() {}

   public static void playMapScratchSound() {
     int roll = MathUtils.random(3);
     switch (roll) {
       case 0:
         CardCrawlGame.sound.play("MAP_HOVER_1");
         return;

       case 1:
         CardCrawlGame.sound.play("MAP_HOVER_2");
         return;

       case 2:
         CardCrawlGame.sound.play("MAP_HOVER_3");
         return;
     }

     CardCrawlGame.sound.play("MAP_HOVER_4");
   }

   public static void playRadialChangeSound(int index, int max) {
     float pitchAdjust = index / max * 1.8F;

     CardCrawlGame.sound.playA("MAP_MARKS_CLICK", pitchAdjust);
   }

   public static void playRadialCloseSound() {}
}
