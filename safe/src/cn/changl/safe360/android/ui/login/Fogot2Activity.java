package cn.changl.safe360.android.ui.login;

import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.AndroidConfig;
import android.content.Context;
import android.content.Intent;
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
import cn.changl.safe360.android.biz.vo.RegisterObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;

public class Fogot2Activity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;

	EditText mCheckNumber = null;
	boolean mHasCheckNumber = false;

	TextView mPhoneTip = null;
	TextView tickTip = null;
	Button mResend = null;

	int mTickCount = 35;
	String phone;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, Fogot2Activity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fogot_2);

		initView();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_fogot_2_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mRight2.setText(R.string.titlebar_button_tip_next);
		mTitleBar.mRight2.setEnabled(false);
		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				nextStep();
			}
		});

		mCheckNumber = (EditText) this.findViewById(R.id.edt_fogot_2_check);
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
						mTitleBar.mRight2.setEnabled(true);
					}
				} else {
					if (null != mTitleBar) {
						mTitleBar.mRight2.setEnabled(false);
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

		Intent intent = this.getIntent();
		Bundle bl = intent.getExtras();
		if (bl != null) {
			mPhoneTip = (TextView) findViewById(R.id.tv_fogot_2_tip_2);
			phone = bl.getString("phone");
			String phonetip = String.format(getString(R.string.activity_fogot_2_tv_tip_1_1), phone);
			mPhoneTip.setText(phonetip);
		}
		tickTip = (TextView) findViewById(R.id.tv_fogot_2_tip_2);
		mResend = (Button) findViewById(R.id.btn_fogot_2_resend);
		mResend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendMessage(YSMSG.REQ_GET_LOST_PASSWORD_AUTH_CODE, 0, 0, phone);
				showWaitingDialog();

				mTickCount = 35;

				String tips = String.format(getString(R.string.activity_fogot_2_tv_tip_3), mTickCount);
				mResend.setText(tips);
				mResend.setTextColor(getResources().getColor(R.color.button_disable_text_color));

				mResend.setEnabled(false);

				handler.postDelayed(runnable, 1000);
			}
		});
		String tips = String.format(getString(R.string.activity_fogot_2_tv_tip_3), mTickCount);
		mResend.setText(tips);
		mResend.setEnabled(false);
		handler.postDelayed(runnable, 1000);

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (1 == msg.what) {
				if (null != mResend) {
					if (mTickCount > 0) {
						String tips = String.format(getString(R.string.activity_fogot_2_tv_tip_3), mTickCount);
						mResend.setText(tips);
						mResend.setTextColor(getResources().getColor(R.color.button_disable_text_color));
						mResend.setEnabled(false);
					} else {
						mResend.setText(R.string.activity_fogot_2_tv_resend);
						mResend.setEnabled(true);
						mResend.setTextColor(getResources().getColor(R.color.white));
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

	private void nextStep() {
		// 隐藏软键盘
		AndroidConfig.hiddenInput(this, mCheckNumber);

		// 保存用户输入的注册验证码
		RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
		if (regObject != null) {
			regObject.setAuthCode(mCheckNumber.getEditableText().toString());
		}
		// 发送验证修改密码的验证码请求
		sendEmptyMessage(YSMSG.REQ_VERIFY_AUTH_CODE);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_VERIFY_AUTH_CODE: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 验证通过
				if (CoreModel.getInstance().getRegisterObject() != null) {
					RegisterObject registObj = CoreModel.getInstance().getRegisterObject();
					registObj.setAuthCode(mCheckNumber.getEditableText().toString());
				}
				Intent intent = new Intent(Fogot2Activity.this, Fogot3Activity.class);
				startActivity(intent);
			} else {
				// 验证失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_GET_LOST_PASSWORD_AUTH_CODE: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {

			} else {
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		}
	}
}
