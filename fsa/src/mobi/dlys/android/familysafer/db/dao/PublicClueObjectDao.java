package mobi.dlys.android.familysafer.db.dao;

import mobi.dlys.android.familysafer.biz.vo.PublicClueObject;

public class PublicClueObjectDao extends AbstractDao<PublicClueObject> {

	public PublicClueObjectDao() {
		super();
	}
	public static boolean hasMoreClue(int count) {
		if (count == new PublicClueObjectDao().count()) {
			return false;
		}
		return true;
	}

}
