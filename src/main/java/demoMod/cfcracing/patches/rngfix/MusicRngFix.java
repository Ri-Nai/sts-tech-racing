package demoMod.cfcracing.patches.rngfix;

import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.random.Random;

/**
 * 修复音乐选择消耗随机数导致的世界线变动
 */
public class MusicRngFix {
    static Random musicRng = new Random(0L);

    @SpirePatch(clz = MainMusic.class, method = "getSong")
    public static class GetSongPatch {
        public static SpireReturn<Music> Prefix(MainMusic self, String key) {
            switch (key) {
                case "Exordium":
                    switch (MusicRngFix.musicRng.random(1)) {
                        case 0:
                            return SpireReturn.Return(MainMusic.newMusic("audio/music/STS_Level1_NewMix_v1.ogg"));
                    }
                    return SpireReturn.Return(MainMusic.newMusic("audio/music/STS_Level1-2_v2.ogg"));

                case "TheCity":
                    switch (MusicRngFix.musicRng.random(1)) {
                        case 0:
                            return SpireReturn.Return(MainMusic.newMusic("audio/music/STS_Level2_NewMix_v1.ogg"));
                    }
                    return SpireReturn.Return(MainMusic.newMusic("audio/music/STS_Level2-2_v2.ogg"));

                case "TheBeyond":
                    switch (MusicRngFix.musicRng.random(1)) {
                        case 0:
                            return SpireReturn.Return(MainMusic.newMusic("audio/music/STS_Level3_v2.ogg"));
                    }
                    return SpireReturn.Return(MainMusic.newMusic("audio/music/STS_Level3-2_v2.ogg"));

                case "TheEnding":
                    return SpireReturn.Return(MainMusic.newMusic("audio/music/STS_Act4_BGM_v2.ogg"));
                case "MENU":
                    return SpireReturn.Return(MainMusic.newMusic("audio/music/STS_MenuTheme_NewMix_v1.ogg"));
            }
            System.out.println("NO SUCH MAIN BGM (playing level_1 instead): " + key);
            return SpireReturn.Return(MainMusic.newMusic("audio/music/STS_Level1_NewMix_v1.ogg"));
        }
    }
}
