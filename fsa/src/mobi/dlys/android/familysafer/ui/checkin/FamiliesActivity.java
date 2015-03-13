package mobi.dlys.android.familysafer.ui.checkin;

import java.util.ArrayList;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.checkin.FamiliesActivity.FamiliesAdapter.ViewHolder;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FamiliesActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	ListView mListview = null;
	ArrayList<FriendObject> mFamiliesList = null;
	FamiliesAdapter mFamiliesAdapter = null;
	ArrayList<Integer> mSelectedList = null;
	Button mSend = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_families);

		initView();

		initData();

	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_families_ttb_title);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mListview = (ListView) this.findViewById(R.id.lv_families);
		mFamiliesList = new ArrayList<FriendObject>();

		mFamiliesAdapter = new FamiliesAdapter(this);
		mListview.setAdapter(mFamiliesAdapter);
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ViewHolder holder = (ViewHolder) arg1.getTag();
				holder.ckbStatus.toggle();
			}
		});

		mListview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

		mSelectedList = new ArrayList<Integer>();

		mSend = (Button) findViewById(R.id.btn_families_send);
		mSend.setBackgroundResource(R.drawable.btn_disable);
		mSend.setEnabled(false);
		mSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(FamiliesActivity.this, SendCheckActivity.class);
				intent.putExtra("all", 0);
				intent.putIntegerArrayListExtra("data", mSelectedList);
				startActivityForResult(intent, CheckinActivity.SendCheck_Some_Action_Id);
			}
		});

	}

	private void initData() {
		sendEmptyMessage(YSMSG.REQ_GET_FRIEND_LIST);
		showWaitingDialog();
	}

	@Override
	protected void onDestroy() {
		if (null != mFamiliesList) {
			mFamiliesList.clear();
		}
		super.onDestroy();
	}

	void updateList() {
		if (null != mFamiliesAdapter) {
			mFamiliesAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_GET_FRIEND_LIST: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				if (CoreModel.getInstance().getFriendList().size() > 0) {
					mFamiliesList.clear();
					mFamiliesList.addAll(CoreModel.getInstance().getFriendList());
				}
			} else {
				// failed
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
			updateList();
		}
			break;
		}
	}

	public class FamiliesAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;

		public FamiliesAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return mFamiliesList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (null == mInflater) {
				mInflater = LayoutInflater.from(mContext);
			}

			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_families, null);

				holder.imgFamilies = (ImageView) convertView.findViewById(R.id.img_families_image);
				holder.txtName = (TextView) convertView.findViewById(R.id.tv_families_name);
				holder.ckbStatus = (CheckBox) convertView.findViewById(R.id.ckb_families_check);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position >= 0 && position < mFamiliesList.size()) {
				FriendObject family = mFamiliesList.get(position);
				if (family != null) {
					String imageFile = family.getImage();
					if (!TextUtils.isEmpty(imageFile)) {
						ImageLoaderHelper.displayImage(imageFile, holder.imgFamilies, R.drawable.user, true);
					} else {
						holder.imgFamilies.setImageResource(R.drawable.user);
					}
					if (TextUtils.isEmpty(family.getRemarkName())) {
						holder.txtName.setText(family.getNickname());
					} else {
						holder.txtName.setText(family.getRemarkName());
					}

					int status = family.getStatus();
					if (-1 == status) {
						holder.ckbStatus.setVisibility(View.GONE);
					} else if (0 == status) {
						holder.ckbStatus.setChecked(false);
					} else if (1 == status) {
						holder.ckbStatus.setChecked(true);
					}
					holder.ckbStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						public void onCheckedChanged(CompoundButton arg0, boolean ischeck) {
							FriendObject fo = mFamiliesList.get(position);
							if (null != fo) {
								if (ischeck) {
									fo.setStatus(1);
									mSelectedList.add(fo.getUserId());
								} else {
									fo.setStatus(0);
									removeUserid(fo.getUserId());
								}
							}
							if (mSelectedList.size() > 0) {
								mSend.setBackgroundResource(R.drawable.button_green_selector);
								mSend.setEnabled(true);
							} else {
								mSend.setBackgroundResource(R.drawable.btn_disable);
								mSend.setEnabled(false);
							}
						}
					});
				}
			}

			return convertView;
		}

		public void removeUserid(int userid) {
			for (int i = 0; i < mSelectedList.size(); i++) {
				int id = mSelectedList.get(i);
				if (id == userid) {
					mSelectedList.remove(i);
				}
			}
		};

		class ViewHolder {
			public ImageView imgFamilies;
			public TextView txtName;
			public CheckBox ckbStatus;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CheckinActivity.SendCheck_Some_Action_Id) {
			if (resultCode == CheckinActivity.SendCheck_Some_Action_Id_Result) {
				finish();
			}
		}
	}
}
