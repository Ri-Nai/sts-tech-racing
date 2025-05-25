package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;

public class MonsterRoomElitePatch {
    @SpirePatch(
            clz = MonsterRoomElite.class,
            method = "onPlayerEntry"
    )
    public static class PatchOnPlayerEntry {
        public static void Postfix(MonsterRoomElite monsterRoomElite) {
            int count = 0;
            if (CatFoodCupRacingMod.saves.has("eliteCount")) {
                count = CatFoodCupRacingMod.saves.getInt("eliteCount");
            }
            boolean needIncrementCount = false;
            if (CatFoodCupRacingMod.saves.has("eliteLastFloor")) {
                if (AbstractDungeon.floorNum > CatFoodCupRacingMod.saves.getInt("eliteLastFloor")) {
                    needIncrementCount = true;
                }
            } else {
                needIncrementCount = true;
            }
            CatFoodCupRacingMod.saves.setInt("eliteLastFloor", AbstractDungeon.floorNum);
            if (needIncrementCount) {
                CatFoodCupRacingMod.saves.setInt("eliteCount", ++count);
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            CardGroupPatch.PatchGetRandomCard2.eliteCardRng = new Random(Settings.seed + CatFoodCupRacingMod.saves.getInt("eliteCount"));
        }
    }
}
