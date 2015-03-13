package mobi.dlys.android.familysafer.ui.checkin;

import java.util.ArrayList;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;

public class SendCheckActivity extends BaseExActivity {

    ImageView mSending = null;
    AnimationDrawable mSendingAni = null;
    ArrayList<Integer> mSelectedList = null;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendcheck);

        initView();
        initData();
    }

    void initView() {
        mSending = (ImageView) this.findViewById(R.id.img_sendcheck_tip);
        mSendingAni = (AnimationDrawable) mSending.getBackground();
        mSendingAni.setOneShot(false);
        if (mSendingAni.isRunning()) {
            mSendingAni.stop();
        }
        mSendingAni.start();
    }

    void initData() {
        msg = getIntent().getStringExtra("message");
        CoreModel.getInstance().setCheckinMessage(msg);
        mHandler.postDelayed(new Runnable() {
            public void run() {
                int actoin = getIntent().getIntExtra("all", 0);
                if (actoin == 1) {
                    sendEmptyMessage(YSMSG.REQ_CHECKIN_TO_ALL);
                } else {
                    mSelectedList = getIntent().getExtras().getIntegerArrayList("data");
                    sendMessage(YSMSG.REQ_CHECKIN_TO_SAME_ONE, 0, 0, mSelectedList);
                }
            }
        }, 2000);

    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_CHECKIN_TO_ALL: {
            CoreModel.getInstance().setCheckinMessage("");
            if (msg.arg1 == 200) {

                SentCheckActivity.startActivity(this);
                setResult(CheckinActivity.SendCheck_All_Action_Id_Result);
            } else {
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(this, result.getErrorMsg());
                } else {

                }
            }
            finish();
        }
            break;
        case YSMSG.RESP_CHECKIN_TO_SAME_ONE: {
            CoreModel.getInstance().setCheckinMessage("");
            if (msg.arg1 == 200) {
                // 成功
                SentCheckActivity.startActivity(this);
                setResult(CheckinActivity.SendCheck_Some_Action_Id_Result);
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
            finish();
            break;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
