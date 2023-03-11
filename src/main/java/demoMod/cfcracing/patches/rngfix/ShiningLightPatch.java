package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.exordium.ShiningLight;
import com.megacrit.cardcrawl.random.Random;

public class ShiningLightPatch {
    @SpirePatch(
            clz = ShiningLight.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(ShiningLight event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 930392L);
        }
    }
}
