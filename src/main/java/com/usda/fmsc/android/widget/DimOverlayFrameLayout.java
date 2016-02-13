package com.usda.fmsc.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.usda.fmsc.android.R;

public class DimOverlayFrameLayout extends FrameLayout {

	public DimOverlayFrameLayout(Context context) {
		super(context);
		init();
	}

	public DimOverlayFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DimOverlayFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.dim_overlay, this);
	}
}
