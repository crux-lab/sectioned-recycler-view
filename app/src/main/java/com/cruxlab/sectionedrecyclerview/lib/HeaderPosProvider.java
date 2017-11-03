package com.cruxlab.sectionedrecyclerview.lib;

/**
 * Interface for providing adapter position to duplicated header view holders.
 */
public interface HeaderPosProvider {

    int getHeaderAdapterPos(short sectionType);

}
