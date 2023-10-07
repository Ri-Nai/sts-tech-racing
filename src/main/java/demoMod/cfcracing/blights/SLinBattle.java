package demoMod.cfcracing.blights;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.BlightStrings;
import demoMod.cfcracing.CatFoodCupRacingMod;
import demoMod.cfcracing.patches.SaveLoadCheck;

public class SLinBattle extends AbstractBlight {
    public static final String ID = "cfc:SLinBattle";

    public static final String NAME;

    public static final String[]DESCRIPTION;

    private static final Texture IMG = new Texture("cfcImages/blight/combatSLChecker.png");
    private static final Texture IMG_OUTLINE = new Texture("cfcImages/blight/combatSLChecker.png");


    public SLinBattle() {
        super(ID, NAME, DESCRIPTION[0], "void.png", true);
        this.img = IMG;
        this.outlineImg = IMG_OUTLINE;
        this.increment = 0;
        this.counter = 1;
        updateDescription();
    }

    public void updateDescription() {
        this.description = String.format(DESCRIPTION[0], CatFoodCupRacingMod.maxSLTimes, SaveLoadCheck.inBattleSLCounter);
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
    }

    static {
        BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
        NAME = blightStrings.NAME;
        DESCRIPTION = blightStrings.DESCRIPTION;
    }
}
