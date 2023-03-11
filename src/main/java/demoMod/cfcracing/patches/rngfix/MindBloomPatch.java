package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.MindBloom;
import com.megacrit.cardcrawl.random.Random;

public class MindBloomPatch {
    @SpirePatch(
            clz = MindBloom.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(MindBloom event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 9576151L);
        }
    }
}
