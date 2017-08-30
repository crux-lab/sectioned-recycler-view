package com.cruxlab.sectionedrecyclerview.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.lib.SectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionedRV;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SectionedRV srv;

    private String[] strings = new String[] {"One", "Two", "Three", "Four", "Five"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        srv = findViewById(R.id.srv);
        for (int i = 0; i < 20; i++) {
            srv.getAdapter().addSection(i % 3 == 0 ? yellowSectionAdapter : i % 3 == 1 ? redSectionAdapter : blueSectionAdapter);
        }
    }

    private SectionAdapter<StringViewHolder> yellowSectionAdapter = new SectionAdapter<StringViewHolder>() {

        @Override
        public StringViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new StringViewHolder(view, Color.WHITE);
        }

        @Override
        public void onBindHeaderViewHolder(StringViewHolder holder) {
            holder.bind("YELLOW TEXT");
        }

        @Override
        public StringViewHolder onCreateViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new StringViewHolder(view, Color.YELLOW);
        }

        @Override
        public void onBindViewHolder(StringViewHolder holder, int position) {
            holder.bind(strings[position]);
        }

        @Override
        public int getItemCount() {
            return strings.length;
        }

    };

    private SectionAdapter<StringViewHolder> redSectionAdapter = new SectionAdapter<StringViewHolder>() {

        @Override
        public StringViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new StringViewHolder(view, Color.WHITE);
        }

        @Override
        public void onBindHeaderViewHolder(StringViewHolder holder) {
            holder.bind("RED TEXT");
        }

        @Override
        public StringViewHolder onCreateViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new StringViewHolder(view, Color.RED);
        }

        @Override
        public void onBindViewHolder(StringViewHolder holder, int position) {
            holder.bind(strings[position]);
        }

        @Override
        public int getItemCount() {
            return strings.length;
        }
    };

    private SectionAdapter<StringViewHolder> blueSectionAdapter = new SectionAdapter<StringViewHolder>() {

        @Override
        public StringViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new StringViewHolder(view, Color.WHITE);
        }

        @Override
        public void onBindHeaderViewHolder(StringViewHolder holder) {
            holder.bind("BLUE TEXT");
        }

        @Override
        public StringViewHolder onCreateViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new StringViewHolder(view, Color.BLUE);
        }

        @Override
        public void onBindViewHolder(StringViewHolder holder, int position) {
            holder.bind(strings[position]);
        }

        @Override
        public int getItemCount() {
            return strings.length;
        }
    };

    private class StringViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private int color;

        public StringViewHolder(View itemView, int color) {
            super(itemView);
            this.color = color;
            this.text = itemView.findViewById(R.id.tv_text);
        }

        public void bind(final String string) {
            text.setText(string);
            text.setBackgroundColor(color);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int section = new Random().nextInt(srv.getAdapter().getSectionCount());
                    if (section % 3 == 0) {
                        srv.getAdapter().insertSection(section, yellowSectionAdapter);
                    } else if (section % 3 == 1) {
                        srv.getAdapter().removeSection(section);
                    } else {
                        srv.getAdapter().changeSection(section, blueSectionAdapter);
                    }
                }
            });
        }
    }

}
