package easel.config.samples;

import easel.config.enums.ConfigStringEnum;

public enum SampleStringChoices
   implements ConfigStringEnum
{
   STRING_ONE("one"),
   STRING_TWO("two");

   String defaultValue;

   SampleStringChoices(String defaultValue) {
     this.defaultValue = defaultValue;
   }
   public String getDefault() {
     return this.defaultValue;
   }
}
