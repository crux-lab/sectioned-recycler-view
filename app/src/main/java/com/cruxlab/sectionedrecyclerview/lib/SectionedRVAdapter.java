package com.cruxlab.sectionedrecyclerview.lib;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

final class SectionedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionManager, SectionItemManager {

    private SectionedRVHolder holder;
    private int freeType;
    private ArrayList<Integer> sectionToType;
    private SparseArray<SectionAdapter> typeToAdapter;

    SectionedRVAdapter(SectionedRVHolder holder) {
        this.holder = holder;
        typeToAdapter = new SparseArray<>();
        sectionToType = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        if (isTypeHeader(type)) {
            type--;
            return typeToAdapter.get(type).onCreateHeaderViewHolder(parent);
        } else {
            return typeToAdapter.get(type).onCreateViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (isTypeHeader(type)) {
            type--;
            typeToAdapter.get(type).onBindHeaderViewHolder(holder);
        } else {
            int section = getSection(position);
            int sectionPos = getSectionPos(section, position);
            typeToAdapter.get(type).onBindViewHolder(holder, sectionPos);
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
        Checker.checkPosition(pos, getItemCount());
        int section = getSection(pos);
        int type = sectionToType.get(section);
        if (isHeader(pos)) type++;
        return type;
    }

    @Override
    public int getSectionCount() {
        return typeToAdapter.size();
    }

    @Override
    public void addSection(@NonNull SectionAdapter sectionAdapter) {
        sectionAdapter.section = getSectionCount();
        sectionAdapter.setItemManager(this);
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(freeType);
        freeType += 2;
        notifyDataSetChanged();
        holder.forceUpdateHeaderView();
    }

    @Override
    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter) {
        Checker.checkSection(section, getSectionCount());
        sectionAdapter.section = getSectionCount();
        sectionAdapter.setItemManager(this);
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(section, freeType);
        freeType += 2;
        for (int s = section + 1; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            typeToAdapter.get(type).section = s;
        }
        notifyDataSetChanged();
        holder.forceUpdateHeaderView();
    }

    @Override
    public void replaceSection(int section, @NonNull SectionAdapter sectionAdapter) {
        Checker.checkSection(section, getSectionCount());
        removeSection(section);
        if (section == getSectionCount()) {
            addSection(sectionAdapter);
        } else {
            insertSection(section, sectionAdapter);
        }
        holder.forceUpdateHeaderView();
    }

    @Override
    public void removeSection(int section) {
        Checker.checkSection(section, getSectionCount());
        int remType = sectionToType.get(section);
        typeToAdapter.remove(remType);
        sectionToType.remove(section);
        for (int s = section; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            typeToAdapter.get(type).section = s;
        }
        notifyDataSetChanged();
        holder.forceUpdateHeaderView();
    }

    int getSection(int pos) {
        Checker.checkPosition(pos, getItemCount());
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

    View getHeaderView(ViewGroup parent, int section) {
        int type = sectionToType.get(section);
        SectionAdapter adapter = typeToAdapter.get(type);
        RecyclerView.ViewHolder vh = adapter.onCreateHeaderViewHolder(parent);
        adapter.onBindHeaderViewHolder(vh);
        return vh.itemView;
    }

    private int getHeaderPos(int section) {
        Checker.checkSection(section, getSectionCount());
        int pos = 0;
        for (int s = 0; s < section; s++) {
            int type = sectionToType.get(s);
            pos += typeToAdapter.get(type).getItemCount() + 1;
        }
        return pos;
    }

    private int getSectionPos(int section, int pos) {
        Checker.checkSection(section, getSectionCount());
        Checker.checkPosition(pos, getItemCount());
        for (int s = 0; s < section; s++) {
            int type = sectionToType.get(s);
            pos -= typeToAdapter.get(type).getItemCount() + 1;
        }
        return pos - 1;
    }

    private int getAdapterPos(int section, int pos) {
        Checker.checkSection(section, getSectionCount());
        Checker.checkPosition(pos, getItemCount());
        int cnt = 0;
        for (int s = 0; s < section; s++) {
            int type = sectionToType.get(s);
            cnt += typeToAdapter.get(type).getItemCount() + 1;
        }
        return cnt + 1 + pos;
    }

    private boolean isHeader(int pos) {
        Checker.checkPosition(pos, getItemCount());
        for (int s = 0, cnt = 0; s < getSectionCount(); s++) {
            int type = sectionToType.get(s);
            if (pos == cnt) return true;
            if (pos < cnt) return false;
            cnt += typeToAdapter.get(type).getItemCount() + 1;
        }
        return false;
    }

    private boolean isTypeHeader(int type) {
        return type % 2 != 0;
    }

    @Override
    public void notifyChanged(int section) {
        notifyDataSetChanged();
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyInserted(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getItemCount());
        notifyItemInserted(adapterPos);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyRemoved(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getItemCount());
        notifyItemRemoved(adapterPos);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyChanged(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getItemCount());
        notifyItemChanged(adapterPos);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyRangeInserted(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getItemCount());
        notifyItemRangeInserted(adapterStartPos, cnt);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyRangeRemoved(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getItemCount());
        notifyItemRangeRemoved(adapterStartPos, cnt);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyRangeChanged(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getItemCount());
        notifyItemRangeChanged(adapterStartPos, cnt);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyMoved(int section, int fromPos, int toPos) {
        int adapterFromPos = getAdapterPos(section, fromPos);
        Checker.checkPosition(adapterFromPos, getItemCount());
        int adapterToPos = getAdapterPos(section, toPos);
        Checker.checkPosition(adapterToPos, getItemCount());
        notifyItemMoved(adapterFromPos, adapterToPos);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyHeaderChanged(int section) {
        int headerPos = getHeaderPos(section);
        notifyItemChanged(headerPos);
    }

}
