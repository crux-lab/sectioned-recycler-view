package com.cruxlab.sectionedrecyclerview.demo.view_holders;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.DemoSectionItemSwipeCallback;
import com.cruxlab.sectionedrecyclerview.demo.MainActivity;
import com.cruxlab.sectionedrecyclerview.demo.adapters.DemoSectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.BaseSectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionItemSwipeCallback;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;

import java.util.ArrayList;

public class DemoHeaderViewHolder extends BaseSectionAdapter.HeaderViewHolder {

    private SectionManager sectionManager;
    private TextView text;
    private ImageButton btnDuplicate, btnChange, btnRemove, btnHeader;

    public DemoHeaderViewHolder(View itemView, SectionManager sectionManager) {
        super(itemView);
        this.sectionManager = sectionManager;
        this.text = itemView.findViewById(R.id.tv_text);
        this.btnDuplicate = itemView.findViewById(R.id.ibtn_duplicate);
        this.btnChange = itemView.findViewById(R.id.ibtn_change);
        this.btnRemove = itemView.findViewById(R.id.ibtn_remove);
        this.btnHeader = itemView.findViewById(R.id.ibtn_header);
    }

    public void bind(final String string, final int color, final DemoSectionAdapter adapter) {
        text.setText(string);
        itemView.setBackgroundColor(color);
        btnDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int section = getSection();
                if (section < 0) return;
                DemoSectionAdapter duplicatedAdapter = new DemoSectionAdapter(adapter.color, sectionManager, adapter.isHeaderVisible(), adapter.isHeaderPinned());
                duplicatedAdapter.numbers = new ArrayList<>(adapter.numbers);
                SectionItemSwipeCallback swipeCallback = sectionManager.getSwipeCallback(section);
                SectionItemSwipeCallback duplicatedCallback = null;
                if (swipeCallback != null) {
                    duplicatedCallback = new DemoSectionItemSwipeCallback(adapter.color, ((DemoSectionItemSwipeCallback) swipeCallback).deleteIcon);
                }
                sectionManager.insertSection(section + 1, duplicatedAdapter, duplicatedCallback, MainActivity.HEADER_TYPE_DEFAULT);
                //For mandatory update section in headers
                for (int s = section + 1; s < sectionManager.getSectionCount(); s++) {
                    sectionManager.updateSection(s);
                }
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int section = getSection();
                if (section < 0) return;
                int newColor = adapter.color == Color.YELLOW ? Color.RED :
                        adapter.color == Color.RED ? Color.BLUE : Color.YELLOW;
                DemoSectionAdapter newAdapter = new DemoSectionAdapter(newColor, sectionManager, adapter.isHeaderVisible(), adapter.isHeaderPinned());
                newAdapter.numbers = new ArrayList<>(adapter.numbers);
                SectionItemSwipeCallback swipeCallback = sectionManager.getSwipeCallback(section);
                SectionItemSwipeCallback newSwipeCallback = null;
                if (swipeCallback != null) {
                    newSwipeCallback = new DemoSectionItemSwipeCallback(newColor, ((DemoSectionItemSwipeCallback) swipeCallback).deleteIcon);
                }
                sectionManager.replaceSection(section, newAdapter, newSwipeCallback, MainActivity.HEADER_TYPE_DEFAULT);
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int section = getSection();
                if (section < 0) return;
                sectionManager.removeSection(section);
                //For mandatory update section in headers
                for (int s = section; s < sectionManager.getSectionCount(); s++) {
                    sectionManager.updateSection(s);
                }
            }
        });
        btnHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null) {
                    adapter.updateHeaderPinnedState(!adapter.isHeaderPinned());
                    adapter.notifyHeaderChanged();
                }
            }
        });
        if (adapter != null) {
            btnHeader.setImageResource(adapter.isHeaderPinned() ? R.drawable.ic_lock : R.drawable.ic_lock_open);
        } else {
            btnHeader.setVisibility(View.GONE);
        }
    }
}
