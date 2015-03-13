package mobi.dlys.android.familysafer.db.dao;

import java.sql.SQLException;
import java.util.List;

import mobi.dlys.android.familysafer.biz.vo.ClueImageObject;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.biz.vo.PublicClueObject;

import com.j256.ormlite.stmt.QueryBuilder;

public class ClueImageObjectDao extends AbstractDao<ClueImageObject> {

	public ClueImageObjectDao() {
		super();
	}

	public static void getClueImage(List<ClueObject> list) {
		if (null == list) {
			return;
		}

		ClueImageObjectDao clueImageDao = new ClueImageObjectDao();
		ClueObject clue;
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				clue = list.get(i);
				clue.setImageList(clueImageDao.findAllByClueId(clue.getClueId()));
			}
		}
	}

	public static void getClueImage(ClueObject clueObject) {
		if (null == clueObject) {
			return;
		}

		ClueImageObjectDao clueImageDao = new ClueImageObjectDao();
		clueObject.setImageList(clueImageDao.findAllByClueId(clueObject.getClueId()));
	}

	public static void getPublicClueImage(List<PublicClueObject> list) {
		if (null == list) {
			return;
		}

		ClueImageObjectDao clueImageDao = new ClueImageObjectDao();
		PublicClueObject clue;
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				clue = list.get(i);
				clue.setImageList(clueImageDao.findAllByClueId(clue.getClueId()));
			}
		}
	}

	public List<ClueImageObject> findAllByClueId(Integer id) {
		List<ClueImageObject> list = null;
		try {
			QueryBuilder query = dbHelper.getDao(entityClass).queryBuilder();
			query.orderBy("insertTime", true);
			query.where().eq("clueId", id);
			list = dbHelper.getDao(entityClass).query(query.prepare());
		} catch (SQLException e) {
		} catch (Exception e) {
		}
		return list;
	}
}
