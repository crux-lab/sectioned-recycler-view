package com.cruxlab.sectionedrecyclerview.demo.view_holders;

import android.view.View;
import android.widget.ImageButton;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.adapters.BaseAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionItemSwipeCallback;

public class HeaderVH extends com.cruxlab.sectionedrecyclerview.lib.BaseSectionAdapter.HeaderViewHolder {

    private ImageButton ibtnDuplicate, ibtnChange, btnRemove, btnPin;

    public HeaderVH(View itemView) {
        super(itemView);
        this.ibtnDuplicate = itemView.findViewById(R.id.ibtn_duplicate);
        this.ibtnChange = itemView.findViewById(R.id.ibtn_change);
        this.btnRemove = itemView.findViewById(R.id.ibtn_remove);
        this.btnPin = itemView.findViewById(R.id.ibtn_header);
    }

    public void bind(final BaseAdapter adapter, final int color) {
        itemView.setBackgroundColor(color);
        ibtnDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The adapter position is used to calculate what section index corresponds to this ViewHolder.
                // So, this method returns -1 when getAdapterPosition() of the real ViewHolder returns -1 or
                // when this ViewHolder hasn't been used in any RecyclerView.
                // In our case it can happen when we click on a delete button of the view whose removal animation
                // is still in progress.
                // Duplicated HeaderVH, which is displayed at the top of the SectionHeaderLayout, can access
                // its section and global adapter position any time, despite it is not at the RecyclerView, so we
                // don't have to handle click on the pinned header separately.
                int section = getSection();
                if (section < 0) return;

                BaseAdapter duplicatedAdapter = adapter.getCopy();
                SectionItemSwipeCallback swipeCallback = adapter.sectionManager.getSwipeCallback(section);

                // We add the duplicated section to the RecyclerView using SectionManager.
                // Note, that we pass the header type, that is used to determine, that sections have the same
                // HeaderViewHolder for further caching and reusing.
                adapter.sectionManager.insertSection(section + 1, duplicatedAdapter, swipeCallback, adapter.type);
            }
        });
        ibtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The adapter position is used to calculate what section index corresponds to this ViewHolder.
                // So, this method returns -1 when getAdapterPosition() of the real ViewHolder returns -1 or
                // when this ViewHolder hasn't been used in any RecyclerView.
                // In our case it can happen when we click on a delete button of the view whose removal animation
                // is in progress.
                // Duplicated HeaderVH, which is displayed at the top of the SectionHeaderLayout, can access
                // its section and global adapter position any time, despite it is not at the RecyclerView, so we
                // don't have to handle click on the pinned header separately.
                int section = getSection();
                if (section < 0) return;

                BaseAdapter newAdapter = adapter.getNext();
                SectionItemSwipeCallback swipeCallback = adapter.sectionManager.getSwipeCallback(section);

                // We replace this section with another one using SectionManager.
                // Note, that we pass the header type, that is used to determine, that sections have the same
                // HeaderViewHolder for further caching and reusing.
                adapter.sectionManager.replaceSection(section, newAdapter, swipeCallback, newAdapter.type);
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The adapter position is used to calculate what section index corresponds to this ViewHolder.
                // So, this method returns -1 when getAdapterPosition() of the real ViewHolder returns -1 or
                // when this ViewHolder hasn't been used in any RecyclerView.
                // In our case it can happen when we click on a delete button of the view whose removal animation
                // is in progress.
                // Duplicated HeaderVH, which is displayed at the top of the SectionHeaderLayout, can access
                // its section and global adapter position any time, despite it is not at the RecyclerView, so we
                // don't have to handle click on the pinned header separately.
                int section = getSection();
                if (section < 0) return;

                // We remove this section from the RecyclerView using SectionManager.
                adapter.sectionManager.removeSection(section);
            }
        });
        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // In our case after updating header pinned state we should change an icon. Note, that
                // we don't do it manually right here, because when header is pinned to the top, its
                // view is duplicated and these changes won't affect an original item at the RecyclerView.
                // So note, that you should always call notifyHeaderChanged() to update header view
                // to guarantee that both views will be updated.
                adapter.updateHeaderPinnedState(!adapter.isHeaderPinned());
                adapter.notifyHeaderChanged();
            }
        });
        // We update pinned state icon only when binding this ViewHolder.
        btnPin.setImageResource(adapter.isHeaderPinned() ? R.drawable.ic_lock : R.drawable.ic_lock_open);
    }

}
