package demoMod.cfcracing;

import basemod.*;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.DialogWord;
import com.megacrit.cardcrawl.vfx.SpeechTextEffect;
import demoMod.cfcracing.entity.CardSetting;
import demoMod.cfcracing.patches.SaveLoadCheck;
import demoMod.cfcracing.patches.TopPanelPatch;
import demoMod.cfcracing.patches.rngfix.MonsterHelperPatch;
import demoMod.cfcracing.ui.CardFilterModMenu;
import demoMod.cfcracing.ui.CardFilterModMenuButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@SpireInitializer
public class CatFoodCupRacingMod implements StartGameSubscriber,
        PostInitializeSubscriber,
        PostDungeonInitializeSubscriber,
        StartActSubscriber,
        EditStringsSubscriber, PostUpdateSubscriber {
    public static SpireConfig saves;
    public static HashMap<String, CardSetting> configSettings = new HashMap<>();

    public static ArrayList<AbstractGameAction> myActions = new ArrayList<>();

    public static boolean defaultA15Option = false;

    public static int maxSLTimes = 1;

    public static int ironcladBonus = 360;

    public static int silentBonus = 420;

    public static int defectBonus = 450;

    public static int watcherBonus = 300;

    static {
        try {
            saves = new SpireConfig("cfc-racing", "saves");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize() {
        new CatFoodCupRacingMod();
    }

    public CatFoodCupRacingMod() {
        BaseMod.subscribe(this);
    }

    @Override
    public void receiveStartGame() {
        if (AbstractDungeon.floorNum == 0) {
            saves.setInt("wheelGameCounter", 0);
            saves.setInt("matchGameCounter", 0);
            saves.setInt("cardRarityRngCounter", 0);
            saves.setInt("merchantRngCounter", 0);
            saves.setInt("wheelGameLastFloor", -1);
            saves.setInt("matchGameLastFloor", -1);
            saves.setInt("merchantRngLastFloor", -1);
            saves.setFloat("totalTime", 0);
            saves.setFloat("correctTime", 0);
            TopPanelPatch.correct = 0.0F;

            saves.setInt("EventRngCountLast", -1);
            saves.setString("EventResultLast", "");
            try {
                saves.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            SaveLoadCheck.inBattleSLCounter = 0;
            SaveLoadCheck.outBattleSLCounter = 0;
            saves.setInt("inBattleSLCounter", 0);
            saves.setInt("outBattleSLCounter", 0);
        }
        purgeCardPool();
    }

    @Override
    public void receivePostDungeonInitialize() {
        purgeCardPool();
    }

    public static void toggleCard(String cardID) {
        CardSetting changingSetting = configSettings.get(cardID);
        changingSetting.toggleDisabled();
        changingSetting.saveToData(saves);
        saveSettingsData();
    }

    public static void saveSettingsData() {
        try {
            for (String key : configSettings.keySet()) {
                CardSetting setting = configSettings.get(key);
                setting.saveToData(saves);
            }
            saves.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadSettingsData() {
        try {
            saves.load();
            for (String key : configSettings.keySet()) {
                CardSetting setting = configSettings.get(key);
                if (!saves.has(setting.settingsId)) {
                    saves.setBool(setting.settingsId, setting.isDisabled);
                }
                setting.loadFromData(saves);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDisabled(AbstractCard card) {
        CardSetting cardSetting = configSettings.get(card.cardID);
        return cardSetting != null && cardSetting.isDisabled;
    }

    public static void purgeCardPool() {
        for (Map.Entry<String, CardSetting> c : configSettings.entrySet()) {
            if (c.getValue().isDisabled) {
                removeCardFromPool(c.getKey());
            }
        }
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CardFilterModMenu");
        String warningText = uiStrings == null ? "#r警告：卡池过小！ NL " : uiStrings.TEXT[2];
        if (!VerifyLegality()) {
            myActions.add(new AbstractGameAction() {
                @Override
                public void update() {
                    if (AbstractDungeon.topLevelEffects == null) return;
                    this.isDone = true;
                    String ts = "";
                    for (int i = 0; i < 10; i++) {
                        ts += warningText;
                    }
                    AbstractDungeon.topLevelEffects.add(new SpeechTextEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, 20.0F, ts, DialogWord.AppearEffect.FADE_IN));
                }
            });

        }
    }

    public static void removeCardFromPool(String cardName) {
        AbstractCard card = CardLibrary.getCard(cardName);
        try {
            if (card.color == AbstractCard.CardColor.COLORLESS) {
                AbstractDungeon.colorlessCardPool.removeCard(card);
                AbstractDungeon.srcColorlessCardPool.removeCard(card);
            } else {
                switch (card.rarity) {
                    case COMMON:
                        AbstractDungeon.commonCardPool.removeCard(card);
                        AbstractDungeon.srcCommonCardPool.removeCard(card);
                        break;
                    case UNCOMMON:
                        AbstractDungeon.uncommonCardPool.removeCard(card);
                        AbstractDungeon.srcUncommonCardPool.removeCard(card);
                    case RARE:
                        AbstractDungeon.rareCardPool.removeCard(card);
                        AbstractDungeon.srcRareCardPool.removeCard(card);
                        break;
                    case CURSE:
                        AbstractDungeon.curseCardPool.removeCard(card);
                        AbstractDungeon.srcCurseCardPool.removeCard(card);
                        break;
                }
            }
            AbstractDungeon.player.masterDeck.group.removeIf(card1 -> card1.cardID.equals(cardName));
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void receivePostInitialize() {
        ModPanel settingsPanel = new ModPanel();
        for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
            configSettings.put(c.getKey(), new CardSetting(c.getKey(), c.getKey()));
        }
        loadSettingsData();
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CardFilterModMenu");
        CardFilterModMenuButton subMenu = new CardFilterModMenuButton(525.0F, 612.0F, settingsPanel, button -> CardFilterModMenu.hidden = !CardFilterModMenu.hidden) {
            @Override
            public int renderLayer() {
                return 99;
            }
        };
        ModLabel cardBanLabel = new ModLabel(uiStrings.TEXT[4], 350.0F, 655.0F, settingsPanel, modLabel -> {});
        ModLabeledToggleButton defaultA15 = new ModLabeledToggleButton(uiStrings.TEXT[3], 350.0F, 725.0F, Color.WHITE, FontHelper.buttonLabelFont, defaultA15Option, settingsPanel, (me) -> {},
                (me) -> {
                    if (!subMenu.menuHidden) {
                        me.enabled = !me.enabled;
                        return;
                    }
                    defaultA15Option = me.enabled;
                    saves.setBool("defaultA15Option", defaultA15Option);
                    try {
                        saves.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        ModSlider maxSLTimesSlider = new ModSlider(uiStrings.TEXT[5], 470.0F, 600.0F, 1.0F, "", settingsPanel, modSlider -> {
            int newValue = Math.round(1.0F + modSlider.value * 4.0F);
            if (newValue != maxSLTimes) {
                maxSLTimes = newValue;
                saves.setInt("maxSLTimes", maxSLTimes);
                try {
                    saves.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public void update() {
                if (!subMenu.menuHidden) {
                    return;
                }
                super.update();
                this.value = 1.0F + this.value * 4.0F;
            }
        };
        ModLabel heartBonusTimeLabel = new ModLabel(uiStrings.TEXT[6], 1100.0F, 655.0F, settingsPanel, modLabel -> {});
        ModSlider ironcladBonusSlider = new ModSlider(uiStrings.TEXT[7], 1220.0F, 605.0F, 1500.0F, "s", settingsPanel, modSlider -> {
            int t = Math.round(modSlider.value * modSlider.multiplier);
            int newValue = t - (t % 10);
            if (newValue != ironcladBonus) {
                ironcladBonus = newValue;
                saves.setInt("ironcladBonus", ironcladBonus);
                try {
                    saves.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public void update() {
                if (!subMenu.menuHidden) {
                    return;
                }
                super.update();
                int t = Math.round(this.value * this.multiplier);
                this.value = t - (t % 10);
            }

            @Override
            public void render(SpriteBatch sb) {
                float sliderX = ReflectionHacks.getPrivate(this, ModSlider.class, "sliderX");
                float y = ReflectionHacks.getPrivate(this, ModSlider.class, "y");
                float handleX = ReflectionHacks.getPrivate(this, ModSlider.class, "handleX");
                String label = ReflectionHacks.getPrivate(this, ModSlider.class, "label");
                String suffix = ReflectionHacks.getPrivate(this, ModSlider.class, "suffix");
                boolean sliderGrabbed = ReflectionHacks.getPrivate(this, ModSlider.class, "sliderGrabbed");
                Hitbox hb = ReflectionHacks.getPrivate(this, ModSlider.class, "hb");
                Hitbox bgHb = ReflectionHacks.getPrivate(this, ModSlider.class, "bgHb");

                sb.setColor(Color.WHITE);
                sb.draw(ImageMaster.OPTION_SLIDER_BG, sliderX, y - 12.0F, 0.0F, 12.0F, 250.0F, 24.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 250, 24, false, false);
                sb.draw(ImageMaster.OPTION_SLIDER, handleX - 22.0F, y - 22.0F, 22.0F, 22.0F, 44.0F, 44.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 44, 44, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, label, sliderX - 55.0F * Settings.scale, y, Color.WHITE);
                String renderVal = Integer.toString((int) Math.floor(this.value));
                if (sliderGrabbed) {
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal + suffix, sliderX + 230.0F * Settings.scale + 55.0F * Settings.scale, y, Settings.GREEN_TEXT_COLOR);
                } else {
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal + suffix, sliderX + 230.0F * Settings.scale + 55.0F * Settings.scale, y, Settings.BLUE_TEXT_COLOR);
                }

                hb.render(sb);
                bgHb.render(sb);
            }
        };

        ModSlider silentBonusSlider = new ModSlider(uiStrings.TEXT[8], 1220.0F, 505.0F, 1500.0F, "s", settingsPanel, modSlider -> {
            int t = Math.round(modSlider.value * modSlider.multiplier);
            int newValue = t - (t % 10);
            if (newValue != silentBonus) {
                silentBonus = newValue;
                saves.setInt("silentBonus", silentBonus);
                try {
                    saves.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public void update() {
                if (!subMenu.menuHidden) {
                    return;
                }
                super.update();
                int t = Math.round(this.value * this.multiplier);
                this.value = t - (t % 10);
            }

            @Override
            public void render(SpriteBatch sb) {
                float sliderX = ReflectionHacks.getPrivate(this, ModSlider.class, "sliderX");
                float y = ReflectionHacks.getPrivate(this, ModSlider.class, "y");
                float handleX = ReflectionHacks.getPrivate(this, ModSlider.class, "handleX");
                String label = ReflectionHacks.getPrivate(this, ModSlider.class, "label");
                String suffix = ReflectionHacks.getPrivate(this, ModSlider.class, "suffix");
                boolean sliderGrabbed = ReflectionHacks.getPrivate(this, ModSlider.class, "sliderGrabbed");
                Hitbox hb = ReflectionHacks.getPrivate(this, ModSlider.class, "hb");
                Hitbox bgHb = ReflectionHacks.getPrivate(this, ModSlider.class, "bgHb");

                sb.setColor(Color.WHITE);
                sb.draw(ImageMaster.OPTION_SLIDER_BG, sliderX, y - 12.0F, 0.0F, 12.0F, 250.0F, 24.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 250, 24, false, false);
                sb.draw(ImageMaster.OPTION_SLIDER, handleX - 22.0F, y - 22.0F, 22.0F, 22.0F, 44.0F, 44.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 44, 44, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, label, sliderX - 55.0F * Settings.scale, y, Color.WHITE);
                String renderVal = Integer.toString((int) Math.floor(this.value));
                if (sliderGrabbed) {
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal + suffix, sliderX + 230.0F * Settings.scale + 55.0F * Settings.scale, y, Settings.GREEN_TEXT_COLOR);
                } else {
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal + suffix, sliderX + 230.0F * Settings.scale + 55.0F * Settings.scale, y, Settings.BLUE_TEXT_COLOR);
                }

                hb.render(sb);
                bgHb.render(sb);
            }
        };

        ModSlider defectBonusSlider = new ModSlider(uiStrings.TEXT[9], 1220.0F, 405.0F, 1500.0F, "s", settingsPanel, modSlider -> {
            int t = Math.round(modSlider.value * modSlider.multiplier);
            int newValue = t - (t % 10);
            if (newValue != defectBonus) {
                defectBonus = newValue;
                saves.setInt("defectBonus", defectBonus);
                try {
                    saves.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public void update() {
                if (!subMenu.menuHidden) {
                    return;
                }
                super.update();
                int t = Math.round(this.value * this.multiplier);
                this.value = t - (t % 10);
            }

            @Override
            public void render(SpriteBatch sb) {
                float sliderX = ReflectionHacks.getPrivate(this, ModSlider.class, "sliderX");
                float y = ReflectionHacks.getPrivate(this, ModSlider.class, "y");
                float handleX = ReflectionHacks.getPrivate(this, ModSlider.class, "handleX");
                String label = ReflectionHacks.getPrivate(this, ModSlider.class, "label");
                String suffix = ReflectionHacks.getPrivate(this, ModSlider.class, "suffix");
                boolean sliderGrabbed = ReflectionHacks.getPrivate(this, ModSlider.class, "sliderGrabbed");
                Hitbox hb = ReflectionHacks.getPrivate(this, ModSlider.class, "hb");
                Hitbox bgHb = ReflectionHacks.getPrivate(this, ModSlider.class, "bgHb");

                sb.setColor(Color.WHITE);
                sb.draw(ImageMaster.OPTION_SLIDER_BG, sliderX, y - 12.0F, 0.0F, 12.0F, 250.0F, 24.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 250, 24, false, false);
                sb.draw(ImageMaster.OPTION_SLIDER, handleX - 22.0F, y - 22.0F, 22.0F, 22.0F, 44.0F, 44.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 44, 44, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, label, sliderX - 55.0F * Settings.scale, y, Color.WHITE);
                String renderVal = Integer.toString((int) Math.floor(this.value));
                if (sliderGrabbed) {
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal + suffix, sliderX + 230.0F * Settings.scale + 55.0F * Settings.scale, y, Settings.GREEN_TEXT_COLOR);
                } else {
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal + suffix, sliderX + 230.0F * Settings.scale + 55.0F * Settings.scale, y, Settings.BLUE_TEXT_COLOR);
                }

                hb.render(sb);
                bgHb.render(sb);
            }
        };

        ModSlider watcherBonusSlider = new ModSlider(uiStrings.TEXT[10], 1220.0F, 305.0F, 1500.0F, "s", settingsPanel, modSlider -> {
            int t = Math.round(modSlider.value * modSlider.multiplier);
            float newValue = t - (t % 10);
            if (newValue != watcherBonus) {
                watcherBonus = (int) newValue;
                saves.setInt("watcherBonus", watcherBonus);
                try {
                    saves.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public void update() {
                if (!subMenu.menuHidden) {
                    return;
                }
                super.update();
                int t = Math.round(this.value * this.multiplier);
                this.value = t - (t % 10);
            }

            @Override
            public void render(SpriteBatch sb) {
                float sliderX = ReflectionHacks.getPrivate(this, ModSlider.class, "sliderX");
                float y = ReflectionHacks.getPrivate(this, ModSlider.class, "y");
                float handleX = ReflectionHacks.getPrivate(this, ModSlider.class, "handleX");
                String label = ReflectionHacks.getPrivate(this, ModSlider.class, "label");
                String suffix = ReflectionHacks.getPrivate(this, ModSlider.class, "suffix");
                boolean sliderGrabbed = ReflectionHacks.getPrivate(this, ModSlider.class, "sliderGrabbed");
                Hitbox hb = ReflectionHacks.getPrivate(this, ModSlider.class, "hb");
                Hitbox bgHb = ReflectionHacks.getPrivate(this, ModSlider.class, "bgHb");

                sb.setColor(Color.WHITE);
                sb.draw(ImageMaster.OPTION_SLIDER_BG, sliderX, y - 12.0F, 0.0F, 12.0F, 250.0F, 24.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 250, 24, false, false);
                sb.draw(ImageMaster.OPTION_SLIDER, handleX - 22.0F, y - 22.0F, 22.0F, 22.0F, 44.0F, 44.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 44, 44, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, label, sliderX - 55.0F * Settings.scale, y, Color.WHITE);
                String renderVal = Integer.toString((int) Math.floor(this.value));
                if (sliderGrabbed) {
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal + suffix, sliderX + 230.0F * Settings.scale + 55.0F * Settings.scale, y, Settings.GREEN_TEXT_COLOR);
                } else {
                    FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, renderVal + suffix, sliderX + 230.0F * Settings.scale + 55.0F * Settings.scale, y, Settings.BLUE_TEXT_COLOR);
                }

                hb.render(sb);
                bgHb.render(sb);
            }
        };

        settingsPanel.addUIElement(cardBanLabel);
        settingsPanel.addUIElement(defaultA15);
        settingsPanel.addUIElement(maxSLTimesSlider);
        settingsPanel.addUIElement(heartBonusTimeLabel);
        settingsPanel.addUIElement(ironcladBonusSlider);
        settingsPanel.addUIElement(silentBonusSlider);
        settingsPanel.addUIElement(defectBonusSlider);
        settingsPanel.addUIElement(watcherBonusSlider);
        settingsPanel.addUIElement(subMenu);

        Texture badgeTexture = ImageMaster.loadImage("cfcImages/ui/badge.png");
        BaseMod.registerModBadge(badgeTexture, "CFC Racing Mod", "Temple9", "todo", settingsPanel);
        BaseMod.addSaveField("cfc:SLCheck", new SaveLoadCheck());
        BaseMod.addSaveField("cfc:encounterPool", new MonsterHelperPatch());
        if (saves.has("defaultA15Option")) {
            defaultA15Option = saves.getBool("defaultA15Option");
        }
        if (saves.has("maxSLTimes")) {
            maxSLTimes = saves.getInt("maxSLTimes");
        }
        maxSLTimesSlider.setValue((maxSLTimes - 1.0F) / 4.0F);
        if (saves.has("ironcladBonus")) {
            ironcladBonus = saves.getInt("ironcladBonus");
        }
        ironcladBonusSlider.setValue(ironcladBonus / 1500.0F);
        if (saves.has("silentBonus")) {
            silentBonus = saves.getInt("silentBonus");
        }
        silentBonusSlider.setValue(silentBonus / 1500.0F);
        if (saves.has("defectBonus")) {
            defectBonus = saves.getInt("defectBonus");
        }
        defectBonusSlider.setValue(defectBonus / 1500.0F);
        if (saves.has("watcherBonus")) {
            watcherBonus = saves.getInt("watcherBonus");
        }
        watcherBonusSlider.setValue(watcherBonus / 1500.0F);
    }

    @Override
    public void receiveStartAct() {
        purgeCardPool();
    }

    @Override
    public void receiveEditStrings() {
        String language = Settings.language.name().toLowerCase(Locale.ROOT);
        String uiStrings = Gdx.files.internal("localizations/" + language + "/CFCRacing-UIStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
        String blightStrings = Gdx.files.internal("localizations/" + language + "/CFCRacing-BlightStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(BlightStrings.class, blightStrings);
    }

    @Override
    public void receivePostUpdate() {
        if (!myActions.isEmpty()) {
            myActions.get(0).update();
            if (myActions.get(0).isDone) myActions.remove(0);
        }
        if (!BaseMod.modSettingsUp) {
            CardFilterModMenu.hidden = true;
        }
    }


    public static boolean VerifyLegality() {
        if (AbstractDungeon.uncommonCardPool.group.size() + AbstractDungeon.commonCardPool.group.size() < 20)
            return false;//图书馆。要考虑出金卡概率为0的情况。
        if (AbstractDungeon.commonCardPool.group.size() < 4) return false;//问号牌卡牌掉落，下同。
        if (AbstractDungeon.uncommonCardPool.group.size() < 4) return false;
        if (AbstractDungeon.rareCardPool.group.size() < 4) return false;

        if (AbstractDungeon.curseCardPool.group.size() < 1) return false;//红钥匙随机诅咒（是这样吗？）

        int cntA = 0;
        int cntS = 0;
        for (AbstractCard c : AbstractDungeon.commonCardPool.group) {
            if (c.type == AbstractCard.CardType.ATTACK) cntA++;
            else if (c.type == AbstractCard.CardType.SKILL) cntS++;
        }
        if (cntA < 2 || cntS < 2) return false;//商店
        cntA = 0;
        cntS = 0;
        for (AbstractCard c : AbstractDungeon.uncommonCardPool.group) {
            if (c.type == AbstractCard.CardType.ATTACK) cntA++;
            else if (c.type == AbstractCard.CardType.SKILL) cntS++;
        }
        if (cntA < 2 || cntS < 2) return false;//商店
        cntA = 0;
        cntS = 0;
        int cntP = 0;
        for (AbstractCard c : AbstractDungeon.rareCardPool.group) {
            if (c.type == AbstractCard.CardType.ATTACK) cntA++;
            else if (c.type == AbstractCard.CardType.SKILL) cntS++;
            else if (c.type == AbstractCard.CardType.POWER) cntP++;
        }
        if (cntA < 2 || cntS < 2 || cntP < 1) return false;//商店 不知道为什么，但是找不到罕见能力会去出稀有能力，所以有就行……吗？
        int cntR = 0;
        int cntU = 0;
        for (AbstractCard c : AbstractDungeon.colorlessCardPool.group) {
            if (c.rarity == AbstractCard.CardRarity.RARE) cntR++;
            else if (c.rarity == AbstractCard.CardRarity.UNCOMMON) cntU++;
        }
        if (cntR < 4 || cntU < 4) return false;//感知石。考虑问号牌的情况下
        cntA = cntS = cntP = 0;
        for (AbstractCard c : AbstractDungeon.srcCommonCardPool.group) {
            if (c.hasTag(AbstractCard.CardTags.HEALING)) continue;
            if (c.type == AbstractCard.CardType.ATTACK) cntA++;
            else if (c.type == AbstractCard.CardType.SKILL) cntS++;
            else if (c.type == AbstractCard.CardType.POWER) cntP++;
        }
        for (AbstractCard c : AbstractDungeon.srcUncommonCardPool.group) {
            if (c.hasTag(AbstractCard.CardTags.HEALING)) continue;
            if (c.type == AbstractCard.CardType.ATTACK) cntA++;
            else if (c.type == AbstractCard.CardType.SKILL) cntS++;
            else if (c.type == AbstractCard.CardType.POWER) cntP++;
        }
        for (AbstractCard c : AbstractDungeon.srcRareCardPool.group) {
            if (c.hasTag(AbstractCard.CardTags.HEALING)) continue;
            if (c.type == AbstractCard.CardType.ATTACK) cntA++;
            else if (c.type == AbstractCard.CardType.SKILL) cntS++;
            else if (c.type == AbstractCard.CardType.POWER) cntP++;
        }
        if (cntA < 3 || cntS < 3 || cntP < 3) return false;//药水等
        return true;
    }
}
