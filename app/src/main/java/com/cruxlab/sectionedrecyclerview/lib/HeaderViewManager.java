package com.cruxlab.sectionedrecyclerview.lib;

import android.view.View;
import android.view.ViewGroup;

/**
 * Interface for interaction with header view in {@link SectionedRVLayout}.
 */
interface HeaderViewManager {

    int getFirstVisiblePos();
    void addHeaderView(View headerView, int nextHeaderPos);
    void removeHeaderView();
    void translateHeaderView(int nextHeaderPos);
    ViewGroup getHeaderViewParent();

}
