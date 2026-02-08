package demoMod.cfcracing.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;

import java.util.ArrayList;

/**
 * 封锁涅奥（Neow）开局第四选项（Boss交换）。
 * 强制 bossCount=1 使第四选项始终出现，但以灰色禁用状态显示。
 * 借鉴自 defect-racing-2。
 */
public class NeowEventPatch {
    @SpirePatch(
            clz = NeowEvent.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {boolean.class}
    )
    public static class PatchConstructor {
        public static void Postfix(NeowEvent event) {
            // 强制 bossCount=1，使第四选项始终生成
            ReflectionHacks.setPrivate(event, NeowEvent.class, "bossCount", 1);
        }
    }

    @SpirePatch(clz = NeowEvent.class, method = "blessing")
    public static class PatchBlessing {
        @SpireInsertPatch(rloc = 16)
        public static SpireReturn<Object> Insert(NeowEvent self) {
            // 获取已生成的奖励列表
            @SuppressWarnings("unchecked")
            ArrayList<NeowReward> rewards = (ArrayList<NeowReward>)
                    ReflectionHacks.getPrivate(self, NeowEvent.class, "rewards");
            // 将第四个选项以禁用状态（灰色）添加到对话选项
            self.roomEventText.addDialogOption(rewards.get(3).optionLabel, true);
            // 跳过原有的第四选项添加逻辑，直接进入选择界面
            ReflectionHacks.setPrivate(self, NeowEvent.class, "screenNum", 3);
            return SpireReturn.Return(null);
        }
    }
}
