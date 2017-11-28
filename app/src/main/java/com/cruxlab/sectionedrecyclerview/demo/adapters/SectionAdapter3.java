package com.cruxlab.sectionedrecyclerview.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.SmartHeaderVH;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;

import java.util.ArrayList;
import java.util.Arrays;

public class SectionAdapter3 extends BaseAdapter<SmartHeaderVH> {


    public SectionAdapter3(int color, SectionManager sectionManager, boolean isHeaderVisible, boolean isHeaderPinned) {
        super(color, sectionManager, isHeaderVisible, isHeaderPinned);
        this.type = 3;
        this.strings = new ArrayList<>(Arrays.asList(
                "Swipe left to remove item from the list.",
                "You can customise swiping behavior for each section separately.",
                "Section headers are unswipeable."));
    }

    @Override
    public SmartHeaderVH onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_view_holder3, parent, false);
        return new SmartHeaderVH(view);
    }

    @Override
    public BaseAdapter getCopy() {
        SectionAdapter3 copy = new SectionAdapter3(color, sectionManager, isHeaderVisible(), isHeaderPinned());
        copy.strings = new ArrayList<>(strings);
        return copy;
    }

    @Override
    public BaseAdapter getNext() {
        return new SectionAdapter1(color, sectionManager, isHeaderVisible(), isHeaderPinned());
    }

    @Override
    public void duplicateItem(int pos) {
        super.duplicateItem(pos);
        notifyHeaderChanged();
    }

    @Override
    public void removeItem(int pos) {
        super.removeItem(pos);
        notifyHeaderChanged();
    }

}
