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
    private static final float ROW_HEIGHT = 40.0F;

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
        // 渲染左侧文字标签（行高与 ModLabel/ModLabeledToggleButton 一致）
        FontHelper.renderSmartText(sb, FontHelper.buttonLabelFont, label, x, y, 9999.0F, ROW_HEIGHT, Color.WHITE);

        // 根据文字宽度计算下拉位置；下拉与标签同一行：垂直居中对齐（与 IntSlider 的 renderFontCentered 语义一致）
        float textWidth = FontHelper.getWidth(FontHelper.buttonLabelFont, label, 1.0f);
        float offset = textWidth + 50.0F;
        float rowH = dropdown.approximateRowHeight();
        float dropdownY = y + (ROW_HEIGHT - rowH) * 0.5F;
        dropdown.render(sb, x + offset, dropdownY);
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
