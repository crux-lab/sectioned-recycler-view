/*
 * MIT License
 *
 * Copyright (c) 2017 Cruxlab, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cruxlab.sectionedrecyclerview.demo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.cruxlab.sectionedrecyclerview.lib.PositionManager;
import com.cruxlab.sectionedrecyclerview.lib.SectionDataManager;
import com.cruxlab.sectionedrecyclerview.lib.SectionHeaderLayout;
import com.cruxlab.sectionedrecyclerview.demo.adapters.BaseAdapter;
import com.cruxlab.sectionedrecyclerview.demo.adapters.DefaultAdapter;
import com.cruxlab.sectionedrecyclerview.demo.adapters.HeaderlessAdapter;
import com.cruxlab.sectionedrecyclerview.demo.adapters.SimpleAdapter;
import com.cruxlab.sectionedrecyclerview.demo.adapters.SmartAdapter;

public class MainActivity extends AppCompatActivity {

    private int[] colors;
    private DemoSwipeCallback[] callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();

        // Initialize your RecyclerView with a successor of LinearLayoutManager:
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);

        // Create SectionDataManager and set its adapter to the RecyclerView.
        // After that you can use SectionDataManager, that implements SectionManager interface,
        // to add/remove/replace sections in your RecyclerView.
        SectionDataManager sectionDataManager = new SectionDataManager();
        RecyclerView.Adapter adapter = sectionDataManager.getAdapter();
        recyclerView.setAdapter(adapter);

        // You can customize item swiping behaviour for each section individually.
        // To enable this feature, create ItemTouchHelper initialized with SectionDataManager's callback
        // and attach it to your RecyclerView:
        ItemTouchHelper.Callback callback = sectionDataManager.getSwipeCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // To enable displaying pinned headers, attach SectionHeaderLayout to your RecyclerView and
        // SectionDataManager. After this you can manage header pinned state with your adapter.
        // You can disable displaying pinned headers any time by calling detach().
        SectionHeaderLayout sectionHeaderLayout = findViewById(R.id.section_header_layout);
        sectionHeaderLayout.attachTo(recyclerView, sectionDataManager);

        for (int i = 0; i < 12; i++) {
            if (i % 4 == 3) {
                // Adding a section without header to the end of the list, passing a SimpleSectionAdapter.
                sectionDataManager.addSection(new HeaderlessAdapter());
                continue;
            }
            int color = colors[i / 4 % 3];
            BaseAdapter sectionAdapter;
            if (i % 4 == 0) {
                sectionAdapter = new DefaultAdapter(color, sectionDataManager, true, true);
            } else if (i % 4 == 1) {
                sectionAdapter = new SimpleAdapter(color, sectionDataManager, true, true);
            } else {
                sectionAdapter = new SmartAdapter(color, sectionDataManager, true, true);
            }
            DemoSwipeCallback swipeCallback = callbacks[i / 4 % 3];
            // Adding a section with header to the end of the list, passing SectionAdapter,
            // DemoSwipeCallback to customize swiping behavior for items in this section and
            // a header type, that is used to determine, that sections have the same HeaderViewHolder
            // for further caching and reusing.
            sectionDataManager.addSection(sectionAdapter, swipeCallback, sectionAdapter.type);
        }

        // When using GridLayoutManager set SpanSizeLookup as follows to display full width
        // section headers. It uses SectionDataManager's implementation of PositionManager
        // to determine whether the item at the given position is a header:
        // final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        // recyclerView.setLayoutManager(gridLayoutManager);
        // final PositionManager posManager = sectionDataManager;
        // gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
        //    @Override
        //    public int getSpanSize(int position) {
        //        if (posManager.isHeader(position)) {
        //            return gridLayoutManager.getSpanCount();
        //        } else {
        //            return 1;
        //        }
        //    }
        // });
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
