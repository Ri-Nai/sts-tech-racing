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
    private static final float BASE_ROW_HEIGHT = 40.0F;
    private static final float BASE_MIN_LABEL_WIDTH = 220.0F;
    private static final float BASE_MAX_LABEL_WIDTH = 420.0F;
    private static final float BASE_LABEL_PADDING = 50.0F;
    private static final float BASE_RIGHT_SAFE_MARGIN = 360.0F;

    private final float baseX;
    private final float baseY;
    private final String label;
    private final DropdownMenu dropdown;
    private final Consumer<Integer> onChange;
    private final BooleanSupplier lockSupplier;

    public DropdownSetting(float x, float y, String label, String[] options, ModPanel panel, Consumer<Integer> onChange, BooleanSupplier lockSupplier) {
        this.baseX = x;
        this.baseY = y;
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
        float x = baseX * Settings.xScale;
        float y = baseY * Settings.yScale;
        float rowHeight = BASE_ROW_HEIGHT * Settings.yScale;
        float minLabelWidth = BASE_MIN_LABEL_WIDTH * Settings.xScale;
        float maxLabelWidth = BASE_MAX_LABEL_WIDTH * Settings.xScale;
        float labelPadding = BASE_LABEL_PADDING * Settings.xScale;
        float rightSafeMargin = BASE_RIGHT_SAFE_MARGIN * Settings.xScale;

        // 与 basemod 控件一致：先绘制标签，再渲染右侧下拉框
        FontHelper.renderSmartText(sb, FontHelper.buttonLabelFont, label, x, y, 9999.0F, rowHeight, Color.WHITE);

        float textWidth = FontHelper.getWidth(FontHelper.buttonLabelFont, label, 1.0f);
        float preferredOffset = textWidth + labelPadding;
        float offset = Math.max(minLabelWidth, Math.min(preferredOffset, maxLabelWidth));
        float dropdownX = x + offset;
        float maxDropdownX = Settings.WIDTH - rightSafeMargin;
        dropdownX = Math.min(dropdownX, maxDropdownX);

        float rowH = dropdown.approximateRowHeight();
        float dropdownY = y + (rowHeight - rowH) * 0.5F;
        dropdown.render(sb, dropdownX, dropdownY);
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
