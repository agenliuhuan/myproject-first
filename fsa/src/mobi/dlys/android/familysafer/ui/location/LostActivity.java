package mobi.dlys.android.familysafer.ui.location;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LostActivity extends BaseExActivity implements OnClickListener {

	protected TitleBarHolder mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost);

		initView();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_lost_titlebar);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		Button lostButton = (Button) findViewById(R.id.btn_lost_call);
		lostButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (R.id.btn_lost_call == arg0.getId()) {
			UserObject uo = CoreModel.getInstance().getUserInfo();
			if (uo != null) {
				String location = uo.getLocation();
				if (null != location) {
					if (!TextUtils.isEmpty(location)) {
						setResult(getIntent().getIntExtra("rid", 0));
						finish();
					}
				}
			}
		}
	}
}
