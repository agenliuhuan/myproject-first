package cn.changl.safe360.android.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.changl.safe360.android.biz.vo.MessageObject;

public class Safe360MessageDataBase {
	// private final static String DB_BASE_NAME = "/contacts.db";
	// private final static int DB_VERSION = 1;
	private Context context;
	private SQLiteDatabase sqLiteDataBase;

	public Safe360MessageDataBase(Context context) {
		this.context = context;
		sqLiteDataBase = context.openOrCreateDatabase("message.db", Context.MODE_PRIVATE, null);
		sqLiteDataBase
				.execSQL("create table if not exists message(id INTEGER Primary Key AUTOINCREMENT,type INTEGER,title TEXT,description TEXT,data TEXT,time TEXT);");
	}

	public void close() {
		if (null != sqLiteDataBase) {
			sqLiteDataBase.close();
		}
	}

	public void createTable() {
		try {
			if (sqLiteDataBase == null) {
				return;
			}
			// sqLiteDataBase.delete("contact", null, null);
			// sqLiteDataBase.execSQL("drop table if exists message;");
			sqLiteDataBase
					.execSQL("create table if not exists message(id INTEGER Primary Key AUTOINCREMENT,type INTEGER,title TEXT,description TEXT,data TEXT,time TEXT);");
			// sqLiteDataBase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name = contact;");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isExitsTable() {
		Cursor cursor = null;
		boolean result = false;
		try {
			if (sqLiteDataBase == null) {
				return false;
			}
			String sql = "select count(*) from sqlite_master where type ='table' and name ='message';";
			cursor = sqLiteDataBase.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}

	public void insertRow(MessageObject msg) {
		if (sqLiteDataBase == null) {
			return;
		}
		try {
			sqLiteDataBase.execSQL("INSERT INTO message(type,title,description,data,time) values('" + msg.getType() + "','" + msg.getTitle() + "','"
					+ msg.getDescription() + "','" + msg.getData() + "','" + msg.getTime() + "');");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<MessageObject> query() {
		String[] strArray = { "type", "title", "description", "data", "time" };
		List<MessageObject> listContacts = null;
		Cursor cursor = null;
		if (sqLiteDataBase == null) {
			return listContacts;
		}

		try {
			listContacts = new ArrayList<MessageObject>();
			cursor = sqLiteDataBase.query("message", strArray, null, null, null, null, null, null);
			int typeFieldColumnIndex = cursor.getColumnIndex("type");
			int titleFieldColumnIndex = cursor.getColumnIndex("title");
			int descFieldColumnIndex = cursor.getColumnIndex("description");
			int dataFieldColumnIndex = cursor.getColumnIndex("data");
			int timeFieldColumnIndex = cursor.getColumnIndex("time");
			while (cursor.moveToNext()) {
				int type = cursor.getInt(typeFieldColumnIndex);
				String title = cursor.getString(titleFieldColumnIndex);
				String desc = cursor.getString(descFieldColumnIndex);
				String data = cursor.getString(dataFieldColumnIndex);
				String time = cursor.getString(timeFieldColumnIndex);
				MessageObject msg = new MessageObject();
				msg.setType(type);
				msg.setTitle(title);
				msg.setDescription(desc);
				msg.setData(data);
				msg.setTime(time);
				listContacts.add(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return listContacts;
	}
}
