package com.cruxlab.sectionedrecyclerview.lib;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.cruxlab.sectionedrecyclerview.R;

public class SectionedRVLayout extends RelativeLayout {

    private RecyclerView sectionedRV;
    private LinearLayoutManager layoutManager;
    private SectionedRVAdapter adapter;

    private int prevTopSectionType = -1;
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
            if (type == prevTopSectionType) {
                prevTopSectionType = -1;
                checkIsHeaderViewChanged();
            }
        }

        @Override
        public void checkIsHeaderViewChanged() {
            post(new Runnable() {
                public void run() {
                    int firstVisPos = layoutManager.findFirstVisibleItemPosition();
                    if (firstVisPos < 0 || firstVisPos >= adapter.getItemCount()) {
                        removeHeaderView();
                        return;
                    }
                    int topSection = adapter.getSection(firstVisPos);
                    int topSectionType = adapter.getSectionType(topSection);
                    if (prevTopSectionType != topSectionType) {
                        prevTopSectionType = topSectionType;
                        View headerView = adapter.getHeaderView(sectionedRV, topSectionType);
                        nextHeaderPos = topSection < (adapter.getSectionCount() - 1) ?
                                adapter.getHeaderPos(topSection + 1) : -1;
                        addHeaderView(headerView);
                    } else if (getChildCount() > 1) {
                        View headerView = getChildAt(1);
                        headerView.setTranslationY(calcTranslation(headerView.getHeight()));
                    }
                }
            });
        }
    };

    private void removeHeaderView() {
        if (getChildCount() > 1) {
            removeViewAt(1);
        }
    }

    private void addHeaderView(final View view) {
        removeHeaderView();
        RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(view.getLayoutParams());
        newParams.addRule(RelativeLayout.ALIGN_BOTTOM);
        view.setLayoutParams(newParams);
        view.setVisibility(INVISIBLE);
        addView(view);
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setTranslationY(calcTranslation(view.getHeight()));
                view.setVisibility(VISIBLE);
            }
        });
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
    }

    public SectionManager getSectionManager() {
        return adapter;
    }

}
