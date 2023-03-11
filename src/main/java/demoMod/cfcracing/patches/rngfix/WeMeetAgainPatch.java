package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.random.Random;

public class WeMeetAgainPatch {
    @SpirePatch(
            clz = WeMeetAgain.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Prefix(WeMeetAgain event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 12265L);
        }
    }
}
