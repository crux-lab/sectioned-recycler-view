package com.cruxlab.sectionedrecyclerview.demo;


import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cruxlab.sectionedrecyclerview.demo.view_holders.DemoItemViewHolder;
import com.cruxlab.sectionedrecyclerview.lib.BaseSectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionItemSwipeCallback;

public class DemoSectionItemSwipeCallback extends SectionItemSwipeCallback {

    public int color;
    public Drawable deleteIcon;
    private ColorDrawable background;

    public DemoSectionItemSwipeCallback(int color, Drawable deleteIcon) {
        this.color = color;
        this.background = new ColorDrawable();
        this.deleteIcon = deleteIcon;
    }

    @Override
    public int getSwipeDirFlags(RecyclerView recyclerView, BaseSectionAdapter.ItemViewHolder viewHolder) {
        return ItemTouchHelper.LEFT;
    }

    @Override
    public void onSwiped(BaseSectionAdapter.ItemViewHolder viewHolder, int direction) {
        DemoItemViewHolder itemViewHolder = (DemoItemViewHolder) viewHolder;
        int sectionPos = itemViewHolder.getSectionAdapterPosition();
        if (sectionPos == -1) return;
        if (itemViewHolder.getAdapter() != null) {
            itemViewHolder.getAdapter().removeNumber(sectionPos);
        } else {
            itemViewHolder.getSimpleAdapter().removeFruit(sectionPos);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, BaseSectionAdapter.ItemViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();
        background.setColor(color);
        background.setBounds((int) (itemView.getRight() + dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);
        int deleteIconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
        int deleteIconMargin = (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();
        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteIcon.draw(c);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
