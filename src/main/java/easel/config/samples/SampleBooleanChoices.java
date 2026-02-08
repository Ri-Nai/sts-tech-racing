package easel.config.samples;

import easel.config.enums.ConfigBooleanEnum;

public enum SampleBooleanChoices
   implements ConfigBooleanEnum
{
   BOOL_ONE(true),
   BOOL_TWO(false);

   boolean defaultValue;

   SampleBooleanChoices(boolean defaultValue) {
     this.defaultValue = defaultValue;
   }
   public boolean getDefault() {
     return this.defaultValue;
   }
}
