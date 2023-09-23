package demoMod.cfcracing.blights;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.abstracts.CustomSavableRaw;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.BlightStrings;

public class SLinBattle extends AbstractBlight {
    public static final String ID = "cfc:SLinBattle";

    public static final String NAME;

    public static final String[]DESCRIPTION;

    private AbstractPlayer p;

    private static final Texture IMG = new Texture("cfcImages/blight/combatSLChecker.png");
    private static final Texture IMG_OUTLINE = new Texture("cfcImages/blight/combatSLChecker.png");


    public SLinBattle() {
        super(ID, NAME, DESCRIPTION[0], "void.png", true);
        this.img = IMG;
        this.outlineImg = IMG_OUTLINE;
        this.increment = 0;
        this.p = AbstractDungeon.player;
        this.counter = -1;
        //BaseMod.addSaveField(ID, (CustomSavableRaw)this);
    }
    public void updateDescription() {
        this.description = DESCRIPTION[0];
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
