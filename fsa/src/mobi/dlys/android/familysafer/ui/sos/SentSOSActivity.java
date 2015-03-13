package mobi.dlys.android.familysafer.ui.sos;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SentSOSActivity extends BaseExActivity {

	Button mFailSOSButton = null;
	Button mCall110Button = null;

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, SentSOSActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sentsos);

		initView();
	}

	void initView() {
		mFailSOSButton = (Button) findViewById(R.id.btn_sentsos_know);
		mCall110Button = (Button) findViewById(R.id.btn_sentsos_call_110);

		mFailSOSButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SentSOSActivity.this.finish();

			}

		});

		mCall110Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						AnalyticsHelper.onEvent(SentSOSActivity.this, AnalyticsHelper.index_call_1102);
					}
				}, 1000);
				TelephonyUtils.call(SentSOSActivity.this, "110");
				SentSOSActivity.this.finish();
			}

		});

	}
}