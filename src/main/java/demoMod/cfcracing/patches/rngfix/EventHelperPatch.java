package demoMod.cfcracing.patches.rngfix;

import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.megacrit.cardcrawl.random.Random;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;
import java.util.HashMap;

public class EventHelperPatch{//基于楼层数强制更改返回的房间结果。可能对于事件战斗后sl变动的问题有效——但是很可能仍然不解决额外eventRng调用导致的后续事件错位问题。乐观来说，至少解决了最容易出问题的部分。
    @SpirePatch(clz = EventHelper.class,method = "roll",paramtypez = {Random.class})
    static public class PatchRoll{
        static public EventHelper.RoomResult Postfix(EventHelper.RoomResult result, Random eventRng){
            if(CatFoodCupRacingMod.saves.has("EventRngCountLast") && CatFoodCupRacingMod.saves.getInt("EventRngCountLast") == AbstractDungeon.floorNum){
                return EventHelper.RoomResult.valueOf(CatFoodCupRacingMod.saves.getString("EventResultLast"));
            }
            CatFoodCupRacingMod.saves.setInt("EventRngCountLast",AbstractDungeon.floorNum);
            CatFoodCupRacingMod.saves.setString("EventResultLast", String.valueOf(result));
            try {
                CatFoodCupRacingMod.saves.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return result;
        }
    }
}
