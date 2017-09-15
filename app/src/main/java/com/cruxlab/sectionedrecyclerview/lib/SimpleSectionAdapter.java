package com.cruxlab.sectionedrecyclerview.lib;

import android.view.ViewGroup;

public abstract class SimpleSectionAdapter<VH extends SectionAdapter.ItemViewHolder> extends SectionAdapter<VH, SectionAdapter.ViewHolder> {

    public SimpleSectionAdapter() {
        super(false, false);
    }

    @Override
    public final boolean isHeaderVisible() {
        return false;
    }

    @Override
    public final boolean isHeaderPinned() {
        return false;
    }

    @Override
    public final SectionAdapter.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        throw new RuntimeException("SimpleSectionAdapter doesn't have header, so it should not call method onCreateHeaderViewHolder().");
    }

    @Override
    public final void onBindHeaderViewHolder(SectionAdapter.ViewHolder holder) {
        throw new RuntimeException("SimpleSectionAdapter doesn't have header, so it should not call method onBindHeaderViewHolder().");
    }

    @Override
    public final void updateHeaderVisibility(boolean visible) {
        throw new RuntimeException("SimpleSectionAdapter doesn't have header, so it should not call method updateHeaderVisibility(boolean visible).");
    }

    @Override
    public final void updateHeaderPinnedState(boolean pinned) {
        throw new RuntimeException("SimpleSectionAdapter doesn't have header, so it should not call method updateHeaderPinnedState(boolean pinned).");
    }

    @Override
    public final void notifyHeaderChanged() {
        throw new RuntimeException("SimpleSectionAdapter doesn't have header, so it should not call method notifyHeaderChanged().");
    }

}
