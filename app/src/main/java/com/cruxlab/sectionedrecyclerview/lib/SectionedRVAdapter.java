package com.cruxlab.sectionedrecyclerview.lib;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;

public final class SectionedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int freeType;
    private ArrayList<Integer> sectionToType;
    private SparseArray<RecyclerView.Adapter<RecyclerView.ViewHolder>> typeToAdapter;

    public SectionedRVAdapter() {
        super();
        typeToAdapter = new SparseArray<>();
        sectionToType = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        //How to pass real viewType for sectionAdapters?
        return typeToAdapter.get(type).onCreateViewHolder(parent, 0);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int section = getSection(position);
        int type = sectionToType.get(section);
        int relativePos = getRelativePos(section, position);
        typeToAdapter.get(type).onBindViewHolder(holder, relativePos);
    }

    @Override
    public int getItemCount() {
        int cnt = 0;
        for (int s = 0; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            cnt += typeToAdapter.get(type).getItemCount();
        }
        return cnt;
    }

    @Override
    public int getItemViewType(int pos) {
        int section = getSection(pos);
        return sectionToType.get(section);
    }

    public int getSectionCount() {
        return typeToAdapter.size();
    }

    public void addSection(@NonNull RecyclerView.Adapter sectionAdapter) {
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(freeType);
        freeType++;
        notifyDataSetChanged();
    }

    public void insertSection(int section, @NonNull RecyclerView.Adapter sectionAdapter) {
        checkSection(section);
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(section, freeType);
        freeType++;
        notifyDataSetChanged();
    }

    public void changeSection(int section, @NonNull RecyclerView.Adapter sectionAdapter) {
        checkSection(section);
        removeSection(section);
        if (section == getSectionCount()) {
            addSection(sectionAdapter);
        } else {
            insertSection(section, sectionAdapter);
        }
    }

    public void removeSection(int section) {
        checkSection(section);
        int type = sectionToType.get(section);
        typeToAdapter.remove(type);
        sectionToType.remove(section);
        notifyDataSetChanged();
    }

    public void notifySectionChanged(int section) {
        checkSection(section);
        //Consider using notifyDatasetChanged() for no delay in animation
        notifyItemRangeChanged(getSectionStartPos(section), getItemCountAfterIncl(section));
    }

    private int getSectionStartPos(int section) {
        checkSection(section);
        int pos = 0;
        for (int s = 0; s < section; s++) {
            int type = sectionToType.get(s);
            pos += typeToAdapter.get(type).getItemCount();
        }
        return pos;
    }

    private int getRelativePos(int section, int pos) {
        checkSection(section);
        checkPosition(pos);
        for (int s = 0; s < section; s++) {
            int type = sectionToType.get(s);
            pos -= typeToAdapter.get(type).getItemCount();
        }
        return pos;
    }

    private int getSection(int pos) {
        checkPosition(pos);
        int section = -1;
        for (int s = 0, cnt = 0; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            cnt += typeToAdapter.get(type).getItemCount();
            if (pos < cnt) {
                section = s;
                break;
            }
        }
        return section;
    }

    private int getItemCountAfterIncl(int section) {
        checkSection(section);
        int cnt = 0;
        for (int s = section; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            cnt += typeToAdapter.get(type).getItemCount();
        }
        return cnt;
    }

    private void checkPosition(int pos) {
        if (pos < 0 || pos >= getItemCount()) {
            throw new IllegalArgumentException("Position " + pos + " is out of range. Current item count is " + getItemCount() + ".");
        }
    }

    private void checkSection(int section) {
        if (section < 0 || section >= getSectionCount()) {
            throw new IllegalArgumentException("Section " + section + " is out of range. Current section count is " + getSectionCount() + ".");
        }
    }

}
