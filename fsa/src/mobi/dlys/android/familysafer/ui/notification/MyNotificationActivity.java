package mobi.dlys.android.familysafer.ui.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.dlys.android.core.image.universalimageloader.core.assist.FailReason;
import mobi.dlys.android.core.image.universalimageloader.core.listener.ImageLoadingListener;
import mobi.dlys.android.core.image.universalimageloader.core.listener.ImageLoadingProgressListener;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.db.dao.EventContentDao;
import mobi.dlys.android.familysafer.db.dao.EventObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
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
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyNotificationActivity extends BaseExActivity {
    protected TitleBarHolder mTitleBar;

    private RelativeLayout mNoNotificationRL = null;
    private XListView mListview = null;
    private ArrayList<EventObjectEx> mNotificationsList = null;
    private NotificationsAdapter mNotificationsAdapter = null;

    private boolean mUpdateData = true;

    private int eventID;

    protected boolean mPause = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initSubView();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (CoreModel.getInstance().isUpdateEventList()) {
                    CoreModel.getInstance().setUpdateEventList(false);
                }
                mUpdateData = false;
                mListview.startRefresh();
            }
        }, 200);
    }

    @Override
    public void onDestroy() {
        Player.stop();
        unregisterReceiver(mPlayerStatusReceiver);
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
        if (Player.isPlaying()) {
            Player.pause();
        }
    }

    private void initSubView() {
        mTitleBar = new TitleBarHolder(this);
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
        mTitleBar.mRight.setVisibility(View.INVISIBLE);

        mNoNotificationRL = (RelativeLayout) findViewById(R.id.no_notificationRL);
        mListview = (XListView) this.findViewById(R.id.lv_mynotifycation);
        mNotificationsList = new ArrayList<EventObjectEx>();

        mNotificationsAdapter = new NotificationsAdapter(this);
        mListview.setAdapter(mNotificationsAdapter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerManage.ACTION_PLAYER);
        intentFilter.addAction(PlayerManage.ACTION_NET_NOT_CONNECTION);
        registerReceiver(mPlayerStatusReceiver, intentFilter);
    }

    private void startAnimation(ImageView view) {
        if (null == view) {
            return;
        }

        view.setBackgroundResource(R.anim.voiceto);
        AnimationDrawable anim = (AnimationDrawable) view.getBackground();
        if (anim != null) {
            anim.start();
        }
    }

    private void stopAnimation(ImageView view) {
        if (null == view) {
            return;
        }

        if (view.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable anim = (AnimationDrawable) view.getBackground();
            if (anim != null && anim.isRunning()) {
                anim.stop();
                anim.selectDrawable(2);
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
                    int nEventId = bundle.getInt(PlayerManage.ACTION_PLAYER_EVENTID);
                    if ((nState == PlayState.STOP) || (nState == PlayState.PREPARE)) {
                        ImageView btnRecord = (ImageView) mListview.findViewWithTag("event" + nEventId + "voice" + nMusicId);
                        stopAnimation(btnRecord);
                    } else if (nState == PlayState.NEW_SONG) {
                        ImageView btnRecord = (ImageView) mListview.findViewWithTag("event" + nEventId + "voice" + nMusicId);
                        startAnimation(btnRecord);
                    } else if (nState == PlayState.BUFFER || nState == PlayState.PLAY) {
                        ImageView btnRecord = (ImageView) mListview.findViewWithTag("event" + nEventId + "voice" + nMusicId);
                        startAnimation(btnRecord);
                    }
                }
            } catch (Exception e) {
            }
        }
    };

    private void playOrPauseMusic(EventObjectEx notification) {
        PlayerMusicInfo musicInfo = new PlayerMusicInfo();
        Player.getMusicInfo(musicInfo);

        if (null == notification || notification.getType() == 1 || notification.getType() == 2 || null == notification.getContent() || null == notification.getContent().getSOSVoice() || TextUtils.isEmpty(notification.getContent().getSOSVoice().voiceUrl)) {
            return;
        }

        if (Player.isPlaying() && musicInfo.nMusicID == notification.getContent().getSOSVoice().voiceSosId && musicInfo.nEventID == notification.getEventId()) {
            Player.stop();
        } else {
            Player.replaceMusic(PlayerMusicInfo.convert(notification));
        }
    }

    void initData() {

        Intent intent = getIntent();
        if (intent != null) {
            String type = intent.getStringExtra("type");
            setEventIDByType(type);
        }

        mListview.showFooterView(false);
        mListview.setPullLoadEnable(false);
        mListview.setPullRefreshEnable(true);
        mListview.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                if (mUpdateData) {
                    mUpdateData = false;

                    sendMessage(YSMSG.REQ_GET_CACHE_CHECKIN_MSG_LIST, 0, 0, PageInfoObjectDao.getFirstPageInfo(eventID));
                } else {
                    sendMessage(YSMSG.REQ_GET_CHECKIN_MSG_LIST, 0, 0, PageInfoObjectDao.getFirstPageInfo(eventID));
                    PreferencesUtils.setNewEventCount(CoreModel.getInstance().getUserId(), 0);
                }
            }

            @Override
            public void onLoadMore() {
                PageInfoObject pageInfo = PageInfoObjectDao.getNextCachePageNo(eventID);
                if (pageInfo != null) {
                    if (pageInfo.isLastPage() && !EventObjectDao.hasMoreEvent(mNotificationsList.size(), eventID)) {
                        YSToast.showToast(MyNotificationActivity.this, R.string.toast_no_more_data);
                        mListview.stopLoadMore();
                        mListview.setPullLoadEnable(false);
                    } else {
                        sendMessage(YSMSG.REQ_GET_CACHE_CHECKIN_MSG_LIST, 0, 0, pageInfo);
                    }
                }
            }
        });

        if (new EventContentDao().count() <= 0) {
            PreferencesUtils.setNewEventCount(CoreModel.getInstance().getUserId(), 0);
            CoreModel.getInstance().setUpdateEventList(false);
        }
        mListview.setVisibility(View.VISIBLE);

    }

    private void setEventIDByType(String type) {
        if (type.equals("sos")) {
            eventID = PageInfoObjectDao.ID_EVENT_FRIEND_SOS;
            mTitleBar.mTitle.setText(R.string.fragment_notification_sos);

        }
        if (type.equals("in")) {
            eventID = PageInfoObjectDao.ID_EVENT_FRIEND_CHECKIN;
            mTitleBar.mTitle.setText(R.string.fragment_notification_in);
        }
        if (type.equals("tip")) {
            eventID = PageInfoObjectDao.ID_EVENT_CONFIRM;
            mTitleBar.mTitle.setText(R.string.fragment_notification_tip_title);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_CHECKIN_MSG_LIST:
        case YSMSG.RESP_GET_CACHE_CHECKIN_MSG_LIST: {
            mListview.stopRefresh();
            mListview.stopLoadMore();
            if (msg.arg1 == 200) {
                if (msg.arg2 == 1) {
                    mNotificationsList.clear();
                }

                List<EventObjectEx> list = (ArrayList<EventObjectEx>) msg.obj;
                if (list != null) {
                    mNotificationsList.addAll(list);
                    updateList();
                }

                mListview.setPullLoadEnable(!PageInfoObjectDao.isLastPage(eventID) || EventObjectDao.hasMoreEvent(mNotificationsList.size(), eventID));

                if (mNotificationsList.size() > 0) {
                    mNoNotificationRL.setVisibility(View.GONE);
                    mListview.setVisibility(View.VISIBLE);
                } else {
                    mListview.setVisibility(View.GONE);
                    mNoNotificationRL.setVisibility(View.VISIBLE);
                }
            } else {
                // failed
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(MyNotificationActivity.this, result.getErrorMsg());
                } else {
                    YSToast.showToast(MyNotificationActivity.this, R.string.network_error);
                }
            }
        }
            break;
        case YSMSG.RESP_CHECK_IN_CONFIRM:
            if (msg.arg1 == 200) {

            } else {

            }
            break;
        case YSMSG.RESP_VOICE_SOS_CONFIRM:
            if (msg.arg1 == 200) {

            } else {

            }
            break;
        case YSMSG.RESP_GET_NEW_MSG_NUM:
            if (msg.arg1 == 200) {
                int eventCount = CoreModel.getInstance().getEventCount();
                if (eventCount > 0 && !mPause && !mUpdateData) {
                    // mListview.startRefresh();
                }
            }
            break;
        }

    }

    void updateList() {
        if (null != mNotificationsAdapter) {
            mNotificationsAdapter.notifyDataSetChanged();
        }
    }

    private class NotificationsAdapter extends BaseAdapter {
        private Context mContext;

        public NotificationsAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mNotificationsList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                if (eventID == PageInfoObjectDao.ID_EVENT_CONFIRM) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(this.mContext).inflate(R.layout.list_item_checknotify, null, false);
                    holder.userImage = (ImageView) convertView.findViewById(R.id.item_checknotify_user_image);
                    holder.checkTipTV = (TextView) convertView.findViewById(R.id.item_checknotify_tip);
                    holder.dateTV = (TextView) convertView.findViewById(R.id.item_checknotify_date);
                    holder.statusImage = (ImageView) convertView.findViewById(R.id.item_checknotify_status_img);
                    holder.checkNameTV = (TextView) convertView.findViewById(R.id.item_checknotify_name);
                    holder.checkContentTV = (TextView) convertView.findViewById(R.id.item_checknotify_content);
                    holder.checkContentTipTV = (TextView) convertView.findViewById(R.id.item_checknotify_content_tip);
                    convertView.setTag(holder);
                } else if (eventID == PageInfoObjectDao.ID_EVENT_FRIEND_CHECKIN) {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(this.mContext).inflate(R.layout.list_item_mycheckin, null, false);
                    holder.checkinTV = (TextView) convertView.findViewById(R.id.item_mycheckin_checkin_tv_location);
                    holder.userImage = (ImageView) convertView.findViewById(R.id.item_mycheckin_user_image);
                    holder.mapImage = (ImageView) convertView.findViewById(R.id.item_mycheckin_mapimage);
                    holder.mapImageDian = (ImageView) convertView.findViewById(R.id.item_mycheckin_mapimage_dian);
                    holder.statusImage = (ImageView) convertView.findViewById(R.id.item_mycheckin_status_img);
                    holder.statusTV = (TextView) convertView.findViewById(R.id.item_mycheckin_status_tv);
                    holder.dateTV = (TextView) convertView.findViewById(R.id.item_mycheckin_date_tv);
                    convertView.setTag(holder);
                } else {
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(this.mContext).inflate(R.layout.list_item_mysos, null, false);
                    holder.userImage = (ImageView) convertView.findViewById(R.id.item_mysos_user_image);
                    holder.mapImage = (ImageView) convertView.findViewById(R.id.item_mysos_mapimage);
                    holder.mapImageDian = (ImageView) convertView.findViewById(R.id.item_mysos_mapimage_dian);
                    holder.statusImage = (ImageView) convertView.findViewById(R.id.item_mysos_status_img);
                    holder.statusTV = (TextView) convertView.findViewById(R.id.item_mysos_status_tv);
                    holder.statusLocationTV = (TextView) convertView.findViewById(R.id.item_mysos_status_tv_location);
                    holder.voiceLL = (RelativeLayout) convertView.findViewById(R.id.item_mysos_voice_LL);
                    holder.voiceTV = (TextView) convertView.findViewById(R.id.item_mysos_voiceTV);
                    holder.voiceImg = (ImageView) convertView.findViewById(R.id.item_mysos_voiceImg);
                    holder.dateTV = (TextView) convertView.findViewById(R.id.item_mysos_date_tv);
                    convertView.setTag(holder);
                }
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    EventObjectEx notification = mNotificationsList.get(position);
                    if (notification != null) {
                        int type = notification.getType();
                        Intent intent = new Intent();
                        if (3 == type) {
                            if (notification.getContent() != null && !notification.isConfirmed()) {
                                sendMessage(YSMSG.REQ_VOICE_SOS_CONFIRM, notification.getEventId(), notification.getContent().id, null);
                                notification.setConfirmed();
                            }
                            intent.setClass(MyNotificationActivity.this, NotificationDetail1Activity.class);
                            intent.putExtra(NotificationDetail1Activity.EXTRA_EVENT_OBJECT, notification);
                            startActivity(intent);

                        } else if (1 == type) {
                            if (notification.getContent() != null && !notification.isConfirmed()) {
                                sendMessage(YSMSG.REQ_CHECK_IN_CONFIRM, notification.getEventId(), notification.getContent().id, null);
                                notification.setConfirmed();
                            }

                            intent.setClass(MyNotificationActivity.this, NotificationDetail2Activity.class);
                            intent.putExtra(NotificationDetail1Activity.EXTRA_EVENT_OBJECT, notification);
                            startActivity(intent);
                        }
                    }
                }
            });

            if (holder.voiceLL != null) {
                holder.voiceLL.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        EventObjectEx notification = mNotificationsList.get(position);
                        if (notification.getType() == 3 && notification.getContent() != null && !notification.isConfirmed()) {
                            sendMessage(YSMSG.REQ_VOICE_SOS_CONFIRM, notification.getEventId(), notification.getContent().id, null);
                            notification.setConfirmed();
                        }
                        if (notification.getType() == 1 && notification.getContent() != null && !notification.isConfirmed()) {
                            sendMessage(YSMSG.REQ_CHECK_IN_CONFIRM, notification.getEventId(), notification.getContent().id, null);
                            notification.setConfirmed();
                        }
                        playOrPauseMusic(notification);
                    }
                });
            }
            final View contentView = convertView;
            if (position >= 0 && position < mNotificationsList.size()) {
                EventObjectEx notification = mNotificationsList.get(position);
                if (notification != null) {
                    String imageFile = notification.getImage();
                    if (!TextUtils.isEmpty(imageFile) && holder.userImage != null) {
                        ImageLoaderHelper.displayImage(imageFile, holder.userImage, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
                    } else {
                        holder.userImage.setImageResource(R.drawable.user);
                    }

                    if (holder.dateTV != null && !TextUtils.isEmpty(notification.getCreateTime())) {
                        holder.dateTV.setText(notification.getCreateTime());
                    }

                    if (holder.voiceImg != null) {
                        if (notification.getContent() != null && notification.getContent().getSOSVoice() != null) {
                            holder.voiceImg.setTag("event" + notification.getEventId() + "voice" + notification.getContent().getSOSVoice().voiceSosId);
                            holder.voiceLL.setVisibility(View.VISIBLE);
                            if (Player.isPlaying()) {
                                PlayerMusicInfo musicInfo = new PlayerMusicInfo();
                                Player.getMusicInfo(musicInfo);
                                if (musicInfo.nMusicID == notification.getContent().getSOSVoice().voiceSosId && musicInfo.nEventID == notification.getEventId()) {
                                    startAnimation(holder.voiceImg);
                                } else {
                                    stopAnimation(holder.voiceImg);
                                }
                            } else {
                                stopAnimation(holder.voiceImg);
                            }

                        } else {
                            holder.voiceLL.setVisibility(View.GONE);
                        }
                    }

                    int type = notification.getType();
                    if (1 == type) {
                        holder.mapImageDian.setTag("dian" + notification.getEventId());
                        holder.mapImage.setTag(notification.getEventId());
                        holder.mapImageDian.setVisibility(View.GONE);
                        holder.statusImage.setImageResource(R.drawable.icon_checkin);
                        if (holder.statusTV != null) {
                            String locationtip = "";
                            FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(notification.getUserId());
                            if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                                // locationtip = fo.getRemarkName() +
                                // getString(R.string.activity_notificationdetail_2_tv_arrived);
                                locationtip = fo.getRemarkName();
                            } else {
                                locationtip = notification.getNickname();
                                // locationtip = notification.getNickname() +
                                // getString(R.string.activity_notificationdetail_2_tv_arrived);
                            }
                            if (!TextUtils.isEmpty(notification.getContent().getMsg())) {
                                holder.statusTV.setText(locationtip + ":" + notification.getContent().getMsg());
                            } else {
                                holder.statusTV.setText(locationtip + ":" + getString(R.string.wo) + getString(R.string.activity_checkin_notify_familyLL_Tip6));
                            }
                            String location = notification.getLocation();
                            if (location.contains("|")) {
                                location = location.split("\\|")[1];
                                location.replace("\\|", "");
                            }
                            // location = replceLocation(location);

                            holder.checkinTV.setText(location);
                            holder.checkinTV.setVisibility(View.VISIBLE);
                        }
                        if (holder.mapImage != null && !TextUtils.isEmpty(notification.getContent().getLng()) && !TextUtils.isEmpty(notification.getContent().getLat())) {
                            int width = getResources().getDimensionPixelSize(R.dimen.list_item_mysos_map_width);
                            int height = getResources().getDimensionPixelSize(R.dimen.list_item_mysos_map_height);
                            String mapurl = "http://api.map.baidu.com/staticimage?center=" + notification.getContent().getLng() + "," + notification.getContent().getLat() + "&width=" + width + "&height=" + height + "&zoom=19";
                            ImageLoaderHelper.displayImage(mapurl, holder.mapImage, R.drawable.default_bg_image, false, new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    String tag = "dian" + view.getTag();
                                    View dianview = contentView.findViewWithTag(tag);
                                    if (dianview != null) {
                                        dianview.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    String tag = "dian" + view.getTag();
                                    View dianview = contentView.findViewWithTag(tag);
                                    if (dianview != null) {
                                        dianview.setVisibility(View.VISIBLE);
                                    }

                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                    // TODO Auto-generated method stub

                                }
                            }, new ImageLoadingProgressListener() {
                                public void onProgressUpdate(String imageUri, View view, int current, int total) {

                                }
                            });
                        }
                    } else if (2 == type) {
                        holder.checkTipTV.setText(getString(R.string.checknotify_tip));
                        holder.statusImage.setImageResource(R.drawable.checknotify_inimg);
                        if (CoreModel.getInstance().getUserInfo() != null) {
                            holder.checkNameTV.setText(CoreModel.getInstance().getUserInfo().getNickname());
                        }
                        holder.checkContentTipTV.setText(getString(R.string.checknotify_in));
                        String location = notification.getStringContent();
                        location = location.replace(getString(R.string.checknotify_already_in1), "");
                        location = location.replace(getString(R.string.checknotify_already_in2), "");
                        if (location.contains("|")) {
                            location = location.split("\\|")[1];
                            location.replace("\\|", "");
                        }
                        location = replceLocation(location);
                        if (location.length() >= 1) {
                            location = location.substring(0, location.length() - 1);
                        }
                        holder.checkContentTV.setText(location);
                    } else if (3 == type) {
                        holder.mapImageDian.setTag("dian" + notification.getEventId());
                        holder.mapImage.setTag(notification.getEventId());
                        holder.mapImageDian.setVisibility(View.GONE);
                        holder.statusImage.setImageResource(R.drawable.icon_sos);
                        
                        String name = "";
                        FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(notification.getUserId());
                        if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                            name = fo.getRemarkName();
                        } else {
                            name = notification.getNickname();
                        }
                        if (holder.statusTV != null) {
                            holder.statusTV.setText(name+":"+getString(R.string.checknotify_sos));
                            holder.statusTV.setVisibility(View.VISIBLE);
                        } else {
                            holder.statusTV.setVisibility(View.GONE);
                        }
                        if (holder.statusLocationTV != null) {
                            if (!TextUtils.isEmpty(notification.getLocation())) {
                                String location = notification.getLocation();
                                if (location.contains("|")) {
                                    location = location.split("\\|")[1];
                                    location.replace("\\|", "");
                                }
                                location = replceLocation(location);
                                holder.statusLocationTV.setText("“" + location + "”");
                                holder.statusLocationTV.setVisibility(View.VISIBLE);
                            } else {
                                holder.statusLocationTV.setVisibility(View.GONE);
                            }
                        }

                        if (holder.voiceTV != null && !TextUtils.isEmpty(notification.getDuration())) {
                            holder.voiceTV.setText(notification.getDuration());
                        }
                        if (holder.mapImage != null && notification.getContent().getLng() != null && notification.getContent().getLat() != null) {
                            int width = getResources().getDimensionPixelSize(R.dimen.list_item_mysos_map_width);
                            int height = getResources().getDimensionPixelSize(R.dimen.list_item_mysos_map_height);

                            String mapurl = "http://api.map.baidu.com/staticimage?center=" + notification.getContent().getLng() + "," + notification.getContent().getLat() + "&width=" + width + "&height=" + height + "&zoom=19";
                            ImageLoaderHelper.displayImage(mapurl, holder.mapImage, R.drawable.default_bg_image, false, new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    String tag = "dian" + view.getTag();
                                    View dianview = contentView.findViewWithTag(tag);
                                    if (dianview != null) {
                                        dianview.setVisibility(View.GONE);
                                    }

                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    String tag = "dian" + view.getTag();
                                    View dianview = contentView.findViewWithTag(tag);
                                    if (dianview != null) {
                                        dianview.setVisibility(View.VISIBLE);
                                    }

                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                    // TODO Auto-generated method stub

                                }
                            }, new ImageLoadingProgressListener() {

                                @Override
                                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                                    // TODO Auto-generated method stub

                                }
                            });
                        }

                    } else if (4 == type) {
                        holder.checkTipTV.setText(getString(R.string.checknotify_tip));
                        holder.statusImage.setImageResource(R.drawable.checknotify_sosimg);
                        if (CoreModel.getInstance().getUserInfo() != null) {
                            holder.checkNameTV.setText(CoreModel.getInstance().getUserInfo().getNickname());
                        }
                        holder.checkContentTipTV.setText(getString(R.string.checknotify_sos));
                        String location = notification.getLocation();
                        // if (location.contains("|")) {
                        // location = location.split("\\|")[1];
                        // location.replace("\\|", "");
                        // }
                        location = replceLocation(location);
                        holder.checkContentTV.setText(location);
                    }
                }
            }
            return convertView;
        }

        public final class ViewHolder {
            ImageView userImage;
            ImageView mapImage;
            ImageView mapImageDian;
            ImageView statusImage;
            TextView statusTV;
            TextView checkinTV;
            TextView statusLocationTV;
            RelativeLayout voiceLL;
            TextView voiceTV;
            ImageView voiceImg;
            TextView dateTV;
            TextView checkTipTV;
            TextView checkNameTV;
            TextView checkContentTV;
            TextView checkContentTipTV;

        }
    }

    private String replceLocation(String location) {
        if (location.contains(getString(R.string.shi))) {
            location = location.split(getString(R.string.shi), 2)[1];
        }
        return location;
    }

    private int getWidthByDuration(String duration) {
        int baseWidth = (int) getResources().getDimension(R.dimen.record_base_width);
        int increase = (int) getResources().getDimension(R.dimen.record_increase_width);
        if (duration != null) {
            Pattern p = Pattern.compile("\"");
            Matcher m = p.matcher(duration);
            duration = m.replaceAll("");
        }
        double dtion = Double.parseDouble(duration);
        double d = (dtion) / ((double) (60));
        double tan = (Math.PI / 2) * d;
        int width = (int) ((double) (Math.sin(tan) * (double) increase)) + baseWidth;
        return width;
    }
}