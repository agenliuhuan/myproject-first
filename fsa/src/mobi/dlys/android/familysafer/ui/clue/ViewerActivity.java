package mobi.dlys.android.familysafer.ui.clue;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.image.universalimageloader.core.assist.FailReason;
import mobi.dlys.android.core.image.universalimageloader.core.listener.ImageLoadingListener;
import mobi.dlys.android.core.image.universalimageloader.core.listener.ImageLoadingProgressListener;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.view.RoundProgressBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewerActivity extends BaseExActivity {
    private Button mLeftBtn;
    private TextView mTitle;

    private ArrayList<String> mData;
    private ViewPager mViewPager;
    private ArrayList<View> mViewPagerList = new ArrayList<View>();
    private int position = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_viewer);
        initView();
        Intent intent = getIntent();
        if (null != intent) {
            mData = intent.getStringArrayListExtra("clues");
            position = intent.getIntExtra("position", 0);
            mTitle.setText(1 + "/" + mData.size());
        }
        setData(mData, position);
    }

    private void initView() {
        mLeftBtn = (Button) findViewById(R.id.viewer_leftBtn);
        mTitle = (TextView) findViewById(R.id.viewer_center_title);

        mLeftBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ViewerActivity.this.finish();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewer_viewpager);
        MyViewPagerAdapter mAdapter = new MyViewPagerAdapter(mViewPagerList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            public void onPageSelected(int position) {
                mTitle.setText((position + 1) + "/" + mData.size());
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (mViewPager != null) {
                    mViewPager.invalidate();
                }
            }

            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void setData(ArrayList<String> data, int position) {
        mViewPagerList.clear();
        if (null != mData) {
            for (int i = 0; i < mData.size(); i++) {
                String filename = mData.get(i);
                View convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_viewer_image, null);
                final ImageView imageView = (ImageView) convertView.findViewById(R.id.item_viewer_image);
                final RoundProgressBar pBar = (RoundProgressBar) convertView.findViewById(R.id.item_viewer_progressBar);
                if (!TextUtils.isEmpty(filename)) {
                    // ImageLoaderHelper.displayImage(filename, imageView,
                    // R.drawable.default_bg_image, false);
                    ImageLoaderHelper.displayImage(filename, imageView, 0, false, new ImageLoadingListener() {
                        public void onLoadingStarted(String imageUri, View view) {
                            pBar.setProgress(10);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            imageView.setVisibility(View.VISIBLE);
                            pBar.setVisibility(View.GONE);
                            imageView.setImageResource(R.drawable.default_bg_image);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            imageView.setVisibility(View.VISIBLE);
                            pBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    }, new ImageLoadingProgressListener() {
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            if (total != 0) {
                                pBar.setProgress((int) ((double) current / total * 100));
                            }

                        }
                    });
                } else {
                    imageView.setImageResource(R.drawable.default_bg_image);
                }
                mViewPagerList.add(convertView);
            }
        }
        MyViewPagerAdapter madapter = (MyViewPagerAdapter) mViewPager.getAdapter();
        madapter.notifyDataSetChanged(mViewPagerList);
        mViewPager.setCurrentItem(position);

    }

    class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> ViewList) {
            mListViews = ViewList;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        public int getCount() {

            return mListViews.size();
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (View) arg1;
        }

        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mListViews.get(position));
        }

        public void notifyDataSetChanged(List<View> ViewList) {
            mListViews = ViewList;
            notifyDataSetChanged();
            if (mViewPager != null) {
                mViewPager.invalidate();
            }
        }

    }
}