package mobi.dlys.android.familysafer.ui.notification;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapListener;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView.MyMarker;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.MySOSObject;
import mobi.dlys.android.familysafer.biz.vo.event.EventContent;
import mobi.dlys.android.familysafer.biz.vo.event.SOSVoice;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.player.PlayerManage;
import mobi.dlys.android.familysafer.player.PlayerMusicInfo;
import mobi.dlys.android.familysafer.player.utils.Player;
import mobi.dlys.android.familysafer.ui.checkin.CheckinActivity;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.communication.CommunicationActivity;
import mobi.dlys.android.familysafer.ui.sos.ShowMapActivity;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.search.poi.PoiResult;

public class NotificationDetail1Activity extends BaseExActivity implements BaiduMapListener {
    public static final String EXTRA_EVENT_OBJECT = "extra_event_object";

    protected TitleBarHolder mTitleBar;
    Button mPlayButton;
    TextView mDurationTV;
    TextView mTip1TV;
    BaiduMapView mBaiduMapView = null;
    TextView mLocationTV;
    Button mCallButton;
    Button mEnterButton = null;

    boolean isPlaying = true;
    EventObjectEx notification;

    private TextView tvLoc = null;
    private ImageView imgUser = null;
    private View mAvatar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationdetail_1);

        initView();
        initData();
    }

    void initView() {
        mTitleBar = new TitleBarHolder(NotificationDetail1Activity.this);
        mTitleBar.mTitle.setText(R.string.activity_notificationdetail_1_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                NotificationDetail1Activity.this.finish();
            }
        });
        mTitleBar.mRight.setVisibility(View.INVISIBLE);

        mPlayButton = (Button) findViewById(R.id.btn_notificationdetail_play);
        mEnterButton = (Button) findViewById(R.id.btn_common_entercom);
        mDurationTV = (TextView) findViewById(R.id.notificationdetail_duration_tv);
        mTip1TV = (TextView) findViewById(R.id.notificationdetail_tip1);
        mLocationTV = (TextView) findViewById(R.id.notificationdetail_location_tv);
        mCallButton = (Button) findViewById(R.id.btn_common_call);
        mCallButton.setOnClickListener(new OnClickListener() {
            public void onClick(View paramView) {
                int userid = notification.getUserId();
                FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(userid);
                if (null != fo) {
                    TelephonyUtils.call(NotificationDetail1Activity.this, fo.getPhone());
                }
            }
        });
        mEnterButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(NotificationDetail1Activity.this, CommunicationActivity.class);
                int userid = notification.getUserId();
                intent.putExtra("userid", userid);
                intent.putExtra("nickname", notification.getNickname());
                intent.putExtra("avatar", notification.getImage());
                startActivity(intent);
            }
        });
        mBaiduMapView = new BaiduMapView(this, R.id.img_notificationdetail_1_map, false, false);
        mBaiduMapView.setListener(this);

        mPlayButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                playOrPauseMusic();
            }
        });

        mAvatar = LayoutInflater.from(this).inflate(R.layout.layout_avatatar_pop, mBaiduMapView.getLayout());
        tvLoc = (TextView) mAvatar.findViewById(R.id.tv_checkin_location);
        tvLoc.setVisibility(View.GONE);
        imgUser = (ImageView) mAvatar.findViewById(R.id.img_checkin_avatar);
    }

    private void initData() {
        if (null != getIntent()) {
            BaseObject obiect = (BaseObject) getIntent().getSerializableExtra(EXTRA_EVENT_OBJECT);
            if (obiect instanceof EventObjectEx) {
                notification = (EventObjectEx) obiect;
            }
            if (obiect instanceof MySOSObject) {
                MySOSObject sosobj = (MySOSObject) obiect;
                notification = getEventBySOS(sosobj);
                mCallButton.setEnabled(false);
                mEnterButton.setEnabled(false);
            } else {
                if (notification.getType() == 4) {
                    mCallButton.setEnabled(false);
                    mEnterButton.setEnabled(false);
                }
            }
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerManage.ACTION_PLAYER);
        intentFilter.addAction(PlayerManage.ACTION_NET_NOT_CONNECTION);
        registerReceiver(mPlayerStatusReceiver, intentFilter);

        if (null != notification) {
            double lat = notification.getContent().getLat2();
            double lon = notification.getContent().getLng2();
            String img = notification.getImage();
            if (0.0 != lat && 0.0 != lon && 4.9E-324 != lat && 4.9E-324 != lon && null != img) {
                mBaiduMapView.setAvatar(0, (lat), (lon), imgUser, img, mAvatar);
            }
            mDurationTV.setText(notification.getDuration());
            String location = notification.getLocation();
            if (location.contains("|")) {
                location.replace("|", "");
            }
            mLocationTV.setText(location);
        }

        refreshPlayState();
    }

    private EventObjectEx getEventBySOS(MySOSObject sosobj) {
        EventObjectEx event = new EventObjectEx();
        SOSVoice voice = new SOSVoice();
        voice.voiceSosId = sosobj.getVoiceSosId();
        voice.voiceUrl = sosobj.getVoiceUrl();
        String duration = sosobj.getDuration();
        duration = duration.replace("\"", "");
        voice.duration = Integer.parseInt(duration);
        EventContent content = new EventContent();
        content.setVoiceSosId(sosobj.getVoiceSosId());
        content.setSOSVoice(voice);
        content.setLat(sosobj.getLat());
        content.setLng(sosobj.getLng());
        event.setContent(content);
        event.setCreateTime(sosobj.getCreateTime());
        event.setLocation(sosobj.getLocation());
        event.setImage(sosobj.getImage());
        return event;
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
                    refreshPlayState();
                }
            } catch (Exception e) {
            }
        }
    };

    private void refreshPlayState() {
        PlayerMusicInfo musicInfo = new PlayerMusicInfo();
        Player.getMusicInfo(musicInfo);

        if (null == notification || notification.getType() == 1 || notification.getType() == 2 || null == notification.getContent() || null == notification.getContent().getSOSVoice() || TextUtils.isEmpty(notification.getContent().getSOSVoice().voiceUrl)) {
            return;
        }

        if (Player.isPlaying() && musicInfo.nMusicID == notification.getContent().getSOSVoice().voiceSosId) {
            mPlayButton.setBackgroundResource(R.drawable.notification_pause_selector);
        } else {
            mPlayButton.setBackgroundResource(R.drawable.notification_play_selector);
        }
    }

    private void playOrPauseMusic() {
        PlayerMusicInfo musicInfo = new PlayerMusicInfo();
        Player.getMusicInfo(musicInfo);

        if (null == notification || notification.getType() == 1 || notification.getType() == 2 || null == notification.getContent() || null == notification.getContent().getSOSVoice() || TextUtils.isEmpty(notification.getContent().getSOSVoice().voiceUrl)) {
            return;
        }

        if (Player.isPlaying() && musicInfo.nMusicID == notification.getContent().getSOSVoice().voiceSosId) {
            Player.stop();
        } else {
            Player.replaceMusic(PlayerMusicInfo.convert(notification));
        }

    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onMarkerClick(MyMarker mymarker) {
        double lat = notification.getContent().getLat2();
        double lon = notification.getContent().getLng2();
        String img = notification.getImage();
        Intent intent = new Intent(NotificationDetail1Activity.this, ShowMapActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lon);
        intent.putExtra("avatar", img);
        startActivity(intent);
    }

    @Override
    public void onSnapshotReady(Bitmap snapshot) {

    }

    @Override
    public void onSearched(PoiResult result) {

    }

    @Override
    protected void onPause() {
        mBaiduMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mBaiduMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (Player.isPlaying()) {
            Player.stop();
        }
        unregisterReceiver(mPlayerStatusReceiver);

        mBaiduMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void OnMapChanged(MapStatus arg0) {

    }

    @Override
    public void OnMapClick() {
        double lat = notification.getContent().getLat2();
        double lon = notification.getContent().getLng2();
        String img = notification.getImage();

        Intent intent = new Intent(NotificationDetail1Activity.this, ShowMapActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lon);
        intent.putExtra("avatar", img);
        startActivity(intent);
    }
}