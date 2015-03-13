package cn.changl.safe360.android.ui.comm;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.changl.safe360.android.R;

/**
 * 公用TitleBar 辅助初始Titlebar的各个子控件。
 * 
 * @author
 * 
 */
public class TitleBarHolder {

	public Button mLeft;
	public TextView mTitle;
	public Button mRight;
	public Button mRight2;

	public TitleBarHolder(final Activity activity) {
		mLeft = (Button) activity.findViewById(R.id.btn_title_left);
		mTitle = (TextView) activity.findViewById(R.id.txt_title_center);
		mRight = (Button) activity.findViewById(R.id.btn_title_right);
		mRight2 = (Button) activity.findViewById(R.id.btn_title_right2);

		if (null != mLeft) {
			mLeft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.finish();
				}
			});
		}
	}

	public TitleBarHolder(final Activity act, View group) {
		mLeft = (Button) group.findViewById(R.id.btn_title_left);
		mTitle = (TextView) group.findViewById(R.id.txt_title_center);
		mRight = (Button) group.findViewById(R.id.btn_title_right);
		mRight2 = (Button) group.findViewById(R.id.btn_title_right2);

		if (null != mLeft) {
			mLeft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					act.finish();
				}
			});
		}
	}

}
