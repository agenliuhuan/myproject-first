package mobi.dlys.android.familysafer.db.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import mobi.dlys.android.familysafer.biz.vo.MsgObject;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

public class MsgObjectDao extends AbstractDao<MsgObject> {

	public MsgObjectDao() {
		super();
	}

	public int count(int friendId) {
		try {
			QueryBuilder query = dbHelper.getDao(entityClass).queryBuilder();
			query.orderBy("createTime", false).where().eq("fromUser", friendId).or().eq("toUser", friendId);
			List<MsgObject> list = dbHelper.getDao(entityClass).query(query.prepare());
			return list.size();
		} catch (SQLException e) {
		} catch (Exception e) {
		}
		return 0;
	}

	public List<MsgObject> findPage(int showedCount, int pageSize, int friendId) {
		List<MsgObject> list = null;
		try {
			QueryBuilder query = dbHelper.getDao(entityClass).queryBuilder();
			query.orderBy("id", false).where().in("fromUser", friendId).or().eq("toUser", friendId);
			query.offset((long) showedCount).limit((long) pageSize);
			list = dbHelper.getDao(entityClass).query(query.prepare());
		} catch (SQLException e) {
		} catch (Exception e) {
		} finally {

		}
		return list;
	}

	public List<MsgObject> findAll(int friendId) {
		List<MsgObject> list = null;
		try {
			QueryBuilder query = dbHelper.getDao(entityClass).queryBuilder();
			query.orderBy("id", true).where().eq("fromUser", friendId).or().eq("toUser", friendId);
			list = dbHelper.getDao(entityClass).query(query.prepare());
		} catch (SQLException e) {
		} catch (Exception e) {
		} finally {

		}
		return list;
	}

	public void updateMsgId(MsgObject msgObject) {
		try {
			UpdateBuilder update = dbHelper.getDao(entityClass).updateBuilder();
			update.where().eq("voiceFilePath", msgObject.getVoiceFilePath());
			update.updateColumnValue("msgId", msgObject.getMsgId());
			update.updateColumnValue("status", msgObject.getStatus());
			update.updateColumnValue("voice", msgObject.getVoice());
			update.updateColumnValue("createTime", msgObject.getCreateTime());
			dbHelper.getDao(entityClass).update(update.prepare());
		} catch (SQLException e) {
		} catch (Exception e) {
		}
	}

	private void updateLocalMsg(Dao<MsgObject, ?> dao, MsgObject msgObject) {
		try {
			UpdateBuilder update = dao.updateBuilder();
			update.where().eq("msgId", msgObject.getMsgId());
			update.updateColumnValue("status", msgObject.getStatus());
			update.updateColumnValue("voice", msgObject.getVoice());
			update.updateColumnValue("createTime", msgObject.getCreateTime());
			update.updateColumnValue("voiceFilePath", "");
			dao.update(update.prepare());
		} catch (SQLException e) {
		} catch (Exception e) {
		}
	}

	public boolean isExistWithMsgId(Dao<MsgObject, ?> dao, int msgId) {
		List<MsgObject> list = null;
		try {
			QueryBuilder<MsgObject, ?> query = dao.queryBuilder();
			query.where().eq("msgId", msgId);
			list = dao.query(query.prepare());
			if (list.size() > 0) {
				return true;
			}
		} catch (Exception e) {
		}

		return false;
	}

	public void updateAllinBatchOperation(final List<MsgObject> t) throws Exception {
		try {
			final Dao<MsgObject, ?> dao = dbHelper.getDao(entityClass);
			dao.callBatchTasks(new Callable<Boolean>() {
				public Boolean call() throws Exception {
					for (MsgObject t2 : t) {
						if (isExistWithMsgId(dao, t2.getMsgId())) {
							updateLocalMsg(dao, t2);
						} else {
							dao.createOrUpdate(t2);
						}
					}
					return true;
				}
			});

		} catch (SQLException e) {
		} catch (Exception e) {
		} finally {

		}
	}

	public void resetMsgStatus() {
		try {
			UpdateBuilder update = dbHelper.getDao(entityClass).updateBuilder();
			update.where().eq("status", 0);
			update.updateColumnValue("status", 2);
			dbHelper.getDao(entityClass).update(update.prepare());
		} catch (SQLException e) {
		} catch (Exception e) {
		}
	}
}
