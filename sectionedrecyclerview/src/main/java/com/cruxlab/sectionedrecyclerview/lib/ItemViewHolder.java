package com.cruxlab.sectionedrecyclerview.lib;

import android.view.View;

/**
 * Base ViewHolder class for item view in BaseSectionAdapter.
 * <p>
 * Describes an item view and metadata about its place within the RecyclerView and
 * BaseSectionAdapter. Provides item view position within its section.
 */
public abstract class ItemViewHolder extends ViewHolder {

    public ItemViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Returns the position in the corresponding BaseSectionAdapter represented by this ViewHolder
     * or -1, if this ViewHolder hasn't been used in any RecyclerView.
     *
     * @return Section adapter position.
     */
    public final int getPosInSection() {
        int adapterPos = getGlobalAdapterPosition();
        return positionConverter != null ? positionConverter.calcPosInSection(adapterPos) : -1;
    }

}
