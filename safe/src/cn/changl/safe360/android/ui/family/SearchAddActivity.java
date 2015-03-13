package cn.changl.safe360.android.ui.family;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.utils.TelephonyUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class SearchAddActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	EditText mPhoneNumber = null;
	Button mSearch = null;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, SearchAddActivity.class);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchadd);

		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_searchadd_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeInput();
				SearchAddActivity.this.finish();
			}
		});
		mPhoneNumber = (EditText) this.findViewById(R.id.edt_searchadd_phone);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mPhoneNumber, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 500);
		mPhoneNumber.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void afterTextChanged(Editable arg0) {
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

		mSearch = (Button) findViewById(R.id.btn_searchadd_search);
		mSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String phoneNum = mPhoneNumber.getEditableText().toString().trim();
				if (CoreModel.getInstance().getUserInfo() != null) {
					if (CoreModel.getInstance().getUserInfo().getPhone().equals(phoneNum)) {
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_ismyselef));
						return;
					}
				}
				if (!TelephonyUtils.isMobile(phoneNum)) {
					YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_format_error));
				} else {
					ArrayList<String> list = new ArrayList<String>();
					list.add(mPhoneNumber.getEditableText().toString());
					sendMessage(YSMSG.REQ_CHECK_USER_REGISTER, 0, 0, list);
					closeInput();
					showWaitingDialog();
				}
			}
		});
	}

	private void closeInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(SearchAddActivity.this.getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_CHECK_USER_REGISTER: {
			if (msg.arg1 == 200) {
				List<UserInfo> userInfoList = (List<UserInfo>) msg.obj;
				if (userInfoList != null) {
					boolean regist = userInfoList.get(0).getRegistStatus();
					if (regist) {
						sendMessage(YSMSG.REQ_INVITE_FRIEND, 0, userInfoList.get(0).getUserId(), null);
					} else {
						dismissWaitingDialog();
						YSToast.showToast(this, R.string.toast_usernot_register);
					}
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
		case YSMSG.RESP_INVITE_FRIEND: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				YSToast.showToast(this, R.string.toast_send_add_friend_success);
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