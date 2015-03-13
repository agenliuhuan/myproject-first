package mobi.dlys.android.familysafer.ui.communication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.MsgTopicObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.db.dao.FriendObjectDao;
import mobi.dlys.android.familysafer.db.dao.MsgTopicObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView.IXListViewListener;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import mobi.dlys.android.familysafer.utils.DateUtils;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TidingFragment extends BaseExFragment {
	protected TitleBarHolder mTitleBar;
	private XListView mListview = null;
	private ArrayList<MsgTopicObject> mMyfamilyList = null;
	private RelativeLayout mNoTidingsRL = null;
	private TidingsAdapter mAdapter = null;
	private boolean mUpdateData = true;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			displayTip();
			if (CoreModel.getInstance().isUpdateMsgList()) {
				CoreModel.getInstance().setUpdateMsgList(false);
				mListview.startRefresh();
			}
		} else {
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_tidings, null);
		initSubView();
		initData();
		return mRootView;
	}

	private void initData() {
		displayTip();

		mListview.showFooterView(false);
		mListview.setPullLoadEnable(false);
		mListview.setPullRefreshEnable(true);
		mListview.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				// 下拉刷新
				sendEmptyMessage(YSMSG.REQ_GET_MSG_TOPIC_LIST);
			}

			@Override
			public void onLoadMore() {
				// 上拉加载更多
				PageInfoObject pageInfo = PageInfoObjectDao.getNextCachePageNo(PageInfoObjectDao.ID_IM_MSG_TOPIC);
				if (pageInfo != null) {
					if (pageInfo.isLastPage() && !MsgTopicObjectDao.hasMoreMsgTopic(mMyfamilyList.size())) {
						YSToast.showToast(getActivity(), R.string.toast_no_more_data);
						mListview.stopLoadMore();
						mListview.setPullLoadEnable(false);
					} else {
						sendMessage(YSMSG.REQ_GET_CACHE_MSG_TOPIC_LIST, pageInfo.getReadCachePageNo(), 0, pageInfo);
					}
				}
			}
		});

		mListview.setVisibility(View.VISIBLE);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mListview.startRefresh();
			}
		}, 200);
	}

	void updateList() {
		if (null != mAdapter) {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void initSubView() {
		mTitleBar = new TitleBarHolder(getActivity(), mRootView);
		mTitleBar.mTitle.setText(R.string.fragment_tidings_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MainActivity) getActivity()).toggle();
			}
		});
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		mMyfamilyList = new ArrayList<MsgTopicObject>();

		mListview = (XListView) this.findViewById(R.id.lv_tidings);
		mListview.setVisibility(View.GONE);

		mListview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mListview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				MsgTopicObject msgTopic = null;
				if (position - 1 >= 0 && position - 1 < mMyfamilyList.size()) {
					msgTopic = mMyfamilyList.get(position - 1);
				}
				if (null != msgTopic) {
					CoreModel.getInstance().setMsgCount(CoreModel.getInstance().getMsgCount() - msgTopic.getUnReadCount());
					displayTip();
					((MainActivity) getActivity()).setMsgCount(CoreModel.getInstance().getMsgCount());
					Intent intent = new Intent();
					intent.setClass(getActivity(), CommunicationActivity.class);
					intent.putExtra("userid", msgTopic.getUserId());
					intent.putExtra("nickname", msgTopic.getNickname());
					intent.putExtra("avatar", msgTopic.getImage());
					if (msgTopic.getUnReadCount() > 0) {
						intent.putExtra("newmsg", true);
					}
					startActivity(intent);
				}
			}
		});

		mAdapter = new TidingsAdapter();

		mListview.setAdapter(mAdapter);

		mNoTidingsRL = (RelativeLayout) findViewById(R.id.no_tidingsRL);
		mNoTidingsRL.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mIsFragmentVisible && !mUpdateData) {
			mListview.startRefresh();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	public void displayTip() {
		mTitleBar.displayTip();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_GET_MSG_TOPIC_LIST:
		case YSMSG.RESP_GET_CACHE_MSG_TOPIC_LIST: {
			mUpdateData = false;
			mListview.stopLoadMore();
			mListview.stopRefresh();
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				if (msg.arg2 == 1) {
					mMyfamilyList.clear();
				}
				List<MsgTopicObject> friendlist = (List<MsgTopicObject>) msg.obj;
				if (null != friendlist) {
					mMyfamilyList.addAll(friendlist);
					updateList();
				}
				mListview.setPullLoadEnable(!PageInfoObjectDao.isLastPage(PageInfoObjectDao.ID_IM_MSG_TOPIC)
						|| MsgTopicObjectDao.hasMoreMsgTopic(mMyfamilyList.size()));

				if (mMyfamilyList.size() > 0) {
					mNoTidingsRL.setVisibility(View.GONE);
					mListview.setVisibility(View.VISIBLE);
				} else {
					mNoTidingsRL.setVisibility(View.VISIBLE);
					mListview.setVisibility(View.GONE);
				}
			} else {
				// failed
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(getActivity(), result.getErrorMsg());
				} else {
					YSToast.showToast(getActivity(), R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_GET_NEW_MSG_NUM:
			if (msg.arg1 == 200) {
				displayTip();

				int msgCount = CoreModel.getInstance().getMsgCount();
				if (msgCount > 0 && !mPause && mIsFragmentVisible && !mUpdateData) {
					mListview.startRefresh();
				}
			}
			break;
		}
	}

	class TidingsAdapter extends BaseAdapter {

		public int getCount() {
			return mMyfamilyList.size();
		}

		public MsgTopicObject getItem(int position) {
			return mMyfamilyList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_tiding, null);
				holder.imgTiding = (ImageView) convertView.findViewById(R.id.img_tiding_item);
				holder.txtName = (TextView) convertView.findViewById(R.id.item_tiding_name);
				holder.tip = (TextView) convertView.findViewById(R.id.item_tiding_tip);
				holder.date = (TextView) convertView.findViewById(R.id.item_tiding_date);
				holder.num = (TextView) convertView.findViewById(R.id.item_tiding_num);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MsgTopicObject family = mMyfamilyList.get(position);
			if (family != null) {
				String imageFile = family.getImage();
				if (!TextUtils.isEmpty(imageFile)) {
					ImageLoaderHelper.displayImage(imageFile, holder.imgTiding, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
				} else {
					holder.imgTiding.setImageResource(R.drawable.user);
				}
				FriendObject friendObject = new FriendObjectDao().findById(family.getUserId());
				if (null != friendObject && !TextUtils.isEmpty(friendObject.getRemarkName())) {
					holder.txtName.setText(friendObject.getRemarkName());
				} else {
					holder.txtName.setText(family.getNickname());
				}
				holder.tip.setText("[" + getString(R.string.item_tiding_tip) + "]");
				int num = family.getUnReadCount();
				if (num == 0) {
					holder.num.setVisibility(View.GONE);
				} else if (num > 99) {
					holder.num.setVisibility(View.VISIBLE);
					holder.num.setText("99");
				} else {
					holder.num.setVisibility(View.VISIBLE);
					holder.num.setText(family.getUnReadCount() + "");
				}

				long time = String2Time(family.getLastTime());
				holder.date.setText(DateUtils.getRelativeDateTimeString(time));
			}

			return convertView;
		}

		public final class ViewHolder {
			public ImageView imgTiding;
			public TextView txtName;
			public TextView tip;
			public TextView date;
			public TextView num;

		}
	}

	String DateFormat = "yy-MM-dd HH:mm:ss";

	private long String2Time(String text) {
		long time = 0;
		try {
			time = new SimpleDateFormat(DateFormat).parse(text).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}
}
