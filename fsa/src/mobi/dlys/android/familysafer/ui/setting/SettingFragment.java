package mobi.dlys.android.familysafer.ui.setting;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.login.LoginActivity;
import mobi.dlys.android.familysafer.ui.main.Guide2Activity;
import mobi.dlys.android.familysafer.ui.main.GuideActivity;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import mobi.dlys.android.familysafer.ui.main.SplashActivity;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import mobi.dlys.android.familysafer.utils.umeng.FeedbackHelper;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingFragment extends BaseExFragment {//implements OnClickListener, OnCheckedChangeListener {
	protected TitleBarHolder mTitleBar;

	private RelativeLayout mAccount = null;
	//private RelativeLayout mHideLayout = null;
	//private CheckBox mHide = null;
	private RelativeLayout mGuide = null;
	private RelativeLayout mFeedback = null;
	private RelativeLayout mUpdate = null;

	private Button mLogout = null;
	private boolean mUpdateData = true;

	private TextView tvVer = null;
	private ImageView imgVer = null;

	private String strVersion = null;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			displayTip();
		} else {
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_setting, null);

		initSubView();
		initData();
		return mRootView;
	}

	private void initSubView() {
		mTitleBar = new TitleBarHolder(getActivity(), mRootView);
		mTitleBar.mTitle.setText(R.string.fragment_setting_ttb_title);
		mTitleBar.mTitle.setTextColor(getResources().getColor(R.color.title_green_line));
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MainActivity) getActivity()).toggle();
			}
		});
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		mLogout = (Button) findViewById(R.id.btn_setting_logout);
		mLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showLogoutDialog();

			}
		});

		// mAccount = (RelativeLayout)
		// findViewById(R.id.layout_setting_account);
		// mAccount.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// Intent intent = new Intent(getActivity(), AccountActivity.class);
		// startActivity(intent);
		// }
		// });

		//mHideLayout = (RelativeLayout) findViewById(R.id.layout_setting_hide);
		// mHideLayout.setOnClickListener(this);

		//mHide = (CheckBox) findViewById(R.id.cb_setting_hide);
		//mHide.setOnCheckedChangeListener(this);
		//mHide.setOnClickListener(this);

		mGuide = (RelativeLayout) findViewById(R.id.layout_setting_guide);
		mGuide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), Guide2Activity.class);
				startActivity(intent);

			}
		});

		mFeedback = (RelativeLayout) findViewById(R.id.layout_setting_feedback);
		mFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FeedbackHelper.getInstance(getActivity()).startFeedback();
			}
		});

		mUpdate = (RelativeLayout) findViewById(R.id.layout_setting_update);
		mUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				checkVersion();
			}
		});

		tvVer = (TextView) findViewById(R.id.tv_version);
		tvVer.setText("");

		imgVer = (ImageView) findViewById(R.id.img_version);
		imgVer.setVisibility(View.GONE);
	}

	private void initData() {
		//UserObject user = CoreModel.getInstance().getUserInfo();
		//if (user != null) {
		//	mHide.setChecked(user.getHideLocation());
		//}

		strVersion = getAppVersionName(getActivity().getApplicationContext());
		displayTip();
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {

		}
		return versionName;
	}

	public void displayTip() {
		if (PreferencesUtils.hasNewVersion()) {
			tvVer.setText(strVersion);
			tvVer.setVisibility(View.VISIBLE);
			imgVer.setVisibility(View.VISIBLE);
		} else {
			tvVer.setText(strVersion);
			tvVer.setVisibility(View.VISIBLE);
			imgVer.setVisibility(View.GONE);
		}
		mTitleBar.displayTip();
	}

	//@Override
	//public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	//	switch (buttonView.getId()) {
	//	case R.id.cb_setting_hide: {
//
	//	}
	//		break;
	//	}

	//}

	//@Override
	//public void onClick(View arg0) {
	//	switch (arg0.getId()) {
	//	case R.id.cb_setting_hide: {
	//		mHandler.postDelayed(new Runnable() {
	//			@Override
	//			public void run() {
	//				AnalyticsHelper.onEvent(getActivity(), AnalyticsHelper.index_hide_myself);
	//			}
	//		}, 1000);

	//		hideLocation(mHide.isChecked());
	//	}
	//		break;
	//	}
	//}

	private void hideLocation(boolean hide) {
		// 发送隐藏地理位置请求
		sendMessage(YSMSG.REQ_HIDE_LOCATION, 0, 0, hide);
		showWaitingDialog();
	}

	private void checkVersion() {
		// 发送检测版本请求
		CoreModel.getInstance().setVersionChecked(true);
		sendEmptyMessage(YSMSG.REQ_CHECK_VERSION);
		showWaitingDialog();
	}

	private void logout() {
		// 发送检测版本请求
		sendEmptyMessage(YSMSG.REQ_LOGOUT);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_HIDE_LOCATION: {
			dismissWaitingDialog();

			if (msg.arg1 == 200) {
				// 成功

			} else {
				// 失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(getActivity(), result.getErrorMsg());
				} else {
					YSToast.showToast(getActivity(), R.string.network_error);
				}

				//mHide.setChecked(!mHide.isChecked());
			}
		}
			break;
		case YSMSG.RESP_LOGOUT: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 注销成功
				GuideActivity.startActivity(getActivity());
				((MainActivity) getActivity()).finish();
			} else {
				// 注销失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(getActivity(), result.getErrorMsg());
				} else {
					YSToast.showToast(getActivity(), R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_GET_NEW_MSG_NUM:
			if (msg.arg1 == 200) {
				displayTip();
			}
			break;
		case YSMSG.RESP_CHECK_VERSION: {
			dismissWaitingDialog();
		}
			break;
		}
	}

	private void showLogoutDialog() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_two_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(getActivity(), view, false, true);
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

}