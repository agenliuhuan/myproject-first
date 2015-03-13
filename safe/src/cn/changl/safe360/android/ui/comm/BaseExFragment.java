package cn.changl.safe360.android.ui.comm;

import mobi.dlys.android.core.mvc.BaseFragment;
import android.os.Bundle;
import android.widget.ProgressBar;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.utils.umeng.AnalyticsHelper;

public class BaseExFragment extends BaseFragment {

	private YSWaitingDialog mWaitingDialog;
	protected boolean mIsFragmentVisible;
	protected boolean mPause = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIsFragmentVisible = true;
	}

	@Override
	public void onResume() {
		super.onResume();

		mPause = false;
		AnalyticsHelper.onPageStart(this.getClass().getSimpleName());
	}

	@Override
	public void onPause() {
		super.onPause();

		mPause = true;
		AnalyticsHelper.onPageEnd(this.getClass().getSimpleName());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		dismissWaitingDialog();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			mIsFragmentVisible = true;
		} else {
			mIsFragmentVisible = false;
		}
	}

	public void showWaitingDialog() {
		if (null == mWaitingDialog) {
			mWaitingDialog = new YSWaitingDialog(getActivity());
		}

		if (!mWaitingDialog.isShowing()) {
			mWaitingDialog.show();
		}
	}

	public void showOrUpdateWaitingDialog(String message) {

		if (null == mWaitingDialog) {
			mWaitingDialog = new YSWaitingDialog(getActivity());
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
