package easel.config;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import easel.config.enums.ConfigBooleanEnum;
import easel.config.enums.ConfigIntegerEnum;
import easel.config.enums.ConfigStringEnum;
import java.io.IOException;
import java.util.HashMap;

public class EaselConfigHelper<B extends ConfigBooleanEnum, I extends ConfigIntegerEnum, S extends ConfigStringEnum>
{
   @SerializedName("booleans")
   private HashMap<String, Boolean> booleanMap = new HashMap<>();

   @SerializedName("integers")
   private HashMap<String, Integer> integerMap = new HashMap<>();

   @SerializedName("strings")
   private HashMap<String, String> stringMap = new HashMap<>();

   private final String modName;

   private final String configName;

   private EaselConfigHelper(String modName, String configName, Class<? extends B> booleans, Class<? extends I> integers, Class<? extends S> strings) {
     initializeBooleans(booleans);
     initializeIntegers(integers);
     initializeStrings(strings);

     this.modName = modName;
     this.configName = configName;

     load();
   }

   public static <B extends ConfigBooleanEnum> EaselConfigHelper<B, ConfigIntegerEnum, ConfigStringEnum> fromBooleansOnly(String modName, String configName, Class<? extends B> booleanClz) {
     return new EaselConfigHelper<>(modName, configName, booleanClz, ConfigIntegerEnum.class, ConfigStringEnum.class);
   }

   public static <I extends ConfigIntegerEnum> EaselConfigHelper<ConfigBooleanEnum, I, ConfigStringEnum> fromIntegersOnly(String modName, String configName, Class<? extends I> integerClz) {
     return new EaselConfigHelper<>(modName, configName, ConfigBooleanEnum.class, integerClz, ConfigStringEnum.class);
   }

   public static <S extends ConfigStringEnum> EaselConfigHelper<ConfigBooleanEnum, ConfigIntegerEnum, S> fromStringsOnly(String modName, String configName, Class<? extends S> stringClz) {
     return new EaselConfigHelper<>(modName, configName, ConfigBooleanEnum.class, ConfigIntegerEnum.class, stringClz);
   }

   public static <B extends ConfigBooleanEnum, I extends ConfigIntegerEnum> EaselConfigHelper<B, I, ConfigStringEnum> fromBooleansIntegers(String modName, String configName, Class<? extends B> booleanClz, Class<? extends I> integerClz) {
     return new EaselConfigHelper<>(modName, configName, booleanClz, integerClz, ConfigStringEnum.class);
   }

   public static <B extends ConfigBooleanEnum, S extends ConfigStringEnum> EaselConfigHelper<B, ConfigIntegerEnum, S> fromBooleansStrings(String modName, String configName, Class<? extends B> booleanClz, Class<? extends S> stringClz) {
     return new EaselConfigHelper<>(modName, configName, booleanClz, ConfigIntegerEnum.class, stringClz);
   }

   public static <I extends ConfigIntegerEnum, S extends ConfigStringEnum> EaselConfigHelper<ConfigBooleanEnum, I, S> fromIntegersStrings(String modName, String configName, Class<? extends I> integerClz, Class<? extends S> stringClz) {
     return new EaselConfigHelper<>(modName, configName, ConfigBooleanEnum.class, integerClz, stringClz);
   }

   public static <B extends ConfigBooleanEnum, I extends ConfigIntegerEnum, S extends ConfigStringEnum> EaselConfigHelper<B, I, S> fromBooleansIntegersStrings(String modName, String configName, Class<? extends B> booleanClz, Class<? extends I> integerClz, Class<? extends S> stringClz) {
     return new EaselConfigHelper<>(modName, configName, booleanClz, integerClz, stringClz);
   }

   private void initializeBooleans(Class<? extends B> booleans) {
     this.booleanMap = new HashMap<>();
     if (booleans.getEnumConstants() != null)
       for (ConfigBooleanEnum configBooleanEnum : (ConfigBooleanEnum[])booleans.getEnumConstants()) {
         this.booleanMap.put(configBooleanEnum.toString(), Boolean.valueOf(configBooleanEnum.getDefault()));
       }
   }

   private void initializeIntegers(Class<? extends I> integers) {
     this.integerMap = new HashMap<>();
     if (integers.getEnumConstants() != null)
       for (ConfigIntegerEnum configIntegerEnum : (ConfigIntegerEnum[])integers.getEnumConstants()) {
         this.integerMap.put(configIntegerEnum.toString(), Integer.valueOf(configIntegerEnum.getDefault()));
       }
   }

   private void initializeStrings(Class<? extends S> strings) {
     this.stringMap = new HashMap<>();

     if (strings.getEnumConstants() != null) {
       for (ConfigStringEnum configStringEnum : (ConfigStringEnum[])strings.getEnumConstants()) {
         this.stringMap.put(configStringEnum.toString(), configStringEnum.getDefault());
       }
     }
   }

   public boolean getBoolean(B choice) {
     return ((Boolean)this.booleanMap.getOrDefault(choice.toString(), Boolean.valueOf(choice.getDefault()))).booleanValue();
   }

   public boolean setBoolean(B choice, boolean value) {
     Boolean existing = this.booleanMap.get(choice.toString());

     if (existing == null || !existing.equals(Boolean.valueOf(value))) {
       this.booleanMap.put(choice.toString(), Boolean.valueOf(value));
       return save();
     }

     return false;
   }

   public int getInt(I choice) {
     return ((Integer)this.integerMap.getOrDefault(choice.toString(), Integer.valueOf(choice.getDefault()))).intValue();
   }

   public boolean setInt(I choice, int value) {
     Integer existing = this.integerMap.get(choice.toString());

     if (existing == null || !existing.equals(Integer.valueOf(value))) {
       this.integerMap.put(choice.toString(), Integer.valueOf(value));
       return save();
     }

     return false;
   }

   public String getString(S choice) {
     return this.stringMap.getOrDefault(choice.toString(), choice.getDefault());
   }

   public boolean setString(S choice, String value) {
     String existing = this.stringMap.get(choice.toString());

     if (existing == null || !existing.equals(value)) {
       this.stringMap.put(choice.toString(), value);
       return save();
     }

     return false;
   }

   public boolean setBooleanWithoutSaving(B choice, boolean value) {
     Boolean existing = this.booleanMap.get(choice.toString());

     if (existing == null || !existing.equals(Boolean.valueOf(value))) {
       this.booleanMap.put(choice.toString(), Boolean.valueOf(value));
       return true;
     }

     return false;
   }

   public boolean setIntWithoutSaving(I choice, int value) {
     Integer existing = this.integerMap.get(choice.toString());

     if (existing == null || !existing.equals(Integer.valueOf(value))) {
       this.integerMap.put(choice.toString(), Integer.valueOf(value));
       return true;
     }

     return false;
   }

   public boolean setStringWithoutSaving(S choice, String value) {
     String existing = this.stringMap.get(choice.toString());

     if (existing == null || !existing.equals(value)) {
       this.stringMap.put(choice.toString(), value);
       return true;
     }

     return false;
   }

   private void loadFromJsonString(String jsonString) {
     EaselConfigHelper other = (EaselConfigHelper)(new Gson()).fromJson(jsonString, EaselConfigHelper.class);

     if (other.booleanMap != null) {
       this.booleanMap = other.booleanMap;
     } else {
       this.booleanMap.clear();
     }
     if (other.integerMap != null) {
       this.integerMap = other.integerMap;
     } else {
       this.integerMap.clear();
     }
     if (other.stringMap != null) {
       this.stringMap = other.stringMap;
     } else {
       this.stringMap.clear();
     }
   }

   public String toString() {
     return (new Gson()).toJson(this);
   }

   public boolean save() {
     try {
       SpireConfig spireConfig = new SpireConfig(this.modName, this.configName);
       spireConfig.setString("json", toString());
       spireConfig.save();
       return true;
     }
     catch (IOException e) {
       e.printStackTrace();

       return false;
     }
   }

   public boolean load() {
     try {
       SpireConfig spireConfig = new SpireConfig(this.modName, this.configName);
       spireConfig.load();

       if (spireConfig.has("json")) {
         loadFromJsonString(spireConfig.getString("json"));
         return true;
       }

     } catch (IOException e) {
       e.printStackTrace();
     }

     return false;
   }

   public boolean resetBooleansToDefaults(Class<B> booleanClz) {
     initializeBooleans(booleanClz);
     return save();
   }

   public boolean resetIntegersToDefaults(Class<I> integerClz) {
     initializeIntegers(integerClz);
     return save();
   }

   public boolean resetStringsToDefaults(Class<S> stringClz) {
     initializeStrings(stringClz);
     return save();
   }

   public boolean resetAllToDefaults(Class<B> booleanClz, Class<I> integerClz, Class<S> stringClz) {
     boolean hasSaved = false;
     hasSaved = (hasSaved || resetBooleansToDefaults(booleanClz));
     hasSaved = (hasSaved || resetIntegersToDefaults(integerClz));
     hasSaved = (hasSaved || resetStringsToDefaults(stringClz));
     return hasSaved;
   }
}
