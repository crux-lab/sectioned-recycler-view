package com.cruxlab.sectionedrecyclerview.lib;

/**
 * Interface for conversion between section and adapter positions.
 */
public interface PositionConverter {

    /**
     * Returns the global adapter position that corresponds to the given section and item position
     * in it or -1, if the given values are invalid.
     *
     * @param section    Index of the section.
     * @param sectionPos Item position in section.
     * @return Global adapter position or -1.
     */
    int getAdapterPos(int section, int sectionPos);

    /**
     * Returns the section index that corresponds to the given global position in the adapter or -1,
     * if the given <code>adapterPosition</code> is invalid.
     *
     * @param adapterPos Global position in the adapter.
     * @return Section index or -1.
     */
    int getSection(int adapterPos);


    /**
     * Returns the item position in section that corresponds to the given global position in the
     * adapter or -1, if the given <code>adapterPosition</code> is invalid or corresponds to header.
     *
     * @param adapterPos Global position in adapter.
     * @return Position in section or -1.
     */
    int getPosInSection(int adapterPos);
}
