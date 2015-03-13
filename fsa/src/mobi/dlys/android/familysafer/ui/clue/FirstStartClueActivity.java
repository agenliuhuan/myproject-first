package mobi.dlys.android.familysafer.ui.clue;

import java.util.ArrayList;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;

public class FirstStartClueActivity extends BaseExActivity {
    private ViewPager mViewPager;
    private ArrayList<View> mViewList = new ArrayList<View>();
    boolean firststartclue = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firststartclue);
        initView();
    }

    private void initView() {
        if (getIntent() != null) {
            firststartclue = getIntent().getBooleanExtra("firststartclue", false);
        }

        mViewPager = (ViewPager) findViewById(R.id.firststartclue_viewpager);

        View view1 = LayoutInflater.from(getBaseContext()).inflate(R.layout.firstclueview1, null, false);
        View view2 = LayoutInflater.from(getBaseContext()).inflate(R.layout.firstclueview2, null, false);
        View view3 = LayoutInflater.from(getBaseContext()).inflate(R.layout.firstclueview3, null, false);

        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);

        mViewPager.setAdapter(pa);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int arg0) {

            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (arg0 == 2 && arg1 == 0 && arg2 == 0 && isScrolling) {
                    if (firststartclue) {
                        if (Start) {
                            Start = false;
                            Intent intent = new Intent(FirstStartClueActivity.this, ClueActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("type", "takephoto");
                            intent.putExtras(bundle);
                            startActivity(intent);
                            FirstStartClueActivity.this.finish();
                        }
                    } else {
                        FirstStartClueActivity.this.finish();
                    }
                }
            }

            public void onPageScrollStateChanged(int arg0) {
                if (arg0 == 1) {
                    isScrolling = true;
                } else {
                    isScrolling = false;
                }
            }
        });
    }

    boolean Start = true;
    boolean isScrolling;
    PagerAdapter pa = new PagerAdapter() {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mViewList.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mViewList.get(position));
            return mViewList.get(position);
        }
    };
}
