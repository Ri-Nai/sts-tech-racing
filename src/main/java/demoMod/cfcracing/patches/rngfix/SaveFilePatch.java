package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;

public class SaveFilePatch {
    public static final Gson gson = new Gson();

    @SpirePatch(
            clz = SaveFile.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    SaveFile.SaveType.class
            }
    )
    public static class PatchConstructor {
        public static void Postfix(SaveFile saveFile, SaveFile.SaveType saveType) {
            saveFile.monster_hp_seed_count = AbstractDungeon.monsterHpRng.counter;
            saveFile.ai_seed_count = AbstractDungeon.aiRng.counter;
            saveFile.shuffle_seed_count = AbstractDungeon.shuffleRng.counter;
            CatFoodCupRacingMod.saves.setInt("cardRarityRngCounter", AbstractDungeonPatch.CardRarityRngFix.cardRarityRng.counter);
            CatFoodCupRacingMod.saves.setString("shrines", gson.toJson(AbstractDungeon.shrineList));
            try {
                CatFoodCupRacingMod.saves.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
