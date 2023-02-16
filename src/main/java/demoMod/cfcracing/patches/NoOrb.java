package demoMod.cfcracing.patches;

import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.GiveOrbSlotOnChannel;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

/**
 * 防止basemod在角色生成充能球时给没有充能球栏位的角色增加一个充能球栏位
 */
@SpirePatch(
        clz = GiveOrbSlotOnChannel.class,
        method = "Prefix"
)
public class NoOrb {
    public static SpireReturn<Void> Prefix(AbstractPlayer __instance, AbstractOrb orbToSet) {
        return SpireReturn.Return(null);
    }
}
