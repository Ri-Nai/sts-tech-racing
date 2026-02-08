package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Orrery;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;

/**
 * 完全重写天体仪逻辑：生成5组独立卡牌奖励，每组使用独立的稀有度掷骰和升级判定
 */
public class OrreryPatch {
    @SpirePatch(
            clz = Orrery.class,
            method = "onEquip"
    )
    public static class PatchOnEquip {
        public static SpireReturn<Object> Prefix(Orrery self) {
            AbstractDungeon.getCurrRoom().rewards.clear();
            for (int i = 0; i < 5; i++) {
                RewardItem ri = new RewardItem(new PotionSlot(0));
                ri.type = RewardItem.RewardType.CARD;
                ri.potion = null;
                ri.cards = OrreryPatch.getRewardCards();
                ri.text = RewardItem.TEXT[2];
                AbstractDungeon.getCurrRoom().rewards.add(ri);
            }
            AbstractDungeon.combatRewardScreen.open(self.DESCRIPTIONS[1]);
            AbstractDungeon.getCurrRoom().rewardPopOutTimer = 0.0F;
            return SpireReturn.Return(null);
        }
    }

    public static ArrayList<AbstractCard> getRewardCards() {
        ArrayList<AbstractCard> retVal = new ArrayList<>();
        AbstractPlayer player = AbstractDungeon.player;
        int numCards = 3;

        for (AbstractRelic r : player.relics) {
            numCards = r.changeNumberOfCardsInReward(numCards);
        }
        if (ModHelper.isModEnabled("Binary")) {
            numCards--;
        }

        for (int i = 0; i < numCards; i++) {
            AbstractCard.CardRarity rarity = AbstractDungeon.rollRarity(AbstractDungeonPatch.CardRarityRngFix.cardRarityRng);
            AbstractCard card = null;
            boolean containsDupe = true;

            while (containsDupe) {
                containsDupe = false;
                if (player.hasRelic("PrismaticShard")) {
                    card = CardLibrary.getAnyColorCard(rarity);
                } else {
                    card = getCard(rarity);
                }
                for (AbstractCard c : retVal) {
                    if (c.cardID.equals(card.cardID)) {
                        containsDupe = true;
                    }
                }
            }

            if (card != null) {
                retVal.add(card);
            }
        }

        ArrayList<AbstractCard> retVal2 = new ArrayList<>();
        for (AbstractCard c : retVal) {
            retVal2.add(c.makeCopy());
        }

        float cardUpgradedChance;
        switch (AbstractDungeon.actNum) {
            case 1:
                cardUpgradedChance = 0.0F;
                break;
            case 2:
                cardUpgradedChance = 0.125F;
                break;
            default:
                cardUpgradedChance = 0.25F;
                break;
        }
        for (AbstractCard c : retVal2) {
            if (c.rarity != AbstractCard.CardRarity.RARE && AbstractDungeon.cardRng.randomBoolean(cardUpgradedChance) && c.canUpgrade()) {
                c.upgrade();
            } else {
                for (AbstractRelic r : player.relics) {
                    r.onPreviewObtainCard(c);
                }
            }
        }

        return retVal2;
    }

    public static AbstractCard getCard(AbstractCard.CardRarity rarity) {
        switch (rarity) {
            case COMMON:
                return getRandomCard(AbstractDungeon.commonCardPool);
            case UNCOMMON:
                return getRandomCard(AbstractDungeon.uncommonCardPool);
            case RARE:
                return getRandomCard(AbstractDungeon.rareCardPool);
            case CURSE:
                return getRandomCard(AbstractDungeon.curseCardPool);
        }
        return null;
    }

    public static AbstractCard getRandomCard(CardGroup cg) {
        return cg.group.get(AbstractDungeon.cardRng.random(cg.group.size() - 1));
    }
}
