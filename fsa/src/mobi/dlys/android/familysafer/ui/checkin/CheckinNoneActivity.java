package mobi.dlys.android.familysafer.ui.checkin;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.family.AddFamily2Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class CheckinNoneActivity extends BaseExActivity implements OnClickListener {

	protected TitleBarHolder mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkin_none);

		initView();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_checkin_none_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		View button = this.findViewById(R.id.btn_checkin_none_add);
		button.setOnClickListener((OnClickListener) this);
	}

	@Override
	public void onClick(View arg0) {
		if (R.id.btn_checkin_none_add == arg0.getId()) {
			Intent intent = new Intent(CheckinNoneActivity.this, AddFamily2Activity.class);
			startActivity(intent);
			CheckinNoneActivity.this.finish();
		}
	}
}
