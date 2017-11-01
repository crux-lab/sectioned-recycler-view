package com.cruxlab.sectionedrecyclerview.lib;

import android.view.ViewGroup;

/**
 * Contains {@link SectionAdapter} or {@link SectionWithHeaderAdapter}. Passes calls to non null
 * adapter instance, handling unsupported calls for SectionAdapter without header.
 */
class SectionAdapterWrapper {

    private final SectionAdapter sectionAdapter;
    private final SectionWithHeaderAdapter sectionWithHeaderAdapter;

    SectionAdapterWrapper(SectionAdapter sectionAdapter) {
        if (sectionAdapter == null) {
            throw new IllegalArgumentException("SectionAdapter cannot be null.");
        }
        this.sectionAdapter = sectionAdapter;
        this.sectionWithHeaderAdapter = null;
    }

    SectionAdapterWrapper(SectionWithHeaderAdapter sectionWithHeaderAdapter) {
        if (sectionWithHeaderAdapter == null) {
            throw new IllegalArgumentException("SectionWithHeaderAdapter cannot be null.");
        }
        this.sectionWithHeaderAdapter = sectionWithHeaderAdapter;
        this.sectionAdapter = null;
    }

    SectionAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, short type) {
        if (sectionWithHeaderAdapter != null) {
            return sectionWithHeaderAdapter.onCreateItemViewHolder(parent, type);
        } else {
            return sectionAdapter.onCreateItemViewHolder(parent, type);
        }
    }

    SectionAdapter.HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        if (sectionWithHeaderAdapter != null) {
            return sectionWithHeaderAdapter.onCreateHeaderViewHolder(parent);
        } else {
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    void onBindViewHolder(SectionAdapter.ItemViewHolder holder, int position) {
        if (sectionWithHeaderAdapter != null) {
            sectionWithHeaderAdapter.onBindItemViewHolder(holder, position);
        } else {
            sectionAdapter.onBindItemViewHolder(holder, position);
        }
    }

    @SuppressWarnings("unchecked")
    void onBindHeaderViewHolder(SectionAdapter.HeaderViewHolder holder) {
        if (sectionWithHeaderAdapter != null) {
            sectionWithHeaderAdapter.onBindHeaderViewHolder(holder);
        }
    }

    short getItemViewType(int position) {
        if (sectionWithHeaderAdapter != null) {
            return sectionWithHeaderAdapter.getItemViewType(position);
        } else {
            return sectionAdapter.getItemViewType(position);
        }
    }

    boolean isHeaderVisible() {
        return sectionWithHeaderAdapter != null &&
                sectionWithHeaderAdapter.isHeaderVisible();
    }

    int getHeaderVisibilityInt() {
        return isHeaderVisible() ? 1 : 0;
    }

    boolean isHeaderPinned() {
        return sectionWithHeaderAdapter != null &&
                sectionWithHeaderAdapter.isHeaderPinned();
    }

    void setSection(int section) {
        if (sectionWithHeaderAdapter != null) {
            sectionWithHeaderAdapter.section = section;
        } else {
            sectionAdapter.section = section;
        }
    }

    int getSection() {
        if (sectionWithHeaderAdapter != null) {
            return sectionWithHeaderAdapter.section;
        } else {
            return sectionAdapter.section;
        }
    }

    @SuppressWarnings("unchecked")
    <T extends SectionAdapter> T getAdapter() {
        if (sectionWithHeaderAdapter != null) {
            return (T) sectionWithHeaderAdapter;
        } else {
            return (T) sectionAdapter;
        }
    }

    void setItemManager(SectionItemManager itemManager) {
        if (sectionWithHeaderAdapter != null) {
            sectionWithHeaderAdapter.setItemManager(itemManager);
        } else {
            sectionAdapter.setItemManager(itemManager);
        }
    }

    int getItemCount() {
        if (sectionWithHeaderAdapter != null) {
            return sectionWithHeaderAdapter.getItemCount();
        } else {
            return sectionAdapter.getItemCount();
        }
    }

}
