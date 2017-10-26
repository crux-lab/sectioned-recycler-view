package com.cruxlab.sectionedrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Base class for a SectionAdapter.
 * <p>
 * Provides a binding from an app-specific data set to views that are displayed within
 * a SectionedRecyclerView in an individual section.
 * <p>
 * Similar to {@link android.support.v7.widget.RecyclerView.Adapter}.
 *
 * @param <VH> A class that extends ItemViewHolder that will be used by the adapter to manage item views.
 */
public abstract class SectionAdapter<VH extends SectionAdapter.ItemViewHolder> {

    int section;
    SectionItemManager itemManager;

    /**
     * Returns the total number of items in the section data set held by the adapter.
     *
     * @return The total number of items in this section.
     */
    public abstract int getItemCount();

    /**
     * Called when SectionDataManager needs a new {@link SectionAdapter.ItemViewHolder} of the given
     * type to represent an item in this section.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}.
     *
     * @param parent The ViewGroup into which the new item view will be added after it is bound to
     *               an adapter position.
     * @param type   The view type of the new View.
     */
    public abstract VH onCreateViewHolder(ViewGroup parent, short type);

    /**
     * Called by SectionDataManager to display the data at the specified section position. This
     * method should update the contents of the {@link SectionAdapter.ViewHolder#itemView} to
     * reflect the item at the given position.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the section data set.
     * @param position The position of the item within the adapter's data set.
     */
    public abstract void onBindViewHolder(VH holder, int position);

    /**
     * Return the view type of the item within this section at <code>position</code> for the purposes
     * of view recycling.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}.
     *
     * @param position Position to query.
     * @return Short value identifying the type of the view needed to represent the item at
     *         <code>position</code> in this section. Type codes need not be contiguous.
     */
    public short getItemViewType(int position) {
        return 0;
    }

    /**
     * Notifies SectionDataManager that the item in this section at <code>pos</code> has been
     * inserted.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemInserted(int)}.
     *
     * @param pos Inserted item position.
     */
    public final void notifyItemInserted(int pos) {
        if (itemManager != null) {
            itemManager.notifyInserted(section, pos);
        }
    }

    /**
     * Notifies SectionDataManager that the items in this section in the given list of positions
     * have been inserted.
     *
     * @param posList Varargs of inserted positions.
     */
    public final void notifyItemInserted(int... posList) {
        for (int pos : posList) {
            notifyItemInserted(pos);
        }
    }

    /**
     * Notifies SectionDataManager that the items in this section in the given list of positions
     * have been inserted.
     *
     * @param posList List of inserted positions.
     */
    public final void notifyItemInserted(List<Integer> posList) {
        for (int pos : posList) {
            notifyItemInserted(pos);
        }
    }

    /**
     * Notifies SectionDataManager that <code>cnt</code> items starting at <code>startPos</code>
     * in this section have been inserted.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemRangeInserted(int, int)}.
     *
     * @param startPos Position of the first item that was inserted.
     * @param cnt      Number of items inserted.
     */
    public final void notifyItemRangeInserted(int startPos, int cnt) {
        if (itemManager != null) {
            itemManager.notifyRangeInserted(section, startPos, cnt);
        }
    }

    /**
     * Notifies SectionDataManager that the item in this section at <code>pos</code> has been
     * removed.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemRemoved(int)}.
     *
     * @param pos Removed item position.
     */
    public final void notifyItemRemoved(int pos) {
        if (itemManager != null) {
            itemManager.notifyRemoved(section, pos);
        }
    }

    /**
     * Notifies SectionDataManager that the items in this section in the given list of positions
     * have been removed.
     *
     * @param posList Varargs of removed positions.
     */
    public final void notifyItemRemoved(int... posList) {
        for (int pos : posList) {
            notifyItemRemoved(pos);
        }
    }

    /**
     * Notifies SectionDataManager that the items in this section in the given list of positions
     * have been removed.
     *
     * @param posList List of removed positions.
     */
    public final void notifyItemRemoved(List<Integer> posList) {
        for (int pos : posList) {
            notifyItemRemoved(pos);
        }
    }

    /**
     * Notifies SectionDataManager that <code>cnt</code> items starting at <code>startPos</code>
     * in this section have been removed.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemRangeRemoved(int, int)}.
     *
     * @param startPos Position of the first item that was removed.
     * @param cnt      Number of items removed.
     */
    public final void notifyItemRangeRemoved(int startPos, int cnt) {
        if (itemManager != null) {
            itemManager.notifyRangeRemoved(section, startPos, cnt);
        }
    }

    /**
     * Notifies SectionDataManager that the item in this section at <code>pos</code>
     * has been changed.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemChanged(int)}.
     *
     * @param pos Changed item position.
     */
    public final void notifyItemChanged(int pos) {
        if (itemManager != null) {
            itemManager.notifyChanged(section, pos);
        }
    }

    /**
     * Notifies SectionDataManager that the items in this section in the given list of positions
     * have been changed.
     *
     * @param posList Varargs of changed positions.
     */
    public final void notifyItemChanged(int... posList) {
        for (int pos : posList) {
            notifyItemChanged(pos);
        }
    }

    /**
     * Notifies SectionDataManager that the items in this section in the given list of positions
     * have been changed.
     *
     * @param posList List of changed positions.
     */
    public final void notifyItemChanged(List<Integer> posList) {
        for (int pos : posList) {
            notifyItemChanged(pos);
        }
    }

    /**
     * Notifies SectionDataManager that <code>cnt</code> items starting at <code>startPos</code>
     * in this section have been changed.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemRangeChanged(int, int)}.
     *
     * @param startPos Position of the first item that was changed.
     * @param cnt      Number of items changed.
     */
    public final void notifyItemRangeChanged(int startPos, int cnt) {
        if (itemManager != null) {
            itemManager.notifyRangeChanged(section, startPos, cnt);
        }
    }

    /**
     * Notifies SectionDataManager that the item at <code>fromPosition</code> has been moved
     * to <code>toPosition</code>.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#notifyItemMoved(int, int)}.
     *
     * @param fromPos Previous position of the item.
     * @param toPos   New position of the item.
     */
    public final void notifyItemMoved(int fromPos, int toPos) {
        if (itemManager != null) {
            itemManager.notifyMoved(section, fromPos, toPos);
        }
    }

    /**
     * Returns the 0-based index of the section currently represented by this SectionAdapter in
     * SectionedRecyclerView.
     *
     * @return Current section index.
     */
    public final int getSection() {
        return section;
    }

    void setItemManager(SectionItemManager itemManager) {
        this.itemManager = itemManager;
    }

    /**
     * Base ViewHolder class for SectionAdapter.
     * <p>
     * Describes a view and metadata about its place within the SectionedRecyclerView.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.ViewHolder}.
     */
    public abstract static class ViewHolder {

        public final View itemView;
        PositionConverter positionConverter;
        ViewHolderWrapper viewHolderWrapper;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("ItemView may not be null when creating ViewHolder.");
            }
            this.itemView = itemView;
        }

        /**
         * Returns the global adapter position in the {@link RecyclerView.Adapter} represented by
         * this ViewHolder or -1, if this ViewHolder hasn't been used in any SectionedRV.
         * <p>
         * Similar to {@link RecyclerView.ViewHolder#getAdapterPosition()}.
         *
         * @return Global adapter position or -1.
         */
        public final int getGlobalAdapterPosition() {
            return viewHolderWrapper != null ? viewHolderWrapper.getAdapterPosition() : -1;
        }

        /**
         * Returns the section index that corresponds to this ViewGolder or -1, if this ViewHolder
         * hasn't been used in any SectionedRV.
         *
         * @return Index of the section or -1.
         */
        public final int getSection() {
            int adapterPos = getGlobalAdapterPosition();
            return positionConverter != null ? positionConverter.calcSection(adapterPos) : -1;
        }

    }
    /**
     * Base ViewHolder class for item view in SectionAdapter.
     * <p>
     * Describes an item view and metadata about its place within the SectionedRecyclerView and
     * SectionAdapter. Item views unlike header views have section adapter position.
     */
    public abstract static class ItemViewHolder extends SectionAdapter.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * Returns the section adapter position represented by this ViewHolder or -1, if this
         * ViewHolder hasn't been used in any SectionedRV.
         *
         * @return Section adapter position.
         */
        public final int getSectionAdapterPosition() {
            int adapterPos = getGlobalAdapterPosition();
            return positionConverter != null ? positionConverter.calcPosInSection(adapterPos) : -1;
        }

    }

}
