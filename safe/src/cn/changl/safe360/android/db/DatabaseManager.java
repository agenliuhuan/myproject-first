package cn.changl.safe360.android.db;

import cn.changl.safe360.android.App;

public class DatabaseManager {

	private DatabaseHelper databaseHelper = null;

	private static DatabaseManager mInstance = null;

	public static DatabaseManager getInstance() {
		if (null == mInstance) {
			mInstance = new DatabaseManager();
		}

		return mInstance;
	}

	/**
	 * 初始化DatabaseHelper
	 * 
	 * @param userId
	 */
	public void initHelper(int userId) {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(App.getInstance().getApplicationContext(), userId);
		}
	}

	/**
	 * 获取DatabaseHelper（必须初始化后，才能调用）
	 * 
	 * @return
	 */
	public DatabaseHelper getHelper() {
		return databaseHelper;
	}

	/**
	 * 释放DatabaseHelper
	 */
	public void releaseHelper() {
		if (databaseHelper != null) {
			databaseHelper.close();
			databaseHelper = null;
		}
	}

}
