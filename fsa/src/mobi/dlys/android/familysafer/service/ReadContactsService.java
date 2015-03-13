package mobi.dlys.android.familysafer.service;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.biz.vo.ContactsObject;
import mobi.dlys.android.familysafer.ui.comm.ContactDataBase;
import mobi.dlys.android.familysafer.utils.ContactsUtils;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

public class ReadContactsService extends Service {
	public static int mContactsReadingStatus = 0; // 0:未开始 1:正在读取 2:读取完成

	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {

	}

	public void onStart(Intent intent, int startId) {
		new Thread(new Runnable() {
			public void run() {
				mContactsReadingStatus = 1;
				Cursor cursor = null;
				ContactDataBase cb = null;
				try {
					cb = new ContactDataBase(ReadContactsService.this);
					cb.createTable();
					LogUtils.i("constantssize", "APP readContactAndStore ContactsList Cursorstart");
					Map<String, String> map = ContactsUtils.getContact(ReadContactsService.this);
					LogUtils.i("constantssize", "APP readContactAndStore ContactsList Cursorend:" + map.size());
					Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<String, String> entry = iterator.next();
						String phone = entry.getKey();
						String name = entry.getValue();
						ContactsObject contacts = new ContactsObject();
						contacts.setName(name);
						contacts.setPhone(phone);
						contacts.setType(0);
						cb.insertRow(contacts);
					}
					LogUtils.i("constantssize", "APP readContactAndStore insertend");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (cursor != null && !cursor.isClosed()) {
						cursor.close();
					}
					if (cb != null) {
						cb.close();
					}
				}
				mContactsReadingStatus = 2;
				Intent i = new Intent(App.readingStatus);
				sendBroadcast(i);

				ReadContactsService.this.stopSelf();
			}
		}).start();

	}

	public void onDestroy() {

	}

}
