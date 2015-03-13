package mobi.dlys.android.familysafer.db.dao;

import java.sql.SQLException;
import java.util.List;

import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

public class EventObjectDao extends AbstractDao<EventObjectEx> {

    public EventObjectDao() {
        super();
    }

    public static boolean hasMoreEvent(int count) {
        if (count == new EventObjectDao().count()) {
            return false;
        }
        return true;
    }

    public static boolean hasMoreEvent(int count, int eventId) {
        if (count == new EventObjectDao().count(eventId)) {
            return false;
        }
        return true;
    }

    public int count(int eventId) {
        try {
            int type1 = 0;
            int type2 = 0;
            if (eventId == PageInfoObjectDao.ID_EVENT_FRIEND_SOS) {
                type1 = 3;
            } else if (eventId == PageInfoObjectDao.ID_EVENT_FRIEND_CHECKIN) {
                type1 = 1;
            } else if (eventId == PageInfoObjectDao.ID_EVENT_CLUE) {
                type1 = 5;
            } else {
                type1 = 2;
                type2 = 4;
            }
            QueryBuilder query = dbHelper.getDao(entityClass).queryBuilder();
            query.where().eq("type", type1).or().eq("type", type2);
            List<EventObjectEx> list = dbHelper.getDao(entityClass).query(query.prepare());
            return list.size();
        } catch (SQLException e) {
        } catch (Exception e) {
        }
        return 0;
    }

    public List<EventObjectEx> findPage(int eventId, int pageNo, int pageSize) {
        List<EventObjectEx> list = null;
        try {
            int type1 = 0;
            int type2 = 0;
            if (eventId == PageInfoObjectDao.ID_EVENT_FRIEND_SOS) {
                type1 = 3;
            } else if (eventId == PageInfoObjectDao.ID_EVENT_FRIEND_CHECKIN) {
                type1 = 1;
            } else if (eventId == PageInfoObjectDao.ID_EVENT_CLUE) {
                type1 = 5;
            } else {
                type1 = 2;
                type2 = 4;
            }
            QueryBuilder query = dbHelper.getDao(entityClass).queryBuilder();
            query.where().eq("type", type1).or().eq("type", type2);
            query.orderBy("insertTime", true).offset((long) (pageNo - 1) * pageSize).limit((long) pageSize);
            list = dbHelper.getDao(entityClass).query(query.prepare());
        } catch (SQLException e) {
        } catch (Exception e) {
        } finally {

        }
        return list;
    }

    public void clear(int eventId) {
        try {
            int type1 = 0;
            int type2 = 0;
            if (eventId == PageInfoObjectDao.ID_EVENT_FRIEND_SOS) {
                type1 = 3;
            } else if (eventId == PageInfoObjectDao.ID_EVENT_FRIEND_CHECKIN) {
                type1 = 1;
            } else {
                type1 = 2;
                type2 = 4;
            }
            DeleteBuilder db = dbHelper.getDao(entityClass).deleteBuilder();
            db.where().eq("type", type1).or().eq("type", type2);
            db.delete();
        } catch (SQLException e) {
        } catch (Exception e) {
        } finally {

        }
    }
}
