package com.cruxlab.sectionedrecyclerview.demo.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.HeaderVH;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;

import java.util.ArrayList;
import java.util.Arrays;

public class SectionAdapter2 extends BaseAdapter<HeaderVH> {

    public SectionAdapter2(int color, SectionManager sectionManager, boolean isHeaderVisible, boolean isHeaderPinned) {
        super(color, sectionManager, isHeaderVisible, isHeaderPinned);
        this.type = 2;
        this.strings = new ArrayList<>(Arrays.asList(
                "Each section can manage its header visibility.",
                "Section headers can pin to the top of the RecyclerView automatically.",
                "Top header behaves just like a regular list item (e.g. handle clicks).",
                "To enable this feature you should attach SectionHeaderLayout to your sectioned RecyclerView.",
                "SectionHeaderLayout should be RecyclerView's direct parent."));
    }

    @Override
    public BaseAdapter getCopy() {
        SectionAdapter2 copy = new SectionAdapter2(color, sectionManager, isHeaderVisible(), isHeaderPinned());
        copy.strings = new ArrayList<>(strings);
        return copy;
    }

    @Override
    public BaseAdapter getNext() {
        return new SectionAdapter3(color, sectionManager, isHeaderVisible(), isHeaderPinned());
    }

    @Override
    public HeaderVH onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_view_holder2, parent, false);
        return new HeaderVH(view);
    }

}
