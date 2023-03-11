package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;

public class ShopRoomPatch {
    @SpirePatch(
            clz = ShopRoom.class,
            method = "onPlayerEntry"
    )
    public static class PatchOnPlayerEntry {
        public static void Prefix(ShopRoom room) {
            int count = 0;
            if (CatFoodCupRacingMod.saves.has("merchantRngCounter")) {
                count = CatFoodCupRacingMod.saves.getInt("merchantRngCounter");
            }
            boolean needIncrementCount = false;
            if (CatFoodCupRacingMod.saves.has("merchantRngLastFloor")) {
                if (AbstractDungeon.floorNum > CatFoodCupRacingMod.saves.getInt("merchantRngLastFloor")) {
                    needIncrementCount = true;
                }
            } else {
                needIncrementCount = true;
            }
            CatFoodCupRacingMod.saves.setInt("merchantRngLastFloor", AbstractDungeon.floorNum);
            if (needIncrementCount) {
                CatFoodCupRacingMod.saves.setInt("merchantRngCounter", ++count);
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            AbstractDungeon.merchantRng = new Random(Settings.seed + count);
        }
    }
}
