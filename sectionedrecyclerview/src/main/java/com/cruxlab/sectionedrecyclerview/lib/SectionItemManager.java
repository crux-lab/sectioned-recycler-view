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
 * Interface for interaction between {@link BaseSectionAdapter} and RecyclerView.
 */
interface SectionItemManager {

    void notifyInserted(int section, int pos);
    void notifyRemoved(int section, int pos);
    void notifyChanged(int section, int pos);
    void notifyRangeInserted(int section, int startPos, int cnt);
    void notifyRangeRemoved(int section, int startPos, int cnt);
    void notifyRangeChanged(int section, int startPos, int cnt);
    void notifyDataSetChanged(int section);
    void notifyMoved(int section, int fromPos, int toPos);
    void notifyHeaderChanged(int section);
    void notifyHeaderVisibilityChanged(int section, boolean visible);
    void notifyHeaderPinnedStateChanged(int section, boolean pinned);

}
