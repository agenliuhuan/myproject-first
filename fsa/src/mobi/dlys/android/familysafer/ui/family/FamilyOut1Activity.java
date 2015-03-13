package mobi.dlys.android.familysafer.ui.family;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.api.PPNetManager;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FamilyOut1Activity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	Button mInviteButton = null;
	TextView mNameTV = null;
	TextView mPhoneTV = null;
	String phone;
	String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_familyout);

		initView();
		initData();
	}

	private void initData() {
		if (null != getIntent().getExtras()) {
			name = getIntent().getExtras().getString("name");
			phone = getIntent().getExtras().getString("phone");
			mNameTV.setText(name);
			mPhoneTV.setText(phone);
		}
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_familyout_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FamilyOut1Activity.this.finish();
			}
		});

		mInviteButton = (Button) findViewById(R.id.btn_familyout_invite);
		mNameTV = (TextView) findViewById(R.id.tv_familyout_name);
		mPhoneTV = (TextView) findViewById(R.id.tv_familyout_phone);
		TextView tipTV = (TextView) findViewById(R.id.tv_familyout_tip);
		tipTV.setText(getString(R.string.activity_familyout_tv_tip_2));
		mInviteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 发送短信
				String smsContent = getResources().getString(R.string.sms_invite1);
				smsContent += PPNetManager.getInstance().getDownloadPage();
				smsContent += getResources().getString(R.string.sms_invite2);
				TelephonyUtils.sendSms(FamilyOut1Activity.this, phone, smsContent, false);

			}
		});
	}
}
