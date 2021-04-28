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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * Holder layout for the header view of the RecyclerView with sections.
 * <p>
 * Header view is located on top of RecyclerView at the top of the layout and is always layout's
 * last child.
 * <p>
 * Header view is managed by {@link SectionDataManager.HeaderManager} via {@link #headerViewManager}.
 * It is changed in two cases: while scrolling or after data set changes. First case is handled in
 * RecyclerView's {@link #onScrollListener}, second one in headerViewManager's callback. In
 * both cases HeaderManager updates header view state if necessary.
 */
public class SectionHeaderLayout extends RelativeLayout {

    private RecyclerView recyclerView;
    private SectionDataManager.HeaderManager headerManager;

    public SectionHeaderLayout(Context context) {
        super(context);
    }

    public SectionHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SectionHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SectionHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Attaches to the given RecyclerView and SectionDataManager. Adds {@link #onScrollListener} to
     * the given RecyclerView to manage header view while scrolling. RecyclerView's layout manager
     * should be a successor of LinearLayoutManager.
     *
     * @param recyclerView       RecyclerView to attach to.
     * @param sectionDataManager SectionDataManager to attach to.
     */
    public void attachTo(RecyclerView recyclerView, SectionDataManager sectionDataManager) {
        this.recyclerView = recyclerView;
        headerManager = sectionDataManager.createHeaderManager(headerViewManager);
        recyclerView.addOnScrollListener(onScrollListener);
        headerManager.checkIsHeaderViewChanged();
    }

    /**
     * Returns whether this SectionHeaderLayout has been attached to RecyclerView and
     * SectionDataManager.
     *
     * @return True if it has been attached, false otherwise.
     */
    public boolean isAttached() {
        return headerManager != null;
    }

    /**
     * Detaches from RecyclerView and SectionDataManager.
     */
    public void detach() {
        if (!isAttached()) {
            throw new RuntimeException("SectionHeaderLayout hasn't been attached " +
                    "to any RecyclerView and SectionDataManager.");
        }
        recyclerView.removeOnScrollListener(onScrollListener);
        headerManager.removeSelf();
        headerViewManager.removeHeaderView();
        recyclerView = null;
        headerManager = null;
    }

    /**
     * HeaderViewManager implementation, that is used by {@link SectionDataManager.HeaderManager}
     * to interact with the header view.
     */
    private HeaderViewManager headerViewManager = new HeaderViewManager() {

        @Override
        public int getFirstVisiblePos() {
            return ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }

        @Override
        public void checkFirstVisiblePos() {
            checkHeaderView();
        }

        @Override
        public void addHeaderView(final View headerView, final int nextHeaderPos) {
            LayoutParams newParams = new LayoutParams(headerView.getLayoutParams());
            newParams.addRule(RelativeLayout.ALIGN_TOP);
            headerView.setLayoutParams(newParams);
            runJustBeforeBeingDrawn(headerView, new Runnable() {
                @Override
                public void run() {
                    headerView.setTranslationY(calcTranslation(headerView.getHeight(), nextHeaderPos));
                    headerView.requestLayout();
                }
            });
            // Uses postponed runnable, because removeViewAt(int) should not be invoked from drawing
            // related methods (e.g. {@link #onScrollListener} is invoked from RecyclerView's
            // onLayout(boolean, int, int, int, int)).
            post(new Runnable() {
                @Override
                public void run() {
                    if (getChildCount() > 1) {
                        removeViewAt(1);
                    }
                    addView(headerView);
                }
            });
        }

        /* Uses postponed runnable, because removeViewAt(int) should not be invoked from drawing
        related methods. */
        @Override
        public void removeHeaderView() {
            if (getChildCount() > 1) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (getChildCount() > 1) {
                            removeViewAt(1);
                        }
                    }
                });
            }
        }

        @Override
        public void translateHeaderView(final int nextHeaderPos) {
            if (getChildCount() > 1) {
                final View headerView = getChildAt(getChildCount() - 1);
                runJustBeforeBeingDrawn(headerView, new Runnable() {
                    @Override
                    public void run() {
                        headerView.setTranslationY(calcTranslation(headerView.getHeight(), nextHeaderPos));
                        headerView.requestLayout();
                    }
                });
            }
        }

        @Override
        public ViewGroup getHeaderViewParent() {
            return SectionHeaderLayout.this;
        }

    };

    /**
     * Notifies {@link SectionDataManager.HeaderManager} that the RecyclerView was scrolled, so the
     * header view could have been changed.
     */
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            headerManager.checkIsHeaderViewChanged();
        }

    };

    /**
     * Notifies {@link SectionDataManager.HeaderManager} that the header view could have been
     * changed. Uses {@link #runJustBeforeBeingDrawn(View, Runnable)} to provide a correct first
     * visible item position after the update.
     */
    private void checkHeaderView() {
        runJustBeforeBeingDrawn(recyclerView, new Runnable() {
            @Override
            public void run() {
                headerManager.checkIsHeaderViewChanged();
            }
        });
    }

    /**
     * Calculates yTranslation for the current header view based on its height and next header
     * position.
     *
     * @param headerHeight  Height of the current header view in px.
     * @param nextHeaderPos Adapter position of the next header view.
     * @return Calculated yTranslation for the header view.
     */
    private int calcTranslation(int headerHeight, int nextHeaderPos) {
        View nextHeaderView = recyclerView.getLayoutManager().findViewByPosition(nextHeaderPos);
        if (nextHeaderView != null) {
            int topOffset = nextHeaderView.getTop();
            int offset = headerHeight - topOffset;
            if (offset > 0) return -offset;
        }
        return 0;
    }

    /**
     * Runs the code just before the given view is being drawn so that its size has been already
     * calculated.
     *
     * @param view     View to be drawn.
     * @param runnable Code to run.
     */
    private static void runJustBeforeBeingDrawn(final View view, final Runnable runnable) {
        final ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                runnable.run();
                return true;
            }
        };
        view.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);
    }
}
