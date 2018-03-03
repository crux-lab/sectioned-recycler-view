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

/**
 * Interface for conversion between section and adapter positions.
 */
public interface PositionManager {

    /**
     * Returns the global adapter position that corresponds to the given section and item position
     * in it or -1, if the given values are invalid.
     *
     * @param section Index of the section.
     * @param pos     Item position in section.
     * @return Global adapter position or -1.
     */
    int calcAdapterPos(int section, int pos);

    /**
     * Returns the section index that corresponds to the given global position in the adapter or -1,
     * if the given <code>adapterPos</code> is invalid.
     *
     * @param adapterPos Global position in the adapter.
     * @return Section index or -1.
     */
    int calcSection(int adapterPos);


    /**
     * Returns the item position in section that corresponds to the given global position in the
     * adapter or -1, if the given <code>adapterPos</code> is invalid or corresponds to header.
     *
     * @param adapterPos Global position in adapter.
     * @return Position in section or -1.
     */
    int calcPosInSection(int adapterPos);


    /**
     * Checks whether the given global adapter position of an item corresponds to a header.
     * If <code>adapterPos</code> is invalid, returns <code>false</code>.
     *
     * @param adapterPos Global position in adapter.
     * @return Whether an item is a header.
     */
    boolean isHeader(int adapterPos);

}
