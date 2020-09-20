package com.herewhite.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.herewhite.demo.R;
import com.herewhite.demo.utils.SelectHideUtil;
import com.herewhite.demo.widget.CirclePointView;

import java.util.List;

public class ColorSelectAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private int[] mColorList;

    public ColorSelectAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setList(int[] list) {
        mColorList = list;
    }

    @Override
    public int getCount() {
        if (mColorList != null) {
            return mColorList.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mColorList != null) {
            return mColorList[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final int tPosition = position;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_color_select, null);
            viewHolder.color =  convertView.findViewById(R.id.color);
            viewHolder.color_bordor = convertView.findViewById(R.id.color_border);
            viewHolder.color_select = convertView.findViewById(R.id.color_select);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SelectHideUtil.setSelect(viewHolder.color_select);
        viewHolder.color.setCircleColor(mColorList[position]);
        ;
        if (position == mColorList.length - 1) {
            //白色比较特殊与背景色一直，设置框和选中为黑色
            viewHolder.color_bordor.setCircleColor(mColorList[position - 1]);
            viewHolder.color_select.setCircleColor(mColorList[position - 1]);
        } else {
            viewHolder.color_bordor.setCircleColor(mColorList[position]);
            viewHolder.color_select.setCircleColor(mColorList[position]);
        }
        viewHolder.color_select.setAlpha(0);

        return convertView;
    }

    static class ViewHolder {
        CirclePointView color;
        CirclePointView color_bordor;
        CirclePointView color_select;
    }
}
