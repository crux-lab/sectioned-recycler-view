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
import android.view.ViewGroup;

/**
 * Base Adapter class for a section with header.
 * <p>
 * Besides item views, provides a header view displayed within a RecyclerView in an individual section.
 *
 * @param <IVH> A class that extends ItemViewHolder that will be used by the adapter to manage item views.
 * @param <HVH> A class that extends ViewHolder that will be used by the adapter to manage header view.
 */

public abstract class SectionAdapter<IVH extends BaseSectionAdapter.ItemViewHolder, HVH extends BaseSectionAdapter.HeaderViewHolder> extends BaseSectionAdapter<IVH> {

    public static short NO_HEADER_TYPE = -1;

    short headerType = NO_HEADER_TYPE;
    private boolean isHeaderVisible;
    private boolean isHeaderPinned;

    public SectionAdapter(boolean isHeaderVisible, boolean isHeaderPinned) {
        this.isHeaderVisible = isHeaderVisible;
        this.isHeaderPinned = isHeaderPinned;
    }

    /**
     * Called when SectionDataManager needs a new {@link BaseSectionAdapter.HeaderViewHolder} of the given
     * type to represent a header in this section.
     * <p>
     * Similar to {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}.
     *
     * @param parent The ViewGroup into which the new header view will be added after it is bound to
     *               an adapter position.
     */
    public abstract HVH onCreateHeaderViewHolder(ViewGroup parent);

    /**
     * Called by SectionDataManager to display header data. This method should update the contents
     * of the header {@link BaseSectionAdapter.HeaderViewHolder#itemView}.
     * <p>
     * Similar to {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}.
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

    /**
     * Returns header type currently associated with this adapter or {@link SectionAdapter#NO_HEADER_TYPE}.
     *
     * @return Header type.
     */
    public final int getHeaderType() {
        return headerType;
    }

}
