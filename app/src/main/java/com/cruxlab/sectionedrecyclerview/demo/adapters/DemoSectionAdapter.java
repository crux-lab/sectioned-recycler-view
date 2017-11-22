package com.cruxlab.sectionedrecyclerview.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.DemoHeaderViewHolder;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.DemoItemViewHolder;
import com.cruxlab.sectionedrecyclerview.lib.SectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;

import java.util.ArrayList;
import java.util.Arrays;

public class DemoSectionAdapter extends SectionAdapter<DemoItemViewHolder, DemoHeaderViewHolder> {

    private SectionManager sectionManager;
    public ArrayList<String> numbers = new ArrayList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
    public int color;

    public DemoSectionAdapter(int color, SectionManager sectionManager, boolean isHeaderVisible, boolean isHeaderPinned) {
        super(isHeaderVisible, isHeaderPinned);
        this.color = color;
        this.sectionManager = sectionManager;
    }

    @Override
    public DemoHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_view, parent, false);
        return new DemoHeaderViewHolder(view, sectionManager);
    }

    @Override
    public DemoItemViewHolder onCreateItemViewHolder(ViewGroup parent, short type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_view, parent, false);
        return new DemoItemViewHolder(view, this);
    }

    @Override
    public void onBindItemViewHolder(DemoItemViewHolder holder, int position) {
        holder.bind(numbers.get(position % numbers.size()));
    }

    @Override
    public void onBindHeaderViewHolder(DemoHeaderViewHolder holder) {
        holder.bind("Section " + getSection() + ", item count: " + getItemCount(), color, this);
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    public void duplicateNumber(int pos) {
        numbers.add(pos + 1, numbers.get(pos));
        notifyItemInserted(pos + 1);
        notifyHeaderChanged();
    }

    public void removeNumber(int pos) {
        numbers.remove(pos);
        notifyItemRemoved(pos);
        notifyHeaderChanged();
    }

    public void changeNumber(int pos) {
        numbers.set(pos, numbers.get(pos) + " changed");
        notifyItemChanged(pos);
    }

}
