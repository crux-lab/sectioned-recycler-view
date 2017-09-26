package com.cruxlab.sectionedrecyclerview.lib;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class SectionDataManager implements SectionManager, SectionItemManager, SectionPositionProvider {

    private short freeType = 1;
    private HeaderViewManager headerViewManager;
    private ArrayList<Integer> sectionToPosSum;
    private ArrayList<Short> sectionToType;
    private SparseArray<SectionAdapter> typeToAdapter;
    private SparseArray<SectionAdapter.ViewHolder> typeToHeaderVH;
    private RecyclerView.Adapter<MockViewHolder> mockVHAdapter = new RecyclerView.Adapter<MockViewHolder>() {

        @Override
        public MockViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            short sectionType = (short) (type);
            short itemType = (short) (type >> 16);
            if (isTypeHeader(sectionType)) {
                SectionAdapter adapter = typeToAdapter.get(-sectionType);
                SectionAdapter.ViewHolder headerViewHolder = adapter.onCreateHeaderViewHolder(parent);
                return new MockViewHolder(headerViewHolder);
            } else {
                SectionAdapter adapter = typeToAdapter.get(sectionType);
                SectionAdapter.ItemViewHolder itemViewHolder = adapter.onCreateViewHolder(parent, itemType);
                MockViewHolder mockViewHolder = new MockViewHolder(itemViewHolder);
                itemViewHolder.mockViewHolder = mockViewHolder;
                itemViewHolder.sectionPositionProvider = SectionDataManager.this;
                return mockViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(MockViewHolder mockViewHolder, int position) {
            int type = getItemViewType(position);
            short sectionType = (short) (type);
            if (isTypeHeader(sectionType)) {
                SectionAdapter adapter = typeToAdapter.get(-sectionType);
                adapter.onBindHeaderViewHolder(mockViewHolder.headerViewHolder);
            } else {
                int sectionPos = getSectionPos(position);
                SectionAdapter adapter = typeToAdapter.get(sectionType);
                adapter.onBindViewHolder(mockViewHolder.itemViewHolder, sectionPos);
            }
        }

        @Override
        public int getItemCount() {
            return getTotalItemCount();
        }

        @Override
        public int getItemViewType(int pos) {
            Checker.checkPosition(pos, getTotalItemCount());
            int section = getSectionByAdapterPos(pos);
            short sectionType = sectionToType.get(section);
            int sectionPos = getSectionPos(pos);
            SectionAdapter adapter = typeToAdapter.get(sectionType);
            short itemType = adapter.getItemViewType(sectionPos);
            if (adapter.isHeaderVisible() && getSectionFirstPos(section) == pos) sectionType *= -1;
            return (itemType << 16) + sectionType;
        }

    };

    SectionDataManager(HeaderViewManager headerViewManager) {
        this.headerViewManager = headerViewManager;
        sectionToPosSum = new ArrayList<>();
        sectionToType = new ArrayList<>();
        typeToAdapter = new SparseArray<>();
        typeToHeaderVH = new SparseArray<>();
    }

    /* PUBLIC METHODS */

    public RecyclerView.Adapter<MockViewHolder> getMockVHAdapter() {
        return mockVHAdapter;
    }

    public int getTotalItemCount() {
        return getSectionCount() > 0 ? sectionToPosSum.get(getSectionCount() - 1) : 0;
    }

    public int getSectionByAdapterPos(int adapterPos) {
        Checker.checkPosition(adapterPos, getTotalItemCount());
        return upperBoundBinarySearch(sectionToPosSum, adapterPos);
    }

    public int getSectionType(int section) {
        Checker.checkSection(section, getSectionCount());
        return sectionToType.get(section);
    }

    public int getSectionFirstPos(int section) {
        Checker.checkSection(section, getSectionCount() + 1);
        return section > 0 ? sectionToPosSum.get(section - 1) : 0;
    }

    public int getAdapterPos(int section, int sectionPos) {
        Checker.checkSection(section, getSectionCount());
        Checker.checkPosition(sectionPos, getTotalItemCount());
        short sectionType = sectionToType.get(section);
        SectionAdapter adapter = typeToAdapter.get(sectionType);
        return (section > 0 ? sectionToPosSum.get(section - 1) : 0) + sectionPos + (adapter.isHeaderVisible() ? 1 : 0);
    }

    public int getSectionCurItemCount(int section) {
        Checker.checkSection(section, getSectionCount());
        return sectionToPosSum.get(section) - (section > 0 ? sectionToPosSum.get(section - 1) : 0);
    }

    public View getBoundHeaderView(ViewGroup parent, int section) {
        Checker.checkSection(section, getSectionCount());
        short sectionType = sectionToType.get(section);
        SectionAdapter adapter = typeToAdapter.get(sectionType);
        if (!adapter.isHeaderVisible() || !adapter.isHeaderPinned()) return null;
        SectionAdapter.ViewHolder headerViewHolder = typeToHeaderVH.get(sectionType);
        if (headerViewHolder == null) {
            headerViewHolder = adapter.onCreateHeaderViewHolder(parent);
            typeToHeaderVH.put(sectionType, headerViewHolder);
        }
        adapter.onBindHeaderViewHolder(headerViewHolder);
        return headerViewHolder.itemView;
    }

    /* END PUBLIC METHODS */
    /* SECTION MANAGER */

    @Override
    public int getSectionCount() {
        return typeToAdapter.size();
    }

    @Override
    public void addSection(@NonNull SectionAdapter sectionAdapter) {
        sectionAdapter.section = getSectionCount();
        sectionAdapter.setItemManager(this);
        int start = getTotalItemCount();
        int cnt = sectionAdapter.getItemCount() + (sectionAdapter.isHeaderVisible() ? 1 : 0);
        int posSum = getTotalItemCount() + cnt;
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(freeType);
        sectionToPosSum.add(posSum);
        freeType++;
        mockVHAdapter.notifyItemRangeInserted(start, cnt);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter) {
        Checker.checkSection(section, getSectionCount() + 1);
        sectionAdapter.section = section;
        sectionAdapter.setItemManager(this);
        int start = getSectionFirstPos(section);
        int cnt = sectionAdapter.getItemCount() + (sectionAdapter.isHeaderVisible() ? 1 : 0);
        int posSum = (section > 0 ? sectionToPosSum.get(section - 1) : 0) + cnt;
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(section, freeType);
        sectionToPosSum.add(section, posSum);
        freeType++;
        updatePosSum(section + 1, cnt, true);
        mockVHAdapter.notifyItemRangeInserted(start, cnt);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void replaceSection(int section, @NonNull SectionAdapter sectionAdapter) {
        Checker.checkSection(section, getSectionCount());
        removeSection(section);
        if (section == getSectionCount()) {
            addSection(sectionAdapter);
        } else {
            insertSection(section, sectionAdapter);
        }
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void removeSection(int section) {
        Checker.checkSection(section, getSectionCount());
        short sectionType = sectionToType.get(section);
        int cnt = getSectionCurItemCount(section);
        int start = getSectionFirstPos(section);
        typeToAdapter.remove(sectionType);
        typeToHeaderVH.remove(sectionType);
        sectionToType.remove(section);
        sectionToPosSum.remove(section);
        updatePosSum(section, -cnt, true);
        mockVHAdapter.notifyItemRangeRemoved(start, cnt);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void updateSection(int section) {
        Checker.checkSection(section, getSectionCount());
        mockVHAdapter.notifyItemRangeChanged(getSectionFirstPos(section), getSectionCurItemCount(section));
        headerViewManager.notifyHeaderUpdated(sectionToType.get(section));
    }

    /* END SECTION MANAGER */
    /* SECTION ITEM MANAGER */

    @Override
    public void notifyInserted(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getTotalItemCount() + 1);
        updatePosSum(section, 1, false);
        mockVHAdapter.notifyItemInserted(adapterPos);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void notifyRemoved(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getTotalItemCount() + 1);
        updatePosSum(section, -1, false);
        mockVHAdapter.notifyItemRemoved(adapterPos);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void notifyChanged(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getTotalItemCount());
        mockVHAdapter.notifyItemChanged(adapterPos);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void notifyRangeInserted(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getTotalItemCount());
        updatePosSum(section, cnt, false);
        mockVHAdapter.notifyItemRangeInserted(adapterStartPos, cnt);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void notifyRangeRemoved(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getTotalItemCount());
        updatePosSum(section, -cnt, false);
        mockVHAdapter.notifyItemRangeRemoved(adapterStartPos, cnt);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void notifyRangeChanged(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getTotalItemCount());
        mockVHAdapter.notifyItemRangeChanged(adapterStartPos, cnt);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void notifyMoved(int section, int fromPos, int toPos) {
        int adapterFromPos = getAdapterPos(section, fromPos);
        Checker.checkPosition(adapterFromPos, getTotalItemCount());
        int adapterToPos = getAdapterPos(section, toPos);
        Checker.checkPosition(adapterToPos, getTotalItemCount());
        mockVHAdapter.notifyItemMoved(adapterFromPos, adapterToPos);
        headerViewManager.checkIsHeaderViewChanged();
    }

    @Override
    public void notifyHeaderChanged(int section) {
        short sectionType = sectionToType.get(section);
        if (!typeToAdapter.get(sectionType).isHeaderVisible()) return;
        int headerPos = getSectionFirstPos(section);
        mockVHAdapter.notifyItemChanged(headerPos);
        headerViewManager.notifyHeaderUpdated(sectionToType.get(section));
    }

    @Override
    public void notifyHeaderVisibilityChanged(int section, boolean visible) {
        Checker.checkSection(section, getSectionCount());
        if (visible) {
            updatePosSum(section, 1, false);
            mockVHAdapter.notifyItemInserted(getSectionFirstPos(section));
        } else {
            updatePosSum(section, -1, false);
            mockVHAdapter.notifyItemRemoved(getSectionFirstPos(section));
        }
        headerViewManager.notifyHeaderUpdated(sectionToType.get(section));
    }

    @Override
    public void notifyHeaderPinnedStateChanged(int section, boolean pinned) {
        Checker.checkSection(section, getSectionCount());
        headerViewManager.notifyHeaderUpdated(sectionToType.get(section));
    }

    /* END SECTION ITEM MANAGER */
    /* SECTION POSITION PROVIDER */

    @Override
    public int getSectionPos(int adapterPos) {
        Checker.checkPosition(adapterPos, getTotalItemCount());
        int section = getSectionByAdapterPos(adapterPos);
        short sectionType = sectionToType.get(section);
        SectionAdapter adapter = typeToAdapter.get(sectionType);
        return adapterPos - (section > 0 ? sectionToPosSum.get(section - 1) : 0) - (adapter.isHeaderVisible() ? 1 : 0);
    }

    /* END SECTION POSITION PROVIDER */

    private boolean isTypeHeader(int type) {
        return type < 0;
    }

    private void updatePosSum(int startSection, int cnt, boolean updateSection) {
        for (int s = startSection; s < getSectionCount(); s++) {
            if (updateSection) {
                short sectionType = sectionToType.get(s);
                typeToAdapter.get(sectionType).section = s;
            }
            int prevSum = sectionToPosSum.get(s);
            sectionToPosSum.set(s, prevSum + cnt);
        }
    }

    //Finds first pos where val greater than key (where key value should be inserted (after other equal ones))
    private static int upperBoundBinarySearch(List<Integer> list, int key) {
        int l = 0, r = list.size() - 1;
        while (true) {
            if (l == r) {
                if (key < list.get(l)) {
                    return l;
                } else {
                    return l + 1;
                }
            }
            if (l + 1 == r) {
                if (key < list.get(l)) {
                    return l;
                } else if (key < list.get(r)) {
                    return r;
                } else {
                    return r + 1;
                }
            }
            int m = (l + r) / 2;
            if (key < list.get(m)) {
                r = m;
            } else {
                l = m + 1;
            }
        }
    }

    class MockViewHolder extends RecyclerView.ViewHolder {

        SectionAdapter.ItemViewHolder itemViewHolder;
        SectionAdapter.ViewHolder headerViewHolder;

        public MockViewHolder(SectionAdapter.ItemViewHolder itemViewHolder) {
            super(itemViewHolder.itemView);
            this.itemViewHolder = itemViewHolder;
        }

        public MockViewHolder(SectionAdapter.ViewHolder headerViewHolder) {
            super(headerViewHolder.itemView);
            this.headerViewHolder = headerViewHolder;
        }

    }

}
