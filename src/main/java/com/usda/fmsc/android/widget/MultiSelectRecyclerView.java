package com.usda.fmsc.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.usda.fmsc.android.R;
import com.usda.fmsc.android.widget.multiselection.MultiSelector;
import com.usda.fmsc.android.widget.multiselection.SelectableHolder;

public class MultiSelectRecyclerView extends RecyclerViewEx {

    public MultiSelectRecyclerView(Context context) {
        super(context);
    }

    public MultiSelectRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiSelectRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public static abstract class MSAdapter<VH extends MSViewHolder> extends RecyclerViewEx.AdapterEx<VH> {
        private final MultiSelector multiSelector;

        public MSAdapter(Context context, MultiSelector multiSelector) {
            super(context);
            this.multiSelector = multiSelector;
        }

        public MultiSelector getMultiSelector() {
            return multiSelector;
        }

        @Override
        public void onBindViewHolderEx(VH holder, int position) {
            holder.bind();
        }
    }

    public abstract static class BaseAdapter extends MSAdapter<MSViewHolder> {

        public BaseAdapter(Context context, MultiSelector multiSelector) {
            super(context, multiSelector);
        }

        @Override
        public int getItemViewTypeEx(int position) {
            return INVALID_TYPE;
        }

        @Override
        public MSViewHolder onCreateFooterViewHolder(ViewGroup parent) {
            return new MSViewHolder(inflater.inflate(R.layout.rv_footer, parent, false), false);
        }

        @Override
        public MSViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            return new MSViewHolder(inflater.inflate(R.layout.rv_header, parent, false), false);
        }
    }

    public static class MSViewHolder extends MultiSelectRecyclerView.ViewHolderEx implements SelectableHolder {
        private boolean activated, isSelected, isSelectable = true;
        private MultiSelector multiSelector;

        public MSViewHolder(View itemView, MultiSelector multiSelector) {
            super(itemView);

            this.multiSelector = multiSelector;

            if (this.multiSelector == null) {
                isSelectable = false;
            }
        }

        public MSViewHolder(View itemView, boolean isSelectable) {
            super(itemView);

            this.isSelected = isSelectable;
        }

        public void bind() {
            multiSelector.bindHolder(this, getBindingAdapterPosition(), getItemId());
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
            multiSelector.setSelected(this, selected);
        }

        public boolean isSelectable() {
            return isSelectable;
        }

        public void setSelectable(boolean selectable) {
            isSelectable = selectable;
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
}
