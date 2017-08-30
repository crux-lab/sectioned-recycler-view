package com.cruxlab.sectionedrecyclerview.lib;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.cruxlab.sectionedrecyclerview.R;

public class SectionedRV extends FrameLayout {

    private LinearLayoutManager layoutManager;
    private SectionedRVAdapter adapter;

    private int prevHeaderPos = -1;

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int topPos = layoutManager.findFirstVisibleItemPosition();
            int topSection = adapter.getSection(topPos);
            int headerPos = adapter.getSectionStartPos(topSection);
            if (prevHeaderPos != headerPos) {
                View view = layoutManager.findViewByPosition(headerPos);
                if (view == null) return;
                if (getChildCount() > 1) {
                    removeViewAt(1);
                }
                prevHeaderPos = headerPos;
                layoutManager.removeView(view);
                addView(view);
            }
        }
    };

    public SectionedRV(Context context) {
        super(context);
        init(context);
    }

    public SectionedRV(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SectionedRV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.sectioned_recycler_view, this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new SectionedRVAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.addOnScrollListener(onScrollListener);
    }

    public SectionedRVAdapter getAdapter() {
        return adapter;
    }
}
