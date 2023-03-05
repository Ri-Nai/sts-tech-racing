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

    /**
     * 这个是棱镜用的随机掉落牌，原版使用了cardRandomRng，会影响战斗中的随机数，比如电球的攻击目标等
     */
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
                            && !(card.type == AbstractCard.CardType.CURSE)
                            && !(card.type == AbstractCard.CardType.STATUS)
                            && !UnlockTracker.isCardLocked(card.cardID)
                            && !CatFoodCupRacingMod.isDisabled(card)).collect(Collectors.toList());
            Collections.shuffle(cards, new Random(AbstractDungeon.cardRng.randomLong()));
            return SpireReturn.Return(cards.get(0));
        }
    }
}
