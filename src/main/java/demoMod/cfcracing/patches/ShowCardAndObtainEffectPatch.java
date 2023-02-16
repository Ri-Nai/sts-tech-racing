package demoMod.cfcracing.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import demoMod.cfcracing.CatFoodCupRacingMod;

public class ShowCardAndObtainEffectPatch {
    @SpirePatch(
            clz = ShowCardAndObtainEffect.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 1)
        public static SpireReturn<Void> Insert(ShowCardAndObtainEffect effect) {
            float duration = ReflectionHacks.getPrivate(effect, AbstractGameEffect.class, "duration");
            if (duration <= 0.0F) {
                AbstractCard card = ReflectionHacks.getPrivate(effect, ShowCardAndObtainEffect.class, "card");
                if (CatFoodCupRacingMod.isDisabled(card)) {
                    effect.isDone = true;
                    return SpireReturn.Return(null);
                }
            }
            return SpireReturn.Continue();
        }
    }
}
