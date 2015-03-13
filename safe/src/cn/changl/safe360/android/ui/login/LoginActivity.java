package cn.changl.safe360.android.ui.login;

import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.core.utils.LogUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.LoginObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.ui.main.MainActivity;
import cn.changl.safe360.android.ui.register.Register1Activity;
import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.android.utils.UpdateVersionUtils;

public class LoginActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;
	private EditText mPhoneNumber = null;
	private EditText mPassword = null;

	private Button mLogin = null;
	private TextView mFogot = null;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, LoginActivity.class);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeInput();
			Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
			intent.putExtra("fromLogin", true);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle outState) {
		super.onCreate(outState);

		// 后台运行
		Intent intent = getIntent();
		if (null != intent) {
			boolean move = intent.getBooleanExtra("movetasktoback", false);
			LogUtils.i("movetasktoback", String.valueOf(move));
			if (move) {
				moveTaskToBack(true);
			}
		}

		setContentView(R.layout.activity_login);

		initView();
		initData();

	}

	/**
	 * 初始化子视图
	 */
	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_login_ttb_title);
		mTitleBar.mRight2.setText(R.string.activity_login_ttb_righttext);
		mTitleBar.mRight2.setEnabled(true);
		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Register1Activity.startActivity(mActivity);
			}
		});
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeInput();
				Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
				intent.putExtra("fromReg", true);
				startActivity(intent);
				finish();
			}
		});
		mPhoneNumber = (EditText) this.findViewById(R.id.edt_login_phone);
		mPhoneNumber.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			public void afterTextChanged(Editable arg0) {
				if (null == temp) {
					return;
				}
				selectionStart = mPhoneNumber.getSelectionStart();
				selectionEnd = mPhoneNumber.getSelectionEnd();
				if (temp.length() > 0) {
					if (temp.charAt(0) != '1') {

						YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_format_error));

						arg0.clear();
						mPhoneNumber.setText(arg0);

					} else if (temp.length() > 11) {
						arg0.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						mPhoneNumber.setText(arg0);
						mPhoneNumber.setSelection(tempSelection);
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_length_error));
					}
				}
				if (temp.length() == 11) {
					mPhoneNumber.clearFocus();
					mPassword.requestFocus();
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

		mPassword = (EditText) this.findViewById(R.id.edt_login_password);
		mPassword.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void afterTextChanged(Editable arg0) {
				if (null == temp) {
					return;
				}
				selectionStart = mPassword.getSelectionStart();
				selectionEnd = mPassword.getSelectionEnd();
				if (temp.length() > 15) {
					arg0.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					mPassword.setText(arg0);
					mPassword.setSelection(tempSelection);
					YSToast.showToast(getApplicationContext(), getString(R.string.toast_password_max_error));
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

		mLogin = (Button) this.findViewById(R.id.btn_login_login);
		mLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (mPhoneNumber.getText().length() == 0) {
					mPhoneNumber.requestFocus();
					return;

				}
				if (mPassword.getText().length() == 0) {
					mPassword.requestFocus();
					return;
				}
				if (mPassword.getText().length() < 6) {
					YSToast.showToast(getApplicationContext(), R.string.toast_password_min_error);
					return;
				}
				login();
			}
		});
		mFogot = (TextView) this.findViewById(R.id.tv_login_fogot);
		mFogot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Fogot1Activity.startActivity(mActivity);
			}
		});

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mPhoneNumber, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300);

		String phone = PreferencesUtils.getLoginPhone();
		if (!TextUtils.isEmpty(phone)) {
			mPhoneNumber.setText(phone);
			mPassword.requestFocus();
		}

	}

	private void closeInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen && getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	private void login() {
		// 隐藏软键盘
		AndroidConfig.hiddenInput(this, mPhoneNumber);
		AndroidConfig.hiddenInput(this, mPassword);

		// 保存用户输入的用户名和密码
		LoginObject loginObj = new LoginObject();
		loginObj.setPhone(mPhoneNumber.getText().toString());
		loginObj.setPassword(mPassword.getText().toString());
		loginObj.setLat(String.valueOf(App.getInstance().getLocater().getLat()));
		loginObj.setLng(String.valueOf(App.getInstance().getLocater().getLng()));
		loginObj.setLocation(App.getInstance().getLocater().getAddress());
		// 发送登录请求
		sendMessage(YSMSG.REQ_LOGIN, 0, 0, loginObj);

		showOrUpdateWaitingDialog(R.string.activity_login_login_dialog_msg);

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
		case YSMSG.RESP_LOGIN: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 登录成功
				MainActivity.startActivity(mActivity);
				LoginActivity.this.finish();
			} else {
				// 登录失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_CHECK_VERSION: {
			UpdateVersionUtils.handleMessage(App.getInstance().getForegroundActivity(), msg);
		}
			break;
		}
	}

}
