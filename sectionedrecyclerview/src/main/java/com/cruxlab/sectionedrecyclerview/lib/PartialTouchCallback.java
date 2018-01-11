package com.cruxlab.sectionedrecyclerview.lib;

/**
 * Base class for callbacks that provide some
 * {@link android.support.v7.widget.helper.ItemTouchHelper.Callback}'s functionality. They use
 * {@link #defaultCallback} as default implementation.
 */
class PartialTouchCallback {

    DefaultTouchCallback defaultCallback;

    void checkDefaultCallback() {
        if (defaultCallback == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " hasn't been used for any section.");
        }
    }

}
