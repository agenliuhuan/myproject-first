package mobi.dlys.android.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 操作Preference的工具类
 * 
 * 
 */
public class PreferenceHelper {

	private static final String PERF_NAME = "config";
	private final SharedPreferences mPreferences;

	/**
	 * @param context
	 */
	public PreferenceHelper() {
		mPreferences = AndroidConfig.getContext().getSharedPreferences(
				PERF_NAME, Context.MODE_PRIVATE);
	}

	public int getLong(String id, int defaulValue) {
		return mPreferences.getInt(id, defaulValue);
	}

	public void setLong(String id, int value) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putInt(id, value);
		editor.commit();
	}

	public long getLong(String id, long defaulValue) {
		return mPreferences.getLong(id, defaulValue);
	}

	public void setLong(String id, long value) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putLong(id, value);
		editor.commit();
	}

	public void setBoolean(String id, boolean value) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putBoolean(id, value);
		editor.commit();
	}

	public boolean getBoolean(String id, boolean defaulValue) {

		return mPreferences.getBoolean(id, defaulValue);
	}

	public void setString(String id, String value) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(id, value);
		editor.commit();
	}

	public String getString(String id, String defaulValue) {
		return mPreferences.getString(id, defaulValue);
	}

	public static void removeKeyFromPref(Context context, String prefName,
			String key) {
		SharedPreferences sp = context.getSharedPreferences(prefName,
				Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.remove(key);
		edit.commit();
	}

}
