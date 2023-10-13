package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.DrugDealer;
import com.megacrit.cardcrawl.random.Random;

public class DrugDealerPatch {
    @SpirePatch(
            clz = DrugDealer.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(DrugDealer event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 2580L);
        }
    }
}
