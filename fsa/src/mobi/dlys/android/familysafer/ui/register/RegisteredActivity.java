package mobi.dlys.android.familysafer.ui.register;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.family.AddFamily1Activity;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

public class RegisteredActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;

	Button mAddButton = null;
	CheckBox mMatch = null;
	boolean mCanAdd = true;

	ImageView mUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registered);

		initView();
		initData();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_registered_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setText(R.string.titlebar_button_tip_skip);
		mTitleBar.mRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				App.getInstance().clearActivity();
				Intent intent = new Intent(RegisteredActivity.this, MainActivity.class);
				startActivity(intent);
				RegisteredActivity.this.finish();

			}
		});

		mMatch = (CheckBox) findViewById(R.id.ckb_registered_contact);
		mMatch.setChecked(true);
		mMatch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mMatch) {
					if (null != mAddButton) {
						if (mMatch.isChecked()) {
							mAddButton.setBackgroundResource(R.drawable.button_green_selector);
							mAddButton.setEnabled(true);
						} else {
							mAddButton.setBackgroundResource(R.drawable.btn_disable);
							mAddButton.setEnabled(false);
						}
					}
				}

			}
		});

		mAddButton = (Button) findViewById(R.id.btn_registered_add);
		mAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				App.getInstance().addActivity(RegisteredActivity.this);
				Intent intent = new Intent(RegisteredActivity.this, AddFamily1Activity.class);
				startActivity(intent);
			}

		});

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mUser = (ImageView) findViewById(R.id.img_registered_image);

	}

	private void initData() {
		UserObject uo = CoreModel.getInstance().getUserInfo();
		if (null != mUser && null != uo) {
			ImageLoaderHelper.displayAvatar(uo.getImage(), mUser);
		}

	}

}