package mobi.dlys.android.familysafer.ui.setting;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.RegisterObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class Phone1Activity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	EditText mPhoneNumber = null;
	EditText mPassword = null;

	TextView mCurrentPhoneNumber;

	boolean mHasPhoneNumber = false;
	boolean mHasPassword = false;

	String mTip;
	String phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_1);

		initView();
		initData();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_phone_1_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);
		mTitleBar.mRight2.setText(R.string.titlebar_button_tip_next);
		mTitleBar.mRight2.setTextColor(R.color.setting_item_text_gray_color);
		mTitleBar.mRight2.setClickable(false);
		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mPassword.getText().length() < 6) {
					YSToast.showToast(getApplicationContext(), R.string.toast_password_min_error);
					return;
				}
				nextStep();
			}

		});

		mCurrentPhoneNumber = (TextView) findViewById(R.id.tv_phone_1_tip);

		mPhoneNumber = (EditText) this.findViewById(R.id.edt_phone_1_number);
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
				mHasPhoneNumber = false;
				if (temp.length() > 0) {
					if (temp.charAt(0) != '1') {
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_format_error));
						arg0.clear();
						mPhoneNumber.setText(arg0);
						mHasPhoneNumber = false;
					}
					if (temp.length() > 11) {
						arg0.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						mPhoneNumber.setText(arg0);
						mPhoneNumber.setSelection(tempSelection);
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_length_out));
					}
					if (temp.length() == 11) {
						if (temp.toString().equals(phoneNumber)) {
							mHasPhoneNumber = false;
							YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_notnew_error));
						} else {
							mHasPhoneNumber = true;
						}
					} else {
						mHasPhoneNumber = false;
					}
				} else {
					mHasPhoneNumber = false;
				}
				if (mHasPhoneNumber && mHasPassword) {
					if (null != mTitleBar) {
						mTitleBar.mRight2.setClickable(true);
						mTitleBar.mRight2.setTextColor(0xff44b651);
					}
				} else {
					if (null != mTitleBar) {
						mTitleBar.mRight2.setClickable(false);
						mTitleBar.mRight2.setTextColor(R.color.setting_item_text_gray_color);
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

		mPassword = (EditText) this.findViewById(R.id.edt_phone_1_password);
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
					if (temp.length() > 15) {
						arg0.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						mPassword.setText(arg0);
						mPassword.setSelection(tempSelection);
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_password_max_error));
					} else {
						mHasPassword = true;
					}
				} else {

					mHasPassword = false;
				}
				if (mHasPhoneNumber && mHasPassword) {
					if (null != mTitleBar) {
						mTitleBar.mRight2.setClickable(true);
						mTitleBar.mRight2.setTextColor(0xff44b651);
					}
				} else {
					if (null != mTitleBar) {
						mTitleBar.mRight2.setClickable(false);
						mTitleBar.mRight2.setTextColor(R.color.setting_item_text_gray_color);
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

	private void initData() {
		UserObject userInfo = CoreModel.getInstance().getUserInfo();
		if (userInfo != null) {
			phoneNumber = userInfo.getPhone();

			mTip = "当前手机号为<font color='#44b651'>" + phoneNumber + "</font>，如果此手机号不再使用，请及时更改手机号。更改成功后，可以使用新手机号登陆。";
			mCurrentPhoneNumber.setText(Html.fromHtml(mTip));
		}

	}

	private void nextStep() {
		// 隐藏软键盘
		AndroidConfig.hiddenInput(this, mPhoneNumber);
		AndroidConfig.hiddenInput(this, mPassword);

		// 保存手机号码和密码
		RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
		if (regObject == null) {
			regObject = new RegisterObject();
		}

		regObject.setPhone(mPhoneNumber.getEditableText().toString());
		regObject.setPassword(mPassword.getEditableText().toString());
		CoreModel.getInstance().setRegisterObject(regObject);

		// 发送验证密码请求
		sendEmptyMessage(YSMSG.REQ_VERIFY_PWD_FOR_MODIFY_PHONE);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_GET_MODIFY_PHONE_AUTH_CODE: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 获取验证码成功
				Phone2Activity.startActivityForResult(this);
			} else {
				// 获取验证码失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_VERIFY_PWD_FOR_MODIFY_PHONE: {
			if (msg.arg1 == 200) {
				// 发送验证号码是否已经注册过
				ArrayList<String> list = new ArrayList<String>();
				list.add(mPhoneNumber.getEditableText().toString());
				sendMessage(YSMSG.REQ_CHECK_USER_REGISTER, 0, 0, list);
			} else {
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
		case YSMSG.RESP_CHECK_USER_REGISTER: {
			if (msg.arg1 == 200) {
				boolean isRegist = true;
				if (msg.obj instanceof FamilySaferPb) {
					FamilySaferPb userinfo = (FamilySaferPb) msg.obj;
					UserInfo userinfo1 = userinfo.getUserInfos(0);
					if (null != userinfo1) {
						isRegist = userinfo1.getRegistStatus() && mPhoneNumber.getEditableText().toString().equals(userinfo1.getPhone());
					}
				}

				if (isRegist) {
					// 用户已经注册
					YSToast.showToast(this, R.string.toast_phone_registered_error);
					dismissWaitingDialog();
				} else {
					// 发送获取验证码请求
					sendEmptyMessage(YSMSG.REQ_GET_MODIFY_PHONE_AUTH_CODE);
				}
			} else {
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
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Phone2Activity.Modify_Phone_Action_Id) {
			if (resultCode == Phone2Activity.Modify_Phone_Action_Id_Result) {
				finish();
			}
		}
	}
}