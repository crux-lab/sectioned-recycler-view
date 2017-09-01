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
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;
import com.cruxlab.sectionedrecyclerview.lib.SectionedRVLayout;

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

    private SectionAdapter<StringViewHolder> getYellowSectionAdapter() {
        return new SectionAdapter<StringViewHolder>() {

            private int cnt = 5;
            private String[] strings = new String[] {"One", "Two", "Three", "Four", "Five"};

            @Override
            public StringViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                return new StringViewHolder(view, Color.WHITE);
            }

            @Override
            public void onBindHeaderViewHolder(StringViewHolder holder) {
                holder.bind("YELLOW TEXT " + cnt + " ----- click on item to change its text");
            }

            @Override
            public StringViewHolder onCreateViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                return new StringViewHolder(view, Color.YELLOW);
            }

            @Override
            public void onBindViewHolder(StringViewHolder holder, final int position) {
                holder.bind(strings[position % strings.length]);
                holder.bind(strings[position % strings.length]);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        strings[position % strings.length] += " changed";
                        notifyItemChanged(position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return cnt;
            }

        };
    }

    private SectionAdapter<StringViewHolder> getRedSectionAdapter() {
        return new SectionAdapter<StringViewHolder>() {

            private int cnt = 5;
            private String[] strings = new String[] {"One", "Two", "Three", "Four", "Five"};

            @Override
            public StringViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                return new StringViewHolder(view, Color.WHITE);
            }

            @Override
            public void onBindHeaderViewHolder(StringViewHolder holder) {
                holder.bind("RED TEXT " + cnt + " ----- click ro remove item");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (cnt > 0) {
                            notifyItemRemoved(cnt - 1);
                            notifyHeaderChanged();
                            cnt--;
                        }
                    }
                });
            }

            @Override
            public StringViewHolder onCreateViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                return new StringViewHolder(view, Color.RED);
            }

            @Override
            public void onBindViewHolder(StringViewHolder holder, int position) {
                holder.bind(strings[position % strings.length]);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (cnt > 0) {
                            notifyItemRemoved(cnt - 1);
                            notifyHeaderChanged();
                            cnt--;
                        }
                    }
                });
            }

            @Override
            public int getItemCount() {
                return cnt;
            }
        };
    }

    private SectionAdapter<StringViewHolder> getBlueSectionAdapter() {
        return new SectionAdapter<StringViewHolder>() {

            private int cnt = 5;
            private String[] strings = new String[] {"One", "Two", "Three", "Four", "Five"};

            @Override
            public StringViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                return new StringViewHolder(view, Color.WHITE);
            }

            @Override
            public void onBindHeaderViewHolder(StringViewHolder holder) {
                holder.bind("BLUE TEXT " + cnt + " ----- click to add item");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyItemInserted(cnt);
                        notifyHeaderChanged();
                        cnt++;
                    }
                });
            }

            @Override
            public StringViewHolder onCreateViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                return new StringViewHolder(view, Color.BLUE);
            }

            @Override
            public void onBindViewHolder(StringViewHolder holder, int position) {
                holder.bind(strings[position % strings.length]);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyItemInserted(cnt);
                        notifyHeaderChanged();
                        cnt++;
                    }
                });
            }

            @Override
            public int getItemCount() {
                return cnt;
            }
        };
    }

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
        }
    }

}
