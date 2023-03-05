package demoMod.cfcracing;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import demoMod.cfcracing.entity.CardSetting;
import demoMod.cfcracing.ui.CardFilterModMenu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SpireInitializer
public class CatFoodCupRacingMod implements StartGameSubscriber,
                                            PostInitializeSubscriber,
                                            PostDungeonInitializeSubscriber,
                                            StartActSubscriber,
                                            EditStringsSubscriber {
    public static SpireConfig saves;
    public static HashMap<String, CardSetting> configSettings = new HashMap<>();

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
            saves.setInt("trulyRandomCardRngCounter", 0);
            saves.setInt("wheelGameLastFloor", -1);
            saves.setInt("matchGameLastFloor", -1);
            try {
                saves.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
        }
        catch (IOException e) {
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
        CardFilterModMenu subMenu = new CardFilterModMenu(settingsPanel);
        subMenu.initialize();
        settingsPanel.addUIElement(subMenu);
        Texture badgeTexture = ImageMaster.loadImage("cfcImages/ui/badge.png");
        BaseMod.registerModBadge(badgeTexture, "CFC Racing Mod", "Temple9", "todo", settingsPanel);
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
    }
}
