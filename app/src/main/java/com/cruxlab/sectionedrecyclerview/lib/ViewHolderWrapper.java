package com.cruxlab.sectionedrecyclerview.lib;

import android.support.v7.widget.RecyclerView;

/**
 * RecyclerView.ViewHolder implementation to work with RecyclerView.Adapter in
 * {@link SectionDataManager}. Contains the corresponding {@link BaseSectionAdapter.ViewHolder} and
 * refers to the same View.
 */
class ViewHolderWrapper extends RecyclerView.ViewHolder {

    final BaseSectionAdapter.ViewHolder viewHolder;

    ViewHolderWrapper(BaseSectionAdapter.ViewHolder viewHolder) {
        super(viewHolder.itemView);
        this.viewHolder = viewHolder;
    }

}
