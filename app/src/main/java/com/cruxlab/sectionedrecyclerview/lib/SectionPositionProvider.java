package com.cruxlab.sectionedrecyclerview.lib;

/**
 * Interface for converting adapter position to section position.
 */
interface SectionPositionProvider {

    int getPosInSection(int adapterPos);

}
