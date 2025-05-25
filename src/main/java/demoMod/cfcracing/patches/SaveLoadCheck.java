package demoMod.cfcracing.patches;

import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.screens.options.ExitGameButton;
import com.megacrit.cardcrawl.screens.options.SettingsScreen;
import demoMod.cfcracing.CatFoodCupRacingMod;
import demoMod.cfcracing.blights.SLinBattle;
import demoMod.cfcracing.blights.SLoutBattle;

import java.io.IOException;

import static demoMod.cfcracing.CatFoodCupRacingMod.myActions;

public class SaveLoadCheck implements CustomSavable<Integer> {
    public Integer onSave() {
        CatFoodCupRacingMod.saves.setFloat("lastPlayTime", CardCrawlGame.playtime);
        CatFoodCupRacingMod.saves.setString("lastStartTime", Long.toString(System.currentTimeMillis()));
        try {
            CatFoodCupRacingMod.saves.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    public void onLoad(Integer n) {
        myActions.add(new AbstractGameAction() {
            @Override
            public void update() {
                myActions.add(new AbstractGameAction() {
                    @Override
                    public void update() {
                        this.isDone = true;
                        if (AbstractDungeon.getCurrMapNode() == null || AbstractDungeon.getCurrRoom() == null) return;
                        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !(AbstractDungeon.getCurrRoom()).isBattleOver) {
                            if (!AbstractDungeon.player.hasBlight(SLinBattle.ID)) {
                                SLinBattle slinBattle = new SLinBattle();
                                slinBattle.instantObtain(AbstractDungeon.player, AbstractDungeon.player.blights.size(), true);
                            }
                        } else {
                            if (!AbstractDungeon.player.hasBlight(SLoutBattle.ID)) {
                                SLoutBattle sloutBattle = new SLoutBattle();
                                sloutBattle.instantObtain(AbstractDungeon.player, AbstractDungeon.player.blights.size(), true);
                            }
                        }
                    }
                });
                isDone = true;
            }
        });
        CatFoodCupRacingMod.saves.setString("lastStartTime", Long.toString(System.currentTimeMillis()));
    }

    @SpirePatch(clz = BlightHelper.class, method = "getBlight")
    public static class PatchBlights {
        public static SpireReturn<AbstractBlight> Prefix(String id) {
            if (id.equals(SLinBattle.ID)) {
                return SpireReturn.Return(new SLinBattle());
            }
            if (id.equals(SLoutBattle.ID)) {
                return SpireReturn.Return(new SLoutBattle());
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = SettingsScreen.class,
            method = "update"
    )
    public static class PatchUpdate {

        @SpirePostfixPatch
        public static void Postfix(SettingsScreen __instance) {
            if (AbstractDungeon.player != null &&
                    AbstractDungeon.getCurrMapNode() != null &&
                    AbstractDungeon.getCurrRoom() != null) {
                boolean shouldHide = false;
                if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !(AbstractDungeon.getCurrRoom()).isBattleOver && !(AbstractDungeon.getCurrRoom() instanceof EventRoom)) {
                    if (AbstractDungeon.player.hasBlight(SLinBattle.ID)) {
                        shouldHide = true;
                    }
                } else {
                    if (AbstractDungeon.player.hasBlight(SLoutBattle.ID)) {
                        shouldHide = true;
                    }
                }
                if (shouldHide) {
                    __instance.exitPopup.hide();
                }
            }
        }
    }

    @SpirePatch(
            clz = ExitGameButton.class,
            method = "update"
    )
    public static class UpdateBlightFlash {
        @SpireInsertPatch(rloc = 7)
        public static void Insert(ExitGameButton button) {
            if (AbstractDungeon.player != null &&
                    AbstractDungeon.getCurrMapNode() != null &&
                    AbstractDungeon.getCurrRoom() != null) {
                if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !(AbstractDungeon.getCurrRoom()).isBattleOver && !(AbstractDungeon.getCurrRoom() instanceof EventRoom)) {
                    if (AbstractDungeon.player.hasBlight(SLinBattle.ID)) {
                        AbstractDungeon.player.getBlight(SLinBattle.ID).flash();
                    }
                } else {
                    if (AbstractDungeon.player.hasBlight(SLoutBattle.ID)) {
                        AbstractDungeon.player.getBlight(SLoutBattle.ID).flash();
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = Settings.class,
            method = "setFinalActAvailability"
    )
    public static class ResetSLMark {
        public static void Postfix() {
            System.out.println("Reset SL counters...");
            CatFoodCupRacingMod.saves.setInt("slInCombat", 1);
            CatFoodCupRacingMod.saves.setInt("slOutCombat", 1);
            try {
                CatFoodCupRacingMod.saves.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
