package com.usda.fmsc.android.widget.multiselection;

import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class WeakHolderTracker {
    private final SparseArray<WeakReference<SelectableHolder>> mHoldersByPosition = new SparseArray<>();

    /**
     * Returns the holder with a given position. If non-null, the returned
     * holder is guaranteed to have getPosition() == position.
     *
     * @param position
     * @return
     */
    public SelectableHolder getHolder(int position) {
        WeakReference<SelectableHolder> holderRef = mHoldersByPosition.get(position);
        if (holderRef == null) {
            return null;
        }

        SelectableHolder holder = holderRef.get();
        if (holder == null || holder.getPosition() != position) {
            mHoldersByPosition.remove(position);
            return null;
        }

        return holder;
    }

    public void bindHolder(SelectableHolder holder, int position) {
        mHoldersByPosition.put(position, new WeakReference<>(holder));
    }

    public List<SelectableHolder> getTrackedHolders() {
        List<SelectableHolder> holders = new ArrayList<>();

        for (int i = 0; i < mHoldersByPosition.size(); i++) {
            int key = mHoldersByPosition.keyAt(i);
            SelectableHolder holder = getHolder(key);

            if (holder != null) {
                holders.add(holder);
            }
        }

        return holders;
    }
}
