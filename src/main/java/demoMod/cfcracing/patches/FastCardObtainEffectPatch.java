package demoMod.cfcracing.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import demoMod.cfcracing.CatFoodCupRacingMod;

public class FastCardObtainEffectPatch {
    @SpirePatch(
            clz = FastCardObtainEffect.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 0)
        public static SpireReturn<Void> Insert(FastCardObtainEffect effect) {
            AbstractCard card = ReflectionHacks.getPrivate(effect, FastCardObtainEffect.class, "card");
            if (CatFoodCupRacingMod.isDisabled(card)) {
                effect.isDone = true;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
