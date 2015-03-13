package mobi.dlys.android.familysafer.ui.comm;

import mobi.dlys.android.core.mvc.BaseActivity;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.widget.ProgressBar;

public class BaseExActivity extends BaseActivity {
	private YSWaitingDialog mWaitingDialog;

	@Override
	protected void onResume() {
		super.onResume();

		App.getInstance().setForegroundActivity(this);
		AnalyticsHelper.onPageStart(this.getClass().getSimpleName());
		AnalyticsHelper.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		AnalyticsHelper.onPageEnd(this.getClass().getSimpleName());
		AnalyticsHelper.onPause(this);
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
