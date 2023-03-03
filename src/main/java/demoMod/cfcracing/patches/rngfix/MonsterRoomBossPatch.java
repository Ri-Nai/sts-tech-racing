package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;

import com.megacrit.cardcrawl.random.Random;

public class MonsterRoomBossPatch {
    @SpirePatch(
            clz = MonsterRoomBoss.class,
            method = "onPlayerEntry"
    )
    public static class PatchOnPlayerEntry {
        public static void Postfix(MonsterRoomBoss monsterRoomBoss) {
            CardGroupPatch.PatchGetRandomCard2.bossCardRng = new Random(Settings.seed + AbstractDungeon.floorNum);
        }
    }
}
