package com.cruxlab.sectionedrecyclerview.lib;

import android.view.View;

abstract class Checker {

    static void checkPosition(int pos, int itemCount) {
        if (pos < 0 || pos >= itemCount) {
            throw new IllegalArgumentException("Position " + pos + " is out of range. Current item count is " + itemCount + ".");
        }
    }

    static void checkSection(int section, int sectionCount) {
        if (section < 0 || section >= sectionCount) {
            throw new IllegalArgumentException("Section " + section + " is out of range. Current section count is " + sectionCount + ".");
        }
    }

    static void checkItemManager(SectionItemManager itemManager) {
        if (itemManager == null) {
            throw new IllegalStateException("This SectionAdapter hasn't been added to any SectionedRV, so no SectionItemManager has been set.");
        }
    }

    //TODO: fix when inserting/removing
    static void checkPosRange(int startPos, int cnt, int itemCount) {
        checkPosition(startPos, itemCount);
        if (cnt <= 0) {
            throw new IllegalArgumentException("Cnt in position range should be positive, but cnt is " +  cnt + ".");
        }
        int endPos = startPos + cnt - 1;
        if (endPos >= itemCount) {
            throw new IllegalArgumentException("Position cnt " +  cnt + " from start position " + startPos + " is out of range. Current item count is " +  itemCount + ".");
        }
    }

    static void checkItemView(View itemView) {
        if (itemView == null) {
            throw new IllegalArgumentException("ItemView may not be null when creating SectionAdapter.ViewHolder.");
        }
    }

    static void checkViewHolder(SectionedRVAdapter.ViewHolder viewHolder) {
        if (viewHolder == null) {
            throw new IllegalStateException("This SectionAdapter.ItemViewHolder hasn't been used in any SectionedRV, so no SectionedRVAdapter.ViewHolder has been set.");
        }
    }

    static void checkSectionPositionProvider(SectionPositionProvider sectionPositionProvider) {
        if (sectionPositionProvider == null) {
            throw new IllegalStateException("This SectionAdapter.ItemViewHolder hasn't been used in any SectionedRV, so no SectionPositionProvider has been set.");
        }
    }

}
