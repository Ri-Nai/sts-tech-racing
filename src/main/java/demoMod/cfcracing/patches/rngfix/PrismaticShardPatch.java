package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import demoMod.cfcracing.CatFoodCupRacingMod;

import java.io.IOException;

public class PrismaticShardPatch {
    @SpirePatch(
            clz = PrismaticShard.class,
            method = "onEquip"
    )
    public static class PatchOnEquip {
        public static void Postfix(PrismaticShard relic) {
            CatFoodCupRacingMod.saves.setInt("cardRngCounter", 2580);
            AbstractDungeon.cardRng = new Random(Settings.seed, 2580);
            AbstractDungeonPatch.CardRarityRngFix.cardRarityRng = new Random(Settings.seed, 10000);
            CatFoodCupRacingMod.saves.setInt("cardRarityRngCounter", 10000);
            AbstractDungeonPatch.CardRarityRngFix.cardRarityEliteRng = new Random(Settings.seed, 10000);
            CatFoodCupRacingMod.saves.setInt("cardRarityEliteRngCounter", 10000);
            CatFoodCupRacingMod.saves.setInt("eliteCount", 0);
            CardGroupPatch.PatchGetRandomCard2.eliteCardRng = new Random(Settings.seed + CatFoodCupRacingMod.saves.getInt("eliteCount"));
            AbstractDungeon.cardBlizzRandomizer = 0;
            try {
                CatFoodCupRacingMod.saves.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
