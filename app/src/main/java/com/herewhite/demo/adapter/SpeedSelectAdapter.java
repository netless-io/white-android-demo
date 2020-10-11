package com.herewhite.demo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.herewhite.demo.R;
import com.herewhite.demo.utils.SelectUtil;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.SDKError;

import java.util.List;

public class SpeedSelectAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private String[] mSpeedList;
    private int mIndex;
    private Context mContext;
    private OnSpeedItemClick mOnSpeedItemClick;

    public SpeedSelectAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setList(String[] list) {
        mSpeedList = list;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public void setListener(OnSpeedItemClick onSpeedItemClick) {
        mOnSpeedItemClick = onSpeedItemClick;
    }


    @Override
    public int getCount() {
        if (mSpeedList != null) {
            return mSpeedList.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mSpeedList != null) {
            return mSpeedList[position];
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
            convertView = mInflater.inflate(R.layout.item_speed_select, null);
            viewHolder.speedName = convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mIndex == position) {
            viewHolder.speedName.setTextColor(R.color.white_default);
        } else {
            viewHolder.speedName.setTextColor(R.color.color_slect_11);
        }

        viewHolder.speedName.setText(mSpeedList[position]);


        viewHolder.speedName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSpeedItemClick != null) {
                    mOnSpeedItemClick.onSpeedClick(position);
                }
            }
        });


        return convertView;
    }

    static class ViewHolder {
        TextView speedName;
    }

    public interface OnSpeedItemClick {
        public void onSpeedClick(int position);
    }
}
