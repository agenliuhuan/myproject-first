package mobi.dlys.android.familysafer.db.dao;

import java.util.List;

import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;

public class ClueObjectDao extends AbstractDao<ClueObject> {

	public ClueObjectDao() {
		super();
	}

	public static boolean hasMoreClue(int count) {
		if (count == new ClueObjectDao().count()) {
			return false;
		}
		return true;
	}

	public static void getClueObject(List<EventObjectEx> list) {
		if (null == list) {
			return;
		}

		ClueObjectDao clueDao = new ClueObjectDao();
		EventObjectEx eventObject;
		ClueObject clueObject;

		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				eventObject = list.get(i);
				clueObject = clueDao.findById(eventObject.getClueId());
				if (null != clueObject) {
					eventObject.setClue(clueObject);
					ClueImageObjectDao.getClueImage(clueObject);
				}
			}
		}
	}

}
