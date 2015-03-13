package cn.changl.safe360.android.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.changl.safe360.android.App;

/**
 * 资源实用类
 * 
 * @author rocksen
 */

public class ResUtils {
	/**
	 * 根据资源id获取字符串
	 * 
	 * @param resId
	 * @return
	 */
	public static String getString(int resId) {
		return App.getInstance().getString(resId);
	}

	/**
	 * 获取图片资源的路径，用于ImageLoader加载图片
	 * 
	 * @param resId
	 * @return
	 */
	public static String getDrawableString(int resId) {
		return "drawable://" + resId;
	}

	/**
	 * 获取竖排文字
	 * 
	 * @param src
	 * @return
	 */
	public static String getVerticalText(String src) {
		if (TextUtils.isEmpty(src)) {
			return src;
		}

		String verticalText = "";
		for (int i = 0; i < src.length(); i++) {
			verticalText += src.substring(i, i + 1);
			verticalText += "\n";
		}

		return verticalText;
	}

	/**
	 * 根据资源id获取尺寸
	 * 
	 * @param resId
	 * @return
	 */
	public static float getDimen(int resId) {
		return App.getInstance().getResources().getDimension(resId);
	}

	/**
	 * 设置view是否可见
	 * 
	 * @param activity
	 * @param resId
	 * @param visibility
	 */
	public static void setVisibility(Activity activity, int resId, int visibility) {
		if (activity == null || ((View) activity.findViewById(resId)) == null)
			return;

		((View) activity.findViewById(resId)).setVisibility(visibility);
	}

	public static void setVisibility(View view, int resId, int visibility) {
		if (view == null || ((View) view.findViewById(resId)) == null)
			return;

		((View) view.findViewById(resId)).setVisibility(visibility);
	}

	/**
	 * 判断view是否可见
	 * 
	 * @param activity
	 * @param resId
	 * @return
	 */
	public static boolean isVisible(Activity activity, int resId) {
		if (activity == null || ((View) activity.findViewById(resId)) == null)
			return false;

		if (View.VISIBLE == ((View) activity.findViewById(resId)).getVisibility()) {
			return true;
		}

		return false;
	}

	/**
	 * 设置文本
	 * 
	 * @param activity
	 * @param resId
	 * @param text
	 */
	public static void setText(Activity activity, int resId, String text) {
		if (null == activity || ((TextView) activity.findViewById(resId)) == null)
			return;

		((TextView) activity.findViewById(resId)).setText(text);
	}

	public static void setText(View view, int resId, String text) {
		if (null == view || ((TextView) view.findViewById(resId)) == null)
			return;

		((TextView) view.findViewById(resId)).setText(text);
	}

	public static void setText(TextView view, String text) {
		if (null == view)
			return;

		view.setText(text);
	}

	public static void setText(Button btn, String text) {
		if (btn == null)
			return;

		btn.setText(text);
	}

	/**
	 * 设置背景资源图片
	 * 
	 * @param activity
	 * @param resId
	 * @param imageResId
	 */
	public static void setBgResource(Activity activity, int resId, int imageResId) {
		if (null == activity || ((ImageView) activity.findViewById(resId)) == null)
			return;

		try {
			((ImageView) activity.findViewById(resId)).setBackgroundResource(imageResId);
		} catch (Exception e) {

		} catch (OutOfMemoryError e) {

		}
	}

	public static void setImageResource(Activity activity, int resId, int imageResId) {
		if (null == activity || ((ImageView) activity.findViewById(resId)) == null)
			return;

		try {
			((ImageView) activity.findViewById(resId)).setBackgroundResource(imageResId);
		} catch (Exception e) {

		} catch (OutOfMemoryError e) {

		}
	}

	public static void setImageResource(View view, int imageResId) {
		if (null == view)
			return;

		try {
			((ImageView) view).setImageResource(imageResId);
		} catch (Exception e) {

		} catch (OutOfMemoryError e) {

		}
	}

	public static void setImageBitmap(Activity activity, int resId, Bitmap bmp) {
		if (activity == null || bmp == null || ((ImageView) activity.findViewById(resId)) == null)
			return;

		try {
			((ImageView) activity.findViewById(resId)).setImageBitmap(bmp);
		} catch (Exception e) {

		} catch (OutOfMemoryError e) {

		}
	}

	public static void setImageBitmap(View view, Bitmap bmp) {
		if (null == view || bmp == null)
			return;

		try {
			((ImageView) view).setImageBitmap(bmp);
		} catch (Exception e) {

		} catch (OutOfMemoryError e) {

		}
	}

	/**
	 * 设置背景资源图片
	 * 
	 * @param activity
	 * @param resId
	 * @param imageResId
	 */
	public static void setBackgroundDrawable(Activity activity, int resId, View view) {
		if (null == activity || view == null)
			return;

		try {
			BitmapDrawable bd = new BitmapDrawable(activity.getResources().openRawResource(resId));
			view.setBackgroundDrawable(bd);
		} catch (Exception e) {

		} catch (OutOfMemoryError e) {

		}
	}

	/**
	 * 释放背景资源
	 * 
	 * @param view
	 */
	public static void recycleDrawable(Activity activity, int resId) {
		if (activity == null) {
			return;
		}

		View view = (View) activity.findViewById(resId);
		if (view == null) {
			return;
		}

		try {
			BitmapDrawable bd = (BitmapDrawable) view.getBackground();
			view.setBackgroundResource(0);// 把背景设为null，避免onDraw刷新背景时候出现used a
											// recycled bitmap错误
			if (bd != null) {
				bd.setCallback(null);
				// bd.getBitmap().recycle();
			}
		} catch (Exception e) {
		}
	}

	public static void recycleDrawable(View view, int resId, boolean recycle) {
		if (view == null) {
			return;
		}

		View view2 = (View) view.findViewById(resId);
		if (view2 == null) {
			return;
		}

		if (view2 instanceof ImageView) {
			recycleImageView((ImageView) view2, recycle);
		} else {
			recycleDrawable(view2, recycle);
		}
	}

	public static void recycleDrawable(View view, boolean recycle) {
		if (view == null) {
			return;
		}

		try {
			Drawable drawable = view.getBackground();
			view.setBackgroundResource(0);// 把背景设为null，避免onDraw刷新背景时候出现used a
			if (drawable instanceof BitmapDrawable) {
				BitmapDrawable bd = (BitmapDrawable) drawable;
				view.setBackgroundResource(0);// 把背景设为null，避免onDraw刷新背景时候出现used
												// a
				// recycled bitmap错误
				if (bd != null) {
					if (recycle) {
						bd.getBitmap().recycle();
					}
					bd.setCallback(null);
				}
			}
		} catch (Exception e) {
		}
	}

	public static void recycleDrawableWithColor(View view, int color) {
		if (view == null) {
			return;
		}

		try {
			BitmapDrawable bd = (BitmapDrawable) view.getBackground();
			view.setBackgroundResource(0);// 把背景设为null，避免onDraw刷新背景时候出现used a
			view.setBackgroundColor(color);
			// recycled bitmap错误
			if (bd != null) {
				bd.getBitmap().recycle();
				bd.setCallback(null);
			}
		} catch (Exception e) {
		}
	}

	public static void recycleImageView(ImageView view, boolean recycle) {
		if (view == null) {
			return;
		}

		try {
			Drawable drawable = view.getDrawable();
			if (drawable instanceof StateListDrawable) {
				StateListDrawable bd = (StateListDrawable) drawable;
				view.setImageDrawable(null); // 把背景设为null，避免onDraw刷新背景时候出现used a
				// recycled bitmap错误
				if (bd != null) {
					bd.setCallback(null);
				}
			} else if (drawable instanceof BitmapDrawable) {
				BitmapDrawable bd = (BitmapDrawable) drawable;
				view.setImageDrawable(null); // 把背景设为null，避免onDraw刷新背景时候出现used a
				// recycled bitmap错误
				if (bd != null) {
					if (recycle) {
						bd.getBitmap().recycle();
					}
					bd.setCallback(null);
				}
			}
		} catch (Exception e) {
		}

	}

	/**
	 * 设置view是否可见
	 * 
	 * @param view
	 * @param visibility
	 */
	public static void setVisibility(View view, int visibility) {
		if (view == null)
			return;

		view.setVisibility(visibility);
	}

	public static void setTextColor(View view, int resId, int color) {
		if (null == view || ((TextView) view.findViewById(resId)) == null)
			return;

		((TextView) view.findViewById(resId)).setTextColor(getAppContext().getResources().getColor(color));
	}

	public static void setTextColor(TextView view, int color) {
		if (view == null)
			return;

		view.setTextColor(getAppContext().getResources().getColor(color));
	}

	public static void setBackgroundColor(View view, int resId, int color) {
		if (null == view || ((View) view.findViewById(resId)) == null)
			return;

		((View) view.findViewById(resId)).setBackgroundColor(getAppContext().getResources().getColor(color));
	}

	public static void setBackgroundColor(View view, int color) {
		if (view == null)
			return;

		view.setBackgroundColor(getAppContext().getResources().getColor(color));
	}

	/**
	 * 设置view是否激活
	 * 
	 * @param view
	 * @param enabled
	 */
	public static void setEnabled(View view, boolean enabled) {
		if (view == null)
			return;

		view.setEnabled(enabled);
	}

	/**
	 * 获取全局Context
	 * 
	 * @return
	 */
	public static Context getAppContext() {
		return App.getInstance().getApplicationContext();
	}
}