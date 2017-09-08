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
                    int topPos = layoutManager.findFirstVisibleItemPosition();
                    if (topPos < 0 || topPos >= adapter.getItemCount()) {
                        if (getChildCount() > 1) {
                            removeViewAt(1);
                        }
                        return;
                    }
                    int topSectionType = adapter.getSectionType(topPos);
                    if (prevTopSectionType != topSectionType) {
                        prevTopSectionType = topSectionType;
                        if (getChildCount() > 1) {
                            removeViewAt(1);
                        }
                        View view = adapter.getHeaderView(sectionedRV, topSectionType);
                        RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(view.getLayoutParams());
                        newParams.addRule(RelativeLayout.ALIGN_BOTTOM);
                        view.setLayoutParams(newParams);
                        addView(view);
                    }
                }
            });
        }
    };

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
