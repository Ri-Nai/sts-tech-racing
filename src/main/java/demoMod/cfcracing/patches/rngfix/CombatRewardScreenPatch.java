package demoMod.cfcracing.patches.rngfix;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.ArrayList;

public class CombatRewardScreenPatch {
    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "setupItemReward"
    )
    public static class PatchSetupItemReward {
        public static SpireReturn<Void> Prefix(CombatRewardScreen screen) {
            ReflectionHacks.setPrivate(screen, CombatRewardScreen.class, "rewardAnimTimer", 0.2F);
            InputHelper.justClickedLeft = false;
            screen.rewards = new ArrayList<>(AbstractDungeon.getCurrRoom().rewards);

            AbstractDungeon.overlayMenu.proceedButton.show();
            screen.hasTakenAll = false;
            screen.positionRewards();
            return SpireReturn.Return(null);
        }
    }
}
