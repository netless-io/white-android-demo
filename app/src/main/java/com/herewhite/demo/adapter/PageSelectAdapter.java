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
import com.herewhite.demo.utils.SelectHideUtil;
import com.herewhite.demo.utils.SelectUtil;
import com.herewhite.demo.widget.CirclePointView;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.SDKError;
import com.herewhite.sdk.domain.Scene;

import java.util.List;

public class PageSelectAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Scene[] mPageList;
    private OnPreviewItemClick mOnPreviewItemClick;
    private int mIndex;

    public PageSelectAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setList(Scene[] list) {
        mPageList = list;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public void setListener(OnPreviewItemClick onPreviewItemClick) {
        mOnPreviewItemClick = onPreviewItemClick;
    }



    @Override
    public int getCount() {
        if (mPageList != null) {
            return mPageList.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mPageList != null) {
            return mPageList[position];
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
            convertView = mInflater.inflate(R.layout.item_page_select, null);
            viewHolder.previewImgIndex = convertView.findViewById(R.id.img_preview_select);
            viewHolder.previewImg =  convertView.findViewById(R.id.img_preview);
            viewHolder.previewName = convertView.findViewById(R.id.tv_name);
            viewHolder.previewDel = convertView.findViewById(R.id.img_del);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mIndex == position) {
            viewHolder.previewImgIndex.setVisibility(View.VISIBLE);
        } else {
            viewHolder.previewImgIndex.setVisibility(View.GONE);
        }
        SelectUtil.setSelect(viewHolder.previewImg);
        SelectUtil.setSelect(viewHolder.previewDel);
        viewHolder.previewName.setText("" + (position + 1));
        if (mOnPreviewItemClick != null) {
            mOnPreviewItemClick.getBitmap(position, new Promise<Bitmap>() {
                @Override
                public void then(Bitmap bitmap) {
                    viewHolder.previewImg.setImageBitmap(bitmap);
                }

                @Override
                public void catchEx(SDKError t) {

                }
            });
        }

        viewHolder.previewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPreviewItemClick != null) {
                    mOnPreviewItemClick.onPreviewImgClick(position);
                }
            }
        });
        viewHolder.previewDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPreviewItemClick != null) {
                    mOnPreviewItemClick.onPreviewDelClick(position);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView previewImgIndex;
        ImageView previewImg;
        TextView previewName;
        ImageView previewDel;
    }

    public interface OnPreviewItemClick {
        public void onPreviewImgClick(int position);
        public void onPreviewDelClick(int position);
        public void getBitmap(int position, Promise<Bitmap> promise);
    }
}
