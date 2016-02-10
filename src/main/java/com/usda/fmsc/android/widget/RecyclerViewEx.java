package com.usda.fmsc.android.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerViewEx extends RecyclerView {

    private boolean hasHeader, hasFooter;


    public RecyclerViewEx(Context context) {
        super(context);
    }

    public RecyclerViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setViewHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public boolean hasHeader() {
        return this.hasHeader;
    }

    public void setViewHashFooter(boolean hasFooter) {
        this.hasFooter = hasFooter;
    }

    public boolean hasFooter() {
        return hasFooter;
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(hasHeader ? ++position : position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(hasHeader ? ++position : position);
    }

    public void setAdapter(AdapterEx adapter) {
        super.setAdapter(adapter);

        setViewHasHeader(adapter.hasHeader());
        setViewHashFooter(adapter.hasFooter());
    }



    public static abstract class AdapterEx<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
        public final int HEADER = -1;
        public final int FOOTER = -2;
        public final int INVALID_TYPE = 0;

        private boolean hasHeader, hasFooter;

        protected LayoutInflater inflater;


        public AdapterEx(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public final int getItemViewType(int position) {
            if (hasHeader && position == 0) return HEADER;
            if (hasFooter && position == getItemCount() - 1) return FOOTER;

            return getItemViewTypeEx(hasHeader ? --position : position);
        }

        /**
         * Gets the item view type. Can not be -1 (Header) or -2 (Footer).
         *
         * @param position The position of the item within the adapter's data set.
         */
        public abstract int getItemViewTypeEx(int position);


        @Override
        public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case HEADER: return onCreateHeaderViewHolder(parent);
                case FOOTER: return onCreateFooterViewHolder(parent);
                default: return onCreateViewHolderEx(parent, viewType);
            }
        }

        public abstract VH onCreateViewHolderEx(ViewGroup parent, int viewType);

        public VH onCreateHeaderViewHolder(ViewGroup parent) {
            return null;
        }

        public VH onCreateFooterViewHolder(ViewGroup parent) {
            return null;
        }


        @Override
        public final void onBindViewHolder(VH holder, int position) {
            if (hasHeader && position == 0) {
                onBindHeaderViewHolder(holder);
            } else if (hasFooter && position == getItemCount() - 1) {
                onBindFooterViewHolder(holder);
            } else {
                onBindViewHolderEx(holder, hasHeader ? --position : position);
            }
        }

        public abstract void onBindViewHolderEx(VH holder, int position);

        public void onBindHeaderViewHolder(VH holder) {

        }

        public void onBindFooterViewHolder(VH holder) {

        }


        @Override
        public final long getItemId(int position) {
            if (hasHeader && position == 0) return HEADER;
            if (hasFooter && position == getItemCount() - 1) return FOOTER;

            return getItemIdEx(hasHeader ? --position : position);
        }

        public long getItemIdEx(int position) {
            return super.getItemId(position);
        }


        @Override
        public final int getItemCount() {
            return getItemCountEx() + (hasHeader ? 1 : 0) + (hasFooter ? 1 : 0);
        }

        public abstract int getItemCountEx();


        public void setViewHasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
        }

        public boolean hasHeader() {
            return this.hasHeader;
        }

        public void setViewHasFooter(boolean hasFooter) {
            this.hasFooter = hasFooter;
        }

        public boolean hasFooter() {
            return hasFooter;
        }
    }


    public static class ViewHolderEx extends RecyclerViewEx.ViewHolder {
        public ViewHolderEx(View itemView) {
            super(itemView);
        }
    }
}
