package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.Nloth;
import com.megacrit.cardcrawl.random.Random;

public class NlothPatch {
    @SpirePatch(
            clz = Nloth.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Prefix(Nloth event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 608L);
        }
    }
}
