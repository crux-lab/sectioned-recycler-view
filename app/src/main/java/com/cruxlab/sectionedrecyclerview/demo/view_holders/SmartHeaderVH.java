package com.cruxlab.sectionedrecyclerview.demo.view_holders;

import android.view.View;
import android.widget.TextView;

import com.cruxlab.sectionedrecyclerview.R;
import com.cruxlab.sectionedrecyclerview.demo.adapters.BaseAdapter;

public class SmartHeaderVH extends HeaderVH {

    private TextView tvItemCnt;

    public SmartHeaderVH(View itemView) {
        super(itemView);
        tvItemCnt = itemView.findViewById(R.id.tv_item_cnt);
    }

    @Override
    public void bind(BaseAdapter adapter, int color) {
        super.bind(adapter, color);
        tvItemCnt.setText(String.valueOf(adapter.getItemCount()));
    }

}
