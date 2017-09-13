package com.cruxlab.sectionedrecyclerview.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.lib.SectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;
import com.cruxlab.sectionedrecyclerview.lib.SectionedRVLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private SectionManager sectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionedRVLayout srvl = findViewById(R.id.srvl);
        sectionManager = srvl.getSectionManager();
        for (int i = 0; i < 20; i++) {
            SectionAdapter adapter = new DemoSectionAdapter(i % 3 == 0 ? Color.YELLOW : i % 3 == 1 ? Color.RED : Color.BLUE);
            sectionManager.addSection(adapter);
            adapter.changeHeaderVisibility(i % 4 == 0);
        }
    }

    private class DemoSectionAdapter extends SectionAdapter<ItemViewHolder, HeaderViewHolder> {

        public ArrayList<String> strings = new ArrayList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
        public int color;

        DemoSectionAdapter(int color) {
            this.color = color;
        }

        @Override
        public MainActivity.HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_view, parent, false);
            return new MainActivity.HeaderViewHolder(view, color, this);
        }

        @Override
        public MainActivity.ItemViewHolder onCreateViewHolder(ViewGroup parent, short type) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_view, parent, false);
            return new MainActivity.ItemViewHolder(view, this);
        }


        @Override
        public void onBindViewHolder(MainActivity.ItemViewHolder holder, int position) {
            holder.bind(strings.get(position % strings.size()));
        }

        @Override
        public void onBindHeaderViewHolder(MainActivity.HeaderViewHolder holder) {
            holder.bind("Section " + getSection() + ", item count: " + getItemCount());
        }

        @Override
        public boolean hasHeader() {
            return true;
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }

        public void duplicateString(int pos) {
            strings.add(pos + 1, strings.get(pos));
            notifyItemInserted(pos + 1);
            notifyHeaderChanged();
        }

        public void removeString(int pos) {
            strings.remove(pos);
            notifyItemRemoved(pos);
            notifyHeaderChanged();
        }

        public void changeString(int pos) {
            strings.set(pos, strings.get(pos) + " changed");
            notifyItemChanged(pos);
        }

    }

    private class ItemViewHolder extends SectionAdapter.ItemViewHolder {

        private TextView text;
        private DemoSectionAdapter adapter;
        private ImageButton btnDuplicate, btnChange, btnRemove, btnHeader;

        public ItemViewHolder(View itemView, DemoSectionAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            this.text = itemView.findViewById(R.id.tv_text);
            this.btnDuplicate = itemView.findViewById(R.id.ibtn_duplicate);
            this.btnChange = itemView.findViewById(R.id.ibtn_change);
            this.btnRemove = itemView.findViewById(R.id.ibtn_remove);
            this.btnHeader = itemView.findViewById(R.id.ibtn_header);
            if (!adapter.hasHeader()) btnHeader.setVisibility(View.GONE);
        }

        public void bind(final String string) {
            text.setText(string);
            btnDuplicate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int sectionPos = getSectionPosition();
                    adapter.duplicateString(sectionPos);
                }
            });
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int sectionPos = getSectionPosition();
                    adapter.changeString(sectionPos);
                }
            });
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int sectionPos = getSectionPosition();
                    if (sectionPos == -1) return;
                    adapter.removeString(sectionPos);
                }
            });
            btnHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.changeHeaderVisibility(true);
                }
            });
        }
    }

    private class HeaderViewHolder extends SectionAdapter.ViewHolder {

        private DemoSectionAdapter adapter;
        private TextView text;
        private int color;
        private ImageButton btnDuplicate, btnChange, btnRemove, btnHeader;
        private boolean isRemoved;

        public HeaderViewHolder(View itemView, int color, DemoSectionAdapter adapter) {
            super(itemView);
            this.color = color;
            this.adapter = adapter;
            this.text = itemView.findViewById(R.id.tv_text);
            this.btnDuplicate = itemView.findViewById(R.id.ibtn_duplicate);
            this.btnChange = itemView.findViewById(R.id.ibtn_change);
            this.btnRemove = itemView.findViewById(R.id.ibtn_remove);
            this.btnHeader = itemView.findViewById(R.id.ibtn_header);
            if (!adapter.hasHeader()) btnHeader.setVisibility(View.GONE);
        }

        public void bind(final String string) {
            text.setText(string);
            itemView.setBackgroundColor(color);
            btnDuplicate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int section = adapter.getSection();
                    DemoSectionAdapter duplicatedAdapter = new DemoSectionAdapter(adapter.color);
                    duplicatedAdapter.strings = new ArrayList<>(adapter.strings);
                    sectionManager.insertSection(section + 1, duplicatedAdapter);
                    //For mandatory update section in headers
                    for (int s = section + 1 ; s < sectionManager.getSectionCount(); s++) {
                        sectionManager.updateSection(s);
                    }
                }
            });
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int section = adapter.getSection();
                    DemoSectionAdapter newAdapter = new DemoSectionAdapter(adapter.color == Color.YELLOW ?
                            Color.RED : adapter.color == Color.RED ? Color.BLUE : Color.YELLOW);
                    newAdapter.strings = new ArrayList<>(adapter.strings);
                    sectionManager.replaceSection(section, newAdapter);
                }
            });
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isRemoved) return;
                    isRemoved = true;
                    int section = adapter.getSection();
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
                    adapter.changeHeaderVisibility(false);
                }
            });
        }
    }

}
