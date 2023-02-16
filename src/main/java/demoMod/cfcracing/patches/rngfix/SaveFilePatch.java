package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

public class SaveFilePatch {
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
            saveFile.card_random_seed_count = AbstractDungeon.cardRandomRng.counter;
        }
    }
}
