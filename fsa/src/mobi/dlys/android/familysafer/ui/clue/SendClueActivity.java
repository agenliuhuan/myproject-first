package mobi.dlys.android.familysafer.ui.clue;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.YSToast;

public class SendClueActivity extends BaseExActivity {
    public static final String EXTRA_CLUE_OBJECT = "extra_clue_object";

    ImageView mSending = null;
    AnimationDrawable mSendingAni = null;
    ClueObject mClueObject;

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
        TextView tv=(TextView)findViewById(R.id.tv_sendcheck_tip);
        tv.setText(getString(R.string.activity_sendcluetv_tip));
    }

    void initData() {

        mHandler.postDelayed(new Runnable() {
            public void run() {
                mClueObject = (ClueObject) getIntent().getSerializableExtra(EXTRA_CLUE_OBJECT);
                if (null == mClueObject) {
                    YSToast.showToast(SendClueActivity.this, R.string.network_error);
                    SendClueActivity.this.finish();
                }
                sendMessage(YSMSG.REQ_PUSH_CLUE, 0, 0, mClueObject);
            }
        }, 2000);

    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_PUSH_CLUE: {
            dismissWaitingDialog();
            if (msg.arg1 == 200) {
                CoreModel.getInstance().setUpdateClueList(true);
                CoreModel.getInstance().setmUpdateMyClueList(true);
                Intent intent = new Intent(SendClueActivity.this, SendClueOKActivity.class);
                intent.putExtra(SentClueActivity.EXTRA_CLUE_OBJECT, mClueObject);
                startActivity(intent);
                finish();
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
