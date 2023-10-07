package demoMod.cfcracing.ui;

import basemod.ModButton;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.function.Consumer;

public class CardFilterModMenuButton extends ModButton {
    private final CardFilterModMenu cardFilterModMenu;
    public boolean menuHidden = false;

    public CardFilterModMenuButton(float xPos, float yPos, ModPanel p, Consumer<ModButton> c) {
        super(xPos, yPos, p, c);
        cardFilterModMenu = new CardFilterModMenu(p);
        cardFilterModMenu.initialize();
    }

    @Override
    public void update() {
        super.update();
        cardFilterModMenu.update();
        menuHidden = CardFilterModMenu.hidden;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        cardFilterModMenu.render(sb);
    }
}
