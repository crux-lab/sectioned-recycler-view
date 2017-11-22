package com.cruxlab.sectionedrecyclerview.demo.view_holders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.adapters.DemoSectionAdapter;
import com.cruxlab.sectionedrecyclerview.demo.adapters.DemoSimpleSectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.BaseSectionAdapter;

public class DemoItemViewHolder extends BaseSectionAdapter.ItemViewHolder {

    private TextView text;
    private DemoSectionAdapter adapter;
    private DemoSimpleSectionAdapter simpleAdapter;
    private ImageButton btnDuplicate, btnChange, btnRemove, btnHeader;

    public DemoItemViewHolder(View itemView, DemoSectionAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        this.text = itemView.findViewById(R.id.tv_text);
        this.btnDuplicate = itemView.findViewById(R.id.ibtn_duplicate);
        this.btnChange = itemView.findViewById(R.id.ibtn_change);
        this.btnRemove = itemView.findViewById(R.id.ibtn_remove);
        this.btnHeader = itemView.findViewById(R.id.ibtn_header);
    }

    public DemoItemViewHolder(View itemView, DemoSimpleSectionAdapter simpleAdapter) {
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
                int sectionPos = getSectionAdapterPosition();
                if (sectionPos == -1) return;
                if (adapter != null) {
                    adapter.duplicateNumber(sectionPos);
                } else {
                    simpleAdapter.duplicateFruit(sectionPos);
                }
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sectionPos = getSectionAdapterPosition();
                if (sectionPos == -1) return;
                if (adapter != null) {
                    adapter.changeNumber(sectionPos);
                } else {
                    simpleAdapter.changeFruit(sectionPos);
                }
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sectionPos = getSectionAdapterPosition();
                if (sectionPos == -1) return;
                if (adapter != null) {
                    adapter.removeNumber(sectionPos);
                } else {
                    simpleAdapter.removeFruit(sectionPos);
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

    public DemoSectionAdapter getAdapter() {
        return adapter;
    }

    public DemoSimpleSectionAdapter getSimpleAdapter() {
        return simpleAdapter;
    }

}