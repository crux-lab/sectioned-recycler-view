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

import android.view.ViewGroup;

/**
 * Contains {@link SimpleSectionAdapter} or {@link SectionAdapter}. Passes calls to non null
 * adapter instance, handling unsupported calls for SimpleSectionAdapter without header.
 */
class SectionAdapterWrapper {

    private final SimpleSectionAdapter simpleSectionAdapter;
    private final SectionAdapter sectionAdapter;

    SectionAdapterWrapper(SimpleSectionAdapter simpleSectionAdapter) {
        if (simpleSectionAdapter == null) {
            throw new IllegalArgumentException("SimpleSectionAdapter cannot be null.");
        }
        this.simpleSectionAdapter = simpleSectionAdapter;
        this.sectionAdapter = null;
    }

    SectionAdapterWrapper(SectionAdapter sectionAdapter, short headerType) {
        if (sectionAdapter == null) {
            throw new IllegalArgumentException("SectionAdapter cannot be null.");
        }
        this.sectionAdapter = sectionAdapter;
        sectionAdapter.headerType = headerType;
        this.simpleSectionAdapter = null;
    }

    BaseSectionAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, short type) {
        if (sectionAdapter != null) {
            return sectionAdapter.onCreateItemViewHolder(parent, type);
        } else {
            return simpleSectionAdapter.onCreateItemViewHolder(parent, type);
        }
    }

    BaseSectionAdapter.HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        if (sectionAdapter != null) {
            return sectionAdapter.onCreateHeaderViewHolder(parent);
        } else {
            return null;
        }
    }
    @SuppressWarnings("unchecked")
    void onBindViewHolder(BaseSectionAdapter.ItemViewHolder holder, int position) {
        if (sectionAdapter != null) {
            sectionAdapter.onBindItemViewHolder(holder, position);
        } else {
            simpleSectionAdapter.onBindItemViewHolder(holder, position);
        }
    }

    @SuppressWarnings("unchecked")
    void onBindHeaderViewHolder(BaseSectionAdapter.HeaderViewHolder holder) {
        if (sectionAdapter != null) {
            sectionAdapter.onBindHeaderViewHolder(holder);
        }
    }

    short getItemViewType(int position) {
        if (sectionAdapter != null) {
            return sectionAdapter.getItemViewType(position);
        } else {
            return simpleSectionAdapter.getItemViewType(position);
        }
    }

    boolean isHeaderVisible() {
        return sectionAdapter != null &&
                sectionAdapter.isHeaderVisible();
    }

    int getHeaderVisibilityInt() {
        return isHeaderVisible() ? 1 : 0;
    }

    boolean isHeaderPinned() {
        return sectionAdapter != null &&
                sectionAdapter.isHeaderPinned();
    }

    void setSection(int section) {
        if (sectionAdapter != null) {
            sectionAdapter.section = section;
        } else {
            simpleSectionAdapter.section = section;
        }
    }

    int getSection() {
        if (sectionAdapter != null) {
            return sectionAdapter.section;
        } else {
            return simpleSectionAdapter.section;
        }
    }

    @SuppressWarnings("unchecked")
    <T extends BaseSectionAdapter> T getAdapter() {
        if (sectionAdapter != null) {
            return (T) sectionAdapter;
        } else {
            return (T) simpleSectionAdapter;
        }
    }

    void setItemManager(SectionItemManager itemManager) {
        if (sectionAdapter != null) {
            sectionAdapter.setItemManager(itemManager);
        } else {
            simpleSectionAdapter.setItemManager(itemManager);
        }
    }

    int getItemCount() {
        if (sectionAdapter != null) {
            return sectionAdapter.getItemCount();
        } else {
            return simpleSectionAdapter.getItemCount();
        }
    }

    short getHeaderType() {
        if (sectionAdapter != null) {
            return sectionAdapter.headerType;
        }
        return SectionAdapter.NO_HEADER_TYPE;
    }

    void resetAdapter() {
        setSection(-1);
        setItemManager(null);
        if (sectionAdapter != null) {
            sectionAdapter.headerType = SectionAdapter.NO_HEADER_TYPE;
        }
    }

}
