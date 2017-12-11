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
import com.cruxlab.sectionedrecyclerview.lib.SimpleSectionAdapter;
import com.cruxlab.sectionedrecyclerview.demo.view_holders.SimpleItemVH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class HeaderlessAdapter extends SimpleSectionAdapter<SimpleItemVH> {

    public ArrayList<String> strings = new ArrayList<>(Arrays.asList(
            "This is a section without header.",
            "Use header buttons to pin/unpin header, duplicate/change/remove section.",
            "Use section with header item buttons to hide/show header, duplicate/change/remove item.",
            "Use section without header item buttons to duplicate/change/remove item range."));

    @Override
    public SimpleItemVH onCreateItemViewHolder(ViewGroup parent, short type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_vh, parent, false);
        return new SimpleItemVH(view);
    }

    @Override
    public void onBindItemViewHolder(SimpleItemVH holder, int position) {
        holder.bind(this, strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    // Inserting some same items to the RecyclerView.
    public void duplicateItems(int pos) {
        String text = strings.get(pos);
        int cnt = new Random().nextInt(3) + 1;
        for (int i = 0; i < cnt; i++) {
            strings.add(pos + 1, text);
        }
        notifyItemRangeInserted(pos + 1, cnt);
    }

    // Removing this and next same items from the RecyclerView.
    public void removeItems(int pos) {
        String text = strings.get(pos);
        int cnt = 0;
        while (pos < getItemCount() && strings.get(pos).equals(text)) {
            strings.remove(pos);
            cnt++;
        }
        notifyItemRangeRemoved(pos, cnt);
    }

    // Updating this and next same item's text.
    public void changeItems(int pos) {
        String text = strings.get(pos);
        int changePos = pos;
        while (changePos < getItemCount() && strings.get(changePos).equals(text)) {
            strings.set(changePos, text + " *changed*");
            changePos++;
        }
        notifyItemRangeChanged(pos, changePos - pos);
    }

}
