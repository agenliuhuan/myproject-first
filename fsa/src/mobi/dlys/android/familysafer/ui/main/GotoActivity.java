package mobi.dlys.android.familysafer.ui.main;

import mobi.dlys.android.core.mvc.BaseActivity;
import mobi.dlys.android.core.utils.LogUtils;
import android.content.Intent;
import android.os.Bundle;

public class GotoActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gotoActivity();
		finish();
	}

	private void gotoActivity() {
		Intent intent = getIntent();
		if (null != intent) {
			boolean move = intent.getBooleanExtra("movetasktoback", false);
			LogUtils.i("movetasktoback", String.valueOf(move));
			if (move) {
				moveTaskToBack(true);

				Intent intent2 = new Intent(GotoActivity.this, GuideActivity.class);
				startActivity(intent2);
			}
		}
	}
}
