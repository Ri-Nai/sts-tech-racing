package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CardLibraryPatch {
    @SpirePatch(
            clz = CardLibrary.class,
            method = "getAnyColorCard",
            paramtypez = {
                    AbstractCard.CardType.class,
                    AbstractCard.CardRarity.class
            }
    )
    public static class PatchGetAnyColorCard1 {
        public static SpireReturn<AbstractCard> Prefix(AbstractCard.CardType type, AbstractCard.CardRarity rarity) {
            List<AbstractCard> cards = CardLibrary.cards.values()
                    .stream().filter(
                    card -> card.rarity == rarity
                            && !card.hasTag(AbstractCard.CardTags.HEALING)
                            && card.type == type
                            && !UnlockTracker.isCardLocked(card.cardID)
                            && !CatFoodCupRacingMod.isDisabled(card)).collect(Collectors.toList());
            Collections.shuffle(cards, new Random(AbstractDungeon.cardRandomRng.randomLong()));
            return SpireReturn.Return(cards.get(0));
        }
    }

    @SpirePatch(
            clz = CardLibrary.class,
            method = "getAnyColorCard",
            paramtypez = {
                    AbstractCard.CardRarity.class
            }
    )
    public static class PatchGetAnyColorCard2 {
        public static SpireReturn<AbstractCard> Prefix(AbstractCard.CardRarity rarity) {
            List<AbstractCard> cards = CardLibrary.cards.values().stream().filter(
                    card -> card.rarity == rarity
                            && !card.hasTag(AbstractCard.CardTags.HEALING)
                            && !UnlockTracker.isCardLocked(card.cardID)
                            && !CatFoodCupRacingMod.isDisabled(card)).collect(Collectors.toList());
            Collections.shuffle(cards, new Random(AbstractDungeon.cardRandomRng.randomLong()));
            return SpireReturn.Return(cards.get(0));
        }
    }
}
