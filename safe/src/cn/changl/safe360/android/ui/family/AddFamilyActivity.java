package cn.changl.safe360.android.ui.family;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobi.dlys.android.core.utils.ActivityUtils;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ContactsObject;
import cn.changl.safe360.android.biz.vo.LocalUserObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.contacts.ContactDataBase;
import cn.changl.safe360.android.contacts.ContactsSyncService;
import cn.changl.safe360.android.db.dao.LocalUserObjectDao;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.DialogHelper;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.utils.PinYinHelper;
import cn.changl.safe360.android.utils.TelephonyUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class AddFamilyActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;
	ListView mListview = null;
	ArrayList<ContactsObject> mContactsList = null;
	ArrayList<ContactsObject> mAllContactsList = null;
	ContactsAdapter mContactsAdapter = null;
	EditText addfamily_et = null;
	View titleView;
	Button clearBtn;
	TextView clearTV;
	RelativeLayout coverlv_addfamily;
	RelativeLayout noContactRL;
	Button noContactBtn;
	TextView noContactTV;
	String mUrl;
	private int mLoadingProgress;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, AddFamilyActivity.class);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfamily);

		initView();

	}

	private void initView() {
		titleView = (View) findViewById(R.id.titlebar_addfamily);
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_addfamily_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		// mTitleBar.mRight.setBackgroundResource(R.drawable.addfamily_titleright_selector);
		// mTitleBar.mRight.setOnClickListener(new OnClickListener() {
		// public void onClick(View arg0) {
		// // SearchAddActivity.startActivity(AddFamilyActivity.this);
		// }
		// });
		clearBtn = (Button) findViewById(R.id.addfamily_clearBtn);
		clearTV = (TextView) findViewById(R.id.addfamily_clear_tv);
		coverlv_addfamily = (RelativeLayout) findViewById(R.id.coverlv_addfamily);
		mListview = (ListView) this.findViewById(R.id.lv_addfamily);
		noContactRL = (RelativeLayout) findViewById(R.id.noContactsRL);
		noContactBtn = (Button) findViewById(R.id.noContacts_SendBtn);
		noContactTV = (TextView) findViewById(R.id.nocontactTV);
		mContactsList = new ArrayList<ContactsObject>();
		mAllContactsList = new ArrayList<ContactsObject>();

		mListview.setSelector(R.drawable.on_item_selected);
		mListview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContactsObject contacts = mContactsList.get(position);

				if (contacts.isRegistered()) {
				} else if (contacts.unRegistered()) {
				}
			}
		});

		mContactsAdapter = new ContactsAdapter(this);
		mListview.setAdapter(mContactsAdapter);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		IntentFilter intentFilter = new IntentFilter(ContactsSyncService.ReadingStatus);
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
					noContactRL.setVisibility(View.GONE);
				}
				if (temp.length() > 0) {
					titleView.setVisibility(View.GONE);
					clearBtn.setVisibility(View.VISIBLE);
					clearTV.setVisibility(View.VISIBLE);
					mContactsList = getNewListByText(temp.toString());
					if (mContactsList.size() > 0) {
						updateContactsList();
						noContactRL.setVisibility(View.GONE);
					} else {
						noContactRL.setVisibility(View.VISIBLE);
					}
					coverlv_addfamily.setVisibility(View.GONE);
				}
				if (temp.length() == 11 && TelephonyUtils.isMobile(temp.toString())) {
					noContactBtn.setVisibility(View.VISIBLE);
					noContactTV.setText(getString(R.string.activity_addfamily_nocontact_text2));
				} else {
					noContactBtn.setVisibility(View.GONE);
					noContactTV.setText(getString(R.string.activity_addfamily_nocontact_text1));
				}
			}
		});
		noContactBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String phoneNum = addfamily_et.getEditableText().toString().trim();
				if (CoreModel.getInstance().getUserInfo() != null) {
					if (CoreModel.getInstance().getUserInfo().getPhone().equals(phoneNum)) {
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_ismyselef));
						return;
					}
				}

				if (!TelephonyUtils.isMobile(phoneNum)) {
					YSToast.showToast(getApplicationContext(), getString(R.string.toast_phone_format_error));
				} else {
					ArrayList<String> list = new ArrayList<String>();
					list.add(addfamily_et.getEditableText().toString());
					sendMessage(YSMSG.REQ_CHECK_USER_REGISTER, 0, 0, list);
					closeInput();
					showWaitingDialog();
				}
			}
		});
		addfamily_et.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (titleView.getVisibility() != View.GONE) {
					titleView.setVisibility(View.GONE);
					coverlv_addfamily.setVisibility(View.VISIBLE);
					noContactRL.setVisibility(View.GONE);
				}
			}
		});

		clearBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				addfamily_et.setText("");
				coverlv_addfamily.setVisibility(View.VISIBLE);
				noContactRL.setVisibility(View.GONE);
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
				noContactRL.setVisibility(View.GONE);
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
				noContactRL.setVisibility(View.GONE);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (titleView.getVisibility() != View.VISIBLE) {
				addfamily_et.setText("");
				closeInput();
				titleView.setVisibility(View.VISIBLE);
				clearBtn.setVisibility(View.GONE);
				clearTV.setVisibility(View.GONE);
				coverlv_addfamily.setVisibility(View.GONE);
				noContactRL.setVisibility(View.GONE);
				mContactsList = mAllContactsList;
				updateContactsList();
			} else {
				AddFamilyActivity.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void closeInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(AddFamilyActivity.this.getCurrentFocus().getWindowToken(), 0);
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

		if (ContactsSyncService.isReadComplete()) {
			// sendMessage(YSMSG.REQ_MATCH_PHONE_CONTACTS_LIST, 200, 0,
			// AddFamilyActivity.this);
			getContacts();
		} else {
			showOrUpdateWaitingDialog(R.string.reading_contacts);
		}
	}

	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ContactsSyncService.ReadingStatus)) {
				// sendMessage(YSMSG.REQ_MATCH_PHONE_CONTACTS_LIST, 200, 0,
				// AddFamilyActivity.this);
				dismissWaitingDialog();
				getContacts();
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
					String smsContent = getResources().getString(R.string.sms_invite);
					smsContent = String.format(smsContent, mUrl/*
																 * PPNetManager.
																 * getInstance
																 * ().
																 * getDownloadPage
																 * ()
																 */);
					LocalUserObjectDao localUserDao = new LocalUserObjectDao();
					if (msgtext.equals(smsContent)) {
						for (ContactsObject co : mContactsList) {
							if (co.getPhone().equals(num)) {
								co.setType(4);
								localUserDao.insert(new LocalUserObject(num));
							}
						}
						updateContactsList();
					}
				}

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
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
		if (null != myBroadcastReceiver) {
			unregisterReceiver(myBroadcastReceiver);
		}
		super.onDestroy();
	}

	private void getContacts() {
		ContactDataBase cb = new ContactDataBase(this);
		List<ContactsObject> list = cb.query();
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
								return -1;
							}
							if (f2.getName().matches("[0-9]*")) {
								return 1;
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
		} else {
			// 匹配联系人失败
			YSToast.showToast(this, R.string.toast_get_contacts_failed);
		}
		updateContactsList();
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
		// case YSMSG.RESP_MATCH_PHONE_CONTACTS_LIST: {
		// dismissWaitingDialog();
		// if (msg.arg1 == 200) {
		// ArrayList<ContactsObject> list = (ArrayList<ContactsObject>) msg.obj;
		// if (list != null) {
		// Comparator<ContactsObject> contactsComparator = new
		// Comparator<ContactsObject>() {
		//
		// @Override
		// public int compare(ContactsObject f1, ContactsObject f2) {
		// if (f1 == null || f2 == null) {
		// if (f1 == null) {
		// return -1;
		// } else {
		// return 1;
		// }
		// } else {
		// if (3 == f1.getType()) {
		// return 1;
		// }
		// if (3 == f2.getType()) {
		// return -1;
		// } else {
		// return f2.getType() - f1.getType();
		// }
		// }
		// }
		// };
		// Collections.sort(list, contactsComparator);
		// contactsComparator = new Comparator<ContactsObject>() {
		//
		// @Override
		// public int compare(ContactsObject f1, ContactsObject f2) {
		// if (f1 == null || f2 == null) {
		// if (f1 == null) {
		// return -1;
		// } else {
		// return 1;
		// }
		// } else {
		// if (f1.getType() == f2.getType()) {
		// if (f1.getName().matches("[a-zA-Z]*")) {
		// return -1;
		// }
		// if (f2.getName().matches("[a-zA-Z]*")) {
		// return 1;
		// }
		// return
		// PinYinHelper.getFirstHanYuPinYin(f1.getName()).compareTo(PinYinHelper.getFirstHanYuPinYin(f2.getName()));
		// } else {
		// return 0;
		// }
		// }
		// }
		// };
		// Collections.sort(list, contactsComparator);
		// contactsComparator = new Comparator<ContactsObject>() {
		//
		// @Override
		// public int compare(ContactsObject f1, ContactsObject f2) {
		// if (f1 == null || f2 == null) {
		// if (f1 == null) {
		// return -1;
		// } else {
		// return 1;
		// }
		// } else {
		// if (f1.getType() == f2.getType()) {
		// if (f1.getName().matches("[0-9]*")) {
		// return -1;
		// }
		// if (f2.getName().matches("[0-9]*")) {
		// return 1;
		// }
		// return 0;
		// } else {
		// return 0;
		// }
		// }
		// }
		// };
		// Collections.sort(list, contactsComparator);
		// mContactsList.clear();
		// mAllContactsList.clear();
		// if (null != CoreModel.getInstance().getUserInfo()) {
		// String myPhone = CoreModel.getInstance().getUserInfo().getPhone();
		// for (int i = 0; i < list.size(); ++i) {
		// String tmpPhone = list.get(i).getPhone();
		// if (0 == tmpPhone.compareTo(myPhone)) {
		// list.remove(i);
		// i = -1;
		// }
		// }
		// }
		// mContactsList.addAll(list);
		// mAllContactsList.addAll(list);
		// }
		// } else {
		// // 匹配联系人失败
		// YSToast.showToast(this, R.string.toast_get_contacts_failed);
		// }
		// updateContactsList();
		// }
		// break;
		case YSMSG.RESP_INVITE_FRIEND: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 添加好友成功
				updateContactsList();
				YSToast.showToast(this, R.string.toast_send_add_friend_success);
			} else {
				// 失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_INVITE_TEMP_FRIEND: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				ContactsObject contacts = (ContactsObject) msg.obj;
				if (contacts != null) {
					mUrl = contacts.getUrl();
					String smsContent = getResources().getString(R.string.sms_invite);
					smsContent = String.format(smsContent, contacts.getUrl());
					TelephonyUtils.sendSms(AddFamilyActivity.this, contacts.getPhone(), smsContent, false);
				}
			} else {
				// 失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_MATCH_PHONE_PROGRESS: {
			int progress = msg.arg1;
			int count = msg.arg2;
			if (progress > mLoadingProgress) {
				mLoadingProgress = progress;
			}
			if (progress != 100) {
				simulateLoadingProgress();
			} else {
				stopSimulateLoadingProgress();
				showOrUpdateWaitingDialog(createBufferringText(mLoadingProgress));
			}
		}
			break;
		case YSMSG.RESP_CHECK_USER_REGISTER: {
			if (msg.arg1 == 200) {
				List<UserInfo> userInfoList = (List<UserInfo>) msg.obj;
				if (userInfoList != null) {
					boolean regist = userInfoList.get(0).getRegistStatus();
					if (regist) {
						sendMessage(YSMSG.REQ_INVITE_FRIEND, 0, userInfoList.get(0).getUserId(), null);
					} else {
						dismissWaitingDialog();
						String smsContent = getResources().getString(R.string.sms_invite);
						// YSToast.showToast(this,
						// R.string.toast_usernot_register);
						showInviteDialog(addfamily_et.getEditableText().toString(), smsContent);
					}
				}

			} else {
				dismissWaitingDialog();
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

			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_contact_1, null);

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
				ContactsObject contacts = mContactsList.get(position);
				if (contacts != null) {
					String roottext = contacts.getName() + " (" + contacts.getPhone() + ")";
					SpannableString msp = setTextColor(roottext, addfamily_et.getEditableText().toString());
					holder.txtName.setText(msp);
					if (contacts.isRegistered()) {
						holder.btnAddinvite.setVisibility(View.VISIBLE);
						holder.sendokLL.setVisibility(View.GONE);
						holder.btnAddinvite.setBackgroundResource(R.drawable.btn_addfamily_selector);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_add);
						// holder.imgStatus.setImageResource(R.drawable.addfamily_isregist);
					} else if (contacts.isInvited()) {
						holder.btnAddinvite.setVisibility(View.GONE);
						holder.sendokLL.setVisibility(View.VISIBLE);
						holder.sendokTv.setText(R.string.activity_addfamily_tv_invited);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_invite);
						// holder.imgStatus.setImageResource(R.drawable.addfamily_unregist);
					} else if (contacts.isAdded()) {
						holder.btnAddinvite.setVisibility(View.GONE);
						holder.sendokLL.setVisibility(View.VISIBLE);
						holder.sendokTv.setText(R.string.activity_addfamily_itemtv_added);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_add);
						// holder.imgStatus.setImageResource(R.drawable.addfamily_isregist);
					} else if (contacts.isFriend()) {
						holder.btnAddinvite.setVisibility(View.GONE);
						holder.sendokLL.setVisibility(View.GONE);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_added);
						holder.imgStatus.setImageResource(R.drawable.addfamily_isregist);
					} else {
						holder.btnAddinvite.setVisibility(View.VISIBLE);
						holder.sendokLL.setVisibility(View.GONE);
						holder.btnAddinvite.setBackgroundResource(R.drawable.btn_invatefamily_selector);
						holder.txtDesc.setText(R.string.activity_addfamily_tv_invite);
						// holder.imgStatus.setImageResource(R.drawable.addfamily_unregist);
					}
					OnClick listener = new OnClick();
					listener.setPosition(position);
					holder.btnAddinvite.setOnClickListener(listener);
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

		class OnClick implements OnClickListener {
			int position;

			public void setPosition(int position) {
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				ContactsObject contacts = mContactsList.get(position);
				if (contacts.isRegistered()) {
					showWaitingDialog();
					sendMessage(YSMSG.REQ_INVITE_FRIEND, 0, contacts.getUserId(), null);
					contacts.setType(5);
				} else if (contacts.unRegistered()) {
					showWaitingDialog();
					sendMessage(YSMSG.REQ_INVITE_TEMP_FRIEND, 0, 0, contacts.getPhone());
					// String smsContent =
					// getResources().getString(R.string.sms_invite);
					// smsContent = String.format(smsContent,
					// PPNetManager.getInstance().getDownloadPage());
					// TelephonyUtils.sendSms(AddFamilyActivity.this,
					// contacts.getPhone(), smsContent, false);
				}
				updateContactsList();
			}
		}
	}

	private void showInviteDialog(final String phone, final String smscontent) {
		DialogHelper.showTwoDialog(AddFamilyActivity.this, false, null, getString(R.string.dialog_invitesms_content), getString(R.string.dialog_invitesms_yes),
				getString(R.string.dialog_invitesms_no), true, null, new OnClickListener() {
					public void onClick(View v) {
						TelephonyUtils.sendSms(AddFamilyActivity.this, phone, smscontent, false);
					}
				});
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

	private void simulateLoadingProgress() {
		if (isWaitingDialogShowing()) {
			stopSimulateLoadingProgress();
			mHandler.postDelayed(mProgressRunnale, 1000);
		}
	}

	private void stopSimulateLoadingProgress() {
		mHandler.removeCallbacks(mProgressRunnale);
	}

	Runnable mProgressRunnale = new Runnable() {

		@Override
		public void run() {
			int[] delays = new int[] { 1000, 2000 };
			int[] increases = new int[] { 2, 3, 4, 5 };

			if (mLoadingProgress < 90 && isWaitingDialogShowing()) {
				mLoadingProgress += increases[(int) (Math.random() * increases.length)];
				showOrUpdateWaitingDialog(createBufferringText(mLoadingProgress));
				mHandler.postDelayed(this, delays[(int) (Math.random() * delays.length)]);
			}
		}
	};

	private String createBufferringText(int progress) {
		if (progress > 100) {
			progress = 100;
		}
		return getString(R.string.matching_contacts) + " (" + progress + "%)";
	}
}
