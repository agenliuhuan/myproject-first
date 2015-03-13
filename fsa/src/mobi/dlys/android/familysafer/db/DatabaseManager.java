package mobi.dlys.android.familysafer.db;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.db.dao.FriendRequestObjectDao;
import mobi.dlys.android.familysafer.db.dao.MsgObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;

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
			PageInfoObjectDao.initAllPageInfo();
			new FriendRequestObjectDao().clear();
			new MsgObjectDao().resetMsgStatus();
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
