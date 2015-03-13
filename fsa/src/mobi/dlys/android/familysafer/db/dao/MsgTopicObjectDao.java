package mobi.dlys.android.familysafer.db.dao;

import mobi.dlys.android.familysafer.biz.vo.MsgTopicObject;

public class MsgTopicObjectDao extends AbstractDao<MsgTopicObject> {

	public MsgTopicObjectDao() {
		super();
	}

	public static boolean hasMoreMsgTopic(int count) {
		if (count == new MsgTopicObjectDao().count()) {
			return false;
		}
		return true;
	}

}
