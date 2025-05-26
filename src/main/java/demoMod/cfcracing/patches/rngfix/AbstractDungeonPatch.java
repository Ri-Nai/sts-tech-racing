package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import com.megacrit.cardcrawl.cards.curses.Pride;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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
import com.megacrit.cardcrawl.relics.NlothsGift;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import demoMod.cfcracing.CatFoodCupRacingMod;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

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
            condition.put(MoaiHead.ID, () -> AbstractDungeon.player.hasRelic("Golden Idol") || AbstractDungeon.player.isBloodied);
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
            condition.put(NoteForYourself.ID, () -> false);
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
            for (int i = 0; i < cardsToAdd.size(); i++) {
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
        public static Random cardRarityEliteRng;
        public static Random tmpRng;

        @SpirePatch(
                clz = AbstractDungeon.class,
                method = "generateSeeds"
        )
        public static class PatchGenerateSeeds {
            public static void Postfix() {
                cardRarityRng = new Random(Settings.seed);
                cardRarityEliteRng = new Random(Settings.seed);
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
                if (!CatFoodCupRacingMod.saves.has("cardRarityEliteRngCounter")) {
                    CatFoodCupRacingMod.saves.setInt("cardRarityEliteRngCounter", 0);
                }
                cardRarityRng = new Random(Settings.seed, CatFoodCupRacingMod.saves.getInt("cardRarityRngCounter"));
                cardRarityEliteRng = new Random(Settings.seed, CatFoodCupRacingMod.saves.getInt("cardRarityEliteRngCounter"));
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
                    return SpireReturn.Return(AbstractDungeon.currMapNode == null ? getCardRarityFallback(AbstractDungeon.miscRng.random(99)) : AbstractDungeon.getCurrRoom().getCardRarity(AbstractDungeon.miscRng.random(99)));
                }
                if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
                    if (tmpRng == null) {
                        tmpRng = AbstractDungeon.cardRng;
                        AbstractDungeon.cardRng = CardGroupPatch.PatchGetRandomCard2.eliteCardRng;
                    }
                    System.out.println("cardRarityEliteRng counter: " + CatFoodCupRacingMod.saves.getInt("cardRarityEliteRngCounter"));
                    return SpireReturn.Return(getCardRarityForElite(cardRarityEliteRng.random(99)));
                } else if (tmpRng != null) {
                    AbstractDungeon.cardRng = tmpRng;
                    tmpRng = null;
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

            private static AbstractCard.CardRarity getCardRarityForElite(int roll) {
                int rareRate = 15;
                if (AbstractDungeon.player.hasRelic(NlothsGift.ID)) {
                    rareRate *= 3;
                }
                if (roll < rareRate) {
                    return AbstractCard.CardRarity.RARE;
                } else {
                    return roll < 40 + rareRate ? AbstractCard.CardRarity.UNCOMMON : AbstractCard.CardRarity.COMMON;
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
            method = "dungeonTransitionSetup"
    )
    public static class PatchDungeonTransitionSetup {
        public static void Prefix() {
            if (AbstractDungeon.actNum > 0)
                AbstractDungeon.eventRng.setCounter(AbstractDungeon.actNum * 200);
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "loadSave"
    )
    public static class PatchLoadSave {
        public static void Postfix(AbstractDungeon dungeon, SaveFile saveFile) {
            if (CatFoodCupRacingMod.saves.has("shrines")) {
                AbstractDungeon.shrineList = SaveFilePatch.gson.fromJson(CatFoodCupRacingMod.saves.getString("shrines"), new TypeToken<ArrayList<String>>() {
                }.getType());
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    String.class,
                    AbstractPlayer.class,
                    SaveFile.class
            }
    )
    public static class PatchConstructor {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (isMethodCalled(m)) {
                        m.replace("{}");
                    }
                }

                private boolean isMethodCalled(MethodCall m) {
                    return "initializeShrineList".equals(m.getMethodName());
                }
            };
        }
    }
}
