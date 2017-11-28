package com.cruxlab.sectionedrecyclerview.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.HeaderVH;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;

import java.util.ArrayList;
import java.util.Arrays;

public class SectionAdapter1 extends BaseAdapter<HeaderVH> {

    public SectionAdapter1(int color, SectionManager sectionManager, boolean isHeaderVisible, boolean isHeaderPinned) {
        super(color, sectionManager, isHeaderVisible, isHeaderPinned);
        this.type = 1;
        this.strings = new ArrayList<>(Arrays.asList(
                "Items in this RecyclerView are divided into groups - sections.",
                "Each section has an adapter to represent its items and an optional header.",
                "Just like RecyclerView.Adapter section adapter creates and binds ViewHolders."));
    }

    @Override
    public BaseAdapter getCopy() {
        SectionAdapter1 copy = new SectionAdapter1(color, sectionManager, isHeaderVisible(), isHeaderPinned());
        copy.strings = new ArrayList<>(strings);
        return copy;
    }

    @Override
    public BaseAdapter getNext() {
        return new SectionAdapter2(color, sectionManager, isHeaderVisible(), isHeaderPinned());
    }

    @Override
    public HeaderVH onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_view_holder1, parent, false);
        return new HeaderVH(view);
    }

}
