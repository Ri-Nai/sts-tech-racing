package demoMod.cfcracing.ui;

import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class IntSlider implements IUIElement {
    private static final float SLIDE_W = 230.0F * Settings.scale;
    private final Consumer<Integer> change;
    private final Hitbox hb;
    private final Hitbox bgHb;
    private final float sliderX;
    private float handleX;
    private final float y;
    private boolean sliderGrabbed = false;
    private final String label;
    private final String suffix;
    private final int lBound;
    private final int uBound;
    private final int minStep;
    private int value;
    private int renderValue;
    public ModPanel parent;

    public IntSlider(String lbl, float posX, float posY, int lBound, int uBound, int minStep, String suf, ModPanel p, Consumer<Integer> changeAction) {
        this.label = lbl;
        this.suffix = suf;
        this.lBound = lBound;
        this.uBound = uBound;
        this.minStep = minStep;
        this.parent = p;
        this.change = changeAction;
        posX *= Settings.scale;
        this.sliderX = posX - 11.0F * Settings.scale;
        this.handleX = posX + SLIDE_W;
        this.y = posY * Settings.scale;
        this.hb = new Hitbox(42.0F * Settings.scale, 38.0F * Settings.scale);
        this.bgHb = new Hitbox(300.0F * Settings.scale, 38.0F * Settings.scale);
        this.bgHb.move(this.sliderX + SLIDE_W / 2.0F, this.y);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.OPTION_SLIDER_BG, this.sliderX, this.y - 12.0F, 0.0F, 12.0F, 250.0F, 24.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 250, 24, false, false);
        sb.draw(ImageMaster.OPTION_SLIDER, this.handleX - 22.0F, this.y - 22.0F, 22.0F, 22.0F, 44.0F, 44.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 44, 44, false, false);
        FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, this.label, this.sliderX - 55.0F * Settings.scale, this.y, Color.WHITE);
        if (this.sliderGrabbed) {
            FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderValue + this.suffix, this.sliderX + SLIDE_W + 55.0F * Settings.scale, this.y, Settings.GREEN_TEXT_COLOR);
        } else {
            FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderValue + this.suffix, this.sliderX + SLIDE_W + 55.0F * Settings.scale, this.y, Settings.BLUE_TEXT_COLOR);
        }

        this.hb.render(sb);
        this.bgHb.render(sb);
    }

    @Override
    public void update() {
        this.hb.update();
        this.bgHb.update();
        this.hb.move(this.handleX, this.y);
        if (this.sliderGrabbed) {
            if (InputHelper.isMouseDown) {
                this.handleX = MathHelper.fadeLerpSnap(this.handleX, (float)InputHelper.mX);
                this.handleX = Math.min(this.sliderX + SLIDE_W + 11.0F * Settings.scale, Math.max(this.handleX, this.sliderX + 11.0F * Settings.scale));
            } else {
                this.sliderGrabbed = false;
            }
        } else if (InputHelper.justClickedLeft && (this.hb.hovered || this.bgHb.hovered)) {
            this.sliderGrabbed = true;
        }

        int oldVal = this.value;
        this.value = lBound + (int) (((this.handleX - 11.0F * Settings.scale - this.sliderX) / SLIDE_W) * (uBound - lBound));
        if (oldVal != this.value && this.value % this.minStep == 0) {
            this.renderValue = this.value;
            this.change.accept(this.value);
        }
    }

    public void setValue(int value) {
        this.value = Math.max(value, this.lBound);
        this.value = Math.min(this.value, this.uBound);
        this.renderValue = this.value;
        float percent = this.value / (float) (this.uBound - this.lBound);
        this.handleX = this.sliderX + SLIDE_W * percent + 11.0F * Settings.scale;
    }

    @Override
    public int renderLayer() {
        return 1;
    }

    @Override
    public int updateOrder() {
        return 1;
    }
}
