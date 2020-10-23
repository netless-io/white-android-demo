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
import com.herewhite.sdk.domain.Scene;

import java.util.ArrayList;
import java.util.Map;

public class FolderSelectAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
//    private Scene[] mPageList;
    private Map<String, Scene[]> mStringMap;
    private ArrayList<String> mKeyList = new ArrayList<>();
    private ArrayList<Scene[]> mSceneList = new ArrayList<>();
    private OnPreviewItemClick mOnPreviewItemClick;
    private String mIndex;

    public FolderSelectAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

//    public void setList(Scene[] list) {
//        mPageList = list;
//    }

    public void setMap(Map<String, Scene[]> stringMap) {
        mStringMap = stringMap;
        mKeyList.clear();
        mSceneList.clear();
        for(Map.Entry<String, Scene[]> entry : mStringMap.entrySet()){
            String mapKey = entry.getKey();
            Scene[] mapValue = entry.getValue();
            if (mapKey.equals("/")) {
                mKeyList.add(0, mapKey);
                mSceneList.add(0, mapValue);
            } else {
                mKeyList.add(mapKey);
                mSceneList.add(mapValue);
            }

        }
    }

    public void setIndex(String index) {
        mIndex = index;
    }

    public void setListener(OnPreviewItemClick onPreviewItemClick) {
        mOnPreviewItemClick = onPreviewItemClick;
    }



    @Override
    public int getCount() {
        if (mStringMap != null) {
            return mStringMap.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
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
        if (position == 0) {
            viewHolder.previewDel.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.previewDel.setVisibility(View.VISIBLE);
        }
        String key = mKeyList.get(position);
        Scene[] scenes = mSceneList.get(position);
        if (mIndex.equals(key)) {
            viewHolder.previewImgIndex.setVisibility(View.VISIBLE);
        } else {
            viewHolder.previewImgIndex.setVisibility(View.GONE);
        }
        SelectUtil.setSelect(viewHolder.previewImg);
        SelectUtil.setSelect(viewHolder.previewDel);

        if (key.equals("/")) {
            viewHolder.previewName.setText(R.string.folder_first);
        } else if (key.startsWith("/static")){
            viewHolder.previewName.setText(R.string.folder_static);
        } else if (key.startsWith("/dynamic")) {
            viewHolder.previewName.setText(R.string.folder_dynamic);
        } else {
            viewHolder.previewName.setText(R.string.folder_unknow);
        }

        if (mOnPreviewItemClick != null) {
            mOnPreviewItemClick.getBitmap(position, key, scenes,  new Promise<Bitmap>() {
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
                    mOnPreviewItemClick.onPreviewImgClick(position, key, scenes);
                }
            }
        });
        viewHolder.previewDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPreviewItemClick != null) {
                    mOnPreviewItemClick.onPreviewDelClick(position, key, scenes);
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
        public void onPreviewImgClick(int position, String key, Scene[] scenes);
        public void onPreviewDelClick(int position, String key, Scene[] scenes);
        public void getBitmap(int position, String key, Scene[] scenes, Promise<Bitmap> promise);
    }
}
