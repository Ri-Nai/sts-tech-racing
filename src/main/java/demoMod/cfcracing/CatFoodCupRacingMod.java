package demoMod.cfcracing;

import MapMarks.MapMarks;
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
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.DialogWord;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.SpeechTextEffect;
import demoMod.cfcracing.entity.CardSetting;
import demoMod.cfcracing.blights.SLinBattle;
import demoMod.cfcracing.blights.SLoutBattle;
import demoMod.cfcracing.patches.SaveLoadCheck;
import demoMod.cfcracing.patches.TopPanelPatch;
import demoMod.cfcracing.patches.rngfix.MonsterHelperPatch;
import demoMod.cfcracing.ui.CardFilterModMenu;
import demoMod.cfcracing.ui.CardFilterModMenuButton;
import demoMod.cfcracing.ui.DropdownSetting;
import demoMod.cfcracing.wheelOptions.WheelOptions;

import java.io.IOException;
import java.util.*;


@SpireInitializer
public class CatFoodCupRacingMod implements StartGameSubscriber,
        PostInitializeSubscriber,
        PostDungeonInitializeSubscriber,
        StartActSubscriber,
        EditStringsSubscriber, PostUpdateSubscriber, PostRenderSubscriber,
        RenderSubscriber, AddAudioSubscriber {
    public static SpireConfig saves;
    public static HashMap<String, CardSetting> configSettings = new HashMap<>();

    public static ArrayList<AbstractGameAction> myActions = new ArrayList<>();

    public static List<AbstractGameEffect> effectList = new ArrayList<>();

    public static MapMarks mm = new MapMarks();

    public static boolean defaultA15Option = false;

    public static int maxSLCombatTimes = 1;

    public static int maxSLEventTimes = 1;

    public static int slInCombatRemaining = 0;

    public static int slOutCombatRemaining = 0;

    public static int slActLastUpdated = 0;

    public static final int[] SL_LIMIT_OPTIONS = new int[]{1, 2, 3, 5, 999};

    static {
        try {
            saves = new SpireConfig("tech-racing", "saves");
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
            saves.setInt("cardRarityEliteRngCounter", 0);
            saves.setInt("cardRngCounter", 0);
            saves.setInt("eliteCount", 0);
            saves.setInt("eliteLastFloor", -1);
            saves.setInt("merchantRngCounter", 0);
            saves.setInt("wheelGameLastFloor", -1);
            saves.setInt("matchGameLastFloor", -1);
            saves.setInt("merchantRngLastFloor", -1);
            saves.setInt("treasureCounter", 0);
            saves.setInt("treasureLastFloor", -1);
            saves.setFloat("totalTime", 0);
            saves.setFloat("correctTime", 0);
            saves.setString("lastStartTime", Long.toString(System.currentTimeMillis()));
            saves.setFloat("lastPlayTime", 0.0F);
            TopPanelPatch.correct = 0.0F;
            TopPanelPatch.PatchUpdate.defeatedHeart = false;
            resetSlCountersForAct(AbstractDungeon.actNum);

            saves.setInt("EventRngCountLast", -1);
            saves.setString("EventResultLast", "");
            WheelOptions.PROXY.onStartGame();
            WheelOptions.PROXY.onLoadSave();
            try {
                saves.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MonsterHelperPatch.encounterMap.clear();
        }
        ensureSlBlights();
        purgeCardPool();
        mm.receiveStartGame();
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
        final float settingAnchorX = 350.0F;
        final float defaultA15Y = 725.0F;
        final float dropdownRowGap = 100.0F;
        final float slPerActY = defaultA15Y - dropdownRowGap;
        final float cardBanLabelY = 655.0F;
        ModLabel cardBanLabel = new ModLabel(uiStrings.TEXT[4], settingAnchorX, cardBanLabelY, settingsPanel, modLabel -> {});
        ModLabeledToggleButton defaultA15 = new ModLabeledToggleButton(uiStrings.TEXT[3], settingAnchorX, defaultA15Y, Color.WHITE, FontHelper.buttonLabelFont, defaultA15Option, settingsPanel, (me) -> {},
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
        DropdownSetting slPerActDropdown = new DropdownSetting(settingAnchorX, slPerActY, uiStrings.TEXT[5], new String[]{"1", "2", "3", "5", "999"}, settingsPanel, selectedIndex -> {
            int chosen = SL_LIMIT_OPTIONS[Math.min(Math.max(selectedIndex, 0), SL_LIMIT_OPTIONS.length - 1)];
            if (chosen != maxSLCombatTimes || chosen != maxSLEventTimes) {
                maxSLCombatTimes = chosen;
                maxSLEventTimes = chosen;
                saves.setInt("maxSLCombatTimes", maxSLCombatTimes);
                saves.setInt("maxSLEventTimes", maxSLEventTimes);
                try {
                    saves.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resetSlCountersForAct(slActLastUpdated == 0 ? 1 : slActLastUpdated);
            }
        }, () -> !subMenu.menuHidden);
        settingsPanel.addUIElement(cardBanLabel);
        settingsPanel.addUIElement(defaultA15);
        settingsPanel.addUIElement(subMenu);

        Texture badgeTexture = ImageMaster.loadImage("cfcImages/ui/badge.png");
        BaseMod.registerModBadge(badgeTexture, "CFC Racing Mod", "Temple9", "todo", settingsPanel);
        BaseMod.addSaveField("cfc:SLCheck", new SaveLoadCheck());
        BaseMod.addSaveField("cfc:encounterPool", new MonsterHelperPatch());
        if (saves.has("defaultA15Option")) {
            defaultA15Option = saves.getBool("defaultA15Option");
        }
        if (saves.has("maxSLCombatTimes")) {
            maxSLCombatTimes = saves.getInt("maxSLCombatTimes");
        } else {
            saves.setInt("maxSLCombatTimes", maxSLCombatTimes);
        }
        if (saves.has("maxSLEventTimes")) {
            maxSLEventTimes = saves.getInt("maxSLEventTimes");
        } else {
            saves.setInt("maxSLEventTimes", maxSLEventTimes);
        }
        slPerActDropdown.setSelectedIndex(getSlLimitIndex(maxSLCombatTimes));
        loadSlCountersFromSaves();
        mm.receivePostInitialize();
    }

    @Override
    public void receiveStartAct() {
        ensureSlBlights();
        purgeCardPool();
        mm.receiveStartAct();
    }

    @Override
    public void receiveEditStrings() {
        String language = Settings.language.name().toLowerCase(Locale.ROOT);
        String uiStrings = Gdx.files.internal("localizations/" + language + "/CFCRacing-UIStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
        String blightStrings = Gdx.files.internal("localizations/" + language + "/CFCRacing-BlightStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(BlightStrings.class, blightStrings);
        String runModStrings = Gdx.files.internal("localizations/" + language + "/CFCRacing-RunModStrings.json").readString("UTF-8");
        BaseMod.loadCustomStrings(com.megacrit.cardcrawl.localization.RunModStrings.class, runModStrings);
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
        effectList.forEach(AbstractGameEffect::update);
        effectList.removeIf(e -> e.isDone);
        mm.receivePostUpdate();
    }

    public static void loadSlCountersFromSaves() {
        if (saves.has("slInCombatRemaining")) {
            slInCombatRemaining = saves.getInt("slInCombatRemaining");
        } else {
            slInCombatRemaining = maxSLCombatTimes;
            saves.setInt("slInCombatRemaining", slInCombatRemaining);
        }
        if (saves.has("slOutCombatRemaining")) {
            slOutCombatRemaining = saves.getInt("slOutCombatRemaining");
        } else {
            slOutCombatRemaining = maxSLEventTimes;
            saves.setInt("slOutCombatRemaining", slOutCombatRemaining);
        }
        if (saves.has("slActLastUpdated")) {
            slActLastUpdated = saves.getInt("slActLastUpdated");
        }
    }

    public static void persistSlCounters() {
        saves.setInt("slInCombatRemaining", slInCombatRemaining);
        saves.setInt("slOutCombatRemaining", slOutCombatRemaining);
        saves.setInt("slActLastUpdated", slActLastUpdated);
        try {
            saves.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetSlCountersForAct(int actNum) {
        int act = Math.max(1, actNum);
        slInCombatRemaining = maxSLCombatTimes;
        slOutCombatRemaining = maxSLEventTimes;
        slActLastUpdated = act;
        persistSlCounters();
        syncSlBlights();
    }

    public static void refreshSlCountersForAct() {
        loadSlCountersFromSaves();
        syncSlBlights();
    }

    public static void syncSlBlights() {
        if (AbstractDungeon.player == null) return;
        if (AbstractDungeon.player.hasBlight(SLinBattle.ID)) {
            AbstractDungeon.player.getBlight(SLinBattle.ID).counter = slInCombatRemaining;
            AbstractDungeon.player.getBlight(SLinBattle.ID).updateDescription();
        }
        // 如果选择了 HALF_SL 转盘选项，不同步事件SL的Blight（因为它不会被显示）
        if (!WheelOptions.isHalfSlActive() && AbstractDungeon.player.hasBlight(SLoutBattle.ID)) {
            AbstractDungeon.player.getBlight(SLoutBattle.ID).counter = slOutCombatRemaining;
            AbstractDungeon.player.getBlight(SLoutBattle.ID).updateDescription();
        }
    }

    public static int getSlLimitIndex(int value) {
        for (int i = 0; i < SL_LIMIT_OPTIONS.length; i++) {
            if (SL_LIMIT_OPTIONS[i] == value) return i;
        }
        return 0;
    }

    public static void handleActChangeIfNeeded() {
        if (AbstractDungeon.player == null) return;
        loadSlCountersFromSaves();
        if (AbstractDungeon.actNum > slActLastUpdated) {
            resetSlCountersForAct(AbstractDungeon.actNum);
            ensureSlBlights();
        } else {
            syncSlBlights();
        }
    }

    public static void ensureSlBlights() {
        if (AbstractDungeon.player == null) return;
        if (!AbstractDungeon.player.hasBlight(SLinBattle.ID)) {
            SLinBattle slIn = new SLinBattle();
            slIn.instantObtain(AbstractDungeon.player, AbstractDungeon.player.blights.size(), true);
        }
        // 如果选择了 HALF_SL 转盘选项，不显示事件SL的Blight
        if (!WheelOptions.isHalfSlActive()) {
            if (!AbstractDungeon.player.hasBlight(SLoutBattle.ID)) {
                SLoutBattle slOut = new SLoutBattle();
                slOut.instantObtain(AbstractDungeon.player, AbstractDungeon.player.blights.size(), true);
            }
        } else {
            // 如果已经有了这个Blight，移除它
            if (AbstractDungeon.player.hasBlight(SLoutBattle.ID)) {
                AbstractDungeon.player.blights.removeIf(blight -> blight.blightID.equals(SLoutBattle.ID));
            }
        }
        syncSlBlights();
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
        return cntA >= 3 && cntS >= 3 && cntP >= 3;//药水等
    }

    @Override
    public void receivePostRender(SpriteBatch sb) {
        effectList.forEach(e -> e.render(sb));
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        mm.receiveRender(sb);
    }

    public void receiveAddAudio() {
        mm.receiveAddAudio();
    }
}
