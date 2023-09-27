package demoMod.cfcracing.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import demoMod.cfcracing.CatFoodCupRacingMod;

public class CharacterSelectScreenPatch {
    @SpirePatch(
            clz = CharacterSelectScreen.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static void Postfix(CharacterSelectScreen screen) {
            if (CatFoodCupRacingMod.defaultA15Option) {
                screen.isAscensionMode = true;
                screen.ascensionLevel = 15;
            }
        }
    }
}
