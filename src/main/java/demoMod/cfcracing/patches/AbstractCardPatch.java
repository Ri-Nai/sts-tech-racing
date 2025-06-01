package demoMod.cfcracing.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class AbstractCardPatch {
    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeStatEquivalentCopy"
    )
    public static class PatchMakeStatEquivalentCopy {
        @SpireInsertPatch(rloc = 1, localvars = {"card"})
        public static void Insert(AbstractCard card, @ByRef AbstractCard[] _card) {
            _card[0].isEthereal = card.isEthereal;
        }
    }
}
