package com.usda.fmsc.android.widget.listeners;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;

public abstract class AdvancedDrawerListener implements DrawerLayout.DrawerListener {
    private final DrawerLayout drawerLayout;
    boolean leftDrawerOpened, rightDrawerOpened;

    public AdvancedDrawerListener(DrawerLayout layout) {
        this.drawerLayout = layout;

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            leftDrawerOpened = true;
        } else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            rightDrawerOpened = true;
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            leftDrawerOpened = true;
            onLeftDrawerOpened(drawerView);
        } else {
            rightDrawerOpened = true;
            onRightDrawerOpened(drawerView);
        }
    }

    public void onLeftDrawerOpened(View drawerView) {

    }

    public void onRightDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START) && leftDrawerOpened) {
            leftDrawerOpened = false;
            onLeftDrawerClosed(drawerView);
        } else if (!drawerLayout.isDrawerOpen(GravityCompat.END) && rightDrawerOpened) {
            rightDrawerOpened = false;
            onRightDrawerClosed(drawerView);
        }
    }

    public void onLeftDrawerClosed(View drawerView) {

    }

    public void onRightDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
