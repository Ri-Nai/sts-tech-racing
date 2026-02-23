package demoMod.cfcracing.wheelOptions;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.relics.FrozenEye;
import com.megacrit.cardcrawl.relics.IncenseBurner;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.util.ArrayList;
import java.util.List;

public enum WheelOptions {
    FUTURE_BATTLE {
        @Override
        public void onStartGame() {
            if (AbstractDungeon.player == null) {
                return;
            }
            if (!AbstractDungeon.player.hasRelic(ChemicalX.ID)) {
                new ChemicalX().instantObtain();
            }
            if (!AbstractDungeon.player.hasRelic(FrozenEye.ID)) {
                new FrozenEye().instantObtain();
            }
            removeRelicFromPools(ChemicalX.ID);
            removeRelicFromPools(FrozenEye.ID);
        }

        @Override
        public void onInitializeRelicList() {
            removeRelicFromPools(ChemicalX.ID);
            removeRelicFromPools(FrozenEye.ID);
        }
    },
    INCENSE {
        @Override
        public void onStartGame() {
            if (AbstractDungeon.player == null) {
                return;
            }
            if (!AbstractDungeon.player.hasRelic(IncenseBurner.ID)) {
                new IncenseBurner().instantObtain();
            }
            removeRelicFromPools(IncenseBurner.ID);
        }

        @Override
        public void onInitializeRelicList() {
            removeRelicFromPools(IncenseBurner.ID);
        }
    },
    WU_HONGLAN {
        // 效果由玩家在 Mod 配置界面自行调整
    },
    COPY_DEFEND {
        @Override
        public void onStartGame() {
            if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
                return;
            }
            // 检查是否已经应用过效果（通过检查是否有升级过的初始打击来判断）
            boolean alreadyApplied = false;
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (card.hasTag(AbstractCard.CardTags.STARTER_STRIKE) && card.upgraded) {
                    alreadyApplied = true;
                    break;
                }
            }
            if (alreadyApplied) {
                return;
            }

            List<AbstractCard> strikesToUpgrade = new ArrayList<>();
            AbstractCard strikeToReplace = null;

            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (card.hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                    strikesToUpgrade.add(card);
                    if (strikeToReplace == null) {
                        strikeToReplace = card;
                    }
                }
            }

            // 升级所有 strike
            for (AbstractCard strike : strikesToUpgrade) {
                if (!strike.upgraded) {
                    strike.upgrade();
                }
            }

            // 替换一张 strike 为对应角色的 defend
            if (strikeToReplace != null) {
                AbstractDungeon.player.masterDeck.group.remove(strikeToReplace);
                String defendId = getDefendIdForColor(strikeToReplace.color);
                if (defendId != null) {
                    AbstractCard defend = CardLibrary.getCard(defendId).makeCopy();
                    AbstractDungeon.player.masterDeck.addToTop(defend);
                }
            }
        }

        private String getDefendIdForColor(AbstractCard.CardColor color) {
            switch (color) {
                case RED:
                    return "Defend_R";
                case GREEN:
                    return "Defend_G";
                case BLUE:
                    return "Defend_B";
                case PURPLE:
                    return "Defend_P";
                default:
                    return null;
            }
        }
    },
    HALF_SL {
        // 这个选项不需要在 onStartGame/onLoadSave 中做任何事
        // 效果通过 isHalfSlActive() 方法动态判断
    },
    EARLY_WIN {
        @Override
        public void onStartGame() {
            if (AbstractDungeon.player == null || AbstractDungeon.player.masterDeck == null) {
                return;
            }
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if ("Sweeping Beam".equals(card.cardID)) {
                    return;
                }
            }
            AbstractCard sweepingBeam = CardLibrary.getCard("Sweeping Beam");
            if (sweepingBeam != null) {
                AbstractDungeon.player.masterDeck.addToTop(sweepingBeam.makeCopy());
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

        @Override
        public void onInitializeRelicList() {
            int index = check();
            if (index < 0 || index == this.ordinal()) {
                return;
            }
            WheelOptions.values()[index].onInitializeRelicList();
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

    public void onInitializeRelicList() {

    }

    /**
     * 判断当前游戏是否选择了 HALF_SL 转盘选项
     */
    public static boolean isHalfSlActive() {
        if (CatFoodCupRacingMod.saves.has("appliedWheelOption")) {
            return CatFoodCupRacingMod.saves.getInt("appliedWheelOption") == HALF_SL.ordinal();
        }
        return false;
    }

    private static void removeRelicFromPools(String relicId) {
        AbstractDungeon.commonRelicPool.removeIf(relicId::equals);
        AbstractDungeon.uncommonRelicPool.removeIf(relicId::equals);
        AbstractDungeon.rareRelicPool.removeIf(relicId::equals);
        AbstractDungeon.shopRelicPool.removeIf(relicId::equals);
        AbstractDungeon.bossRelicPool.removeIf(relicId::equals);
    }
}
