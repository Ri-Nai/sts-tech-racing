package demoMod.cfcracing.wheelOptions;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.FrozenEye;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.vfx.RestartForChangesEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import demoMod.cfcracing.CatFoodCupRacingMod;
import demoMod.cfcracing.patches.TopPanelPatch;

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
    VANILLA_GAME {
        @Override
        public void onSelectThisOption() {
            Settings.setLanguage(Settings.GameLanguage.ENG, false);
            Settings.gamePref.flush();
            CatFoodCupRacingMod.effectList.add(new RestartForChangesEffect());
        }
    },
    FORETHOUGHT {
        @Override
        public void onStartGame() {
            if (!AbstractDungeon.player.hasRelic(FrozenEye.ID)) {
                new FrozenEye().instantObtain();
            }
        }

        @Override
        public void onUseCard(AbstractCard card, UseCardAction action) {
            if (card.cost == -1) {
                AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
            }
        }
    },
    SURVIVE_MODE {
    },
    DIVERGENT_THINKING {
        @Override
        public void onEnterRoom(AbstractRoom room) {
            if (room instanceof TreasureRoom && AbstractDungeon.actNum == 2 && !AbstractDungeon.player.hasRelic(PrismaticShard.ID)) {
                new PrismaticShard().instantObtain();
            }
            AbstractDungeon.shopRelicPool.remove(PrismaticShard.ID);
        }

        @Override
        public void onObtainCard(AbstractCard card) {
            if (card.color != AbstractDungeon.player.getCardColor() && card.color != AbstractCard.CardColor.COLORLESS) {
                TopPanelPatch.correct += 16.0F;
            }
        }

        @Override
        public void onRemoveCardFromDeck(AbstractCard card) {
            if (card.color != AbstractDungeon.player.getCardColor() && card.color != AbstractCard.CardColor.COLORLESS) {
                CatFoodCupRacingMod.saves.setFloat("lastPlayTime", CatFoodCupRacingMod.saves.getFloat("lastPlayTime") + 16.0F);
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

        @Override
        public void onSelectThisOption() {
            int index = check();
            if (index < 0 || index == this.ordinal()) {
                return;
            }
            WheelOptions.values()[index].onSelectThisOption();
        }

        @Override
        public void onUseCard(AbstractCard card, UseCardAction action) {
            int index = check();
            if (index < 0 || index == this.ordinal()) {
                return;
            }
            WheelOptions.values()[index].onUseCard(card, action);
        }

        @Override
        public void onEnterRoom(AbstractRoom room) {
            int index = check();
            if (index < 0 || index == this.ordinal()) {
                return;
            }
            WheelOptions.values()[index].onEnterRoom(room);
        }

        @Override
        public void onObtainCard(AbstractCard card) {
            int index = check();
            if (index < 0 || index == this.ordinal()) {
                return;
            }
            WheelOptions.values()[index].onObtainCard(card);
        }

        @Override
        public void onRemoveCardFromDeck(AbstractCard card) {
            int index = check();
            if (index < 0 || index == this.ordinal()) {
                return;
            }
            WheelOptions.values()[index].onRemoveCardFromDeck(card);
        }
    }
    ;

    public void onStartGame() {

    }

    public void onLoadSave() {

    }

    public void onSelectThisOption() {

    }

    public void onUseCard(AbstractCard card, UseCardAction action) {

    }

    public void onEnterRoom(AbstractRoom room) {

    }

    public void onObtainCard(AbstractCard card) {

    }

    public void onRemoveCardFromDeck(AbstractCard card) {

    }
}
