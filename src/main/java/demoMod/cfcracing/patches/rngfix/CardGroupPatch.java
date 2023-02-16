package demoMod.cfcracing.patches.rngfix;

import basemod.Pair;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.*;

public class CardGroupPatch {
    @SpirePatch(
            clz = CardGroup.class,
            method = "shuffle",
            paramtypez = {}
    )
    public static class PatchShuffle1 {
        public static SpireReturn<Void> Prefix(CardGroup group) {
            List<Integer> orders = new ArrayList<>();
            for (int i=0;i<300;i++) {
                orders.add(i);
            }
            Collections.shuffle(orders, new Random(AbstractDungeon.shuffleRng.randomLong()));
            List<Pair<Integer, AbstractCard>> cards = new ArrayList<>();
            int index = 0;
            for (Integer i : orders) {
                if (index >= group.size()) break;
                Pair<Integer, AbstractCard> cardPair = new Pair<>(i, group.group.get(index));
                cards.add(cardPair);
                index++;
            }
            int i = orders.size();
            while (index < group.size()) {
                Pair<Integer, AbstractCard> cardPair = new Pair<>(i, group.group.get(index));
                cards.add(cardPair);
                index++;
                i++;
            }
            cards.sort(Comparator.comparingInt(Pair::getKey));
            group.clear();
            for (Pair<Integer, AbstractCard> cardPair : cards) {
                group.group.add(cardPair.getValue());
            }
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "shuffle",
            paramtypez = {com.megacrit.cardcrawl.random.Random.class}
    )
    public static class PatchShuffle2 {
        public static SpireReturn<Void> Prefix(CardGroup group, com.megacrit.cardcrawl.random.Random rng) {
            List<Integer> orders = new ArrayList<>();
            for (int i=0;i<300;i++) {
                orders.add(i);
            }
            Collections.shuffle(orders, new Random(rng.randomLong()));
            List<Pair<Integer, AbstractCard>> cards = new ArrayList<>();
            int index = 0;
            for (Integer i : orders) {
                if (index >= group.size()) break;
                Pair<Integer, AbstractCard> cardPair = new Pair<>(i, group.group.get(index));
                cards.add(cardPair);
                index++;
            }
            int i = orders.size();
            while (index < group.size()) {
                Pair<Integer, AbstractCard> cardPair = new Pair<>(i, group.group.get(index));
                cards.add(cardPair);
                index++;
                i++;
            }
            cards.sort(Comparator.comparingInt(Pair::getKey));
            group.clear();
            for (Pair<Integer, AbstractCard> cardPair : cards) {
                group.group.add(cardPair.getValue());
            }
            return SpireReturn.Return(null);
        }
    }
}
