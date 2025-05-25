package demoMod.cfcracing.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import demoMod.cfcracing.patches.rngfix.AbstractDungeonPatch;

public class SaveAndContinuePatch {
    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "deleteSave"
    )
    public static class PatchDeleteSave {
        public static void Postfix(AbstractPlayer p) {
            AbstractDungeon.nextRoom = null;
            AbstractDungeonPatch.CardRarityRngFix.tmpRng = null;
        }
    }
}
