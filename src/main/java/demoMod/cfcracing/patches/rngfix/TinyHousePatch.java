package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.TinyHouse;

public class TinyHousePatch {
    @SpirePatch(
            clz = TinyHouse.class,
            method = "onEquip"
    )
    public static class PatchOnEquip {
        public static void Prefix(TinyHouse tinyHouse) {
            AbstractDungeon.getCurrRoom().addCardToRewards();
        }
    }
}
