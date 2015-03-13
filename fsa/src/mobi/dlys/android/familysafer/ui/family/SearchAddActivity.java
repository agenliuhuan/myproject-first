package mobi.dlys.android.familysafer.ui.family;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ContactsObject;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.ContactDataBase;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class SearchAddActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	EditText mPhoneNumber = null;
	Button mSearch = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchadd);

		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_searchadd_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
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
				if (!TelephonyUtils.isMobile(mPhoneNumber.getEditableText().toString().trim())) {
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
				dismissWaitingDialog();
				// 已注册
				if (msg.obj instanceof FamilySaferPb) {
					FamilySaferPb userinfo = (FamilySaferPb) msg.obj;
					UserInfo userinfo1 = userinfo.getUserInfos(0);
					if (null != userinfo1) {
						int userid = userinfo1.getUserId();
						boolean isRegist = userinfo1.getRegistStatus();
						if (isRegist) {
							// 判断是不是好友
							List<FriendObject> list = CoreModel.getInstance().getFriendList();
							if (checkIsFriend(mPhoneNumber.getText().toString(), list)) {
								Intent intent = new Intent(SearchAddActivity.this, FamilyDetailActivity.class);
								intent.putExtra("extra_user_id", userid);
								startActivity(intent);
							} else {
								Intent intent = new Intent();
								intent.setClass(SearchAddActivity.this, FamilyIn2Activity.class);
								intent.putExtra("extra_user_id", userid);
								startActivity(intent);
							}
						} else {
							// 用户未注册
							ContactDataBase cb = new ContactDataBase(SearchAddActivity.this);
							ContactsObject co = null;
							co = cb.query(mPhoneNumber.getText().toString());
							if (co != null) {
								Intent intent = new Intent();
								intent.setClass(SearchAddActivity.this, FamilyOut2Activity.class);
								intent.putExtra("name", co.getName());
								intent.putExtra("phone", co.getPhone());
								startActivity(intent);
							} else {
								Intent intent = new Intent();
								intent.setClass(SearchAddActivity.this, FamilyOut2Activity.class);
								intent.putExtra("phone", mPhoneNumber.getText().toString());
								startActivity(intent);
							}

						}
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
		}
	}

	private boolean checkIsFriend(String phone, List<FriendObject> list) {
		for (FriendObject fo : list) {
			if (fo.getPhone().equals(phone)) {
				return true;
			}
		}
		return false;
	}
}