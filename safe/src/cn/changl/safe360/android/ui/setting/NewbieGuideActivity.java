package cn.changl.safe360.android.ui.setting;

import java.util.ArrayList;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.main.MainActivity;

public class NewbieGuideActivity extends BaseExActivity {
	private ViewPager mViewPager;
	private ArrayList<View> mViewPagerList = new ArrayList<View>();

	Button mLogin = null;
	Button mRegister = null;
//	Button mGuest = null;

	ImageView mFirst = null;
	ImageView mSecond = null;
	ImageView mThird = null;

	private boolean isNeedMain = false;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, NewbieGuideActivity.class);
	}

	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.activity_guide);

		initView();

	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.guide_viewpager);
		View page1 = LayoutInflater.from(this).inflate(R.layout.layout_guide_1, null), page2 = LayoutInflater.from(this).inflate(R.layout.layout_guide_2, null), page3 = LayoutInflater
				.from(this).inflate(R.layout.layout_guide_3, null);
		mViewPagerList.add(page1);
		mViewPagerList.add(page2);
		mViewPagerList.add(page3);
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
				((ViewPager) container).removeView(mViewPagerList.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(mViewPagerList.get(position));
				return mViewPagerList.get(position);
			}
		};
		mViewPager.setAdapter(pa);
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener2());

		mLogin = (Button) this.findViewById(R.id.btn_guide_login);
		mLogin.setVisibility(View.GONE);

		mRegister = (Button) this.findViewById(R.id.btn_guide_register);
		mRegister.setVisibility(View.GONE);

//		mGuest = (Button) this.findViewById(R.id.btn_guide_guest);
//		mGuest.setVisibility(View.GONE);

		mFirst = (ImageView) this.findViewById(R.id.img_guide_first);
		mSecond = (ImageView) this.findViewById(R.id.img_guide_second);
		mThird = (ImageView) this.findViewById(R.id.img_guide_third);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		isNeedMain = getIntent().getBooleanExtra("LoginOnNewVersion", false);

	}

	boolean isScrolling;

	public class MyOnPageChangeListener2 implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (arg0 == 1) {
				isScrolling = true;
			} else {
				isScrolling = false;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if (arg0 == 3 && arg1 == 0 && arg2 == 0 && isScrolling && isNeedMain) {
				Intent intent = new Intent(NewbieGuideActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				if (null != mFirst) {
					mFirst.setImageResource(R.drawable.current);
				}
				if (null != mSecond) {
					mSecond.setImageResource(R.drawable.other);
				}
				if (null != mThird) {
					mThird.setImageResource(R.drawable.other);
				}

				break;
			case 1:
				if (null != mFirst) {
					mFirst.setImageResource(R.drawable.other);
				}
				if (null != mSecond) {
					mSecond.setImageResource(R.drawable.current);
				}
				if (null != mThird) {
					mThird.setImageResource(R.drawable.other);
				}

				break;
			case 2:
				if (null != mFirst) {
					mFirst.setImageResource(R.drawable.other);
				}
				if (null != mSecond) {
					mSecond.setImageResource(R.drawable.other);
				}
				if (null != mThird) {
					mThird.setImageResource(R.drawable.current);
				}

				break;
			case 3:
				if (null != mFirst) {
					mFirst.setImageResource(R.drawable.other);
				}
				if (null != mSecond) {
					mSecond.setImageResource(R.drawable.other);
				}
				if (null != mThird) {
					mThird.setImageResource(R.drawable.other);
				}

				break;
			}
		}

	}
}
