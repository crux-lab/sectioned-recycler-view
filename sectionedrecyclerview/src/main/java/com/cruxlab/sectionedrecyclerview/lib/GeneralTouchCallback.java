package com.cruxlab.sectionedrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Using this class you can customize some intersectional methods of library's ItemTouchHelper.Callback
 * {@link SectionDataManager#swipeCallback}. Implementations provided by default are default
 * implementations in ItemTouchHelper.Callback, so that you should override only methods you want to
 * change.
 *
 * @see SectionDataManager#setGeneralTouchCallback(GeneralTouchCallback).
 */
public class GeneralTouchCallback {

    /* Provides default implementations. */
    private static ItemTouchHelper.Callback defaultCallback = new ItemTouchHelper.Callback() {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return 0;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

    };

    /**
     * Similar to {@link ItemTouchHelper.Callback#getBoundingBoxMargin()}.
     *
     * @return The extra margin to be added to the hit box of the dragged View.
     */
    public int getBoundingBoxMargin() {
        return defaultCallback.getBoundingBoxMargin();
    }

    /**
     * Similar to {@link ItemTouchHelper.Callback#getSwipeEscapeVelocity(float)}.
     *
     * @return The minimum swipe velocity.
     */
    float getSwipeEscapeVelocity(float defaultValue) {
        return defaultCallback.getSwipeEscapeVelocity(defaultValue);
    }

    /**
     * Similar to {@link ItemTouchHelper.Callback#getSwipeVelocityThreshold(float)}.
     *
     * @return The velocity cap for pointer movements.
     */
    public float getSwipeVelocityThreshold(float defaultValue) {
        return defaultCallback.getSwipeVelocityThreshold(defaultValue);
    }

    /**
     * Similar to {@link ItemTouchHelper.Callback#chooseDropTarget(RecyclerView.ViewHolder, List, int, int)}.
     *
     * @return A ViewHolder to whose position the dragged ViewHolder should be moved to.
     */
    public BaseSectionAdapter.ViewHolder chooseDropTarget(BaseSectionAdapter.ViewHolder selected,
                                                    List<BaseSectionAdapter.ViewHolder> dropTargets,
                                                    int curX, int curY) {
        List<RecyclerView.ViewHolder> targets = new ArrayList<>();
        for (BaseSectionAdapter.ViewHolder viewHolder : dropTargets) {
            targets.add(viewHolder.viewHolderWrapper);
        }
        return ((ViewHolderWrapper) defaultCallback.chooseDropTarget(selected.viewHolderWrapper, targets, curX, curY)).viewHolder;
    }

    /**
     * Similar to {@link ItemTouchHelper.Callback#getAnimationDuration(RecyclerView, int, float, float)}.
     *
     * @return The duration for the animation.
     */
    public long getAnimationDuration(RecyclerView recyclerView, int animationType,
                                     float animateDx, float animateDy) {
        return defaultCallback.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    /**
     * Similar to {@link ItemTouchHelper.Callback#interpolateOutOfBoundsScroll(RecyclerView, int, int, int, long)}.
     * Default implementation
     *
     * @return The amount that RecyclerView should scroll.
     */
    public int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                            int viewSize, int viewSizeOutOfBounds,
                                            int totalSize, long msSinceStartScroll) {
        return defaultCallback.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll);
    }

}
