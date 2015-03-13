package cn.changl.safe360.android.ui.smslocation;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.utils.ExtraName;

public class SMSLocationActivity extends BaseExActivity implements OnClickListener {
	private static final int RC_GET_PHONE = 100;

	protected TitleBarHolder mTitleBar;

	private EditText mPhoneEdit;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, SMSLocationActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_location);

		initView();
		initData();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_smslocation_titile);

		((Button) findViewById(R.id.btn_smslocation_contacts)).setOnClickListener(this);

		mPhoneEdit = (EditText) findViewById(R.id.edt_smslocation_phone);
	}

	private void initData() {

	}

	@Override
	public void onClick(View paramView) {
		switch (paramView.getId()) {
		case R.id.btn_smslocation_contacts: {
			ContactsListActivity.startActivityForResult(mActivity, RC_GET_PHONE);
		}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RC_GET_PHONE && resultCode == RESULT_OK && data != null) {
			String phone = data.getStringExtra(ExtraName.EN_PHONE);
			if (!TextUtils.isEmpty(phone)) {
				mPhoneEdit.setText(phone);
			}
		}
	}
}
