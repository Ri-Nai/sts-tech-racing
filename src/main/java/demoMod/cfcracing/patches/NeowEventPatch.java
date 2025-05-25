package demoMod.cfcracing.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.neow.NeowEvent;

public class NeowEventPatch {
    @SpirePatch(
            clz = NeowEvent.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    boolean.class
            }
    )
    public static class PatchConstructor {
        public static void Postfix(NeowEvent event) {
            ReflectionHacks.setPrivate(event, NeowEvent.class, "bossCount", 1);
        }
    }
}
