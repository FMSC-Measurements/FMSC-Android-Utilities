package com.usda.fmsc.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.UiThread;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.usda.fmsc.android.R;

import java.util.ArrayList;

@UiThread
public class LinearLayoutEx extends LinearLayout {
    private int maxWidth, maxHeight;


    public LinearLayoutEx(Context context) {
        this(context, null, 0);
    }

    public LinearLayoutEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayoutEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LinearLayoutEx);
        maxWidth = a.getDimensionPixelSize(R.styleable.LinearLayoutEx_layoutMaxWidth, 0);
        maxHeight = a.getDimensionPixelSize(R.styleable.LinearLayoutEx_layoutMaxHeight, 0);
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Adjust width as necessary
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        if(maxWidth > 0 && maxWidth < measuredWidth) {
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, measureMode);
        }

        // Adjust height as necessary
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        if(maxHeight > 0 && maxHeight < measuredHeight) {
            int measureMode = MeasureSpec.getMode(heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, measureMode);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public ArrayList<View> getChildren() {
        ArrayList<View> children = new ArrayList<>();

        for (int i = 0; i < getChildCount(); i++) {
            children.add(getChildAt(i));
        }

        return children;
    }
}
