package cn.changl.safe360.android.ui.register;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.AndroidConfig;
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
import android.widget.EditText;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.RegisterObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class Register1Activity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;

	EditText mPhoneNumber = null;
	EditText mPassword = null;

	boolean mHasPhoneNumber = false;
	boolean mHasPassword = false;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, Register1Activity.class);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeInput();
			// Intent intent = new Intent(Register1Activity.this,
			// GuideActivity.class);
			// intent.putExtra("fromReg", true);
			// startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_1);

		initView();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_register_1_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeInput();
				// Intent intent = new Intent(Register1Activity.this,
				// GuideActivity.class);
				// intent.putExtra("fromReg", true);
				// startActivity(intent);
				finish();
			}

		});
		mTitleBar.mRight2.setText(R.string.titlebar_button_tip_next);
		mTitleBar.mRight2.setEnabled(false);
		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mPhoneNumber.getText().charAt(0) != '1') {
					YSToast.showToast(getApplicationContext(), R.string.toast_phone_format_error);
					return;
				}
				if (mPhoneNumber.getText().length() != 11) {
					YSToast.showToast(getApplicationContext(), R.string.toast_phone_length_error);
					return;
				}
				if (mPassword.getText().length() < 6) {
					YSToast.showToast(getApplicationContext(), R.string.toast_password_min_error);
					return;
				}
				nextStep();
			}
		});

		// mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// closeInput();
		// // Intent intent = new Intent(Register1Activity.this,
		// GuideActivity.class);
		// // intent.putExtra("fromLogin", true);
		// // startActivity(intent);
		// finish();
		//
		// }
		// });

		mPhoneNumber = (EditText) this.findViewById(R.id.edt_register_1_phone);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mPhoneNumber, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300);

		mPhoneNumber.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
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
						mHasPhoneNumber = false;

					} else if (temp.length() > 11) {
						arg0.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						mPhoneNumber.setText(arg0);
						mPhoneNumber.setSelection(tempSelection);
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_length_error));
						mHasPhoneNumber = false;
					}
					if (temp.length() == 11) {
						mPhoneNumber.clearFocus();
						mPassword.requestFocus();
						mHasPhoneNumber = true;
					} else {
						mHasPhoneNumber = false;
					}
				} else {
					mHasPhoneNumber = false;
				}
				if (mHasPhoneNumber && mHasPassword) {
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

		mPassword = (EditText) this.findViewById(R.id.edt_register_1_password);
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
				if (temp.length() >= 6) {
					mHasPassword = true;
					if (temp.length() > 15) {
						arg0.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						mPassword.setText(arg0);
						mPassword.setSelection(tempSelection);
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_password_max_error));
					}
				} else {

					mHasPassword = false;
				}
				if (mHasPhoneNumber && mHasPassword) {
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

	}

	private void closeInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(Register1Activity.this.getCurrentFocus().getWindowToken(), 0);
		}
	}

	private void nextStep() {
		// 隐藏软键盘
		AndroidConfig.hiddenInput(this, mPhoneNumber);
		AndroidConfig.hiddenInput(this, mPassword);

		// 保存注册信息
		RegisterObject registerObj = new RegisterObject();
		registerObj.setPhone(mPhoneNumber.getText().toString());
		registerObj.setPassword(mPassword.getText().toString());
		CoreModel.getInstance().setRegisterObject(registerObj);
		// 检查用户是否注册
		ArrayList<String> list = new ArrayList<String>();
		list.add(mPhoneNumber.getEditableText().toString());
		sendMessage(YSMSG.REQ_CHECK_USER_REGISTER, 0, 0, list);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_CHECK_USER_REGISTER: {

			if (msg.arg1 == 200) {
				// 成功,发送获取注册验证码请求
				List<UserInfo> userInfoList = (List<UserInfo>) msg.obj;
				if (userInfoList != null) {
					boolean regist = userInfoList.get(0).getRegistStatus();
					if (regist) {
						dismissWaitingDialog();
						YSToast.showToast(this, R.string.toast_phone_registered_error);
					} else {
						sendEmptyMessage(YSMSG.REQ_GET_REG_AUTH_CODE);
					}
				}
			} else {
				// 失败
				dismissWaitingDialog();
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_GET_REG_AUTH_CODE: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 成功
				App.getInstance().addActivity(Register1Activity.this);
				Intent intent = new Intent(Register1Activity.this, Register2Activity.class);
				Bundle bl = new Bundle();
				bl.putString("phone", mPhoneNumber.getEditableText().toString());
				intent.putExtras(bl);
				startActivity(intent);
			} else {
				// 失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
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

}
