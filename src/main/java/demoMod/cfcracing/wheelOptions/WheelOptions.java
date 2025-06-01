package demoMod.cfcracing.wheelOptions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import demoMod.cfcracing.CatFoodCupRacingMod;

public enum WheelOptions {
    BASIC_LOGIC {
        @Override
        public void onStartGame() {
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new IronWave(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        }
    },
    ETERNAL_CURSE {
        @Override
        public void onLoadSave() {
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (card instanceof AscendersBane) {
                    card.isEthereal = false;
                }
            }
        }
    },
    PROXY {
        private int check() {
            if (CatFoodCupRacingMod.saves.has("appliedWheelOption")) {
                return CatFoodCupRacingMod.saves.getInt("appliedWheelOption");
            }
            return -1;
        }

        @Override
        public void onStartGame() {
            int index = check();
            if (index < 0 || index == this.ordinal()) {
                return;
            }
            WheelOptions.values()[index].onStartGame();
        }

        @Override
        public void onLoadSave() {
            int index = check();
            if (index < 0 || index == this.ordinal()) {
                return;
            }
            WheelOptions.values()[index].onLoadSave();
        }
    }
    ;

    public void onStartGame() {

    }

    public void onLoadSave() {

    }
}
