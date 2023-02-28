package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.GremlinWheelGame;
import com.megacrit.cardcrawl.random.Random;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;

/**
 * 保证相同次数出现的转盘事件结果一致
 */
public class GremlinWheelGamePatch {
    @SpirePatch(
            clz = GremlinWheelGame.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix() {
            int count = 0;
            if (CatFoodCupRacingMod.saves.has("wheelGameCounter")) {
                count = CatFoodCupRacingMod.saves.getInt("wheelGameCounter");
            }
            boolean needIncrementCount = false;
            if (CatFoodCupRacingMod.saves.has("wheelGameLastFloor")) {
                if (AbstractDungeon.floorNum > CatFoodCupRacingMod.saves.getInt("wheelGameLastFloor")) {
                    needIncrementCount = true;
                }
            } else {
                needIncrementCount = true;
            }
            CatFoodCupRacingMod.saves.setInt("wheelGameLastFloor", AbstractDungeon.floorNum);
            if (needIncrementCount) {
                CatFoodCupRacingMod.saves.setInt("wheelGameCounter", ++count);
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            AbstractDungeon.miscRng = new Random(Settings.seed + count);
        }
    }
}
