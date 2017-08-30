package com.cruxlab.sectionedrecyclerview.lib;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;

public final class SectionedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int freeType;
    private ArrayList<Integer> sectionToType;
    private SparseArray<SectionAdapter> typeToAdapter;

    SectionedRVAdapter() {
        super();
        typeToAdapter = new SparseArray<>();
        sectionToType = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        if (type % 2 == 0) {
            return typeToAdapter.get(type).onCreateViewHolder(parent);
        } else {
            return typeToAdapter.get(type - 1).onCreateHeaderViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type % 2 == 0) {
            int section = getSection(position);
            int relativePos = getRelativePos(section, position);
            typeToAdapter.get(type).onBindViewHolder(holder, relativePos);
        } else {
            typeToAdapter.get(type - 1).onBindHeaderViewHolder(holder);
        }
    }

    @Override
    public int getItemCount() {
        int cnt = 0;
        for (int s = 0; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            cnt += typeToAdapter.get(type).getItemCount() + 1;
        }
        return cnt;
    }

    @Override
    public int getItemViewType(int pos) {
        checkPosition(pos);
        int section = getSection(pos);
        int type = sectionToType.get(section);
        if (isHeader(pos)) type++;
        return type;
    }

    public int getSectionCount() {
        return typeToAdapter.size();
    }

    public void addSection(@NonNull SectionAdapter sectionAdapter) {
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(freeType);
        freeType += 2;
        notifyDataSetChanged();
    }

    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter) {
        checkSection(section);
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(section, freeType);
        freeType += 2;
        notifyDataSetChanged();
    }

    public void changeSection(int section, @NonNull SectionAdapter sectionAdapter) {
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

    int getSection(int pos) {
        checkPosition(pos);
        int section = -1;
        for (int s = 0, cnt = 0; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            cnt += typeToAdapter.get(type).getItemCount() + 1;
            if (pos < cnt) {
                section = s;
                break;
            }
        }
        return section;
    }

    int getSectionStartPos(int section) {
        checkSection(section);
        int pos = 0;
        for (int s = 0; s < section; s++) {
            int type = sectionToType.get(s);
            pos += typeToAdapter.get(type).getItemCount() + 1;
        }
        return pos;
    }

    private int getRelativePos(int section, int pos) {
        checkSection(section);
        checkPosition(pos);
        for (int s = 0; s < section; s++) {
            int type = sectionToType.get(s);
            pos -= typeToAdapter.get(type).getItemCount() + 1;
        }
        return pos - 1;
    }

    private int getItemCountAfterIncl(int section) {
        checkSection(section);
        int cnt = 0;
        for (int s = section; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            cnt += typeToAdapter.get(type).getItemCount() + 1;
        }
        return cnt;
    }

    private boolean isHeader(int pos) {
        checkPosition(pos);
        for (int s = 0, cnt = 0; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            if (pos == cnt) return true;
            if (pos < cnt) return false;
            cnt += typeToAdapter.get(type).getItemCount() + 1;
        }
        return false;
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
