package demoMod.cfcracing.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
import com.megacrit.cardcrawl.vfx.campfire.CampfireRecallEffect;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class ObtainKeyPatch {
    @SpirePatch(clz = ObtainKeyEffect.class, method = SpirePatch.CONSTRUCTOR)
    static public class CONSTRUCTORPatch{
        @SpirePostfixPatch
        static public void Postfix(ObtainKeyEffect __instance, ObtainKeyEffect.KeyColor keyColor){
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
    @SpirePatch(clz = CampfireRecallEffect.class, method = "update")
    static public class updatePatch{
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                int count = 0;
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if ("duration".equals(f.getFieldName())) {
                        if(count == 1){
                            f.replace("{$_=($proceed($$) - 1.1F);}");
                        }
                        count++;
                    }
                }
            };
        }
    }
}
