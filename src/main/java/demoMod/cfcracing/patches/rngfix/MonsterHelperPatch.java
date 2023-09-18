package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;

public class MonsterHelperPatch {
    @SpirePatch(
            clz = MonsterHelper.class,
            method = "getEncounter"
    )
    public static class PatchGetEncounter {
        public static int encounterCounter = 0;

        public static void Prefix(String key) {
            if (CatFoodCupRacingMod.saves.has("eventEnemyCounter") && AbstractDungeon.getCurrRoom() instanceof EventRoom) {
                encounterCounter = CatFoodCupRacingMod.saves.getInt("eventEnemyCounter");
                encounterCounter++;
                CatFoodCupRacingMod.saves.setInt("eventEnemyCounter", encounterCounter);
                try {
                    CatFoodCupRacingMod.saves.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
                encounterCounter = 1;
            }
            AbstractDungeon.miscRng = new Random(Settings.seed + encounterCounter * AbstractDungeon.actNum * 100L);
            AbstractDungeon.aiRng = new Random(Settings.seed + encounterCounter * AbstractDungeon.actNum * 100L);
        }
    }
}
