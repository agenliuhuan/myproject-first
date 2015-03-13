package mobi.dlys.android.familysafer.ui.checkin;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SentCheckActivity extends BaseExActivity {

	public static void startActivity(Context context) {
		Intent intent = new Intent(context, SentCheckActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sentcheck);

		initView();

		initData();
	}

	void initView() {
		Button btnKnow = (Button) this.findViewById(R.id.btn_sentcheck_know);
		btnKnow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SentCheckActivity.this.finish();
			}
		});

	}

	void initData() {

	}

}
