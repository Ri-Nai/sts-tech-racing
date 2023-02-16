package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheJoust;
import com.megacrit.cardcrawl.random.Random;

public class TheJoustPatch {
    @SpirePatch(
            clz = TheJoust.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(TheJoust event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 109L); //使序列固定但不和其他事件的序列相同
        }
    }
}
