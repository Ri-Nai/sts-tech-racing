package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CtBehavior;

import java.util.ArrayList;

public class NeowRewardPatch {
    /**
     * 让随机诅咒使用涅奥房间专属的随机数序列，防止后续掉落的卡牌奖励有较大的改变
     */
    @SpirePatch(
            clz = NeowReward.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert(NeowReward neowReward) {
            AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(CardLibrary.getCurse(new AscendersBane(), NeowEvent.rng), (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            return SpireReturn.Return(null);
        }

        private static final class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(NeowReward.class, "cursed");
                int[] result = LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), matcher);
                result[0] = result[0] + 2;
                return result;
            }
        }
    }
}
