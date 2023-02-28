package demoMod.cfcracing.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAndDeckAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.watcher.ForeignInfluenceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.util.ArrayList;

public class MakeTempCardPatch {
    @SpirePatch(
            clz = MakeTempCardInDiscardAction.class,
            method = "update"
    )
    public static class PatchMakeTempCardInDiscardAction {
        public static void Prefix(MakeTempCardInDiscardAction action) {
            AbstractCard card = ReflectionHacks.getPrivate(action, MakeTempCardInDiscardAction.class, "c");
            if (CatFoodCupRacingMod.isDisabled(card)) {
                ReflectionHacks.setPrivate(action, AbstractGameAction.class, "duration", 0.0F);
            }
        }
    }

    @SpirePatch(
            clz = MakeTempCardInDiscardAndDeckAction.class,
            method = "update"
    )
    public static class PatchMakeTempCardInDiscardAndDeckAction {
        public static void Prefix(MakeTempCardInDiscardAndDeckAction action) {
            AbstractCard card = ReflectionHacks.getPrivate(action, MakeTempCardInDiscardAndDeckAction.class, "cardToMake");
            if (CatFoodCupRacingMod.isDisabled(card)) {
                ReflectionHacks.setPrivate(action, AbstractGameAction.class, "duration", 0.0F);
            }
        }
    }

    @SpirePatch(
            clz = MakeTempCardInDrawPileAction.class,
            method = "update"
    )
    public static class PatchMakeTempCardInDrawPileAction {
        public static void Prefix(MakeTempCardInDrawPileAction action) {
            AbstractCard card = ReflectionHacks.getPrivate(action, MakeTempCardInDrawPileAction.class, "cardToMake");
            if (CatFoodCupRacingMod.isDisabled(card)) {
                ReflectionHacks.setPrivate(action, AbstractGameAction.class, "duration", 0.0F);
            }
        }
    }

    @SpirePatch(
            clz = MakeTempCardInHandAction.class,
            method = "update"
    )
    public static class PatchMakeTempCardInHandAction {
        public static void Prefix(MakeTempCardInHandAction action) {
            AbstractCard card = ReflectionHacks.getPrivate(action, MakeTempCardInHandAction.class, "c");
            if (CatFoodCupRacingMod.isDisabled(card)) {
                ReflectionHacks.setPrivate(action, AbstractGameAction.class, "amount", 0);
            }
        }
    }

    @SpirePatch(
            clz = ForeignInfluenceAction.class,
            method = "generateCardChoices"
    )
    public static class PatchForeignInfluenceAction {
        public static SpireReturn<ArrayList<AbstractCard>> Prefix(ForeignInfluenceAction action) {
            ArrayList<AbstractCard> derp = new ArrayList<>();

            while(derp.size() != 3) {
                boolean dupe = false;
                int roll = AbstractDungeon.cardRandomRng.random(99);
                AbstractCard.CardRarity cardRarity;
                if (roll < 55) {
                    cardRarity = AbstractCard.CardRarity.COMMON;
                } else if (roll < 85) {
                    cardRarity = AbstractCard.CardRarity.UNCOMMON;
                } else {
                    cardRarity = AbstractCard.CardRarity.RARE;
                }

                AbstractCard tmp = CardLibrary.getAnyColorCard(AbstractCard.CardType.ATTACK, cardRarity);

                for (AbstractCard c : derp) {
                    if (c.cardID.equals(tmp.cardID) || CatFoodCupRacingMod.isDisabled(c)) {
                        dupe = true;
                        break;
                    }
                }

                if (!dupe) {
                    derp.add(tmp.makeCopy());
                }
            }

            return SpireReturn.Return(derp);
        }
    }
}
