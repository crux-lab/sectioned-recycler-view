package com.cruxlab.sectionedrecyclerview.lib;

import android.support.v7.widget.RecyclerView;

/**
 * RecyclerView.ViewHolder implementation to work with RecyclerView.Adapter in
 * {@link SectionDataManager}. Contains the corresponding {@link SectionAdapter.ViewHolder} and
 * refers to the same View.
 */
class ViewHolderWrapper extends RecyclerView.ViewHolder {

    final SectionAdapter.ViewHolder viewHolder;

    ViewHolderWrapper(SectionAdapter.ViewHolder viewHolder) {
        super(viewHolder.itemView);
        this.viewHolder = viewHolder;
    }

}
