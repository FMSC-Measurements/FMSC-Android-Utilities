package com.usda.fmsc.android.widget;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usda.fmsc.android.R;

public class RecyclerViewEx<VH extends RecyclerView.ViewHolder> extends RecyclerView {
    private boolean hasHeader, hasFooter;
    private AdapterEx<VH> adapter;


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

        if (adapter != null) {
            adapter.setViewHasHeader(hasHeader);
        }
    }

    public boolean hasHeader() {
        return this.hasHeader;
    }

    public void setViewHasFooter(boolean hasFooter) {
        this.hasFooter = hasFooter;

        if (adapter != null) {
            adapter.setViewHasFooter(hasHeader);
        }
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

    public void setAdapter(AdapterEx<VH> adapter) {
        super.setAdapter(adapter);

        this.adapter = adapter;
        this.adapter.setViewHasHeader(hasHeader);
        this.adapter.setViewHasFooter(hasFooter);
    }



    public static abstract class AdapterEx<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
        public static final int HEADER = Integer.MAX_VALUE;
        public static final int FOOTER = Integer.MIN_VALUE;
        public static final int INVALID_TYPE = 0;

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


        @NonNull
        @Override
        public final VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case HEADER: return onCreateHeaderViewHolder(parent);
                case FOOTER: return onCreateFooterViewHolder(parent);
                default: return onCreateViewHolderEx(parent, viewType);
            }
        }

        public abstract VH onCreateViewHolderEx(ViewGroup parent, int viewType);

        public abstract VH onCreateHeaderViewHolder(ViewGroup parent);

        public abstract VH onCreateFooterViewHolder(ViewGroup parent);


        @Override
        public final void onBindViewHolder(@NonNull VH holder, int position) {
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


        void setViewHasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
        }

        public boolean hasHeader() {
            return this.hasHeader;
        }

        void setViewHasFooter(boolean hasFooter) {
            this.hasFooter = hasFooter;
        }

        public boolean hasFooter() {
            return hasFooter;
        }
    }


    public abstract static class BaseAdapterEx extends RecyclerViewEx.AdapterEx<ViewHolderEx> {
        public BaseAdapterEx(Context context) {
            super(context);
        }

        @Override
        public int getItemViewTypeEx(int position) {
            return INVALID_TYPE;
        }

        @Override
        public ViewHolderEx onCreateFooterViewHolder(ViewGroup parent) {
            return new ViewHolderEx(inflater.inflate(R.layout.rv_footer, parent, false));
        }

        @Override
        public ViewHolderEx onCreateHeaderViewHolder(ViewGroup parent) {
            return new ViewHolderEx(inflater.inflate(R.layout.rv_header, parent, false));
        }
    }

    public static class ViewHolderEx extends RecyclerViewEx.ViewHolder {
        public ViewHolderEx(View itemView) {
            super(itemView);
        }
    }
}
