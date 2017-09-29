package com.cruxlab.sectionedrecyclerview.lib;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class SectionAdapter<VH extends SectionAdapter.ItemViewHolder, HVH extends SectionAdapter.ViewHolder> {

    int section;
    SectionItemManager itemManager;
    private boolean isHeaderVisible;
    private boolean isHeaderPinned;

    public SectionAdapter(boolean isHeaderVisible, boolean isHeaderPinned) {
        this.isHeaderVisible = isHeaderVisible;
        this.isHeaderPinned = isHeaderPinned;
    }

    public abstract int getItemCount();

    public abstract VH onCreateViewHolder(ViewGroup parent, short type);

    public abstract void onBindViewHolder(VH holder, int position);

    public abstract HVH onCreateHeaderViewHolder(ViewGroup parent);

    public abstract void onBindHeaderViewHolder(HVH holder);

    public short getItemViewType(int position) {
        return 0;
    }

    public void updateHeaderVisibility(boolean visible) {
        Checker.checkItemManager(itemManager);
        if (visible == isHeaderVisible) return;
        isHeaderVisible = visible;
        itemManager.notifyHeaderVisibilityChanged(section, visible);
    }

    public void updateHeaderPinnedState(boolean pinned) {
        Checker.checkItemManager(itemManager);
        if (pinned == isHeaderPinned) return;
        isHeaderPinned = pinned;
        itemManager.notifyHeaderPinnedStateChanged(section, pinned);
    }

    public boolean isHeaderVisible() {
        return isHeaderVisible;
    }

    public boolean isHeaderPinned() {
        return isHeaderPinned;
    }

    public final void notifyItemInserted(int pos) {
        Checker.checkItemManager(itemManager);
        Checker.checkPosition(pos, getItemCount() + 1);
        itemManager.notifyInserted(section, pos);
    }

    public final void notifyItemInserted(int... posList) {
        for (int pos : posList) {
            notifyItemInserted(pos);
        }
    }

    public final void notifyItemInserted(List<Integer> posList) {
        for (int pos : posList) {
            notifyItemInserted(pos);
        }
    }

    public final void notifyItemRangeInserted(int startPos, int cnt) {
        Checker.checkItemManager(itemManager);
        Checker.checkPosRange(startPos, cnt, getItemCount());
        itemManager.notifyRangeInserted(section, startPos, cnt);
    }

    public final void notifyItemRemoved(int pos) {
        Checker.checkItemManager(itemManager);
        Checker.checkPosition(pos, getItemCount() + 1);
        itemManager.notifyRemoved(section, pos);
    }

    public final void notifyItemRemoved(int... posList) {
        for (int pos : posList) {
            notifyItemRemoved(pos);
        }
    }

    public final void notifyItemRemoved(List<Integer> posList) {
        for (int pos : posList) {
            notifyItemRemoved(pos);
        }
    }

    public final void notifyItemRangeRemoved(int startPos, int cnt) {
        Checker.checkItemManager(itemManager);
        Checker.checkPosRange(startPos, cnt, getItemCount());
        itemManager.notifyRangeRemoved(section, startPos, cnt);
    }

    public final void notifyItemChanged(int pos) {
        Checker.checkItemManager(itemManager);
        Checker.checkPosition(pos, getItemCount());
        itemManager.notifyChanged(section, pos);
    }

    public final void notifyItemChanged(int... posList) {
        for (int pos : posList) {
            notifyItemChanged(pos);
        }
    }

    public final void notifyItemChanged(List<Integer> posList) {
        for (int pos : posList) {
            notifyItemChanged(pos);
        }
    }

    public final void notifyItemRangeChanged(int startPos, int cnt) {
        Checker.checkItemManager(itemManager);
        Checker.checkPosRange(startPos, cnt, getItemCount());
        itemManager.notifyRangeChanged(section, startPos, cnt);
    }

    public final void notifyItemMoved(int fromPos, int toPos) {
        Checker.checkItemManager(itemManager);
        Checker.checkPosition(fromPos, getItemCount());
        Checker.checkPosition(toPos, getItemCount());
        itemManager.notifyMoved(section, fromPos, toPos);
    }

    public void notifyHeaderChanged() {
        Checker.checkItemManager(itemManager);
        itemManager.notifyHeaderChanged(section);
    }

    public final int getSection() {
        return section;
    }

    void setItemManager(SectionItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public abstract static class ViewHolder {

        public final View itemView;

        public ViewHolder(View itemView) {
            Checker.checkItemView(itemView);
            this.itemView = itemView;
        }

    }

    public abstract static class ItemViewHolder extends SectionAdapter.ViewHolder {

        SectionDataManager.ViewHolderWrapper viewHolderWrapper;
        SectionPositionProvider sectionPositionProvider;

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public final int getSectionPosition() {
            Checker.checkViewHolderWrapper(viewHolderWrapper);
            Checker.checkSectionPositionProvider(sectionPositionProvider);
            int adapterPos = viewHolderWrapper.getAdapterPosition();
            if (adapterPos == -1) return -1;
            return sectionPositionProvider.getSectionPos(adapterPos);
        }

    }

}
