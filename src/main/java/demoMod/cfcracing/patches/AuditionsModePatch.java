package demoMod.cfcracing.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.NuclearBattery;
import com.megacrit.cardcrawl.relics.Pear;
import com.megacrit.cardcrawl.relics.ToyOrnithopter;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import com.megacrit.cardcrawl.trials.CustomTrial;
import basemod.ReflectionHacks;

import java.util.ArrayList;

public class AuditionsModePatch {
    
    public static final String AUDITIONS_MODE_ID = "cfc:Auditions Mode";
    
    /**
     * Add the Auditions Mode option to the Custom Mode screen
     */
    @SpirePatch(
            clz = CustomModeScreen.class,
            method = "initializeMods"
    )
    public static class AddAuditionsMod {
        @SpirePostfixPatch
        public static void Postfix(CustomModeScreen __instance) {
            ArrayList<CustomMod> modList = ReflectionHacks.getPrivate(__instance, CustomModeScreen.class, "modList");
            // Add as a neutral (blue) non-daily mod
            CustomMod auditionsMod = new CustomMod(AUDITIONS_MODE_ID, "b", false);
            modList.add(auditionsMod);
        }
    }
    
    /**
     * Handle the Auditions Mode logic when starting a custom run
     */
    @SpirePatch(
            clz = CustomModeScreen.class,
            method = "addNonDailyMods"
    )
    public static class HandleAuditionsMode {
        @SpirePostfixPatch
        public static void Postfix(CustomModeScreen __instance, CustomTrial trial, ArrayList<String> modIds) {
            if (modIds.contains(AUDITIONS_MODE_ID)) {
                // Add Pear as starter relic
                trial.addStarterRelic(Pear.ID);
                
                // Add our mod ID to the daily mods list so we can check it during gameplay
                trial.addDailyMod(AUDITIONS_MODE_ID);
            }
        }
    }
    
    /**
     * Give Toy Ornithopter and Nuclear Battery at boss treasure rooms
     */
    @SpirePatch(
            clz = TreasureRoomBoss.class,
            method = "onPlayerEntry"
    )
    public static class OnEnterBossTreasure {
        @SpirePostfixPatch
        public static void Postfix(TreasureRoomBoss __instance) {
            if (!AbstractPlayer.customMods.contains(AUDITIONS_MODE_ID)) {
                return;
            }
            
            // Act 1 boss chest: Add Toy Ornithopter
            if (AbstractDungeon.actNum == 1) {
                if (!AbstractDungeon.player.hasRelic(ToyOrnithopter.ID)) {
                    new ToyOrnithopter().instantObtain();
                }
            }
            
            // Act 2 boss chest: Add Nuclear Battery and orb slot for non-Defect
            if (AbstractDungeon.actNum == 2) {
                if (!AbstractDungeon.player.hasRelic(NuclearBattery.ID)) {
                    // Add orb slot BEFORE obtaining Nuclear Battery if not Defect
                    // Use masterMaxOrbs like PrismaticShard does for permanent effect
                    if (AbstractDungeon.player.chosenClass != AbstractPlayer.PlayerClass.DEFECT) {
                        if (AbstractDungeon.player.masterMaxOrbs == 0) {
                            AbstractDungeon.player.masterMaxOrbs = 1;
                        }
                    }
                    new NuclearBattery().instantObtain();
                }
            }
        }
    }
    
    /**
     * Remove relics from pools to prevent duplicates
     */
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "initializeRelicList"
    )
    public static class RemoveFromRelicPools {
        @SpirePostfixPatch
        public static void Postfix(AbstractDungeon __instance) {
            if (!AbstractPlayer.customMods.contains(AUDITIONS_MODE_ID)) {
                return;
            }
            
            AbstractDungeon.uncommonRelicPool.remove(Pear.ID);
            AbstractDungeon.commonRelicPool.remove(ToyOrnithopter.ID);
            AbstractDungeon.bossRelicPool.remove(NuclearBattery.ID);
            AbstractDungeon.shopRelicPool.remove(Pear.ID);
            AbstractDungeon.shopRelicPool.remove(ToyOrnithopter.ID);
            AbstractDungeon.shopRelicPool.remove(NuclearBattery.ID);
        }
    }
}
