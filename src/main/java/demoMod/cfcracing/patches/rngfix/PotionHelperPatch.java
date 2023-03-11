package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.ShopRoom;

public class PotionHelperPatch {
    @SpirePatch(
            clz = PotionHelper.class,
            method = "getRandomPotion",
            paramtypez = {}
    )
    public static class PatchGetRandomPotion {
        public static SpireReturn<AbstractPotion> Prefix() {
            Random rng = AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() instanceof ShopRoom ? AbstractDungeon.merchantRng : AbstractDungeon.potionRng;
            String randomKey = PotionHelper.potions.get(rng.random(PotionHelper.potions.size() - 1));
            return SpireReturn.Return(PotionHelper.getPotion(randomKey));
        }
    }
}
