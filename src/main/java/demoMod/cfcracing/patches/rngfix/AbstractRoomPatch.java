package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.*;
import javassist.CtBehavior;

import java.util.ArrayList;

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

        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractRoom room) {
            if ((room.event == null || !room.event.noCardsInRewards) && !(room instanceof TreasureRoom) && !(room instanceof RestRoom)) {
                RewardItem cardReward;
                if (ModHelper.isModEnabled("Vintage") && AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
                    if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite || AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
                        cardReward = new RewardItem();
                        if (cardReward.cards.size() > 0) {
                            room.rewards.add(cardReward);
                        }
                    }
                } else {
                    cardReward = new RewardItem();
                    if (cardReward.cards.size() > 0) {
                        room.rewards.add(cardReward);
                    }

                    if (AbstractDungeon.getCurrRoom() instanceof MonsterRoom && AbstractDungeon.player.hasRelic("Prayer Wheel") && !(AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) && !(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss)) {
                        cardReward = new RewardItem();
                        if (cardReward.cards.size() > 0) {
                            room.rewards.add(cardReward);
                        }
                    }
                }
            }
        }

        private static final class Locator extends SpireInsertLocator {

            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(AbstractRoom.class, "rewardAllowed");
                int[] result = LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), matcher);
                result[0] = result[0] + 1;
                return result;
            }
        }
    }
}
