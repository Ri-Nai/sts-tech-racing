package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;

public class TreasureRoomPatch {
    @SpirePatch(
            clz = TreasureRoom.class,
            method = "onPlayerEntry"
    )
    public static class PatchOnPlayerEntry {
        public static void Postfix(TreasureRoom room) {
            int count = 0;
            if (CatFoodCupRacingMod.saves.has("treasureCounter")) {
                count = CatFoodCupRacingMod.saves.getInt("treasureCounter");
            }
            boolean needIncrementCount = false;
            if (CatFoodCupRacingMod.saves.has("treasureLastFloor")) {
                if (AbstractDungeon.floorNum > CatFoodCupRacingMod.saves.getInt("treasureLastFloor")) {
                    needIncrementCount = true;
                }
            } else {
                needIncrementCount = true;
            }
            CatFoodCupRacingMod.saves.setInt("treasureLastFloor", AbstractDungeon.floorNum);
            if (needIncrementCount) {
                CatFoodCupRacingMod.saves.setInt("treasureCounter", ++count);
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            AbstractDungeon.miscRng = new Random(Settings.seed + count + 777L);
        }
    }
}
