/*
 * MIT License
 *
 * Copyright (c) 2017 Cruxlab, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cruxlab.sectionedrecyclerview.lib;


import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Base Adapter class for a section.
 * <p>
 * Provides a binding from an app-specific data set to views that are displayed within a RecyclerView
 * in an individual section.
 * <p>
 * Similar to {@link RecyclerView.Adapter}.
 *
 * @param <IVH> A class that extends ItemViewHolder that will be used by the adapter to manage item views.
 */
public abstract class BaseSectionAdapter<IVH extends BaseSectionAdapter.ItemViewHolder> {

    int section = -1;
    SectionItemManager itemManager;

    /**
     * Returns the total number of items in the section data set held by the adapter.
     *
     * @return The total number of items in this section.
     */
    public abstract int getItemCount();

    /**
     * Called when SectionDataManager needs a new {@link BaseSectionAdapter.ItemViewHolder} of the given
     * type to represent an item in this section.
     * <p>
     * Similar to {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}.
     *
     * @param parent The ViewGroup into which the new item view will be added after it is bound to
     *               an adapter position.
     * @param type   The view type of the new View.
     */
    public abstract IVH onCreateItemViewHolder(ViewGroup parent, short type);

    /**
     * Called by SectionDataManager to display the data at the specified section position. This
     * method should update the contents of the {@link BaseSectionAdapter.ItemViewHolder#itemView}
     * to reflect the item at the given position.
     * <p>
     * Similar to {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the section data set.
     * @param position The position of the item within the adapter's data set.
     */
    public abstract void onBindItemViewHolder(IVH holder, int position);

    /**
     * Return the view type of the item within this section at <code>position</code> for the purposes
     * of view recycling.
     * <p>
     * Similar to {@link RecyclerView.Adapter#getItemViewType(int)}.
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
     * Similar to {@link RecyclerView.Adapter#notifyItemInserted(int)}.
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
     * Similar to {@link RecyclerView.Adapter#notifyItemRangeInserted(int, int)}.
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
     * Similar to {@link RecyclerView.Adapter#notifyItemRemoved(int)}.
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
     * Similar to {@link RecyclerView.Adapter#notifyItemRangeRemoved(int, int)}.
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
     * Similar to {@link RecyclerView.Adapter#notifyItemChanged(int)}.
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
     * Similar to {@link RecyclerView.Adapter#notifyItemRangeChanged(int, int)}.
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
     * Similar to {@link RecyclerView.Adapter#notifyItemMoved(int, int)}.
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
     * Notifies SectionDataManager that all items in this section were changed.
     * <p>
     * Similar to {@link RecyclerView.Adapter#notifyDataSetChanged()}.
     * <p>
     * Note that this method does not notify about changes in the header, you can use
     * {@link SectionManager#updateSection(int)} for complete section update.
     * Also you can use {@link SectionAdapter#notifyHeaderChanged()} (int)} to update
     * the header directly.
     */
    public final void notifyDataSetChanged() {
        if (itemManager != null) {
            itemManager.notifyDataSetChanged(section);
        }
    }

    /**
     * Returns the 0-based index of the section currently represented by this BaseSectionAdapter in
     * RecyclerView.
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
     * Base ViewHolder class for BaseSectionAdapter.
     * <p>
     * Describes a view and metadata about its place within the RecyclerView.
     * <p>
     * Similar to {@link RecyclerView.ViewHolder}.
     */
    public abstract static class ViewHolder {

        public final View itemView;
        PositionManager posManager;
        ViewHolderWrapper viewHolderWrapper;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("ItemView cannot be null when creating ViewHolder.");
            }
            this.itemView = itemView;
        }

        /**
         * Returns the global adapter position in the {@link RecyclerView.Adapter} represented by
         * this ViewHolder or -1, if this ViewHolder hasn't been used in any RecyclerView.
         * <p>
         * Similar to {@link RecyclerView.ViewHolder#getAdapterPosition()}.
         *
         * @return Global adapter position or -1.
         */
        public int getGlobalAdapterPosition() {
            return viewHolderWrapper != null ? viewHolderWrapper.getAdapterPosition() : -1;
        }

        /**
         * Returns the global layout position represented by this ViewHolder or -1, if this ViewHolder
         * hasn't been used in any RecyclerView (e.g. for pinned HeaderViewHolder).
         * <p>
         * Similar to {@link RecyclerView.ViewHolder#getLayoutPosition()}.
         *
         * @return Global layout position or -1.
         */
        public int getGlobalLayoutPosition() {
            return viewHolderWrapper != null ? viewHolderWrapper.getLayoutPosition() : -1;
        }

        /**
         * Returns the section index that corresponds to this ViewHolder or -1, if this ViewHolder
         * hasn't been used in any RecyclerView.
         *
         * @return Index of the section or -1.
         */
        public final int getSection() {
            int adapterPos = getGlobalAdapterPosition();
            return posManager != null ? posManager.calcSection(adapterPos) : -1;
        }

    }

    /**
     * Base ViewHolder class for item view in BaseSectionAdapter.
     * <p>
     * Describes an item view and metadata about its place within the RecyclerView and
     * BaseSectionAdapter. Provides item view position within its section.
     */
    public abstract static class ItemViewHolder extends BaseSectionAdapter.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * Returns the position in the corresponding BaseSectionAdapter represented by this ViewHolder
         * or -1, if this ViewHolder hasn't been used in any RecyclerView.
         *
         * @return Section adapter position.
         */
        public final int getSectionAdapterPosition() {
            int adapterPos = getGlobalAdapterPosition();
            return posManager != null ? posManager.calcPosInSection(adapterPos) : -1;
        }

    }

    /**
     * Base ViewHolder class for header view in SimpleSectionAdapter.
     * <p>
     * Describes a header view and metadata about its place within the RecyclerView. When
     * the corresponding header is duplicated, it uses {@link #sourcePositionProvider} to obtain
     * the global adapter position.
     */
    public abstract static class HeaderViewHolder extends BaseSectionAdapter.ViewHolder {

        short sectionType;
        HeaderPosProvider sourcePositionProvider;

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public final int getGlobalAdapterPosition() {
            if (sourcePositionProvider != null) {
                return sourcePositionProvider.getHeaderAdapterPos(sectionType);
            } else {
                return super.getGlobalAdapterPosition();
            }
        }

    }

}
