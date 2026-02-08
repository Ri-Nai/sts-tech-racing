package demoMod.cfcracing.patches.rngfix;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.Nloth;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class NlothPatch {
    @SpirePatch(
            clz = Nloth.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Prefix(Nloth event) {
            AbstractDungeon.miscRng = new Random(Settings.seed + 608L);
        }

        /**
         * 防止棱镜被 N'loth 偷走导致世界线变动
         */
        @SpireInsertPatch(rloc = 5, localvars = {"relics"})
        public static void Insert(Nloth event, @ByRef ArrayList<AbstractRelic>[] _relics) {
            ArrayList<AbstractRelic> relics = _relics[0];
            relics.removeIf(relic -> relic.relicId.equals("PrismaticShard"));
        }
    }
}
