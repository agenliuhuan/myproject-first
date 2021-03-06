package cn.changl.safe360.android.utils;

import mobi.dlys.android.core.mvc.BaseController;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UpdateObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.YSAlertDialog;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.ui.comm.slidingmenu.app.BaseSlidingActivity;

public class UpdateVersionUtils {

	public static void firstCheckVersion() {
		// 发送检测版本请求
		if (!CoreModel.getInstance().isVersionChecked()) {
			BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_CHECK_VERSION);
		}
	}

	public static void checkVersion() {
		// 发送检测版本请求
		BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_CHECK_VERSION);
	}

	public static void handleMessage(Activity activity, Message msg) {
		BaseExActivity baseActivity = null;
		if (activity instanceof BaseExActivity) {
			baseActivity = (BaseExActivity) activity;
		}

		BaseSlidingActivity sldingActivity = null;
		if (activity instanceof BaseSlidingActivity) {
			sldingActivity = (BaseSlidingActivity) activity;
		}

		if (null == msg || null == activity || (sldingActivity == null && baseActivity == null)) {
			return;
		}

		if (msg.arg1 == 200) {
			// 检测成功
			if (msg.obj instanceof UpdateObject) {
				UpdateObject update = (UpdateObject) msg.obj;
				if (update.canUpdate()) {
					PreferencesUtils.setNewVersion(true);
					if (baseActivity != null) {
						baseActivity.dismissWaitingDialog();
					} else if (sldingActivity != null) {
						sldingActivity.dismissWaitingDialog();
					}
					if (!update.forceUpdate() && !CoreModel.getInstance().isVersionChecked()) {
						showUpdateDialog(App.getInstance().getForegroundActivity(), update);
					} else {
						showUpdateDialog(App.getInstance().getForegroundActivity(), update);
					}
				} else {
					// 没有更新
					PreferencesUtils.setNewVersion(false);
					if (CoreModel.getInstance().isVersionChecked()) {
						if (baseActivity != null) {
//							baseActivity.showOrUpdateWaitingDialog(R.string.update_no_update_version);
//							baseActivity.updateWaitingDialogResource(R.drawable.sendok);
							YSToast.showToast(baseActivity, R.string.update_no_update_version);
						} else if (sldingActivity != null) {
							sldingActivity.showOrUpdateWaitingDialog(R.string.update_no_update_version);
							sldingActivity.updateWaitingDialogResource(R.drawable.sendok);
						}
					}
					// YSAlertDialog.createNewestDialog(getActivity());
				}
				CoreModel.getInstance().setVersionChecked(true);
			}
		} else {
			if (CoreModel.getInstance().isVersionChecked()) {
				// 检测失败
				if (baseActivity != null) {
					baseActivity.dismissWaitingDialog();
				} else if (sldingActivity != null) {
					sldingActivity.dismissWaitingDialog();
				}
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(activity, result.getErrorMsg());
				} else {
					YSToast.showToast(activity, R.string.network_error);
				}
			} else {
				CoreModel.getInstance().setVersionChecked(true);
			}
		}

	}

	/**
	 * 显示更新对话框
	 * 
	 * @param activity
	 * @param updateObject
	 */
	private static void showUpdateDialog(final Activity activity, final UpdateObject updateObject) {
		View view = activity.getLayoutInflater().inflate(R.layout.dialog_two_button, null);
		if (view != null && updateObject != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(activity, view, false, true);
			final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			txtTitle.setText(activity.getString(R.string.update_title));
			final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			txtContent.setText(updateObject.getUpdateLog());

			final boolean forceUpdate = updateObject.forceUpdate();

			final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
			btnConfirm.setText(activity.getString(R.string.update_yes));
			btnCancel.setText(activity.getString(R.string.update_no));
			btnConfirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					downloadPackege(activity, updateObject.getDownloadUrl());
					dialog.cancel();
					if (forceUpdate) {
						App.getInstance().exitApp();
					}
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.cancel();
					if (forceUpdate) {
						App.getInstance().exitApp();
					}
				}
			});

			dialog.show();
		}
	}

	/**
	 * 下载apk安装包
	 * 
	 * @param context
	 * @param url
	 */
	private static void downloadPackege(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(downloadIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
