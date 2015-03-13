package mobi.dlys.android.familysafer.db.dao;

import mobi.dlys.android.familysafer.biz.vo.FriendRequestObject;

public class FriendRequestObjectDao extends AbstractDao<FriendRequestObject> {

	public FriendRequestObjectDao() {
		super();
	}

	public static boolean hasMoreFriendRequest(int count) {
		if (count == new FriendRequestObjectDao().count()) {
			return false;
		}
		return true;
	}
}
