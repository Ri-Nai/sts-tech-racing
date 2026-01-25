package demoMod.cfcracing.ui;

import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class DropdownSetting implements IUIElement, DropdownMenuListener {
    private final float x;
    private final float y;
    private final String label;
    private final DropdownMenu dropdown;
    private final Consumer<Integer> onChange;
    private final BooleanSupplier lockSupplier;

    public DropdownSetting(float x, float y, String label, String[] options, ModPanel panel, Consumer<Integer> onChange, BooleanSupplier lockSupplier) {
        this.x = x;
        this.y = y;
        this.label = label;
        this.onChange = onChange;
        this.lockSupplier = lockSupplier;
        this.dropdown = new DropdownMenu(this, options, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        panel.addUIElement(this);
    }

    public DropdownSetting(float x, float y, String label, String[] options, ModPanel panel, Consumer<Integer> onChange) {
        this(x, y, label, options, panel, onChange, () -> false);
    }

    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int index, String optionText) {
        onChange.accept(index);
    }

    @Override
    public void render(SpriteBatch sb) {
        FontHelper.renderSmartText(sb, FontHelper.buttonLabelFont, label, x, y, 9999.0F, 40.0F, Color.WHITE);
        dropdown.render(sb, x + 330.0F, y + 25.0F - dropdown.approximateRowHeight());
    }

    @Override
    public void update() {
        if (lockSupplier.getAsBoolean()) {
            return;
        }
        dropdown.update();
    }

    @Override
    public int renderLayer() {
        return 0;
    }

    @Override
    public int updateOrder() {
        return 0;
    }

    public void setSelectedIndex(int index) {
        dropdown.setSelectedIndex(index);
    }
}
