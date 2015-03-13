package mobi.dlys.android.familysafer.ui.communication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.audio.Recorder;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.MsgObject;
import mobi.dlys.android.familysafer.db.dao.MsgObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.player.PlayerManage;
import mobi.dlys.android.familysafer.player.PlayerMusicInfo;
import mobi.dlys.android.familysafer.player.utils.Player;
import mobi.dlys.android.familysafer.player.utils.Player.PlayState;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView.IXListViewListener;
import mobi.dlys.android.familysafer.utils.DateUtils;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.VibratorManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommunicationActivity extends BaseExActivity {
	private static final int CHECK_NEW_MSG_SECOND = 10 * 1000; // 10秒检测一次
	private static final int MAX_VOICE_DURATION = 120; // 120秒

	protected TitleBarHolder mTitleBar;
	private XListView communication_LV;
	private RelativeLayout communication_mainRL;
	private RelativeLayout communication_bottomRL;
	private LinearLayout communication_voicecontentLL;
	private ImageView communication_voice_image;
	private TextView communication_voice_tip1;
	private TextView communication_voice_tip2;
	private Button communication_voice_button;
	private List<MsgObject> mList;
	private MyAdapter mAdapter;
	private boolean isSendVoice = false;
	private AnimationDrawable RecodingAmin;
	private int count = 0;
	private String mVoicePath;
	private Recorder mRecorder;

	private int mUserId;
	private String mAvatar;
	private String mNickname;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication);
		initView();
		initData();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);

		communication_mainRL = (RelativeLayout) findViewById(R.id.communication_mainRL);
		communication_bottomRL = (RelativeLayout) findViewById(R.id.communication_bottomRL);
		communication_LV = (XListView) findViewById(R.id.lv_communication);
		communication_voice_button = (Button) findViewById(R.id.communication_voice_button);

		mList = new ArrayList<MsgObject>();
		mAdapter = new MyAdapter();
		communication_LV.setAdapter(mAdapter);

		communication_voicecontentLL = (LinearLayout) findViewById(R.id.communication_voice_contentLL);
		communication_voice_image = (ImageView) findViewById(R.id.communication_voice_image);
		communication_voice_tip1 = (TextView) findViewById(R.id.communication_voice_tip1);
		communication_voice_tip2 = (TextView) findViewById(R.id.communication_voice_tip2);
		RecodingAmin = (AnimationDrawable) communication_voice_image.getBackground();

		communication_voice_button.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View arg0) {
				mVoicePath = Recorder.getVoiceFilePath();
				if (!startRecord(mVoicePath)) {
					YSToast.showToast(CommunicationActivity.this, R.string.fragment_main_recode_fail);
					return true;
				}
				if (Player.isPlaying()) {
					Player.stop();
				}
				VibratorManager.startVibrator(getBaseContext());
				communication_voicecontentLL.setBackgroundResource(R.drawable.communication_voice_green_bg);
				communication_voice_button.setBackgroundResource(R.drawable.button_green_selector);
				communication_voice_tip2.setText(R.string.communication_voice_tip2);
				showBottomView(communication_voicecontentLL, getBaseContext());
				RecodingAmin.start();
				handler.post(recoding);
				return false;
			}
		});
		communication_LV.setPullLoadEnable(false);
		communication_LV.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				// 下拉刷新
				sendMessage(YSMSG.REQ_GET_CACHE_MSG_LIST, mList.size(), mUserId, null);
			}

			@Override
			public void onLoadMore() {
				// 上拉加载更多

			}
		});
	}

	private void initData() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(PlayerManage.ACTION_PLAYER);
		intentFilter.addAction(PlayerManage.ACTION_NET_NOT_CONNECTION);
		registerReceiver(mPlayerStatusReceiver, intentFilter);

		if (getIntent() != null) {
			mUserId = getIntent().getIntExtra("userid", 0);
			mNickname = getIntent().getStringExtra("nickname");
			mAvatar = getIntent().getStringExtra("avatar");

			mTitleBar.mTitle.setText(mNickname);

			showWaitingDialog();
			if (getIntent().getBooleanExtra("newmsg", false)) {
				sendMessage(YSMSG.REQ_GET_MSG_LIST, 0, mUserId, null);
			} else {
				sendMessage(YSMSG.REQ_GET_CACHE_MSG_LIST, 0, mUserId, null);
			}
		}

	}

	/**
	 * 接收音乐播放广播
	 */
	private BroadcastReceiver mPlayerStatusReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String strAction = "";

			try {
				strAction = intent.getAction();
				if (strAction.equals(PlayerManage.ACTION_NET_NOT_CONNECTION)) {

				} else if (strAction.equals(PlayerManage.ACTION_PLAYER)) {
					Bundle bundle = intent.getExtras();
					int nState = bundle.getInt(PlayerManage.ACTION_PLAYER_STATE);
					int nMusicId = bundle.getInt(PlayerManage.ACTION_PLAYER_MUSICID);
					int nMsgId = bundle.getInt(PlayerManage.ACTION_PLAYER_MSGID);
					if ((nState == PlayState.STOP) || (nState == PlayState.PREPARE)) {
						ImageView btnRecord = (ImageView) communication_mainRL.findViewWithTag("message" + nMsgId + "voice" + nMusicId);
						stopAnimation(btnRecord);
					} else if (nState == PlayState.NEW_SONG) {
						ImageView btnRecord = (ImageView) communication_mainRL.findViewWithTag("message" + nMsgId + "voice" + nMusicId);
						startAnimation(btnRecord);
					} else if (nState == PlayState.BUFFER || nState == PlayState.PLAY) {
						ImageView btnRecord = (ImageView) communication_mainRL.findViewWithTag("message" + nMsgId + "voice" + nMusicId);
						startAnimation(btnRecord);
					}
				}
			} catch (Exception e) {
			}
		}
	};

	private void playOrPauseMusic(MsgObject message) {
		PlayerMusicInfo musicInfo = new PlayerMusicInfo();
		Player.getMusicInfo(musicInfo);

		if (null == message) {
			return;
		}

		if (Player.isPlaying() && musicInfo.nMusicID == message.getMsgId() && musicInfo.nMsgID == message.getMsgId()) {
			Player.stop();
		} else {
			Player.replaceMusic(PlayerMusicInfo.convert(message));
		}
	}

	private Runnable connectserver = new Runnable() {
		public void run() {
			removeMessage(YSMSG.REQ_GET_MSG_LIST);
			sendMessage(YSMSG.REQ_GET_MSG_LIST, 0, mUserId, null);
		}
	};

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_GET_MSG_LIST: {
			dismissWaitingDialog();
			communication_LV.stopRefresh();
			if (msg.arg1 == 200) {
				List<MsgObject> msglist = (List<MsgObject>) msg.obj;
				if (null != msglist && msglist.size() > 0) {
					boolean toBottom = false;
					boolean showTip = false;
					if (msg.arg2 > 0) {
						if (communication_LV.getLastVisiblePosition() == communication_LV.getCount() - 1) {
							toBottom = true;
						} else {
							showTip = true;
						}
					}
					Collections.sort(msglist, comparator);
					// Collections.reverse(msglist);
					mList.clear();
					mList.addAll(msglist);
					updateList();
					if (toBottom) {
						communication_LV.setSelection(communication_LV.getBottom());
					}
					if (showTip) {

					}
				}
			}

			mHandler.removeCallbacks(connectserver);
			mHandler.postDelayed(connectserver, CHECK_NEW_MSG_SECOND);
		}
			break;
		case YSMSG.RESP_GET_CACHE_MSG_LIST: {
			dismissWaitingDialog();
			communication_LV.stopRefresh();
			if (msg.arg1 == 200) {
				List<MsgObject> msglist = (List<MsgObject>) msg.obj;
				if (null != msglist) {
					boolean toBottom = false;
					if (mList.size() <= 0) {
						toBottom = true;
					}
					mList.addAll(0, msglist);
					Collections.sort(mList, comparator);
					updateList();
					if (toBottom) {
						communication_LV.setSelection(communication_LV.getBottom());
					}
				} else {
					if (mList.size() > 0) {
						YSToast.showToast(this, R.string.toast_no_more_data);
					}
				}
			}
			sendMessage(YSMSG.REQ_GET_MSG_LIST, 0, mUserId, null);
		}
			break;
		case YSMSG.RESP_SEND_MSG: {
			if (msg.arg2 > 0) {
				int status = 1;
				if (msg.arg1 != 200) {
					status = 2;
				}
				updateMsgStatus(msg.obj, msg.arg2, status);
				Button btnStatus = (Button) communication_mainRL.findViewWithTag("msgid" + msg.arg2);
				updateVoiceStatus(btnStatus, status);
				updateList();
			}
		}
			break;
		}
	}

	private void updateMsgStatus(Object msgObj, int indexId, int status) {
		if (mList != null && mList.size() > 0) {
			for (int i = 0; i < mList.size(); i++) {
				if (mList.get(i).getId() == indexId) {
					mList.get(i).setStatus(status);
					if (msgObj != null && msgObj instanceof Integer) {
						mList.get(i).setMsgId((Integer) msgObj);
					}
				}
			}
		}
	}

	Comparator<MsgObject> comparator = new Comparator<MsgObject>() {
		public int compare(MsgObject m1, MsgObject m2) {
			if (m1 == null || m2 == null) {
				if (m1 == null) {
					return -1;
				} else {
					return 1;
				}
			} else {
				if (TextUtils.isEmpty(m1.getCreateTime())) {
					return 1;
				}
				if (TextUtils.isEmpty(m2.getCreateTime())) {
					return -1;
				}
				long time1 = String2Time(m1.getCreateTime());
				long time2 = String2Time(m2.getCreateTime());
				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				c1.setTimeInMillis(time1);
				c2.setTimeInMillis(time2);
				return c1.compareTo(c2);
			}
		}
	};

	private boolean startRecord(String voiceFilePath) {
		if (TextUtils.isEmpty(voiceFilePath)) {
			return false;
		}
		if (null == mRecorder) {
			mRecorder = new Recorder();
		}

		if (null != mRecorder) {
			mRecorder.startRecord(voiceFilePath);
			return true;
		}
		return false;
	}

	private void stopRecord() {
		if (null != mRecorder) {
			mRecorder.stopRecord();
		}
	}

	private void updateVoiceStatus(Button statusBtn, int status) {
		if (statusBtn != null) {
			if (1 == status) {
				statusBtn.clearAnimation();
				statusBtn.setVisibility(View.GONE);
			} else if (2 == status) {
				statusBtn.clearAnimation();
				statusBtn.setBackgroundResource(R.drawable.img_locate_fail);
				statusBtn.setVisibility(View.VISIBLE);
			} else {
				statusBtn.setBackgroundResource(R.drawable.img_locating);
				statusBtn.startAnimation(AnimationUtils.loadAnimation(CommunicationActivity.this, R.anim.view_rotate_loop));
				statusBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	protected void onPause() {
		super.onPause();
		if (Player.isPlaying()) {
			Player.stop();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mPlayerStatusReceiver);
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		if (communication_voicecontentLL.getVisibility() != View.VISIBLE) {
			return super.dispatchTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			if (!inRangeOfView(communication_bottomRL, event) && !isUnderBottomLayout(event)) {
				communication_voicecontentLL.setBackgroundResource(R.drawable.communication_vioce_red_bg);
				communication_voice_button.setBackgroundResource(R.drawable.button_red_selector);
				communication_voice_tip2.setText(R.string.communication_voice_tip3);
				isSendVoice = false;
			} else {
				communication_voicecontentLL.setBackgroundResource(R.drawable.communication_voice_green_bg);
				communication_voice_button.setBackgroundResource(R.drawable.button_green_selector);
				communication_voice_tip2.setText(R.string.communication_voice_tip2);
				isSendVoice = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			stopRecord();

			if (null != RecodingAmin) {
				if (RecodingAmin.isRunning()) {
					RecodingAmin.stop();
				}
			}
			hideBottomView(communication_voicecontentLL, getBaseContext());
			handler.removeCallbacks(recoding);

			if (count <= 0) {
				YSToast.showToast(this, getString(R.string.toast_sos_voice_fail));
			} else {
				if (isSendVoice) {
					// 发送语音
					MsgObjectDao msgDao = new MsgObjectDao();
					MsgObject msgObject = new MsgObject();
					msgObject.setVoiceFilePath(mVoicePath);
					msgObject.setDuration(count);
					msgObject.setFromUser(CoreModel.getInstance().getUserId());
					msgObject.setToUser(mUserId);
					msgObject.setStatus(0);
					msgObject.setCreateTime(DateUtils.getCurrDateString(DateFormat));
					msgObject = msgDao.insert(msgObject);
					mList.add(msgObject);
					Collections.sort(mList, comparator);
					updateList();
					communication_LV.setSelection(communication_LV.getBottom());
					sendMessage(YSMSG.REQ_SEND_MSG, count, mUserId, msgObject);
				}
			}

			count = 0;
			communication_voice_button.setBackgroundResource(R.drawable.button_green_selector);
			break;
		}

		return super.dispatchTouchEvent(event);
	};

	Runnable recoding = new Runnable() {
		public void run() {
			count++;

			if (count >= MAX_VOICE_DURATION) {
				stopRecord();

				if (null != RecodingAmin) {
					if (RecodingAmin.isRunning()) {
						RecodingAmin.stop();
					}
				}
				hideBottomView(communication_voicecontentLL, getBaseContext());
				handler.removeCallbacks(recoding);
				if (isSendVoice) {
					// 发送语音
					MsgObjectDao msgDao = new MsgObjectDao();
					MsgObject msgObject = new MsgObject();
					msgObject.setVoiceFilePath(mVoicePath);
					msgObject.setDuration(count);
					msgObject.setFromUser(CoreModel.getInstance().getUserId());
					msgObject.setToUser(mUserId);
					msgObject.setStatus(0);
					msgObject.setCreateTime(DateUtils.getCurrDateString(DateFormat));
					msgObject = msgDao.insert(msgObject);
					mList.add(msgObject);
					Collections.sort(mList, comparator);
					updateList();
					communication_LV.setSelection(communication_LV.getBottom());
					sendMessage(YSMSG.REQ_SEND_MSG, count, mUserId, msgObject);
				}
				count = 0;
				communication_voice_button.setBackgroundResource(R.drawable.button_green_selector);
			} else {
				handler.sendEmptyMessage(1);
				handler.postDelayed(recoding, 1000);
			}
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				communication_voice_tip1.setText(getString(R.string.communication_voice_tip1) + count + "s");
			}

		};
	};

	public static void hideBottomView(final View v, Context context) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_bottom_out);
		v.startAnimation(anim);
		v.setVisibility(View.GONE);
	}

	public static void showBottomView(final View v, Context context) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_bottom_in);
		v.startAnimation(anim);
		v.setVisibility(View.VISIBLE);
	}

	/**
	 * 判断坐标是否某个View内
	 * 
	 * @param view
	 * @param ev
	 * @return
	 */
	private boolean inRangeOfView(View view, MotionEvent ev) {
		if (view == null || ev == null) {
			return false;
		}

		int[] location = new int[2];
		view.getLocationOnScreen(location);

		int x = location[0];
		int y = location[1];
		if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
			return false;
		}
		return true;
	}

	private boolean isUnderBottomLayout(MotionEvent ev) {
		int[] location = new int[2];
		communication_bottomRL.getLocationOnScreen(location);

		if (ev.getY() >= location[1]) {
			return true;
		}

		return false;
	}

	void updateList() {
		if (null != mAdapter && null != communication_LV) {
			mAdapter.notifyDataSetChanged();
			communication_LV.invalidate();
		}
	}

	class MyAdapter extends BaseAdapter {
		public int getCount() {
			return mList.size();
		}

		public MsgObject getItem(int position) {
			return mList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			MsgObject msgObject = mList.get(position);

			if (convertView == null || (holder = (ViewHolder) convertView.getTag()).flag != msgObject.getType()) {
				holder = new ViewHolder();

				if (msgObject.getType() == MsgObject.MESSAGE_FROM) {
					holder.flag = MsgObject.MESSAGE_FROM;
					convertView = LayoutInflater.from(CommunicationActivity.this).inflate(R.layout.item_communication_from, null);
				} else {
					holder.flag = MsgObject.MESSAGE_TO;
					convertView = LayoutInflater.from(CommunicationActivity.this).inflate(R.layout.item_communication_to, null);
				}
				holder.itemIV = (ImageView) convertView.findViewById(R.id.item_communication_image);
				holder.itemRL = (RelativeLayout) convertView.findViewById(R.id.item_communication_RL);
				holder.itemLLimg = (ImageView) convertView.findViewById(R.id.item_communication_LL_img);
				holder.itemLLtv = (TextView) convertView.findViewById(R.id.item_communication_LL_tv);
				holder.itemDatetv = (TextView) convertView.findViewById(R.id.item_communication_date_tv);
				holder.statusBtn = (Button) convertView.findViewById(R.id.item_communication_status_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String imagepath;
			holder.statusBtn.setVisibility(View.GONE);
			if (holder.flag == MsgObject.MESSAGE_FROM) {
				imagepath = mAvatar;
			} else {
				imagepath = CoreModel.getInstance().getUserInfo().getImage();
			}

			if (!TextUtils.isEmpty(imagepath)) {
				ImageLoaderHelper.displayImage(imagepath, holder.itemIV, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
			} else {
				holder.itemIV.setImageResource(R.drawable.user);
			}

			long spaceTime = 0;
			if (position <= mList.size() - 1 && position > 0) {
				MsgObject msgObject1 = mList.get(position);
				MsgObject msgObject2 = mList.get(position - 1);
				if (TextUtils.isEmpty(msgObject1.getCreateTime()) || TextUtils.isEmpty(msgObject2.getCreateTime())) {
					spaceTime = 0;
				} else {
					long ctime1 = String2Time(msgObject1.getCreateTime());
					long ctime2 = String2Time(msgObject2.getCreateTime());
					spaceTime = ctime1 - ctime2;
				}
			}
			if (position == 0) {
				spaceTime = 5 * 60 * 1000;
			}

			if (spaceTime < 5 * 60 * 1000) {
				holder.itemDatetv.setVisibility(View.GONE);
			} else {
				holder.itemDatetv.setVisibility(View.VISIBLE);
				holder.itemDatetv.setText(getDisplayTime(msgObject.getCreateTime()));
			}
			if (msgObject != null) {
				if (msgObject.getMsgId() > 0) {
					holder.itemLLimg.setTag("message" + msgObject.getMsgId() + "voice" + msgObject.getMsgId());
				}
				holder.itemLLtv.setText(msgObject.getDuration() + "'");
				int width = getWidthByDuration(msgObject.getDuration());
				ViewGroup.LayoutParams params = holder.itemRL.getLayoutParams();
				params.width = width;
				holder.itemRL.setLayoutParams(params);

				holder.statusBtn.setTag("msgid" + msgObject.getId());
				updateVoiceStatus(holder.statusBtn, msgObject.getStatus());
				AnimationDrawable anim = (AnimationDrawable) holder.itemLLimg.getBackground();
				anim.selectDrawable(2);
				holder.statusBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						MsgObject msgObject = mList.get(position);
						if (msgObject.isFailed()) {
							msgObject.setStatus(0);
							updateList();
							sendMessage(YSMSG.REQ_SEND_MSG, msgObject.getDuration(), msgObject.getToUser(), msgObject);
						}
					}
				});
				if (Player.isPlaying()) {
					PlayerMusicInfo musicInfo = new PlayerMusicInfo();
					Player.getMusicInfo(musicInfo);

					if (musicInfo.nMusicID == msgObject.getMsgId() && musicInfo.nMsgID == msgObject.getMsgId()) {
						startAnimation(holder.itemLLimg);
					} else {
						stopAnimation(holder.itemLLimg);
					}
				} else {
					stopAnimation(holder.itemLLimg);
				}

				final ImageView itemLLimg = holder.itemLLimg;
				holder.itemRL.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						if (mList.get(position).isSuccess()) {
							itemLLimg.setTag("message" + mList.get(position).getMsgId() + "voice" + mList.get(position).getMsgId());
							playOrPauseMusic(mList.get(position));
						}
						if (mList.get(position).isFailed()) {
							// YSToast.showToast(CommunicationActivity.this,
							// R.string.toast_sms_upload_failed);
							MsgObject msgObject = mList.get(position);
							if (msgObject.isFailed()) {
								msgObject.setStatus(0);
								updateList();
								sendMessage(YSMSG.REQ_SEND_MSG, msgObject.getDuration(), msgObject.getToUser(), msgObject);
							}
						}
						if (mList.get(position).isUpLoading()) {

						}
					}
				});
			}

			return convertView;
		}

		class ViewHolder {
			ImageView itemIV;
			RelativeLayout itemRL;
			ImageView itemLLimg;
			TextView itemLLtv;
			TextView itemDatetv;
			Button statusBtn;
			int flag;
		}
	}

	private String getDisplayTime(String time) {
		long ctime1 = String2Time(time);
		if (isCurDate(ctime1)) {
			return DateUtils.getDateString(ctime1, "HH:mm");
		} else if (isYesDate(ctime1)) {
			return getString(R.string.yesterday) + DateUtils.getDateString(ctime1, "HH:mm");
		} else {
			return DateUtils.getDateString(ctime1, "MM" + getString(R.string.yue) + "dd" + getString(R.string.ri) + " HH:mm");
		}
	}

	private boolean isSameDate(String time1, String time2) {
		long ctime1 = String2Time(time1);
		long ctime2 = String2Time(time2);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String sp_time = sf.format(ctime1);
		String current_time = sf.format(ctime2);
		if (!sp_time.equals(current_time)) {
			// 不同一天
			return false;
		}
		return true;
	}

	private boolean isCurDate(long time) {
		long ctime2 = System.currentTimeMillis();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String sp_time = sf.format(time);
		String current_time = sf.format(ctime2);

		if (!sp_time.equals(current_time)) {
			// 不是今天
			return false;
		}
		return true;
	}

	private boolean isYesDate(long time) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String sp_time = sf.format(time);
		if (!sp_time.equals(yesterday)) {
			// 不是昨天
			return false;
		}
		return true;
	}

	private int getWidthByDuration(int duration) {
		int baseWidth = (int) getResources().getDimension(R.dimen.record_base_width);
		int increase = (int) getResources().getDimension(R.dimen.record_increase_width);
		double d = ((double) duration) / ((double) (120));
		double tan = (Math.PI / 2) * d;
		int width = (int) ((double) (Math.sin(tan) * (double) increase)) + baseWidth;
		return width;
	}

	String DateFormat = "yyyy-MM-dd HH:mm:ss";

	private void startAnimation(ImageView view) {
		if (null == view) {
			return;
		}

		AnimationDrawable anim = (AnimationDrawable) view.getBackground();
		if (anim != null) {
			anim.start();
		}
	}

	private void stopAnimation(ImageView view) {
		if (null == view) {
			return;
		}

		AnimationDrawable anim = (AnimationDrawable) view.getBackground();
		if (anim != null && anim.isRunning()) {
			anim.stop();
			anim.selectDrawable(2);
		}
	}

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
