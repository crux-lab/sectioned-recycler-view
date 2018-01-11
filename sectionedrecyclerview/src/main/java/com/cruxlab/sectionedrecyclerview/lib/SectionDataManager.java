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


import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores and manages all data for RecyclerView sections.
 * <p>
 * Items in RecyclerView are divided into groups - sections. Each section consists of regular items
 * and an optional header (just another item for the RecyclerView.Adapter implementation), which can
 * be represented as views using corresponding {@link SimpleSectionAdapter} or {@link SectionAdapter}.
 * <p>
 * Each section obtains own unique type stored in {@link #sectionToType}. It is used to determine
 * that the section which corresponds to the given global adapter position has changed, so the
 * corresponding ViewHolder should be recreated. Each BaseSectionAdapter also can use short values
 * to distinguish own items.
 * <p>
 * Each section with header can be added only with a specified header type. It is used to make
 * RecyclerView reuse HeaderViewHolders for different sections. {@link HeaderManager} also uses it
 * to cache and store duplicated HeaderViewHolders, which are created only once for any header type.
 * <p>
 * The main task is to determine, which section corresponds to the given global adapter position and
 * whether it is a header or a regular item in it. To do it efficiently partial sum array is used
 * {@link #sectionToPosSum}, where on the i-th position is the number of items in RecyclerView in all
 * sections before i-th inclusive, and binary search (e.g. {@link #calcSection(int)}).
 * For details on how RecyclerView interacts with sections see the RecyclerView.Adapter {@link #adapter}
 * and ItemTouchHelper.Callback {@link #itemTouchCallback} implementations.
 */
public class SectionDataManager implements SectionManager, PositionConverter {

    private static final short NO_SECTION_TYPE = 0;

    private short freeType = 1;
    private ArrayList<Integer> sectionToPosSum;
    private ArrayList<Short> sectionToType;
    private SparseArray<SectionAdapterWrapper> typeToAdapter;
    private SparseArray<SectionItemTouchCallback> typeToCallback;
    private SparseArray<Set<Short>> headerTypeToSectionTypes;
    private GeneralTouchCallback generalTouchCallback;
    private HeaderManager headerManager;
    private ItemTouchCallback itemTouchCallback;

    public SectionDataManager() {
        sectionToPosSum = new ArrayList<>();
        sectionToType = new ArrayList<>();
        typeToAdapter = new SparseArray<>();
        typeToCallback = new SparseArray<>();
        headerTypeToSectionTypes = new SparseArray<>();
        itemTouchCallback = new ItemTouchCallback();
        generalTouchCallback = new GeneralTouchCallback();
        generalTouchCallback.defaultCallback = itemTouchCallback;
    }

    /**
     * Returns Adapter for RecyclerView, which interacts with sections passing calls to
     * SectionAdapters.
     *
     * @return RecyclerView.Adapter implementation.
     */
    public RecyclerView.Adapter<ViewHolderWrapper> getAdapter() {
        return adapter;
    }

    /**
     * Returns Callback for RecyclerView's ItemTouchHelper, which interacts with sections passing
     * calls to SectionItemTouchCallbacks.
     *
     * @return ItemTouchHelper.Callback implementation.
     */
    public ItemTouchHelper.Callback getTouchCallback() {
        return itemTouchCallback;
    }

    /**
     * Sets the given instance as an implementation for some intersectional methods of {@link #itemTouchCallback}.
     * You can reset to default implementation passing null.
     *
     * @param callback GeneralTouchCallback to use instead of default.
     */
    public void setGeneralTouchCallback(GeneralTouchCallback callback) {
        generalTouchCallback.defaultCallback = null;
        if (callback != null) {
            generalTouchCallback = callback;
        } else {
            generalTouchCallback = new GeneralTouchCallback();
        }
        generalTouchCallback.defaultCallback = itemTouchCallback;
    }

    /**
     * Creates {@link HeaderManager} to interact with {@link SectionHeaderLayout}.
     *
     * @param headerViewManager HeaderViewManager to interact with.
     */
    HeaderManager createHeaderManager(HeaderViewManager headerViewManager) {
        return headerManager = new HeaderManager(headerViewManager);
    }

    /* SECTION MANAGER */

    @Override
    public int getSectionCount() {
        return typeToAdapter.size();
    }

    @Override
    public void addSection(@NonNull SimpleSectionAdapter simpleSectionAdapter) {
        addSection(simpleSectionAdapter, null);
    }

    @Override
    public void addSection(@NonNull SectionAdapter sectionAdapter, short headerType) {
        addSection(sectionAdapter, null, headerType);
    }

    @Override
    public void addSection(@NonNull SimpleSectionAdapter simpleSectionAdapter, SectionItemTouchCallback touchCallback) {
        addSection(new SectionAdapterWrapper(simpleSectionAdapter), touchCallback);
    }

    @Override
    public void addSection(@NonNull SectionAdapter sectionAdapter, SectionItemTouchCallback touchCallback, short headerType) {
        checkHeaderType(headerType);
        addSectionWithHeaderType(headerType, freeType);
        addSection(new SectionAdapterWrapper(sectionAdapter, headerType), touchCallback);
    }

    private void addSection(SectionAdapterWrapper adapterWrapper, SectionItemTouchCallback touchCallback) {
        checkFreeType();
        adapterWrapper.setSection(getSectionCount());
        adapterWrapper.setItemManager(sectionItemManager);
        int start = getTotalItemCount();
        int cnt = adapterWrapper.getItemCount() + adapterWrapper.getHeaderVisibilityInt();
        int posSum = getTotalItemCount() + cnt;
        typeToAdapter.put(freeType, adapterWrapper);
        if (touchCallback != null) {
            touchCallback.defaultCallback = itemTouchCallback;
            typeToCallback.put(freeType, touchCallback);
        }
        sectionToType.add(freeType);
        sectionToPosSum.add(posSum);
        freeType++;
        adapter.notifyItemRangeInserted(start, cnt);
        if (headerManager != null) {
            headerManager.checkFirstVisiblePos();
        }
    }

    @Override
    public void insertSection(int section, @NonNull SimpleSectionAdapter simpleSectionAdapter) {
        insertSection(section, simpleSectionAdapter, null);
    }

    @Override
    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter, short headerType) {
        insertSection(section, sectionAdapter, null, headerType);
    }

    @Override
    public void insertSection(int section, @NonNull SimpleSectionAdapter simpleSectionAdapter, SectionItemTouchCallback touchCallback) {
        insertSection(section, new SectionAdapterWrapper(simpleSectionAdapter), touchCallback);
    }

    @Override
    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter, SectionItemTouchCallback touchCallback, short headerType) {
        checkHeaderType(headerType);
        addSectionWithHeaderType(headerType, freeType);
        insertSection(section, new SectionAdapterWrapper(sectionAdapter, headerType), touchCallback);
    }

    private void insertSection(int section, SectionAdapterWrapper adapterWrapper, SectionItemTouchCallback touchCallback) {
        checkFreeType();
        checkSectionIndex(section, true);
        adapterWrapper.setSection(section);
        adapterWrapper.setItemManager(sectionItemManager);
        int start = getSectionFirstPos(section);
        int cnt = adapterWrapper.getItemCount() + adapterWrapper.getHeaderVisibilityInt();
        int posSum = (section > 0 ? sectionToPosSum.get(section - 1) : 0) + cnt;
        typeToAdapter.put(freeType, adapterWrapper);
        if (touchCallback != null) {
            touchCallback.defaultCallback = itemTouchCallback;
            typeToCallback.put(freeType, touchCallback);
        }
        sectionToType.add(section, freeType);
        sectionToPosSum.add(section, posSum);
        freeType++;
        updatePosSum(section + 1, cnt, true);
        adapter.notifyItemRangeInserted(start, cnt);
        if (headerManager != null) {
            headerManager.checkFirstVisiblePos();
        }
    }

    @Override
    public void replaceSection(int section, @NonNull SimpleSectionAdapter simpleSectionAdapter) {
        replaceSection(section, simpleSectionAdapter, null);
    }

    @Override
    public void replaceSection(int section, @NonNull SectionAdapter sectionAdapter, short headerType) {
        replaceSection(section, sectionAdapter, null, headerType);
    }

    @Override
    public void replaceSection(int section, @NonNull SimpleSectionAdapter simpleSectionAdapter, SectionItemTouchCallback touchCallback) {
        replaceSection(section, new SectionAdapterWrapper(simpleSectionAdapter), touchCallback);
    }

    @Override
    public void replaceSection(int section, @NonNull SectionAdapter sectionAdapter, SectionItemTouchCallback touchCallback, short headerType) {
        checkHeaderType(headerType);
        addSectionWithHeaderType(headerType, freeType);
        replaceSection(section, new SectionAdapterWrapper(sectionAdapter, headerType), touchCallback);
    }

    private void replaceSection(int section, SectionAdapterWrapper adapterWrapper, SectionItemTouchCallback touchCallback) {
        checkSectionIndex(section);
        removeSection(section);
        if (section == getSectionCount()) {
            addSection(adapterWrapper, touchCallback);
        } else {
            insertSection(section, adapterWrapper, touchCallback);
        }
    }

    @Override
    public void removeSection(int section) {
        checkSectionIndex(section);
        short sectionType = sectionToType.get(section);
        int cnt = getSectionRealItemCount(section);
        int start = getSectionFirstPos(section);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        if (adapterWrapper.getHeaderType() != SectionAdapter.NO_HEADER_TYPE) {
            removeSectionWithType(adapterWrapper.getHeaderType(), sectionType);
        }
        adapterWrapper.resetAdapter();
        typeToAdapter.remove(sectionType);
        SectionItemTouchCallback touchCallback = typeToCallback.get(sectionType);
        typeToCallback.remove(sectionType);
        touchCallback.defaultCallback = null;
        sectionToType.remove(section);
        sectionToPosSum.remove(section);
        updatePosSum(section, -cnt, true);
        adapter.notifyItemRangeRemoved(start, cnt);
        if (headerManager != null) {
            headerManager.checkFirstVisiblePos();
        }
    }

    @Override
    public void updateSection(int section) {
        checkSectionIndex(section);
        adapter.notifyItemRangeChanged(getSectionFirstPos(section), getSectionRealItemCount(section));
        if (headerManager != null) {
            short sectionType = sectionToType.get(section);
            headerManager.updateHeaderView(sectionType);
        }
    }

    @Override
    public void setTouchCallback(int section, @NonNull SectionItemTouchCallback touchCallback) {
        checkSectionIndex(section);
        short sectionType = sectionToType.get(section);
        touchCallback.defaultCallback = itemTouchCallback;
        typeToCallback.put(sectionType, touchCallback);
    }

    @Override
    public void removeTouchCallback(int section) {
        checkSectionIndex(section);
        short sectionType = sectionToType.get(section);
        SectionItemTouchCallback touchCallback = typeToCallback.get(sectionType);
        typeToCallback.remove(sectionType);
        touchCallback.defaultCallback = null;
    }

    @Override
    public <T extends BaseSectionAdapter> T getSectionAdapter(int section) {
        checkSectionIndex(section);
        short sectionType = sectionToType.get(section);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        return adapterWrapper.getAdapter();
    }

    @Override
    public SectionItemTouchCallback getTouchCallback(int section) {
        checkSectionIndex(section);
        short sectionType = sectionToType.get(section);
        return typeToCallback.get(sectionType);
    }

    /* END SECTION MANAGER */
    /* ADAPTER */

    /**
     * This RecyclerView.Adapter implementation provides interaction between RecyclerView and
     * SectionAdapters.
     * <p>
     * Can be obtained by calling {@link #getAdapter()}.
     *
     * @see BaseSectionAdapter
     * @see SectionAdapter
     * @see ItemViewHolder
     * @see HeaderViewHolder
     */
    private RecyclerView.Adapter<ViewHolderWrapper> adapter = new RecyclerView.Adapter<ViewHolderWrapper>() {

        /**
         * Uses type to get an appropriate SectionAdapterWrapper, item type within section or
         * header type, if an item view is a section header. Passes the corresponding call to the
         * BaseSectionAdapter via {@link SectionAdapterWrapper}, obtaining {@link ViewHolder}.
         * Returns {@link ViewHolderWrapper}, that refers to the same View. iewHolder holds
         * a reference to it to access the global adapter position any time.
         */
        @Override
        public ViewHolderWrapper onCreateViewHolder(ViewGroup parent, int type) {
            ViewHolder viewHolder;
            if (isTypeHeader(type)) {
                short headerType = (short) type;
                Set<Short> sectionTypes = headerTypeToSectionTypes.get(headerType);
                short sectionType = sectionTypes.iterator().next();
                SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
                viewHolder = adapterWrapper.onCreateHeaderViewHolder(parent);
            } else {
                short itemType = (short) (type);
                short sectionType = (short) (type >> 16);
                SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
                viewHolder = adapterWrapper.onCreateViewHolder(parent, itemType);
            }
            ViewHolderWrapper wrapper = new ViewHolderWrapper(viewHolder);
            viewHolder.viewHolderWrapper = wrapper;
            viewHolder.positionConverter = SectionDataManager.this;
            return wrapper;
        }

        /**
         * Uses position to determine section type and header type, if item view is a section header.
         * Obtains {@link ViewHolder} from {@link ViewHolderWrapper} and passes the
         * corresponding call to the BaseSectionAdapter via {@link SectionAdapterWrapper}.
         */
        @Override
        public void onBindViewHolder(ViewHolderWrapper wrapper, int position) {
            int type = getItemViewType(position);
            if (isTypeHeader(type)) {
                int section = calcSection(position);
                short sectionType = sectionToType.get(section);
                SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) wrapper.viewHolder;
                adapterWrapper.onBindHeaderViewHolder(headerViewHolder);
            } else {
                short sectionType = (short) (type >> 16);
                int sectionPos = calcPosInSection(position);
                SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
                ItemViewHolder itemViewHolder = (ItemViewHolder) wrapper.viewHolder;
                adapterWrapper.onBindViewHolder(itemViewHolder, sectionPos);
            }
        }

        @Override
        public int getItemCount() {
            return getTotalItemCount();
        }

        /**
         * Item view type allows to determine section or header type, item type within section and
         * whether item view is a section header. It is an integer, consisted of two shorts as follows:
         * <code>(sectionType << 16) + (itemType or headerType)</code>,
         * where <code>itemType</code> is an item type within section, obtained from
         * SectionAdapterWrapper, <code>headerType</code> is a type to distinguish and reuse headers
         * and <code>sectionType</code> is a section type, calculated from adapter position.
         * When the given position corresponds to a header, section type is 0.
         */
        @Override
        public int getItemViewType(int pos) {
            int section = calcSection(pos);
            short sectionType = sectionToType.get(section);
            int sectionPos = calcPosInSection(pos);
            SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
            if (adapterWrapper.isHeaderVisible() && getSectionFirstPos(section) == pos) {
                return adapterWrapper.getHeaderType();
            } else {
                short itemType = adapterWrapper.getItemViewType(sectionPos);
                return (sectionType << 16) + itemType;
            }
        }

    };

    /* END ADAPTER */
    /* TOUCH CALLBACK */

    /**
     * ItemTouchHelper.Callback extension provides interaction between RecyclerView and
     * SectionItemTouchCallbacks.
     * <p>
     * Can be obtained by calling {@link #getTouchCallback()}.
     *
     * @see SectionItemTouchCallback
     */
    private class ItemTouchCallback extends ItemTouchHelper.Callback implements DefaultTouchCallback {

        /**
         * Restricts dragging and swiping for section headers. Returns swipe and drag flags
         * obtained from corresponding {@link SectionItemTouchCallback}, if it exists and the
         * swipe/long press drag is enabled.
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = 0, dragFlags = 0;
            if (!isTypeHeader(viewHolder.getItemViewType())) {
                SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
                if (touchCallback != null) {
                    ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                    ItemViewHolder itemViewHolder = (ItemViewHolder) wrapper.viewHolder;

                    //TODO: Handle behavior when reusing callback/adapter
                    if (touchCallback.defaultCallback == null) {
                        touchCallback.defaultCallback = this;
                    }

                    if (touchCallback.isSwipeEnabled()) {
                        swipeFlags = touchCallback.getSwipeDirFlags(recyclerView, itemViewHolder);
                    }
                    if (touchCallback.defaultCallback != null && touchCallback.isLongPressDragEnabled()) {
                        dragFlags = touchCallback.getDragDirFlags(recyclerView, itemViewHolder);
                    }
                }
            }
            return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags);
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
                if (touchCallback != null) {
                    ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                    touchCallback.onChildDraw(c, recyclerView, (ItemViewHolder) wrapper.viewHolder,
                            dX, dY, actionState, isCurrentlyActive);
                }
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
                if (touchCallback != null) {
                    ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                    touchCallback.onChildDrawOver(c, recyclerView, (ItemViewHolder) wrapper.viewHolder,
                            dX, dY, actionState, isCurrentlyActive);
                }
            } else {
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
                if (touchCallback != null) {
                    ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                    touchCallback.clearView(recyclerView, (ItemViewHolder) wrapper.viewHolder);
                }
            } else {
                super.clearView(recyclerView, viewHolder);
            }
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder == null) return;
            if (viewHolder.getAdapterPosition() >= 0) {
                SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
                if (touchCallback != null) {
                    ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                    touchCallback.onSelectedChanged((ItemViewHolder) wrapper.viewHolder, actionState);
                }
            } else {
                super.onSelectedChanged(viewHolder, actionState);
            }
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
            if (touchCallback != null) {
                ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                touchCallback.onSwiped((ItemViewHolder) wrapper.viewHolder, direction);
            }
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
            if (touchCallback != null) {
                ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                return touchCallback.getSwipeThreshold((ItemViewHolder) wrapper.viewHolder);
            }
            return super.getSwipeThreshold(viewHolder);
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            if (isTypeHeader(target.getItemViewType())) return false;
            int curSection = ((ViewHolderWrapper) current).viewHolder.getSection();
            if (curSection == -1) return false;
            int targetSection = ((ViewHolderWrapper) target).viewHolder.getSection();
            if (curSection != targetSection) return false;
            SectionItemTouchCallback touchCallback = getTouchCallback(current);
            return touchCallback != null && touchCallback.canDropOver(recyclerView, ((ViewHolderWrapper) current).viewHolder, ((ViewHolderWrapper) target).viewHolder);
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
            if (touchCallback != null) {
                ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                ItemViewHolder itemViewHolder = (ItemViewHolder) wrapper.viewHolder;
                ViewHolderWrapper targetWrapper = (ViewHolderWrapper) target;
                ItemViewHolder targetItemViewHolder = (ItemViewHolder) targetWrapper.viewHolder;
                return touchCallback.onMove(recyclerView, itemViewHolder, targetItemViewHolder);
            }
            return false;
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
            SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
            if (touchCallback != null) {
                ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                ItemViewHolder itemViewHolder = (ItemViewHolder) wrapper.viewHolder;
                ViewHolderWrapper targetWrapper = (ViewHolderWrapper) target;
                ItemViewHolder targetItemViewHolder = (ItemViewHolder) targetWrapper.viewHolder;
                int fromSectionPos = calcPosInSection(fromPos);
                int toSectionPos = calcPosInSection(toPos);
                touchCallback.onMoved(recyclerView, itemViewHolder, fromSectionPos, targetItemViewHolder, toSectionPos, x, y);
            } else {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }
        }

        /**
         * Passes the corresponding call to the {@link SectionItemTouchCallback}.
         */
        @Override
        public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
            SectionItemTouchCallback touchCallback = getTouchCallback(viewHolder);
            if (touchCallback != null) {
                ViewHolderWrapper wrapper = (ViewHolderWrapper) viewHolder;
                return touchCallback.getMoveThreshold((ItemViewHolder) wrapper.viewHolder);
            }
            return super.getMoveThreshold(viewHolder);
        }

        /**
         * Passes the corresponding call to the {@link GeneralTouchCallback}.
         */
        @Override
        public int getBoundingBoxMargin() {
            return generalTouchCallback.getBoundingBoxMargin();
        }

        /**
         * Passes the corresponding call to the {@link GeneralTouchCallback}.
         */
        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            return generalTouchCallback.getSwipeEscapeVelocity(defaultValue);
        }

        /*
         * Passes the corresponding call to the {@link GeneralTouchCallback}.
         */
        @Override
        public float getSwipeVelocityThreshold(float defaultValue) {
            return generalTouchCallback.getSwipeVelocityThreshold(defaultValue);
        }

        /**
         * Passes the corresponding call to the {@link GeneralTouchCallback}.
         */
        @Override
        public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected, List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
            ArrayList<ViewHolder> targets = new ArrayList<>();
            for (RecyclerView.ViewHolder target : dropTargets) {
                targets.add(((ViewHolderWrapper) target).viewHolder);
            }
            ViewHolder winner = generalTouchCallback.chooseDropTarget(((ViewHolderWrapper) selected).viewHolder, targets, curX, curY);
            return winner == null ? null : winner.viewHolderWrapper;
        }

        /**
         * Passes the corresponding call to the {@link GeneralTouchCallback}.
         */
        @Override
        public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            return generalTouchCallback.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        /**
         * Passes the corresponding call to the {@link GeneralTouchCallback}.
         */
        @Override
        public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
            return generalTouchCallback.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll);
        }

        /* DEFAULT TOUCH CALLBACK */

        @Override
        public int getDefaultBoundingBoxMargin() {
            return super.getBoundingBoxMargin();
        }

        @Override
        public float getDefaultSwipeEscapeVelocity(float defaultValue) {
            return super.getSwipeEscapeVelocity(defaultValue);
        }

        @Override
        public float getDefaultSwipeVelocityThreshold(float defaultValue) {
            return super.getSwipeVelocityThreshold(defaultValue);
        }

        @Override
        public ViewHolder chooseDropTargetByDefault(ViewHolder selected, List<ViewHolder> dropTargets, int curX, int curY) {
            List<RecyclerView.ViewHolder> targets = new ArrayList<>();
            for (ViewHolder viewHolder : dropTargets) {
                targets.add(viewHolder.viewHolderWrapper);
            }
            ViewHolderWrapper winner = (ViewHolderWrapper) super.chooseDropTarget(selected.viewHolderWrapper, targets, curX, curY);
            return winner == null ? null : winner.viewHolder;
        }

        @Override
        public long getDefaultAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        @Override
        public int interpolateOutOfBoundsScrollByDefault(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
            return super.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll);
        }

        @Override
        public void onMovedByDefault(RecyclerView recyclerView, ItemViewHolder viewHolder, int fromPos, ItemViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder.viewHolderWrapper, viewHolder.getGlobalAdapterPosition(), target.viewHolderWrapper, target.getGlobalAdapterPosition(), x, y);
        }

        @Override
        public boolean canDropOverByDefault(RecyclerView recyclerView, ViewHolder current, ViewHolder target) {
            return super.canDropOver(recyclerView, current.viewHolderWrapper, target.viewHolderWrapper);
        }

        @Override
        public float getDefaultMoveThreshold(ItemViewHolder viewHolder) {
            return super.getMoveThreshold(viewHolder.viewHolderWrapper);
        }

        @Override
        public boolean isLongPressDragEnabledByDefault() {
            return super.isLongPressDragEnabled();
        }

        @Override
        public float getDefaultSwipeThreshold(ItemViewHolder viewHolder) {
            return super.getSwipeThreshold(viewHolder.viewHolderWrapper);
        }

        @Override
        public boolean isSwipeEnabledByDefault() {
            return super.isItemViewSwipeEnabled();
        }

        /* END DEFAULT TOUCH CALLBACK */

    };

    /* END TOUCH CALLBACK */
    /* SECTION ITEM MANAGER */

    private SectionItemManager sectionItemManager = new SectionItemManager() {

        @Override
        public void notifyInserted(int section, int pos) {
            checkSectionIndex(section);
            checkSectionItemIndex(section, pos, true);
            checkSectionItemCntConsistency(section, 1);
            updatePosSum(section, 1, false);
            int adapterPos = getAdapterPos(section, pos);
            adapter.notifyItemInserted(adapterPos);
            if (headerManager != null) {
                headerManager.checkFirstVisiblePos();
            }
        }

        @Override
        public void notifyRemoved(int section, int pos) {
            checkSectionIndex(section);
            checkSectionItemIndex(section, pos);
            checkSectionItemCntConsistency(section, -1);
            updatePosSum(section, -1, false);
            int adapterPos = getAdapterPos(section, pos);
            adapter.notifyItemRemoved(adapterPos);
            if (headerManager != null) {
                headerManager.checkFirstVisiblePos();
            }
        }

        @Override
        public void notifyChanged(int section, int pos) {
            checkSectionIndex(section);
            checkSectionItemIndex(section, pos);
            int adapterPos = getAdapterPos(section, pos);
            adapter.notifyItemChanged(adapterPos);
            if (headerManager != null) {
                headerManager.checkFirstVisiblePos();
            }
        }

        @Override
        public void notifyRangeInserted(int section, int startPos, int cnt) {
            checkSectionIndex(section);
            checkSectionItemIndex(section, startPos, true);
            checkRangeItemCnt(cnt);
            checkSectionItemCntConsistency(section, cnt);
            updatePosSum(section, cnt, false);
            int adapterStartPos = getAdapterPos(section, startPos);
            adapter.notifyItemRangeInserted(adapterStartPos, cnt);
            if (headerManager != null) {
                headerManager.checkFirstVisiblePos();
            }
        }

        @Override
        public void notifyRangeRemoved(int section, int startPos, int cnt) {
            checkSectionIndex(section);
            checkSectionItemIndex(section, startPos);
            checkRangeItemCnt(cnt);
            checkRangeBounds(section, startPos, cnt);
            checkSectionItemCntConsistency(section, -cnt);
            int adapterStartPos = getAdapterPos(section, startPos);
            updatePosSum(section, -cnt, false);
            adapter.notifyItemRangeRemoved(adapterStartPos, cnt);
            if (headerManager != null) {
                headerManager.checkFirstVisiblePos();
            }

        }

        @Override
        public void notifyRangeChanged(int section, int startPos, int cnt) {
            checkSectionIndex(section);
            checkSectionItemIndex(section, startPos);
            checkRangeItemCnt(cnt);
            checkRangeBounds(section, startPos, cnt);
            int adapterStartPos = getAdapterPos(section, startPos);
            adapter.notifyItemRangeChanged(adapterStartPos, cnt);
            if (headerManager != null) {
                headerManager.checkFirstVisiblePos();
            }
        }

        @Override
        public void notifyMoved(int section, int fromPos, int toPos) {
            checkSectionIndex(section);
            checkSectionItemIndex(section, fromPos);
            checkSectionItemIndex(section, toPos);
            int adapterFromPos = getAdapterPos(section, fromPos);
            int adapterToPos = getAdapterPos(section, toPos);
            adapter.notifyItemMoved(adapterFromPos, adapterToPos);
            if (headerManager != null) {
                headerManager.checkFirstVisiblePos();
            }
        }

        @Override
        public void notifyHeaderChanged(int section) {
            checkSectionIndex(section);
            short sectionType = sectionToType.get(section);
            SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
            if (!adapterWrapper.isHeaderVisible()) return;
            int headerPos = getSectionFirstPos(section);
            adapter.notifyItemChanged(headerPos);
            if (headerManager != null) {
                headerManager.updateHeaderView(sectionType);
            }
        }

        @Override
        public void notifyHeaderVisibilityChanged(int section, boolean visible) {
            checkSectionIndex(section);
            if (visible) {
                updatePosSum(section, 1, false);
                adapter.notifyItemInserted(getSectionFirstPos(section));
            } else {
                updatePosSum(section, -1, false);
                adapter.notifyItemRemoved(getSectionFirstPos(section));
            }
            if (headerManager != null) {
                headerManager.checkFirstVisiblePos();
            }
        }

        @Override
        public void notifyHeaderPinnedStateChanged(int section, boolean pinned) {
            checkSectionIndex(section);
            if (headerManager != null) {
                headerManager.checkIsHeaderViewChanged();
            }
        }
    };

    /* END SECTION ITEM MANAGER */
    /* POSITION CONVERTER */

    @Override
    public int calcAdapterPos(int section, int pos) {
        try {
            checkSectionIndex(section);
            checkSectionItemIndex(section, pos);
            return getAdapterPos(section, pos);
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }
    }

    @Override
    public int calcSection(int adapterPos) {
        if (!checkIndex(adapterPos, getTotalItemCount())) {
            return -1;
        }
        return upperBoundBinarySearch(sectionToPosSum, adapterPos);
    }

    @Override
    public int calcPosInSection(int adapterPos) {
        if (!checkIndex(adapterPos, getTotalItemCount())) {
            return -1;
        }
        int section = calcSection(adapterPos);
        short sectionType = sectionToType.get(section);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        return adapterPos - (section > 0 ? sectionToPosSum.get(section - 1) : 0)
                - adapterWrapper.getHeaderVisibilityInt();
    }

    /* END POSITION CONVERTER */
    /* HEADER MANAGER */

    /**
     * Manages header state.
     * <p>
     * It determines, which header view corresponds to the first visible adapter position,
     * and adds/removes/translates the header view via {@link #headerViewManager}.
     * <p>
     * Duplicated {@link HeaderViewHolder}s for {@link SectionHeaderLayout} can be obtained
     * by calling {@link #getDuplicatedHeaderVH(short)}. Every {@link SectionAdapter}'s header
     * is associated with a header type. It indicates that different SectionAdapters can use
     * the same HeaderViewHolder. HeaderViewHolder for any header type is created only once, cached
     * and stored in {@link #typeToHeader}.
     * <p>
     * The contents of the current header view can be updated by rebinding the corresponding
     * {@link HeaderViewHolder}.
     */
    class HeaderManager implements HeaderPosProvider {

        short topSectionType = NO_SECTION_TYPE;
        short topHeaderType = SectionAdapter.NO_HEADER_TYPE;

        HeaderViewManager headerViewManager;
        SparseArray<HeaderViewHolder> typeToHeader;

        HeaderManager(HeaderViewManager headerViewManager) {
            this.headerViewManager = headerViewManager;
            typeToHeader = new SparseArray<>();
        }

        void removeSelf() {
            headerManager = null;
        }

        /* HEADER POSITION PROVIDER */

        @Override
        public int getHeaderAdapterPos(short sectionType) {
            SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
            int section = adapterWrapper.getSection();
            return getSectionFirstPos(section);
        }

        /* END HEADER POSITION PROVIDER */

        /**
         * Checks, whether the header should be updated (added/removed/translated) based on the first
         * visible position (e.g. called after scroll). To update the contents of the corresponding
         * header ViewHolder you should call {@link #updateHeaderView(short)}.
         * <p>
         * Interacts with header view via {@link HeaderViewManager}. Current header view section type
         * is stored in {@link #topSectionType}.
         */
        void checkIsHeaderViewChanged() {
            int topPos = headerViewManager.getFirstVisiblePos();
            if (!checkIndex(topPos, getTotalItemCount())) {
                removeHeaderView();
                return;
            }
            int section = calcSection(topPos);
            short sectionType = sectionToType.get(section);
            SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
            if (adapterWrapper.isHeaderVisible() && adapterWrapper.isHeaderPinned()) {
                if (sectionType == topSectionType) {
                    int nextHeaderPos = getSectionFirstPos(section + 1);
                    headerViewManager.translateHeaderView(nextHeaderPos);
                } else {
                    short headerType = adapterWrapper.getHeaderType();
                    if (headerType == topHeaderType) {
                        topSectionType = sectionType;
                        updateHeaderView(topSectionType);
                        int nextHeaderPos = getSectionFirstPos(section + 1);
                        headerViewManager.translateHeaderView(nextHeaderPos);
                    } else {
                        addHeaderView(section);
                    }
                }
            } else {
                removeHeaderView();
            }
        }

        /**
         * Makes {@link #headerViewManager} check first visible position.
         */
        void checkFirstVisiblePos() {
            headerViewManager.checkFirstVisiblePos();
        }

        /**
         * Notifies {@link HeaderViewManager} that the header view should be added, passing next header
         * position for calculations.
         *
         * @param section Index of the top section.
         */
        private void addHeaderView(int section) {
            topSectionType = sectionToType.get(section);
            SectionAdapterWrapper adapterWrapper = typeToAdapter.get(topSectionType);
            topHeaderType = adapterWrapper.getHeaderType();
            HeaderViewHolder headerViewHolder = getDuplicatedHeaderVH(topSectionType);
            headerViewHolder.sectionType = topSectionType;
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
            HeaderViewHolder headerViewHolder = getDuplicatedHeaderVH(sectionType);
            SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
            headerViewHolder.sectionType = sectionType;
            adapterWrapper.onBindHeaderViewHolder(headerViewHolder);
        }

        /**
         * Notifies {@link HeaderViewManager} that the header view should be removed.
         */
        private void removeHeaderView() {
            if (topSectionType != NO_SECTION_TYPE) {
                headerViewManager.removeHeaderView();
                topSectionType = NO_SECTION_TYPE;
                topHeaderType = SectionAdapter.NO_HEADER_TYPE;
            }
        }

        /**
         * Returns HeaderViewHolder for the given section type. Creates one only if it doesn't exist
         * yet and stores it in {@link #typeToHeader}.
         *
         * @param sectionType Type of the section.
         * @return ViewHolder of the header.
         */
        private HeaderViewHolder getDuplicatedHeaderVH(short sectionType) {
            SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
            short headerType = adapterWrapper.getHeaderType();
            if (headerType == SectionAdapter.NO_HEADER_TYPE) {
                return null;
            }
            HeaderViewHolder headerViewHolder = typeToHeader.get(headerType);
            if (headerViewHolder == null) {
                ViewGroup parent = headerViewManager.getHeaderViewParent();
                headerViewHolder = adapterWrapper.onCreateHeaderViewHolder(parent);
                headerViewHolder.sourcePosProvider = this;
                headerViewHolder.positionConverter = SectionDataManager.this;
                typeToHeader.put(headerType, headerViewHolder);
            }
            return headerViewHolder;
        }

    }

    /* END HEADER MANAGER */

    /**
     * Checks whether the given item view type corresponds to header view.
     *
     * @see #adapter's getItemViewType(int)
     * @param type Item view type.
     * @return True if the given type corresponds to header, false otherwise.
     */
    private boolean isTypeHeader(int type) {
        return (type >> 16) == NO_SECTION_TYPE;
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
     * Returns SectionItemTouchCallback for the given ViewHolder or null, if the obtained adapter
     * position is invalid.
     *
     * @param viewHolder ViewHolder of the swiped/moved view.
     * @return Corresponding SectionItemTouchCallback if exists, or null.
     */
    private SectionItemTouchCallback getTouchCallback(RecyclerView.ViewHolder viewHolder) {
        int adapterPos = viewHolder.getAdapterPosition();
        if (!checkIndex(adapterPos, getTotalItemCount())) {
            return null;
        }
        int section = calcSection(adapterPos);
        return getTouchCallback(section);
    }

    /**
     * Returns the adapter position of the item at position <code>pos</code> counting from the first
     * position corresponding to the given section (the given position can be bigger than section
     * item count).
     *
     * @param section Index of the section.
     * @param pos     Item position.
     * @return Adapter position corresponding to the given section and position.
     */
    private int getAdapterPos(int section, int pos) {
        checkSectionIndex(section);
        short sectionType = sectionToType.get(section);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        return (section > 0 ? sectionToPosSum.get(section - 1) : 0) + pos + adapterWrapper.getHeaderVisibilityInt();
    }

    /**
     * Returns the first global adapter position, that corresponds to the given section (can be
     * either item or header view).
     *
     * @param section Index of the section.
     * @return First global adapter position.
     */
    private int getSectionFirstPos(int section) {
        checkSectionIndex(section, true);
        return section > 0 ? sectionToPosSum.get(section - 1) : 0;
    }

    /**
     * Returns the number of items in RecyclerView, which currently correspond to the given section
     * index (including header if it is visible).
     *
     * @param section Index of the section.
     * @return Number of items.
     */
    private int getSectionRealItemCount(int section) {
        checkSectionIndex(section);
        return sectionToPosSum.get(section) - (section > 0 ? sectionToPosSum.get(section - 1) : 0);
    }

    /**
     * Returns the number of items in RecyclerView, which currently correspond to the items of
     * the given section (excluding header).
     *
     * @param section Index of the section.
     * @return Number of items.
     */
    private int getSectionItemCount(int section) {
        short sectionType = sectionToType.get(section);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        return getSectionRealItemCount(section) - adapterWrapper.getHeaderVisibilityInt();
    }

    /**
     * Adds the given section type to {@link #headerTypeToSectionTypes}, that means that the
     * corresponding adapter is able to create HeaderViewHolder with the given header type.
     *
     * @param headerType  Type of the header.
     * @param sectionType Type of the section to add.
     */
    private void addSectionWithHeaderType(short headerType, short sectionType) {
        Set<Short> sectionTypes = headerTypeToSectionTypes.get(headerType, new HashSet<Short>());
        sectionTypes.add(sectionType);
        headerTypeToSectionTypes.put(headerType, sectionTypes);

    }

    /**
     * Removes the given section type from {@link #headerTypeToSectionTypes}. Removes cached HeaderViewHolder
     * from HeaderManager's storage if there are no more adapters with the given header type.
     *
     * @param headerType  Type of the header.
     * @param sectionType Type of the section to remove.
     */
    private void removeSectionWithType(short headerType, short sectionType) {
        Set<Short> sectionTypes = headerTypeToSectionTypes.get(headerType, new HashSet<Short>());
        sectionTypes.remove(sectionType);
        if (sectionTypes.isEmpty()) {
            headerTypeToSectionTypes.remove(headerType);
            if (headerManager != null) {
                headerManager.typeToHeader.remove(headerType);
            }
        }
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

    /* CHECKS */

    /**
     * Checks whether the given <code>index</code> is greater than or equal to zero and less
     * than <code>limit</code>.
     *
     * @param index Number to check.
     * @param limit Right end of the half-interval.
     * @return True if the given number belongs to the half-interval [0, limit)
     */
    private boolean checkIndex(int index, int limit) {
        return 0 <= index && index < limit;
    }

    /**
     * Calls {@link #checkSectionIndex(int, boolean)} with default <code>border</code> value (false).
     */
    private void checkSectionIndex(int section) {
        checkSectionIndex(section, false);
    }

    /**
     * Raises an exception if the given <code>section</code> index is invalid. If <code>border</code>
     * is true, the given section index can be equal to the section count.
     *
     * @param section Index of the section.
     * @param border  True if the given section index can be equal to the section count.
     */
    private void checkSectionIndex(int section, boolean border) {
        int sectionCnt = getSectionCount();
        int limit = sectionCnt + (border ? 1 : 0);
        if (!checkIndex(section, limit)) {
            throw new IndexOutOfBoundsException("Section index " + section + " is out of range. " +
                    "Current section count is " + sectionCnt + ".");
        }
    }

    /**
     * Calls {@link #checkSectionItemIndex(int, int, boolean)} with default <code>border</code>
     * value (false).
     */
    private void checkSectionItemIndex(int section, int pos) {
        checkSectionItemIndex(section, pos, false);
    }

    /**
     * Raises an exception if the given item position <code>pos</code> in the given section is
     * invalid. If <code>border</code> is true, the given position can be equal to the item count.
     *
     * @param section Index of the section.
     * @param pos     Position to check.
     * @param border  True if the given position can be equal to the item count.
     */
    private void checkSectionItemIndex(int section, int pos, boolean border) {
        int itemCnt = getSectionItemCount(section);
        int limit = itemCnt + (border ? 1 : 0);
        if (!checkIndex(pos, limit)) {
            throw new IndexOutOfBoundsException("Item position " + pos + " in section " + section
                    + " is out of range. " + "Current section item count is " + itemCnt + ".");
        }
    }

    /**
     * Raises an exception if the item count returned from the corresponding section adapter doesn't
     * match the expected one.
     *
     * @param section Index of the section.
     * @param delta   Value on which the item count is being changed.
     */
    private void checkSectionItemCntConsistency(int section, int delta) {
        short sectionType = sectionToType.get(section);
        SectionAdapterWrapper adapterWrapper = typeToAdapter.get(sectionType);
        int shouldBe = getSectionItemCount(section) + delta;
        int found = adapterWrapper.getItemCount();
        if (shouldBe != found) {
            throw new RuntimeException("Inconsistency detected. Section item count should be "
                    + shouldBe + ", but SectionAdapter returned " + found + ".");
        }
    }

    /**
     * Raises an exception if the given item count is negative.
     *
     * @param cnt Number to check;
     */
    private void checkRangeItemCnt(int cnt) {
        if (cnt < 0) {
            throw new IllegalArgumentException("Item count in range cannot be negative.");
        }
    }

    /**
     * Raises an exception if position count <code>cnt</code> starting from <code>startPos</code> is
     * out of range (more than the item count of the given section).
     *
     * @param section  Index of the section to check.
     * @param startPos Position in section to count from.
     * @param cnt      Number of items.
     */
    private void checkRangeBounds(int section, int startPos, int cnt) {
        int itemCnt = getSectionItemCount(section);
        int lastPos = startPos + cnt - 1;
        if (!checkIndex(lastPos, itemCnt)) {
            throw new IndexOutOfBoundsException("Position count " +  cnt + " starting from position "
                    + startPos + " is out of range. Current item count is " +  itemCnt + ".");
        }
    }

    /**
     * Raises an exception if the given header type equals to {@link SectionAdapter#NO_HEADER_TYPE}.
     *
     * @param headerType Type of the header to check.
     */
    private void checkHeaderType(short headerType) {
        if (headerType == SectionAdapter.NO_HEADER_TYPE) {
            throw new IllegalArgumentException("Header type cannot be equal to NO_HEADER_TYPE that is -1.");
        }
    }

    /**
     * Raises an exception when <code>freeType</code> exceeded {@link Short#MAX_VALUE} and overflowed.
     */
    private void checkFreeType() {
        if (freeType < 0) {
            throw new RuntimeException("Exceeded number of created sections, so there is no available section type.");
        }
    }

    /* END CHECKS */

}
