package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.exordium.ScrapOoze;
import com.megacrit.cardcrawl.random.Random;

public class ScrapOozePatch {
    @SpirePatch(
            clz = ScrapOoze.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(ScrapOoze event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 157L);
        }
    }
}
