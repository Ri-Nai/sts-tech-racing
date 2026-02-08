package demoMod.cfcracing.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import demoMod.cfcracing.CatFoodCupRacingMod;
import demoMod.cfcracing.wheelOptions.WheelOptions;

import java.io.IOException;

public class CharacterSelectScreenPatch {
    private static DropdownMenu wheelOptionsMenu;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("cfc:WheelOptions");
    private static Hitbox resetHb;

    @SpirePatch(
            clz = CharacterSelectScreen.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static void Postfix(CharacterSelectScreen screen) {
            if (CatFoodCupRacingMod.defaultA15Option) {
                screen.isAscensionMode = true;
                screen.ascensionLevel = 15;
            }
            if (resetHb == null) {
                resetHb = new Hitbox(90.0F * Settings.scale, 90.0F * Settings.scale);
                resetHb.move(58.0F * Settings.scale, Settings.HEIGHT * 0.8F - 14.0F * Settings.scale);
            }
            if (wheelOptionsMenu == null) {
                wheelOptionsMenu = new DropdownMenu((menu, index, text) -> {
                    CatFoodCupRacingMod.saves.setInt("appliedWheelOption", index);
                    WheelOptions.PROXY.onSelectThisOption();
                    try {
                        CatFoodCupRacingMod.saves.save();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, uiStrings.TEXT, FontHelper.tipBodyFont, Settings.CREAM_COLOR.cpy());
            }
            wheelOptionsMenu.update();
            resetHb.update();
            if (resetHb.hovered) {
                TipHelper.renderGenericTip(111.0F * Settings.scale, Settings.HEIGHT * 0.9F, CardCrawlGame.languagePack.getUIString("cfc:WheelOptionsTitle").TEXT[1], CardCrawlGame.languagePack.getUIString("cfc:WheelOptionsTitle").TEXT[2]);
            }
            if (resetHb.justHovered) {
                CardCrawlGame.sound.play("UI_HOVER");
            } else if (resetHb.hovered && InputHelper.justClickedLeft) {
                resetHb.clickStarted = true;
                CardCrawlGame.sound.play("UI_CLICK_1");
                CatFoodCupRacingMod.saves.remove("appliedWheelOption");
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SpirePatch(
            clz = CharacterSelectScreen.class,
            method = "render"
    )
    public static class PatchRender {
        public static void Postfix(CharacterSelectScreen screen, SpriteBatch sb) {
            if (wheelOptionsMenu == null) {
                return;
            }
            wheelOptionsMenu.render(sb, 120.0F * Settings.scale, Settings.HEIGHT * 0.8F);
            FontHelper.renderSmartText(sb, FontHelper.cardTitleFont, CardCrawlGame.languagePack.getUIString("cfc:WheelOptionsTitle").TEXT[0], 320.0F * Settings.scale, Settings.HEIGHT * 0.8F, 9999.0F, 0.0F, Settings.GOLD_COLOR);
            if (CatFoodCupRacingMod.saves.has("appliedWheelOption")) {
                int index = CatFoodCupRacingMod.saves.getInt("appliedWheelOption");
                FontHelper.renderSmartText(sb, FontHelper.tipBodyFont, uiStrings.EXTRA_TEXT[index], 320.0F * Settings.scale, Settings.HEIGHT * 0.8F - 32.0F * Settings.scale, 9999.0F, 32.0F * Settings.scale, Color.WHITE);
            }
            float scale = Settings.scale;
            if (resetHb.hovered) {
                scale = Settings.scale * 1.04F;
            }
            sb.draw(ImageMaster.PROFILE_DELETE, resetHb.cX - 50.0F, resetHb.cY - 50.0F, 50.0F, 50.0F, 100.0F, 100.0F, scale, scale, 0.0F, 0, 0, 100, 100, false, false);
            if (resetHb.hovered) {
                sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.4F));
                sb.setBlendFunction(770, 1);
                sb.draw(ImageMaster.PROFILE_DELETE, resetHb.cX - 50.0F, resetHb.cY - 50.0F, 50.0F, 50.0F, 100.0F, 100.0F, scale, scale, 0.0F, 0, 0, 100, 100, false, false);
                sb.setBlendFunction(770, 771);
            }
            resetHb.render(sb);
        }
    }

    @SpirePatch(
            cls = "com.megacrit.cardcrawl.screens.options.DropdownMenu$DropdownRow",
            method = "update"
    )
    public static class DropdownRowPatch {
        public static void Postfix(Object dropdownRow) {
            if (wheelOptionsMenu != null && wheelOptionsMenu.rows.contains(dropdownRow)) {
                try {
                    Class<?> cls = Class.forName("com.megacrit.cardcrawl.screens.options.DropdownMenu$DropdownRow");
                    Hitbox hb = ReflectionHacks.getPrivate(dropdownRow, cls, "hb");
                    int index = ReflectionHacks.getPrivate(dropdownRow, cls, "index");
                    if (hb.hovered) {
                        TipHelper.renderGenericTip(111.0F * Settings.scale, Settings.HEIGHT * 0.9F, uiStrings.TEXT[index], uiStrings.EXTRA_TEXT[index]);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
