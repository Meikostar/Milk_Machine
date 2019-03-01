package com.canplay.milk.mvp.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.bean.IMG;
import com.mykar.framework.ui.view.image.photoview.PhotoView;



/**
 * Created by honghouyang on 16/9/12.
 */
public class ImageListWitesActivity extends BaseActivity {
    private ViewPager viewPager;
    private TextView pageText;
    private IMG img;
    private ViewPagerAdapter adapter;
    private int position;


    public void initViews() {
        setContentView(R.layout.activity_image_list);
        img = (IMG) getIntent().getSerializableExtra("img");
        position = getIntent().getIntExtra("position", 0);
        initMainView();
    }

    @Override
    public void bindEvents() {

    }

    @Override
    public void initData() {

    }


    private void initMainView() {
        pageText = (TextView) findViewById(R.id.page_text);
        viewPager = (ViewPager) findViewById(R.id.image_pager);
        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        pageText.setText(position+1  + "/" + img.child_list.size());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int new_position) {
                position = new_position;
                pageText.setText(position + 1 + "/" + img.child_list.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int new_position) {
            View view = LayoutInflater.from(ImageListWitesActivity.this).inflate(
                    R.layout.lp_pager_img_list, null);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.img_content);

//            int screenWidth = Win.getScreenWidth(ImageListWitesActivity.this); // 获取屏幕宽度
//            ViewGroup.LayoutParams lp = photoView.getLayoutParams();
//            lp.width = screenWidth;
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            photoView.setLayoutParams(lp);
//            photoView.setMaxWidth(screenWidth);
//            photoView.setMaxHeight(screenWidth * 5); //这里其实可以根据需求而定，我这里测试为最大宽度的5倍
            Glide.with(ImageListWitesActivity.this).load( img.child_list.get(new_position).img_url).asBitmap()
                    .placeholder(R.drawable.moren).into(photoView);

//            photoView.setImageWithURL(ImageListWitesActivity.this, img.child_list.get(new_position).img_url);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return img.child_list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
