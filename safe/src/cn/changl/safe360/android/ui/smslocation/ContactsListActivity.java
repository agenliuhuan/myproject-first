package cn.changl.safe360.android.ui.smslocation;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.HandlerUtils.MessageListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ContactsObject;
import cn.changl.safe360.android.contacts.ContactsSyncService;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.contactslist.ContactItemInterface;
import cn.changl.safe360.android.utils.ExtraName;
import cn.changl.safe360.android.utils.PinYinHelper;

public class ContactsListActivity extends BaseExActivity implements TextWatcher, MessageListener {
	private final static String TAG = ContactsListActivity.class.getSimpleName();
	protected TitleBarHolder mTitleBar;

	private CustomContactsListView listview;

	private EditText searchBox;
	private String searchString;

	private Object searchLock = new Object();
	boolean inSearchMode = false;

	List<ContactItemInterface> mContactList;
	List<ContactItemInterface> mFilterList;
	CustomContactsAdapter mAdapter;
	private SearchListTask curSearchTask = null;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, ContactsListActivity.class);
	}

	public static void startActivityForResult(Activity activity, int requestCode) {
		ActivityUtils.startActivityForResult(activity, ContactsListActivity.class, requestCode);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

        initView();
        initData();
    }

    private void initView() {
        mTitleBar = new TitleBarHolder(this);
        mTitleBar.mTitle.setText(R.string.activity_smslocation_contact_title);
        mFilterList = new ArrayList<ContactItemInterface>();
        mContactList = new ArrayList<ContactItemInterface>();

		listview = (CustomContactsListView) this.findViewById(R.id.list_contacts);
		listview.setFastScrollEnabled(true);

		// use this to process individual clicks
		// cannot use OnClickListener as the touch event is overrided by
		// IndexScroller
		// use last touch X and Y if want to handle click for an individual item
		// within the row
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				List<ContactItemInterface> searchList = inSearchMode ? mFilterList : mContactList;

				Intent intent = new Intent();
				intent.putExtra(ExtraName.EN_PHONE, searchList.get(position).getPhone());
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		searchBox = (EditText) findViewById(R.id.edt_contacts_search);
		searchBox.addTextChangedListener(this);
	}

	private void initData() {
		IntentFilter intentFilter = new IntentFilter(ContactsSyncService.ReadingStatus);
		registerReceiver(mContactsReceiver, intentFilter);

		showWaitingDialog();

		if (PinYinHelper.isHashMapEmpty()) {
			PinYinHelper.init(getBaseContext());
		}

		if (ContactsSyncService.isReadComplete()) {
			sendMessage(YSMSG.REQ_READ_PHONE_CONTACTS_LIST, 0, 0, ContactsListActivity.this);
		} else {
			Intent intent = new Intent(this, ContactsSyncService.class);
			startService(intent);
		}

	}

	private void updateList() {
		mAdapter = new CustomContactsAdapter(this, R.layout.list_item_contact_2, mContactList);
		listview.setAdapter(mAdapter);

		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

		searchString = searchBox.getText().toString().trim().toUpperCase();

		if (curSearchTask != null && curSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
			try {
				curSearchTask.cancel(true);
			} catch (Exception e) {
				Log.i(TAG, "Fail to cancel running search task");
			}

		}
		curSearchTask = new SearchListTask();
		curSearchTask.execute(searchString); // put it in a task so that ui is
												// not freeze
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	private class SearchListTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			mFilterList.clear();

			String keyword = params[0];

			inSearchMode = (keyword.length() > 0);

			if (inSearchMode) {
				// get all the items matching this
				for (ContactItemInterface item : mContactList) {
					CustomContactsItem contact = (CustomContactsItem) item;

					if ((contact.getName().toUpperCase().indexOf(keyword) > -1)) {
						mFilterList.add(item);
					}

				}

			}
			return null;
		}

		protected void onPostExecute(String result) {

			synchronized (searchLock) {

				if (inSearchMode) {
					CustomContactsAdapter adapter = new CustomContactsAdapter(ContactsListActivity.this, R.layout.list_item_contact_2, mFilterList);
					adapter.setInSearchMode(true);
					listview.setInSearchMode(true);
					listview.setAdapter(adapter);
				} else {
					CustomContactsAdapter adapter = new CustomContactsAdapter(ContactsListActivity.this, R.layout.list_item_contact_2, mContactList);
					adapter.setInSearchMode(false);
					listview.setInSearchMode(false);
					listview.setAdapter(adapter);
				}
			}

		}
	}

	private BroadcastReceiver mContactsReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ContactsSyncService.ReadingStatus)) {
				sendMessage(YSMSG.REQ_READ_PHONE_CONTACTS_LIST, 0, 0, ContactsListActivity.this);
			}
		}
	};

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_READ_PHONE_CONTACTS_LIST: {
			dismissWaitingDialog();

			if (msg.arg1 == 200) {
				mContactList.clear();
				List<ContactsObject> contactsList = (List<ContactsObject>) msg.obj;
				if (contactsList != null && contactsList.size() > 0) {
					for (ContactsObject contact : contactsList) {
						CustomContactsItem item = new CustomContactsItem(contact.getName(), contact.getPhone(), PinYinHelper.getFirstHanYuPinYin(contact
								.getName()));
						mContactList.add(item);
					}
				}

				updateList();
			}
		}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mContactsReceiver);
	}
}
