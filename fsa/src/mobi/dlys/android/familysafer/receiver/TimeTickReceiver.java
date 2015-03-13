package mobi.dlys.android.familysafer.receiver;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.main.GuideActivity;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 该广播接收器每分钟会收到一个次广播。
 * 
 * Broadcast Action: The current time has changed. Sent every minute.
 * 
 * 
 */
public class TimeTickReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		if (arg1.getAction().equals(Intent.ACTION_TIME_TICK) && App.getCurProcessName(App.getInstance()).equals("mobi.dlys.android.familysafer")) {
			boolean isExist = ActivityUtils.isActivityExist(context, "mobi.dlys.android.familysafer", "mobi.dlys.android.familysafer.ui.main.MainActivity");
			LogUtils.i("movetasktoback", String.valueOf(isExist));
			if (!isExist) {
				if (PreferencesUtils.autoLogin()) {
					Intent ootStartIntent = new Intent(context, GuideActivity.class);
					ootStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ootStartIntent.putExtra("movetasktoback", true);
					context.startActivity(ootStartIntent);
				}
			} else {
				if (CoreModel.getInstance().isLogined()) {
					if (PreferencesUtils.isTimeToModifyLocation()) {
						BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_MODIFY_LOCATION);
					}
				}

			}

			PreferencesUtils.setTimeToModifyLocation();
		}
	}

}
