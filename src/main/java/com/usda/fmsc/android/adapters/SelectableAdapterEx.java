package com.usda.fmsc.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.usda.fmsc.android.widget.RecyclerViewEx;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public abstract class SelectableAdapterEx<T, VH extends SelectableAdapterEx.SelectableViewHolderEx> extends RecyclerViewEx.AdapterEx<VH> {
    public enum SelectionMode {
        Single,
        Multi
    }

    private final List<T> items;
    private final SelectionMode mode;
    private int selectedIndex;
    private final ArrayList<Integer> selectedIndicies;
    private Listener<T> listener;


    public SelectableAdapterEx(Context context, List<T> items) {
        this(context, items, SelectionMode.Single);
    }

    public SelectableAdapterEx(Context context, List<T> items, SelectionMode mode) {
        super(context);

        if (items == null) {
            throw new IllegalArgumentException();
        }

        this.items = items;
        this.mode = mode;
        selectedIndex = -1;
        selectedIndicies = new ArrayList<>();
    }


    @Override
    public int getItemCountEx() {
        return items.size();
    }

    public T getItem(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IndexOutOfBoundsException();
        }

        return items.get(index);
    }


    public void add(T item) {
        add(item, true);
    }

    public synchronized void add(T item, boolean notify) {
        items.add(item);

        if (notify)
            notifyItemInserted(items.size());
    }

    public void add(int index, T item) {
        add(index, item, true);
    }

    public synchronized void add(int index, T item, boolean notify) {
        items.add(index, item);

        if (notify)
            notifyItemInserted(index);
    }


    public void remove(T item) {
        remove(item, true);
    }

    public synchronized void remove(T item, boolean notify) {
        int index = items.indexOf(item);

        if (index >= 0) {
            items.remove(item);

            if (notify)
                notifyItemRemoved(index);
        }
    }

    public T remove(int index) {
        return remove(index, true);
    }

    public synchronized T remove(int index, boolean notify) {
        if (index < 0 || index >= items.size()) {
            throw new IndexOutOfBoundsException();
        }

        T item = items.remove(index);

        if (notify)
            notifyItemRemoved(index);

        return item;
    }


    public void clear() {
        clear(true);
    }

    public synchronized void clear(boolean notify) {
        items.clear();

        if (notify)
            notifyDataSetChanged();
    }


    public List<T> getItems() {
        return items;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolderEx(VH holder, int position) {
        T item = getItem(position);

        holder.onBind(item);
        onBindViewHolderEx(holder, item, position);

        holder.selectHolder(position == selectedIndex);
    }

    protected void onBindViewHolderEx(VH holder, T item, int position) {

    }


    private void onItemSelected(SelectableViewHolderEx holder, boolean selected) {
        if (selected) {
            if (mode == SelectionMode.Single) {
                selectedIndex = holder.getAdapterPosition();

                if (listener != null)
                    listener.onItemSelected(holder.getItem(), selectedIndex, holder.getLayoutPosition());
            } else {
                Integer index = holder.getAdapterPosition();
                if (index > -1 && !selectedIndicies.contains(index)) {
                    selectedIndicies.add(index);

                    if (listener != null)
                        listener.onItemSelected(holder.getItem(), index, holder.getLayoutPosition());
                }
            }

            notifyDataSetChanged();
        } else {
            if (mode == SelectionMode.Single) {
                selectedIndex = -1;
            } else {
                Integer index = holder.getBindingAdapterPosition();
                if (index > -1) {
                    selectedIndicies.remove(index);
                }
            }
        }
    }


    public void selectItem(T item) {
        int index = items.indexOf(item);

        if (index > -1) {
            if (mode == SelectionMode.Single) {
                selectedIndex = index;
                notifyDataSetChanged();
            } else {
                if (!selectedIndicies.contains(index)) {
                    selectedIndicies.add(index);
                    notifyDataSetChanged();
                }
            }
        }
    }

    public void selectItem(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IndexOutOfBoundsException();
        }

        if (mode == SelectionMode.Single) {
            selectedIndex = index;
            notifyDataSetChanged();
        } else {
            if (!selectedIndicies.contains(index)) {
                selectedIndicies.add(index);
                notifyDataSetChanged();
            }
        }
    }

    public void deselectItems() {
        selectedIndex = -1;
        selectedIndicies.clear();
        notifyDataSetChanged();
    }

    public void deselectItem(int index) {
        if (mode == SelectionMode.Single) {
            if (selectedIndex == index) {
                selectedIndex = -1;
                notifyDataSetChanged();
            }
        } else {
            if (selectedIndicies.contains(index)) {
                selectedIndicies.remove(index);
                notifyDataSetChanged();
            }
        }
    }


    public void setListener(Listener<T> listener) {
        this.listener = listener;
    }



    public abstract class SelectableViewHolderEx extends RecyclerViewEx.ViewHolder {
        private T item;

        public SelectableViewHolderEx(View itemView) {
            super(itemView);
        }

        protected final void selectHolder(boolean select) {
            onViewSelected(select);
        }

        public final void select() {
            onViewSelected(true);
            onItemSelected(this, true);
        }

        public final void deselect() {
            onViewSelected(false);
            onItemSelected(this, false);
        }

        protected abstract void onViewSelected(boolean selected);

        public final void onBind(T item) {
            this.item = item;
            onBindView(item);
        }

        public abstract void onBindView(T item);

        public T getItem() {
            return item;
        }
    }

    public class SelectableViewHolderExHeaderFooter extends SelectableViewHolderEx {

        public SelectableViewHolderExHeaderFooter(View itemView) {
            super(itemView);
        }

        @Override
        protected void onViewSelected(boolean selected) {

        }

        @Override
        public void onBindView(T item) {

        }
    }



    public interface Listener<T> {
         void onItemSelected(T item, int adapterPosition, int layoutPosition);
    }
}

