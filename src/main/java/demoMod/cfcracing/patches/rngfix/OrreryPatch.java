package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Orrery;

public class OrreryPatch {
    @SpirePatch(
            clz = Orrery.class,
            method = "onEquip"
    )
    public static class PatchOnEquip {
        public static void Prefix(Orrery relic) {
            AbstractDungeon.getCurrRoom().addCardToRewards();
        }
    }
}
