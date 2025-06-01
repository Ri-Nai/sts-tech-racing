package demoMod.cfcracing.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import demoMod.cfcracing.CatFoodCupRacingMod;
import demoMod.cfcracing.patches.rngfix.AbstractDungeonPatch;

import java.io.IOException;

public class SaveAndContinuePatch {
    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "deleteSave"
    )
    public static class PatchDeleteSave {
        public static void Postfix(AbstractPlayer p) {
            AbstractDungeon.nextRoom = null;
            AbstractDungeonPatch.CardRarityRngFix.tmpRng = null;
            CatFoodCupRacingMod.saves.remove("appliedWheelOption");
            try {
                CatFoodCupRacingMod.saves.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
