package mobi.dlys.android.familysafer.ui.comm;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.vo.UpdateObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

public class YSAlertDialog {
	public static AlertDialog dialog;

	@SuppressWarnings("deprecation")
	public static void create(Context context, String title, String msg, String button1) {
		if (null == dialog || !dialog.isShowing()) {
			dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).create();
			dialog.setButton(button1, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					YSAlertDialog.close();
				}
			});
			dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						YSAlertDialog.close();
						break;
					default:
						break;
					}
					return false;
				}
			});
			dialog.show();
		}
	}

	@SuppressWarnings("deprecation")
	public static void create(Context context, String title, String msg, String button1, DialogInterface.OnClickListener okClickListener) {
		if (null == dialog || !dialog.isShowing()) {
			dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).create();
			dialog.setButton(button1, okClickListener);
			dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						// InforDialog.close();
						break;
					default:
						break;
					}
					return false;
				}
			});
			dialog.show();
		}
	}

	@SuppressWarnings("deprecation")
	public static void create(Context context, String title, String msg, String button1, DialogInterface.OnClickListener okClickListener,
			DialogInterface.OnKeyListener onKeyListener) {
		if (null == dialog || !dialog.isShowing()) {
			dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.setButton(button1, okClickListener);
			dialog.setOnKeyListener(onKeyListener);
			dialog.show();
		}
	}

	@SuppressWarnings("deprecation")
	public static AlertDialog create(Context context, String title, String msg, String button1, String button2, DialogInterface.OnClickListener okClickListener) {
		if (null == dialog || !dialog.isShowing()) {
			dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).create();
			dialog.setButton(button1, okClickListener);
			dialog.setButton2(button2, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					YSAlertDialog.close();
				}
			});
			dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						YSAlertDialog.close();
						break;
					default:
						break;
					}
					return false;
				}
			});
			dialog.show();
		}
		return dialog;
	}

	@SuppressWarnings("deprecation")
	public static AlertDialog create(Context context, String title, String msg, String button1, String button2,
			DialogInterface.OnClickListener okClickListener, DialogInterface.OnClickListener okClickListener2) {
		if (null == dialog || !dialog.isShowing()) {
			dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).create();
			dialog.setButton(button1, okClickListener);
			dialog.setButton2(button2, okClickListener2);
			dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						YSAlertDialog.close();
						break;
					default:
						break;
					}
					return false;
				}
			});
			dialog.show();
		}
		return dialog;
	}

	@SuppressWarnings("deprecation")
	public static void create(Context context, View view, String title, String msg, String button1, String button2,
			DialogInterface.OnClickListener okClickListener) {
		if (null == dialog || !dialog.isShowing()) {
			dialog = new AlertDialog.Builder(context).setView(view).setTitle(title).setMessage(msg).create();
			dialog.setButton(button1, okClickListener);
			dialog.setButton2(button2, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					YSAlertDialog.close();
				}
			});
			dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						YSAlertDialog.close();
						break;
					default:
						break;
					}
					return false;
				}
			});
			dialog.show();
		}
	}

	@SuppressWarnings("deprecation")
	public static void create(Context context, View view, String title, String msg, String button1, String button2,
			DialogInterface.OnClickListener okClickListener, DialogInterface.OnClickListener cancelClickListener) {
		if (null == dialog || !dialog.isShowing()) {
			dialog = new AlertDialog.Builder(context).setView(view).setTitle(title).setMessage(msg).create();
			dialog.setButton(button1, okClickListener);
			dialog.setButton2(button2, cancelClickListener);
			dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						YSAlertDialog.close();
						break;
					default:
						break;
					}
					return false;
				}
			});
			dialog.show();
		}
	}

	/**
	 * 对话框基类
	 * 
	 * @param context
	 * @param view
	 * @param systemAlert
	 *            是否在手机桌面弹出
	 * @param cancelable
	 *            是否可以取消
	 * @return
	 */
	public static Dialog createBaseDialog(Context context, View view, boolean systemAlert, boolean cancelable) {
		Dialog dialog = new Dialog(context, R.style.BaseDialog);
		dialog.setContentView(view);
		if (systemAlert) {
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD 
										| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//										| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
										| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		}
		dialog.setCancelable(cancelable);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					YSAlertDialog.close();
					break;
				default:
					break;
				}
				return false;
			}
		});
		dialog.show();
		return dialog;
	}

	public static Dialog createModalDialog(Context context, View view) {
		Dialog dialog = new Dialog(context, R.style.BaseDialog);
		dialog.setContentView(view);
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					return true;
				default:
					break;
				}
				return false;
			}
		});
		dialog.show();
		return dialog;
	}

	/**
	 * 版本升级对话框
	 * 
	 * @param checkVersion
	 * @return
	 */
	public static boolean updateVersionDialog(final Context context, final Handler handler, final UpdateObject updateObject) {
		if (updateObject.forceUpdate()) {
			// 强制更新
		} else {
			// 普通更新
			YSAlertDialog.create(context, context.getString(R.string.update_title), updateObject.getUpdateLog(), context.getString(R.string.btn_confirm),
					context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});

			return true;

		}
		return false;
	}

	public static void close() {
		if (null != dialog) {
			dialog.cancel();
			dialog = null;
		}
	}

	public static void createNewestDialog(final Activity activity) {
		View view = activity.getLayoutInflater().inflate(R.layout.dialog_one_button, null);
		final Dialog dialog = createBaseDialog(activity, view, false, true);
		dialog.show();
	}
}
