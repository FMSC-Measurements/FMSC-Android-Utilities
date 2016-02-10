package com.usda.fmsc.android.widget.multiselection;

import android.view.View;

public class SimpleMultiSelectorBindingHolder extends MultiSelectorBindingHolder {
    boolean selectable, activated;

    public SimpleMultiSelectorBindingHolder(View itemView, MultiSelector multiSelector) {
        super(itemView, multiSelector);
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public boolean isActivated() {
        return activated;
    }
}
