package com.usda.fmsc.android.adapters;


import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.usda.fmsc.android.widget.RecyclerViewPager;

/**
 * RecyclerViewPagerAdapter </br>
 * Adapter wrapper. Use to add margin at first and last itemView.
 */
public class RecyclerViewPagerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final RecyclerViewPager mViewPager;
    private RecyclerView.Adapter<VH> mAdapter;


    public RecyclerViewPagerAdapter(RecyclerViewPager viewPager, RecyclerView.Adapter<VH> adapter) {
        mAdapter = adapter;
        mViewPager = viewPager;
        setHasStableIds(mAdapter.hasStableIds());
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        mAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        mAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        mAdapter.onBindViewHolder(holder, position);
        final View itemView = holder.itemView;
        ViewGroup.LayoutParams lp = itemView.getLayoutParams() == null ? new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) : itemView.getLayoutParams();
        if (mViewPager.getLayoutManager().canScrollHorizontally()) {
            lp.width = mViewPager.getWidth() - mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
        } else {
            lp.height = mViewPager.getHeight() - mViewPager.getPaddingTop() - mViewPager.getPaddingBottom();
        }
        itemView.setLayoutParams(lp);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        mAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    public RecyclerViewPagerAdapter getAdapter() {
        return this;
    }
}
