package demoMod.cfcracing.patches.rngfix;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import com.megacrit.cardcrawl.events.city.DrugDealer;
import com.megacrit.cardcrawl.events.city.KnowingSkull;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.events.exordium.LivingWall;
import com.megacrit.cardcrawl.events.shrines.Designer;
import com.megacrit.cardcrawl.events.shrines.Transmogrifier;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Astrolabe;
import com.megacrit.cardcrawl.relics.PandorasBox;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 综合修复各类事件中的随机数紊乱问题
 * 从 defect-racing-2 移植：Designer、LivingWall、DrugDealer、Astrolabe、PandorasBox、
 * Transmogrifier、KnowingSkull、SensoryStone、TheLibrary
 */
public class EventRngFixPatch {
    public static Random skullRng;
    static Random sensoryStoneRng;
    public static Random libraryRng;

    // ==================== Designer ====================
    @SpirePatch(clz = Designer.class, method = "buttonEffect")
    public static class DesignerPatch {
        public static void Prefix(Designer event, int p) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 43520L);
        }
    }

    // ==================== LivingWall ====================
    @SpirePatch(clz = LivingWall.class, method = "buttonEffect")
    public static class LivingWallPatch {
        public static void Prefix(LivingWall event, int p) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 64521L);
        }
    }

    // ==================== DrugDealer ====================
    @SpirePatch(clz = DrugDealer.class, method = "buttonEffect")
    public static class DrugDealerPatch {
        public static void Prefix(DrugDealer event, int p) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 99845L);
        }
    }

    // ==================== Astrolabe ====================
    @SpirePatch(clz = Astrolabe.class, method = "onEquip")
    public static class AstrolabePatch {
        public static void Prefix(Astrolabe self) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 12343L + AbstractDungeon.floorNum);
        }
    }

    // ==================== PandorasBox miscRng 前置修复 ====================
    @SpirePatch(clz = PandorasBox.class, method = "onEquip")
    public static class PandorasBoxPrefixPatch {
        public static void Prefix(PandorasBox self) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 12343L + AbstractDungeon.floorNum);
        }
    }

    // ==================== Transmogrifier ====================
    @SpirePatch(clz = Transmogrifier.class, method = "<ctor>")
    public static class TransmogrifierPatch {
        public static void Prefix(Transmogrifier event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 84512L);
        }
    }

    // ==================== KnowingSkull ====================
    public static AbstractCard returnColorlessCard2(AbstractCard.CardRarity rarity) {
        ArrayList<AbstractCard> tmp = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.colorlessCardPool.group) {
            if (c.rarity == rarity) {
                tmp.add(c);
            }
        }
        if (tmp.isEmpty()) {
            return null;
        }
        Collections.sort(tmp);
        return tmp.get(skullRng.random(tmp.size() - 1));
    }

    @SpirePatch(clz = KnowingSkull.class, method = "obtainReward")
    public static class KnowingSkullPatch {
        @SpireInsertPatch(rloc = 32, localvars = {"c"})
        public static void Insert(KnowingSkull self, int slot, @ByRef AbstractCard[] c) {
            c[0] = EventRngFixPatch.returnColorlessCard2(AbstractCard.CardRarity.UNCOMMON).makeCopy();
        }
    }

    @SpirePatch(clz = KnowingSkull.class, method = "onEnterRoom")
    public static class KnowingSkullPatch2 {
        public static void Prefix(KnowingSkull self) {
            EventRngFixPatch.skullRng = new Random(Settings.seed + 1443L);
        }
    }

    // ==================== SensoryStone ====================
    public static AbstractCard getRandomCard2(CardGroup cg, AbstractCard.CardRarity rarity) {
        ArrayList<AbstractCard> tmp = new ArrayList<>();
        for (AbstractCard c : cg.group) {
            if (c.rarity == rarity) {
                tmp.add(c);
            }
        }
        if (tmp.isEmpty()) {
            return null;
        }
        Collections.sort(tmp);
        return tmp.get(sensoryStoneRng.random(tmp.size() - 1));
    }

    public static AbstractCard getColorlessCardFromPool2(AbstractCard.CardRarity rarity) {
        switch (rarity) {
            case RARE:
                AbstractCard r1 = getRandomCard2(AbstractDungeon.colorlessCardPool, rarity);
                if (r1 != null) return r1;
                // fall through
            case UNCOMMON:
                AbstractCard r2 = getRandomCard2(AbstractDungeon.colorlessCardPool, rarity);
                if (r2 != null) return r2;
        }
        return null;
    }

    public static ArrayList<AbstractCard> getColorlessRewardCards2() {
        ArrayList<AbstractCard> retVal = new ArrayList<>();
        AbstractPlayer p = AbstractDungeon.player;
        int numCards = 3;

        for (AbstractRelic r : p.relics) {
            numCards = r.changeNumberOfCardsInReward(numCards);
        }
        if (ModHelper.isModEnabled("Binary")) {
            numCards--;
        }

        for (int i = 0; i < numCards; i++) {
            AbstractCard.CardRarity rarity = sensoryStoneRng.randomBoolean(AbstractDungeon.colorlessRareChance)
                    ? AbstractCard.CardRarity.RARE : AbstractCard.CardRarity.UNCOMMON;
            AbstractCard card = null;
            switch (rarity) {
                case UNCOMMON:
                    card = getColorlessCardFromPool2(rarity);
                    break;
                case RARE:
                    card = getColorlessCardFromPool2(rarity);
                    AbstractDungeon.cardBlizzRandomizer = AbstractDungeon.cardBlizzStartOffset;
                    break;
            }
            while (retVal.contains(card)) {
                card = getColorlessCardFromPool2(rarity);
            }
            if (card != null) {
                retVal.add(card);
            }
        }

        ArrayList<AbstractCard> retVal2 = new ArrayList<>();
        for (AbstractCard c : retVal) {
            retVal2.add(c.makeCopy());
        }
        return retVal2;
    }

    @SpirePatch(clz = SensoryStone.class, method = "reward")
    public static class SensoryStonePatch {
        public static SpireReturn<Object> Prefix(SensoryStone self, int num) {
            AbstractDungeon.getCurrRoom().rewards.clear();
            EventRngFixPatch.sensoryStoneRng = new Random(Settings.seed + 12037L);
            for (int i = 0; i < num; i++) {
                RewardItem ri = new RewardItem(new PotionSlot(0));
                ri.type = RewardItem.RewardType.CARD;
                ri.potion = null;
                ri.cards = EventRngFixPatch.getColorlessRewardCards2();
                ri.text = RewardItem.TEXT[2];
                for (AbstractCard c : ri.cards) {
                    for (AbstractRelic a : AbstractDungeon.player.relics) {
                        a.onPreviewObtainCard(c);
                    }
                }
                AbstractDungeon.getCurrRoom().addCardReward(ri);
            }
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.combatRewardScreen.open();
            ReflectionHacks.setPrivate(self, SensoryStone.class, "screen", 3);
            return SpireReturn.Return(null);
        }
    }

    // ==================== TheLibrary ====================
    public static AbstractCard getRandomCard3(CardGroup cg) {
        ArrayList<AbstractCard> tmp = new ArrayList<>(cg.group);
        Collections.sort(tmp);
        return tmp.get(libraryRng.random(tmp.size() - 1));
    }

    public static AbstractCard.CardRarity getCardRarityFallback2(int roll) {
        int rareRate = 3;
        if (roll < rareRate) {
            return AbstractCard.CardRarity.RARE;
        }
        return (roll < 40) ? AbstractCard.CardRarity.UNCOMMON : AbstractCard.CardRarity.COMMON;
    }

    @SpirePatch(clz = TheLibrary.class, method = "buttonEffect")
    public static class TheLibraryPatch {
        @SpireInsertPatch(rlocs = {15, 24}, localvars = {"card"})
        public static void Insert(TheLibrary self, int btnPressed, @ByRef AbstractCard[] card) {
            AbstractCard.CardRarity rarity = EventRngFixPatch.getCardRarityFallback2(EventRngFixPatch.libraryRng.random(99));
            switch (rarity) {
                case COMMON:
                    card[0] = EventRngFixPatch.getRandomCard3(AbstractDungeon.commonCardPool).makeCopy();
                    break;
                case UNCOMMON:
                    card[0] = EventRngFixPatch.getRandomCard3(AbstractDungeon.uncommonCardPool).makeCopy();
                    break;
                case RARE:
                    card[0] = EventRngFixPatch.getRandomCard3(AbstractDungeon.rareCardPool).makeCopy();
                    break;
            }
        }

        public static void Prefix(TheLibrary self, int btnPressed) {
            EventRngFixPatch.libraryRng = new Random(Settings.seed + 114514L);
        }
    }
}
