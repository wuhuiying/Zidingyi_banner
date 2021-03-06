package com.why.wyhuiying1511a;

import android.content.Context;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.why.wyhuiying1511a.Bean.Title;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小慧莹 on 2018/1/2.
 */

public class CustomBanner extends FrameLayout {

    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private List<Title.DataBean> list =new ArrayList<>();
    private int time = 2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){

                int currentItem = viewPager.getCurrentItem();

                viewPager.setCurrentItem(currentItem +1);

                //再次发送
                sendEmptyMessageDelayed(0,time*1000);

            }
        }
    };
    private List<ImageView> listDoc;
    private OnClickLisner onClickLisner;

    public CustomBanner(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        View view = View.inflate(getContext(), R.layout.banner_layout,this);

        //找到控件
        viewPager = view.findViewById(R.id.banner_view_pager);
        linearLayout = view.findViewById(R.id.linear_bannner);
    }

    /**
     * 对外提供设置image路径的方法
     */
    public void setImageUrls(List<Title.DataBean> list){
        this.list = list;

        if (list == null){
            return;
        }

        //设置适配器
        LunBoAdapter lunBoAdapter = new LunBoAdapter(getContext(),list);
        viewPager.setAdapter(lunBoAdapter);

        initDoc();

        //显示中间某个位置
        viewPager.setCurrentItem(list.size()*10000);

        //使用handler自动轮播
        handler.sendEmptyMessageDelayed(0,time*1000);

        //状态改变的监听事件
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //在选中某一页的时候,切换小圆点的背景
                for (int i = 0;i<listDoc.size();i++){
                    if (position%listDoc.size() == i){
                        listDoc.get(i).setBackgroundResource(R.drawable.shape1);
                    }else {
                        listDoc.get(i).setBackgroundResource(R.drawable.shape2);
                    }
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    /**
     * 初始化小圆点
     */
    private void initDoc() {

        //创建一个集合,记录这些小圆点
        listDoc = new ArrayList<>();
        //清空布局
        linearLayout.removeAllViews();

        for (int i=0;i<list.size();i++){

            ImageView docImage = new ImageView(getContext());
            if (i == 0){
                docImage.setBackgroundResource(R.drawable.shape1);
            }else {
                docImage.setBackgroundResource(R.drawable.shape2);
            }

            //添加到集合
            listDoc.add(docImage);

            //添加到线性布局
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(5,0,5,0);

            linearLayout.addView(docImage,params);


        }


    }

    /**
     * 对外提供轮播的时间
     */
    public void setTimeSecond(int time){
        this.time = time;
    }

    /**
     * 点击事件
     * @param onClickLisner
     */
    public void setClickListner(OnClickLisner onClickLisner) {

        this.onClickLisner = onClickLisner;
    }

    private class LunBoAdapter extends PagerAdapter {

        private List<Title.DataBean> list;
        private Context context;

        public LunBoAdapter(Context context, List<Title.DataBean> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            //创建imageView
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //加载这张图片
            Glide.with(context).load(list.get(position%list.size()).getIcon()).into(imageView);


            //点击事件
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //触发
                    onClickLisner.onItemClick(position%list.size());
                }
            });

            //触摸事件
            imageView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            //取消handler身上的消息和回调
                            handler.removeCallbacksAndMessages(null);

                            break;
                        case MotionEvent.ACTION_MOVE:
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            handler.sendEmptyMessageDelayed(0,time*1000);
                            break;
                        case MotionEvent.ACTION_UP:
                            handler.sendEmptyMessageDelayed(0,time*1000);
                            break;
                    }

                    return false;
                }
            });

            //添加到容器
            container.addView(imageView);

            //返回

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);
        }
    }

    public interface OnClickLisner{
        void onItemClick(int position);
    }
}
