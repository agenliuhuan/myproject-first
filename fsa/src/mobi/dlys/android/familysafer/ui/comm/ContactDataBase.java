package mobi.dlys.android.familysafer.ui.comm;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.biz.vo.ContactsObject;
import mobi.dlys.android.familysafer.utils.AES;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
			sqLiteDataBase.execSQL("create table if not exists contact(id INTEGER Primary Key AUTOINCREMENT,name TEXT,phone TEXT);");
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
			sqLiteDataBase.execSQL("INSERT INTO contact(name,phone) values('" + co.getName() + "','" + co.getPhone() + "');");
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
			while (cursor.moveToNext()) {
				String name = cursor.getString(nameFieldColumnIndex);
				String phone = cursor.getString(phoneFieldColumnIndex);
				contact = new ContactsObject();
				contact.setName(name);
				contact.setPhone(phone);
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
		String[] strArray = { "name", "phone" };
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
			while (cursor.moveToNext()) {
				String name = cursor.getString(nameFieldColumnIndex);
				String phone = cursor.getString(phoneFieldColumnIndex);
				ContactsObject contact = new ContactsObject();
				contact.setName(name);
				contact.setPhone(phone);
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

	// private class DataBaseHelper extends SQLiteOpenHelper {
	// public DataBaseHelper(Context context) {
	// this(context,DB_BASE_NAME, null, DB_VERSION);
	// }
	//
	// public DataBaseHelper(Context context, String name, CursorFactory
	// factory, int version) {
	// super(context, name, factory, version);
	// }
	//
	// @Override
	// public void onCreate(SQLiteDatabase db) {
	// db.execSQL("create table if not exists contact(id INTEGER Primary Key AUTOINCREMENT,name TEXT,phone TEXT)");
	// }
	//
	// @Override
	// public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	// {
	// this.deleteDB(db);
	// this.onCreate(db);
	// }
	//
	// private void deleteDB(SQLiteDatabase db) {
	// db.execSQL("drop table if exists contact");
	// }
	// }
}
