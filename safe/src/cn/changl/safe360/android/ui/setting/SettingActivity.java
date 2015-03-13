package cn.changl.safe360.android.ui.setting;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.AndroidConfig;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSAlertDialog;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.utils.PreferencesUtils;

public class SettingActivity extends BaseExActivity implements OnClickListener {
	protected TitleBarHolder mTitleBar;

	// private RelativeLayout mHideLayout = null;
	private CheckBox mHide = null;
	private RelativeLayout mGuide = null;
	private RelativeLayout mCompro = null;
	private RelativeLayout mUpdate = null;

	private Button exitFamilyBtn = null;

	private TextView tvVer = null;
	private ImageView imgVer = null;

	private String strVersion = null;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, SettingActivity.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initSubView();
		initData();
	}

	private void initSubView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.fragment_setting_ttb_title);

		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		exitFamilyBtn = (Button) findViewById(R.id.layout_setting_exitfamilyBtn);
		exitFamilyBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				showExitGroupDialog();
			}
		});

		mGuide = (RelativeLayout) findViewById(R.id.layout_setting_guide);
		mGuide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SettingActivity.this, NewbieGuideActivity.class);
				startActivity(intent);

			}
		});
		mCompro = (RelativeLayout) findViewById(R.id.layout_setting_compro);
		mCompro.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				HelperActivity.startActivity(SettingActivity.this);
			}
		});

		mUpdate = (RelativeLayout) findViewById(R.id.layout_setting_update);
		mUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				checkVersion();
			}
		});
		TextView copyright = (TextView) findViewById(R.id.layout_setting_copyrights);
		copyright.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CopyRightActivity.startActivity(SettingActivity.this);
			}
		});

		mHide = (CheckBox) findViewById(R.id.checkbox_setting_hide);
		mHide.setOnClickListener(this);
		tvVer = (TextView) findViewById(R.id.tv_version);
		tvVer.setText("");

		imgVer = (ImageView) findViewById(R.id.img_version);
		imgVer.setVisibility(View.GONE);
	}

	private void initData() {
		UserObject user = CoreModel.getInstance().getUserInfo();
		if (user != null) {
			mHide.setChecked(user.getHideLocation());
		}

		strVersion = AndroidConfig.getVersionName();
		if (!TextUtils.isEmpty(strVersion)) {
			tvVer.setText("v" + strVersion);
		}
		if (CoreModel.getInstance().getFriendList().isEmpty()) {
			exitFamilyBtn.setVisibility(View.GONE);
		} else {
			exitFamilyBtn.setVisibility(View.VISIBLE);
		}
	}

	public void displayNewVersionTip() {
		if (PreferencesUtils.hasNewVersion()) {
			imgVer.setVisibility(View.VISIBLE);
		} else {
			imgVer.setVisibility(View.GONE);
		}
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.checkbox_setting_hide: {
			// mHandler.postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// AnalyticsHelper.onEvent(SettingActivity.this,
			// AnalyticsHelper.index_hide_myself);
			// }
			// }, 1000);

			hideLocation(mHide.isChecked());
		}
			break;
		}
	}

	private void hideLocation(boolean hide) {
		// 发送隐藏地理位置请求
		sendMessage(YSMSG.REQ_HIDE_LOCATION, 0, 0, hide);
		showWaitingDialog();
	}

	private void checkVersion() {
		// 发送检测版本请求
		CoreModel.getInstance().setVersionChecked(true);
		sendEmptyMessage(YSMSG.REQ_CHECK_VERSION);
		showOrUpdateWaitingDialog(R.string.fragment_setting_dlg_check);
	}

	private void exitgroup() {
		// 退出圈子
		sendEmptyMessage(YSMSG.REQ_EXIT_GROUP);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_HIDE_LOCATION: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 成功
				if (mHide.isChecked()) {
					YSToast.showToast(getBaseContext(), R.string.toast_user_hidelocation);
				} else {
					YSToast.showToast(getBaseContext(), R.string.toast_user_nothidelocation);
				}

			} else {
				// 失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(getBaseContext(), result.getErrorMsg());
				} else {
					YSToast.showToast(getBaseContext(), R.string.network_error);
				}
				mHide.setChecked(!mHide.isChecked());
			}
		}
			break;
		case YSMSG.RESP_EXIT_GROUP: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 注销成功
				// GuideActivity.startActivity(SettingActivity.this);
				sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
				exitFamilyBtn.setVisibility(View.GONE);
			} else {
				// 注销失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(getBaseContext(), result.getErrorMsg());
				} else {
					YSToast.showToast(getBaseContext(), R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_CHECK_VERSION: {
			dismissWaitingDialog();
		}
			break;
		}
	}

	private void showNewVersionDialog() {
		View view = SettingActivity.this.getLayoutInflater().inflate(R.layout.dialog_two_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(SettingActivity.this, view, false, true);
			final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			txtTitle.setText(getString(R.string.update_title));
			final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			txtContent.setText(getString(R.string.dialog_logout_content));

			final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
			btnConfirm.setText(getString(R.string.update_yes));
			btnCancel.setText(getString(R.string.update_no));
			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
					// logout();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
				}
			});

			dialog.show();
		}
	}

	private void showExitGroupDialog() {
		View view = SettingActivity.this.getLayoutInflater().inflate(R.layout.dialog_two_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(SettingActivity.this, view, false, true);
			final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			txtTitle.setText(getString(R.string.dialog_title_tip));
			final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			txtContent.setText(getString(R.string.dialog_exitgroup_content));

			final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
			btnConfirm.setText(getString(R.string.dialog_exitgroup_yes));
			btnCancel.setText(getString(R.string.dialog_exitgroup_no));
			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
					exitgroup();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
				}
			});

			dialog.show();
		}
	}

}