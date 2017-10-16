package com.cruxlab.sectionedrecyclerview.lib;

/**
 * Interface for interaction between {@link SectionAdapter} and SectionedRecyclerView.
 */
interface SectionItemManager {

    void notifyInserted(int section, int pos);
    void notifyRemoved(int section, int pos);
    void notifyChanged(int section, int pos);
    void notifyRangeInserted(int section, int startPos, int cnt);
    void notifyRangeRemoved(int section, int startPos, int cnt);
    void notifyRangeChanged(int section, int startPos, int cnt);
    void notifyMoved(int section, int fromPos, int toPos);
    void notifyHeaderChanged(int section);
    void notifyHeaderVisibilityChanged(int section, boolean visible);
    void notifyHeaderPinnedStateChanged(int section, boolean pinned);

}
