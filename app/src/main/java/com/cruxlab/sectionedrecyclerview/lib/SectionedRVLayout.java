package com.cruxlab.sectionedrecyclerview.lib;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.cruxlab.sectionedrecyclerview.R;

/**
 * Holder layout for the header view and SectionedRecyclerView.
 * <p>
 * RecyclerView matches the size of the layout. Header view is located on top of it at the top of
 * the layout and is always layout's last child.
 * <p>
 * Header view is managed by {@link #sectionDataManager} via {@link #headerViewManager}. It is
 * changed in two cases: while scrolling or after data set changes. First case is handled in
 * RecyclerView's {@link #onScrollListener}, second one in {@link #layoutManager}'s callbacks. In
 * both cases SectionDataManager updates header view state if necessary.
 */
public class SectionedRVLayout extends RelativeLayout {

    private RecyclerView sectionedRV;
    private LinearLayoutManager layoutManager;
    private SectionDataManager sectionDataManager;

    public SectionedRVLayout(Context context) {
        super(context);
        init(context);
    }

    public SectionedRVLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SectionedRVLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Returns an interface for section managing.
     *
     * @return SectionManager instance.
     */
    public SectionManager getSectionManager() {
        return sectionDataManager;
    }

    /**
     * Returns an interface for conversion between section and adapter positions.
     *
     * @return PositionConverter instance.
     */
    public PositionConverter getPositionConverter() {
        return sectionDataManager;
    }

    /**
     * Returns RecyclerView that works with SectionDataManager's adapter. Be careful using it. To
     * manage ItemDecorations or to set ItemAnimator, RecyclerListener or OnFlingListener better use
     * the corresponding methods.
     *
     * @return RecyclerView instance.
     */
    public RecyclerView getRecyclerView() {
        return sectionedRV;
    }

    /**
     * Adds ItemDecoration to the SectionedRecyclerView.
     *
     * @param decor Decoration to add.
     */
    public void addRVItemDecoration(RecyclerView.ItemDecoration decor) {
        sectionedRV.addItemDecoration(decor);
    }

    /**
     * Removes ItemDecoration from the SectionedRecyclerView.
     *
     * @param decor Decoration to remove.
     */
    public void removeRVItemDecoration(RecyclerView.ItemDecoration decor) {
        sectionedRV.removeItemDecoration(decor);
    }

    /**
     * Inserts ItemDecoration to the SectionedRecyclerView at the <code>index</code> position in the
     * decoration chain.
     *
     * @param decor Decoration to add.
     * @param index Position in the decoration chain to insert this decoration at.
     */
    public void addRVItemDecoration(RecyclerView.ItemDecoration decor, int index) {
        sectionedRV.addItemDecoration(decor, index);
    }

    /**
     * Invalidates all SectionedRecyclerView's ItemDecorations.
     */
    public void invalidateRVItemDecorations() {
        sectionedRV.invalidateItemDecorations();
    }

    /**
     * Sets the ItemAnimator that will handle animations involving changes to the items in this
     * SectionedRecyclerView.
     *
     * @param animator The ItemAnimator being set.
     */
    public void setRVItemAnimator(RecyclerView.ItemAnimator animator) {
        sectionedRV.setItemAnimator(animator);
    }

    /**
     * Registers a listener that will be notified whenever a child view in SectionRecyclerView is
     * recycled.
     *
     * @param listener Listener to register, or null to clear.
     */
    public void setRVRecyclerListener(RecyclerView.RecyclerListener listener) {
        sectionedRV.setRecyclerListener(listener);
    }

    /**
     * Sets an OnFlingListener for the SectionedRecyclerView.
     *
     * @param listener The OnFlingListener instance.
     */
    public void setRVOnFlingListener(RecyclerView.OnFlingListener listener) {
        sectionedRV.setOnFlingListener(listener);
    }

    /**
     * Initializes layout and RecyclerView related objects.
     *
     * @param context Context for initialization.
     */
    private void init(Context context) {
        inflate(context, R.layout.sectioned_recycler_view, this);
        sectionedRV = findViewById(R.id.recycler_view);
        sectionDataManager = new SectionDataManager(headerViewManager);
        sectionedRV.setAdapter(sectionDataManager.getAdapter());
        initLayoutManager(context);
        sectionedRV.setLayoutManager(layoutManager);
        sectionedRV.setHasFixedSize(false);
        sectionedRV.addOnScrollListener(onScrollListener);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(sectionDataManager.getSwipeCallback());
        itemTouchHelper.attachToRecyclerView(sectionedRV);
    }

    /**
     * HeaderViewManager implementation, that is used by {@link #sectionDataManager} to interact with
     * the header view.
     */
    private HeaderViewManager headerViewManager = new HeaderViewManager() {

        @Override
        public int getFirstVisiblePos() {
            return layoutManager.findFirstVisibleItemPosition();
        }

        @Override
        public void addHeaderView(final View headerView, final int nextHeaderPos) {
            LayoutParams newParams = new LayoutParams(headerView.getLayoutParams());
            newParams.addRule(RelativeLayout.ALIGN_TOP);
            headerView.setLayoutParams(newParams);
            runJustBeforeBeingDrawn(headerView, new Runnable() {
                @Override
                public void run() {
                    headerView.setTranslationY(calcTranslation(headerView.getHeight(), nextHeaderPos));
                    headerView.requestLayout();
                }
            });
            addView(headerView);
            removePrevHeaderView();
        }

        /* Uses postponed runnable, because removeViewAt(int) should not be invoked from drawing
        related methods. */
        @Override
        public void removeHeaderView() {
            if (getChildCount() > 1) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (getChildCount() > 1) {
                            removeViewAt(1);
                        }
                    }
                });
            }
        }

        @Override
        public void translateHeaderView(final int nextHeaderPos) {
            if (getChildCount() > 1) {
                final View headerView = getChildAt(getChildCount() - 1);
                runJustBeforeBeingDrawn(headerView, new Runnable() {
                    @Override
                    public void run() {
                        headerView.setTranslationY(calcTranslation(headerView.getHeight(), nextHeaderPos));
                        headerView.requestLayout();
                    }
                });
            }
        }

        @Override
        public ViewGroup getHeaderViewParent() {
            return SectionedRVLayout.this;
        }

    };

    /**
     * Notifies {@link #sectionDataManager} that the RecyclerView was scrolled, so the header view
     * could have been changed.
     */
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            sectionDataManager.checkIsHeaderViewChanged();
        }

    };

    /**
     * Initializes vertical LayoutManager, which notifies {@link #sectionDataManager} that the
     * RecyclerView data set was changed, so the header view could have been changed.
     *
     * @param context Context for initialization.
     * @see #checkHeaderView()
     */
    private void initLayoutManager(Context context) {
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {

            @Override
            public void onItemsChanged(RecyclerView recyclerView) {
                super.onItemsChanged(recyclerView);
                checkHeaderView();
            }

            @Override
            public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
                super.onItemsAdded(recyclerView, positionStart, itemCount);
                checkHeaderView();
            }

            @Override
            public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
                super.onItemsRemoved(recyclerView, positionStart, itemCount);
                checkHeaderView();
            }

            @Override
            public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
                super.onItemsUpdated(recyclerView, positionStart, itemCount);
                checkHeaderView();
            }

            @Override
            public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
                super.onItemsUpdated(recyclerView, positionStart, itemCount, payload);
                checkHeaderView();
            }

            @Override
            public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
                super.onItemsMoved(recyclerView, from, to, itemCount);
                checkHeaderView();
            }

        };
    }

    /**
     * Notifies {@link #sectionDataManager} that the header view could have been changed. Uses
     * {@link #runJustBeforeBeingDrawn(View, Runnable)} to provide a correct first visible item
     * position after the update.
     */
    private void checkHeaderView() {
        runJustBeforeBeingDrawn(sectionedRV, new Runnable() {
            @Override
            public void run() {
                sectionDataManager.checkIsHeaderViewChanged();
            }
        });
    }

    /**
     * Removes previous header view if exists. Uses postponed runnable, because removeViewAt(int)
     * should not be invoked from drawing related methods (e.g. {@link #onScrollListener} is invoked
     * from RecyclerView's onLayout(boolean, int, int, int, int)).
     */
    private void removePrevHeaderView() {
        if (getChildCount() > 2) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (getChildCount() > 2) {
                        removeViewAt(1);
                    }
                }
            });
        }
    }

    /**
     * Calculates yTranslation for the current header view based on its height and next header
     * position.
     *
     * @param headerHeight  Height of the current header view in px.
     * @param nextHeaderPos Adapter position of the next header view.
     * @return Calculated yTranslation for the header view.
     */
    private int calcTranslation(int headerHeight, int nextHeaderPos) {
        View nextHeaderView = layoutManager.findViewByPosition(nextHeaderPos);
        if (nextHeaderView != null) {
            int topOffset = nextHeaderView.getTop() - getTop();
            int offset = headerHeight - topOffset;
            if (offset > 0) return -offset;
        }
        return 0;
    }

    /**
     * Runs the code just before the given view is being drawn so that its size has been already
     * calculated.
     *
     * @param view     View to be drawn.
     * @param runnable Code to run.
     */
    private static void runJustBeforeBeingDrawn(final View view, final Runnable runnable) {
        final ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                runnable.run();
                return true;
            }
        };
        view.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);
    }

}
