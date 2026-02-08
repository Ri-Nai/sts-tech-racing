package easel.utils;

import com.megacrit.cardcrawl.core.CardCrawlGame;

public class EaselSoundHelper
{
   public static void cawCaw() {
     CardCrawlGame.sound.play("VO_CULTIST_1A");
   }

   public static void screenOpenSound() {
     CardCrawlGame.sound.play("DECK_OPEN");
   }

   public static void screenCloseSound() {
     CardCrawlGame.sound.play("DECK_CLOSE");
   }

   public static void uiHoverSound() {
     CardCrawlGame.sound.play("UI_HOVER");
   }

   public static void uiClick1() {
     CardCrawlGame.sound.play("UI_CLICK_1");
   }

   public static void uiClick2() {
     CardCrawlGame.sound.play("UI_CLICK_2");
   }
}
