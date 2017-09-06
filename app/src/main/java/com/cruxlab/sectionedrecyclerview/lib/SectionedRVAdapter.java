package com.cruxlab.sectionedrecyclerview.lib;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

final class SectionedRVAdapter extends RecyclerView.Adapter<SectionedRVAdapter.ViewHolder> implements SectionManager, SectionItemManager, SectionPositionProvider {

    private SectionedRVHolder holder;
    private short freeType = 1;
    private ArrayList<Short> sectionToType;
    private SparseArray<SectionAdapter> typeToAdapter;

    SectionedRVAdapter(SectionedRVHolder holder) {
        this.holder = holder;
        typeToAdapter = new SparseArray<>();
        sectionToType = new ArrayList<>();
    }

    @Override
    public SectionedRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        short sectionType = (short) (type >> 16);
        short itemType = (short) type;
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
        short sectionType = (short) (type >> 16);
        if (isTypeHeader(sectionType)) {
            SectionAdapter adapter = typeToAdapter.get(-sectionType);
            adapter.onBindHeaderViewHolder(viewHolder.headerViewHolder);
        } else {
            int section = getSection(position);
            int sectionPos = getSectionPos(section, position);
            SectionAdapter adapter = typeToAdapter.get(sectionType);
            adapter.onBindViewHolder(viewHolder.itemViewHolder, sectionPos);
        }
    }

    @Override
    public int getItemCount() {
        int cnt = 0;
        for (int s = 0; s < getSectionCount(); s++) {
            int sectionType = sectionToType.get(s);
            cnt += typeToAdapter.get(sectionType).getItemCount() + 1;
        }
        return cnt;
    }

    @Override
    public int getItemViewType(int pos) {
        Checker.checkPosition(pos, getItemCount());
        int section = getSection(pos);
        short sectionType = sectionToType.get(section);
        short itemType = typeToAdapter.get(sectionType).getItemViewType(pos);
        if (isHeader(pos)) sectionType *= -1;
        return (sectionType << 16) + itemType;
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
        freeType++;
        notifyDataSetChanged();
        holder.forceUpdateHeaderView();
    }

    @Override
    public void insertSection(int section, @NonNull SectionAdapter sectionAdapter) {
        Checker.checkSection(section, getSectionCount() + 1);
        sectionAdapter.section = section;
        sectionAdapter.setItemManager(this);
        typeToAdapter.put(freeType, sectionAdapter);
        sectionToType.add(section, freeType);
        freeType++;
        for (int s = section + 1; s < getSectionCount(); s++) {
            int sectionType = sectionToType.get(s);
            typeToAdapter.get(sectionType).section = s;
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
        Checker.checkSection(section, getSectionCount() + 1);
        int remSectionType = sectionToType.get(section);
        typeToAdapter.remove(remSectionType);
        sectionToType.remove(section);
        for (int s = section; s < getSectionCount(); s++) {
            int sectionType = sectionToType.get(s);
            typeToAdapter.get(sectionType).section = s;
        }
        notifyDataSetChanged();
        holder.forceUpdateHeaderView();
    }

    int getSection(int pos) {
        Checker.checkPosition(pos, getItemCount());
        int section = -1;
        for (int s = 0, cnt = 0; s < getSectionCount(); s++) {
            int sectionType = sectionToType.get(s);
            cnt += typeToAdapter.get(sectionType).getItemCount() + 1;
            if (pos < cnt) {
                section = s;
                break;
            }
        }
        return section;
    }

    View getHeaderView(ViewGroup parent, int section) {
        int sectionType = sectionToType.get(section);
        SectionAdapter adapter = typeToAdapter.get(sectionType);
        SectionAdapter.ViewHolder headerViewHolder = adapter.onCreateHeaderViewHolder(parent);
        adapter.onBindHeaderViewHolder(headerViewHolder);
        return headerViewHolder.itemView;
    }

    private int getHeaderPos(int section) {
        Checker.checkSection(section, getSectionCount());
        int pos = 0;
        for (int s = 0; s < section; s++) {
            int sectionType = sectionToType.get(s);
            pos += typeToAdapter.get(sectionType).getItemCount() + 1;
        }
        return pos;
    }

    private int getSectionPos(int section, int pos) {
        Checker.checkSection(section, getSectionCount());
        Checker.checkPosition(pos, getItemCount());
        for (int s = 0; s < section; s++) {
            int sectionType = sectionToType.get(s);
            pos -= typeToAdapter.get(sectionType).getItemCount() + 1;
        }
        return pos - 1;
    }

    private int getAdapterPos(int section, int pos) {
        Checker.checkSection(section, getSectionCount());
        Checker.checkPosition(pos, getItemCount());
        int cnt = 0;
        for (int s = 0; s < section; s++) {
            int sectionType = sectionToType.get(s);
            cnt += typeToAdapter.get(sectionType).getItemCount() + 1;
        }
        return cnt + 1 + pos;
    }

    private boolean isHeader(int pos) {
        Checker.checkPosition(pos, getItemCount());
        for (int s = 0, cnt = 0; s < getSectionCount(); s++) {
            int sectionType = sectionToType.get(s);
            if (pos == cnt) return true;
            if (pos < cnt) return false;
            cnt += typeToAdapter.get(sectionType).getItemCount() + 1;
        }
        return false;
    }

    private boolean isTypeHeader(int type) {
        return type < 0;
    }

    @Override
    public int getSectionPos(int adapterPos) {
        int section = getSection(adapterPos);
        return getSectionPos(section, adapterPos);
    }

    @Override
    public void notifyChanged(int section) {
        notifyDataSetChanged();
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyInserted(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getItemCount() + 1);
        notifyItemInserted(adapterPos);
        holder.forceUpdateHeaderView();
    }

    @Override
    public void notifyRemoved(int section, int pos) {
        int adapterPos = getAdapterPos(section, pos);
        Checker.checkPosition(adapterPos, getItemCount() + 1);
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
