package demoMod.cfcracing.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 修复发现之类的印牌受帧率影响的bug
 */
@SuppressWarnings("unchecked")
public class DiscoveryActionPatch {
    @SpirePatch(
            clz = DiscoveryAction.class,
            method = SpirePatch.CLASS
    )
    public static class AddFields {
        public static SpireField<List<AbstractCard>> generatedCards = new SpireField<>(ArrayList::new);
    }

    @SpirePatch(
            clz = DiscoveryAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {}
    )
    @SpirePatch(
            clz = DiscoveryAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractCard.CardType.class,
                    int.class
            }
    )
    @SpirePatch(
            clz = DiscoveryAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    boolean.class,
                    int.class
            }
    )
    public static class PatchConstructor {
        public static void Postfix(DiscoveryAction action) {
            try {
                if (ReflectionHacks.getPrivate(action, DiscoveryAction.class, "returnColorless")) {
                    AddFields.generatedCards.set(action, (List<AbstractCard>) ReflectionHacks.getCachedMethod(DiscoveryAction.class, "generateColorlessCardChoices").invoke(action));
                } else {
                    AddFields.generatedCards.set(action, (List<AbstractCard>) ReflectionHacks.getCachedMethod(DiscoveryAction.class, "generateCardChoices", AbstractCard.CardType.class).invoke(action, (AbstractCard.CardType) ReflectionHacks.getPrivate(action, DiscoveryAction.class, "cardType")));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SpirePatch(
            clz = DiscoveryAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static SpireReturn<Void> Prefix(DiscoveryAction action) {
            float duration = ReflectionHacks.getPrivate(action, AbstractGameAction.class, "duration");
            AbstractCard.CardType cardType = ReflectionHacks.getPrivate(action, DiscoveryAction.class, "cardType");
            try {
                if (duration == Settings.ACTION_DUR_FAST) {
                    AbstractDungeon.cardRewardScreen.customCombatOpen((ArrayList<AbstractCard>) AddFields.generatedCards.get(action), CardRewardScreen.TEXT[1], cardType != null);
                    ReflectionHacks.getCachedMethod(AbstractGameAction.class, "tickDuration").invoke(action);
                } else {
                    if (!(boolean)ReflectionHacks.getPrivate(action, DiscoveryAction.class, "retrieveCard")) {
                        if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                            AbstractCard disCard = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
                            AbstractCard disCard2 = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
                            if (AbstractDungeon.player.hasPower("MasterRealityPower")) {
                                disCard.upgrade();
                                disCard2.upgrade();
                            }

                            disCard.setCostForTurn(0);
                            disCard2.setCostForTurn(0);
                            disCard.current_x = -1000.0F * Settings.xScale;
                            disCard2.current_x = -1000.0F * Settings.xScale + AbstractCard.IMG_HEIGHT_S;
                            if (action.amount == 1) {
                                if (AbstractDungeon.player.hand.size() < 10) {
                                    AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(disCard, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                                } else {
                                    AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(disCard, (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                                }

                                disCard2 = null;
                            } else if (AbstractDungeon.player.hand.size() + action.amount <= 10) {
                                AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(disCard, (float) Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                                AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(disCard2, (float) Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                            } else if (AbstractDungeon.player.hand.size() == 9) {
                                AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(disCard, (float) Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                                AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(disCard2, (float) Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                            } else {
                                AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(disCard, (float) Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                                AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(disCard2, (float) Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                            }

                            AbstractDungeon.cardRewardScreen.discoveryCard = null;
                        }

                        ReflectionHacks.setPrivate(action, DiscoveryAction.class, "retrieveCard", true);
                    }
                    ReflectionHacks.getCachedMethod(AbstractGameAction.class, "tickDuration").invoke(action);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return SpireReturn.Return(null);
        }
    }
}
