package demoMod.cfcracing.patches.rngfix;

import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import demoMod.cfcracing.CatFoodCupRacingMod;
import demoMod.cfcracing.patches.TopPanelPatch;

import java.io.IOException;
import java.util.HashMap;

public class MonsterHelperPatch implements CustomSavable<HashMap<String, Integer>> {
    public static HashMap<String, Integer> encounterMap = new HashMap<>();

    public HashMap<String, Integer> onSave() {
        if (AbstractDungeon.floorNum < 1) {
            encounterMap.clear();
            TopPanelPatch.correct = 0F;
        }
        CatFoodCupRacingMod.saves.setFloat("correctTime", TopPanelPatch.correct);
        try {
            CatFoodCupRacingMod.saves.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return encounterMap;
    }

    public void onLoad(HashMap<String, Integer> map) {
        if (map == null) {
            return;
        }
        encounterMap.clear();
        encounterMap.putAll(map);
        //读碎心减时的部分放这里写了，懒得新建一个存档类了
        if (CatFoodCupRacingMod.saves.has("correctTime")) {
            TopPanelPatch.correct = CatFoodCupRacingMod.saves.getFloat("correctTime");
        }
    }

    @SpirePatch(
            clz = MonsterHelper.class,
            method = "getEncounter"
    )
    public static class PatchGetEncounter {
        public static void Prefix(String key) {
            if (!encounterMap.containsKey(key)) {
                encounterMap.put(key, AbstractDungeon.floorNum + 100);
            } else {
                int tmp = encounterMap.get(key);
                if (tmp % 100 < AbstractDungeon.floorNum) {
                    encounterMap.put(key, AbstractDungeon.floorNum + (tmp / 100 + 1) * 100);
                }
            }
            //System.out.println(key+":"+encounterMap.get(key));
            long salt = encounterMap.get(key) / 100 * AbstractDungeon.actNum * 100L + hash(key);
            AbstractDungeon.miscRng = new Random(Settings.seed + salt);//记得加key盐
            AbstractDungeon.aiRng = new Random(Settings.seed + salt);
            AbstractDungeon.monsterHpRng = new Random(Settings.seed + salt);
            AbstractDungeon.shuffleRng = new Random(Settings.seed + salt);
            AbstractDungeon.cardRandomRng = new Random(Settings.seed + salt);
        }

        static int hash(String s) {
            int ttr = 0;
            for (int i = 0; i < s.length(); i++) {
                ttr *= 101;
                ttr += s.charAt(i);
                ttr %= 10007;
            }
            ttr *= ttr;
            return ttr % 10007;
        }
    }
}
