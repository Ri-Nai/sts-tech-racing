package demoMod.cfcracing.patches.rngfix;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import com.megacrit.cardcrawl.cards.curses.Pride;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.util.*;
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
            Collections.shuffle(cards, new Random((AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss ? CardGroupPatch.PatchGetRandomCard2.bossCardRng : (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite ? CardGroupPatch.PatchGetRandomCard2.eliteCardRng : AbstractDungeon.cardRng)).randomLong()));
            return SpireReturn.Return(cards.get(0));
        }
    }

    @SpirePatch(
            clz = CardLibrary.class,
            method = "getCurse",
            paramtypez = {}
    )
    public static class PatchGetCurse {
        public static SpireReturn<AbstractCard> Prefix() {
            if (!(AbstractDungeon.getCurrRoom() instanceof TreasureRoom)) {
                return SpireReturn.Continue();
            }
            ArrayList<String> tmp = new ArrayList<>();
            for (Map.Entry<String, AbstractCard> stringAbstractCardEntry : ((HashMap<String, AbstractCard>) ReflectionHacks.getPrivateStatic(CardLibrary.class, "curses")).entrySet()) {
                if (!stringAbstractCardEntry.getValue().cardID.equals(AscendersBane.ID) && !stringAbstractCardEntry.getValue().cardID.equals(Necronomicurse.ID) && !stringAbstractCardEntry.getValue().cardID.equals(CurseOfTheBell.ID) && !stringAbstractCardEntry.getValue().cardID.equals(Pride.ID)) {
                    tmp.add(stringAbstractCardEntry.getKey());
                }
            }
            return SpireReturn.Return(CardLibrary.cards.get(tmp.get(AbstractDungeon.miscRng.random(0, tmp.size() - 1))));
        }
    }
}
