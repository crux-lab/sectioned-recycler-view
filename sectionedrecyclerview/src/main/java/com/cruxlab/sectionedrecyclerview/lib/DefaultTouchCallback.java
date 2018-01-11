package com.cruxlab.sectionedrecyclerview.lib;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Interface for providing default implementations to {@link PartialTouchCallback}'s.
 */
public interface DefaultTouchCallback {

    int getDefaultBoundingBoxMargin();

    float getDefaultSwipeEscapeVelocity(float defaultValue);

    float getDefaultSwipeVelocityThreshold(float defaultValue);

    ViewHolder chooseDropTargetByDefault(ViewHolder selected, List<ViewHolder> dropTargets, int curX, int curY);

    long getDefaultAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy);

    int interpolateOutOfBoundsScrollByDefault(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll);

    void onMovedByDefault(RecyclerView recyclerView, ItemViewHolder viewHolder, int fromPos, ItemViewHolder target, int toPos, int x, int y);

    boolean canDropOverByDefault(RecyclerView recyclerView, ViewHolder current, ViewHolder target);

    float getDefaultMoveThreshold(ItemViewHolder viewHolder);

    boolean isLongPressDragEnabledByDefault();

    float getDefaultSwipeThreshold(ItemViewHolder viewHolder);

    boolean isSwipeEnabledByDefault();

}
