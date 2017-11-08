package com.cruxlab.sectionedrecyclerview.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.DemoItemViewHolder;
import com.cruxlab.sectionedrecyclerview.lib.SectionAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class DemoSectionAdapter extends SectionAdapter<DemoItemViewHolder> {

    public ArrayList<String> fruit = new ArrayList<>(Arrays.asList("Apple", "Orange", "Lemon"));

    @Override
    public DemoItemViewHolder onCreateItemViewHolder(ViewGroup parent, short type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_view, parent, false);
        return new DemoItemViewHolder(view, this);
    }

    @Override
    public void onBindItemViewHolder(DemoItemViewHolder holder, int position) {
        holder.bind(fruit.get(position % fruit.size()));
    }

    @Override
    public int getItemCount() {
        return fruit.size();
    }

    public void duplicateFruit(int pos) {
        fruit.add(pos + 1, fruit.get(pos));
        notifyItemInserted(pos + 1);
    }

    public void removeFruit(int pos) {
        fruit.remove(pos);
        notifyItemRemoved(pos);
    }

    public void changeFruit(int pos) {
        fruit.set(pos, fruit.get(pos) + " changed");
        notifyItemChanged(pos);
    }

}
