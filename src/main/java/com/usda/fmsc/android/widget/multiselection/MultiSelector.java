package com.usda.fmsc.android.widget.multiselection;

import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

public class MultiSelector {
    private List<Listener> _Listeners;

    private SparseBooleanArray mSelections = new SparseBooleanArray();
    private WeakHolderTracker mTracker = new WeakHolderTracker();

    private boolean mIsSelectable;

    private int mSelectionCount = 0;


    public MultiSelector() {
        this(null);
    }

    public MultiSelector(Listener listener) {
        _Listeners = new ArrayList<>();
        addListener(listener);
    }

    public void setSelectable(boolean isSelectable) {
        mIsSelectable = isSelectable;
        refreshAllHolders();
    }

    public boolean isSelectable() {
        return mIsSelectable;
    }

    private void refreshAllHolders() {
        for (SelectableHolder holder : mTracker.getTrackedHolders()) {
            refreshHolder(holder);
        }
    }

    private void refreshHolder(SelectableHolder holder) {
        if (holder == null) {
            return;
        }

        holder.setSelectable(mIsSelectable);
        holder.setActivated(mSelections.get(holder.getPosition()));
    }

    public boolean isSelected(int position, long id) {
        return mSelections.get(position);
    }

    public void setSelected(int position, long id, boolean isSelected) {
        boolean oldSelection = isSelected(position, id);
        mSelections.put(position, isSelected);

        SelectableHolder holder = mTracker.getHolder(position);
        refreshHolder(holder);

        if (oldSelection ^ isSelected) {
            if (isSelected) {
                mSelectionCount++;
            } else {
                mSelectionCount--;
            }

            onItemSelectionChange(holder, isSelected);
        }
    }

    public void clearSelections() {
        mSelections.clear();
        mSelectionCount = 0;
        onClearSelections();
        refreshAllHolders();
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i < mSelections.size(); i++) {
            if (mSelections.valueAt(i)) {
                positions.add(mSelections.keyAt(i));
            }
        }

        return positions;
    }

    public boolean hasSelections() {
        for (int i = 0; i < mSelections.size(); i++) {
            if (mSelections.valueAt(i)) {
                return true;
            }
        }

        return false;
    }

    public int getSelectionCount() {
        return mSelectionCount;
    }

    public void bindHolder(SelectableHolder holder, int position, long id) {
        mTracker.bindHolder(holder, position);
        refreshHolder(holder);
    }

    public void setSelected(SelectableHolder holder, boolean isSelected) {
        setSelected(holder.getPosition(), holder.getItemId(), isSelected);
    }

    public boolean tapSelection(SelectableHolder holder) {
        return tapSelection(holder.getPosition(), holder.getItemId());
    }

    private boolean tapSelection(int position, long itemId) {
        if (mIsSelectable) {
            boolean isSelected = isSelected(position, itemId);
            setSelected(position, itemId, !isSelected);
            return true;
        } else {
            return false;
        }

    }


    private void onItemSelectionChange(SelectableHolder holder, boolean isSelcted) {
        for (Listener listener : _Listeners) {
            listener.onItemSelectionChange(holder, isSelcted);
        }
    }
    private void onClearSelections() {
        for (Listener listener : _Listeners) {
            listener.onClearSelections();
        }
    }


    public final void addListener(Listener listener) {
        if (listener != null && !_Listeners.contains(listener)) {
            _Listeners.add(listener);
        }
    }

    public final boolean removeListener(Listener listener) {
        return _Listeners.remove(listener);
    }


    public interface Listener {
        void onItemSelectionChange(SelectableHolder holder, boolean isSelected);
        void onClearSelections();
    }
}
