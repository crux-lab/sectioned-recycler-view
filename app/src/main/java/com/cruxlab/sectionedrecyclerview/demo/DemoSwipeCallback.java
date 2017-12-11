/*
 * MIT License
 *
 * Copyright (c) 2017 Cruxlab, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cruxlab.sectionedrecyclerview.demo;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cruxlab.sectionedrecyclerview.lib.BaseSectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionItemSwipeCallback;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.ItemVH;

public class DemoSwipeCallback extends SectionItemSwipeCallback {

    public int color;
    public Drawable deleteIcon;
    private ColorDrawable background;

    public DemoSwipeCallback(int color, Drawable deleteIcon) {
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
        ItemVH itemViewHolder = (ItemVH) viewHolder;
        int sectionPos = itemViewHolder.getSectionAdapterPosition();
        // This method can return -1 when getAdapterPosition() of the corresponding
        // RecyclerView.ViewHolder returns -1 or when this ViewHolder isn't used
        // in any RecyclerView.
        if (sectionPos == -1) return;
        itemViewHolder.removeItem(sectionPos);
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
