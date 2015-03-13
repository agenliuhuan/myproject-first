package cn.changl.safe360.android.ui.comm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

public class YSBaseDialog extends Dialog {

	private Context mCtx;

	private Object mTag;

	public YSBaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mCtx = context;
	}

	public YSBaseDialog(Context context, int theme) {
		super(context, theme);
		mCtx = context;
	}

	public YSBaseDialog(Context context) {
		super(context);
		mCtx = context;
	}

	@Override
	public void dismiss() {

		try {

			if (mCtx instanceof Activity) {
				Activity aty = (Activity) mCtx;
				boolean isFinishing = aty.isFinishing();
				if (!isFinishing) {
					super.dismiss();
				}
			} else {
				if (isShowing()) {
					super.dismiss();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void show() {
		try {
			Activity activity = this.getOwnerActivity();
			if (mCtx instanceof Activity) {
				activity = (Activity) mCtx;
			}
			if (activity != null) {
				boolean isFinishing = activity.isFinishing();
				if (!isFinishing && activity.getWindow() != null) {
					super.show();
				}
			} else {
				super.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTag(Object tag) {
		mTag = tag;
	}

	public Object getTag() {
		return mTag;
	}
}
