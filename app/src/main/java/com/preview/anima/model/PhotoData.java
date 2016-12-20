package com.preview.anima.model;

import android.widget.ImageView;

/**
 * 图片数据model
 */
public class PhotoData {

    // 小图地址
    public String s_Url;
    // 大图地址
    public String b_Url;
    // 在屏幕上的位置
    public int[] locOnScreen = new int[]{-1, -1};
    // 图片的宽
    public int width = 0;
    // 图片的高
    public int height = 0;
    //
    public ImageView.ScaleType scaleType;
}
