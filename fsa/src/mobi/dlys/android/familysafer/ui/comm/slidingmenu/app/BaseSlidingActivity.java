package mobi.dlys.android.familysafer.ui.comm.slidingmenu.app;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.YSWaitingDialog;
import mobi.dlys.android.familysafer.ui.comm.slidingmenu.lib.SlidingMenu;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ProgressBar;

public class BaseSlidingActivity extends SlidingFragmentActivity {

	private YSWaitingDialog mWaitingDialog;
	protected ListFragment mFrag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the Behind View
		setBehindContentView(R.layout.layout_slidingmenu);

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
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
