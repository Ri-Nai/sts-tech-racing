package demoMod.cfcracing.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;

import java.util.ArrayList;

public class CustomModScreenPatch {
    @SpirePatch(
            clz = CustomModeScreen.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(CustomModeScreen screen) {
            screen.isAscensionMode = true;
            screen.ascensionLevel = 15;
            ArrayList<CustomMod> modList = ReflectionHacks.getPrivate(screen, CustomModeScreen.class, "modList");
            for (CustomMod customMod : modList) {
                // 默认勾选：前路已定、终幕、海选模式
                if (customMod.ID.equals("Uncertain Future") || 
                    customMod.ID.equals("The Ending") ||
                    customMod.ID.equals(AuditionsModePatch.AUDITIONS_MODE_ID)) {
                    customMod.selected = true;
                }
            }
        }
    }
}
