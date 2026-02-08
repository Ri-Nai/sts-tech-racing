package demoMod.cfcracing.patches.rngfix;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PandorasBox;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

/**
 * 修复潘多拉魔盒的随机卡牌生成逻辑，使用 ReturnTrulyRandomCard 替代原版实现
 */
public class PandorasBoxPatch {
    @SpirePatch(clz = PandorasBox.class, method = "onEquip")
    public static class OnEquipPatch {
        @SpireInsertPatch(rloc = 11)
        public static SpireReturn<Object> Insert(PandorasBox self) {
            int cnt = (int) ReflectionHacks.getPrivate(self, PandorasBox.class, "count");
            if (cnt <= 0) {
                return SpireReturn.Return(null);
            }
            CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (int i = 0; i < cnt; i++) {
                AbstractCard card = AbstractDungeonPatch.PatchReturnTrulyRandomCardFromAvailable.ReturnTrulyRandomCard().makeCopy();
                UnlockTracker.markCardAsSeen(card.cardID);
                card.isSeen = true;
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    r.onPreviewObtainCard(card);
                }
                group.addToBottom(card);
            }
            AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, self.DESCRIPTIONS[1]);
            return SpireReturn.Return(null);
        }
    }
}
