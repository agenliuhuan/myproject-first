package mobi.dlys.android.familysafer.ui.family;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.communication.CommunicationActivity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FamilyDetailActivity extends BaseExActivity implements OnClickListener {
    private static final String EXTRA_USER_ID = "extra_user_id";
    protected TitleBarHolder mTitleBar;

    Button mCallButton = null;
    Button mEnterButton = null;
    Button mRemoveButton = null;
    ImageView mDeatilImage = null;
    TextView mDetailName = null;
    TextView mDetailPhone = null;
    LinearLayout mRemarkLL = null;
    TextView mNickname = null;
    CheckBox mAllow = null;

    UserObject mUser = null;
    int mUserId;

    public static void startActivity(Context context, int userId) {
        Intent intent = new Intent(context, FamilyDetailActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familydetail);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    void initView() {
        mTitleBar = new TitleBarHolder(this);
        mTitleBar.mTitle.setText(R.string.activity_familydetail_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mRight.setVisibility(View.INVISIBLE);

        mDeatilImage = (ImageView) findViewById(R.id.img_familydetail_image);
        mDetailName = (TextView) findViewById(R.id.tv_familydetail_name);
        mDetailPhone = (TextView) findViewById(R.id.tv_familydetail_phone);

        mNickname = (TextView) findViewById(R.id.tv_familydetail_nickname);
        mRemarkLL = (LinearLayout) findViewById(R.id.familydetail_remark_LL);
        mRemarkLL.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (mUser != null) {
                    Intent intent = new Intent();
                    intent.setClass(FamilyDetailActivity.this, FamilyRemarkActivity.class);
                    int userid = mUser.getUserId();
                    String remarkName = mUser.getRemarkName();
                    intent.putExtra("remarkname", remarkName);
                    intent.putExtra("userid", userid);
                    startActivity(intent);
                }
            }
        });

        mCallButton = (Button) findViewById(R.id.btn_common_call);
        mRemoveButton = (Button) findViewById(R.id.btn_familydetail_delete);
        mEnterButton = (Button) findViewById(R.id.btn_common_entercom);

        mCallButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(FamilyDetailActivity.this, AnalyticsHelper.index_call_family);
                    }
                }, 1000);
                String pnum = mDetailPhone.getText().toString();
                TelephonyUtils.call(FamilyDetailActivity.this, pnum);
            }
        });

        mRemoveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                showDelDialog(getString(R.string.activity_familydetail_tv_tip));
            }
        });

        mEnterButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(FamilyDetailActivity.this, AnalyticsHelper.index_send_voice);
                    }
                }, 1000);
                if (mUser != null) {
                    Intent intent = new Intent();
                    intent.setClass(FamilyDetailActivity.this, CommunicationActivity.class);
                    int userid = mUser.getUserId();
                    intent.putExtra("userid", userid);
                    intent.putExtra("nickname", mUser.getNickname());
                    intent.putExtra("avatar", mUser.getImage());
                    startActivity(intent);
                }
            }
        });

        mAllow = (CheckBox) findViewById(R.id.cb_familydetail_allow);
        mAllow.setOnClickListener(this);
        mUserId = getIntent().getIntExtra(EXTRA_USER_ID, 0);
    }

    private void showDelDialog(String text) {
        View view = getLayoutInflater().inflate(R.layout.dialog_two_button, null);
        if (view != null && !TextUtils.isEmpty(text)) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(FamilyDetailActivity.this, view, false, true);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.activity_familydetail_btn_delete));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            txtContent.setText(text);

            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);

            btnConfirm.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    dialog.cancel();
                    sendMessage(YSMSG.REQ_DEL_FRIEND, mUserId, 0, null);
                    showWaitingDialog();
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }

    }

    private void initData() {
        if (mUserId != 0) {
            sendMessage(YSMSG.REQ_GET_USER_INFO, mUserId, 0, null);
            showWaitingDialog();
        }
    }

    private void update() {
        if (mUser != null) {
            if (!TextUtils.isEmpty(mUser.getImage())) {
                ImageLoaderHelper.displayImage(mUser.getImage(), mDeatilImage, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
            } else {
                mDeatilImage.setImageResource(R.drawable.user);
            }

            mDetailName.setText(mUser.getNickname());
            mDetailPhone.setText(mUser.getPhone());
            mNickname.setText(mUser.getRemarkName());
            mAllow.setChecked(mUser.getShowMyPosition());
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_USER_INFO:
            dismissWaitingDialog();
            if (msg.arg1 == 200) {
                // success
                if (msg.obj instanceof UserObject) {
                    mUser = (UserObject) msg.obj;
                }
                update();
            } else {
                // failed
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(this, result.getErrorMsg());
                } else {
                    YSToast.showToast(this, R.string.network_error);
                }
            }
            break;
        case YSMSG.RESP_DEL_FRIEND:
            dismissWaitingDialog();
            if (msg.arg1 == 200) {
                FamilyDetailActivity.this.finish();
                YSToast.showToast(this, R.string.toast_del_friend_success);
            } else {
                // failed
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(this, result.getErrorMsg());
                } else {
                    YSToast.showToast(this, R.string.network_error);
                }
            }
            break;
        case YSMSG.RESP_SHOW_MY_LOCATION: {
            dismissWaitingDialog();
            if (msg.arg1 == 200) {
                // success
            } else {
                // failed
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(this, result.getErrorMsg());
                } else {
                    YSToast.showToast(this, R.string.network_error);
                }
                // 还原CheckBox状态
                mAllow.setChecked(!mAllow.isChecked());
            }

        }
            break;
        }
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.cb_familydetail_allow: {
            showMyLocation(mAllow.isChecked());
        }
            break;
        }
    }

    private void showMyLocation(boolean show) {
        sendMessage(YSMSG.REQ_SHOW_MY_LOCATION, mUserId, 0, show);
        showWaitingDialog();
    }

}