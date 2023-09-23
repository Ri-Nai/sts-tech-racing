package demoMod.cfcracing.patches;

import basemod.patches.com.megacrit.cardcrawl.ui.panels.TopPanel.TopPanelPatches;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.DoorUnlockScreen;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.ui.DialogWord;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.vfx.GameSavedEffect;
import com.megacrit.cardcrawl.vfx.SpeechTextEffect;
import demoMod.cfcracing.CatFoodCupRacingMod;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.IOException;

import static java.lang.Math.min;

public class TopPanelPatch {
    public static Float correct = 0.0F;

    @SpirePatch(
            clz = TopPanelPatches.RenderPatch.class,
            method = "Postfix"
    )
    static public class PatchPostfix {
        static boolean tmp = false;

        @SpireInsertPatch(rloc = 5)
        static public void Insert1(TopPanel __instance, SpriteBatch sb) {
            tmp = CardCrawlGame.stopClock;
            CardCrawlGame.stopClock = true;
        }

        @SpireInsertPatch(rloc = 10, localvars = {"clockColor"})
        static public void Insert2(TopPanel __instance, SpriteBatch sb, @ByRef Color[] clockColor) {
            CardCrawlGame.stopClock = tmp;
            if (CardCrawlGame.stopClock) {
                clockColor[0] = Settings.GREEN_TEXT_COLOR;
            } else {
                if (CardCrawlGame.playtime < 800) {
                    clockColor[0] = new Color(1.0F, 0.84F, 0.15F, 1.0F);
                } else if (CardCrawlGame.playtime < 1200) {
                    clockColor[0] = new Color(0.72F, 0.72F, 0.72F, 1.0F);
                } else if (CardCrawlGame.playtime < 1800) {
                    clockColor[0] = new Color(0.82F, 0.54F, 0.28F, 1.0F);
                } else {
                    clockColor[0] = new Color(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (isMethodCalled(m)) {
                        m.replace("{if(" + TopPanelPatch.PatchPostfix.class.getName() + ".check()){$3+=" + TopPanelPatch.PatchPostfix.class.getName() + ".cal();}$_=$proceed($$);}");
                    }
                }

                private boolean isMethodCalled(MethodCall m) {
                    return "renderFontLeftTopAligned".equals(m.getMethodName());
                }
            };
        }

        public static boolean check() {
            return CatFoodCupRacingMod.saves.has("totalTime") && CatFoodCupRacingMod.saves.getFloat("totalTime") > 0.0F && AbstractDungeon.floorNum > 0;
        }

        public static String cal() {
            if (correct > 0.0F) {
                return "-" + CharStat.formatHMSM(correct) + "(" + CharStat.formatHMSM(CatFoodCupRacingMod.saves.getFloat("totalTime")) + ")";
            }
            return "(" + CharStat.formatHMSM(CatFoodCupRacingMod.saves.getFloat("totalTime")) + ")";
        }
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "onFinalBossVictoryLogic"
    )
    static public class PatchOnFinalBossVictoryLogic {
        @SpireInsertPatch(rloc = 12)
        static public void Insert(AbstractMonster monster) {
            if (AbstractDungeon.actNum < 4)
                CatFoodCupRacingMod.saves.setFloat("totalTime", CardCrawlGame.playtime);
            else if (CatFoodCupRacingMod.saves.has("totalTime")) {
                CardCrawlGame.playtime = min(CardCrawlGame.playtime, CatFoodCupRacingMod.saves.getFloat("totalTime"));
            }
        }
    }

    @SpirePatch(
            clz = DoorUnlockScreen.class,
            method = "open"
    )
    static public class PatchOpen {
        static public void Prefix(DoorUnlockScreen doorUnlockScreen, boolean eventVersion) {
            if (eventVersion) {
                if (AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.IRONCLAD) {
                    correct = 360.0F;
                } else if (AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.THE_SILENT) {
                    correct = 420.0F;
                } else if (AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.DEFECT) {
                    correct = 450.0F;
                } else {
                    correct = 300.0F;
                }
                CatFoodCupRacingMod.saves.setFloat("correctTime", correct);
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "update"
    )
    static public class PatchUpdate {
        static public void Prefix(AbstractDungeon abstractDungeon) {
            if (!CardCrawlGame.stopClock) {
                if (correct > 0.5F) {
                    CardCrawlGame.playtime -= Gdx.graphics.getDeltaTime() * 60;
                    correct -= Gdx.graphics.getDeltaTime() * 60;
                } else if (correct > 0.0F) {
                    CardCrawlGame.playtime -= correct;
                    correct = 0.0F;
                }
            }
        }
    }

    @SpirePatch(
            clz = GameSavedEffect.class,
            method = "update"
    )
    static public class PatchUpdate2 {
        static public SpireReturn<Void> Prefix(GameSavedEffect effect) {
            effect.isDone = true;
            AbstractDungeon.topLevelEffects.add(new SpeechTextEffect(1600.0F * Settings.scale, Settings.HEIGHT - 74.0F * Settings.scale, 2.0F, GameSavedEffect.TEXT[0], DialogWord.AppearEffect.FADE_IN));
            return SpireReturn.Return(null);
        }
    }
}
