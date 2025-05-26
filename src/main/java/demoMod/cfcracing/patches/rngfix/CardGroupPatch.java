package demoMod.cfcracing.patches.rngfix;

import basemod.Pair;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.rooms.ShopRoom;

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

    /**
     * 防止开局选无色牌影响后续的卡牌掉落以及商店送货员补牌导致世界线变动
     */
    @SpirePatch(
            clz = CardGroup.class,
            method = "getRandomCard",
            paramtypez = {
                    boolean.class,
                    AbstractCard.CardRarity.class
            }
    )
    public static class PatchGetRandomCard1 {
        public static SpireReturn<AbstractCard> Prefix(CardGroup cardGroup, boolean useRng, AbstractCard.CardRarity rarity) {
            ArrayList<AbstractCard> tmp = new ArrayList<>();

            for (AbstractCard c : cardGroup.group) {
                if (c.rarity == rarity) {
                    tmp.add(c);
                }
            }

            if (tmp.isEmpty()) {
                return SpireReturn.Return(null);
            } else {
                Collections.sort(tmp);
                if (useRng) {
                    if (AbstractDungeon.getCurrRoom() instanceof NeowRoom) {
                        return SpireReturn.Return(tmp.get(NeowEvent.rng.random(tmp.size() - 1)));
                    } else if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
                        return SpireReturn.Return(tmp.get(AbstractDungeon.merchantRng.random(tmp.size() - 1)));
                    } else {
                        return SpireReturn.Return(tmp.get(AbstractDungeon.cardRng.random(tmp.size() - 1)));
                    }
                } else {
                    return SpireReturn.Return(tmp.get(MathUtils.random(tmp.size() - 1)));
                }
            }
        }
    }

    /**
     * 保证同样的种子同样的boss房掉落的金卡是相同的
     */
    @SpirePatch(
            clz = CardGroup.class,
            method = "getRandomCard",
            paramtypez = {
                    boolean.class
            }
    )
    public static class PatchGetRandomCard2 {
        public static com.megacrit.cardcrawl.random.Random bossCardRng;
        public static com.megacrit.cardcrawl.random.Random eliteCardRng;

        public static SpireReturn<AbstractCard> Prefix(CardGroup cardGroup, boolean useRng) {
            if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
                if (bossCardRng == null) {
                    MonsterRoomBossPatch.PatchOnPlayerEntry.Postfix(null);
                }
                return SpireReturn.Return(useRng ? cardGroup.group.get(bossCardRng.random(cardGroup.group.size() - 1)) : cardGroup.group.get(MathUtils.random(cardGroup.group.size() - 1)));
            }
            if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
                if (eliteCardRng == null) {
                    MonsterRoomElitePatch.PatchOnPlayerEntry.Postfix(null);
                }
                System.out.println("eliteCardRng count: " + eliteCardRng.counter);
                return SpireReturn.Return(useRng ? cardGroup.group.get(eliteCardRng.random(cardGroup.group.size() - 1)) : cardGroup.group.get(MathUtils.random(cardGroup.group.size() - 1)));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "getRandomCard",
            paramtypez = {
                    AbstractCard.CardType.class,
                    boolean.class
            }
    )
    public static class PatchGetRandomCard3 {
        public static SpireReturn<AbstractCard> Prefix(CardGroup group, AbstractCard.CardType type, boolean useRng) {
            List<AbstractCard> tmp = new ArrayList<>();

            for (AbstractCard c : group.group) {
                if (c.type == type) {
                    tmp.add(c);
                }
            }

            if (tmp.isEmpty()) {
                return SpireReturn.Return(null);
            } else {
                Collections.sort(tmp);
                if (useRng) {
                    if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
                        return SpireReturn.Return(tmp.get(AbstractDungeon.merchantRng.random(tmp.size() - 1)));
                    } else {
                        return SpireReturn.Return(tmp.get(AbstractDungeon.cardRng.random(tmp.size() - 1)));
                    }
                } else {
                    return SpireReturn.Return(tmp.get(MathUtils.random(tmp.size() - 1)));
                }
            }
        }
    }

    /**
     * 使随机插入卡牌行为结果相似
     */
    @SpirePatch(
            clz = CardGroup.class,
            method = "addToRandomSpot"
    )
    static public class PatchAddToRandomSpot{
        @SpireInsertPatch(rloc = 3)
        static public SpireReturn<Void> Insert(CardGroup group,AbstractCard c){
            int idx = (int) AbstractDungeon.cardRandomRng.random((float) group.group.size());
            if(idx < 0) idx = 0;
            if(idx > group.group.size() - 1) idx = group.group.size() - 1;
            group.group.add(idx, c);
            return SpireReturn.Return(null);
        }
    }
}
