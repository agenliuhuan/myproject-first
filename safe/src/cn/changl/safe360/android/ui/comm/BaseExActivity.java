package cn.changl.safe360.android.ui.comm;

import mobi.dlys.android.core.mvc.BaseActivity;
import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.LogUtils;
import android.os.Bundle;
import android.widget.ProgressBar;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.utils.umeng.AnalyticsHelper;

public class BaseExActivity extends BaseActivity {
	private YSWaitingDialog mWaitingDialog;
	private boolean mIsActivity = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		App.getInstance().addActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		App.getInstance().setForegroundActivity(this);
		AnalyticsHelper.onPageStart(this.getClass().getSimpleName());
		AnalyticsHelper.onResume(this);

		if (!mIsActivity) {
			mIsActivity = true;

			App.getInstance().setMapScanForegroundMode();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		AnalyticsHelper.onPageEnd(this.getClass().getSimpleName());
		AnalyticsHelper.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (!ActivityUtils.isAppInForeground(mActivity)) {
			mIsActivity = false;

			App.getInstance().setMapScanBackgroundMode();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		dismissWaitingDialog();
	}

	public void showWaitingDialog() {
		if (null == mWaitingDialog) {
			mWaitingDialog = new YSWaitingDialog(this);
		}

		if (!mWaitingDialog.isShowing()) {
			mWaitingDialog.show();
		}
	}

	public void showOrUpdateWaitingDialog(String message) {

		if (null == mWaitingDialog) {
			mWaitingDialog = new YSWaitingDialog(this);
		}

		mWaitingDialog.setProHintStr(message);

		if (!mWaitingDialog.isShowing()) {
			mWaitingDialog.show();
		}
	}

	public void showOrUpdateWaitingDialog(int resId) {
		showOrUpdateWaitingDialog(getString(resId));
	}

	public void updateWaitingDialogResource(int resId) {
		if (null != mWaitingDialog) {
			ProgressBar pb = (ProgressBar) mWaitingDialog.findViewById(R.id.unified_loading_view_circle);
			if (pb != null) {
				mWaitingDialog.setCanceledOnTouchOutside(true);
				pb.setIndeterminateDrawable(null);
				pb.setBackgroundResource(resId);
			}
		}
	}

	public void dismissWaitingDialog() {

		if (null != mWaitingDialog && mWaitingDialog.isShowing()) {
			mWaitingDialog.dismiss();
			mWaitingDialog = null;
		}
	}

	public boolean isWaitingDialogShowing() {
		return null != mWaitingDialog && mWaitingDialog.isShowing();
	}
}
