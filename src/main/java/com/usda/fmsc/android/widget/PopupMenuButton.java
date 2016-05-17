package com.usda.fmsc.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.MenuRes;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.usda.fmsc.android.AndroidUtils;
import com.usda.fmsc.android.R;

public class PopupMenuButton extends ImageButton implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private PopupMenu.OnMenuItemClickListener listener;
    private PopupMenu popupMenu;

    public PopupMenuButton(Context context) {
        this(context, null, 0);
    }

    public PopupMenuButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupMenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public PopupMenuButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOnClickListener(this);
        setBackground(null);
        popupMenu = new PopupMenu(context, this);
        AndroidUtils.UI.addIconsToPopupMenu(popupMenu);
        popupMenu.setOnMenuItemClickListener(this);

        if (attrs != null) {
            TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.PopupMenuButton, 0, 0);
            try {
                int res = attrArray.getResourceId(R.styleable.PopupMenuButton_menuRes, 0);

                if (res != 0) {
                    setMenu(res);
                }
            } finally {
                attrArray.recycle();
            }
        }
    }


    public void setListener(PopupMenu.OnMenuItemClickListener listener) {
        this.listener = listener;
    }


    public void showPopupMenu() {
        if (popupMenu.getMenu() != null) {
            popupMenu.show();
        }
    }

    public void dismissPopupMenu() {
        if (popupMenu.getMenu() != null) {
            popupMenu.dismiss();
        }
    }


    public void setMenu(@MenuRes int res) {
        popupMenu.getMenuInflater().inflate(res, popupMenu.getMenu());
    }


    public PopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setItemEnabled(@IdRes int res, boolean enabled) {
        MenuItem item = getMenuItem(res);

        if (item != null) {
            item.setEnabled(enabled);
        }
    }

    public void setItemVisible(@IdRes int res, boolean enabled) {
        MenuItem item = getMenuItem(res);

        if (item != null) {
            item.setVisible(enabled);
        }
    }

    public void setItemIcon(@IdRes int res, Drawable drawable) {
        MenuItem item = getMenuItem(res);

        if (item != null) {
            item.setIcon(drawable);
        }
    }

    public void setItemIcon(@IdRes int res, @DrawableRes int drawableRes) {
        MenuItem item = getMenuItem(res);

        if (item != null) {
            item.setIcon(drawableRes);
        }
    }

    public MenuItem getMenuItem(@IdRes int res) {
        if (popupMenu.getMenu() != null)
            return popupMenu.getMenu().findItem(res);

        throw new RuntimeException("Menu not created");
    }

    @Override
    public void onClick(View v) {
        if (popupMenu.getMenu() != null) {
            popupMenu.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return listener != null && listener.onMenuItemClick(item);
    }


}
