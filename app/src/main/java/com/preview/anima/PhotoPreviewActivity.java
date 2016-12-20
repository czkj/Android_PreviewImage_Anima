package com.preview.anima;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.preview.anima.model.PhotoData;

import java.util.ArrayList;

public class PhotoPreviewActivity extends Activity {


    /**
     * UI
     */
    // 黑色背景蒙板
    private View mMaskView = null;
    // 用于缩放的ImageView
    private ImageView mScaleImageView = null;
    // 用于Alpha的ImageView
    private ImageView mAlphaImageView = null;

    // ViewPager
    private ViewPager mViewPager = null;


    /**
     * 数据(数据在退出时，清空)
     */
    //
    private static ArrayList<PhotoData> mPhotoDataList = new ArrayList<PhotoData>();
    // 屏幕的宽高
    private RectF mScreenRect = null;

    // 添加数据
    public static void setPhotoDataList(ArrayList<PhotoData> photoDataList) {
        if (photoDataList != null) {
            mPhotoDataList.clear();
            mPhotoDataList.addAll(photoDataList);
        }
    }

    //
    private int mPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //获取数据
        initData();
        //初始化组件
        initView();

        /**
         * 1、动态改变AlphaImage的位置和宽高
         */
        final PhotoData photoData = mPhotoDataList.get(mPosition);
        //设置小图在此activity中的位置
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mAlphaImageView.getLayoutParams();
        lp.leftMargin = photoData.locOnScreen[0];
        lp.topMargin = photoData.locOnScreen[1];
        lp.width = photoData.width;
        lp.height = photoData.height;
        mAlphaImageView.setScaleType(photoData.scaleType);
        mAlphaImageView.setLayoutParams(lp);
        // 加载AlphaImageView的小图
        Glide.with(PhotoPreviewActivity.this).load(photoData.s_Url).diskCacheStrategy(DiskCacheStrategy.ALL).into(mAlphaImageView);

        /**
         * 2、加载mScaleImageView的小图
         */
        Glide.with(PhotoPreviewActivity.this).load(photoData.s_Url).diskCacheStrategy(DiskCacheStrategy.ALL).into(mScaleImageView);

        /**
         * 3、开启动画
         */
        //
        mAlphaImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //
                calculateScaleAndStartZoomInAnim(photoData);
                //
                mAlphaImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出清空数据
        mPhotoDataList.clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            calculateScaleAndStartZoomOutAnim();
        }
        return true;
    }


    /**
     * 初始化数据
     */
    public void initData() {
        // 屏幕的宽高
        this.mScreenRect = getDisplayPixes();

        // Intent数据
        Intent intent = getIntent();
        this.mPosition = intent.getIntExtra("position", 0);
    }

    public void initView() {
        // 黑色背景蒙板
        mMaskView = findViewById(R.id.mask_View);
        // 用于缩放动画的ImageView
        mScaleImageView = (ImageView) findViewById(R.id.scale_imageView);
        // Alpha动画的ImageView
        mAlphaImageView = (ImageView) findViewById(R.id.alpha_imageView);


        //-----ViewPager-----
        mViewPager = (ViewPager) findViewById(R.id.detail_view);
        //ViewPager设置适配器
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, mPhotoDataList);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onViewPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // 显示的Item
        mViewPager.setCurrentItem(mPosition);
    }


    /**
     * 计算放大比例，开启放大动画
     *
     * @param photoData
     */
    private void calculateScaleAndStartZoomInAnim(final PhotoData photoData) {
        // 放大动画参数
        int translationX = (photoData.locOnScreen[0] + photoData.width / 2) - (int) (mScreenRect.width() / 2);
        int translationY = (photoData.locOnScreen[1] + photoData.height / 2) - (int) (mScreenRect.height() / 2);
        float scaleX = photoData.width / mScreenRect.width();
        float scaleY = photoData.height / mScreenRect.height();
        // 开启放大动画
        startZoomInAnim(mAlphaImageView, mScaleImageView, translationX, translationY, scaleX, scaleY, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewPager.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * 计算缩小比例，开启缩小动画
     */
    private void calculateScaleAndStartZoomOutAnim() {

        /**
         * 隐藏ViewPager
         */
        mViewPager.setVisibility(View.GONE);

        /**
         * 1、动态改变AlphaImage的位置和宽高
         */
        final PhotoData photoData = mPhotoDataList.get(mPosition);
        //设置小图在此activity中的位置
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mAlphaImageView.getLayoutParams();
        lp.leftMargin = photoData.locOnScreen[0];
        lp.topMargin = photoData.locOnScreen[1];
        lp.width = photoData.width;
        lp.height = photoData.height;
        mAlphaImageView.setScaleType(photoData.scaleType);
        mAlphaImageView.setLayoutParams(lp);
        // 加载AlphaImageView的小图
        Glide.with(PhotoPreviewActivity.this).load(photoData.s_Url).diskCacheStrategy(DiskCacheStrategy.ALL).into(mAlphaImageView);

        /**
         * 2、加载mScaleImageView的小图
         */
        Glide.with(PhotoPreviewActivity.this).load(photoData.s_Url).diskCacheStrategy(DiskCacheStrategy.ALL).into(mScaleImageView);


        /**
         * 3、计算参数
         */
        // 缩小动画参数
        int translationX = (photoData.locOnScreen[0] + photoData.width / 2) - (int) (mScreenRect.width() / 2);
        int translationY = (photoData.locOnScreen[1] + photoData.height / 2) - (int) (mScreenRect.height() / 2);
        float scaleX = photoData.width / mScreenRect.width();
        float scaleY = photoData.height / mScreenRect.height();
        // 开启缩小动画
        startZoomOutAnim(mAlphaImageView, mScaleImageView, translationX, translationY, scaleX, scaleY, new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                PhotoPreviewActivity.this.finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * @param position
     */
    private void onViewPageSelected(int position) {
        this.mPosition = position;
    }


    /**
     * #####################################动画begin###################################
     */

    /**
     * 开启放大动画
     *
     * @param scaleImageView
     * @param translationX   位移start
     * @param translationY
     * @param scaleX         scale start
     * @param scaleY
     */
    public void startZoomInAnim(final ImageView alphaImageView, final ImageView scaleImageView, int translationX, int translationY, float scaleX, float scaleY, Animator.AnimatorListener listener) {
        // 动画运行时间，单位ms
        int scaleAnimaTime = 39000;
        int alphaAnimaTime = scaleAnimaTime / 6;

        //-------放大动画--------
        AnimatorSet set = new AnimatorSet();
        set.play(
                ObjectAnimator.ofFloat(scaleImageView, "translationX", translationX, 0))
                .with(ObjectAnimator.ofFloat(scaleImageView, "translationY", translationY, 0))
                .with(ObjectAnimator.ofFloat(scaleImageView, "scaleX", scaleX, 1))
                .with(ObjectAnimator.ofFloat(scaleImageView, "scaleY", scaleY, 1));
        set.setDuration(scaleAnimaTime);
        set.setInterpolator(new DecelerateInterpolator());
        if (listener != null) {
            set.addListener(listener);
        }
        set.start();

        //-------alpha动画--------
        AnimatorSet set1 = new AnimatorSet();
        set1.play(ObjectAnimator.ofFloat(alphaImageView, "alpha", 1, 0))
                .with(ObjectAnimator.ofFloat(scaleImageView, "alpha", 0, 1))
                .with(ObjectAnimator.ofFloat(mMaskView, "alpha", 0, 1));
        set1.setDuration(alphaAnimaTime / 6);
        set1.setInterpolator(new DecelerateInterpolator());
        set1.start();
    }


    /**
     * 开启缩小动画
     *
     * @param scaleImageView
     * @param translationX   位移start
     * @param translationY
     * @param scaleX         scale start
     * @param scaleY
     */
    public void startZoomOutAnim(final ImageView alphaImageView, final ImageView scaleImageView, int translationX, int translationY, float scaleX, float scaleY, Animator.AnimatorListener listener) {
        // 动画运行时间，单位ms
        int scaleAnimaTime = 39000;
        int alphaAnimaTime = scaleAnimaTime + scaleAnimaTime / 10;


        //-------缩小动画--------
        AnimatorSet set = new AnimatorSet();
        set.play(
                ObjectAnimator.ofFloat(scaleImageView, "translationX", 0, translationX))
                .with(ObjectAnimator.ofFloat(scaleImageView, "translationY", 0, translationY))
                .with(ObjectAnimator.ofFloat(scaleImageView, "scaleX", 1, scaleX))
                .with(ObjectAnimator.ofFloat(scaleImageView, "scaleY", 1, scaleY));
        set.setDuration(scaleAnimaTime);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();

        //-------alpha动画--------
        AnimatorSet set1 = new AnimatorSet();
        set1.play(ObjectAnimator.ofFloat(alphaImageView, "alpha", 0, 1))
                .with(ObjectAnimator.ofFloat(scaleImageView, "alpha", 1, 0))
                .with(ObjectAnimator.ofFloat(mMaskView, "alpha", 1, 0));
        set1.setDuration(alphaAnimaTime);
        set1.setInterpolator(new DecelerateInterpolator());
        if (listener != null) {
            set.addListener(listener);
        }
        set1.start();
    }


    /**
     * #####################################屏幕宽高###################################
     */

    /**
     * @return 整个屏幕的坐标
     */
    public RectF getDisplayPixes() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new RectF(0, 0, metrics.widthPixels, metrics.heightPixels);
    }

}
