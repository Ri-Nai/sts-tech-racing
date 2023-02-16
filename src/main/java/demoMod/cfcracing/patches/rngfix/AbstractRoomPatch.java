package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class AbstractRoomPatch {
    /**
     * 这段的作用是防止打怪掉的钱占用的随机数影响宝箱中开出的遗物
     * 每日挑战模式中打怪掉的钱数量是固定的，不占用随机数
     */
    @SpirePatch(
            clz = AbstractRoom.class,
            method = "update"
    )
    public static class PatchUpdate {
        private static boolean isDailyRun = false;

        public static void Prefix(AbstractRoom room) {
            isDailyRun = Settings.isDailyRun;
            Settings.isDailyRun = true;
        }

        public static void Postfix(AbstractRoom room) {
            Settings.isDailyRun = isDailyRun;
        }
    }
}
