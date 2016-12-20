package com.preview.anima;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.preview.anima.model.PhotoData;

import java.util.ArrayList;


public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;

    /**
     * 数据
     */
    private ArrayList<PhotoData> photoDataList = new ArrayList<PhotoData>();

    public ViewPagerAdapter(Context context, ArrayList<PhotoData> photoDataList) {
        this.mContext = context;
        //
        this.photoDataList.clear();
        this.photoDataList.addAll(photoDataList);
    }

    @Override
    public int getCount() {
        return photoDataList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        /**
         * UI
         */
        final ImageView imageView = new ImageView(mContext);
        /**
         * 数据
         */
        PhotoData photoData = photoDataList.get(position);
        // 下载大图
        Glide.with(mContext).load(photoData.b_Url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);


        //
        container.addView(imageView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
