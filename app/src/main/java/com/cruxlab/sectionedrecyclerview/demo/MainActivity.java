package com.cruxlab.sectionedrecyclerview.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            sectionManager.addSection(i % 3 == 0 ? getYellowSectionAdapter() : i % 3 == 1 ? getRedSectionAdapter() : getBlueSectionAdapter());
        }
    }

    private BaseColorAdapter getYellowSectionAdapter() {
        return new BaseColorAdapter() {

            @Override
            public void onBindHeaderViewHolder(StringViewHolder holder) {
                holder.bind("YELLOW TEXT cnt: " + strings.size());
            }

            @Override
            public StringViewHolder onCreateViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                return new StringViewHolder(view, Color.YELLOW, this);
            }

        };
    }

    private BaseColorAdapter getRedSectionAdapter() {
        return new BaseColorAdapter() {

            @Override
            public void onBindHeaderViewHolder(StringViewHolder holder) {
                holder.bind("RED TEXT cnt: " + strings.size());
            }

            @Override
            public StringViewHolder onCreateViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                return new StringViewHolder(view, Color.RED, this);
            }

        };
    }

    private BaseColorAdapter getBlueSectionAdapter() {
        return new BaseColorAdapter() {

            @Override
            public void onBindHeaderViewHolder(StringViewHolder holder) {
                holder.bind("BLUE TEXT cnt: " + strings.size());
            }

            @Override
            public StringViewHolder onCreateViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                return new StringViewHolder(view, Color.BLUE, this);
            }
        };
    }

    private abstract class BaseColorAdapter extends SectionAdapter<StringViewHolder> {

        public ArrayList<String> strings = new ArrayList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));

        @Override
        public StringViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new StringViewHolder(view, Color.WHITE, this);
        }

        @Override
        public void onBindViewHolder(StringViewHolder holder, int position) {
            holder.bind(strings.get(position % strings.size()));
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }

        public void duplicateString(int pos) {
            strings.add(pos, strings.get(pos));
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

    private class StringViewHolder extends SectionAdapter.ViewHolder {

        private BaseColorAdapter adapter;
        private TextView text;
        private int color;

        public StringViewHolder(View itemView, int color, BaseColorAdapter adapter) {
            super(itemView);
            this.color = color;
            this.adapter = adapter;
            this.text = itemView.findViewById(R.id.tv_text);
        }

        public void bind(final String string) {
            text.setText(string);
            text.setBackgroundColor(color);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (color) {
                        case Color.RED: {
                            int sectionPos = getSectionPosition();
                            adapter.removeString(sectionPos);
                            break;
                        }
                        case Color.BLUE: {
                            int sectionPos = getSectionPosition();
                            adapter.duplicateString(sectionPos);
                            break;
                        }
                        case Color.YELLOW: {
                            int sectionPos = getSectionPosition();
                            adapter.changeString(sectionPos);
                        }
                    }
                }
            });
        }
    }

}
