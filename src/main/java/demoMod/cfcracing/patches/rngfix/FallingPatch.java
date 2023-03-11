package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.Falling;
import com.megacrit.cardcrawl.random.Random;

public class FallingPatch {
    @SpirePatch(
            clz = Falling.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Prefix(Falling event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 404L);
        }
    }
}
