package com.cruxlab.sectionedrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Base ViewHolder class for BaseSectionAdapter.
 * <p>
 * Describes a view and metadata about its place within the RecyclerView.
 * <p>
 * Similar to {@link android.support.v7.widget.RecyclerView.ViewHolder}.
 */
public abstract class ViewHolder {

    public final View itemView;
    PositionConverter positionConverter;
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
        return positionConverter != null ? positionConverter.calcSection(adapterPos) : -1;
    }

}
