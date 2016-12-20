package com.preview.anima;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.preview.anima.model.PhotoData;

import java.util.ArrayList;

public class MainActivity extends Activity {

    String[] s_picArray = {"http://d040779c2cd49.scdn.itc.cn/s_w_z/pic/20161213/184474627873966848.jpg",
            "http://d040779c2cd49.scdn.itc.cn/s_w_z/pic/20161213/184474627999795968.jpg",
            "http://d040779c2cd49.scdn.itc.cn/s_w_z/pic/20161213/184474628071099136.jpg"
    };
    String[] b_picArray = {"http://d040779c2cd49.scdn.itc.cn/s_big/pic/20161213/184474627873966848.jpg",
            "http://d040779c2cd49.scdn.itc.cn/s_big/pic/20161213/184474627999795968.jpg",
            "http://d040779c2cd49.scdn.itc.cn/s_big/pic/20161213/184474628071099136.jpg"
    };


    ArrayList<String> s_PhotoList = new ArrayList<String>();
    ArrayList<String> b_PhotoList = new ArrayList<String>();


    /**
     * UI
     */
    GridView mGridView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        initData();
        //
        initUI();
        //

    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 初始化小图数据
        for (int i = 0; i < s_picArray.length; i++) {
            s_PhotoList.add(s_picArray[i]);
        }
        // 初始化大图
        for (int i = 0; i < b_picArray.length; i++) {
            b_PhotoList.add(b_picArray[i]);
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {

                return s_PhotoList.size();
            }

            @Override
            public Object getItem(int position) {
                return s_PhotoList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final ImageView mImage;
                if (convertView == null) {
                    mImage = new ImageView(MainActivity.this);
                    mImage.setLayoutParams(new AbsListView.LayoutParams((int) (getResources().getDisplayMetrics().density * 150), (int) (getResources().getDisplayMetrics().density * 150)));
                    mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    mImage = (ImageView) convertView;
                }
                // glide加载图片
                Glide.with(MainActivity.this).load(s_PhotoList.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(mImage);
                return mImage;
            }
        });

        //--------点击------
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                onGridItemClick(position);
            }
        });
    }

    private void onGridItemClick(int position) {
        // ------初始化数据-------
        ArrayList<PhotoData> mPhotoDataList = new ArrayList<PhotoData>();
        int viewCount = mGridView.getChildCount();
        for (int i = 0; i < viewCount; i++) {
            PhotoData photoData = new PhotoData();
            // ---Url数据---
            photoData.s_Url = s_picArray[i];
            photoData.b_Url = b_picArray[i];
            // ---View的location---
            ImageView view = (ImageView) mGridView.getChildAt(i);
            int[] locationOnScreen = new int[2];
            view.getLocationOnScreen(locationOnScreen);
            photoData.locOnScreen = locationOnScreen;
            // ---View的宽高---
            photoData.width = view.getWidth();
            photoData.height = view.getHeight();
            // scaleType
            photoData.scaleType = view.getScaleType();
            //
            mPhotoDataList.add(photoData);
        }
        // 设置图片数据
        PhotoPreviewActivity.setPhotoDataList(mPhotoDataList);
        // intent
        Intent intent = new Intent(MainActivity.this, PhotoPreviewActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }


}
