package com.cruxlab.sectionedrecyclerview.lib;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.cruxlab.sectionedrecyclerview.R;

public class SectionedRVLayout extends FrameLayout implements SectionedRVHolder {

    private RecyclerView sectionedRV;
    private LinearLayoutManager layoutManager;
    private SectionedRVAdapter adapter;

    private int prevTopSection = -1;

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            updateHeaderView();
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
        adapter = new SectionedRVAdapter(this);
        sectionedRV.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        sectionedRV.setLayoutManager(layoutManager);
        sectionedRV.setHasFixedSize(false);
        sectionedRV.addOnScrollListener(onScrollListener);
    }

    private void updateHeaderView() {
        int topPos = layoutManager.findFirstVisibleItemPosition();
        if (topPos < 0 || topPos >= adapter.getItemCount()) {
            if (getChildCount() > 1) {
                removeViewAt(1);
            }
            return;
        }
        int topSection = adapter.getSection(topPos);
        if (prevTopSection != topSection) {
            prevTopSection = topSection;
            if (getChildCount() > 1) {
                removeViewAt(1);
            }
            View view = adapter.getHeaderView(sectionedRV, topSection);
            addView(view);
        }
    }

    public SectionManager getSectionManager() {
        return adapter;
    }

    @Override
    public void forceUpdateHeaderView() {
        prevTopSection = -1;
        updateHeaderView();
    }

}
