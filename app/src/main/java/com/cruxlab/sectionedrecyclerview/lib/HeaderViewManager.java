package com.cruxlab.sectionedrecyclerview.lib;

import android.view.View;
import android.view.ViewGroup;

interface HeaderViewManager {

    int getFirstVisiblePos();
    void addHeaderView(View headerView, int nextHeaderPos);
    void removeHeaderView();
    void translateHeaderView(int nextHeaderPos);
    ViewGroup getHeaderViewParent();

}
