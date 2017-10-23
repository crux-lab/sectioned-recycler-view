package com.cruxlab.sectionedrecyclerview.lib;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Stores and manages all data for SectionedRecyclerView.
 * <p>
 * Items in SectionedRecyclerView are divided into groups - sections. Each section consists of
 * regular items and an optional header (just another item for the real RecyclerView), which can be
 * represented as views using corresponding {@link SectionAdapter} or {@link SectionWithHeaderAdapter}.
 * <p>
 * Each section obtains own unique type stored in {@link #sectionToType}. It is used to determine
 * that the section which corresponds to the given global adapter position has changed, so the
 * corresponding RecyclerView.ViewHolder should be recreated. Each SectionAdapter also can use short
 * values to distinguish own items.
 * <p>
 * The main task is to determine, which section corresponds to the given global adapter position and
 * whether it is a header or a regular item in it. To do it efficiently we use partial sum array
 * {@link #sectionToPosSum}, where on the i-th position is the number of items in RecyclerView in all
 * sections before i-th inclusive, and binary search (e.g. {@link #getSection(int)}).
 * For details on how RecyclerView interacts with sections see the RecyclerView.Adapter {@link #adapter}
 * and ItemTouchHelper.Callback {@link #swipeCallback} implementations.
 * <p>
 * This class also manages header state. It determines, which header view corresponds to the first
 * visible adapter position, and adds/removes/translates the header view via {@link HeaderViewManager}.
 * The contents of the current header view can be updated by rebinding the corresponding
 * {@link SectionAdapter.ViewHolder}. The duplicated header SectionAdapter.ViewHolders for
 * {@link SectionedRVLayout} can be obtained by calling {@link #getHeaderVH(short)} and are stored
 * in {@link #typeToHeaderVH}, so that each of them is created only once.
 * @see #checkIsHeaderViewChanged()
 * @see #updateHeaderView(short)
 *
 */
class SectionDataManager implements SectionManager, SectionItemManager, PositionConverter {

    private short freeType = 1;
    private short topSectionType = -1;
    private HeaderViewManager headerViewManager;
    private ArrayList<Integer> sectionToPosSum;
    private ArrayList<Short> sectionToType;
    private SparseArray<SectionAdapterWrapper> typeToAdapter;
    private SparseArray<SectionItemSwipeCallback> typeToCallback;
    private SparseArray<SectionAdapter.ViewHolder> typeToHeaderVH;

    /**
     * This RecyclerView.Adapter implementation provides interaction between RecyclerView and
     * SectionAdapters.
     * <p>
     * Can be obtained by calling {@link #getAdapter()}.
     *
     * @see SectionAdapter
     * @see SectionWithHeaderAdapter
     * @see SectionAdapter.ViewHolder
     */
    private RecyclerView.Adapter<ViewHolderWrapper> adapter = new RecyclerView.Adapter<ViewHolderWrapper>() {

        /**
         * Uses type to get an appropriate SectionAdapterWrapper, item type within section and to
         * determine, whether item view is a section header. Passes the corresponding call to the
         * SectionAdapter via {@link SectionAdapterWrapper}, obtaining
         * {@link SectionAdapter.ViewHolder}. Returns {@link ViewHolderWrapper}, that refers to the
         * same View. SectionAdapter.ViewHolder holds a reference to it to access the global adapter
         * position any time.
         */
        @Override
        public ViewHolderWrapper onCreateViewHolder(ViewGroup parent, int type) {
            short sectionType = (short) (type);
            short itemType = (short) (type >> 16);
            if (isTypeHeader(sectionType)) {
                SectionAdapterWrapper adapterWrapper = typeToAdapter.get(-sectionType);
                SectionAdapter.ViewHolder headerViewHolder = adapterWrapper.onCreateHeaderViewHolder(parent);
                ViewHolderWrapper viewHolderWrapper = new ViewHolderWrapper(headerViewHolder);
                headerViewHolder.viewHolderWrapper = viewHolderWrapper;
                headerViewHolder.positionConverter = SectionDataManager.this;
                return viewHolderWrapper;
            } else {
                SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
                SectionAdapter.ItemViewHolder itemViewHolder = adapterWrapper.onCreateViewHolder(parent, itemType);
                ViewHolderWrapper viewHolderWrapper = new ViewHolderWrapper(itemViewHolder);
                itemViewHolder.viewHolderWrapper = viewHolderWrapper;
                itemViewHolder.positionConverter = SectionDataManager.this;
                return viewHolderWrapper;
            }
        }

        /**
         * Uses position to determine section type and whether item view is a section header.
         * Obtains {@link SectionAdapter.ViewHolder} from {@link ViewHolderWrapper} and passes the
         * corresponding call to the SectionAdapter via {@link SectionAdapterWrapper}.
         */
        @Override
        public void onBindViewHolder(ViewHolderWrapper viewHolderWrapper, int position) {
            int type = getItemViewType(position);
            short sectionType = (short) (type);
            if (isTypeHeader(sectionType)) {
                SectionAdapterWrapper adapterWrapper = typeToAdapter.get(-sectionType);
                adapterWrapper.onBindHeaderViewHolder(viewHolderWrapper.viewHolder);
            } else {
                int sectionPos = getPosInSection(position);
                SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
                SectionAdapter.ItemViewHolder itemViewHolder = (SectionAdapter.ItemViewHolder) viewHolderWrapper.viewHolder;
                adapterWrapper.onBindViewHolder(itemViewHolder, sectionPos);
            }
        }

        @Override
        public int getItemCount() {
            return getTotalItemCount();
        }

        /**
         * Item view type allows to determine section type, item type within section and whether
         * item view is a section header. It is an integer, consisted of two shorts as follows:
         * <code>(itemType << 16) + sectionType</code>,
         * where <code>itemType</code> is an item type within section, obtained from
         * SectionAdapterWrapper, and <code>sectionType</code> is a section type, calculated from
         * adapter position, which is negative (multiplied by -1) when the given item view
         * corresponds to a section header.
         */
        @Override
        public int getItemViewType(int pos) {
            Checker.checkPosition(pos, getTotalItemCount());
            int section = getSection(pos);
            short sectionType = sectionToType.get(section);
            int sectionPos = getPosInSection(pos);
            SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
            short itemType = adapterWrapper.getItemViewType(sectionPos);
            if (adapterWrapper.isHeaderVisible() && getSectionFirstPos(section) == pos) sectionType *= -1;
            return (itemType << 16) + sectionType;
        }

    };

    /**
     * ItemTouchHelper.Callback implementation provides interaction between RecyclerView and
     * SectionItemSwipeCallbacks.
     * <p>
     * Can be obtained by calling {@link #getSwipeCallback()}.
     *
     * @see SectionItemSwipeCallback
     */
    private ItemTouchHelper.Callback swipeCallback = new ItemTouchHelper.Callback() {

        /**
         * Restricts dragging for all items and swiping for section headers. Returns swipe flags
         * obtained from corresponding {@link SectionItemSwipeCallback}, if it exists and the
         * swiping is enabled.
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = 0;
            if (!isTypeHeader(viewHolder.getItemViewType())) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                if (swipeCallback != null && swipeCallback.isSwipeEnabled()) {
                    ViewHolderWrapper viewHolderWrapper = (ViewHolderWrapper) viewHolder;
                    swipeFlags = swipeCallback.getSwipeDirFlags(recyclerView, (SectionAdapter.ItemViewHolder) viewHolderWrapper.viewHolder);
                }
            }
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            return false;
        }

        /**
         * Passes the corresponding call to the {@link SectionItemSwipeCallback}.
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
            ViewHolderWrapper viewHolderWrapper = (ViewHolderWrapper) viewHolder;
            swipeCallback.onSwiped((SectionAdapter.ItemViewHolder) viewHolderWrapper.viewHolder, direction);
        }

        /**
         * Passes the corresponding call to the {@link SectionItemSwipeCallback}.
         */
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                ViewHolderWrapper viewHolderWrapper = (ViewHolderWrapper) viewHolder;
                swipeCallback.onChildDraw(c, recyclerView,
                        (SectionAdapter.ItemViewHolder) viewHolderWrapper.viewHolder,
                        dX, dY, actionState, isCurrentlyActive);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        /**
         * Passes the corresponding call to the {@link SectionItemSwipeCallback}.
         */
        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                ViewHolderWrapper viewHolderWrapper = (ViewHolderWrapper) viewHolder;
                swipeCallback.onChildDrawOver(c, recyclerView,
                        (SectionAdapter.ItemViewHolder) viewHolderWrapper.viewHolder,
                        dX, dY, actionState, isCurrentlyActive);
            } else {
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        /**
         * Passes the corresponding call to the {@link SectionItemSwipeCallback}.
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                ViewHolderWrapper viewHolderWrapper = (ViewHolderWrapper) viewHolder;
                swipeCallback.clearView(recyclerView,
                        (SectionAdapter.ItemViewHolder) viewHolderWrapper.viewHolder);
            } else {
                super.clearView(recyclerView, viewHolder);
            }
        }

        /**
         * Passes the corresponding call to the {@link SectionItemSwipeCallback}.
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder == null) return;
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemSwipeCallback swipeCallback = getSwipeCallback(viewHolder);
                ViewHolderWrapper viewHolderWrapper = (ViewHolderWrapper) viewHolder;
                swipeCallback.onSelectedChanged(
                        (SectionAdapter.ItemViewHolder) viewHolderWrapper.viewHolder, actionState);
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

    /***
     * Returns Adapter for RecyclerView, which interacts with sections passing calls to
     * SectionAdapters.
     *
     * @return RecyclerView.Adapter implementation.
     */
    RecyclerView.Adapter<ViewHolderWrapper> getAdapter() {
        return adapter;
    }

    /**
     * Returns Callback for RecyclerView's ItemTouchHelper, which interacts with sections passing
     * calls to SectionItemSwipeCallbacks.
     *
     * @return ItemTouchHelper.Callback implementation.
     */
    ItemTouchHelper.Callback getSwipeCallback() {
        return swipeCallback;
    }

    /**
     * Checks, whether the header should be updated (added/removed/translated) based on the first
     * visible position (e.g. called after swipe). To update the contents of the corresponding
     * header SectionAdapter.ViewHolder you should call {@link #updateHeaderView(short)}.
     * <p>
     * Interacts with header view via {@link HeaderViewManager}. Current header view section type
     * is stored in {@link #topSectionType}.
     */
    void checkIsHeaderViewChanged() {
        int topPos = headerViewManager.getFirstVisiblePos();
        if (topPos < 0 || topPos >= getTotalItemCount()) {
            removeHeaderView();
            return;
        }
        int section = getSection(topPos);
        short sectionType = sectionToType.get(section);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        if (adapterWrapper.isHeaderVisible() && adapterWrapper.isHeaderPinned()) {
            if (sectionType == topSectionType) {
                int nextHeaderPos = getSectionFirstPos(section + 1);
                headerViewManager.translateHeaderView(nextHeaderPos);
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
    public void addSection(@NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter) {
        addSection(sectionWithHeaderAdapter, null);
    }

    @Override
    public void addSection(@NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback) {
        addSection(new SectionAdapterWrapper(sectionAdapter), swipeCallback);
    }

    @Override
    public void addSection(@NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter, SectionItemSwipeCallback swipeCallback) {
        addSection(new SectionAdapterWrapper(sectionWithHeaderAdapter), swipeCallback);
    }

    private void addSection(SectionAdapterWrapper sectionAdapterWrapper, SectionItemSwipeCallback swipeCallback) {
        sectionAdapterWrapper.setSection(getSectionCount());
        sectionAdapterWrapper.setItemManager(this);
        int start = getTotalItemCount();
        int cnt = sectionAdapterWrapper.getItemCount() + (sectionAdapterWrapper.isHeaderVisible() ? 1 : 0);
        int posSum = getTotalItemCount() + cnt;
        typeToAdapter.put(freeType, sectionAdapterWrapper);
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
    public void insertSection(int section, @NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter) {
        insertSection(section, sectionWithHeaderAdapter, null);
    }

    @Override
    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback) {
        insertSection(section, new SectionAdapterWrapper(sectionAdapter), swipeCallback);
    }

    @Override
    public void insertSection(int section, @NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter, SectionItemSwipeCallback swipeCallback) {
        insertSection(section, new SectionAdapterWrapper(sectionWithHeaderAdapter), swipeCallback);
    }

    private void insertSection(int section, SectionAdapterWrapper sectionAdapterWrapper, SectionItemSwipeCallback swipeCallback) {
        Checker.checkSection(section, getSectionCount() + 1);
        sectionAdapterWrapper.setSection(section);
        sectionAdapterWrapper.setItemManager(this);
        int start = getSectionFirstPos(section);
        int cnt = sectionAdapterWrapper.getItemCount() + (sectionAdapterWrapper.isHeaderVisible() ? 1 : 0);
        int posSum = (section > 0 ? sectionToPosSum.get(section - 1) : 0) + cnt;
        typeToAdapter.put(freeType, sectionAdapterWrapper);
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
    public void replaceSection(int section, @NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter) {
        replaceSection(section, sectionWithHeaderAdapter, null);
    }

    @Override
    public void replaceSection(int section, @NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback) {
        replaceSection(section, new SectionAdapterWrapper(sectionAdapter), swipeCallback);
    }

    @Override
    public void replaceSection(int section, @NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter, SectionItemSwipeCallback swipeCallback) {
        replaceSection(section, new SectionAdapterWrapper(sectionWithHeaderAdapter), swipeCallback);
    }

    private void replaceSection(int section, SectionAdapterWrapper sectionAdapterWrapper, SectionItemSwipeCallback swipeCallback) {
        Checker.checkSection(section, getSectionCount());
        removeSection(section);
        if (section == getSectionCount()) {
            addSection(sectionAdapterWrapper, swipeCallback);
        } else {
            insertSection(section, sectionAdapterWrapper, swipeCallback);
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
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        return adapterWrapper.getAdapter();
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
        Checker.checkPosRange(adapterStartPos, cnt, getTotalItemCount() + 1);
        updatePosSum(section, cnt, false);
        adapter.notifyItemRangeInserted(adapterStartPos, cnt);
    }

    @Override
    public void notifyRangeRemoved(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getTotalItemCount() + 1);
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
    /* SECTION POSITION CONVERTER */

    @Override
    public int getAdapterPos(int section, int sectionPos) {
        if (section < 0 || section >= getSectionCount()) return -1;
        short sectionType = sectionToType.get(section);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        int sectionItemCount = getSectionCurItemCount(section) - (adapterWrapper.isHeaderVisible() ? 1 : 0);
        if (sectionPos < 0 || sectionPos >= sectionItemCount) return -1;
        return (section > 0 ? sectionToPosSum.get(section - 1) : 0) + sectionPos + (adapterWrapper.isHeaderVisible() ? 1 : 0);
    }

    @Override
    public int getSection(int adapterPos) {
        if (adapterPos < 0 || adapterPos >= getTotalItemCount()) return -1;
        return upperBoundBinarySearch(sectionToPosSum, adapterPos);
    }

    @Override
    public int getPosInSection(int adapterPos) {
        if (adapterPos < 0 || adapterPos >= getTotalItemCount()) return -1;
        int section = getSection(adapterPos);
        short sectionType = sectionToType.get(section);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        return adapterPos - (section > 0 ? sectionToPosSum.get(section - 1) : 0) - (adapterWrapper.isHeaderVisible() ? 1 : 0);
    }

    /* END SECTION POSITION CONVERTER */

    /**
     * Checks whether the given item view type corresponds to header view.
     *
     * @see #adapter's getItemViewType(int)
     * @param type Item view type.
     * @return True if the given type corresponds to header, false otherwise.
     */
    private boolean isTypeHeader(int type) {
        return type < 0;
    }

    /**
     * Returns the total number of items in RecyclerView, that is the sum of SectionAdapters item
     * counts and the number of headers.
     *
     * @return Total number of items in RecyclerView.
     */
    private int getTotalItemCount() {
        return getSectionCount() > 0 ? sectionToPosSum.get(getSectionCount() - 1) : 0;
    }

    /**
     * Returns SectionItemSwipeCallback for the given ViewHolder or null, if the obtained adapter
     * position is invalid.
     *
     * @param viewHolder ViewHolder of the swiped view.
     * @return Corresponding SectionItemSwipeCallback if exists, or null.
     */
    private SectionItemSwipeCallback getSwipeCallback(RecyclerView.ViewHolder viewHolder) {
        int adapterPos = viewHolder.getAdapterPosition();
        if (adapterPos < 0 || adapterPos >= getTotalItemCount()) return null;
        int section = getSection(adapterPos);
        short sectionType = sectionToType.get(section);
        return typeToCallback.get(sectionType);
    }

    /**
     * Returns the first global adapter position, that corresponds to the given section (can be
     * either item or header view).
     *
     * @param section Index of the section.
     * @return First global adapter position.
     */
    private int getSectionFirstPos(int section) {
        Checker.checkSection(section, getSectionCount() + 1);
        return section > 0 ? sectionToPosSum.get(section - 1) : 0;
    }

    /**
     * Returns the number of items in RecyclerView, which currently corresponds to the given section
     * index (including header if it is visible).
     *
     * @param section Index of the section.
     * @return Number of items.
     */
    private int getSectionCurItemCount(int section) {
        Checker.checkSection(section, getSectionCount());
        return sectionToPosSum.get(section) - (section > 0 ? sectionToPosSum.get(section - 1) : 0);
    }

    /**
     * Returns header SectionAdapter.ViewHolder for the given section type. Creates one only if it
     * doesn't exist yet and stores it in {@link #typeToHeaderVH}.
     *
     * @param sectionType Type of the section.
     * @return SectionAdapter.ViewHolder of the header.
     */
    private SectionAdapter.ViewHolder getHeaderVH(short sectionType) {
        SectionAdapter.ViewHolder headerViewHolder = typeToHeaderVH.get(sectionType);
        if (headerViewHolder == null) {
            ViewGroup parent = headerViewManager.getHeaderViewParent();
            SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
            headerViewHolder = adapterWrapper.onCreateHeaderViewHolder(parent);
            typeToHeaderVH.put(topSectionType, headerViewHolder);
        }
        return headerViewHolder;
    }

    /**
     * Updates by <code>cnt</code> items the partial sum array of item counts starting with section
     * <code>startSection</code>. If <code>updateSection</code> is true, updates the section indexes
     * of the corresponding adapters.
     *
     * @param startSection  First section index to be updated.
     * @param cnt           Value to be updated by.
     * @param updateSection True if the adapters section indexes should be updated.
     */
    private void updatePosSum(int startSection, int cnt, boolean updateSection) {
        for (int s = startSection; s < getSectionCount(); s++) {
            if (updateSection) {
                short sectionType = sectionToType.get(s);
                typeToAdapter.get(sectionType).setSection(s);
            }
            int prevSum = sectionToPosSum.get(s);
            sectionToPosSum.set(s, prevSum + cnt);
        }
    }

    /**
     * Notifies {@link HeaderViewManager} that the header view should be added, passing next header
     * position for calculations.
     *
     * @param section Index of the top section.
     */
    private void addHeaderView(int section) {
        topSectionType = sectionToType.get(section);
        SectionAdapter.ViewHolder headerViewHolder = getHeaderVH(topSectionType);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(topSectionType);
        adapterWrapper.onBindHeaderViewHolder(headerViewHolder);
        int nextHeaderPos = getSectionFirstPos(section + 1);
        headerViewManager.addHeaderView(headerViewHolder.itemView, nextHeaderPos);
    }

    /**
     * Updates the contents of the duplicated header view if <code>sectionType</code> matches the
     * current {@link #topSectionType}.
     *
     * @param sectionType Type of the updated section.
     */
    private void updateHeaderView(short sectionType) {
        if (sectionType != topSectionType) return;
        SectionAdapter.ViewHolder headerViewHolder = getHeaderVH(sectionType);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        adapterWrapper.onBindHeaderViewHolder(headerViewHolder);
    }

    /**
     * Notifies {@link HeaderViewManager} that the header view should be removed.
     */
    private void removeHeaderView() {
        if (topSectionType != -1) {
            headerViewManager.removeHeaderView();
            topSectionType = -1;
        }
    }

    /**
     * Upper bound binary search implementation. Finds the first position in the list, where the
     * value is greater than the given key. In other words, where the key value should be inserted
     * (after other equal ones).
     *
     * @param list List where to search.
     * @param key  Value to be found.
     * @return Upper bound binary search result position.
     */
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

    /**
     * RecyclerView.ViewHolder implementation to work with {@link #adapter}. Contains the
     * corresponding {@link SectionAdapter.ViewHolder} and refers to the same View.
     */
    class ViewHolderWrapper extends RecyclerView.ViewHolder {

        final SectionAdapter.ViewHolder viewHolder;

        ViewHolderWrapper(SectionAdapter.ViewHolder viewHolder) {
            super(viewHolder.itemView);
            this.viewHolder = viewHolder;
        }

    }

    /**
     * Contains {@link SectionAdapter} or {@link SectionWithHeaderAdapter}. Passes calls to non null
     * adapter instance, handling unsupported calls for SectionAdapter without header.
     */
    class SectionAdapterWrapper {

        private final SectionAdapter sectionAdapter;
        private final SectionWithHeaderAdapter sectionWithHeaderAdapter;

        SectionAdapterWrapper(SectionAdapter sectionAdapter) {
            this.sectionAdapter = sectionAdapter;
            this.sectionWithHeaderAdapter = null;
        }

        SectionAdapterWrapper(SectionWithHeaderAdapter sectionWithHeaderAdapter) {
            this.sectionWithHeaderAdapter = sectionWithHeaderAdapter;
            this.sectionAdapter = null;
        }

        SectionAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, short type) {
            if (sectionWithHeaderAdapter != null) {
                return sectionWithHeaderAdapter.onCreateViewHolder(parent, type);
            } else {
                return sectionAdapter.onCreateViewHolder(parent, type);
            }
        }

        SectionAdapter.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            if (sectionWithHeaderAdapter != null) {
                return sectionWithHeaderAdapter.onCreateHeaderViewHolder(parent);
            } else {
                return null;
            }
        }

        void onBindViewHolder(SectionAdapter.ItemViewHolder holder, int position) {
            if (sectionWithHeaderAdapter != null) {
                sectionWithHeaderAdapter.onBindViewHolder(holder, position);
            } else {
                sectionAdapter.onBindViewHolder(holder, position);
            }
        }

        void onBindHeaderViewHolder(SectionAdapter.ViewHolder holder) {
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
            if (sectionWithHeaderAdapter != null) {
                return sectionWithHeaderAdapter.isHeaderVisible();
            } else {
                return false;
            }
        }

        boolean isHeaderPinned() {
            if (sectionWithHeaderAdapter != null) {
                return sectionWithHeaderAdapter.isHeaderPinned();
            } else {
                return false;
            }
        }

        void setSection(int section) {
            if (sectionWithHeaderAdapter != null) {
                sectionWithHeaderAdapter.section = section;
            } else {
                sectionAdapter.section = section;
            }
        }

        SectionAdapter getAdapter() {
            if (sectionWithHeaderAdapter != null) {
                return sectionWithHeaderAdapter;
            } else {
                return sectionAdapter;
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

}
