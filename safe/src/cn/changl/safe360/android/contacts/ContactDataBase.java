package cn.changl.safe360.android.contacts;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.changl.safe360.android.biz.vo.ContactsObject;

public class ContactDataBase {
	// private final static String DB_BASE_NAME = "/contacts.db";
	// private final static int DB_VERSION = 1;
	private Context context;
	private SQLiteDatabase sqLiteDataBase;

	public ContactDataBase(Context context) {
		this.context = context;
		sqLiteDataBase = context.openOrCreateDatabase("contacts.db", Context.MODE_PRIVATE, null);

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
			sqLiteDataBase.execSQL("drop table if exists contact;");
			sqLiteDataBase
					.execSQL("create table if not exists contact(id INTEGER Primary Key AUTOINCREMENT,name TEXT,phone TEXT,type INTEGER,userid INTEGER);");
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
			String sql = "select count(*) from sqlite_master where type ='table' and name ='contact';";
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

	public void dropTable() {
		try {
			if (sqLiteDataBase == null) {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertRow(ContactsObject co) {
		if (sqLiteDataBase == null) {
			return;
		}
		try {
			// LogUtils.i("Phone", co.getPhone());
			sqLiteDataBase.execSQL("INSERT INTO contact(name,phone,type,userid) values('" + co.getName() + "','" + co.getPhone() + "','" + co.getType() + "','"
					+ co.getUserId() + "');");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateRegisterRow(String phone, int userid) {
		if (sqLiteDataBase == null) {
			return;
		}
		try {
			ContentValues cv = new ContentValues();// 实例化ContentValues
			cv.put("userid", userid);
			cv.put("type", 2);// 添加要更改的字段及内容
			String whereClause = "phone=?";// 修改条件
			String[] whereArgs = { phone };// 修改条件的参数
			sqLiteDataBase.update("contact", cv, whereClause, whereArgs);// 执行修改

			// LogUtils.i("Phone", co.getPhone());
			// sqLiteDataBase.execSQL("update contact set type=2 where phone='"
			// + phone + "'");
			// sqLiteDataBase.execSQL("update contact set userid=" + userid +
			// " where phone='" + phone + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateFriendRow(String phone, int userid) {
		if (sqLiteDataBase == null) {
			return;
		}
		try {
			ContentValues cv = new ContentValues();// 实例化ContentValues
			cv.put("userid", userid);
			cv.put("type", 3);// 添加要更改的字段及内容
			String whereClause = "phone=?";// 修改条件
			String[] whereArgs = { phone };// 修改条件的参数
			sqLiteDataBase.update("contact", cv, whereClause, whereArgs);// 执行修改
			// LogUtils.i("Phone", co.getPhone());
			// sqLiteDataBase.execSQL("update contact set type=3 where phone='"
			// + phone + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean phoneIsExists(String phoneNumber) {
		Cursor cursor = null;
		if (sqLiteDataBase == null) {
			return false;
		}
		try {
			cursor = sqLiteDataBase.rawQuery("SELECT * FROM contact where phone = '" + phoneNumber + "' LIMIT 1;", null);
			// int nameFieldColumnIndex = cursor.getColumnIndex("name");
			// int phoneFieldColumnIndex = cursor.getColumnIndex("phone");
			if (cursor.moveToNext()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return false;
	}

	public ContactsObject query(String phoneNumber) {
		ContactsObject contact = null;
		Cursor cursor = null;
		if (sqLiteDataBase == null) {
			return contact;
		}
		try {
			cursor = sqLiteDataBase.rawQuery("SELECT * FROM contact where phone = '" + phoneNumber + "' LIMIT 1;", null);
			int nameFieldColumnIndex = cursor.getColumnIndex("name");
			int phoneFieldColumnIndex = cursor.getColumnIndex("phone");
			int typeFieldColumnIndex = cursor.getColumnIndex("type");
			int useridFieldColumnIndex = cursor.getColumnIndex("userid");
			while (cursor.moveToNext()) {
				String name = cursor.getString(nameFieldColumnIndex);
				String phone = cursor.getString(phoneFieldColumnIndex);
				int type = cursor.getInt(typeFieldColumnIndex);
				int userid = cursor.getInt(useridFieldColumnIndex);
				contact = new ContactsObject();
				contact.setName(name);
				contact.setPhone(phone);
				contact.setType(type);
				contact.setUserId(userid);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return contact;
	}

	public List<ContactsObject> query() {
		String[] strArray = { "name", "phone", "type", "userid" };
		List<ContactsObject> listContacts = null;
		Cursor cursor = null;
		if (sqLiteDataBase == null) {
			return listContacts;
		}

		try {
			listContacts = new ArrayList<ContactsObject>();
			// cursor=sqLiteDataBase.rawQuery("select * from contact", null);
			cursor = sqLiteDataBase.query("contact", strArray, null, null, null, null, null, null);
			int nameFieldColumnIndex = cursor.getColumnIndex("name");
			int phoneFieldColumnIndex = cursor.getColumnIndex("phone");
			int typeFieldColumnIndex = cursor.getColumnIndex("type");
			int useridFieldColumnIndex = cursor.getColumnIndex("userid");
			while (cursor.moveToNext()) {
				String name = cursor.getString(nameFieldColumnIndex);
				String phone = cursor.getString(phoneFieldColumnIndex);
				int type = cursor.getInt(typeFieldColumnIndex);
				int userid = cursor.getInt(useridFieldColumnIndex);
				ContactsObject contact = new ContactsObject();
				contact.setName(name);
				contact.setPhone(phone);
				contact.setType(type);
				contact.setUserId(userid);
				listContacts.add(contact);
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
