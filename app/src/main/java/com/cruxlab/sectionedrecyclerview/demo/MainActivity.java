package com.cruxlab.sectionedrecyclerview.demo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.adapters.DemoSectionAdapter;
import com.cruxlab.sectionedrecyclerview.demo.adapters.DemoSimpleSectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionDataManager;
import com.cruxlab.sectionedrecyclerview.lib.SectionHeaderLayout;

public class MainActivity extends AppCompatActivity {

    public static final int HEADER_TYPE_DEFAULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Drawable deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_remove);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);

        final SectionDataManager sectionDataManager = new SectionDataManager();
        RecyclerView.Adapter adapter = sectionDataManager.getAdapter();
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = sectionDataManager.getSwipeCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        for (int i = 0; i < 20; i++) {
            if (i % 4 != 3) {
                int color = (i % 4 == 0) ? Color.YELLOW : (i % 4 == 1) ? Color.RED : Color.BLUE;
                boolean isHeaderVisible = (i % 4 == 0) || (i % 4 == 1);
                boolean isHeaderPinned = (i % 4 == 0);
                DemoSectionAdapter sectionAdapter = new DemoSectionAdapter(color, sectionDataManager, isHeaderVisible, isHeaderPinned);
                sectionDataManager.addSection(sectionAdapter, new DemoSectionItemSwipeCallback(color, deleteIcon), HEADER_TYPE_DEFAULT);
            } else {
                sectionDataManager.addSection(new DemoSimpleSectionAdapter(), new DemoSectionItemSwipeCallback(Color.GRAY, deleteIcon));
            }
        }

        final SectionHeaderLayout sectionHeaderLayout = findViewById(R.id.section_header_layout);
        final Button pinHeadersBnt = findViewById(R.id.btn_pin_headers);
        pinHeadersBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sectionHeaderLayout.isAttached()) {
                    sectionHeaderLayout.detach();
                    pinHeadersBnt.setText(R.string.pin_headers);
                } else {
                    sectionHeaderLayout.attachTo(recyclerView, sectionDataManager);
                    pinHeadersBnt.setText(R.string.unpin_headers);
                }
            }
        });

    }
}
