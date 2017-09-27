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

    public RecyclerView getRecyclerView() {
        return sectionedRV;
    }

    public SectionManager getSectionManager() {
        return sectionDataManager;
    }

    private void init(Context context) {
        inflate(context, R.layout.sectioned_recycler_view, this);
        sectionedRV = findViewById(R.id.recycler_view);
        sectionDataManager = new SectionDataManager(headerViewManager);
        sectionedRV.setAdapter(sectionDataManager.getMockVHAdapter());
        initLayoutManager(context);
        sectionedRV.setLayoutManager(layoutManager);
        sectionedRV.setHasFixedSize(false);
        sectionedRV.addOnScrollListener(onScrollListener);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeCallback());
        itemTouchHelper.attachToRecyclerView(sectionedRV);
    }

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

        @Override
        public void removeHeaderView() {
            if (getChildCount() > 1) {
                //Because we can't remove views from onLayout
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
        public void translateHeaderView(int nextHeaderPos) {
            if (getChildCount() > 1) {
                View headerView = getChildAt(getChildCount() - 1);
                int translationY = calcTranslation(headerView.getHeight(), nextHeaderPos);
                if (headerView.getTranslationY() != translationY) {
                    headerView.setTranslationY(translationY);
                }
            }
        }

        @Override
        public ViewGroup getHeaderViewParent() {
            return SectionedRVLayout.this;
        }
    };

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            sectionDataManager.checkIsHeaderViewChanged();
        }

    };

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

    private void checkHeaderView() {
        post(new Runnable() {
            @Override
            public void run() {
                sectionDataManager.checkIsHeaderViewChanged();
            }
        });
    }

    private void removePrevHeaderView() {
        if (getChildCount() > 2) {
            //Because we can't remove views from onLayout
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

    private int calcTranslation(int headerHeight, int nextHeaderPos) {
        View nextHeaderView = layoutManager.findViewByPosition(nextHeaderPos);
        if (nextHeaderView != null) {
            int topOffset = nextHeaderView.getTop() - getTop();
            int offset = headerHeight - topOffset;
            if (offset > 0) return -offset;
        }
        return 0;
    }

    private static void runJustBeforeBeingDrawn(final View view, final Runnable runnable) {
        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                runnable.run();
                return true;
            }
        };
        view.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    private final class SwipeCallback extends ItemTouchHelper.Callback {

        @Override
        public final int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            SectionDataManager.MockViewHolder sectionViewHolder = (SectionDataManager.MockViewHolder) viewHolder;
            if (sectionViewHolder.headerViewHolder != null) return 0;
            return makeMovementFlags(0, sectionViewHolder.itemViewHolder.getMovementFlags());
        }

        @Override
        public final boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public final void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            SectionDataManager.MockViewHolder sectionViewHolder = (SectionDataManager.MockViewHolder) viewHolder;
            sectionViewHolder.itemViewHolder.onSwiped(direction);
        }

    }

}
