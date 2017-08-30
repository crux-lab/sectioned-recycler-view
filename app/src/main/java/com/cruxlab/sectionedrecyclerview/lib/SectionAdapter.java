package com.cruxlab.sectionedrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class SectionAdapter<VH extends RecyclerView.ViewHolder> {

    public abstract int getItemCount();
    public abstract VH onCreateViewHolder(ViewGroup parent);
    public abstract void onBindViewHolder(VH holder, int position);
    public abstract VH onCreateHeaderViewHolder(ViewGroup parent);
    public abstract void onBindHeaderViewHolder(VH holder);

}
