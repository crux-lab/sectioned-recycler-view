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
import com.cruxlab.sectionedrecyclerview.lib.SectionAdapter;
import com.cruxlab.sectionedrecyclerview.lib.SectionManager;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.HeaderVH;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.ItemVH;

import java.util.ArrayList;

public abstract class BaseAdapter<HVH extends HeaderVH> extends SectionAdapter<ItemVH, HVH> {

    public ArrayList<String> strings;
    public SectionManager sectionManager;
    public short type;
    public int color;

    public BaseAdapter(int color, SectionManager sectionManager, boolean isHeaderVisible, boolean isHeaderPinned) {
        super(isHeaderVisible, isHeaderPinned);
        this.color = color;
        this.sectionManager = sectionManager;
    }

    public abstract BaseAdapter getCopy();
    public abstract BaseAdapter getNext();

    // Inserting the same item to the RecyclerView.
    public void duplicateItem(int pos) {
        strings.add(pos + 1, strings.get(pos));
        notifyItemInserted(pos + 1);
    }

    // Removing an item from the RecyclerView.
    public void removeItem(int pos) {
        strings.remove(pos);
        notifyItemRemoved(pos);
    }

    // Updating item's text.
    public void changeItem(int pos) {
        strings.set(pos, strings.get(pos) + " *changed*");
        notifyItemChanged(pos);
    }

    @Override
    public ItemVH onCreateItemViewHolder(ViewGroup parent, short type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vh, parent, false);
        return new ItemVH(view);
    }

    @Override
    public void onBindItemViewHolder(ItemVH holder, int position) {
        holder.bind(this, strings.get(position));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderVH holder) {
        holder.bind(this, color);
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

}