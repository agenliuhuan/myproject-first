package mobi.dlys.android.familysafer.receiver;

import mobi.dlys.android.familysafer.ui.login.LoginActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	String action_boot = "android.intent.action.BOOT_COMPLETED";

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(action_boot)) {
			Intent ootStartIntent = new Intent(context, LoginActivity.class);
			ootStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ootStartIntent.putExtra("movetasktoback", true);
			context.startActivity(ootStartIntent);
		}
	}

}
