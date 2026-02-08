package MapMarks;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.options.ExitGameButton;
import java.util.ArrayList;

public class MapMarksPatches
{
   @SpirePatch(clz = ExitGameButton.class, method = "update")
   public static class SaveOnExitPatch
   {
     @SpirePrefixPatch
     public static void Prefix(ExitGameButton param1ExitGameButton) {
       Hitbox hitbox = (Hitbox)ReflectionHacks.getPrivate(param1ExitGameButton, ExitGameButton.class, "hb");

       if (hitbox != null && hitbox.hovered && InputHelper.justClickedLeft &&
         CardCrawlGame.isInARun()) {
         System.out.println("MapMarks: [保存并返回] - 正在保存 Act " + AbstractDungeon.actNum + " 的数据");

         MapTileManager.updateAllTracked();

         ArrayList<String> arrayList = MapTileManager.getCurrentVisualHighlights();

         MapMarksSaver.commitActData(AbstractDungeon.actNum, arrayList);

         MapMarksStorage.save();
       }
     }
   }
}
