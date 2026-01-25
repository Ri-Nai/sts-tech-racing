package demoMod.cfcracing.patches;

import basemod.ReflectionHacks;
import basemod.patches.com.megacrit.cardcrawl.ui.panels.TopPanel.TopPanelPatches;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.DoorUnlockScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;
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
                if (AbstractDungeon.actNum < 4) {
                    if (CardCrawlGame.playtime < 200.0F) {
                        clockColor[0] = new Color(1.0F, 0.2F, 0.2F, 1.0F);
                    } else if (CardCrawlGame.playtime < 400.0F) {
                        clockColor[0] = new Color(0.82F, 0.54F, 0.28F, 1.0F);
                    } else {
                        clockColor[0] = Settings.GREEN_TEXT_COLOR;
                    }
                } else {
                    clockColor[0] = Settings.GREEN_TEXT_COLOR;
                }
            } else if (CardCrawlGame.playtime < 800.0F) {
                clockColor[0] = new Color(1.0F, 0.84F, 0.15F, 1.0F);
            } else if (CardCrawlGame.playtime < 1200.0F) {
                clockColor[0] = new Color(0.72F, 0.72F, 0.72F, 1.0F);
            } else if (CardCrawlGame.playtime < 1800.0F) {
                clockColor[0] = new Color(0.82F, 0.54F, 0.28F, 1.0F);
            } else {
                clockColor[0] = new Color(1.0F, 1.0F, 1.0F, 1.0F);
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
            float total = CatFoodCupRacingMod.saves.getFloat("totalTime");
            if (correct > 0.0F) {
                return "-" + CharStat.formatHMSM(correct) + "(" + CharStat.formatHMSM(total) + ")";
            }
            return "(" + CharStat.formatHMSM(total) + ")";
        }
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "onFinalBossVictoryLogic"
    )
    static public class PatchOnFinalBossVictoryLogic {
        @SpireInsertPatch(rloc = 12)
        static public void Insert(AbstractMonster monster) {
            if ((AbstractDungeon.ascensionLevel == 20 && AbstractDungeon.bossList.size() >= 2 && AbstractDungeon.actNum == 3) || Settings.isEndless) {
                return;
            }
            CardCrawlGame.stopClock = true;
            if (AbstractDungeon.actNum < 4) {
                CatFoodCupRacingMod.saves.setFloat("totalTime", CardCrawlGame.playtime);
                if (Settings.hasRubyKey && Settings.hasEmeraldKey && Settings.hasSapphireKey) {
                    CardCrawlGame.playtime = 900.0F;
                }
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (!Settings.hasEmeraldKey || !Settings.hasSapphireKey || !Settings.hasRubyKey) {
                    CardCrawlGame.playtime = Math.min(CardCrawlGame.playtime, CatFoodCupRacingMod.saves.getFloat("totalTime"));
                }
            } else if (CatFoodCupRacingMod.saves.has("totalTime")) {
                float totalTime = CatFoodCupRacingMod.saves.getFloat("totalTime");
                float reduced = totalTime - 300.0F;
                CardCrawlGame.playtime = reduced;
                CatFoodCupRacingMod.saves.setFloat("totalTime", reduced);
                TopPanelPatch.PatchUpdate.defeatedHeart = true;
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
                correct = 0.0F;
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "update"
    )
    static public class PatchUpdate {
        public static boolean defeatedHeart = false;

        static public void Prefix(AbstractDungeon abstractDungeon) {
            if (!CardCrawlGame.stopClock) {
                if (correct > 1.5F) {
                    CardCrawlGame.playtime -= Gdx.graphics.getDeltaTime() * 60.0F;
                    correct -= Gdx.graphics.getDeltaTime() * 60.0F;
                } else if (correct > 0.0F) {
                    CardCrawlGame.playtime -= correct;
                    correct = 0.0F;
                    CatFoodCupRacingMod.saves.setFloat("correctTime", 0.0F);
                    try {
                        CatFoodCupRacingMod.saves.save();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public static void Postfix(AbstractDungeon dungeon) {
            if (!CardCrawlGame.stopClock && CatFoodCupRacingMod.saves.has("lastStartTime")) {
                double lastStartTime = Long.parseLong(CatFoodCupRacingMod.saves.getString("lastStartTime")) / 1000.0;
                double currentTime = System.currentTimeMillis() / 1000.0;
                CardCrawlGame.playtime = CatFoodCupRacingMod.saves.getFloat("lastPlayTime") + (float) (currentTime - lastStartTime);
            }
            if (CardCrawlGame.stopClock && AbstractDungeon.actNum == 4 && CardCrawlGame.playtime > 0.0F && !defeatedHeart && !AbstractDungeon.player.isDead) {
                CardCrawlGame.playtime -= Gdx.graphics.getDeltaTime();
                if (CardCrawlGame.playtime <= 0.0F) {
                    CardCrawlGame.playtime = 0.0F;
                    AbstractDungeon.closeCurrentScreen();
                    AbstractDungeon.player.isDead = true;
                    AbstractDungeon.deathScreen = new DeathScreen(AbstractDungeon.getMonsters());
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
    @SpirePatch(
            clz = SaveHelper.class,
            method = "saveIfAppropriate"
    )
    static public class PatchSaveIfAppropriate{//强制同步sfpc和chances
        static public void Prefix(SaveFile.SaveType saveType){
            if(AbstractDungeon.nextRoom == null) return;
            if(AbstractDungeon.nextRoom.room == null) return;
            if(SaveHelper.shouldSave() && saveType == SaveFile.SaveType.ENTER_ROOM){
                if(AbstractDungeon.nextRoom.room instanceof EventRoom) return;//进入事件房间时不这么干，防止事件战斗后sl累积概率
                ReflectionHacks.setPrivateStatic(EventHelper.class, "saveFilePreviousChances", null);
            }
        }
    }


    @SpirePatch(clz = DeathScreen.class,method = "<ctor>")
    static public class PatchCtor{
        static public void Prefix(DeathScreen screen){
            CardCrawlGame.stopClock = true;
        }
    }
    @SpirePatch(clz = VictoryScreen.class,method = "<ctor>")
    static public class PatchCtor2{
        static public void Prefix(VictoryScreen screen){
            CardCrawlGame.stopClock = true;
        }
    }

    @SpirePatch(
            clz = TopPanel.class,
            method = "render"
    )
    static public class PatchRender{
        @SpireInsertPatch(rloc = 6)
        static public void Insert(TopPanel panel,SpriteBatch sb){
            for(ModInfo info: Loader.MODINFOS){
                if(info.ID.equals("cfc-racing")){
                    FontHelper.renderFontRightTopAligned(sb, FontHelper.cardDescFont_N,

                            "cfc-racing " + info.ModVersion.toString(), Settings.WIDTH - 16.0F * Settings.scale, Settings.HEIGHT - (Settings.isMobile ? (164.0F * Settings.scale) : (128.0F * Settings.scale)) - 24.0F * Settings.scale, Settings.QUARTER_TRANSPARENT_WHITE_COLOR);
                break;
                }
            }

        }
    }

    @SpirePatch(clz = CharStat.class,method = "formatHMSM",paramtypez = {float.class})
    static public class PatchFormatHMSM{
        static public SpireReturn<String> Prefix(float t){
            String res = "";
            if(t < 0){
                t=-t;
                res="-";
            }
            long duration = (long)t;
            int ms = (int)((t - duration)*1000);
            int seconds = (int)(duration % 60L);
            duration /= 60L;
            int minutes = (int)(duration % 60L);
            int hours = (int)t / 3600;
            if (hours > 0) {
                res += String.format(CharStat.TEXT[24], hours, minutes, seconds);
            } else {
                res += String.format(CharStat.TEXT[25], minutes, seconds);
            }
            res += String.format(".%03d",ms);
            return SpireReturn.Return(res);
        }
    }





}
