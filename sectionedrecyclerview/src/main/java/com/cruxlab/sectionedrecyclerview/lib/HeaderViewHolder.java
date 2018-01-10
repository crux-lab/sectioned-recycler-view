package com.cruxlab.sectionedrecyclerview.lib;

import android.view.View;

/**
 * Base ViewHolder class for header view in SimpleSectionAdapter.
 * <p>
 * Describes a header view and metadata about its place within the RecyclerView. When
 * the corresponding header is duplicated, it uses {@link #sourcePosProvider} to obtain
 * the global adapter position.
 */
public abstract class HeaderViewHolder extends ViewHolder {

    short sectionType;
    HeaderPosProvider sourcePosProvider;

    public HeaderViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public final int getGlobalAdapterPosition() {
        if (sourcePosProvider != null) {
            return sourcePosProvider.getHeaderAdapterPos(sectionType);
        } else {
            return super.getGlobalAdapterPosition();
        }
    }

}
