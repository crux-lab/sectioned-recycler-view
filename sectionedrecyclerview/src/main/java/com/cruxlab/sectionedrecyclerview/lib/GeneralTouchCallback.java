package com.cruxlab.sectionedrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

/**
 * Using this class you can customize some intersectional methods of library's ItemTouchHelper.Callback
 * {@link SectionDataManager#itemTouchCallback}. Implementations provided by default are default
 * implementations in ItemTouchHelper.Callback, so that you should override only methods you want to
 * change.
 *
 * @see SectionDataManager#setGeneralTouchCallback(GeneralTouchCallback).
 */
public class GeneralTouchCallback extends PartialTouchCallback {

    /**
     * Default implementation is {@link ItemTouchHelper.Callback#getBoundingBoxMargin()}.
     *
     * @return The extra margin to be added to the hit box of the dragged View.
     */
    public int getBoundingBoxMargin() {
        checkDefaultCallback();
        return defaultCallback.getDefaultBoundingBoxMargin();
    }

    /**
     * Default implementation is {@link ItemTouchHelper.Callback#getSwipeEscapeVelocity(float)}.
     *
     * @return The minimum swipe velocity.
     */
    float getSwipeEscapeVelocity(float defaultValue) {
        checkDefaultCallback();
        return defaultCallback.getDefaultSwipeEscapeVelocity(defaultValue);
    }

    /**
     * Default implementation is {@link ItemTouchHelper.Callback#getSwipeVelocityThreshold(float)}.
     *
     * @return The velocity cap for pointer movements.
     */
    public float getSwipeVelocityThreshold(float defaultValue) {
        checkDefaultCallback();
        return defaultCallback.getDefaultSwipeVelocityThreshold(defaultValue);
    }

    /**
     * Default implementation is {@link ItemTouchHelper.Callback#chooseDropTarget(RecyclerView.ViewHolder, List, int, int)}.
     *
     * @return A ViewHolder to whose position the dragged ViewHolder should be moved to.
     */
    public ViewHolder chooseDropTarget(ViewHolder selected,
                                       List<ViewHolder> dropTargets,
                                       int curX, int curY) {
        checkDefaultCallback();
        return defaultCallback.chooseDropTargetByDefault(selected, dropTargets, curX, curY);
    }

    /**
     * Default implementation is {@link ItemTouchHelper.Callback#getAnimationDuration(RecyclerView, int, float, float)}.
     *
     * @return The duration for the animation.
     */
    public long getAnimationDuration(RecyclerView recyclerView, int animationType,
                                     float animateDx, float animateDy) {
        checkDefaultCallback();
        return defaultCallback.getDefaultAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    /**
     * Default implementation is {@link ItemTouchHelper.Callback#interpolateOutOfBoundsScroll(RecyclerView, int, int, int, long)}.
     *
     * @return The amount that RecyclerView should scroll.
     */
    public int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                            int viewSize, int viewSizeOutOfBounds,
                                            int totalSize, long msSinceStartScroll) {
        checkDefaultCallback();
        return defaultCallback.interpolateOutOfBoundsScrollByDefault(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll);
    }

}
