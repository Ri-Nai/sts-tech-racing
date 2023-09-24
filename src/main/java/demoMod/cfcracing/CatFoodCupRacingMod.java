package demoMod.cfcracing;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.DialogWord;
import com.megacrit.cardcrawl.vfx.SpeechTextEffect;
import demoMod.cfcracing.entity.CardSetting;
import demoMod.cfcracing.patches.SaveLoadCheck;
import demoMod.cfcracing.ui.CardFilterModMenu;

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
        if (!VerifyLegality()) {
            myActions.add(new AbstractGameAction() {
                @Override
                public void update() {
                    if (AbstractDungeon.topLevelEffects == null) return;
                    this.isDone = true;
                    String ts = "";
                    for (int i = 0; i < 10; i++) {
                        ts += "#r警告：卡池过小 NL ";
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
        CardFilterModMenu subMenu = new CardFilterModMenu(settingsPanel);
        subMenu.initialize();
        settingsPanel.addUIElement(subMenu);
        Texture badgeTexture = ImageMaster.loadImage("cfcImages/ui/badge.png");
        BaseMod.registerModBadge(badgeTexture, "CFC Racing Mod", "Temple9", "todo", settingsPanel);
        BaseMod.addSaveField("cfc:SLCheck", new SaveLoadCheck());
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
