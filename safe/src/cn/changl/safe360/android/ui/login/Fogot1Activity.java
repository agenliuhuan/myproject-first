package cn.changl.safe360.android.ui.login;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.RegisterObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.utils.PreferencesUtils;

public class Fogot1Activity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;
	protected int mFogStag = 1;

	EditText mPhoneNumber = null;
	boolean mHasPhoneNumber = false;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, Fogot1Activity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fogot_1);

		initView();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_fogot_1_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				closeInput();
				// Intent intent = new Intent(Fogot1Activity.this,
				// GuideActivity.class);
				// intent.putExtra("fromFogot", true);
				// startActivity(intent);
				finish();
			}

		});
		mTitleBar.mRight2.setText(R.string.titlebar_button_tip_next);
		mTitleBar.mRight2.setEnabled(false);
		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				nextStep();
			}
		});

		mPhoneNumber = (EditText) this.findViewById(R.id.edt_fogot_1_phone);
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
					mHasPhoneNumber = true;
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
					}
					if (temp.length() == 11) {
						mHasPhoneNumber = true;
					} else {
						mHasPhoneNumber = false;
					}
				} else {
					mHasPhoneNumber = false;
				}
				if (mHasPhoneNumber) {
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
		String phone = PreferencesUtils.getLoginPhone();
		if (!TextUtils.isEmpty(phone)) {
			mPhoneNumber.setText(phone);
			mPhoneNumber.setSelection(phone.length());
		}
	}

	private void closeInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(Fogot1Activity.this.getCurrentFocus().getWindowToken(), 0);
		}
	}

	private void nextStep() {
		// 保存注册信息
		RegisterObject regObject = new RegisterObject();
		regObject.setPhone(mPhoneNumber.getEditableText().toString());
		CoreModel.getInstance().setRegisterObject(regObject);
		// 发送获取修改密码的验证码请求
		ArrayList<String> list = new ArrayList<String>();
		if (!TextUtils.isEmpty(mPhoneNumber.getEditableText().toString())) {
			list.add(mPhoneNumber.getEditableText().toString());
		}

		sendMessage(YSMSG.REQ_CHECK_USER_REGISTER, 0, 0, list);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_CHECK_USER_REGISTER: {
			if (msg.arg1 == 200) {
				// 发送获取修改密码的验证码请求
				RegisterObject regObject = new RegisterObject();
				regObject.setPhone(mPhoneNumber.getEditableText().toString());
				CoreModel.getInstance().setRegisterObject(regObject);
				sendEmptyMessage(YSMSG.REQ_GET_LOST_PASSWORD_AUTH_CODE);

			} else {
				// 用户未注册
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
		case YSMSG.RESP_GET_LOST_PASSWORD_AUTH_CODE: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 成功
				Intent intent = new Intent(Fogot1Activity.this, Fogot2Activity.class);
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
		}
			break;
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
