package mobi.dlys.android.familysafer.ui.sos;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.family.AddFamily2Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class VoiceSOSNoneActivity extends BaseExActivity implements OnClickListener {

	protected TitleBarHolder mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voicesos_none);

		initView();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_voicesos_none_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				VoiceSOSNoneActivity.this.finish();

			}
		});

		View button = this.findViewById(R.id.btn_voicesos_none_add);
		button.setOnClickListener((OnClickListener) this);
	}

	@Override
	public void onClick(View arg0) {
		if (R.id.btn_voicesos_none_add == arg0.getId()) {
			Intent intent = new Intent(VoiceSOSNoneActivity.this, AddFamily2Activity.class);
			startActivity(intent);
			VoiceSOSNoneActivity.this.finish();
		}
	}
}
