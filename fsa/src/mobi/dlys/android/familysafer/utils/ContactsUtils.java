package mobi.dlys.android.familysafer.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactsUtils {
	
	/*
	 * 返回Map<String, String> key是phonenum,value是name
	 */
	public static Map<String, String> getContact(Context context) {
		Map<String, String> map = new HashMap<String, String>();
		Cursor cur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, "mimetype='vnd.android.cursor.item/phone_v2'", null, null);
		while (cur.moveToNext()) {
			int displayNameColumn = cur.getColumnIndex("display_name");
			int phone = cur.getColumnIndex("data1");
			String name = cur.getString(displayNameColumn);
			String phonenum = cur.getString(phone);
			if (phonenum.startsWith("+86")) {
				phonenum = phonenum.replace("+86", "").trim();
			}
			phonenum = phonenum.replace("-", "");
			phonenum = phonenum.replace(" ", "");
			phonenum = phonenum.trim();
			map.put(phonenum, name);
		}
		if (cur != null && !cur.isClosed()) {
			cur.close();
		}
		return map;
	}
	/*
	 * 备用的
	 */
	private void getnewContact(Context context){
		HashMap<String, String> map = new HashMap<String, String>();
		Cursor cur =context. getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		while (cur.moveToNext()) {
			int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
			int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			String contactId = cur.getString(idColumn);
			String disPlayName = cur.getString(displayNameColumn);
			int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			if (phoneCount > 0) {
				Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
				while (phones.moveToNext()) {
					String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					map.put(phoneNumber, disPlayName);
				}
				phones.close();
			}
		}
		cur.close();
	}
}
