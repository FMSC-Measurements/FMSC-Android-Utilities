package com.usda.fmsc.android.widget.multiselection;

public interface SelectableHolder {
    void setSelectable(boolean selectable);
    boolean isSelectable();
    void setActivated(boolean activated);
    boolean isActivated();
    int getPosition();
    long getItemId();
}
