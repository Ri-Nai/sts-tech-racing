package demoMod.cfcracing.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import demoMod.cfcracing.wheelOptions.WheelOptions;

public class UseCardActionPatch {
    @SpirePatch(
            clz = UseCardAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractCard.class,
                    AbstractCreature.class
            }
    )
    public static class PatchConstructor {
        @SpireInsertPatch(rloc = 29)
        public static void Insert(UseCardAction action, AbstractCard card, AbstractCreature target) {
            WheelOptions.PROXY.onUseCard(card, action);
        }
    }
}
