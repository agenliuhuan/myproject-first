package mobi.dlys.android.familysafer.ui.sos;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;

public class SendSOSActivity extends BaseExActivity {

	ImageView mSOSSend = null;
	AnimationDrawable mSOSAni = null;

	int mVoiceDuration = 0;
	String mVoiceFilePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendsos);

		initView();
		initData();
	}

	void initView() {
		mSOSSend = (ImageView) this.findViewById(R.id.img_sendsos_tip);

		mSOSAni = (AnimationDrawable) mSOSSend.getBackground();
		if (mSOSAni != null) {
			mSOSAni.setOneShot(false);
			if (mSOSAni.isRunning()) {
				mSOSAni.stop();
			}
			mSOSAni.start();
		}
	}

	private void initData() {
		mVoiceDuration = getIntent().getIntExtra(VoiceSOSActivity.EXTRA_VOICE_DURATION, 0);
		mVoiceFilePath = getIntent().getStringExtra(VoiceSOSActivity.EXTRA_VOICE_PATH);

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				postSOS();
			}
		}, 2000);
	}

	private void postSOS() {
		sendMessage(YSMSG.REQ_VOICE_SOS, mVoiceDuration, 0, mVoiceFilePath);
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_VOICE_SOS: {
			if (msg.arg1 == 200) {
				// 成功
				SentSOSActivity.startActivity(this);
				setResult(VoiceSOSActivity.SendSOS_Action_Result);
			} else {
				// 失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.toast_send_voice_sos_failled);
				}

			}
			finish();
		}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		return;
	}
}