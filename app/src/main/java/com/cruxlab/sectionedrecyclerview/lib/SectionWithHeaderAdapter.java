package com.cruxlab.sectionedrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Base class for a SectionWithHeaderAdapter.
 * <p>
 * Besides item views, provides a header view displayed within a SectionedRecyclerView in
 * an individual section.
 *
 * @param <IVH>  A class that extends ItemViewHolder that will be used by the adapter to manage item views.
 * @param <HVH> A class that extends ViewHolder that will be used by the adapter to manage header view.
 */

public abstract class SectionWithHeaderAdapter<IVH extends SectionAdapter.ItemViewHolder, HVH extends SectionAdapter.HeaderViewHolder> extends SectionAdapter<IVH> {

    private boolean isHeaderVisible;
    private boolean isHeaderPinned;

    public SectionWithHeaderAdapter(boolean isHeaderVisible, boolean isHeaderPinned) {
        this.isHeaderVisible = isHeaderVisible;
        this.isHeaderPinned = isHeaderPinned;
    }

    /**
     * Called when SectionDataManager needs a new {@link SectionAdapter.HeaderViewHolder} of the given
     * type to represent a header in this section.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}.
     *
     * @param parent The ViewGroup into which the new header view will be added after it is bound to
     *               an adapter position.
     */
    public abstract HVH onCreateHeaderViewHolder(ViewGroup parent);

    /**
     * Called by SectionDataManager to display header data. This method should update the contents
     * of the header {@link SectionAdapter.HeaderViewHolder#itemView}.
     * <p>
     * Similar to {@link android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the header.
     */
    public abstract void onBindHeaderViewHolder(HVH holder);

    /**
     * Notifies SectionDataManager that the header has been changed.
     * <p>
     * Note, that it should be called, when header view updates itself (e.g. changes its contents
     * after some user interaction), because otherwise when header view is duplicated and pinned
     * to the top of the {@link SectionHeaderLayout}, these changes won't affect the real header
     * view in the RecyclerView.
     */
    public void notifyHeaderChanged() {
        if (itemManager == null) return;
        itemManager.notifyHeaderChanged(section);
    }

    /**
     * Updates header visibility and notifies SectionDataManager about changes.
     *
     * @param visible New header visibility.
     */
    public void updateHeaderVisibility(boolean visible) {
        if (itemManager == null) return;
        if (visible == isHeaderVisible) return;
        isHeaderVisible = visible;
        itemManager.notifyHeaderVisibilityChanged(section, visible);
    }

    /**
     * Updates header pinned state and notifies SectionDataManager about changes.
     *
     * @param pinned New header pinned state.
     */
    public void updateHeaderPinnedState(boolean pinned) {
        if (itemManager == null) return;
        if (pinned == isHeaderPinned) return;
        isHeaderPinned = pinned;
        itemManager.notifyHeaderPinnedStateChanged(section, pinned);
    }

    /**
     * Returns whether header is currently visible.
     *
     * @return Header visibility.
     */
    public final boolean isHeaderVisible() {
        return isHeaderVisible;
    }

    /**
     * Returns whether header is currently pinned.
     *
     * @return Header pinned state.
     */
    public final boolean isHeaderPinned() {
        return isHeaderPinned;
    }

}
