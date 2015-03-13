package mobi.dlys.android.familysafer.ui.main;

import java.util.ArrayList;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.login.LoginActivity;
import mobi.dlys.android.familysafer.ui.register.Register1Activity;
import mobi.dlys.android.familysafer.utils.UpdateVersionUtils;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class GuideActivity extends BaseExActivity {
	private ViewPager mViewPager;
	private ArrayList<View> mViewPagerList = new ArrayList<View>();

	Button mLogin = null;
	Button mRegister = null;

	ImageView mFirst = null;
	ImageView mSecond = null;
	ImageView mThird = null;
	ImageView mFourth = null;

	private int mExit = 0;

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, GuideActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mExit++;
			if (1 == mExit) {
				YSToast.showToast(getApplicationContext(), R.string.toast_exit_app);
				return true;
			} else if (2 == mExit) {
				App.getInstance().exitApp();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Intent intent = getIntent();
		if (null != intent) {
			boolean fromLogin = intent.getBooleanExtra("fromLogin", false);
			if (fromLogin) {
				setTheme(R.style.RevertThemeActivity);
			}
		}
		if (null != intent) {
			boolean fromReg = intent.getBooleanExtra("fromReg", false);
			if (fromReg) {
				setTheme(R.style.RevertThemeActivity);
			}
		}
		if (null != intent) {
			boolean fromFogot = intent.getBooleanExtra("fromFogot", false);
			if (fromFogot) {
				setTheme(R.style.RevertThemeActivity);
			}
		}

		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.activity_guide);

		initView();

	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.guide_viewpager);
		View page1 = LayoutInflater.from(this).inflate(R.layout.layout_guide_1, null), page2 = LayoutInflater.from(this).inflate(R.layout.layout_guide_2, null), page3 = LayoutInflater
				.from(this).inflate(R.layout.layout_guide_3, null), page4 = LayoutInflater.from(this).inflate(R.layout.layout_guide_4, null);
		
		mViewPagerList.add(page1);
		mViewPagerList.add(page2);
		mViewPagerList.add(page3);
		mViewPagerList.add(page4);
		PagerAdapter pa = new PagerAdapter() {
			@Override
			public int getCount() {
				return 4;
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
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		mLogin = (Button) this.findViewById(R.id.btn_guide_login);

		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});

		mRegister = (Button) this.findViewById(R.id.btn_guide_register);

		mRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(GuideActivity.this, Register1Activity.class);
				startActivity(intent);
				finish();
			}
		});

		mFirst = (ImageView) this.findViewById(R.id.img_guide_first);
		mSecond = (ImageView) this.findViewById(R.id.img_guide_second);
		mThird = (ImageView) this.findViewById(R.id.img_guide_third);
		mFourth = (ImageView) this.findViewById(R.id.img_guide_fourth);


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mExit = 0;

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

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
				if (null != mFourth) {
					mFourth.setImageResource(R.drawable.other);
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
				if (null != mFourth) {
					mFourth.setImageResource(R.drawable.other);
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
				if (null != mFourth) {
					mFourth.setImageResource(R.drawable.other);
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
				if (null != mFourth) {
					mFourth.setImageResource(R.drawable.current);
				}

				break;
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		BaseController.getInstance().addOutboxHandler(mHandler);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BaseController.getInstance().removeOutboxHandler(mHandler);
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_CHECK_VERSION: {
			UpdateVersionUtils.handleMessage(mActivity, msg);
		}
			break;
		}
	}

}
