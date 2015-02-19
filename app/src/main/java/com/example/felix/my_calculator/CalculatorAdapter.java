package com.example.felix.my_calculator;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by felix on 15/2/17.
 */
public class CalculatorAdapter extends BaseAdapter {

    private String[] mStr;
    private Context mContext;

    public CalculatorAdapter(Context context, String[] strs) {
        mContext = context;
        mStr = strs;
    }

    @Override
    public int getCount() {
        return mStr.length;
    }

    @Override
    public Object getItem(int position) {
        return mStr[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_button, null);
        }
        //显示计算器的案件文字
        TextView tv = (TextView) convertView.findViewById(R.id.txt_button);
        String str = mStr[position];
        tv.setText(str);

        //为Back ，CE 设置不同的样式
        if ("Back".equals(str) || "CE".equals(str)) {
            tv.setBackgroundResource(R.drawable.selector_button_backspace);
            tv.setTextColor(Color.WHITE);
        }


        return convertView;
    }
}
