package cn.changl.safe360.android.ui.setting;

import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.utils.AndroidConfig;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;

public class Phone2Activity extends BaseExActivity {
	public static int Modify_Phone_Action_Id = 10000;
	public static int Modify_Phone_Action_Id_Result = 101;
	protected TitleBarHolder mTitleBar;

	EditText mCheckNumber = null;
	boolean mHasCheckNumber = false;

	TextView mPhoneTipTxt = null;
	Button mResend = null;
	int mTickCount = 35;

	public static void startActivityForResult(Activity activity) {
		Intent intent = new Intent(activity, Phone2Activity.class);
		activity.startActivityForResult(intent, Modify_Phone_Action_Id);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_2);

		initView();
		initData();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_phone_2_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mRight2.setText(R.string.titlebar_button_tip_over);

		mTitleBar.mRight2.setEnabled(false);
		mTitleBar.mRight2.setTextColor(R.color.setting_item_text_gray_color);
		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				complete();
			}
		});

		mPhoneTipTxt = (TextView) findViewById(R.id.tv_phone_2_tip_1);

		mCheckNumber = (EditText) this.findViewById(R.id.edt_phone_2_check);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mCheckNumber, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300);

		mCheckNumber.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			@Override
			public void afterTextChanged(Editable arg0) {
				if (null == temp) {
					return;
				}

				if (temp.length() > 0) {
					mHasCheckNumber = true;

				} else {
					mHasCheckNumber = false;
				}
				if (mHasCheckNumber) {
					if (null != mTitleBar) {
						mTitleBar.mRight.setEnabled(true);
						mTitleBar.mRight.setTextColor(0xff44b651);
					}
				} else {
					if (null != mTitleBar) {
						mTitleBar.mRight.setEnabled(false);
						mTitleBar.mRight.setTextColor(R.color.setting_item_text_gray_color);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				temp = arg0;
			}
		});

		mResend = (Button) findViewById(R.id.btn_phone_2_resend);

		mResend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				requestAuthCode();
				mTickCount = 35;
				String tips = String.valueOf(mTickCount);
				tips += getString(R.string.activity_phone_2_tv_resend);
				mResend.setTextColor(getResources().getColor(R.color.button_disable_text_color));
				mResend.setText(tips);
				mResend.setEnabled(false);
				handler.postDelayed(runnable, 1000);
			}
		});

		String tips = String.valueOf(mTickCount);
		tips += getString(R.string.activity_phone_2_tv_resend);
		mResend.setText(tips);
		mResend.setEnabled(false);

		handler.postDelayed(runnable, 1000);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				if (null != mResend) {
					if (mTickCount > 0) {
						String tips = String.valueOf(mTickCount);
						tips += getString(R.string.activity_phone_2_tv_resend);
						mResend.setText(tips);
						mResend.setEnabled(false);
					} else {
						mResend.setText(R.string.activity_register_2_tv_resend);
						mResend.setEnabled(true);
						mResend.setTextColor(Color.rgb(33, 33, 33));
						handler.removeCallbacks(runnable);
					}

				}
			}

		}
	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
			mTickCount--;
			if (mTickCount >= 0) {
				handler.postDelayed(this, 1000);
			}
		}
	};

	private void initData() {
		// RegisterObject regObject =
		// CoreModel.getInstance().getRegisterObject();
		// if (regObject != null) {
		// String phoneNumber = regObject.getPhone();
		// mPhoneTipTxt.setText(getString(R.string.activity_phone_2_tv_tip_1) +
		// " " + phoneNumber);
		// }
	}

	private void complete() {
		// 隐藏软键盘
		AndroidConfig.hiddenInput(this, mCheckNumber);

		// 保存用户输入的注册验证码

		// 发送请求
		sendEmptyMessage(YSMSG.REQ_MODIFY_PHONE);
		showWaitingDialog();
	}

	/*
	 * 请求验证码
	 */
	private void requestAuthCode() {
		// 发送获取验证码请求
		sendEmptyMessage(YSMSG.REQ_GET_MODIFY_PHONE_AUTH_CODE);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_MODIFY_PHONE: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 正确
				YSToast.showToast(this, R.string.toast_phone_modify_succeed);
				setResult(Modify_Phone_Action_Id_Result);
				Phone2Activity.this.finish();
			} else {
				// 错误
			}
		}
			break;
		case YSMSG.RESP_GET_MODIFY_PHONE_AUTH_CODE: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 正确
			} else {
				// 错误
			}
		}
			break;
		}
	}

}