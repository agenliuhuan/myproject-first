package cn.changl.safe360.android.ui.comm;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;

public class DialogHelper {

	/**
	 * 显示对话框
	 * 
	 * @param activity
	 * @param title
	 * @param content
	 * @param cancelable
	 *            是否可以取消对话框
	 * @param okClickListener
	 * @param cancelClickListener
	 */
	public static void showTwoDialog(Activity activity, boolean systemAlert, String title, String content, String button1, String button2,
			final boolean cancelable, final View.OnClickListener okClickListener, final View.OnClickListener cancelClickListener) {
		if (activity != null) {
			View view = activity.getLayoutInflater().inflate(R.layout.dialog_two_button, null);
			if (view != null) {
				final Dialog dialog = YSAlertDialog.createBaseDialog((systemAlert) ? App.getInstance().getApplicationContext() : activity, view, systemAlert,
						cancelable);
				final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
				if (TextUtils.isEmpty(title)) {
					txtTitle.setText(activity.getResources().getString(R.string.dialog_title_tip));
				} else {
					txtTitle.setText(title);
				}
				final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
				txtContent.setText(content);

				final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
				final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
				if (TextUtils.isEmpty(button1)) {
					btnConfirm.setText(activity.getResources().getString(R.string.btn_confirm));
				} else {
					btnConfirm.setText(button1);
				}
				if (TextUtils.isEmpty(button2)) {
					btnCancel.setText(activity.getResources().getString(R.string.btn_cancel));
				} else {
					btnCancel.setText(button2);
				}
				btnConfirm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (okClickListener != null) {
							okClickListener.onClick(arg0);
						}
						dialog.dismiss();
					}
				});
				btnCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (cancelClickListener != null) {
							cancelClickListener.onClick(arg0);
						}
						dialog.dismiss();
					}
				});

				dialog.show();
			}
		}
	}

	public static void showOneDialog(Activity activity, boolean systemAlert, String title, String content, String button1, final boolean cancelable,
			final View.OnClickListener okClickListener) {
		if (activity != null) {
			View view = activity.getLayoutInflater().inflate(R.layout.dialog_one_button, null);
			if (view != null) {
				final Dialog dialog = YSAlertDialog.createBaseDialog((systemAlert) ? App.getInstance().getApplicationContext() : activity, view, systemAlert,
						cancelable);
				final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
				if (TextUtils.isEmpty(title)) {
					txtTitle.setText(activity.getResources().getString(R.string.dialog_title_tip));
				} else {
					txtTitle.setText(title);
				}
				final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
				txtContent.setText(content);

				final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
				if (TextUtils.isEmpty(button1)) {
					btnConfirm.setText(activity.getResources().getString(R.string.btn_confirm));
				} else {
					btnConfirm.setText(button1);
				}

				btnConfirm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (okClickListener != null) {
							okClickListener.onClick(arg0);
						}
						dialog.dismiss();
					}
				});

				dialog.show();
			}
		}
	}
}
