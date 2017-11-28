package com.cruxlab.sectionedrecyclerview.demo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.adapters.BaseAdapter;
import com.cruxlab.sectionedrecyclerview.demo.adapters.HeaderlessAdapter;
import com.cruxlab.sectionedrecyclerview.demo.adapters.SectionAdapter1;
import com.cruxlab.sectionedrecyclerview.demo.adapters.SectionAdapter2;
import com.cruxlab.sectionedrecyclerview.demo.adapters.SectionAdapter3;
import com.cruxlab.sectionedrecyclerview.lib.SectionsDataManager;
import com.cruxlab.sectionedrecyclerview.lib.SectionHeaderLayout;

public class MainActivity extends AppCompatActivity {

    private int[] colors;
    private DemoSwipeCallback[] callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);

        final SectionsDataManager sectionsDataManager = new SectionsDataManager();
        RecyclerView.Adapter adapter = sectionsDataManager.getAdapter();
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = sectionsDataManager.getSwipeCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        for (int i = 0; i < 12; i++) {
            if (i % 4 == 3) {
                sectionsDataManager.addSection(new HeaderlessAdapter());
                continue;
            }
            int color = colors[i / 4 % 3];
            BaseAdapter sectionAdapter;
            if (i % 4 == 0) {
                sectionAdapter = new SectionAdapter1(color, sectionsDataManager, true, true);
            } else if (i % 4 == 1) {
                sectionAdapter = new SectionAdapter2(color, sectionsDataManager, true, true);
            } else {
                sectionAdapter = new SectionAdapter3(color, sectionsDataManager, true, true);
            }
            DemoSwipeCallback swipeCallback = callbacks[i / 4 % 3];
            sectionsDataManager.addSection(sectionAdapter, swipeCallback, sectionAdapter.type);
        }

        SectionHeaderLayout sectionHeaderLayout = findViewById(R.id.section_header_layout);
        sectionHeaderLayout.attachTo(recyclerView, sectionsDataManager);
    }

    private void initFields() {
        colors = new int[3];
        colors[0] = getResources().getColor(R.color.headerColorBlue);
        colors[1] = getResources().getColor(R.color.headerColorYellow);
        colors[2] = getResources().getColor(R.color.headerColorRed);
        Drawable deleteIcon = getResources().getDrawable(R.drawable.ic_remove);
        callbacks = new DemoSwipeCallback[3];
        callbacks[0] = new DemoSwipeCallback(colors[0], deleteIcon);
        callbacks[1] = new DemoSwipeCallback(colors[1], deleteIcon);
        callbacks[2] = new DemoSwipeCallback(colors[2], deleteIcon);
    }

}
