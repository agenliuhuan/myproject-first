package cn.changl.safe360.android.ui.setting;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.AndroidConfig;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;

public class NicknameActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	EditText mNickName = null;
	boolean mHasNickName = false;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, NicknameActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nickname);

		initView();
		initData();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_nickname_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mRight2.setText(R.string.titlebar_button_tip_over);

		mTitleBar.mRight2.setEnabled(false);
		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String reg = "^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";
				Pattern p1 = Pattern.compile(reg);
				Matcher m1 = p1.matcher(mNickName.getEditableText().toString());
				if (!m1.find()) {
					YSToast.showToast(NicknameActivity.this, "昵称中含有表情，请修改");
					return;
				}
				String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(mNickName.getEditableText().toString());
				if (m.find()) {
					YSToast.showToast(NicknameActivity.this, R.string.toast_nickname_format_error);
				} else {
					complete();
				}

			}
		});

		mNickName = (EditText) this.findViewById(R.id.edt_nickname_new);
		if (null != getIntent()) {
			String nickname = getIntent().getStringExtra("nickname");
			if (!TextUtils.isEmpty(nickname)) {
				if (nickname.length() > 10) {
					nickname = nickname.substring(0, 9);
				}
				mNickName.setText(nickname);
				CharSequence text = mNickName.getText();
				if (text instanceof Spannable) {
					Spannable spanText = (Spannable) text;
					Selection.setSelection(spanText, text.length());
				}
			}
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mNickName, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300);

		mNickName.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void afterTextChanged(Editable arg0) {
				if (null == temp) {
					return;
				}
				selectionStart = mNickName.getSelectionStart();
				selectionEnd = mNickName.getSelectionEnd();
				if (temp.length() > 0) {
					mHasNickName = true;

					if (temp.length() > 10) {
						arg0.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						mNickName.setText(arg0);
						mNickName.setSelection(tempSelection);
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_nickname_max_error));
					}
				} else {
					mHasNickName = false;
				}
				if (mHasNickName) {
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

	private void initData() {
		UserObject user = CoreModel.getInstance().getUserInfo().clone();
		if (user != null && !TextUtils.isEmpty(user.getNickname())) {
			mNickName.setText(user.getNickname());
			mNickName.setSelection(user.getNickname().length());
		}
	}

	private void complete() {
		// 隐藏软键盘
		AndroidConfig.hiddenInput(this, mNickName);

		// 创建临时UserObject
		UserObject user = CoreModel.getInstance().getUserInfo().clone();
		if (user != null) {
			user.setNickname(mNickName.getEditableText().toString());
		}

		// 发送请求
		sendMessage(YSMSG.REQ_MODIFY_USER_INFO, 0, 0, user);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_MODIFY_USER_INFO: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 成功
				YSToast.showToast(this, R.string.toast_nickname_modify_succeed);
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