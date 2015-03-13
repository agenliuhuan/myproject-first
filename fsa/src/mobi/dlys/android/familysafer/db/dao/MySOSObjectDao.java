package mobi.dlys.android.familysafer.db.dao;

import mobi.dlys.android.familysafer.biz.vo.MySOSObject;

public class MySOSObjectDao extends AbstractDao<MySOSObject> {

	public MySOSObjectDao() {
		super();
	}

	public static boolean hasMoreSOS(int count) {
		if (count == new MySOSObjectDao().count()) {
			return false;
		}
		return true;
	}

}
