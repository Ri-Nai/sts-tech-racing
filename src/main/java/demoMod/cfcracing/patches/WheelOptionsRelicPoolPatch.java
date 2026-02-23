package demoMod.cfcracing.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import demoMod.cfcracing.wheelOptions.WheelOptions;

/**
 * Ensure wheel option relic removals are applied every time relic pools are re-initialized
 * (e.g., when starting a new act).
 */
public class WheelOptionsRelicPoolPatch {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "initializeRelicList"
    )
    public static class ApplyWheelOptionRemovals {
        @SpirePostfixPatch
        public static void Postfix() {
            WheelOptions.PROXY.onInitializeRelicList();
        }
    }
}
