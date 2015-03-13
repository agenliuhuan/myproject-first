package mobi.dlys.android.familysafer.ui.setting;

import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class NicknameActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	EditText mNickName = null;
	boolean mHasNickName = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nickname);

		initView();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_nickname_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setText(R.string.titlebar_button_tip_over);

		mTitleBar.mRight.setEnabled(false);
		mTitleBar.mRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				complete();
			}
		});

        mNickName = (EditText) this.findViewById(R.id.edt_nickname_new);
        if (null != getIntent()) {
            String nickname = getIntent().getStringExtra("nickname");
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
						mTitleBar.mRight.setEnabled(true);
					}
				} else {
					if (null != mTitleBar) {
						mTitleBar.mRight.setEnabled(false);
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