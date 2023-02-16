package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.ShopScreen;

public class ShopScreenPatch {
    /**
     * 修复送货员的补牌逻辑，使用随机数随机补的牌
     */
    @SpirePatch(
            clz = ShopScreen.class,
            method = "purchaseCard"
    )
    public static class PatchPurchaseCard {
        @SpireInsertPatch(rloc = 33, localvars = {"c"})
        public static void Insert(ShopScreen screen, AbstractCard hoveredCard, @ByRef(type="cards.AbstractCard") Object[] _c) {
            AbstractCard originalCard = (AbstractCard) _c[0];
            AbstractCard c = AbstractDungeon.getCardFromPool(originalCard.rarity, hoveredCard.type, true).makeCopy();
            while (c.color == AbstractCard.CardColor.COLORLESS) {
                c = AbstractDungeon.getCardFromPool(originalCard.rarity, hoveredCard.type, true).makeCopy();
            }
            _c[0] = c;
        }
    }
}
