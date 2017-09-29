package com.cruxlab.sectionedrecyclerview.lib;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class SectionDataManager implements SectionManager, SectionItemManager, SectionPositionProvider {

    private short freeType = 1;
    private short topSectionType = -1;
    private HeaderViewManager headerViewManager;
    private ArrayList<Integer> sectionToPosSum;
    private ArrayList<Short> sectionToType;
    private SparseArray<SectionAdapter> typeToAdapter;
    private SparseArray<SectionItemSwipeCallback> typeToCallback;
    private SparseArray<SectionAdapter.ViewHolder> typeToHeaderVH;

    /* RECYCLER VIEW ADAPTER */

    private RecyclerView.Adapter<ViewHolderWrapper> adapter = new RecyclerView.Adapter<ViewHolderWrapper>() {

        @Override
        public ViewHolderWrapper onCreateViewHolder(ViewGroup parent, int type) {
            short sectionType = (short) (type);
            short itemType = (short) (type >> 16);
            if (isTypeHeader(sectionType)) {
                SectionAdapter adapter = typeToAdapter.get(-sectionType);
                SectionAdapter.ViewHolder headerViewHolder = adapter.onCreateHeaderViewHolder(parent);
                return new ViewHolderWrapper(headerViewHolder);
            } else {
                SectionAdapter adapter = typeToAdapter.get(sectionType);
                SectionAdapter.ItemViewHolder itemViewHolder = adapter.onCreateViewHolder(parent, itemType);
                ViewHolderWrapper viewHolderWrapper = new ViewHolderWrapper(itemViewHolder);
                itemViewHolder.viewHolderWrapper = viewHolderWrapper;
                itemViewHolder.sectionPositionProvider = SectionDataManager.this;
                return viewHolderWrapper;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolderWrapper viewHolderWrapper, int position) {
            int type = getItemViewType(position);
            short sectionType = (short) (type);
            if (isTypeHeader(sectionType)) {
                SectionAdapter adapter = typeToAdapter.get(-sectionType);
                adapter.onBindHeaderViewHolder(viewHolderWrapper.viewHolder);
            } else {
                int sectionPos = getSectionPos(position);
                SectionAdapter adapter = typeToAdapter.get(sectionType);
                SectionAdapter.ItemViewHolder itemViewHolder = (SectionAdapter.ItemViewHolder) viewHolderWrapper.viewHolder;
                adapter.onBindViewHolder(itemViewHolder, sectionPos);
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

    /* END RECYCLER VIEW ADAPTER */
    /* ITEM TOUCH HELPER CALLBACK */

    private ItemTouchHelper.Callback swipeCallback = new ItemTouchHelper.Callback() {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = 0;
            if (!isTypeHeader(viewHolder.getItemViewType())) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                if (swipeCallback != null && swipeCallback.isSwipeEnabled()) {
                    swipeFlags = swipeCallback.getSwipeDirFlags(recyclerView, ((ViewHolderWrapper) viewHolder).viewHolder);
                }
            }
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
            swipeCallback.onSwiped(((ViewHolderWrapper) viewHolder).viewHolder, direction);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                swipeCallback.onChildDraw(c, recyclerView, ((ViewHolderWrapper) viewHolder).viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                swipeCallback.onChildDrawOver(c, recyclerView, ((ViewHolderWrapper) viewHolder).viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                swipeCallback.clearView(recyclerView, ((ViewHolderWrapper) viewHolder).viewHolder);
            } else {
                super.clearView(recyclerView, viewHolder);
            }
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder == null) return;
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                swipeCallback.onSelectedChanged(((ViewHolderWrapper) viewHolder).viewHolder, actionState);
            } else {
                super.onSelectedChanged(viewHolder, actionState);
            }
        }

    };

    /* END ITEM TOUCH HELPER CALLBACK */

    SectionDataManager(HeaderViewManager headerViewManager) {
        this.headerViewManager = headerViewManager;
        sectionToPosSum = new ArrayList<>();
        sectionToType = new ArrayList<>();
        typeToAdapter = new SparseArray<>();
        typeToCallback = new SparseArray<>();
        typeToHeaderVH = new SparseArray<>();
    }

    RecyclerView.Adapter<ViewHolderWrapper> getAdapter() {
        return adapter;
    }

    ItemTouchHelper.Callback getSwipeCallback() {
        return swipeCallback;
    }

    void checkIsHeaderViewChanged() {
        int topPos = headerViewManager.getFirstVisiblePos();
        if (topPos < 0 || topPos >= getTotalItemCount()) {
            removeHeaderView();
            return;
        }
        int section = getSectionByAdapterPos(topPos);
        short sectionType = sectionToType.get(section);
        SectionAdapter adapter = typeToAdapter.get(sectionType);
        if (adapter.isHeaderVisible() && adapter.isHeaderPinned()) {
            if (sectionType == topSectionType) {
                headerViewManager.translateHeaderView(getSectionFirstPos(section + 1));
            } else {
                addHeaderView(section);
            }
        } else {
            removeHeaderView();
        }
    }

    /* SECTION MANAGER */

    @Override
    public int getSectionCount() {
        return typeToAdapter.size();
    }

    @Override
    public void addSection(@NonNull SectionAdapter sectionAdapter) {
        addSection(sectionAdapter, null);
    }

    @Override
    public void addSection(@NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback) {
        sectionAdapter.section = getSectionCount();
        sectionAdapter.setItemManager(this);
        int start = getTotalItemCount();
        int cnt = sectionAdapter.getItemCount() + (sectionAdapter.isHeaderVisible() ? 1 : 0);
        int posSum = getTotalItemCount() + cnt;
        typeToAdapter.put(freeType, sectionAdapter);
        if (swipeCallback != null) {
            typeToCallback.put(freeType, swipeCallback);
        }
        sectionToType.add(freeType);
        sectionToPosSum.add(posSum);
        freeType++;
        adapter.notifyItemRangeInserted(start, cnt);
    }

    @Override
    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter) {
        insertSection(section, sectionAdapter, null);
    }

    @Override
    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback) {
        Checker.checkSection(section, getSectionCount() + 1);
        sectionAdapter.section = section;
        sectionAdapter.setItemManager(this);
        int start = getSectionFirstPos(section);
        int cnt = sectionAdapter.getItemCount() + (sectionAdapter.isHeaderVisible() ? 1 : 0);
        int posSum = (section > 0 ? sectionToPosSum.get(section - 1) : 0) + cnt;
        typeToAdapter.put(freeType, sectionAdapter);
        if (swipeCallback != null) {
            typeToCallback.put(freeType, swipeCallback);
        }
        sectionToType.add(section, freeType);
        sectionToPosSum.add(section, posSum);
        freeType++;
        updatePosSum(section + 1, cnt, true);
        adapter.notifyItemRangeInserted(start, cnt);
    }

    @Override
    public void replaceSection(int section, @NonNull SectionAdapter sectionAdapter) {
        replaceSection(section, sectionAdapter, null);
    }

    @Override
    public void replaceSection(int section, @NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback) {
        Checker.checkSection(section, getSectionCount());
        removeSection(section);
        if (section == getSectionCount()) {
            addSection(sectionAdapter);
        } else {
            insertSection(section, sectionAdapter);
        }
    }

    @Override
    public void removeSection(int section) {
        Checker.checkSection(section, getSectionCount());
        short sectionType = sectionToType.get(section);
        int cnt = getSectionCurItemCount(section);
        int start = getSectionFirstPos(section);
        typeToAdapter.remove(sectionType);
        typeToCallback.remove(sectionType);
        typeToHeaderVH.remove(sectionType);
        sectionToType.remove(section);
        sectionToPosSum.remove(section);
        updatePosSum(section, -cnt, true);
        adapter.notifyItemRangeRemoved(start, cnt);
    }

    @Override
    public void updateSection(int section) {
        Checker.checkSection(section, getSectionCount());
        adapter.notifyItemRangeChanged(getSectionFirstPos(section), getSectionCurItemCount(section));
        updateHeaderView(sectionToType.get(section));
    }

    @Override
    public void setSwipeCallback(int section, @NonNull SectionItemSwipeCallback swipeCallback) {
        Checker.checkSection(section, getSectionCount());
        short sectionType = sectionToType.get(section);
        typeToCallback.put(sectionType, swipeCallback);
    }

    @Override
    public void removeSwipeCallback(int section) {
        Checker.checkSection(section, getSectionCount());
        short sectionType = sectionToType.get(section);
        typeToCallback.remove(sectionType);
    }

    @Override
    public SectionAdapter getSectionAdapter(int section) {
        Checker.checkSection(section, getSectionCount());
        short sectionType = sectionToType.get(section);
        return typeToAdapter.get(sectionType);
    }

    @Override
    public SectionItemSwipeCallback getSwipeCallback(int section) {
        Checker.checkSection(section, getSectionCount());
        short sectionType = sectionToType.get(section);
        return typeToCallback.get(sectionType);
    }

    /* END SECTION MANAGER */
    /* SECTION ITEM MANAGER */

    @Override
    public void notifyInserted(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getTotalItemCount() + 1);
        updatePosSum(section, 1, false);
        adapter.notifyItemInserted(adapterPos);
    }

    @Override
    public void notifyRemoved(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getTotalItemCount() + 1);
        updatePosSum(section, -1, false);
        adapter.notifyItemRemoved(adapterPos);
    }

    @Override
    public void notifyChanged(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getTotalItemCount());
        adapter.notifyItemChanged(adapterPos);
    }

    @Override
    public void notifyRangeInserted(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getTotalItemCount());
        updatePosSum(section, cnt, false);
        adapter.notifyItemRangeInserted(adapterStartPos, cnt);
    }

    @Override
    public void notifyRangeRemoved(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getTotalItemCount());
        updatePosSum(section, -cnt, false);
        adapter.notifyItemRangeRemoved(adapterStartPos, cnt);
    }

    @Override
    public void notifyRangeChanged(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getTotalItemCount());
        adapter.notifyItemRangeChanged(adapterStartPos, cnt);
    }

    @Override
    public void notifyMoved(int section, int fromPos, int toPos) {
        int adapterFromPos = getAdapterPos(section, fromPos);
        Checker.checkPosition(adapterFromPos, getTotalItemCount());
        int adapterToPos = getAdapterPos(section, toPos);
        Checker.checkPosition(adapterToPos, getTotalItemCount());
        adapter.notifyItemMoved(adapterFromPos, adapterToPos);
    }

    @Override
    public void notifyHeaderChanged(int section) {
        short sectionType = sectionToType.get(section);
        if (!typeToAdapter.get(sectionType).isHeaderVisible()) return;
        int headerPos = getSectionFirstPos(section);
        adapter.notifyItemChanged(headerPos);
        updateHeaderView(sectionToType.get(section));
    }

    @Override
    public void notifyHeaderVisibilityChanged(int section, boolean visible) {
        Checker.checkSection(section, getSectionCount());
        if (visible) {
            updatePosSum(section, 1, false);
            adapter.notifyItemInserted(getSectionFirstPos(section));
        } else {
            updatePosSum(section, -1, false);
            adapter.notifyItemRemoved(getSectionFirstPos(section));
        }
    }

    @Override
    public void notifyHeaderPinnedStateChanged(int section, boolean pinned) {
        Checker.checkSection(section, getSectionCount());
        checkIsHeaderViewChanged();
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

    private int getTotalItemCount() {
        return getSectionCount() > 0 ? sectionToPosSum.get(getSectionCount() - 1) : 0;
    }

    private int getSectionByAdapterPos(int adapterPos) {
        Checker.checkPosition(adapterPos, getTotalItemCount());
        return upperBoundBinarySearch(sectionToPosSum, adapterPos);
    }

    private SectionItemSwipeCallback getSwipeCallback(RecyclerView.ViewHolder viewHolder) {
        int adapterPos = viewHolder.getAdapterPosition();
        int section = getSectionByAdapterPos(adapterPos);
        short sectionType = sectionToType.get(section);
        return typeToCallback.get(sectionType);
    }

    private int getSectionFirstPos(int section) {
        Checker.checkSection(section, getSectionCount() + 1);
        return section > 0 ? sectionToPosSum.get(section - 1) : 0;
    }

    private int getAdapterPos(int section, int sectionPos) {
        Checker.checkSection(section, getSectionCount());
        Checker.checkPosition(sectionPos, getTotalItemCount());
        short sectionType = sectionToType.get(section);
        SectionAdapter adapter = typeToAdapter.get(sectionType);
        return (section > 0 ? sectionToPosSum.get(section - 1) : 0) + sectionPos + (adapter.isHeaderVisible() ? 1 : 0);
    }

    private int getSectionCurItemCount(int section) {
        Checker.checkSection(section, getSectionCount());
        return sectionToPosSum.get(section) - (section > 0 ? sectionToPosSum.get(section - 1) : 0);
    }

    private SectionAdapter.ViewHolder getHeaderVH(short sectionType) {
        SectionAdapter.ViewHolder headerViewHolder = typeToHeaderVH.get(sectionType);
        if (headerViewHolder == null) {
            ViewGroup parent = headerViewManager.getHeaderViewParent();
            SectionAdapter adapter = typeToAdapter.get(sectionType);
            headerViewHolder = adapter.onCreateHeaderViewHolder(parent);
            typeToHeaderVH.put(topSectionType, headerViewHolder);
        }
        return headerViewHolder;
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

    private void addHeaderView(int section) {
        topSectionType = sectionToType.get(section);
        SectionAdapter.ViewHolder headerViewHolder = getHeaderVH(topSectionType);
        SectionAdapter adapter = typeToAdapter.get(topSectionType);
        adapter.onBindHeaderViewHolder(headerViewHolder);
        headerViewManager.addHeaderView(headerViewHolder.itemView, getSectionFirstPos(section + 1));
    }

    private void updateHeaderView(short sectionType) {
        if (sectionType != topSectionType) return;
        SectionAdapter.ViewHolder headerViewHolder = getHeaderVH(sectionType);
        SectionAdapter adapter = typeToAdapter.get(sectionType);
        adapter.onBindHeaderViewHolder(headerViewHolder);
    }

    private void removeHeaderView() {
        if (topSectionType != -1) {
            headerViewManager.removeHeaderView();
            topSectionType = -1;
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

    class ViewHolderWrapper extends RecyclerView.ViewHolder {

        final SectionAdapter.ViewHolder viewHolder;

        ViewHolderWrapper(SectionAdapter.ViewHolder viewHolder) {
            super(viewHolder.itemView);
            this.viewHolder = viewHolder;
        }

    }

}
