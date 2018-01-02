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

package com.cruxlab.sectionedrecyclerview.demo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxlab.sectionedrecyclerview.demo.R;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.HeaderVH;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultAdapter extends BaseAdapter<HeaderVH> {

    public DefaultAdapter(int color, SectionManager sectionManager, boolean isHeaderVisible, boolean isHeaderPinned) {
        super(color, sectionManager, isHeaderVisible, isHeaderPinned);
        this.type = 1;
        this.strings = new ArrayList<>(Arrays.asList(
                "Items in this RecyclerView are divided into groups called sections.",
                "Each section is represented by an adapter and can have a header.",
                "Just like RecyclerView.Adapter section adapter creates and binds ViewHolders."));
    }

    @Override
    public BaseAdapter getCopy() {
        DefaultAdapter copy = new DefaultAdapter(color, sectionManager, isHeaderVisible(), isHeaderPinned());
        copy.strings = new ArrayList<>(strings);
        return copy;
    }

    @Override
    public BaseAdapter getNext() {
        return new SimpleAdapter(color, sectionManager, isHeaderVisible(), isHeaderPinned());
    }

    @Override
    public HeaderVH onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_header_vh, parent, false);
        return new HeaderVH(view);
    }

}
