package demoMod.cfcracing.ui;

import basemod.IUIElement;
import basemod.ModPanel;
import basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar.ColorTabBarFix;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import demoMod.cfcracing.CatFoodCupRacingMod;
import demoMod.cfcracing.entity.CardSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CardFilterModMenu implements TabBarListener, ScrollBarListener, IUIElement {
    private static final Logger logger = LogManager.getLogger(CardFilterModMenu.class.getName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CardLibraryScreen");
    private static final UIStrings resetTipStrings = CardCrawlGame.languagePack.getUIString("CardFilterModMenu");
    public static final String[] TEXT = uiStrings.TEXT;
    private static float drawStartX;
    private static final float drawStartY = Settings.HEIGHT * 0.66F;
    private static float padX;
    private static float padY;
    private static final int CARDS_PER_LINE = 5;
    private boolean grabbedScreen;
    private float grabStartY;
    private float currentDiffY;
    private final float scrollLowerBound;
    private float scrollUpperBound;
    private AbstractCard hoveredCard;
    private AbstractCard clickStartedCard;
    private final ColorTabBar colorBar;
    public MenuCancelButton button;
    public static Map<AbstractCard.CardColor, CardGroup> cardGroupMap = new HashMap<>();
    private final CardGroup redCards;
    private final CardGroup greenCards;
    private final CardGroup blueCards;
    private final CardGroup purpleCards;
    private final CardGroup colorlessCards;
    private final CardGroup curseCards;
    private final CardLibSortHeader sortHeader;
    private CardGroup visibleCards;
    private final ScrollBar scrollBar;
    private CardLibSelectionType type;
    private Texture filterSelectionImg;
    private Texture resetButton;
    private Hitbox resetButtonHb;
    private Color resetButtonColor;
    private int selectionIndex;
    private AbstractCard controllerCard;
    private final ModPanel parent;
    private final Texture overlay_deprecated;

    public CardFilterModMenu(ModPanel parent) {
        this.grabbedScreen = false;
        this.grabStartY = 0.0F;
        this.currentDiffY = 0.0F;
        this.scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
        this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
        this.hoveredCard = null;
        this.clickStartedCard = null;
        this.button = new MenuCancelButton();
        this.redCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.greenCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.blueCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.purpleCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.colorlessCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.curseCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        this.type = CardLibSelectionType.NONE;
        this.filterSelectionImg = null;
        this.selectionIndex = 0;
        this.controllerCard = null;
        this.parent = parent;
        drawStartX = Settings.WIDTH;
        drawStartX -= 5.0F * AbstractCard.IMG_WIDTH * 0.75F;
        drawStartX -= 4.0F * Settings.CARD_VIEW_PAD_X;
        drawStartX /= 2.0F;
        drawStartX += AbstractCard.IMG_WIDTH * 0.75F / 2.0F;
        padX = AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X;
        padY = AbstractCard.IMG_HEIGHT * 0.75F + Settings.CARD_VIEW_PAD_Y;
        this.colorBar = new ColorTabBar(this);
        this.sortHeader = new CardLibSortHeader(null);
        this.scrollBar = new ScrollBar(this);
        this.overlay_deprecated = new Texture("cfcImages/ui/cardLibrary/disabledCardX.png");
        this.resetButton = new Texture("cfcImages/ui/cardLibrary/resetButton.png");
        this.resetButtonHb = new Hitbox(100.0F * Settings.scale, 820.0F * Settings.scale, 200.0F * Settings.scale, 200.0F * Settings.scale);
        this.resetButtonColor = new Color(0.5F, 0.5F, 0.5F, 1.0F);
    }

    public void initialize() {
        logger.info("Initializing card library screen.");
        this.redCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.RED);
        this.greenCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.GREEN);
        this.blueCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.BLUE);
        this.purpleCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.PURPLE);
        this.colorlessCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.COLORLESS);
        this.curseCards.group = CardLibrary.getCardList(CardLibrary.LibraryType.CURSE);
        this.visibleCards = this.redCards;
        this.sortHeader.setGroup(this.visibleCards);
        AbstractCard.CardColor[] arrayOfCardColor = AbstractCard.CardColor.values();
        for (int icolor = AbstractCard.CardColor.CURSE.ordinal() + 1; icolor < arrayOfCardColor.length; icolor++) {
            CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            group.group = CardLibrary.getCardList(CardLibrary.LibraryType.valueOf(arrayOfCardColor[icolor].name()));
            cardGroupMap.put(arrayOfCardColor[icolor], group);
        }
        calculateScrollBounds();
    }

    private void setLockStatus() {
        lockStatusHelper(this.redCards);
        lockStatusHelper(this.greenCards);
        lockStatusHelper(this.blueCards);
        lockStatusHelper(this.purpleCards);
        lockStatusHelper(this.colorlessCards);
        lockStatusHelper(this.curseCards);
    }

    private void lockStatusHelper(CardGroup group) {
        ArrayList<AbstractCard> toAdd = new ArrayList<>();
        Iterator<AbstractCard> i = group.group.iterator();
        while (i.hasNext()) {
            AbstractCard c = i.next();
            if (UnlockTracker.isCardLocked(c.cardID)) {
                AbstractCard tmp = CardLibrary.getCopy(c.cardID);
                tmp.setLocked();
                toAdd.add(tmp);
                i.remove();
            }
        }
        group.group.addAll(toAdd);
    }

    public void open() {
        this.controllerCard = null;
        if (Settings.isInfo) {
            CardLibrary.unlockAndSeeAllCards();
        }
        if (this.filterSelectionImg == null) {
            this.filterSelectionImg = ImageMaster.loadImage("cfcImages/ui/cardLibrary/selectBox.png");
        }
        setLockStatus();
        sortOnOpen();
        this.button.show(TEXT[0]);
        this.currentDiffY = -200.0F;
        for (AbstractCard c : this.redCards.group) {
            c.drawScale = MathUtils.random(0.2F, 0.4F);
            c.targetDrawScale = 0.75F;
        }
        for (AbstractCard c : this.greenCards.group) {
            c.drawScale = MathUtils.random(0.2F, 0.4F);
            c.targetDrawScale = 0.75F;
        }
        for (AbstractCard c : this.blueCards.group) {
            c.drawScale = MathUtils.random(0.2F, 0.4F);
            c.targetDrawScale = 0.75F;
        }
        for (AbstractCard c : this.purpleCards.group) {
            c.drawScale = MathUtils.random(0.2F, 0.4F);
            c.targetDrawScale = 0.75F;
        }
        SingleCardViewPopup.isViewingUpgrade = false;
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.CARD_LIBRARY;
    }

    private void sortOnOpen() {
        this.sortHeader.justSorted = true;
        this.visibleCards.sortAlphabetically(true);
        this.visibleCards.sortByRarity(true);
        this.visibleCards.sortByStatus(true);
    }

    public void update() {
        updateControllerInput();
        if (Settings.isControllerMode && this.controllerCard != null && !CardCrawlGame.isPopupOpen) {
            if (Gdx.input.getY() > Settings.HEIGHT * 0.75F) {
                this.currentDiffY += Settings.SCROLL_SPEED;
            }
            else if (Gdx.input.getY() < Settings.HEIGHT * 0.25F) {
                this.currentDiffY -= Settings.SCROLL_SPEED;
            }
        }
        this.colorBar.update((this.visibleCards.getBottomCard()).current_y + 230.0F * Settings.scale);
        this.sortHeader.update();
        if (this.hoveredCard != null) {
            CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);
            if (InputHelper.justClickedLeft) {
                this.clickStartedCard = this.hoveredCard;
            }
            if ((InputHelper.justReleasedClickLeft && this.clickStartedCard != null && this.hoveredCard != null) || (this.hoveredCard != null && CInputActionSet.select.isJustPressed())) {
                if (Settings.isControllerMode) {
                    this.clickStartedCard = this.hoveredCard;
                }
                InputHelper.justReleasedClickLeft = false;
                CardCrawlGame.cardPopup.open(this.clickStartedCard, this.visibleCards);
                this.clickStartedCard = null;
            }
            if (InputHelper.justClickedRight) {
                assert this.hoveredCard != null;
                logger.info("Disabling card " + this.hoveredCard.cardID);
                CatFoodCupRacingMod.toggleCard(this.hoveredCard.cardID);
            }
        } else {
            this.clickStartedCard = null;
        }
        boolean isScrollBarScrolling = this.scrollBar.update();
        if (!CardCrawlGame.cardPopup.isOpen && !isScrollBarScrolling) {
            updateScrolling();
        }
        updateCards();
        this.button.update();
        if (this.button.hb.clicked || InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            this.button.hb.clicked = false;
            this.button.hide();
            CardCrawlGame.mainMenuScreen.panelScreen.refresh();
        }
        if (Settings.isControllerMode && this.controllerCard != null) {
            Gdx.input.setCursorPosition((int)this.controllerCard.hb.cX, (int)(Settings.HEIGHT - this.controllerCard.hb.cY));
        }
        this.resetButtonHb.update();
        if (this.resetButtonHb.hovered) {
            if (this.resetButtonHb.justHovered) {
                CardCrawlGame.sound.playA("UI_HOVER", -0.4F);
            }
            this.resetButtonColor.r = this.resetButtonColor.g = this.resetButtonColor.b = 1.0F;
            if (InputHelper.justClickedLeft) {
                for (Map.Entry<String, CardSetting> e : CatFoodCupRacingMod.configSettings.entrySet()) {
                    e.getValue().isDisabled = false;
                }
                CatFoodCupRacingMod.saveSettingsData();
            }
        } else {
            this.resetButtonColor.r = this.resetButtonColor.g = this.resetButtonColor.b = 0.5F;
        }
    }

    private void updateControllerInput() {
        if (!Settings.isControllerMode) {
            return;
        }
        this.selectionIndex = 0;
        boolean anyHovered = false;
        this.type = CardLibSelectionType.NONE;
        if (this.colorBar.viewUpgradeHb.hovered) {
            anyHovered = true;
            this.type = CardLibSelectionType.FILTERS;
            this.selectionIndex = 4;
            this.controllerCard = null;
        }
        else if (this.sortHeader.updateControllerInput() != null) {
            anyHovered = true;
            this.controllerCard = null;
            this.type = CardLibSelectionType.FILTERS;
            this.selectionIndex = this.sortHeader.getHoveredIndex();
        } else {

            for (AbstractCard c : this.visibleCards.group) {
                if (c.hb.hovered) {
                    anyHovered = true;
                    this.type = CardLibSelectionType.CARDS;
                    break;
                }
                this.selectionIndex++;
            }
        }
        if (!anyHovered) {
            Gdx.input.setCursorPosition((int) this.visibleCards.group.get(0).hb.cX, Settings.HEIGHT - (int) this.visibleCards.group.get(0).hb.cY);
            return;
        }
        switch (this.type) {
            case FILTERS:
                if ((CInputActionSet.up.isJustPressed() || CInputActionSet.altUp.isJustPressed()) && this.visibleCards.size() > 5) {
                    if (this.selectionIndex < 5) {
                        Gdx.input.setCursorPosition((int)(this.sortHeader.buttons[0]).hb.cX, Settings.HEIGHT - (int)(this.sortHeader.buttons[0]).hb.cY);
                        this.controllerCard = null;
                        return;
                    }
                    this.selectionIndex -= 5;
                    Gdx.input.setCursorPosition((int) this.visibleCards.group.get(this.selectionIndex).hb.cX, Settings.HEIGHT - (int) this.visibleCards.group.get(this.selectionIndex).hb.cY);
                    this.controllerCard = this.visibleCards.group.get(this.selectionIndex);

                    break;
                }
                if ((CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed()) && this.visibleCards.size() > 5) {
                    if (this.selectionIndex < this.visibleCards.size() - 5) {
                        this.selectionIndex += 5;
                    } else {
                        this.selectionIndex %= 5;
                    }
                    Gdx.input.setCursorPosition((int) this.visibleCards.group.get(this.selectionIndex).hb.cX, Settings.HEIGHT - (int) this.visibleCards.group.get(this.selectionIndex).hb.cY);
                    this.controllerCard = this.visibleCards.group.get(this.selectionIndex);
                    break;
                }
                if (CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed()) {
                    if (this.selectionIndex % 5 > 0) {
                        this.selectionIndex--;
                    } else {
                        this.selectionIndex += 4;
                        if (this.selectionIndex > this.visibleCards.size() - 1) {
                            this.selectionIndex = this.visibleCards.size() - 1;
                        }
                    }
                    Gdx.input.setCursorPosition((int) this.visibleCards.group.get(this.selectionIndex).hb.cX, Settings.HEIGHT - (int) this.visibleCards.group.get(this.selectionIndex).hb.cY);
                    this.controllerCard = this.visibleCards.group.get(this.selectionIndex);
                    break;
                }
                if (CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed()) {
                    if (this.selectionIndex % 5 < 4) {
                        this.selectionIndex++;
                        if (this.selectionIndex > this.visibleCards.size() - 1) {
                            this.selectionIndex -= this.visibleCards.size() % 5;
                        }
                    } else {
                        this.selectionIndex -= 4;
                    }
                    Gdx.input.setCursorPosition((int) this.visibleCards.group.get(this.selectionIndex).hb.cX, Settings.HEIGHT - (int) this.visibleCards.group.get(this.selectionIndex).hb.cY);
                    this.controllerCard = this.visibleCards.group.get(this.selectionIndex);
                }
                break;
            case CARDS:
                if (CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed()) {
                    Gdx.input.setCursorPosition((int) this.visibleCards.group.get(0).hb.cX, Settings.HEIGHT - (int) this.visibleCards.group.get(0).hb.cY);
                    break;
                }
                if (CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed()) {
                    this.selectionIndex++;
                    if (this.selectionIndex == 4) {
                        Gdx.input.setCursorPosition((int)this.colorBar.viewUpgradeHb.cX, Settings.HEIGHT - (int)this.colorBar.viewUpgradeHb.cY);
                        break;
                    }
                    if (this.selectionIndex == 5) {
                        this.selectionIndex = 0;
                    }
                    Gdx.input.setCursorPosition((int)(this.sortHeader.buttons[this.selectionIndex]).hb.cX, Settings.HEIGHT - (int)(this.sortHeader.buttons[this.selectionIndex]).hb.cY);

                    break;
                }
                if (!CInputActionSet.left.isJustPressed() && !CInputActionSet.altLeft.isJustPressed()) {
                    break;
                }
                this.selectionIndex--;
                if (this.selectionIndex == -1) {
                    Gdx.input.setCursorPosition((int)this.colorBar.viewUpgradeHb.cX, Settings.HEIGHT - (int)this.colorBar.viewUpgradeHb.cY);
                    break;
                }
                Gdx.input.setCursorPosition((int)(this.sortHeader.buttons[this.selectionIndex]).hb.cX, Settings.HEIGHT - (int)(this.sortHeader.buttons[this.selectionIndex]).hb.cY);
                break;
        }
    }

    private void updateCards() {
        this.hoveredCard = null;
        int lineNum = 0;
        ArrayList<AbstractCard> cards = this.visibleCards.group;
        for (int i = 0; i < cards.size(); i++) {
            int mod = i % 5;
            if (mod == 0 && i != 0) {
                lineNum++;
            }
            cards.get(i).target_x = drawStartX + mod * padX;
            cards.get(i).target_y = drawStartY + this.currentDiffY - lineNum * padY;
            cards.get(i).update();
            cards.get(i).updateHoverLogic();
            if (cards.get(i).hb.hovered) {
                this.hoveredCard = cards.get(i);
            }
        }
        if (this.sortHeader.justSorted) {
            for (AbstractCard c : cards) {
                c.current_x = c.target_x;
                c.current_y = c.target_y;
            }
            this.sortHeader.justSorted = false;
        }
    }

    private void updateScrolling() {
        int y = InputHelper.mY;
        if (!this.grabbedScreen) {
            if (InputHelper.scrolledDown) {
                this.currentDiffY += Settings.SCROLL_SPEED;
            }
            else if (InputHelper.scrolledUp) {
                this.currentDiffY -= Settings.SCROLL_SPEED;
            }
            if (InputHelper.justClickedLeft) {
                this.grabbedScreen = true;
                this.grabStartY = y - this.currentDiffY;
            }

        } else if (InputHelper.isMouseDown) {
            this.currentDiffY = y - this.grabStartY;
        } else {

            this.grabbedScreen = false;
        }
        resetScrolling();
        updateBarPosition();
    }

    private void calculateScrollBounds() {
        int size = this.visibleCards.size();
        int scrollTmp;
        if (size > 10) {
            scrollTmp = size / 5 - 2;
            if (size % 5 != 0) {
                scrollTmp++;
            }
            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + scrollTmp * padY;
        } else {

            this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
        }
    }

    private void resetScrolling() {
        if (this.currentDiffY < this.scrollLowerBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollLowerBound);
        }
        else if (this.currentDiffY > this.scrollUpperBound) {
            this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollUpperBound);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        this.colorBar.render(sb, (this.visibleCards.getBottomCard()).current_y + 230.0F * Settings.scale);
        this.sortHeader.render(sb);
        renderGroup(sb, this.visibleCards);
        if (this.hoveredCard != null) {
            this.hoveredCard.renderHoverShadow(sb);
            this.hoveredCard.renderInLibrary(sb);
        }
        this.button.render(sb);
        this.scrollBar.render(sb);
        if (Settings.isControllerMode) {
            renderControllerUi(sb);
        }
        for (AbstractCard c : this.visibleCards.group) {
            if (CatFoodCupRacingMod.configSettings.get(c.cardID).isDisabled) {
                if (c.hb.hovered) {
                    sb.draw(this.overlay_deprecated, c.current_x - 256.0F, c.current_y - 248.0F, 256.0F, 256.0F, 512.0F, 512.0F, c.drawScale * Settings.scale, c.drawScale * Settings.scale, c.angle, 0, 0, 1024, 1024, false, false);
                    continue;
                }
                sb.draw(this.overlay_deprecated, c.current_x - 256.0F, c.current_y - 250.0F, 256.0F, 256.0F, 512.0F, 512.0F, c.drawScale * Settings.scale, c.drawScale * Settings.scale, c.angle, 0, 0, 1024, 1024, false, false);
            }
        }
        sb.setColor(this.resetButtonColor);
        sb.draw(this.resetButton, this.resetButtonHb.x, this.resetButtonHb.y, this.resetButtonHb.width, this.resetButtonHb.height);
        if (this.resetButtonHb.hovered) {
            TipHelper.renderGenericTip(InputHelper.mX + 70.0F * Settings.scale, InputHelper.mY, resetTipStrings.TEXT[0], resetTipStrings.TEXT[1]);
        }
        this.resetButtonHb.render(sb);
    }

    private void renderControllerUi(SpriteBatch sb) {
        sb.draw(CInputActionSet.pageLeftViewDeck.getKeyImg(), 280.0F * Settings.scale - 32.0F, (this.sortHeader.group.getBottomCard()).current_y + 280.0F * Settings.scale - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        sb.draw(CInputActionSet.pageRightViewExhaust.getKeyImg(), 1640.0F * Settings.scale - 32.0F, (this.sortHeader.group.getBottomCard()).current_y + 280.0F * Settings.scale - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        if (this.type == CardLibSelectionType.FILTERS) {
            if (this.selectionIndex != 4 && this.selectionIndex != -1) {
                sb.setColor(new Color(1.0F, 0.95F, 0.5F, 0.7F + MathUtils.cosDeg((float)(System.currentTimeMillis() / 2L % 360L)) / 5.0F));
                float doop = 1.0F + (1.0F + MathUtils.cosDeg((float)(System.currentTimeMillis() / 2L % 360L))) / 50.0F;
                sb.draw(this.filterSelectionImg, (this.sortHeader.buttons[this.selectionIndex]).hb.cX - 100.0F, (this.sortHeader.buttons[this.selectionIndex]).hb.cY - 43.0F, 100.0F, 43.0F, 200.0F, 86.0F, Settings.scale * doop, Settings.scale * doop, 0.0F, 0, 0, 200, 86, false, false);
            } else {
                sb.setColor(new Color(1.0F, 0.95F, 0.5F, 0.7F + MathUtils.cosDeg((float)(System.currentTimeMillis() / 2L % 360L)) / 5.0F));
                float doop = 1.0F + (1.0F + MathUtils.cosDeg((float)(System.currentTimeMillis() / 2L % 360L))) / 50.0F;
                sb.draw(this.filterSelectionImg, this.colorBar.viewUpgradeHb.cX - 100.0F + 25.0F * Settings.scale, this.colorBar.viewUpgradeHb.cY - 43.0F, 100.0F, 43.0F, 200.0F, 86.0F, Settings.scale * doop * 1.6F, Settings.scale * doop, 0.0F, 0, 0, 200, 86, false, false);
            }
        }
    }

    private void renderGroup(SpriteBatch sb, CardGroup group) {
        group.renderInLibrary(sb);
        group.renderTip(sb);
    }

    @Override
    public void didChangeTab(ColorTabBar tabBar, ColorTabBar.CurrentTab newSelection) {
        if (newSelection == ColorTabBarFix.Enums.MOD) {
            this.visibleCards = cardGroupMap.get((ColorTabBarFix.Fields.getModTab()).color);
        }
        CardGroup oldSelection = this.visibleCards;
        switch (newSelection) {
            case RED:
                this.visibleCards = this.redCards;
                break;

            case GREEN:
                this.visibleCards = this.greenCards;
                break;

            case BLUE:
                this.visibleCards = this.blueCards;
                break;

            case PURPLE:
                this.visibleCards = this.purpleCards;
                break;

            case COLORLESS:
                this.visibleCards = this.colorlessCards;
                break;

            case CURSE:
                this.visibleCards = this.curseCards;
                break;
        }

        if (oldSelection != this.visibleCards) {
            this.sortHeader.setGroup(this.visibleCards);
            calculateScrollBounds();
        }
        this.sortHeader.justSorted = true;
    }

    @Override
    public void scrolledUsingBar(float newPercent) {
        this.currentDiffY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
        updateBarPosition();
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY);
        this.scrollBar.parentScrolledToPercent(percent);
    }

    @Override
    public int renderLayer() {
        return 1;
    }

    @Override
    public int updateOrder() {
        return 1;
    }

    private enum CardLibSelectionType {
        NONE,
        FILTERS,
        CARDS
    }
}
