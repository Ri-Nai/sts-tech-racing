package easel.config.samples;

import easel.config.enums.ConfigIntegerEnum;

public enum SampleIntegerChoices
   implements ConfigIntegerEnum
{
   INT_ONE(1),
   INT_TWO(2);

   int defaultValue;

   SampleIntegerChoices(int defaultValue) {
     this.defaultValue = defaultValue;
   }
   public int getDefault() {
     return this.defaultValue;
   }
}
