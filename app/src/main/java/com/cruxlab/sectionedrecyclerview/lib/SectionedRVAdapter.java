package com.cruxlab.sectionedrecyclerview.lib;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

final class SectionedRVAdapter extends RecyclerView.Adapter<SectionedRVAdapter.ViewHolder> implements SectionManager, SectionItemManager, SectionPositionProvider {

    private SectionedRVHolder holder;
    private short freeType = 1;

    private ArrayList<Integer> sectionToPosSum;
    private ArrayList<Short> sectionToType;
    private SparseArray<SectionAdapter> typeToAdapter;

    SectionedRVAdapter(SectionedRVHolder holder) {
        this.holder = holder;
        sectionToPosSum = new ArrayList<>();
        sectionToType = new ArrayList<>();
        typeToAdapter = new SparseArray<>();
    }

    @Override
    public SectionedRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        short sectionType = (short) (type);
        short itemType = (short) (type >> 16);
        if (isTypeHeader(sectionType)) {
            SectionAdapter adapter = typeToAdapter.get(-sectionType);
            SectionAdapter.ViewHolder headerViewHolder = adapter.onCreateHeaderViewHolder(parent);
            return new ViewHolder(headerViewHolder);
        } else {
            SectionAdapter adapter = typeToAdapter.get(sectionType);
            SectionAdapter.ItemViewHolder itemViewHolder = adapter.onCreateViewHolder(parent, itemType);
            ViewHolder viewHolder = new ViewHolder(itemViewHolder);
            itemViewHolder.viewHolder = viewHolder;
            itemViewHolder.sectionPositionProvider = this;
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(SectionedRVAdapter.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        short sectionType = (short) (type);
        if (isTypeHeader(sectionType)) {
            SectionAdapter adapter = typeToAdapter.get(-sectionType);
            adapter.onBindHeaderViewHolder(viewHolder.headerViewHolder);
        } else {
            int sectionPos = getSectionPos(position);
            SectionAdapter adapter = typeToAdapter.get(sectionType);
            adapter.onBindViewHolder(viewHolder.itemViewHolder, sectionPos);
        }
    }

    @Override
    public int getItemCount() {
        return getSectionCount() > 0 ? sectionToPosSum.get(getSectionCount() - 1) : 0;
    }

    @Override
    public int getItemViewType(int pos) {
        Checker.checkPosition(pos, getItemCount());
        int section = getSection(pos);
        short sectionType = sectionToType.get(section);
        int sectionPos = getSectionPos(pos);
        SectionAdapter adapter = typeToAdapter.get(sectionType);
        short itemType = adapter.getItemViewType(sectionPos);
        if (isHeader(pos)) sectionType *= -1;
        return (itemType << 16) + sectionType;
    }

    @Override
    public int getSectionCount() {
        return typeToAdapter.size();
    }

    @Override
    public void addSection(@NonNull SectionAdapter sectionAdapter) {
        sectionAdapter.section = getSectionCount();
        sectionAdapter.setItemManager(this);
        int start = getItemCount();
        int cnt = sectionAdapter.getItemCount() + 1;
        int posSum = getItemCount() + cnt;
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(freeType);
        sectionToPosSum.add(posSum);
        freeType++;
        notifyItemRangeInserted(start, cnt);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter) {
        Checker.checkSection(section, getSectionCount() + 1);
        sectionAdapter.section = section;
        sectionAdapter.setItemManager(this);
        int start = getHeaderPos(section);
        int cnt = sectionAdapter.getItemCount() + 1;
        int posSum = (section > 0 ? sectionToPosSum.get(section - 1) : 0) + cnt;
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(section, freeType);
        sectionToPosSum.add(section, posSum);
        freeType++;
        updatePosSum(section + 1, cnt, true);
        notifyItemRangeInserted(start, cnt);
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
        Checker.checkSection(section, getSectionCount() + 1);
        int sectionType = sectionToType.get(section);
        int cnt = getSectionItemCount(section) + 1;
        int start = getHeaderPos(section);
        typeToAdapter.remove(sectionType);
        sectionToType.remove(section);
        sectionToPosSum.remove(section);
        updatePosSum(section, -cnt, true);
        notifyItemRangeRemoved(start, cnt);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void updateSection(int section) {
        Checker.checkSection(section, getSectionCount());
        notifyItemRangeChanged(getHeaderPos(section), getSectionItemCount(section));
        holder.forceUpdateHeaderView();
    }

    private void updatePosSum(int startSection, int cnt, boolean updateSection) {
        for (int s = startSection; s < getSectionCount(); s++) {
            if (updateSection) {
                int sectionType = sectionToType.get(s);
                typeToAdapter.get(sectionType).section = s;
            }
            int prevSum = sectionToPosSum.get(s);
            sectionToPosSum.set(s, prevSum + cnt);
        }
    }

    int getSection(int pos) {
        Checker.checkPosition(pos, getItemCount());
        int section = Collections.binarySearch(sectionToPosSum, pos);
        return section >= 0 ? section + 1 : -(section + 1);
    }

    View getHeaderView(ViewGroup parent, int section) {
        int sectionType = sectionToType.get(section);
        SectionAdapter adapter = typeToAdapter.get(sectionType);
        SectionAdapter.ViewHolder headerViewHolder = adapter.onCreateHeaderViewHolder(parent);
        adapter.onBindHeaderViewHolder(headerViewHolder);
        return headerViewHolder.itemView;
    }

    private int getHeaderPos(int section) {
        Checker.checkSection(section, getSectionCount() + 1);
        return section > 0 ? sectionToPosSum.get(section - 1) : 0;
    }

    private int getSectionItemPos(int section, int adapterPos) {
        Checker.checkSection(section, getSectionCount());
        Checker.checkPosition(adapterPos, getItemCount());
        return adapterPos - (section > 0 ? sectionToPosSum.get(section - 1) : 0) - 1;
    }

    private int getAdapterPos(int section, int sectionPos) {
        Checker.checkSection(section, getSectionCount());
        Checker.checkPosition(sectionPos, getItemCount());
        return (section > 0 ? sectionToPosSum.get(section - 1) : 0) + sectionPos + 1;
    }

    private boolean isHeader(int pos) {
        Checker.checkPosition(pos, getItemCount());
        return pos == 0 || Collections.binarySearch(sectionToPosSum, pos) >= 0;
    }

    private boolean isTypeHeader(int type) {
        return type < 0;
    }

    private int getSectionItemCount(int section) {
        Checker.checkSection(section, getSectionCount());
        return sectionToPosSum.get(section) - (section > 0 ? sectionToPosSum.get(section - 1) : 0) - 1;
    }


    @Override
    public int getSectionPos(int adapterPos) {
        int section = getSection(adapterPos);
        return getSectionItemPos(section, adapterPos);
    }

    @Override
    public void notifyInserted(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getItemCount() + 1);
        updatePosSum(section, 1, false);
        notifyItemInserted(adapterPos);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyRemoved(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getItemCount() + 1);
        updatePosSum(section, -1, false);
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
        updatePosSum(section, cnt, false);
        notifyItemRangeInserted(adapterStartPos, cnt);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyRangeRemoved(int section, int startPos, int cnt) {
        int adapterStartPos = getAdapterPos(section, startPos);
        Checker.checkPosRange(adapterStartPos, cnt, getItemCount());
        updatePosSum(section, -cnt, false);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        SectionAdapter.ItemViewHolder itemViewHolder;
        SectionAdapter.ViewHolder headerViewHolder;

        public ViewHolder(SectionAdapter.ItemViewHolder itemViewHolder) {
            super(itemViewHolder.itemView);
            this.itemViewHolder = itemViewHolder;
        }

        public ViewHolder(SectionAdapter.ViewHolder headerViewHolder) {
            super(headerViewHolder.itemView);
            this.headerViewHolder = headerViewHolder;
        }

    }

}
