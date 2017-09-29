package com.cruxlab.sectionedrecyclerview.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.lib.SectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionItemSwipeCallback;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;
import com.cruxlab.sectionedrecyclerview.lib.SectionedRVLayout;
import com.cruxlab.sectionedrecyclerview.lib.SimpleSectionAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private SectionManager sectionManager;
    private Drawable deleteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_remove);
        SectionedRVLayout srvl = findViewById(R.id.srvl);
        sectionManager = srvl.getSectionManager();
        for (int i = 0; i < 20; i++) {
            if (i % 4 != 3) {
                int color = (i % 4 == 0) ? Color.YELLOW : (i % 4 == 1) ? Color.RED : Color.BLUE;
                boolean isHeaderVisible = (i % 4 == 0) || (i % 4 == 1);
                boolean isHeaderPinned = (i % 4 == 0);
                DemoSectionAdapter adapter = new DemoSectionAdapter(color, isHeaderVisible, isHeaderPinned);
                sectionManager.addSection(adapter, new DemoSectionItemSwipeCallback(color));
            } else {
                sectionManager.addSection(new SimpleDemoSectionAdapter(), new DemoSectionItemSwipeCallback(Color.GRAY));
            }
        }
    }

    private class DemoSectionAdapter extends SectionAdapter<ItemViewHolder, HeaderViewHolder> {

        public ArrayList<String> strings = new ArrayList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
        public int color;

        DemoSectionAdapter(int color, boolean isHeaderVisible, boolean isHeaderPinned) {
            super(isHeaderVisible, isHeaderPinned);
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

    private class SimpleDemoSectionAdapter extends SimpleSectionAdapter<ItemViewHolder> {

        public ArrayList<String> strings = new ArrayList<>(Arrays.asList("Apple", "Orange", "Watermelon"));

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
        public int getItemCount() {
            return strings.size();
        }

        public void duplicateString(int pos) {
            strings.add(pos + 1, strings.get(pos));
            notifyItemInserted(pos + 1);
        }

        public void removeString(int pos) {
            strings.remove(pos);
            notifyItemRemoved(pos);
        }

        public void changeString(int pos) {
            strings.set(pos, strings.get(pos) + " changed");
            notifyItemChanged(pos);
        }

    }

    private class DemoSectionItemSwipeCallback extends SectionItemSwipeCallback {

        public int color;
        private ColorDrawable background;

        DemoSectionItemSwipeCallback(int color) {
            this.color = color;
            this.background = new ColorDrawable();
        }

        @Override
        public int getSwipeDirFlags(RecyclerView recyclerView, SectionAdapter.ViewHolder viewHolder) {
            return ItemTouchHelper.LEFT;
        }

        @Override
        public void onSwiped(SectionAdapter.ViewHolder viewHolder, int direction) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            int sectionPos = itemViewHolder.getSectionPosition();
            if (sectionPos == -1) return;
            if (itemViewHolder.adapter != null) {
                itemViewHolder.adapter.removeString(sectionPos);
            } else {
                itemViewHolder.simpleAdapter.removeString(sectionPos);
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, SectionAdapter.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getBottom() - itemView.getTop();
            background.setColor(color);
            background.setBounds((int) (itemView.getRight() + dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);
            int deleteIconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
            int deleteIconMargin = (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();
            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteIcon.draw(c);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    }

    private class ItemViewHolder extends SectionAdapter.ItemViewHolder {

        private TextView text;
        private DemoSectionAdapter adapter;
        private SimpleDemoSectionAdapter simpleAdapter;
        private ImageButton btnDuplicate, btnChange, btnRemove, btnHeader;

        public ItemViewHolder(View itemView, DemoSectionAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            this.text = itemView.findViewById(R.id.tv_text);
            this.btnDuplicate = itemView.findViewById(R.id.ibtn_duplicate);
            this.btnChange = itemView.findViewById(R.id.ibtn_change);
            this.btnRemove = itemView.findViewById(R.id.ibtn_remove);
            this.btnHeader = itemView.findViewById(R.id.ibtn_header);
        }

        public ItemViewHolder(View itemView, SimpleDemoSectionAdapter simpleAdapter) {
            super(itemView);
            this.simpleAdapter = simpleAdapter;
            this.text = itemView.findViewById(R.id.tv_text);
            this.btnDuplicate = itemView.findViewById(R.id.ibtn_duplicate);
            this.btnChange = itemView.findViewById(R.id.ibtn_change);
            this.btnRemove = itemView.findViewById(R.id.ibtn_remove);
            this.btnHeader = itemView.findViewById(R.id.ibtn_header);
        }

        public void bind(final String string) {
            text.setText(string);
            btnDuplicate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int sectionPos = getSectionPosition();
                    if (adapter != null) {
                        adapter.duplicateString(sectionPos);
                    } else {
                        simpleAdapter.duplicateString(sectionPos);
                    }
                }
            });
            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int sectionPos = getSectionPosition();
                    if (adapter != null) {
                        adapter.changeString(sectionPos);
                    } else {
                        simpleAdapter.changeString(sectionPos);
                    }
                }
            });
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int sectionPos = getSectionPosition();
                    if (sectionPos == -1) return;
                    if (adapter != null) {
                        adapter.removeString(sectionPos);
                    } else {
                        simpleAdapter.removeString(sectionPos);
                    }
                }
            });
            btnHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapter != null) {
                        adapter.updateHeaderVisibility(!adapter.isHeaderVisible());
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                    }
                }
            });
            if (adapter != null) {
                btnHeader.setImageResource(adapter.isHeaderVisible() ? R.drawable.ic_visibility : R.drawable.ic_visibility_off);
            } else {
                btnHeader.setVisibility(View.GONE);
            }
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
        }

        public void bind(final String string) {
            text.setText(string);
            itemView.setBackgroundColor(color);
            btnDuplicate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int section = adapter.getSection();
                    DemoSectionAdapter duplicatedAdapter = new DemoSectionAdapter(adapter.color, adapter.isHeaderVisible(), adapter.isHeaderPinned());
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
                            Color.RED : adapter.color == Color.RED ? Color.BLUE : Color.YELLOW, adapter.isHeaderVisible(), adapter.isHeaderPinned());
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

}
