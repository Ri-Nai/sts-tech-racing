package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import com.megacrit.cardcrawl.cards.curses.Pride;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.GremlinMatchGame;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GremlinMatchGamePatch {
    @SpirePatch(
            clz = GremlinMatchGame.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        @SpireInsertPatch(rloc = 1)
        public static void Insert() {
            int count = 0;
            if (CatFoodCupRacingMod.saves.has("matchGameCounter")) {
                count = CatFoodCupRacingMod.saves.getInt("matchGameCounter");
            }
            boolean needIncrementCount = false;
            if (CatFoodCupRacingMod.saves.has("matchGameLastFloor")) {
                if (AbstractDungeon.floorNum > CatFoodCupRacingMod.saves.getInt("matchGameLastFloor")) {
                    needIncrementCount = true;
                }
            } else {
                needIncrementCount = true;
            }
            CatFoodCupRacingMod.saves.setInt("matchGameLastFloor", AbstractDungeon.floorNum);
            if (needIncrementCount) {
                CatFoodCupRacingMod.saves.setInt("matchGameCounter", ++count);
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            AbstractDungeon.miscRng = new Random(Settings.seed + count);
        }
    }

    @SpirePatch(
            clz = GremlinMatchGame.class,
            method = "initializeCards"
    )
    public static class PatchInitializeCards {
        public static SpireReturn<ArrayList<AbstractCard>> Prefix(GremlinMatchGame event) {
            ArrayList<AbstractCard> retVal = new ArrayList<>();
            ArrayList<AbstractCard> retVal2 = new ArrayList<>();
            List<AbstractCard> curseCards = AbstractDungeon.curseCardPool.group.stream().filter(card -> !(card instanceof AscendersBane ||
                    card instanceof Necronomicurse ||
                    card instanceof CurseOfTheBell ||
                    card instanceof Pride)).collect(Collectors.toList());
            List<AbstractCard> colorlessCards = AbstractDungeon.colorlessCardPool.group.stream().filter(card -> card.rarity == AbstractCard.CardRarity.UNCOMMON).collect(Collectors.toList());
            if (AbstractDungeon.ascensionLevel >= 15) {
                retVal.add(AbstractDungeon.getCard(AbstractCard.CardRarity.RARE, AbstractDungeon.miscRng).makeCopy());
                retVal.add(AbstractDungeon.getCard(AbstractCard.CardRarity.UNCOMMON, AbstractDungeon.miscRng).makeCopy());
                retVal.add(AbstractDungeon.getCard(AbstractCard.CardRarity.COMMON, AbstractDungeon.miscRng).makeCopy());
                retVal.add(curseCards.get(AbstractDungeon.miscRng.random(0, curseCards.size() - 1)).makeCopy());
                retVal.add(curseCards.get(AbstractDungeon.miscRng.random(0, curseCards.size() - 1)).makeCopy());
            } else {
                retVal.add(AbstractDungeon.getCard(AbstractCard.CardRarity.RARE, AbstractDungeon.miscRng).makeCopy());
                retVal.add(AbstractDungeon.getCard(AbstractCard.CardRarity.UNCOMMON, AbstractDungeon.miscRng).makeCopy());
                retVal.add(AbstractDungeon.getCard(AbstractCard.CardRarity.COMMON, AbstractDungeon.miscRng).makeCopy());
                retVal.add(colorlessCards.get(AbstractDungeon.miscRng.random(0, colorlessCards.size() - 1)).makeCopy());
                retVal.add(curseCards.get(AbstractDungeon.miscRng.random(0, curseCards.size() - 1)).makeCopy());
            }

            retVal.add(AbstractDungeon.player.getStartCardForEvent());
            Iterator<AbstractCard> var3 = retVal.iterator();

            AbstractCard c;
            while(var3.hasNext()) {
                c = var3.next();
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onPreviewObtainCard(c);
                }
                retVal2.add(c.makeStatEquivalentCopy());
            }

            retVal.addAll(retVal2);
            for(var3 = retVal.iterator(); var3.hasNext(); c.target_y = c.current_y) {
                c = var3.next();
                c.current_x = (float)Settings.WIDTH / 2.0F;
                c.target_x = c.current_x;
                c.current_y = -300.0F * Settings.scale;
            }
            return SpireReturn.Return(retVal);
        }
    }
}
