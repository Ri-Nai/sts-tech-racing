package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import com.megacrit.cardcrawl.cards.curses.Pride;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.MoaiHead;
import com.megacrit.cardcrawl.events.beyond.SecretPortal;
import com.megacrit.cardcrawl.events.city.Beggar;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.events.city.KnowingSkull;
import com.megacrit.cardcrawl.events.city.TheJoust;
import com.megacrit.cardcrawl.events.exordium.Cleric;
import com.megacrit.cardcrawl.events.exordium.DeadAdventurer;
import com.megacrit.cardcrawl.events.exordium.Mushrooms;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 这些随机数序列的种子现在不会随层数而改变
 */
public class AbstractDungeonPatch {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "nextRoomTransition",
            paramtypez = {
                    SaveFile.class
            }
    )
    public static class PatchNextRoomTransition {
        private static Random monsterHpRng;
        private static Random aiRng;
        private static Random shuffleRng;
        private static List<AbstractGameEffect> tmpEffectList;

        /**
         * 防止在下层时清除掉特效动画来卡掉即将塞到牌组里的诅咒
         */
        public static void Prefix(AbstractDungeon dungeon, SaveFile saveFile) {
            tmpEffectList = AbstractDungeon.effectList.stream().filter(effect -> effect instanceof ShowCardAndObtainEffect).collect(Collectors.toList());
        }

        public static void Postfix(AbstractDungeon dungeon, SaveFile saveFile) {
            AbstractDungeon.effectList.addAll(tmpEffectList);
        }

        @SpireInsertPatch(rloc = 79)
        public static void Insert1(AbstractDungeon dungeon, SaveFile saveFile) {
            monsterHpRng = AbstractDungeon.monsterHpRng;
            aiRng = AbstractDungeon.aiRng;
            shuffleRng = AbstractDungeon.shuffleRng;
        }

        @SpireInsertPatch(rloc = 84)
        public static void Insert2(AbstractDungeon dungeon, SaveFile saveFile) {
            AbstractDungeon.monsterHpRng = monsterHpRng;
            AbstractDungeon.aiRng = aiRng;
            AbstractDungeon.shuffleRng = shuffleRng;
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "loadSeeds"
    )
    public static class PatchLoadSeeds {
        public static void Postfix(SaveFile saveFile) {
            AbstractDungeon.monsterHpRng = new Random(Settings.seed, saveFile.monster_hp_seed_count);
            AbstractDungeon.aiRng = new Random(Settings.seed, saveFile.ai_seed_count);
            AbstractDungeon.shuffleRng = new Random(Settings.seed, saveFile.shuffle_seed_count);
            AbstractDungeon.cardRandomRng = new Random(Settings.seed, saveFile.card_random_seed_count);
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getEvent"
    )
    public static class PatchGetEvent {
        static final Map<String, Supplier<Boolean>> condition = new HashMap<>();

        static {
            condition.put(DeadAdventurer.ID, () -> AbstractDungeon.floorNum > 6);
            condition.put(Mushrooms.ID, () -> AbstractDungeon.floorNum > 6);
            condition.put(MoaiHead.ID, () -> AbstractDungeon.player.hasRelic("Golden Idol") ||  AbstractDungeon.player.isBloodied);
            condition.put(Cleric.ID, () -> AbstractDungeon.player.gold >= 35);
            condition.put(Beggar.ID, () -> AbstractDungeon.player.gold >= 75);
            condition.put(Colosseum.ID, () -> AbstractDungeon.currMapNode != null && AbstractDungeon.currMapNode.y > AbstractDungeon.map.size() / 2);
            condition.put(FountainOfCurseRemoval.ID, () -> AbstractDungeon.player.isCursed());
            condition.put(Designer.ID, () -> (AbstractDungeon.id.equals("TheCity") || AbstractDungeon.id.equals("TheBeyond")) && AbstractDungeon.player.gold >= 75);
            condition.put(Duplicator.ID, () -> AbstractDungeon.id.equals("TheCity") || AbstractDungeon.id.equals("TheBeyond"));
            condition.put(FaceTrader.ID, () -> AbstractDungeon.id.equals("TheCity") || AbstractDungeon.id.equals("Exordium"));
            condition.put(KnowingSkull.ID, () -> AbstractDungeon.id.equals("TheCity") && AbstractDungeon.player.currentHealth > 12);
            condition.put(Nloth.ID, () -> AbstractDungeon.id.equals("TheCity") && AbstractDungeon.player.relics.size() >= 2);
            condition.put(TheJoust.ID, () -> AbstractDungeon.id.equals("TheCity") && AbstractDungeon.player.gold >= 50);
            condition.put(WomanInBlue.ID, () -> AbstractDungeon.player.gold >= 50);
            condition.put(SecretPortal.ID, () -> AbstractDungeon.id.equals("TheBeyond") && CardCrawlGame.playtime >= 800.0F);
        }

        public static SpireReturn<AbstractEvent> Prefix(Random rng) {
            Random cpy = new Random(Settings.seed, rng.counter);
            String tmpKey = AbstractDungeon.eventList.get(cpy.random(AbstractDungeon.eventList.size() - 1));
            while (!condition.getOrDefault(tmpKey, () -> true).get()) {
                tmpKey = AbstractDungeon.eventList.get(cpy.random(AbstractDungeon.eventList.size() - 1));
            }
            AbstractDungeon.eventList.remove(tmpKey);
            return SpireReturn.Return(EventHelper.getEvent(tmpKey));
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getShrine"
    )
    public static class PatchGetShrine {
        public static SpireReturn<AbstractEvent> Prefix(Random rng) {
            List<String> tmp = new ArrayList<>();
            tmp.addAll(AbstractDungeon.shrineList);
            tmp.addAll(AbstractDungeon.specialOneTimeEventList);
            Random cpy = new Random(Settings.seed, rng.counter);
            String tmpKey = tmp.get(cpy.random(tmp.size() - 1));
            while (!PatchGetEvent.condition.getOrDefault(tmpKey, () -> true).get()) {
                tmpKey = tmp.get(cpy.random(tmp.size() - 1));
            }
            AbstractDungeon.shrineList.remove(tmpKey);
            AbstractDungeon.specialOneTimeEventList.remove(tmpKey);
            return SpireReturn.Return(EventHelper.getEvent(tmpKey));
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnTrulyRandomCardFromAvailable",
            paramtypez = {
                    AbstractCard.class,
                    Random.class
            }
    )
    public static class PatchReturnTrulyRandomCardFromAvailable {
        public static SpireReturn<AbstractCard> Prefix(AbstractCard card, Random random) {
            List<AbstractCard> list = new ArrayList<>();
            switch (card.color.name()) {
                case "COLORLESS":
                    addCardsToTmpPool(list, AbstractDungeon.colorlessCardPool.group, card);
                    break;
                case "CURSE":
                    addCardsToTmpPool(list, AbstractDungeon.curseCardPool.group.stream().filter(card1 -> !(card1 instanceof AscendersBane || card1 instanceof CurseOfTheBell || card1 instanceof Necronomicurse || card1 instanceof Pride)).collect(Collectors.toList()), card);
                    break;
                default:
                    addCardsToTmpPool(list, AbstractDungeon.commonCardPool.group, card);
                    addCardsToTmpPool(list, AbstractDungeon.srcUncommonCardPool.group, card);
                    addCardsToTmpPool(list, AbstractDungeon.srcRareCardPool.group, card);
            }
            return SpireReturn.Return(list.get(random.random(list.size() - 1)).makeCopy());
        }

        private static void addCardsToTmpPool(List<AbstractCard> tmpPool, List<AbstractCard> cardsToAdd, AbstractCard cardToTransform) {
            for (int i=0;i<cardsToAdd.size();i++) {
                AbstractCard card = cardsToAdd.get(i);
                if (!card.cardID.equals(cardToTransform.cardID)) {
                    tmpPool.add(card);
                }
                if (i > 0 && i < cardsToAdd.size() - 1) {
                    if (cardsToAdd.get(i + 1).cardID.equals(cardToTransform.cardID)) {
                        tmpPool.add(card);
                    }
                } else if (i == cardsToAdd.size() - 1 && card.cardID.equals(cardToTransform.cardID)) {
                    tmpPool.add(cardsToAdd.get(i - 1));
                } else if (i == 0 && card.cardID.equals(cardToTransform.cardID)) {
                    tmpPool.add(cardsToAdd.get(1));
                }
            }
        }
    }

    public static class CardRarityRngFix {
        public static Random cardRarityRng;

        @SpirePatch(
                clz = AbstractDungeon.class,
                method = "generateSeeds"
        )
        public static class PatchGenerateSeeds {
            public static void Postfix() {
                cardRarityRng = new Random(Settings.seed);
            }
        }

        @SpirePatch(
                clz = AbstractDungeon.class,
                method = "loadSeeds"
        )
        public static class PatchLoadSeeds {
            public static void Postfix(SaveFile save) {
                try {
                    CatFoodCupRacingMod.saves.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!CatFoodCupRacingMod.saves.has("cardRarityRngCounter")) {
                    CatFoodCupRacingMod.saves.setInt("cardRarityRngCounter", 0);
                }
                cardRarityRng = new Random(Settings.seed, CatFoodCupRacingMod.saves.getInt("cardRarityRngCounter"));
            }
        }

        @SpirePatch(
                clz = AbstractDungeon.class,
                method = "rollRarity",
                paramtypez = {}
        )
        public static class PatchRollRarity {
            public static SpireReturn<AbstractCard.CardRarity> Prefix() {
                if (AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
                    return SpireReturn.Return(AbstractDungeon.currMapNode == null ? getCardRarityFallback(AbstractDungeon.merchantRng.random(99)) : AbstractDungeon.getCurrRoom().getCardRarity(AbstractDungeon.merchantRng.random(99)));
                }
                return SpireReturn.Return(AbstractDungeon.rollRarity(cardRarityRng));
            }

            private static AbstractCard.CardRarity getCardRarityFallback(int roll) {
                int rareRate = 3;
                if (roll < rareRate) {
                    return AbstractCard.CardRarity.RARE;
                } else {
                    return roll < 40 ? AbstractCard.CardRarity.UNCOMMON : AbstractCard.CardRarity.COMMON;
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "returnRandomPotion",
            paramtypez = {
                    boolean.class
            }
    )
    public static class PatchReturnRandomPotion {
        public static SpireReturn<AbstractPotion> Prefix(boolean limited) {
            Random rng = AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() instanceof ShopRoom ? AbstractDungeon.merchantRng : AbstractDungeon.potionRng;
            int roll = rng.random(0, 99);
            if (roll < PotionHelper.POTION_COMMON_CHANCE) {
                return SpireReturn.Return(AbstractDungeon.returnRandomPotion(AbstractPotion.PotionRarity.COMMON, limited));
            } else {
                return SpireReturn.Return(roll < PotionHelper.POTION_UNCOMMON_CHANCE + PotionHelper.POTION_COMMON_CHANCE ? AbstractDungeon.returnRandomPotion(AbstractPotion.PotionRarity.UNCOMMON, limited) : AbstractDungeon.returnRandomPotion(AbstractPotion.PotionRarity.RARE, limited));
            }
        }
    }

    /**
     * 防止商店出星系仪改变后续卡牌掉落
     */
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getRewardCards"
    )
    public static class PatchGetRewardCards {
        private static AbstractCard.CardRarity tmpRarity = AbstractCard.CardRarity.COMMON;

        @SpireInsertPatch(rloc = 15, localvars = {"rarity"})
        public static void Insert1(@ByRef(type = "cards.AbstractCard$CardRarity") Object[] _rarity) {
            if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
                tmpRarity = (AbstractCard.CardRarity) _rarity[0];
                _rarity[0] = AbstractCard.CardRarity.UNCOMMON;
            }
        }

        @SpireInsertPatch(rloc = 34, localvars = {"rarity"})
        public static void Insert2(@ByRef(type = "cards.AbstractCard$CardRarity") Object[] _rarity) {
            if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
                _rarity[0] = tmpRarity;
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getMonsterForRoomCreation"
    )

    public static class PatchGetMonsterForRoomCreation {
        public static void Prefix() {
            int count;
            if (CatFoodCupRacingMod.saves.has("enemyCounter")) {
                count = CatFoodCupRacingMod.saves.getInt("enemyCounter");
                count++;
                CatFoodCupRacingMod.saves.setInt("enemyCounter", count);
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                MonsterHelperPatch.PatchGetEncounter.encounterCounter = count;
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getEliteMonsterForRoomCreation"
    )
    public static class PatchGetEliteMonsterForRoomCreation {
        public static void Prefix() {
            int count;
            if (CatFoodCupRacingMod.saves.has("eliteCounter")) {
                count = CatFoodCupRacingMod.saves.getInt("eliteCounter");
                count++;
                CatFoodCupRacingMod.saves.setInt("eliteCounter", count);
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                MonsterHelperPatch.PatchGetEncounter.encounterCounter = count;
            }
        }
    }
}
