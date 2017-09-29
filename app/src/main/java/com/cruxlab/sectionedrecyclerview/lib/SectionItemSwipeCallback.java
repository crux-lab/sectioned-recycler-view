package com.cruxlab.sectionedrecyclerview.lib;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public abstract class SectionItemSwipeCallback {

    public abstract int getSwipeDirFlags(RecyclerView recyclerView,
                                         SectionAdapter.ViewHolder viewHolder);

    public abstract void onSwiped(SectionAdapter.ViewHolder viewHolder, int direction);


    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            SectionAdapter.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);
    }

    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                SectionAdapter.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, viewHolder.itemView, dX, dY, actionState,
                isCurrentlyActive);
    }

    public void clearView(RecyclerView recyclerView, SectionAdapter.ViewHolder viewHolder) {
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(viewHolder.itemView);
    }

    // If you override this method, you should call super
    public void onSelectedChanged(SectionAdapter.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(viewHolder.itemView);
        }
    }

    public boolean isSwipeEnabled() {
        return true;
    }

}
