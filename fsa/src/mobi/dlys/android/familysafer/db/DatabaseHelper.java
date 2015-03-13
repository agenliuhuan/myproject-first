package mobi.dlys.android.familysafer.db;

import java.sql.SQLException;

import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.biz.vo.ClueImageObject;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.FriendRequestObject;
import mobi.dlys.android.familysafer.biz.vo.LastMsgIdObject;
import mobi.dlys.android.familysafer.biz.vo.MsgObject;
import mobi.dlys.android.familysafer.biz.vo.MsgTopicObject;
import mobi.dlys.android.familysafer.biz.vo.MySOSObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.PublicClueObject;
import mobi.dlys.android.familysafer.biz.vo.event.EventContent;
import mobi.dlys.android.familysafer.biz.vo.event.SOSVoice;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "FamilySafer";
	private static final String DATABASE_SUFFIX = ".db";
	private static final int DATABASE_VERSION = 26; // 数据库版本号

	public DatabaseHelper(final Context context, int userId) {
		super(context, DATABASE_NAME + userId + DATABASE_SUFFIX, null, DATABASE_VERSION);
	}

	// the DAO object we use to access the SimpleData table
	// private Dao<UserObject, Integer> userObjectDao = null;

	@Override
	public void onCreate(final SQLiteDatabase db, final ConnectionSource connectionSource) {
		try {
			LogUtils.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, FriendObject.class);
			TableUtils.createTable(connectionSource, ClueImageObject.class);
			TableUtils.createTable(connectionSource, ClueObject.class);
			TableUtils.createTable(connectionSource, EventObjectEx.class);
			TableUtils.createTable(connectionSource, FriendRequestObject.class);
			TableUtils.createTable(connectionSource, EventContent.class);
			TableUtils.createTable(connectionSource, SOSVoice.class);
			TableUtils.createTable(connectionSource, PageInfoObject.class);
			TableUtils.createTable(connectionSource, MsgObject.class);
			TableUtils.createTable(connectionSource, MsgTopicObject.class);
			TableUtils.createTable(connectionSource, PublicClueObject.class);
			TableUtils.createTable(connectionSource, LastMsgIdObject.class);
			TableUtils.createTable(connectionSource, MySOSObject.class);
		} catch (final SQLException e) {
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
			TableUtils.dropTable(connectionSource, FriendObject.class, true);
			TableUtils.dropTable(connectionSource, ClueImageObject.class, true);
			TableUtils.dropTable(connectionSource, ClueObject.class, true);
			TableUtils.dropTable(connectionSource, EventObjectEx.class, true);
			TableUtils.dropTable(connectionSource, FriendRequestObject.class, true);
			TableUtils.dropTable(connectionSource, EventContent.class, true);
			TableUtils.dropTable(connectionSource, SOSVoice.class, true);
			TableUtils.dropTable(connectionSource, PageInfoObject.class, true);
			TableUtils.dropTable(connectionSource, MsgObject.class, true);
			TableUtils.dropTable(connectionSource, MsgTopicObject.class, true);
			TableUtils.dropTable(connectionSource, PublicClueObject.class, true);
			TableUtils.dropTable(connectionSource, LastMsgIdObject.class, true);
			TableUtils.dropTable(connectionSource, MySOSObject.class, true);

			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (final SQLException e) {
			LogUtils.e(DatabaseHelper.class.getName(), "Can't drop databases" + e);
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
