package cn.changl.safe360.android.ui.login;

import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.AndroidConfig;
import android.content.Context;
import android.content.Intent;
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
import cn.changl.safe360.android.biz.vo.RegisterObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;

public class Fogot3Activity extends BaseExActivity {
    protected TitleBarHolder mTitleBar;

    EditText mNewPassword = null;
    boolean mHasPassword = false;

    public static void startActivity(Context context) {
        ActivityUtils.startActivity(context, Fogot3Activity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogot_3);

        initView();
    }

    private void initView() {
        mTitleBar = new TitleBarHolder(this);
        mTitleBar.mTitle.setText(R.string.activity_fogot_3_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
        mTitleBar.mRight.setText(R.string.titlebar_button_tip_over);
        mTitleBar.mRight.setEnabled(false);
        mTitleBar.mRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mNewPassword.getText().length() < 6) {
                    YSToast.showToast(getApplicationContext(), R.string.toast_password_min_error);
                    return;
                }
                submit();
            }
        });

        mNewPassword = (EditText) this.findViewById(R.id.edt_fogot_3_newpassword);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mNewPassword, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 300);

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
                    mHasPassword = true;
                    if (temp.length() > 15) {
                        arg0.delete(selectionStart - 1, selectionEnd);
                        int tempSelection = selectionEnd;
                        mNewPassword.setText(arg0);
                        mNewPassword.setSelection(tempSelection);
                        YSToast.showToast(getApplicationContext(), getString(R.string.toast_password_max_error));
                    }
                } else {

                    mHasPassword = false;
                }
                if (mHasPassword) {
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

    private void submit() {
        // 隐藏软键盘
        AndroidConfig.hiddenInput(this, mNewPassword);

        // 发送修改密码请求
        if (CoreModel.getInstance().getRegisterObject() != null) {
            RegisterObject registerObj = CoreModel.getInstance().getRegisterObject();
            registerObj.setPassword(mNewPassword.getEditableText().toString());

        }
        sendEmptyMessage(YSMSG.REQ_SET_NEW_PASSWORD);
        showWaitingDialog();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_SET_NEW_PASSWORD: {
            dismissWaitingDialog();
            if (msg.arg1 == 200) {
                YSToast.showToast(this, R.string.toast_password_modify_succeed);
                // 修改密码成功
                Intent intent = new Intent(Fogot3Activity.this, LoginActivity.class);
                startActivity(intent);
                Fogot3Activity.this.finish();
            } else {
                // 修改密码失败
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
