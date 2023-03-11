package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.exordium.DeadAdventurer;
import com.megacrit.cardcrawl.random.Random;

public class DeadAdventurerPatch {
    @SpirePatch(
            clz = DeadAdventurer.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(DeadAdventurer event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 23336970L);
        }
    }
}
