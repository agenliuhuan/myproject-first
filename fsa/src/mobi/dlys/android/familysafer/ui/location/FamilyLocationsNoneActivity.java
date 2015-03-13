package mobi.dlys.android.familysafer.ui.location;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.communication.CommunicationActivity;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class FamilyLocationsNoneActivity extends BaseExActivity implements OnClickListener {

	protected TitleBarHolder mTitleBar;

	private String mPhone = null;
	private int mUserId;
	String name;
	String image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations_none);

		initView();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_familylocate_none_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		TextView tip = (TextView) this.findViewById(R.id.tv_locations_none_tip_2);
		View button = this.findViewById(R.id.btn_common_call);
		button.setOnClickListener((OnClickListener) this);
		View buttonVoice = this.findViewById(R.id.btn_common_entercom);
		buttonVoice.setOnClickListener((OnClickListener) this);

		if (getIntent() != null) {
			mUserId = getIntent().getIntExtra("userid", 0);
			name = getIntent().getStringExtra("name");
			image = getIntent().getStringExtra("image");
			tip.append(name);
			tip.append(getResources().getString(R.string.activity_familylocations_none_desc_2));

			mPhone = getIntent().getStringExtra("phone");
		}

		if (null == mPhone) {
			button.setEnabled(false);
		} else {
			if (TextUtils.isEmpty(mPhone)) {
				button.setEnabled(false);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		if (R.id.btn_common_call == arg0.getId()) {
			TelephonyUtils.call(FamilyLocationsNoneActivity.this, mPhone);
		} else if (R.id.btn_common_entercom == arg0.getId()) {
			if (mUserId != 0) {
				Intent intent = new Intent();
				intent.setClass(FamilyLocationsNoneActivity.this, CommunicationActivity.class);
				intent.putExtra("userid", mUserId);
				intent.putExtra("nickname", name);
				intent.putExtra("avatar", image);
				startActivity(intent);
			}
		}
	}
}
