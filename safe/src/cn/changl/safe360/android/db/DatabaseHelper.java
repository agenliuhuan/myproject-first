package cn.changl.safe360.android.db;

import mobi.dlys.android.core.utils.LogUtils;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import cn.changl.safe360.android.biz.vo.LocalTripObject;
import cn.changl.safe360.android.biz.vo.LocalUserObject;
import cn.changl.safe360.android.biz.vo.MessageObject;
import cn.changl.safe360.android.biz.vo.PushMsgObject;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "Safe360";
	private static final String DATABASE_SUFFIX = ".db";
	private static final int DATABASE_VERSION = 7; // 数据库版本号

	public DatabaseHelper(final Context context, int userId) {
		super(context, DATABASE_NAME + userId + DATABASE_SUFFIX, null, DATABASE_VERSION);
	}

	// the DAO object we use to access the SimpleData table
	// private Dao<UserObject, Integer> userObjectDao = null;

	@Override
	public void onCreate(final SQLiteDatabase db, final ConnectionSource connectionSource) {
		try {
			LogUtils.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, LocalUserObject.class);
			TableUtils.createTable(connectionSource, LocalTripObject.class);
			TableUtils.createTable(connectionSource, MessageObject.class);
			TableUtils.createTable(connectionSource, PushMsgObject.class);

		} catch (final SQLException e) {
			LogUtils.e(DatabaseHelper.class.getName(), "Can't create database" + e);
			throw new RuntimeException(e);
		} catch (final Exception e) {
			LogUtils.e(DatabaseHelper.class.getName(), "Can't create database" + e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final ConnectionSource connectionSource, final int oldVersion, final int newVersion) {
		try {
			LogUtils.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, LocalUserObject.class, true);
			TableUtils.dropTable(connectionSource, LocalTripObject.class, true);
			TableUtils.dropTable(connectionSource, MessageObject.class, true);
			TableUtils.dropTable(connectionSource, PushMsgObject.class, true);

			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (final SQLException e) {
			LogUtils.e(DatabaseHelper.class.getName(), "Can't drop databases" + e);
			throw new RuntimeException(e);
		} catch (final Exception e) {
			LogUtils.e(DatabaseHelper.class.getName(), "Can't create database" + e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
	}

}
