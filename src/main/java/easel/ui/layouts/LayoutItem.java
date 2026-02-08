package easel.ui.layouts;

import easel.ui.AbstractWidget;
import easel.ui.AnchorPosition;

class LayoutItem {
   AbstractWidget widget;
   AnchorPosition anchor;

   public LayoutItem(AbstractWidget widget, AnchorPosition anchor) {
     this.widget = widget;
     this.anchor = anchor;
   }
}
