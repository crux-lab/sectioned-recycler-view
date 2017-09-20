package com.cruxlab.sectionedrecyclerview.lib;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.cruxlab.sectionedrecyclerview.R;

public class SectionedRVLayout extends RelativeLayout {

    private RecyclerView sectionedRV;
    private LinearLayoutManager layoutManager;
    private SectionedRVAdapter adapter;

    private int topSectionType = -1;
    private int nextHeaderPos = -1;

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            headerViewManager.checkIsHeaderViewChanged();
        }

    };

    private HeaderViewManager headerViewManager = new HeaderViewManager() {

        @Override
        public void notifyHeaderUpdated(int type) {
            if (type == topSectionType) {
                int topSection = getTopSection();
                if (topSection < 0) return;
                updateCurrentHeaderView(topSection);
            }
        }

        @Override
        public void checkIsHeaderViewChanged() {
            int topSection = getTopSection();
            if (topSection < 0) {
                removeHeaderView();
                return;
            }
            if (!updateHeaderViewType(topSection)) {
                translateCurrentHeaderView();
            }
        }
    };

    private int getTopSection() {
        int firstVisPos = layoutManager.findFirstVisibleItemPosition();
        if (firstVisPos < 0 || firstVisPos >= adapter.getItemCount()) return -1;
        return adapter.getSection(firstVisPos);
    }

    private boolean updateHeaderViewType(int topSection) {
        int prevTopSectionType = topSectionType;
        topSectionType = adapter.getSectionType(topSection);
        if (topSectionType != prevTopSectionType) {
            nextHeaderPos = topSection < (adapter.getSectionCount() - 1) ?
                    adapter.getFirstPos(topSection + 1) : -1;
            updateCurrentHeaderView(topSection);
            return true;
        }
        return false;
    }

    private void translateCurrentHeaderView() {
        if (getChildCount() > 1) {
            View headerView = getChildAt(getChildCount() - 1);
            headerView.setTranslationY(calcTranslation(headerView.getHeight()));
        }
    }

    private void updateCurrentHeaderView(int topSection) {
        View headerView = adapter.getBoundHeaderView(sectionedRV, topSection);
        if (headerView == null) {
            removeHeaderView();
        } else {
            if (headerView.getParent() == null) {
                addHeaderView(headerView);
            } //Otherwise, header view was updated in getBoundHeaderView
        }
    }

    private void removeHeaderView() {
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

    private void addHeaderView(final View view) {
        if (view == null) return;
        RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(view.getLayoutParams());
        newParams.addRule(RelativeLayout.ALIGN_TOP);
        view.setLayoutParams(newParams);
        runJustBeforeBeingDrawn(view, new Runnable() {
            @Override
            public void run() {
                view.setTranslationY(calcTranslation(view.getHeight()));
                view.requestLayout();
            }
        });
        addView(view);
        removePrevHeaderView();
    }

    private int calcTranslation(int headerHeight) {
        View nextHeaderView = layoutManager.findViewByPosition(nextHeaderPos);
        if (nextHeaderView != null) {
            int topOffset = nextHeaderView.getTop() - getTop();
            int offset = headerHeight - topOffset;
            if (offset > 0) return -offset;
        }
        return 0;
    }

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

    private void init(Context context) {
        inflate(context, R.layout.sectioned_recycler_view, this);
        sectionedRV = findViewById(R.id.recycler_view);
        adapter = new SectionedRVAdapter(headerViewManager);
        sectionedRV.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        sectionedRV.setLayoutManager(layoutManager);
        sectionedRV.setHasFixedSize(false);
        sectionedRV.addOnScrollListener(onScrollListener);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeCallback());
        itemTouchHelper.attachToRecyclerView(sectionedRV);
    }

    public SectionManager getSectionManager() {
        return adapter;
    }

    public void addRVItemDecoration(RecyclerView.ItemDecoration decor) {
        sectionedRV.addItemDecoration(decor);
    }

    public void removeRVItemDecoration(RecyclerView.ItemDecoration decor) {
        sectionedRV.removeItemDecoration(decor);
    }

    public void addRVItemDecoration(RecyclerView.ItemDecoration decor, int index) {
        sectionedRV.addItemDecoration(decor, index);
    }

    public void invalidateRVItemDecorations() {
        sectionedRV.invalidateItemDecorations();
    }

    public void setRVItemAnimator(RecyclerView.ItemAnimator animator) {
        sectionedRV.setItemAnimator(animator);
    }

    public void setRVRecyclerListener(RecyclerView.RecyclerListener listerer) {
        sectionedRV.setRecyclerListener(listerer);
    }

    public void setRVOnFlingListener(RecyclerView.OnFlingListener listener) {
        sectionedRV.setOnFlingListener(listener);
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
            SectionedRVAdapter.ViewHolder sectionViewHolder = (SectionedRVAdapter.ViewHolder) viewHolder;
            if (sectionViewHolder.headerViewHolder != null) return 0;
            return makeMovementFlags(0, sectionViewHolder.itemViewHolder.getMovementFlags());
        }

        @Override
        public final boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public final void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            SectionedRVAdapter.ViewHolder sectionViewHolder = (SectionedRVAdapter.ViewHolder) viewHolder;
            sectionViewHolder.itemViewHolder.onSwiped(direction);
        }

    }

}
