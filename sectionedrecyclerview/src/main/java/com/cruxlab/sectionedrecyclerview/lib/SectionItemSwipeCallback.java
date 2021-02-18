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

package com.cruxlab.sectionedrecyclerview.lib;

import android.graphics.Canvas;
import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

/**
 * This class lets you control swipe behavior of each item view in {@link BaseSectionAdapter}
 * and also receive callbacks when user performs touch actions.
 * <p>
 * To control which actions user can take on each view, you should override
 * {@link #getSwipeDirFlags(RecyclerView, BaseSectionAdapter.ItemViewHolder)} and return appropriate
 * set of direction flags combining ({@link ItemTouchHelper.Callback#LEFT},
 * {@link ItemTouchHelper.Callback#RIGHT}, {@link ItemTouchHelper.Callback#START}
 * and {@link ItemTouchHelper.Callback#END} or 0 if no movement is allowed.
 * <p>
 * When a View is swiped, ItemTouchHelper animates it until it goes out of bounds, then calls
 * {@link #onSwiped(BaseSectionAdapter.ItemViewHolder, int)}. At this point, you should update your
 * adapter (e.g. remove the item) and call related SectionAdapter#notify event.
 * <p>
 * You can also customize how your View's respond to user interactions and disable swipe
 * for all views {@link #isSwipeEnabled()}.
 * <p>
 * Similar to {@link ItemTouchHelper.Callback}.
 */
public abstract class SectionItemSwipeCallback {

    /**
     * Returns set of direction flags for each item view combining
     * ({@link ItemTouchHelper.Callback#LEFT}, {@link ItemTouchHelper.Callback#RIGHT},
     * {@link ItemTouchHelper.Callback#START} and {@link ItemTouchHelper.Callback#END}
     * or 0 if no movement is allowed.
     *
     * @param recyclerView The RecyclerView to which the ItemTouchHelper is attached to.
     * @param viewHolder   The ViewHolder for which the swipe direction is queried.
     *
     * @return A binary OR of swipe direction flags.
     */
    public abstract int getSwipeDirFlags(RecyclerView recyclerView,
                                         BaseSectionAdapter.ItemViewHolder viewHolder);
    /**
     * Called when a ViewHolder is swiped by the user.
     * <p>
     * Similar to {@link ItemTouchHelper.Callback#onSwiped(RecyclerView.ViewHolder, int)}.
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction  The direction to which the ViewHolder is swiped.
     */
    public abstract void onSwiped(BaseSectionAdapter.ItemViewHolder viewHolder, int direction);

    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     * <p>
     * Default implementation translates the child by the given <code>dX</code>,
     * <code>dY</code>, but you can customize how your View's respond to user interactions.
     * <p>
     * Similar to {@link ItemTouchHelper.Callback#onChildDraw(Canvas, RecyclerView,
     * RecyclerView.ViewHolder, float, float, int, boolean)}.
     *
     * @param c                 The canvas which RecyclerView is drawing its children.
     * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder        The ViewHolder which is being interacted by the User or it was
     *                          interacted and simply animating to its original position.
     * @param dX                The amount of horizontal displacement caused by user's action.
     * @param dY                The amount of vertical displacement caused by user's action.
     * @param actionState       The type of interaction on the View
     *                          {@link ItemTouchHelper.Callback#ACTION_STATE_SWIPE}.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or
     *                          false it is simply animating back to its original state.
     */
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            BaseSectionAdapter.ItemViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView,
                dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     * <p>
     * Default implementation translates the child by the given <code>dX</code>,
     * <code>dY</code>, but you can customize how your View's respond to user interactions.
     * <p>
     * Similar to {@link ItemTouchHelper.Callback#onChildDrawOver(Canvas, RecyclerView,
     * RecyclerView.ViewHolder, float, float, int, boolean)}.
     *
     * @param c                 The canvas which RecyclerView is drawing its children.
     * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder        The ViewHolder which is being interacted by the User or it was
     *                          interacted and simply animating to its original position.
     * @param dX                The amount of horizontal displacement caused by user's action.
     * @param dY                The amount of vertical displacement caused by user's action.
     * @param actionState       The type of interaction on the View
     *                          {@link ItemTouchHelper.Callback#ACTION_STATE_SWIPE}.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or
     *                          false it is simply animating back to its original state.
     */
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                BaseSectionAdapter.ItemViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, viewHolder.itemView,
                dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * Called when the ViewHolder swiped or dragged by the ItemTouchHelper is changed.
     * <p>
     * If you override this method, you should call super.
     * <p>
     * Similar to {@link ItemTouchHelper.Callback#onSelectedChanged(RecyclerView.ViewHolder, int)}.
     *
     * @param viewHolder  The new ViewHolder that is being swiped. Might be null if it is cleared.
     * @param actionState One of {@link ItemTouchHelper#ACTION_STATE_IDLE} or
 *                        {@link ItemTouchHelper#ACTION_STATE_SWIPE}.
     */
    @CallSuper
    public void onSelectedChanged(BaseSectionAdapter.ItemViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(viewHolder.itemView);
        }
    }

    /**
     * Called by the ItemTouchHelper when the user interaction with an element is over and it
     * also completed its animation.
     * <p>
     * Here you should clear all changes previously done on the View.
     * <p>
     * Similar to {@link ItemTouchHelper.Callback#clearView(RecyclerView, RecyclerView.ViewHolder)}.
     *
     * @param recyclerView The RecyclerView which is controlled by the ItemTouchHelper.
     * @param viewHolder   The View that was interacted by the user.
     */
    public void clearView(RecyclerView recyclerView, BaseSectionAdapter.ItemViewHolder viewHolder) {
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(viewHolder.itemView);
    }

    /**
     * Returns whether ItemTouchHelper should start a swipe operation if a pointer is swiped
     * over the View.
     * <p>
     * Similar to {@link ItemTouchHelper.Callback#isItemViewSwipeEnabled()}.
     *
     * @return True if ItemTouchHelper should start swiping an item when user swipes a pointer
     *         over the View, false otherwise. Default value is <code>true</code>.
     */
    public boolean isSwipeEnabled() {
        return true;
    }

}
