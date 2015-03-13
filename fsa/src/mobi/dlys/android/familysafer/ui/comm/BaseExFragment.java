package mobi.dlys.android.familysafer.ui.comm;

import mobi.dlys.android.core.mvc.BaseFragment;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.os.Bundle;
import android.widget.ProgressBar;

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

	public void displayTip() {

	}

	public void clearFriendReqCount() {
		CoreModel.getInstance().setFriendReqCount(0);
	}

	public void clearEventCount() {
		CoreModel.getInstance().setEventCount(0);
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

	public void startConnectServer() {
		mHandler.postDelayed(connectserver, 1000);
	}

	public void stopConnectServer() {
		mHandler.removeCallbacks(connectserver);
	}

	int CHECK_NEW_MSG_SECOND = 10000;
	private Runnable connectserver = new Runnable() {
		public void run() {
			sendEmptyMessage(YSMSG.REQ_GET_NEW_MSG_NUM);
			mHandler.postDelayed(connectserver, CHECK_NEW_MSG_SECOND);
		}
	};
}
