package demoMod.cfcracing.entity;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

public class CardSetting {
    public String settingsId;
    public String name;
    public boolean isDisabled;

    public CardSetting(String id, String name) {
        this.settingsId = id;
        this.name = name;
    }

    public void toggleDisabled() {
        this.isDisabled = !this.isDisabled;
    }

    public void loadFromData(SpireConfig config) {
        this.isDisabled = config.getBool(this.settingsId);
    }

    public void saveToData(SpireConfig config) {
        config.setBool(this.settingsId, this.isDisabled);
    }
}
