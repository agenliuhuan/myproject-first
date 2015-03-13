package cn.changl.safe360.android.ui.main;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.DialogHelper;
import cn.changl.safe360.android.ui.comm.YSAlertDialog;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.ui.family.AddFamilyActivity;
import cn.changl.safe360.android.ui.login.GuideActivity;
import cn.changl.safe360.android.ui.message.MessageActivity;
import cn.changl.safe360.android.ui.setting.AccountActivity;
import cn.changl.safe360.android.ui.setting.SettingActivity;
import cn.changl.safe360.android.ui.sos.SOSActivity;
import cn.changl.safe360.android.utils.ImageLoaderHelper;
import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.android.utils.umeng.FeedbackHelper;

public class LeftMenuActivity extends BaseExActivity implements OnClickListener {
	ImageView userimage;
	TextView username;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_left_menu);
		initSubView();
		initData();
		showMenuGuide();
	}

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, LeftMenuActivity.class);
	}

	private void showMenuGuide() {
		final RelativeLayout menuGuide = (RelativeLayout) findViewById(R.id.layout_mainmenu_guide);
		Button menuGuideBtn = (Button) findViewById(R.id.layout_mainmenu_guide_knowBtn);
		boolean show = PreferencesUtils.getShowGuideValue("menuGuide");
		if (show) {
			menuGuide.setVisibility(View.VISIBLE);
		}
		menuGuideBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				menuGuide.setVisibility(View.GONE);
				PreferencesUtils.setShowGuideValue("menuGuide", false);
			}
		});
		menuGuide.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});
	}

	private void initData() {
		if (!CoreModel.getInstance().isLogined()) {
			((RelativeLayout) findViewById(R.id.main_menu_exit)).setVisibility(View.GONE);
		}
	}

	private void updateUserNameorImg() {
		UserObject user = CoreModel.getInstance().getUserInfo();
		if (null != user) {
			username.setText(user.getNickname());
			if (!TextUtils.isEmpty(user.getUploadImage())) {
				ImageLoaderHelper.displayAvatar(user.getBigDisplayImage(), userimage);
			}
		}
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_LOGOUT: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 注销成功
				CoreModel.getInstance().clearFriendList();
				App.getInstance().removeActivity(mActivity);
				App.getInstance().clearActivity();
				GuideActivity.startActivity(LeftMenuActivity.this);
				finish();
			} else {
				// 注销失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(LeftMenuActivity.this, result.getErrorMsg());
				} else {
					YSToast.showToast(LeftMenuActivity.this, R.string.network_error);
				}
			}
		}
			break;
		}
	}

	public void onResume() {
		super.onResume();
		updateUserNameorImg();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		LeftMenuActivity.this.finish();
		LeftMenuActivity.this.overridePendingTransition(R.anim.translate_between_interface_right_in, R.anim.translate_between_interface_left_out);
	}

	private void initSubView() {
		RelativeLayout userRL = (RelativeLayout) findViewById(R.id.main_menu_user);
		userimage = (ImageView) findViewById(R.id.main_menu_user_img);
		username = (TextView) findViewById(R.id.main_menu_user_name);
		RelativeLayout messageRL = (RelativeLayout) findViewById(R.id.main_menu_message);
		RelativeLayout shakeRL = (RelativeLayout) findViewById(R.id.main_menu_shake);
		RelativeLayout settingRL = (RelativeLayout) findViewById(R.id.main_menu_setting);
		RelativeLayout exitRL = (RelativeLayout) findViewById(R.id.main_menu_exit);
		RelativeLayout feedbackRL = (RelativeLayout) findViewById(R.id.main_menu_feedback);
		ImageView close = (ImageView) findViewById(R.id.left_menu_close);
		((LinearLayout) findViewById(R.id.layout_menu_close)).setOnClickListener(this);
		userRL.setOnClickListener(this);
		exitRL.setOnClickListener(this);
		messageRL.setOnClickListener(this);
		shakeRL.setOnClickListener(this);
		settingRL.setOnClickListener(this);
		feedbackRL.setOnClickListener(this);
		close.setOnClickListener(this);
		setUserInfo();
	}

	private void setUserInfo() {
		UserObject user = CoreModel.getInstance().getUserInfo();
		if (null != user) {
			username.setText(user.getNickname());
			if (!TextUtils.isEmpty(user.getUploadImage())) {
				ImageLoaderHelper.displayAvatar(user.getBigDisplayImage(), userimage);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_menu_user:
			if (CoreModel.getInstance().checkUserLogined(LeftMenuActivity.this)) {
				AccountActivity.startActivity(LeftMenuActivity.this);
			}
			break;
		case R.id.main_menu_message:
			if (CoreModel.getInstance().checkUserLogined(LeftMenuActivity.this)) {
				MessageActivity.startActivity(LeftMenuActivity.this);
			}
			break;
		case R.id.main_menu_shake:
			if (CoreModel.getInstance().checkUserLogined(LeftMenuActivity.this)) {
				if (CoreModel.getInstance().getFriendList() != null && CoreModel.getInstance().getFriendList().size() != 0) {
					SOSActivity.startActivity(LeftMenuActivity.this);
				} else {
					showAddFamilyDialog();
				}
			}
			break;
		case R.id.main_menu_setting:
			if (CoreModel.getInstance().checkUserLogined(LeftMenuActivity.this)) {
				SettingActivity.startActivity(LeftMenuActivity.this);
			}
			break;
		case R.id.main_menu_exit:
			// exit
			if (CoreModel.getInstance().checkUserLogined(LeftMenuActivity.this)) {
				showLogoutDialog();
			}
			break;
		case R.id.main_menu_feedback:
			// exit
			if (CoreModel.getInstance().checkUserLogined(LeftMenuActivity.this)) {
				FeedbackHelper.getInstance(LeftMenuActivity.this).startFeedback();
			}
			break;
		case R.id.layout_menu_close:
		case R.id.left_menu_close:
			LeftMenuActivity.this.finish();
			LeftMenuActivity.this.overridePendingTransition(R.anim.translate_between_interface_right_in, R.anim.translate_between_interface_left_out);
			break;
		}
	}

	private void showLogoutDialog() {
		View view = LeftMenuActivity.this.getLayoutInflater().inflate(R.layout.dialog_two_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(LeftMenuActivity.this, view, false, true);
			final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			txtTitle.setText(getString(R.string.dialog_title_tip));
			final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			txtContent.setText(getString(R.string.dialog_logout_content));

			final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
			btnConfirm.setText(getString(R.string.dialog_logout_yes));
			btnCancel.setText(getString(R.string.dialog_logout_no));
			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
					logout();
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

	protected void showAddFamilyDialog() {
		DialogHelper.showOneDialog(LeftMenuActivity.this, false, null, getString(R.string.dialog_nofamilyescort_content),
				getString(R.string.dialog_delfri_yes), true, new OnClickListener() {
					public void onClick(View v) {
						AddFamilyActivity.startActivity(LeftMenuActivity.this);
					}
				});
	}

	private void logout() {
		// 发送检测版本请求
		sendEmptyMessage(YSMSG.REQ_LOGOUT);
		showWaitingDialog();
	}

}
