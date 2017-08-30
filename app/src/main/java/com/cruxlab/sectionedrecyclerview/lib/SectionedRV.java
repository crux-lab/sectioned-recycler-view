package com.cruxlab.sectionedrecyclerview.lib;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.cruxlab.sectionedrecyclerview.R;

public class SectionedRV extends FrameLayout {

    private RecyclerView recyclerView;

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
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(false);

    }

    public void setAdapter(SectionedRVAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }
}
