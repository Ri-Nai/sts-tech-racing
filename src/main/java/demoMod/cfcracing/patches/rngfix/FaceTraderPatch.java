package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.FaceTrader;
import com.megacrit.cardcrawl.random.Random;

public class FaceTraderPatch {
    @SpirePatch(
            clz = FaceTrader.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(FaceTrader event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 343L);
        }
    }
}
