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

package com.cruxlab.sectionedrecyclerview.demo.view_holders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cruxlab.sectionedrecyclerview.lib.BaseSectionAdapter;
import com.cruxlab.sectionedrecyclerview.demo.R;
import com.cruxlab.sectionedrecyclerview.demo.adapters.HeaderlessAdapter;

public class SimpleItemVH extends BaseSectionAdapter.ItemViewHolder {

    private ImageButton btnDuplicate, btnChange, btnRemove;
    private TextView tvText;

    public SimpleItemVH(View itemView) {
        super(itemView);
        this.btnDuplicate = itemView.findViewById(R.id.ibtn_duplicate);
        this.btnChange = itemView.findViewById(R.id.ibtn_change);
        this.btnRemove = itemView.findViewById(R.id.ibtn_remove);
        this.tvText = itemView.findViewById(R.id.tv_text);
    }

    public void bind(final HeaderlessAdapter adapter, final String string) {
        tvText.setText(string);
        btnDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The adapter position is used to calculate what section index corresponds to this
                // ViewHolder. So, this method returns -1 when the ViewHolder is not used in any
                // RecyclerView.
                // In our case it can happen when we click on a delete button of the view whose removal
                // animation is still in progress.
                int sectionPos = getSectionAdapterPosition();
                if (sectionPos == -1) return;

                adapter.duplicateItems(sectionPos);

            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The adapter position is used to calculate what section index corresponds to this
                // ViewHolder. So, this method returns -1 when the ViewHolder is not used in any
                // RecyclerView.
                // In our case it can happen when we click on a delete button of the view whose removal
                // animation is still in progress.
                int sectionPos = getSectionAdapterPosition();
                if (sectionPos == -1) return;

                adapter.changeItems(sectionPos);
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The adapter position is used to calculate what section index corresponds to this
                // ViewHolder. So, this method returns -1 when the ViewHolder is not used in any
                // RecyclerView.
                // In our case it can happen when we click on a delete button of the view whose removal
                // animation is still in progress.
                int sectionPos = getSectionAdapterPosition();
                if (sectionPos == -1) return;

                adapter.removeItems(sectionPos);
            }
        });
    }

}
