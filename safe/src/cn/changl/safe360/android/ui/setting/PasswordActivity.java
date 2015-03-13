package cn.changl.safe360.android.ui.setting;

import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.AndroidConfig;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ModifyPwdObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;

public class PasswordActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	EditText mPassword = null;
	EditText mNewPassword = null;

	boolean mHasPassword = false;
	boolean mHasNewPassword = false;
	
	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, PasswordActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);

		initView();

	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_password_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mRight2.setText(R.string.titlebar_button_tip_over);

		mTitleBar.mRight2.setEnabled(false);
		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mPassword.getText().length() < 6) {
					YSToast.showToast(getApplicationContext(), R.string.toast_password_min_error);
					return;
				}
				if (mNewPassword.getText().length() < 6) {
					YSToast.showToast(getApplicationContext(), R.string.toast_password_min_error);
					return;
				}
				complete();
			}
		});

		mPassword = (EditText) this.findViewById(R.id.edt_password_old);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mPassword, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300);

		mPassword.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void afterTextChanged(Editable arg0) {
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
				if (mHasPassword && mHasNewPassword) {
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

		mNewPassword = (EditText) this.findViewById(R.id.edt_password_new);
		mNewPassword.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void afterTextChanged(Editable arg0) {
				if (null == temp) {
					return;
				}
				selectionStart = mNewPassword.getSelectionStart();
				selectionEnd = mNewPassword.getSelectionEnd();
				if (temp.length() >= 6) {
					mHasNewPassword = true;
					if (temp.length() > 15) {
						arg0.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						mNewPassword.setText(arg0);
						mNewPassword.setSelection(tempSelection);
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_password_max_error));
					}
				} else {

					mHasNewPassword = false;

				}
				if (mHasPassword && mHasNewPassword) {
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

	private void complete() {
		// 隐藏软键盘
		AndroidConfig.hiddenInput(this, mPassword);
		AndroidConfig.hiddenInput(this, mNewPassword);

		// 发送请求
		ModifyPwdObject pwdObject = new ModifyPwdObject();
        pwdObject.setOldPassword(mPassword.getEditableText().toString());
        pwdObject.setNewPassword(mNewPassword.getEditableText().toString());
        sendMessage(YSMSG.REQ_MODIFY_USER_PASSWORD, 0, 0, pwdObject);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_MODIFY_USER_PASSWORD: {
			dismissWaitingDialog();

			if (msg.arg1 == 200) {
				// 成功
				YSToast.showToast(this, R.string.toast_password_modify_succeed);
				finish();
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
}