package mobi.dlys.android.familysafer.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import android.text.TextUtils;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

public class FriendObjectDao extends AbstractDao<FriendObject> {

	public FriendObjectDao() {
		super();
	}

	public static boolean hasMoreFriend(int count) {
		if (count == new FriendObjectDao().count()) {
			return false;
		}
		return true;
	}

	public void resetDeleteFlag() {
		try {
			UpdateBuilder update = dbHelper.getDao(entityClass).updateBuilder();
			update.updateColumnValue("delete", true);
			dbHelper.getDao(entityClass).update(update.prepare());
		} catch (SQLException e) {
		}catch (Exception e) {
		} 
	}

	public void setDeleteFlag(int userId, boolean delete) {
		try {
			UpdateBuilder update = dbHelper.getDao(entityClass).updateBuilder();
			update.where().eq("userId", userId);
			update.updateColumnValue("delete", delete);
			dbHelper.getDao(entityClass).update(update.prepare());
		} catch (SQLException e) {
		}catch (Exception e) {
		} 
	}

	public void clearNotFriends() {
		try {
			DeleteBuilder delete = dbHelper.getDao(entityClass).deleteBuilder();
			delete.where().eq("delete", true);
			dbHelper.getDao(entityClass).delete(delete.prepare());
		} catch (SQLException e) {
		}catch (Exception e) {
		} 
	}

	public boolean isInfoEmpty(int userId) {
		FriendObject friendObject = findById(userId);
		if (friendObject != null && !TextUtils.isEmpty(friendObject.getNickname())) {
			return false;
		}

		return true;
	}

	public List<FriendObject> getAllFriends() {
		List<FriendObject> friendList = new ArrayList<FriendObject>();

		return friendList;
	}

}
