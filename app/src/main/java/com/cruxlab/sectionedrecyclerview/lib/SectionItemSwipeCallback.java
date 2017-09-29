package com.cruxlab.sectionedrecyclerview.lib;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

public abstract class SectionItemSwipeCallback {

    public abstract int getSwipeDirFlags(RecyclerView recyclerView,
                                         SectionAdapter.ViewHolder viewHolder);

    public abstract void onSwiped(SectionAdapter.ViewHolder viewHolder, int direction);


    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            SectionAdapter.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

    }

    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                SectionAdapter.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
    }

    public void clearView(RecyclerView recyclerView, SectionAdapter.ViewHolder viewHolder) {

    }

    public void onSelectedChanged(SectionAdapter.ViewHolder viewHolder, int actionState) {

    }

    public boolean isSwipeEnabled() {
        return true;
    }

}
