package mobi.dlys.android.familysafer.utils;

import mobi.dlys.android.familysafer.App;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreferance访问接口
 */
public class SharedPreferencesHelper {

	private static final String TAG = "SettingStateController";

	private static final String SETTINGSTATE_NAME = "settingstate";

	private static SharedPreferencesHelper mInstance;

	private Context mContext;

	private SharedPreferencesHelper(Context ctx) {
		mContext = ctx;
	};

	public synchronized static SharedPreferencesHelper getInstance() {

		if (null == mInstance) {
			synchronized (TAG) {
				if (null == mInstance) {
					mInstance = new SharedPreferencesHelper(App.getInstance());
				}
			}
		}
		return mInstance;
	}

	public synchronized boolean getBoolean(String key) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGSTATE_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	public synchronized boolean getBoolean(String key, boolean defaultValue) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGSTATE_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}

	public synchronized void setBoolean(String key, Boolean value) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGSTATE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public synchronized int getInt(String key) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGSTATE_NAME, Context.MODE_PRIVATE);
		return sp.getInt(key, -1);
	}

	public synchronized void setInt(String key, int value) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGSTATE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public synchronized String getString(String key) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGSTATE_NAME, Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	public synchronized void setString(String key, String value) {
		SharedPreferences sp = mContext.getSharedPreferences(SETTINGSTATE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
}
