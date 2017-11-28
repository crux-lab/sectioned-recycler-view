package com.cruxlab.sectionedrecyclerview.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.HeaderVH;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.ItemVH;
import com.cruxlab.sectionedrecyclerview.lib.SectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;

import java.util.ArrayList;

public abstract class BaseAdapter<HVH extends HeaderVH> extends SectionAdapter<ItemVH, HVH> {

    public ArrayList<String> strings;
    public SectionManager sectionManager;
    public int type;
    public int color;

    public BaseAdapter(int color, SectionManager sectionManager, boolean isHeaderVisible, boolean isHeaderPinned) {
        super(isHeaderVisible, isHeaderPinned);
        this.color = color;
        this.sectionManager = sectionManager;
    }

    public abstract BaseAdapter getCopy();
    public abstract BaseAdapter getNext();

    public void duplicateItem(int pos) {
        strings.add(pos + 1, strings.get(pos));
        notifyItemInserted(pos + 1);
    }

    public void removeItem(int pos) {
        strings.remove(pos);
        notifyItemRemoved(pos);
    }

    public void changeItem(int pos) {
        strings.set(pos, strings.get(pos) + " *changed*");
        notifyItemChanged(pos);
    }

    @Override
    public ItemVH onCreateItemViewHolder(ViewGroup parent, short type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vh, parent, false);
        return new ItemVH(view);
    }

    @Override
    public void onBindItemViewHolder(ItemVH holder, int position) {
        holder.bind(this, strings.get(position));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderVH holder) {
        holder.bind(this, color);
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

}