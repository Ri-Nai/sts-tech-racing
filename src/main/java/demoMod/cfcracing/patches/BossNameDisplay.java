package demoMod.cfcracing.patches;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

/**
 * 在地图屏幕顶部显示当前 Boss 名称
 * 使用呼吸式明暗变化的火砖色文字 + 半透明黑色背景
 */
public class BossNameDisplay {
    private static String getBossName() {
        if (AbstractDungeon.bossKey == null) return "";
        if (AbstractDungeon.bossKey.equals(currentID)) {
            return bossName;
        }
        currentID = AbstractDungeon.bossKey;
        String encName = MonsterHelper.getEncounterName(currentID);
        String mName = BaseMod.getMonsterName(encName);
        if (mName != null && !mName.isEmpty()) {
            bossName = mName;
        } else if (encName != null && !encName.isEmpty()) {
            bossName = encName;
        } else {
            bossName = currentID;
        }
        return bossName;
    }

    private static Color oscillatingColor = Color.FIREBRICK.cpy();
    private static float oscillatingTimer = 0.0F;
    private static float oscillatingFader = 0.0F;
    private static String currentID;
    private static String bossName;

    @SpirePatch(clz = DungeonMapScreen.class, method = "render")
    public static class RenderBossName {
        public static void Postfix(DungeonMapScreen __instance, SpriteBatch sb) {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP) {
                String name = BossNameDisplay.getBossName();
                if (name != null && !name.isEmpty()) {
                    BossNameDisplay.oscillatingFader += Gdx.graphics.getRawDeltaTime();
                    if (BossNameDisplay.oscillatingFader > 1.0F) {
                        BossNameDisplay.oscillatingFader = 1.0F;
                        BossNameDisplay.oscillatingTimer += Gdx.graphics.getRawDeltaTime() * 5.0F;
                    }

                    BossNameDisplay.oscillatingColor.a = (0.9F + MathUtils.cos(BossNameDisplay.oscillatingTimer) / 10.0F) * BossNameDisplay.oscillatingFader;

                    FontHelper.layout.setText(FontHelper.charTitleFont, name);
                    sb.setColor(Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR);
                    float y = Settings.HEIGHT - 180.0F * Settings.scale;
                    sb.draw(ImageMaster.WHITE_SQUARE_IMG,
                            Settings.WIDTH / 2.0F - FontHelper.layout.width / 2.0F - 15.0F * Settings.scale,
                            y - 30.0F * Settings.scale,
                            FontHelper.layout.width + 30.0F * Settings.scale,
                            60.0F * Settings.scale);
                    FontHelper.renderFontCentered(sb, FontHelper.charTitleFont, name, Settings.WIDTH / 2.0F, y, BossNameDisplay.oscillatingColor);
                }
            }
        }
    }
}
