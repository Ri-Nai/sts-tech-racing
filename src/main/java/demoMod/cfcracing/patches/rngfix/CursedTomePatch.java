package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.CursedTome;
import com.megacrit.cardcrawl.random.Random;

public class CursedTomePatch {
    @SpirePatch(
            clz = CursedTome.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(CursedTome event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 175L);
        }
    }
}
