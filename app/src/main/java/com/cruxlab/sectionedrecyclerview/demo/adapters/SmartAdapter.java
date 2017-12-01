package com.cruxlab.sectionedrecyclerview.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.SmartHeaderVH;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;

import java.util.ArrayList;
import java.util.Arrays;

public class SmartAdapter extends BaseAdapter<SmartHeaderVH> {


    public SmartAdapter(int color, SectionManager sectionManager, boolean isHeaderVisible, boolean isHeaderPinned) {
        super(color, sectionManager, isHeaderVisible, isHeaderPinned);
        this.type = 3;
        this.strings = new ArrayList<>(Arrays.asList(
                "Swipe left to remove an item from the list.",
                "You can customize item swiping behavior for each section individually.",
                "Section headers are unswipeable."));
    }

    @Override
    public SmartHeaderVH onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smart_header_vh, parent, false);
        return new SmartHeaderVH(view);
    }

    @Override
    public BaseAdapter getCopy() {
        SmartAdapter copy = new SmartAdapter(color, sectionManager, isHeaderVisible(), isHeaderPinned());
        copy.strings = new ArrayList<>(strings);
        return copy;
    }

    @Override
    public BaseAdapter getNext() {
        return new DefaultAdapter(color, sectionManager, isHeaderVisible(), isHeaderPinned());
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
