package mobi.dlys.android.familysafer.ui.login;

import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.LoginObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.main.GuideActivity;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import mobi.dlys.android.familysafer.utils.UpdateVersionUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;

	EditText mPhoneNumber = null;
	EditText mPassword = null;

	Button mLogin = null;
	TextView mFogot = null;

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

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
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
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeInput();
				Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
				intent.putExtra("fromLogin", true);
				startActivity(intent);
				finish();
			}

		});
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

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

			@Override
			public void onClick(View arg0) {
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
				Intent intent = new Intent(LoginActivity.this, Fogot1Activity.class);
				startActivity(intent);
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
		LoginObject loginObject = new LoginObject();
		loginObject.setPhone(mPhoneNumber.getEditableText().toString());
		loginObject.setPassword(mPassword.getEditableText().toString());
		CoreModel.getInstance().setLoginObject(loginObject);

		// 发送登录请求
		sendEmptyMessage(YSMSG.REQ_LOGIN);

		showWaitingDialog();
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
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
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
			UpdateVersionUtils.handleMessage(mActivity, msg);
		}
			break;
		}
	}

}
