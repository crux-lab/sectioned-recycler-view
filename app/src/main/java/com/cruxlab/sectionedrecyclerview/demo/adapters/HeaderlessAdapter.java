package com.cruxlab.sectionedrecyclerview.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.SimpleItemVH;
import com.cruxlab.sectionedrecyclerview.lib.SimpleSectionAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class HeaderlessAdapter extends SimpleSectionAdapter<SimpleItemVH> {

    public ArrayList<String> strings = new ArrayList<>(Arrays.asList(
            "This is a section without header.",
            "Use header buttons to pin/unpin header, duplicate/change/remove section.",
            "Use section with header item buttons to hide/show header, duplicate/change/remove item.",
            "Use section without header item buttons to duplicate/change/remove item range."));

    @Override
    public SimpleItemVH onCreateItemViewHolder(ViewGroup parent, short type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_holder1, parent, false);
        return new SimpleItemVH(view);
    }

    @Override
    public void onBindItemViewHolder(SimpleItemVH holder, int position) {
        holder.bind(this, strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public void duplicateItems(int pos) {
        String text = strings.get(pos);
        int cnt = new Random().nextInt(3) + 1;
        for (int i = 0; i < cnt; i++) {
            strings.add(pos + 1, text);
        }
        notifyItemRangeInserted(pos + 1, cnt);
    }

    public void removeItems(int pos) {
        String text = strings.get(pos);
        int cnt = 0;
        while (pos < getItemCount() && strings.get(pos).equals(text)) {
            strings.remove(pos);
            cnt++;
        }
        notifyItemRangeRemoved(pos, cnt);
    }

    public void changeItems(int pos) {
        String text = strings.get(pos);
        int changePos = pos;
        while (changePos < getItemCount() && strings.get(changePos).equals(text)) {
            strings.set(changePos, text + " *changed*");
            changePos++;
        }
        notifyItemRangeChanged(pos, changePos - pos);
    }

}
