package mobi.dlys.android.familysafer.ui.family;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.api.PPNetManager;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ContactsObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.service.ReadContactsService;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.utils.PinYinHelper;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddFamily2Activity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;
	ListView mListview = null;
	ArrayList<ContactsObject> mContactsList = null;
	ArrayList<ContactsObject> mAllContactsList = null;
	ContactsAdapter mContactsAdapter = null;
	EditText addfamily_et = null;
	public static ContactsObject contacts = null;
	View titleView;
	Button clearBtn;
	TextView clearTV;
	RelativeLayout coverlv_addfamily;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfamily);
		initView();
		initData();
	}

	private void initView() {
		titleView = (View) findViewById(R.id.titlebar_addfamily);
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_addfamily_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setBackgroundResource(R.drawable.addfamily_search_selector);
		mTitleBar.mRight.setGravity(Gravity.CENTER);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				AddFamily2Activity.this.finish();
			}
		});
		mTitleBar.mRight.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(AddFamily2Activity.this, SearchAddActivity.class);
				startActivity(intent);
			}
		});
		clearBtn = (Button) findViewById(R.id.addfamily_clearBtn);
		clearTV = (TextView) findViewById(R.id.addfamily_clear_tv);
		mListview = (ListView) this.findViewById(R.id.lv_addfamily);
		coverlv_addfamily = (RelativeLayout) findViewById(R.id.coverlv_addfamily);
		mContactsList = new ArrayList<ContactsObject>();
		mAllContactsList = new ArrayList<ContactsObject>();
		mListview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				contacts = mContactsList.get(position);

				if (contacts.isFriend()) {
					Intent intent = new Intent(AddFamily2Activity.this, FamilyDetailActivity.class);
					intent.putExtra("extra_user_id", contacts.getUserId());
					startActivity(intent);
				} else if (contacts.isRegistered()) {
					Intent intent = new Intent(AddFamily2Activity.this, FamilyIn2Activity.class);
					intent.putExtra("extra_user_id", contacts.getUserId());
					startActivity(intent);
				} else if (contacts.unRegistered()) {
					Intent intent = new Intent(AddFamily2Activity.this, FamilyOut2Activity.class);
					Bundle bundle = new Bundle();
					bundle.putString("name", contacts.getName());
					bundle.putString("phone", contacts.getPhone());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

		mContactsAdapter = new ContactsAdapter(this);
		mListview.setAdapter(mContactsAdapter);
		IntentFilter intentFilter = new IntentFilter(App.readingStatus);
		registerReceiver(myBroadcastReceiver, intentFilter);

		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, new SMSContentObserver(this, new Handler()));

		addfamily_et = (EditText) findViewById(R.id.et_addfamily);
		addfamily_et.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;

			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				temp = arg0;
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void afterTextChanged(Editable arg0) {
				if (null == temp) {
					return;
				}
				if (temp.length() == 0) {
					mContactsList = mAllContactsList;
					updateContactsList();
					coverlv_addfamily.setVisibility(View.VISIBLE);
				}
				if (temp.length() > 0) {
					titleView.setVisibility(View.GONE);
					clearBtn.setVisibility(View.VISIBLE);
					clearTV.setVisibility(View.VISIBLE);
					mContactsList = getNewListByText(temp.toString());
					updateContactsList();
					coverlv_addfamily.setVisibility(View.GONE);
				}
			}
		});
		addfamily_et.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (titleView.getVisibility() != View.GONE) {
					titleView.setVisibility(View.GONE);
					coverlv_addfamily.setVisibility(View.VISIBLE);

				}
			}
		});
		clearBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				addfamily_et.setText("");
				coverlv_addfamily.setVisibility(View.VISIBLE);
			}
		});
		clearTV.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				addfamily_et.setText("");
				closeInput();
				titleView.setVisibility(View.VISIBLE);
				clearBtn.setVisibility(View.GONE);
				clearTV.setVisibility(View.GONE);
				mContactsList = mAllContactsList;
				updateContactsList();
				coverlv_addfamily.setVisibility(View.GONE);
			}
		});
		coverlv_addfamily.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				addfamily_et.setText("");
				closeInput();
				titleView.setVisibility(View.VISIBLE);
				clearBtn.setVisibility(View.GONE);
				clearTV.setVisibility(View.GONE);
				mContactsList = mAllContactsList;
				updateContactsList();
				coverlv_addfamily.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (titleView.getVisibility() != View.VISIBLE) {
				addfamily_et.setText("");
				closeInput();
				titleView.setVisibility(View.VISIBLE);
				clearBtn.setVisibility(View.GONE);
				clearTV.setVisibility(View.GONE);
				coverlv_addfamily.setVisibility(View.GONE);
				mContactsList = mAllContactsList;
				updateContactsList();
			} else {
				AddFamily2Activity.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void closeInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(AddFamily2Activity.this.getCurrentFocus().getWindowToken(), 0);
		}
	}

	private ArrayList<ContactsObject> getNewListByText(String text) {
		ArrayList<ContactsObject> list = new ArrayList<ContactsObject>();
		for (ContactsObject co : mAllContactsList) {
			if (co.getName().contains(text) || PinYinHelper.getFirstHanYuPinYin(co.getName()).contains(text) || co.getPhone().contains(text)) {
				list.add(co);
			}
		}
		return list;
	}

	private void initData() {
		if (PinYinHelper.isHashMapEmpty()) {
			PinYinHelper.init(getBaseContext());
		}
		showWaitingDialog();
		if (ReadContactsService.mContactsReadingStatus == 2) {
			sendMessage(YSMSG.REQ_MATCH_PHONE_CONTACTS_LIST, 200, 0, this); // 200:表示该请求来源于添加好友流程
		}
	}

	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(App.readingStatus)) {
				sendMessage(YSMSG.REQ_MATCH_PHONE_CONTACTS_LIST, 200, 0, AddFamily2Activity.this); // 200:表示该请求来源于添加好友流程
			}
		}
	};

	public class SMSContentObserver extends ContentObserver {
		private Context mContext;
		String[] projection = new String[] { "address", "body", "date", "type", "read" };

		public SMSContentObserver(Context context, Handler handler) {
			super(handler);
			mContext = context;
		}

		@Override
		public void onChange(boolean selfChange) {
			Uri uri = Uri.parse("content://sms/sent");
			Cursor c = mContext.getContentResolver().query(uri, null, null, null, "date desc");
			if (c != null) {
				if (c.moveToFirst()) {
					String msgtext = c.getString(c.getColumnIndex("body"));
					String num = c.getString(c.getColumnIndex("address"));
					c.close();
					String smsContent = getResources().getString(R.string.sms_invite1);
					smsContent += PPNetManager.getInstance().getDownloadPage();
					smsContent += getResources().getString(R.string.sms_invite2);
					if (msgtext.equals(smsContent)) {
						for (ContactsObject co : mContactsList) {
							if (co.getPhone().equals(num)) {
								co.setType(4);
							}
						}
						updateContactsList();
					}
				}

			}
		}
	}

	protected void onResume() {
		super.onResume();
		updateContactsList();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (null != mContactsList) {
			mContactsList.clear();
		}
		unregisterReceiver(myBroadcastReceiver);
		super.onDestroy();
	}

	void updateContactsList() {
		if (null != mContactsAdapter) {
			mContactsAdapter.notifyDataSetChanged();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_MATCH_PHONE_CONTACTS_LIST: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 匹配联系人成功
				ArrayList<ContactsObject> list = (ArrayList<ContactsObject>) msg.obj;
				LogUtils.i("constantssize", " AddFamily2Activity match end:" + list.size());
				if (list != null) {
					Comparator<ContactsObject> contactsComparator = new Comparator<ContactsObject>() {

						@Override
						public int compare(ContactsObject f1, ContactsObject f2) {
							if (f1 == null || f2 == null) {
								if (f1 == null) {
									return -1;
								} else {
									return 1;
								}
							} else {
								if (3 == f1.getType()) {
									return 1;
								}
								if (3 == f2.getType()) {
									return -1;
								} else {
									return f2.getType() - f1.getType();
								}
							}
						}
					};
					Collections.sort(list, contactsComparator);
					contactsComparator = new Comparator<ContactsObject>() {

						@Override
						public int compare(ContactsObject f1, ContactsObject f2) {
							if (f1 == null || f2 == null) {
								if (f1 == null) {
									return -1;
								} else {
									return 1;
								}
							} else {
								if (f1.getType() == f2.getType()) {
									if (f1.getName().matches("[a-zA-Z]*")) {
										return -1;
									}
									if (f2.getName().matches("[a-zA-Z]*")) {
										return 1;
									}
									return PinYinHelper.getFirstHanYuPinYin(f1.getName()).compareTo(PinYinHelper.getFirstHanYuPinYin(f2.getName()));
								} else {
									return 0;
								}
							}
						}
					};
					Collections.sort(list, contactsComparator);
					contactsComparator = new Comparator<ContactsObject>() {

						@Override
						public int compare(ContactsObject f1, ContactsObject f2) {
							if (f1 == null || f2 == null) {
								if (f1 == null) {
									return -1;
								} else {
									return 1;
								}
							} else {
								if (f1.getType() == f2.getType()) {
									if (f1.getName().matches("[0-9]*")) {
										return 1;
									}
									if (f2.getName().matches("[0-9]*")) {
										return -1;
									}
									return 0;
								} else {
									return 0;
								}
							}
						}
					};
					Collections.sort(list, contactsComparator);
					mContactsList.clear();
					mAllContactsList.clear();
					if (null != CoreModel.getInstance().getUserInfo()) {
						String myPhone = CoreModel.getInstance().getUserInfo().getPhone();
						for (int i = 0; i < list.size(); ++i) {
							String tmpPhone = list.get(i).getPhone();
							if (0 == tmpPhone.compareTo(myPhone)) {
								list.remove(i);
								i = -1;
							}
						}
					}

					mContactsList.addAll(list);
					mAllContactsList.addAll(list);
				}
			} else {
				// 匹配联系人失败
				YSToast.showToast(this, R.string.toast_get_contacts_failed);
			}

			updateContactsList();
		}
			break;
		case YSMSG.RESP_ADD_FRIEND: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 添加好友成功
				updateContactsList();
				YSToast.showToast(this, R.string.toast_send_add_friend_success);
			} else {
				// 添加好友失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		}
	}

	private class ContactsAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;

		public ContactsAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return mContactsList.size();
		}

		@Override
		public ContactsObject getItem(int position) {
			return mContactsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (null == mInflater) {
				mInflater = LayoutInflater.from(mContext);
			}

			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_contact, null);

				holder.txtName = (TextView) convertView.findViewById(R.id.tv_item_name);
				holder.imgStatus = (ImageView) convertView.findViewById(R.id.img_item_status);
				holder.txtDesc = (TextView) convertView.findViewById(R.id.tv_item_desc);
				holder.btnAddinvite = (Button) convertView.findViewById(R.id.btn_item_addinvite);
				holder.sendokLL = (LinearLayout) convertView.findViewById(R.id.sendok_LL_item_contact);
				holder.sendokTv = (TextView) convertView.findViewById(R.id.sendok_tv_item_contact);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position >= 0 && position < mContactsList.size()) {
				final ContactsObject contacts = mContactsList.get(position);
				if (contacts != null) {
					String roottext = contacts.getName() + " (" + contacts.getPhone() + ")";
					SpannableString msp = setTextColor(roottext, addfamily_et.getEditableText().toString());
					holder.txtName.setText(msp);
					if (contacts.isFriend()) {
						holder.btnAddinvite.setVisibility(View.GONE);
						holder.sendokLL.setVisibility(View.GONE);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_added);
						holder.imgStatus.setImageResource(R.drawable.sendok);
					} else if (contacts.isRegistered()) {
						holder.btnAddinvite.setVisibility(View.VISIBLE);
						holder.sendokLL.setVisibility(View.GONE);
						holder.btnAddinvite.setText(R.string.activity_addfamily_btn_add);
						holder.btnAddinvite.setBackgroundResource(R.drawable.button_login_selector);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_add);
						holder.imgStatus.setImageResource(R.drawable.addfamily_isregist);
					} else if (contacts.unRegistered()) {
						holder.btnAddinvite.setVisibility(View.VISIBLE);
						holder.sendokLL.setVisibility(View.GONE);
						holder.btnAddinvite.setText(R.string.activity_addfamily_btn_invite);
						holder.btnAddinvite.setBackgroundResource(R.drawable.button_yellow_selector);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_invite);
						holder.imgStatus.setImageResource(R.drawable.addfamily_unregist);
					} else if (contacts.isInvited()) {
						holder.btnAddinvite.setVisibility(View.GONE);
						holder.sendokLL.setVisibility(View.VISIBLE);
						holder.sendokTv.setText(R.string.activity_addfamily_tv_invited);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_invite);
						holder.imgStatus.setImageResource(R.drawable.addfamily_unregist);
					} else if (contacts.isAdded()) {
						holder.btnAddinvite.setVisibility(View.GONE);
						holder.sendokLL.setVisibility(View.VISIBLE);
						holder.sendokTv.setText(R.string.activity_addfamily_itemtv_added);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_add);
						holder.imgStatus.setImageResource(R.drawable.addfamily_isregist);
					}

					holder.btnAddinvite.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							v.setEnabled(false);
							if (contacts.isRegistered()) {
								showWaitingDialog();
								sendMessage(YSMSG.REQ_ADD_FRIEND, contacts.getUserId(), 0, null);
								contacts.setType(5);
							} else if (contacts.unRegistered()) {
								String smsContent = getResources().getString(R.string.sms_invite1);
								smsContent += PPNetManager.getInstance().getDownloadPage();
								smsContent += getResources().getString(R.string.sms_invite2);
								TelephonyUtils.sendSms(AddFamily2Activity.this, contacts.getPhone(), smsContent, false);
							}
						}
					});
				}
			}

			return convertView;
		}

		public final class ViewHolder {
			public TextView txtName;
			public ImageView imgStatus;
			public TextView txtDesc;
			public Button btnAddinvite;
			public LinearLayout sendokLL;
			public TextView sendokTv;
		}
	}

	private SpannableString setTextColor(String roottext, String text) {
		SpannableString msp = new SpannableString(roottext);
		int start = roottext.indexOf(text);
		if (start < 0) {
			start = PinYinHelper.getFirstHanYuPinYin(roottext).indexOf(text);
			if (start < 0) {
				return msp;
			}
		}
		int end = start + text.length();
		msp.setSpan(new ForegroundColorSpan(getBaseContext().getResources().getColor(R.color.green)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return msp;
	}

}
