package demoMod.cfcracing.blights;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.BlightStrings;

public class SLoutBattle extends AbstractBlight {
    public static final String ID = "cfc:SLoutBattle";

    public static final String NAME;

    public static final String[] DESCRIPTION;

    private static final Texture IMG = new Texture("cfcImages/blight/nonCombatSLChecker.png");
    private static final Texture IMG_OUTLINE = new Texture("cfcImages/blight/nonCombatSLChecker.png");

    public SLoutBattle() {
        super(ID, NAME, DESCRIPTION[0], "void.png", true);
        this.img = IMG;
        this.outlineImg = IMG_OUTLINE;
        this.increment = 0;
        this.counter = 0;
    }

    public void updateDescription() {
        this.description = String.format(DESCRIPTION[0], Math.max(0, this.counter));
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
