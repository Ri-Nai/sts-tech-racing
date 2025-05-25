package demoMod.cfcracing.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
import com.megacrit.cardcrawl.vfx.campfire.CampfireRecallEffect;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class FixObtainKeyEffect {
    public static class ObtainKeyEffectPatch {
        @SpirePatch(
                clz = ObtainKeyEffect.class,
                method = SpirePatch.CONSTRUCTOR
        )
        public static class PatchConstructor {
            public static void Postfix(ObtainKeyEffect effect, ObtainKeyEffect.KeyColor keyColor) {
                switch (keyColor) {
                    case RED:
                        Settings.hasRubyKey = true;
                        break;
                    case GREEN:
                        Settings.hasEmeraldKey = true;
                        break;
                    case BLUE:
                        Settings.hasSapphireKey = true;
                        break;
                }
            }
        }
    }

    public static class CampfireRecallEffectPatch {
        @SpirePatch(
                clz = CampfireRecallEffect.class,
                method = "update"
        )
        public static class PatchUpdate {
            @SpireInstrumentPatch
            public static ExprEditor Instrument() {
                return new ExprEditor() {
                    int count = 0;

                    public void edit(FieldAccess f) throws CannotCompileException {
                        if ("duration".equals(f.getFieldName())) {
                            if (count == 1) {
                                f.replace("{$_=($proceed($$) - 1.1F);}");
                            }
                            count++;
                        }
                    }
                };
            }
        }
    }
}
