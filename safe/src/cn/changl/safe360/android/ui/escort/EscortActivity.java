package cn.changl.safe360.android.ui.escort;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import mobi.dlys.android.core.image.universalimageloader.core.process.BitmapProcessor;
import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.ImageUtils;
import mobi.dlys.android.core.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.api.PPNetManager;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.LocalUserObject;
import cn.changl.safe360.android.biz.vo.MessageDataObject;
import cn.changl.safe360.android.biz.vo.PushMessageObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.TripObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.db.dao.LocalTripObjectDao;
import cn.changl.safe360.android.db.dao.LocalUserObjectDao;
import cn.changl.safe360.android.im.hx.utils.CommonUtils;
import cn.changl.safe360.android.im.hx.utils.SmileUtils;
import cn.changl.safe360.android.map.BaiduLoc;
import cn.changl.safe360.android.map.BaiduLocListener;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.DialogHelper;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.ui.main.ExpandGridView;
import cn.changl.safe360.android.ui.main.ExpressionAdapter;
import cn.changl.safe360.android.ui.main.ExpressionPagerAdapter;
import cn.changl.safe360.android.ui.main.MessageAdapter;
import cn.changl.safe360.android.ui.main.NotifyId;
import cn.changl.safe360.android.ui.main.VoicePlayClickListener;
import cn.changl.safe360.android.utils.ExtraName;
import cn.changl.safe360.android.utils.ImageLoaderHelper;
import cn.changl.safe360.android.utils.NotificationHelper;
import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.android.utils.TelephonyUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Location;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Track;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Trip;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.DensityUtil;
import com.easemob.util.EasyUtils;
import com.easemob.util.VoiceRecorder;

//git.oschina.net/Cosmo/changl-android-safe360.git

public class EscortActivity extends BaseExActivity implements OnClickListener {
	private static final int LOCATION_NOT_CHANGE_COUNT = 10 * 60 * 1000 / BaiduLoc.MAP_SCAN_SPAN_FOR_TRIP;
	private static final int REFRESH_TRIP_TIME = 30 * 1000;

	private static final int MAX_TRIPER_COUNT = 5; // 最多5个人护航

	private static final float DEFAULT_ZOOM = 16.0f;

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	protected TitleBarHolder mTitleBar;
	GridView familyGrid;
	LinearLayout familyGridLL;

	RelativeLayout mBottomLayout;

	private TextView mChatText;
	private LinearLayout ChatTV;
	private RelativeLayout ChatRL;
	private EditText chatinputEdit;
	private Button chatinputvoiceBtn;
	private Button chatinputSendBtn;
	private Button expressionBtn;
	private Button voicetextBtn;
	private ListView chatListView;
	private Button ClostChatBtn;
	private TextView TitleTV;

	boolean isRecord = false;

	LinearLayout popupWindow;
	PopupWindow recordWindow;

	Button arriveBtn;
	TextView locationTV;
	TextView arriveTV;
	TextView overTV;
	TextView powerTV;
	TextView passTV;
	TextView familynumTV;

	Trip trip = null;

	boolean triped = true;// true 被护航 false 护航别人
	ArrayList<UserInfo> escortList = null;
	BaiduLoc loc = null;

	int startTripTime = 0;

	private int requestCode = 528;
	LatLng distanceLatLng;
	LatLng curLatLng;
	LatLng beginLatLng;
	RoutePlanSearch mSearch;

	int locationNotChangeCount = 0;

	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;

	private EMConversation conversation;
	private NewMessageBroadcastReceiver receiver;
	private String toChatUsername;
	private VoiceRecorder voiceRecorder;
	private MessageAdapter adapter;
	private PowerManager.WakeLock wakeLock;
	private int chatType;

	private LinearLayout emojiIconContainer;
	private InputMethodManager manager;
	private ViewPager expressionViewpager;
	private List<String> reslist;

	private TextView recordingTip;
	private TextView recordingCount;
	private ImageView recordingimg;
	private AnimationDrawable recordinganim;

	private String mNewMsgCount;
	private TextView mTxtNewMsgCount;

	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private ImageView mMySelfAvatar;

	private LatLng mCurLatlng; // 自己当前的经纬度
	private LatLng mTripRunnerCurLatlng; // 被护航人当前的经纬度

	boolean mRoutePlanSuccess = false;
	DrivingRouteResult mPlanResult;

	Button showMyLocation;

	Marker marker;
	UserInfo curuserinfo;
	ArrayList<Marker> marketList;
	// 获取护航信息间隔时间
	int getTripTime = REFRESH_TRIP_TIME;
	int curuserId = 0;
	int tripRunnerUserId = 0;

	List<ImageView> mAvatarImageViewList;

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			// micImage.setImageDrawable(micImages[msg.what]);
		}
	};

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, EscortActivity.class);
	}

	public static void startActivity(Context context, boolean trip) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(ExtraName.EN_TRIP_MODE, trip);
		ActivityUtils.startActivity(context, EscortActivity.class, bundle);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_escort);

		initView();
		initData();
	}

	private void initData() {
		trip = CoreModel.getInstance().getTrip();

		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(listener);
		if (trip != null) {
			initInfoView();
			toChatUsername = "" + trip.getIm().getGroupId();

			if (trip.getUserInfosCount() > 0) {
				showAllFriendDialog = true;
			}

			if (CoreModel.getInstance().getUserInfo() != null && trip.getUserInfo() != null) {
				int tripuserid = trip.getUserInfo().getUserId();
				int myid = CoreModel.getInstance().getUserInfo().getUserId();
				curuserId = myid;
				if (tripuserid == myid) {
					triped = true;
				} else {
					triped = false;
				}
			}

			changeViewByTriped();
		}

		if (getIntent() != null && triped) {
			triped = getIntent().getBooleanExtra(ExtraName.EN_TRIP_MODE, true);
			changeViewByTriped();
		}

		// init chat
		voiceRecorder = new VoiceRecorder(micImageHandler);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "safe360");

		// 注册接收消息广播
		receiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);

		// 注册一个消息送达的BroadcastReceiver
		IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getDeliveryAckMessageBroadcastAction());
		deliveryAckMessageIntentFilter.setPriority(5);
		registerReceiver(deliveryAckMessageReceiver, deliveryAckMessageIntentFilter);

		mHandler.post(tripRun);

		if (triped) {
			PreferencesUtils.setTripRunnerException(true);
			showGpsDialog();
		} else {
			PreferencesUtils.setTripRunnerException(false);
		}
	}

	private void showGpsDialog() {
		boolean isGpsOpen = false;
		LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null) {
			isGpsOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}

		if (isGpsOpen) {
			return;
		}

		DialogHelper.showTwoDialog(EscortActivity.this, true, null, getString(R.string.dialog_gps_open_tip), getString(R.string.dialog_delfri_yes),
				getString(R.string.dialog_delfri_no), true, new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(intent, 0);
					}
				}, null);

	}

	private void initAvatarImageViewList() {
		for (int i = 0; i < MAX_TRIPER_COUNT; i++) {
			mAvatarImageViewList.add(new ImageView(mActivity));
		}
	}

	private void changeViewByTriped() {
		if (trip == null) {
			return;
		}
		if (!triped) {
			arriveBtn.setVisibility(View.GONE);
		}

		if (triped) {
			mTitleBar.mLeft.setVisibility(View.GONE);
		}
		mTitleBar.mRight2.setVisibility(View.VISIBLE);
		if (triped) {
			mTitleBar.mRight2.setText(getString(R.string.activity_escorted_righttext));
		} else {
			mTitleBar.mRight2.setText(getString(R.string.activity_escort_righttext));
		}

		if (!triped) {
			tripedCurBattery = trip.getUserInfo().getPower();
		}

		TextView locTV = (TextView) findViewById(R.id.escort_info_locationTipTV);
		TextView ariTV = (TextView) findViewById(R.id.escort_info_arriveTipTV);
		TextView powerTV = (TextView) findViewById(R.id.escort_info_powerTipTV);
		if (triped) {
			locTV.setText(getString(R.string.activity_escort_position));
			ariTV.setText(getString(R.string.activity_escort_arrive));
			powerTV.setText(getString(R.string.activity_escort_power));
		} else {
			locTV.setText(getString(R.string.activity_escort_positioned));
			ariTV.setText(getString(R.string.activity_escort_arriveed));
			powerTV.setText(getString(R.string.activity_escort_powered));
		}
	}

	Runnable tripRun = new Runnable() {
		public void run() {
			if (trip != null) {
				sendMessage(YSMSG.REQ_GET_TRIP_INFO, 0, trip.getTripId(), null);
			}
			mHandler.postDelayed(tripRun, getTripTime);
		}
	};

	private void addMyselfMarker() {
		UserObject userObj = CoreModel.getInstance().getUserInfo();
		if (userObj != null) {
			addMarker(userObj.getUserId(), userObj.getPhone(), userObj.getImage(), userObj.getLat2(), userObj.getLng2(), mMySelfAvatar);
		}
	}

	private ArrayList<UserInfo> removeUserByUserid(List<UserInfo> list, int userid) {
		ArrayList<UserInfo> newlist = new ArrayList<UserInfo>();
		for (UserInfo info : list) {
			if (info.getUserId() != userid) {
				newlist.add(info);
			}
		}

		return newlist;
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(EscortActivity.this);
		mTitleBar.mTitle.setText(getString(R.string.activity_escort_titile));
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CoreModel.getInstance().setEscorting(true);
				finish();
			}
		});

		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (triped) {
					DialogHelper.showTwoDialog(EscortActivity.this, false, null, getString(R.string.dialog_finishTriped_content),
							getString(R.string.dialog_finishTriped_yes), null, true, new OnClickListener() {
								public void onClick(View arg0) {
									stopTrip();
								}
							}, null);
				} else {
					DialogHelper.showTwoDialog(EscortActivity.this, false, null, getString(R.string.dialog_finishTrip_content),
							getString(R.string.dialog_finishTrip_yes), null, true, new OnClickListener() {
								public void onClick(View arg0) {
									stopTrip();
								}
							}, null);
				}
			}
		});

		mMySelfAvatar = new ImageView(this);

		familyGrid = (GridView) findViewById(R.id.escort_familiesGrid);
		familyGridLL = (LinearLayout) findViewById(R.id.escort_familiesGridLL);

		mBottomLayout = (RelativeLayout) findViewById(R.id.escort_bottomRL);

		mChatText = (TextView) findViewById(R.id.txt_escort_chat);
		ChatTV = (LinearLayout) findViewById(R.id.escort_ChatLL);
		ChatRL = (RelativeLayout) findViewById(R.id.escort_chatRL);
		ChatRL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});
		TitleTV = (TextView) findViewById(R.id.escort_chatTitle);
		chatinputSendBtn = (Button) findViewById(R.id.escort_chatinput_SendBtn);
		chatinputEdit = (EditText) findViewById(R.id.escort_chatinput_edit);
		chatinputvoiceBtn = (Button) findViewById(R.id.escort_chatinput_voiceBtn);
		expressionBtn = (Button) findViewById(R.id.escort_chatinput_ExpressionBtn);
		voicetextBtn = (Button) findViewById(R.id.escort_chatinput_voicetextBtn);
		chatListView = (ListView) findViewById(R.id.escort_chatList);
		ClostChatBtn = (Button) findViewById(R.id.escort_chatRLColseBtn);

		ChatTV.setOnClickListener(this);
		chatinputSendBtn.setOnClickListener(this);
		ClostChatBtn.setOnClickListener(this);

		expressionBtn.setOnClickListener(this);
		voicetextBtn.setOnClickListener(this);
		chatinputvoiceBtn.setOnTouchListener(new PressToSpeakListen());
		chatinputEdit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(chatinputEdit.getText().toString())) {
					voicetextBtn.setBackgroundResource(R.drawable.chatinput_voice_selector);
				} else {
					chatinputSendBtn.setVisibility(View.VISIBLE);
				}
			}
		});
		showMyLocation = (Button) findViewById(R.id.escort_myLocationBtn);
		showMyLocation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (CoreModel.getInstance().getUserInfo() != null) {
					UserObject userObj = CoreModel.getInstance().getUserInfo();
					int userid = userObj.getUserId();
					curuserId = userid;
					BaiduLoc loc = App.getInstance().getLocater();
					LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
					userObj.setLocation(loc.getAddress());
					userObj.setLng(loc.getLng());
					userObj.setLat(loc.getLat());
					CoreModel.getInstance().setUserInfo(userObj);
					refreshMapMarker(userid, userObj.getPhone(), true);
					sendEmptyMessage(YSMSG.REQ_MODIFY_LOCATION);
				}
			}
		});

		mAvatarImageViewList = new ArrayList<ImageView>();
		initAvatarImageViewList();

		chatinputEdit.clearFocus();
		marketList = new ArrayList<Marker>();
		escortList = new ArrayList<UserInfo>();
		FamilyGridAdapter adapter = new FamilyGridAdapter(getBaseContext());
		familyGrid.setAdapter(adapter);
		initMap();
		initPopWindow();

		initRecordWindow();

		mTxtNewMsgCount = (TextView) findViewById(R.id.txt_escort_msg_count);

		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		// chatinputEdit.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// emojiIconContainer.setVisibility(View.GONE);
		// }
		// });

		chatinputEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
				emojiIconContainer.setVisibility(View.GONE);
				return false;
			}
		});
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(BatteryReceiver, intentFilter);
	}

	protected void stopTrip() {
		showWaitingDialog();
		Trip trip = CoreModel.getInstance().getTrip();
		if (trip != null && loc != null) {
			TripObject tripObj = new TripObject();
			tripObj.setTripId(trip.getTripId());
			tripObj.setEndAdress(loc.getAddress());
			tripObj.setEndLat(loc.getLat());
			tripObj.setEndLng(loc.getLng());
			if (triped) {
				sendMessage(YSMSG.REQ_FINISH_TRIP, 0, 0, tripObj);
			} else {
				sendMessage(YSMSG.REQ_EXIT_TRIP, 0, trip.getTripId(), null);
			}
		}
	}

	private void initInfoView() {
		arriveBtn = (Button) findViewById(R.id.escort_arriveBtn);
		locationTV = (TextView) findViewById(R.id.escort_info_locationTV);
		arriveTV = (TextView) findViewById(R.id.escort_info_arriveTV);
		overTV = (TextView) findViewById(R.id.escort_info_overTV);
		powerTV = (TextView) findViewById(R.id.escort_info_powerTV);
		passTV = (TextView) findViewById(R.id.escort_info_passTV);
		familynumTV = (TextView) findViewById(R.id.escort_info_familynumTV);
		arriveBtn.setOnClickListener(this);

		loc = App.getInstance().getLocater();
		if (loc != null) {
			loc.addBaiduLocListener(loclistener);
			if (triped) {
				locationTV.setText(loc.getAddress());
			}
		}
		if (trip != null) {
			if (trip.hasEndLocation() && !TextUtils.isEmpty(trip.getEndLocation().getAddress())) {
				arriveTV.setText(trip.getEndLocation().getAddress());
			}

			long startTime = parseString(trip.getBeginTime());
			long curtime = System.currentTimeMillis();
			startTripTime = (int) (curtime - startTime) / 1000;
			if (trip.hasBeginTime()) {
				passTV.setText(tiem2String(startTripTime));
			}

			familynumTV.setText(trip.getUserInfosCount() + "");
		}

		handler.post(tiemrunnable);
	}

	private void updateInfoView() {
		if (trip == null) {
			return;
		}
		loc = App.getInstance().getLocater();
		if (loc != null) {
			loc.addBaiduLocListener(loclistener);
			if (triped) {
				locationTV.setText(loc.getAddress());
			} else {
				if (trip.getUserInfo() != null && trip.getUserInfo().getLocation() != null) {
					locationTV.setText(trip.getUserInfo().getLocation().getAddress());
				} else {
					locationTV.setText(R.string.activity_escort_unknow);
				}
			}
		}

		if (trip.hasEndLocation() && !TextUtils.isEmpty(trip.getEndLocation().getAddress())) {
			arriveTV.setText(trip.getEndLocation().getAddress());
		}

		if (trip.hasBeginTime()) {
			passTV.setText(tiem2String(startTripTime));
		}
		if (trip != null) {
			familynumTV.setText(trip.getUserInfosCount() + "");
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				startTripTime++;
				passTV.setText(tiem2String(startTripTime));
				if (triped) {
					powerTV.setText(String.valueOf(mCurBattery) + "%");
				} else {
					if (tripedCurBattery == 0) {
						powerTV.setText(getString(R.string.activity_escort_unknow));
					} else {
						powerTV.setText(String.valueOf(tripedCurBattery) + "%");
					}
				}
			}
		};
	};

	BaiduLocListener loclistener = new BaiduLocListener() {
		public void onLocationChanged(boolean result, ReverseGeoCodeResult geoResult) {
			try {
				if (geoResult.error == SearchResult.ERRORNO.NO_ERROR) {
					mCurLatlng = geoResult.getLocation();
					String address = geoResult.getAddressDetail().city;
					address += geoResult.getAddressDetail().district;
					address += geoResult.getAddressDetail().street;
					address += geoResult.getAddressDetail().streetNumber;

					if (CoreModel.getInstance().getUserInfo() != null) {
						UserObject user = CoreModel.getInstance().getUserInfo();
						user.setLocation(address);
						user.setLng(loc.getLng());
						user.setLat(loc.getLat());
						user.setPower(mCurBattery);
						if (triped) {
							sendMessage(YSMSG.REQ_MODIFY_LOCATION, 0, trip.getTripId(), null);
						}
					}
					if (CoreModel.getInstance().getUserInfo() != null) {
						refreshMapMarker(CoreModel.getInstance().getUserInfo().getUserId(), CoreModel.getInstance().getUserInfo().getPhone(), false);
					}

					if (triped) {
						locationTV.setText(address);
						if (address.equals(locationTV.getText())) {
							locationNotChangeCount++;
						} else {
							locationNotChangeCount = 0;
						}
					}

					redraw();
					LogUtils.i("locationNotChangeCount", locationNotChangeCount + "");
					if (locationNotChangeCount == LOCATION_NOT_CHANGE_COUNT) {
						if (CoreModel.getInstance().getUserInfo() != null) {
							if (triped) {
								PushMessageObject pushObj = new PushMessageObject();
								pushObj.setTitle(getString(R.string.activity_escort_nochangetitle));
								String desc = String.format(getString(R.string.activity_escort_nochangedesc), address);
								ArrayList<Integer> idList = getUserIdList();
								pushObj.setUseridList(idList);
								pushObj.setDescription(CoreModel.getInstance().getUserInfo().getNickname() + desc);
								JSONObject json = new JSONObject();
								try {
									json.put("userid", CoreModel.getInstance().getUserInfo().getUserId());
									json.put("type", MessageDataObject.TYPE_NOCHANGE);
									json.put("lat", mCurLatlng.latitude);
									json.put("lng", mCurLatlng.longitude);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								pushObj.setData(json.toString());
								sendMessage(YSMSG.REQ_PUSH_MSG, 0, 0, pushObj);
							}

							locationNotChangeCount = 0;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	int tripedCurBattery = 0;
	int mCurBattery = 0;

	private ArrayList<Integer> getUserIdList() {
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (UserInfo info : escortList) {
			idList.add(info.getUserId());
		}
		return idList;
	}

	BroadcastReceiver BatteryReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			int mBatteryLevel = intent.getIntExtra("level", 0);
			// 电量的总刻度
			int mBatteryScale = intent.getIntExtra("scale", 100);
			if (CoreModel.getInstance().getUserInfo() != null && loc != null) {
				UserObject user = CoreModel.getInstance().getUserInfo();
				user.setLocation(loc.getAddress());
				user.setLng(loc.getLng());
				user.setLat(loc.getLat());
				if (triped && trip != null) {
					mCurBattery = mBatteryLevel * 100 / mBatteryScale;
					user.setPower(mCurBattery);
					sendMessage(YSMSG.REQ_MODIFY_LOCATION, 0, trip.getTripId(), user);
				}
			}
		}
	};

	Runnable tiemrunnable = new Runnable() {
		public void run() {
			handler.postDelayed(tiemrunnable, 1000);
			handler.sendEmptyMessage(1);
		}
	};

	private long parseString(String s) {
		SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = myFmt2.parse(s);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private String tiem2String(int time) {
		int hour = (time / 3600);
		int munite = ((time - hour * 3600) / 60);
		int second = (time % 60);
		if (hour == 0) {
			if (munite == 0) {
				return second + getString(R.string.second);
			} else {
				return munite + getString(R.string.munite) + second + getString(R.string.second);
			}
		} else {
			return hour + getString(R.string.hour) + munite + getString(R.string.munite) + second + getString(R.string.second);
		}
	}

	protected void onStop() {
		super.onStop();
		if (loc != null && loclistener != null) {
			loc.removeBaiduLocListener(loclistener);
		}
		unregister(deliveryAckMessageReceiver);
	}

	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		mSearch.destroy();
		handler.removeCallbacks(tiemrunnable);
		unregister(receiver);
		unregister(BatteryReceiver);
		mAvatarImageViewList.clear();
	}

	private void unregister(BroadcastReceiver recv) {
		try {
			unregisterReceiver(recv);
		} catch (Exception e) {

		}
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (!inRangeOfView(popupWindow, ev)) {
			hidePopUp();
		}
		return super.dispatchTouchEvent(ev);
	}

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

	private void initPopWindow() {
		popupWindow = (LinearLayout) findViewById(R.id.escort_popView);
		Button posiBtn = (Button) findViewById(R.id.escort_PosiBtn);
		Button callBtn = (Button) findViewById(R.id.escort_PhoneBtn);
		posiBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				UserInfo user = curuserinfo;
				if (user != null && user.getUserId() != LocalUserObject.LOCAL_USER_ID) {
					if (!user.getHideLocation()) {
						sendMessage(YSMSG.REQ_GET_USER_LOCATION, curuserId, 0, null);
						hidePopUp();
					} else {
						YSToast.showToast(EscortActivity.this, getString(R.string.toast_userhidelocation));
					}
				}
			}
		});
		callBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showCallPhoneDialog(curuserinfo.getPhone());
			}
		});
	}

	protected void showCallPhoneDialog(final String phone) {
		DialogHelper.showTwoDialog(EscortActivity.this, false, null, phone, getString(R.string.dialog_callphone_yes), getString(R.string.dialog_callphone_no),
				true, new OnClickListener() {
					public void onClick(View v) {
						TelephonyUtils.call(EscortActivity.this, phone);
					}
				}, null);
	}

	private void initRecordWindow() {
		View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.recording_popview, null);
		if (view != null) {
			recordingTip = (TextView) view.findViewById(R.id.txt_recording_tip);
			recordingCount = (TextView) view.findViewById(R.id.txt_recording_count);
			recordingimg = (ImageView) view.findViewById(R.id.img_recording_tip);
			recordinganim = (AnimationDrawable) recordingimg.getBackground();
		}
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		recordWindow = new PopupWindow(view, (int) density * 260, (int) density * 270);
	}

	private void initMap() {
		setUpMapIfNeeded();
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		mHandler.post(tripRun);
		updateMsgCount();

		App.getInstance().setMapScanTripMode();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
		mHandler.removeCallbacks(tripRun);
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordinganim.stop();
				recordWindow.dismiss();
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (intent != null && intent.getExtras() != null) {
			int chatType = intent.getExtras().getInt("chatType", EscortActivity.CHATTYPE_SINGLE);
			if (chatType == CHATTYPE_GROUP) {
				startChat();
				hidePopUp();
				ChatRL.setVisibility(View.VISIBLE);
				if (isRecord) {
					voicetextBtn.setBackgroundResource(R.drawable.chatinput_keyborad_selector);
					chatinputSendBtn.setVisibility(View.INVISIBLE);
					chatinputvoiceBtn.setVisibility(View.VISIBLE);
					chatinputEdit.setVisibility(View.GONE);
				} else {
					voicetextBtn.setBackgroundResource(R.drawable.chatinput_voice_selector);
					chatinputSendBtn.setVisibility(View.VISIBLE);
					chatinputvoiceBtn.setVisibility(View.GONE);
					chatinputEdit.setVisibility(View.VISIBLE);
				}
			}
		}

	}

	private void setUpMapIfNeeded() {
		BaiduMapOptions bo = new BaiduMapOptions().compassEnabled(false).overlookingGesturesEnabled(false).rotateGesturesEnabled(false)
				.scaleControlEnabled(false).scrollGesturesEnabled(true).zoomControlsEnabled(false).zoomGesturesEnabled(true);
		mMapView = new MapView(EscortActivity.this, bo);
		FrameLayout fLayout = (FrameLayout) findViewById(R.id.escort_frameLayoutmap);
		fLayout.addView(mMapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(DEFAULT_ZOOM);
		mBaiduMap.setMapStatus(msu);

		BaiduLoc baiduloc = App.getInstance().getLocater();
		if (baiduloc != null) {
			LatLng latlng = new LatLng(baiduloc.getLatitude(), baiduloc.getLongitude());
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
		}
	}

	private void showMarker(int userid) {
		Marker marker = null;
		for (int i = 0; i < marketList.size(); i++) {
			marker = marketList.get(i);
			if (marker != null) {
				int marketid = marker.getExtraInfo().getInt("userid");
				if (marketid == userid) {
					marker.setVisible(true);
				}
			}
		}
	}

	private void drawLineByLatlngs() {
		if (trip == null) {
			return;
		}
		List<Track> trackList = trip.getTracksList();
		if (trackList == null || trackList.size() <= 0) {
			return;
		}
		ArrayList<LatLng> latlngList = new ArrayList<LatLng>();
		for (Track track : trackList) {
			if (BaiduLoc.isLocationValid(track.getLng(), track.getLat())) {
				try {
					LatLng latlng = new LatLng(Double.valueOf(track.getLat()), Double.valueOf(track.getLng()));
					latlngList.add(latlng);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (latlngList.size() > 0 && triped) {
			LatLng latlng = latlngList.get(latlngList.size() - 1);
			if (latlng != null && CoreModel.getInstance().getUserInfo() != null) {
				if (latlng.latitude != CoreModel.getInstance().getUserInfo().getLat2() || latlng.longitude != CoreModel.getInstance().getUserInfo().getLng2()) {
					latlngList.add(new LatLng(CoreModel.getInstance().getUserInfo().getLat2(), CoreModel.getInstance().getUserInfo().getLng2()));
				}
			}
		}
		// YSToast.showToast(mActivity, "point count: " + latlngList.size());
		// LatLng p1 = new LatLng(49.97767, 134.986789);
		// LatLng p3 = new LatLng(39.97923, 116.437428);
		// if (latlngList.size() < 2) {
		// latlngList.add(p1);
		// latlngList.add(p3);
		// }
		if (latlngList.size() >= 2 && latlngList.size() < 10000) {
			OverlayOptions lineOption = new PolylineOptions().width(10).points(latlngList).color(0xFF45E1FD);
			mBaiduMap.addOverlay(lineOption);

			if (trip != null && mPlanResult == null) {
				// View popView =
				// LayoutInflater.from(EscortActivity.this).inflate(R.layout.layout_map_people_window,
				// null);
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_curposition);
				OverlayOptions oop = new MarkerOptions().position(latlngList.get(0)).icon(bitmap);
				mBaiduMap.addOverlay(oop);
			}
		}

	}

	private void refreshMapMarker(int userid, String phone, boolean makeCenter) {
		boolean exist = false;
		UserInfo userInfo = null;
		int marketid;
		String localPhone;
		Marker marker = null;
		for (int i = 0; i < marketList.size(); i++) {
			marker = marketList.get(i);
			if (marker != null) {
				marketid = marker.getExtraInfo().getInt("userid");
				localPhone = marker.getExtraInfo().getString("phone");
				if (marketid == userid && marketid != LocalUserObject.LOCAL_USER_ID) {
					exist = true;
					LatLng latlng = null;
					if (marketid == CoreModel.getInstance().getUserInfo().getUserId()) {
						UserObject myselef = CoreModel.getInstance().getUserInfo();
						if (myselef != null) {
							latlng = new LatLng(myselef.getLat2(), myselef.getLng2());
						}
					} else {
						userInfo = CoreModel.getInstance().getFriendByUserid(marketid);
						if (userInfo != null) {
							UserObject user = UserObject.createFromPb(userInfo);
							if (user != null) {
								latlng = new LatLng(user.getLat2(), user.getLng2());
							}
						}
					}
					if (latlng != null) {
						if (makeCenter) {
							showCenter(latlng);
						}
						marker.setPosition(latlng);
						showMarker(marketid);
						marker.setZIndex(getZIndex(marketList.size()));
					}
				} else if (!TextUtils.isEmpty(localPhone) && phone.equals(localPhone) && marketid == LocalUserObject.LOCAL_USER_ID) {
					exist = true;
					LatLng latlng = null;
					LocalUserObject localUser = new LocalUserObjectDao().getLocalUserByPhone(phone);
					if (localUser != null) {
						latlng = new LatLng(localUser.getLat2(), localUser.getLng2());
						if (makeCenter) {
							showCenter(latlng);
						}
						marker.setPosition(latlng);
						showMarker(marketid);
						marker.setZIndex(getZIndex(marketList.size()));
					}

				} else {
					marker.setZIndex(getZIndex(i));
				}
			}
		}

	}

	private void showCenter(LatLng latlng) {
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
	}

	public void addMarker(int userid, String phone, String imgUrl, double latitude, double longitude, ImageView imageView) {
		String tempimg = imgUrl.replace(PPNetManager.HTTP, "").replace(PPNetManager.IMAGE_DOMAIN, "").replace(PPNetManager.IMAGE_DOMAIN2, "")
				.replace("!avatar.def", "");
		if (!TextUtils.isEmpty(imgUrl) && !TextUtils.isEmpty(tempimg)) {
			LatLng latlng = new LatLng(latitude, longitude);
			// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
			AvatarProcessor process = new AvatarProcessor(userid, phone, latlng);
			ImageLoaderHelper.displayImage(imgUrl, imageView, R.drawable.icon_family, process, true, 90);
		} else {
			if (!isMarkerExist(userid, phone)) {
				View popView = LayoutInflater.from(EscortActivity.this).inflate(R.layout.layout_map_people_window, null);
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(popView);
				OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(bitmap);
				Marker marker = (Marker) mBaiduMap.addOverlay(oop);
				// if (userid ==
				// CoreModel.getInstance().getUserInfo().getUserId()) {
				// marker.setVisible(true);
				// } else {
				// marker.setVisible(false);
				// }
				if (curuserId == userid) {
					marker.setZIndex(getMaxZIndex());
				}
				addMarker(userid, phone, marker);
			} else {
				// refreshMapMarker(userid, phone, false);
			}
		}
	}

	private int getMaxZIndex() {
		return escortList.size() + 1000;
	}

	private int getZIndex(int i) {
		return i + 1000;
	}

	private void addMarker(int userId, String phone, Marker marker) {
		boolean exist = false;

		for (Marker m : marketList) {
			if (m != null && m.getExtraInfo() != null && (m.getExtraInfo().getInt("userid") == userId || m.getExtraInfo().getString("phone").equals(phone))) {
				exist = true;
				break;
			}
		}

		if (!exist) {
			Bundle bundle = new Bundle();
			bundle.putInt("userid", userId);
			bundle.putString("phone", phone);
			marker.setExtraInfo(bundle);
			marketList.add(marker);
		}
	}

	private boolean isMarkerExist(int userId, String phone) {
		boolean exist = false;

		for (Marker m : marketList) {
			if (m != null && m.getExtraInfo() != null && (m.getExtraInfo().getInt("userid") == userId || m.getExtraInfo().getString("phone").equals(phone))) {
				exist = true;
				break;
			}
		}

		return exist;
	}

	public class BlackWhiteProcessor implements BitmapProcessor {

		public BlackWhiteProcessor() {

		}

		public Bitmap process(Bitmap bitmap) {
			Bitmap newBitmap = null;
			try {
				newBitmap = convertToBlackWhite(bitmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return newBitmap;
		}
	}

	public class AvatarProcessor implements BitmapProcessor {
		int userid;
		String phone;
		LatLng latlng;

		public AvatarProcessor(int userid, String phone, LatLng latlng) {
			this.userid = userid;
			this.phone = phone;
			this.latlng = latlng;
		}

		public Bitmap process(Bitmap bitmap) {
			try {
				if (!isMarkerExist(userid, phone)) {
					final Bitmap output = convertViewToBitmapEx(bitmap);
					BitmapDescriptor viewbitmap;
					if (output == null) {
						View popView = LayoutInflater.from(EscortActivity.this).inflate(R.layout.layout_map_people_window, null);
						viewbitmap = BitmapDescriptorFactory.fromView(popView);
					} else {
						viewbitmap = BitmapDescriptorFactory.fromBitmap(output);
					}
					OverlayOptions oop = new MarkerOptions().position(this.latlng).icon(viewbitmap).visible(true);
					Marker marker = (Marker) mBaiduMap.addOverlay(oop);
					// if (userid == trip.getUserInfo().getUserId()) {
					// marker.setVisible(true);
					// } else {
					// marker.setVisible(false);
					// }
					if (curuserId == userid) {
						marker.setZIndex(getMaxZIndex());
					}
					addMarker(userid, phone, marker);
				} else {
					// refreshMapMarker(userid, phone, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}
	}

	public Bitmap convertViewToBitmapEx(Bitmap bitmap) {
		Bitmap roundBitmap = null;
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int width = (int) (density * 40);
		int height = 102;
		int padding = 2;
		Bitmap destBitmap = null;
		final Paint paint = new Paint();
		try {
			// 把view中的内容绘制在画布上
			Bitmap frameBitmap = ImageUtils.getResBitmap(App.getInstance().getResources(), R.drawable.mappeoplebg, width);
			if (frameBitmap != null) {
				roundBitmap = ImageUtils.getCircleBitmap(bitmap, frameBitmap.getWidth() - padding);
				destBitmap = Bitmap.createBitmap(frameBitmap.getWidth(), frameBitmap.getHeight(), Bitmap.Config.ARGB_8888);
				// 利用bitmap生成画布
				Canvas canvas = new Canvas(destBitmap);
				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);

				canvas.drawBitmap(frameBitmap, 0, 0, paint);
				canvas.drawBitmap(roundBitmap, padding, padding, paint);

				ImageUtils.recycleBitmap(roundBitmap);
				ImageUtils.recycleBitmap(frameBitmap);
			}
		} catch (OutOfMemoryError e) {
			destBitmap = null;
			e.printStackTrace();
		} catch (Exception e) {
			destBitmap = null;
			e.printStackTrace();
		}

		return destBitmap;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.escort_ChatLL:
			if (conversation != null) {
				conversation.resetUnreadMsgCount();
			}
			updateMsgCount();
			startChat();
			hidePopUp();
			ChatRL.setVisibility(View.VISIBLE);
			if (isRecord) {
				voicetextBtn.setBackgroundResource(R.drawable.chatinput_keyborad_selector);
				chatinputSendBtn.setVisibility(View.INVISIBLE);
				chatinputvoiceBtn.setVisibility(View.VISIBLE);
				chatinputEdit.setVisibility(View.GONE);
			} else {
				voicetextBtn.setBackgroundResource(R.drawable.chatinput_voice_selector);
				chatinputSendBtn.setVisibility(View.VISIBLE);
				chatinputvoiceBtn.setVisibility(View.GONE);
				chatinputEdit.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.escort_chatinput_SendBtn:
			// send text
			sendText(chatinputEdit.getEditableText().toString());
			break;
		case R.id.escort_chatRLColseBtn:
			if (conversation != null) {
				conversation.resetUnreadMsgCount();
			}
			updateMsgCount();
			ChatRL.setVisibility(View.GONE);
			closeInput();
			break;
		case R.id.escort_chatinput_ExpressionBtn:
			// start Expression
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
			} else {
				emojiIconContainer.setVisibility(View.VISIBLE);
			}
			closeInput();
			if (chatinputEdit.getVisibility() != View.VISIBLE) {
				chatinputEdit.setVisibility(View.VISIBLE);
				chatinputvoiceBtn.setVisibility(View.GONE);
				isRecord = false;
				voicetextBtn.setBackgroundResource(R.drawable.chatinput_voice_selector);
				chatinputSendBtn.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.escort_chatinput_voicetextBtn:
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
			}
			if (chatinputEdit.getVisibility() != View.VISIBLE) {
				chatinputEdit.setVisibility(View.VISIBLE);
				chatinputvoiceBtn.setVisibility(View.GONE);
				isRecord = false;
				voicetextBtn.setBackgroundResource(R.drawable.chatinput_voice_selector);
				chatinputSendBtn.setVisibility(View.VISIBLE);
			} else {
				closeInput();
				chatinputEdit.setVisibility(View.GONE);
				chatinputvoiceBtn.setVisibility(View.VISIBLE);
				isRecord = true;
				voicetextBtn.setBackgroundResource(R.drawable.chatinput_keyborad_selector);
				chatinputSendBtn.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.escort_arriveBtn:
			hidePopUp();
			Intent intent = new Intent(EscortActivity.this, SetPositionActivity.class);
			startActivityForResult(intent, requestCode);
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == this.requestCode && data != null && resultCode == 20) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				mBaiduMap.clear();
				marketList.clear();

				double lat = bundle.getDouble("lat");
				double lng = bundle.getDouble("lng");
				String adress = bundle.getString("adress");
				arriveTV.setText(adress);
				curLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
				distanceLatLng = new LatLng(lat, lng);
				int distance = (int) DistanceUtil.getDistance(curLatLng, distanceLatLng);
				if (distance <= 1000) {
					String dis = distance + getString(R.string.activity_nearsos_mi);
					overTV.setText(dis);
				} else {
					double distan = distance / 1000.0;
					DecimalFormat df = new DecimalFormat("###.000");
					String dis = df.format(distan) + getString(R.string.activity_nearsos_gongli);
					overTV.setText(dis);
				}
				if (!TextUtils.isEmpty(loc.getCurCity()) && !TextUtils.isEmpty(adress) && !TextUtils.isEmpty(loc.getAddress())) {
					PlanNode stNode = PlanNode.withLocation(beginLatLng);
					stNode.withCityNameAndPlaceName(loc.getCurCity(), loc.getAddress());
					PlanNode enNode = PlanNode.withLocation(distanceLatLng);
					enNode.withCityNameAndPlaceName(loc.getCurCity(), adress);
					DrivingRoutePlanOption planOption = new DrivingRoutePlanOption();
					planOption.from(stNode);
					planOption.to(enNode);

					mPlanResult = null;
					mRoutePlanSuccess = false;
					mSearch.drivingSearch(planOption);

					NotifyLister mNotifyer = new NotifyLister();
					mNotifyer.SetNotifyLocation(lat, lng, 500, "bd09ll");
					loc.startNotify(mNotifyer);
				}

				Trip trip = CoreModel.getInstance().getTrip();
				if (trip != null) {
					TripObject tripObj = new TripObject();
					tripObj.setTripId(trip.getTripId());
					tripObj.setEndAdress(adress);
					tripObj.setEndLat(lat + "");
					tripObj.setEndLng(lng + "");
					sendMessage(YSMSG.REQ_MODIFY_TRIP, 0, 0, tripObj);
				}
			}
		}
	}

	private void drawPlanRoute(String strLat, String strLng, final String address) {

		double lat = 0.0;
		double lng = 0.0;
		try {
			lat = Double.parseDouble(strLat);
			lng = Double.parseDouble(strLng);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (lat == 0.0 || lng == 0.0 || loc == null) {
			return;
		}
		arriveTV.setText(address);
		if (triped) {
			curLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
		} else {
			curLatLng = mTripRunnerCurLatlng;
		}

		distanceLatLng = new LatLng(lat, lng);
		int distance = (int) DistanceUtil.getDistance(curLatLng, distanceLatLng);
		if (distance <= 1000) {
			String dis = distance + getString(R.string.activity_nearsos_mi);
			overTV.setText(dis);
		} else {
			double distan = distance / 1000.0;
			DecimalFormat df = new DecimalFormat("###.000");
			String dis = df.format(distan) + getString(R.string.activity_nearsos_gongli);
			overTV.setText(dis);
		}

		if (mSearch != null) {
			PlanNode stNode = PlanNode.withLocation(beginLatLng);
			// stNode.withCityNameAndPlaceName(loc.getCurCity(),
			// loc.getAddress());
			PlanNode enNode = PlanNode.withLocation(distanceLatLng);
			// enNode.withCityNameAndPlaceName(loc.getCurCity(), address);
			DrivingRoutePlanOption planOption = new DrivingRoutePlanOption();
			planOption.from(stNode);
			planOption.to(enNode);

			mSearch.drivingSearch(planOption);

			// NotifyLister mNotifyer = new NotifyLister();
			// mNotifyer.SetNotifyLocation(lat, lng, 500, "bd09ll");
			// loc.startNotify(mNotifyer);
		}

	}

	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
			// 振动提醒已到设定位置附近
			if (CoreModel.getInstance().getUserInfo() != null) {
				PushMessageObject pushObj = new PushMessageObject();
				pushObj.setTitle(getString(R.string.activity_escort_arrivatetitle));
				String desc = String.format(getString(R.string.activity_escort_arrivatedesc), mlocation.getAddrStr());
				pushObj.setDescription(CoreModel.getInstance().getUserInfo().getNickname() + desc);
				ArrayList<Integer> idList = getUserIdList();
				pushObj.setUseridList(idList);
				JSONObject json = new JSONObject();
				try {
					json.put("userid", CoreModel.getInstance().getUserInfo().getUserId());
					json.put("type", MessageDataObject.TYPE_ARRIVATE);
					json.put("lat", mlocation.getLatitude());
					json.put("lng", mlocation.getLongitude());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				pushObj.setData(json.toString());
				sendMessage(YSMSG.REQ_PUSH_MSG, 0, 0, pushObj);
				if (loc != null) {
					loc.stopNotify();
				}
			}

		}
	}

	OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			// 获取步行线路规划结果
		}

		public void onGetTransitRouteResult(TransitRouteResult result) {
			// 获取公交换乘路径规划结果

		}

		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			// 获取驾车线路规划结果
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				YSToast.showToast(EscortActivity.this, getString(R.string.toast_noresult));
			}

			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				mPlanResult = result;
				mRoutePlanSuccess = true;
			}

			redraw();
		}
	};

	private void redraw() {
		mBaiduMap.clear();
		marketList.clear();
		if (mPlanResult != null) {

			DrivingRouteOverlay overlay = new myDrivingRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(mPlanResult.getRouteLines().get(0));
			overlay.addToMap();
			if (!mRoutePlanSuccess) {
				overlay.zoomToSpan();
			}

		}
		drawLineByLatlngs();
		addMyselfMarker();
		refreshList();
	}

	private void refreshList() {
		sortUser();
		FamilyGridAdapter adapter = (FamilyGridAdapter) familyGrid.getAdapter();
		if (null != adapter) {
			adapter.notifyDataSetChanged();
		}
	}

	private void sortUser() {
		if (null != escortList) {
			Comparator<UserInfo> contactsComparator = new Comparator<UserInfo>() {
				public int compare(UserInfo f1, UserInfo f2) {
					Trip trip = CoreModel.getInstance().getTrip();
					if (trip != null) {
						int tripuserid = trip.getUserInfo().getUserId();
						if (tripuserid == f1.getUserId()) {
							return -1;
						} else if (tripuserid == f2.getUserId()) {
							return 1;
						} else {
							return 0;
						}
					} else {
						return 0;
					}
				}
			};
			Collections.sort(escortList, contactsComparator);
		}
	}

	class myDrivingRouteOverlay extends DrivingRouteOverlay {

		public myDrivingRouteOverlay(BaiduMap arg0) {
			super(arg0);
		}

		public BitmapDescriptor getStartMarker() {
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_curposition);
			return bitmap;
		}

		public BitmapDescriptor getTerminalMarker() {
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_distance);
			return bitmap;
		}

	}

	private void closeInput() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputmanger != null) {
				inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		}
	}

	ArrayList<Integer> exitUserIDList = new ArrayList<Integer>();
	boolean showAllFriendDialog = false;

	private void initGrid() {
		if (trip == null) {
			return;
		}
		exitUserIDList.clear();
		if (triped) {
			List<UserInfo> infolist = new ArrayList<UserInfo>();
			infolist.addAll(trip.getUserInfosList());
			if (infolist.size() == 0 && !(new LocalTripObjectDao().hasLocalTrip())) {
				if (showAllFriendDialog) {
					showAllFriendExitDialog();
					showAllFriendDialog = false;
				}
			}
			getExitList(infolist);
			if (escortList.size() == 0) {
				escortList.addAll(infolist);
			}

			CoreModel.updateLocalFriendList(escortList, new LocalTripObjectDao().getLocalTripList());

			boolean onlyLocalUser = true;
			for (UserInfo userInfo : escortList) {
				if (userInfo.getUserId() != LocalUserObject.LOCAL_USER_ID) {
					onlyLocalUser = false;
					break;
				}
			}
			if (onlyLocalUser) {
				mBottomLayout.setVisibility(View.INVISIBLE);
				// mChatText.setEnabled(false);
				// ChatTV.setEnabled(false);
			} else {
				mBottomLayout.setVisibility(View.VISIBLE);
				// mChatText.setEnabled(true);
				// ChatTV.setEnabled(true);
			}
		} else {
			if (trip.getUserInfo() != null) {
				int tripuserid = trip.getUserInfo().getUserId();
				List<UserInfo> infolist = new ArrayList<UserInfo>();
				infolist.addAll(trip.getUserInfosList());
				getExitList(infolist);
				UserInfo info = CoreModel.getInstance().getFriendByUserid(tripuserid);
				ArrayList<UserInfo> infoarray = new ArrayList<UserInfo>();
				infoarray.addAll(infolist);
				if (info != null) {
					infoarray.add(info);
				}
				if (escortList.size() == 0) {
					escortList = removeUserByUserid(infoarray, CoreModel.getInstance().getUserInfo().getUserId());
				}

				mBottomLayout.setVisibility(View.VISIBLE);
			}
		}

		int length = escortList.size();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		// int itemWidth = (int) (65 * density);
		int itemWidth = DensityUtil.dip2px(mActivity, 65);
		int space = DensityUtil.dip2px(mActivity, 8);
		int gridviewWidth = (space + itemWidth) * (length) - space;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, itemWidth);
		familyGrid.setLayoutParams(params);
		familyGrid.setColumnWidth(itemWidth);
		familyGrid.setNumColumns(length);
		familyGrid.setHorizontalSpacing(space/* (int) density * 32 */);
		FamilyGridAdapter adapter = (FamilyGridAdapter) familyGrid.getAdapter();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	private void showAllFriendExitDialog() {
		DialogHelper.showTwoDialog(EscortActivity.this, true, null, getString(R.string.dialog_friendallexit_content),
				getString(R.string.dialog_friendallexit_yes), getString(R.string.dialog_friendallexit_no), true, new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
							ActivityUtils.moveTaskToFront(mActivity);
						}
					}
				}, new OnClickListener() {
					public void onClick(View v) {
						if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
							ActivityUtils.moveTaskToFront(mActivity);
						}
						showWaitingDialog();
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								Trip trip = CoreModel.getInstance().getTrip();
								if (trip != null) {
									TripObject tripObj = new TripObject();
									tripObj.setTripId(trip.getTripId());
									tripObj.setEndAdress(loc.getAddress());
									tripObj.setEndLat(loc.getLat());
									tripObj.setEndLng(loc.getLng());
									sendMessage(YSMSG.REQ_FINISH_TRIP, 0, 0, tripObj);
								}
							}
						}, 2000);
					}
				});
	}

	private void getExitList(List<UserInfo> infolist) {
		for (UserInfo info : escortList) {
			if (!hasInfo(infolist, info.getUserId())) {
				exitUserIDList.add(info.getUserId());
			}
		}
	}

	private boolean hasInfo(List<UserInfo> infolist, int userid) {
		for (UserInfo newinfo : infolist) {
			if (newinfo.getUserId() == userid) {
				return true;
			}
		}
		return false;
	}

	public static Bitmap convertToBlackWhite(Bitmap bmp) {
		try {
			int width = bmp.getWidth(); // 获取位图的宽
			int height = bmp.getHeight(); // 获取位图的高
			int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
			bmp.getPixels(pixels, 0, width, 0, 0, width, height);
			int alpha = 0xFF << 24;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					int grey = pixels[width * i + j];

					int red = ((grey & 0x00FF0000) >> 16);
					int green = ((grey & 0x0000FF00) >> 8);
					int blue = (grey & 0x000000FF);

					grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
					grey = alpha | (grey << 16) | (grey << 8) | grey;
					pixels[width * i + j] = grey;
				}
			}
			Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);
			newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
			Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
			return resizeBmp;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	class FamilyGridAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		int userid;

		public FamilyGridAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {

			return escortList.size();
		}

		public UserInfo getItem(int position) {

			return escortList.get(position);
		}

		public long getItemId(int position) {

			return position;
		}

		public void setSelection(int userid) {
			this.userid = userid;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.family_griditem, null);
				holder.contactimg = (ImageView) convertView.findViewById(R.id.griditem_contact_img);
				holder.contactRL = (RelativeLayout) convertView.findViewById(R.id.griditem_contactRL);
				holder.contactexitimg = (ImageView) convertView.findViewById(R.id.griditem_contact_exitimg);
				holder.contactname = (TextView) convertView.findViewById(R.id.griditem_contact_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			UserInfo info = escortList.get(position);
			UserObject userobj = UserObject.createFromPb(info);

			if (info.getUserId() == LocalUserObject.LOCAL_USER_ID) {

			} else {
				if (userobj.isLocationValid()) {
					if (userobj.isAllowAccessLoaction()) {
						if (position >= 0 && position < mAvatarImageViewList.size()) {
							addMarker(userobj.getUserId(), userobj.getPhone(), userobj.getDisplayImage(), userobj.getLat2(), userobj.getLng2(),
									mAvatarImageViewList.get(position));
						} else {
							addMarker(userobj.getUserId(), userobj.getPhone(), userobj.getDisplayImage(), userobj.getLat2(), userobj.getLng2(),
									holder.contactimg);
						}
					}
				}
			}

			if (!TextUtils.isEmpty(userobj.getUploadImage())) {
				if (userobj.getUserId() != tripRunnerUserId && exitUserIDList.contains(info.getUserId())) {
					BlackWhiteProcessor process = new BlackWhiteProcessor();
					ImageLoaderHelper.displayImage(userobj.getDisplayImage(), holder.contactimg, R.drawable.icon_family, process, true);
				} else {
					ImageLoaderHelper.displayImage(userobj.getDisplayImage(), holder.contactimg, R.drawable.icon_family, true);
				}
			} else {
				holder.contactimg.setImageResource(R.drawable.icon_family);
			}

			if (!TextUtils.isEmpty(userobj.getNickname())) {
				holder.contactname.setText(userobj.getNickname());
			}
			if (this.userid == userobj.getUserId()) {
				holder.contactexitimg.setVisibility(View.VISIBLE);
				holder.contactexitimg.setBackgroundResource(R.drawable.startescort_griditem_selectedbg);
			} else {
				holder.contactexitimg.setVisibility(View.INVISIBLE);
				holder.contactexitimg.setBackgroundResource(R.drawable.escort_family_exitbg);
			}
			holder.contactimg.setTag("img" + position);
			OnClick listener = new OnClick();
			listener.setPosition(position);
			convertView.setOnClickListener(listener);
			return convertView;
		}

		public final class ViewHolder {
			public ImageView contactimg;
			public ImageView contactexitimg;
			public TextView contactname;
			public RelativeLayout contactRL;
		}

		class OnClick implements OnClickListener {
			int position;

			public void setPosition(int position) {
				this.position = position;
			}

			public void onClick(View v) {
				ImageView img = (ImageView) familyGrid.findViewWithTag("img" + position);
				UserInfo info = escortList.get(position);
				if (info != null) {
					curuserId = info.getUserId();
					setSelection(curuserId);
					if (curp == position) {
						hidePopUp();
						curp = -1;
					} else {
						showPopUp(img);
						curp = position;
					}
					curuserinfo = info;
				}
			}
		}
	}

	int curp = -1;

	private void showPopUp(View v) {
		if (null != popupWindow) {
			int[] location = new int[2];
			v.getLocationInWindow(location);
			int imageTop = location[1];
			int imageleft = location[0];
			// int wndHeight = popupWindow.getHeight();
			// int newTop = imageTop - wndHeight;
			popupWindow.setVisibility(View.VISIBLE);
			MarginLayoutParams margin = new MarginLayoutParams(popupWindow.getLayoutParams());
			margin.setMargins(imageleft - v.getWidth() / 4, imageTop - v.getHeight() / 2 - popupWindow.getHeight(), 0, 0);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
			popupWindow.setLayoutParams(layoutParams);
			// popupWindow.layout(imageleft, imageTop, imageleft + w, imageTop +
			// h);
		}
	}

	private void hidePopUp() {
		if (null != popupWindow) {
			if (popupWindow.isShown()) {
				popupWindow.setVisibility(View.INVISIBLE);
				curp = -1;
			}
		}
	}

	boolean markerAdded = false;

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_GET_TRIP_INFO: {
			if (msg.arg1 == 200) {
				Trip newtrip = (Trip) msg.obj;
				if (newtrip != null) {
					trip = newtrip;
				}
				if (trip != null && CoreModel.getInstance().getUserInfo() != null) {
					if (trip.getUserInfo() != null && trip.getUserInfo().getLocation() != null) {
						try {
							mTripRunnerCurLatlng = new LatLng(Double.valueOf(trip.getUserInfo().getLocation().getLat()), Double.valueOf(trip.getUserInfo()
									.getLocation().getLng()));
						} catch (Exception e) {
						}
					}
					updateInfoView();
					int tripuserid = trip.getUserInfo().getUserId();
					tripRunnerUserId = tripuserid;
					int myid = CoreModel.getInstance().getUserInfo().getUserId();
					if (tripuserid == myid) {
						triped = true;
					}
					changeViewByTriped();
					CoreModel.getInstance().setTrip(trip);
					initGrid();
					try {
						beginLatLng = new LatLng(Double.valueOf(trip.getBeginLocation().getLat()), Double.valueOf(trip.getBeginLocation().getLng()));
					} catch (Exception e) {
					}
					if (markerAdded) {
						// for (UserInfo user : escortList) {
						// UserObject userObj = UserObject.createFromPb(user);
						// refreshMapMarker(userObj.getUserId(),
						// userObj.getPhone());
						// }
						// if (CoreModel.getInstance().getUserInfo() != null) {
						// refreshMapMarker(CoreModel.getInstance().getUserInfo().getUserId(),
						// CoreModel.getInstance().getUserInfo().getPhone());
						// }
					} else {
						// for (UserInfo user : escortList) {
						// UserObject userObj = UserObject.createFromPb(user);
						// addMarker(userObj.getUserId(), userObj.getImage(),
						// userObj.getLat2(), userObj.getLng2());
						// }
						markerAdded = true;
					}

					if (trip.hasEndLocation() && !triped) {
						String strLat = trip.getEndLocation().getLat();
						String strLng = trip.getEndLocation().getLng();
						String address = trip.getEndLocation().getAddress();
						drawPlanRoute(strLat, strLng, address);
					}

					if (!triped) {
						if (trip.getUserInfo() != null) {
							int index = -1;
							UserInfo friend = null;
							for (int i = 0; i < escortList.size(); i++) {
								friend = escortList.get(i);
								if (trip.getUserInfo().getUserId() == friend.getUserId()) {
									index = i;
									break;
								}
							}
							if (index != -1) {
								ArrayList<UserInfo> newList = new ArrayList<UserInfo>();
								for (int i = 0; i < escortList.size(); i++) {
									if (i == index) {
										newList.add(trip.getUserInfo());
									} else {
										newList.add(escortList.get(i));
									}
								}
								escortList.clear();
								escortList.addAll(newList);
							}
						}
					}

					redraw();

				}
			}
		}
			break;
		case YSMSG.RESP_FINISH_TRIP:
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 成功
				new LocalTripObjectDao().clear();
				CoreModel.getInstance().setEscorting(false);
				CoreModel.getInstance().setTrip(null);
				EscortActivity.this.finish();
			} else {
				// 失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
			break;
		case YSMSG.RESP_EXIT_TRIP:
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 成功
				CoreModel.getInstance().setEscorting(false);
				CoreModel.getInstance().setTrip(null);
				EscortActivity.this.finish();
			} else {
				// 失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
			break;

		case YSMSG.RESP_PUSH_MSG: {
			if (msg.arg1 == 200) {
				// 成功

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
		case YSMSG.REQ_REFRESH_TRIP_FROM_MAIN: {
			refreshTripInfo();
		}
			break;
		case YSMSG.REQ_FINISH_TRIP_FROM_MAIN: {
			CoreModel.getInstance().setEscorting(false);
			CoreModel.getInstance().setTrip(null);
			EscortActivity.this.finish();
		}
			break;
		case RECORD_CHANGENUM:
			if (recordCount == 0) {
				if (!recordIsSend) {
					mHandler.removeCallbacks(recordrunnable);
					chatinputvoiceBtn.setText(getString(R.string.mainfragment_ChatVoiceBtnText));
					recordinganim.stop();
					recordWindow.dismiss();
					if (wakeLock.isHeld()) {
						wakeLock.release();
					}
					// stop recording and send voice file
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername), Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(this.getApplicationContext(), "无录音权限", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(this.getApplicationContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(this, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
					}
					recordCount = 120;
				}
			} else if (recordCount <= 60) {
				recordingCount.setText(recordCount + "'");
				recordingCount.setVisibility(View.VISIBLE);
			} else {
				recordingCount.setVisibility(View.GONE);
			}
			break;
		case YSMSG.RESP_GET_USER_LOCATION: {
			if (msg.arg1 == 200) {
				UserObject info = (UserObject) msg.obj;
				if (info != null) {
					UserInfo updateinfo = null;
					int index = -1;
					for (int i = 0; i < escortList.size(); i++) {
						UserInfo friend = escortList.get(i);
						if (info.getUserId() == friend.getUserId()) {
							UserInfo.Builder ub = friend.toBuilder();
							Location.Builder lb = Location.newBuilder();
							lb.setAddress(info.getLocation());
							lb.setUpdateTime(info.getLocationChangeTime());
							lb.setLat(info.getLat());
							lb.setLng(info.getLng());
							ub.setLocation(lb.build());
							updateinfo = ub.build();
							index = i;
						}
					}
					if (updateinfo != null && index != -1) {
						ArrayList<UserInfo> newList = new ArrayList<UserInfo>();
						for (int i = 0; i < escortList.size(); i++) {
							if (i == index) {
								newList.add(updateinfo);
							} else {
								newList.add(escortList.get(i));
							}
						}
						escortList.clear();
						escortList.addAll(newList);
					}

					if (mBaiduMap.getMapStatus().zoom != DEFAULT_ZOOM) {
						MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(DEFAULT_ZOOM);
						mBaiduMap.setMapStatus(msu);
					}

					refreshMapMarker(info.getUserId(), info.getPhone(), true);
					LatLng latlng = new LatLng(info.getLat2(), info.getLng2());
					showCenter(latlng);
				}

			} else {
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(EscortActivity.this, result.getErrorMsg());
				} else {
					YSToast.showToast(EscortActivity.this, R.string.network_error);
				}
			}
			FamilyGridAdapter adapter = (FamilyGridAdapter) familyGrid.getAdapter();
			if (null != adapter) {
				sortUser();
				adapter.notifyDataSetChanged();
			}
		}
			break;
		}
	}

	public void refreshTripInfo() {
		if (CoreModel.getInstance().getTrip() != null) {
			Trip trip = CoreModel.getInstance().getTrip();
			sendMessage(YSMSG.REQ_GET_TRIP_INFO, 0, trip.getTripId(), null);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (ChatRL.getVisibility() == View.VISIBLE) {
				if (conversation != null) {
					conversation.resetUnreadMsgCount();
				}
				updateMsgCount();
				ChatRL.setVisibility(View.GONE);
				closeInput();
				return true;
			}
			if (triped) {
				DialogHelper.showTwoDialog(EscortActivity.this, false, null, getString(R.string.dialog_finishTriped_content),
						getString(R.string.dialog_finishTriped_yes), null, true, new OnClickListener() {
							public void onClick(View arg0) {
								stopTrip();
							}
						}, null);
			} else {
				CoreModel.getInstance().setEscorting(true);
				finish();
			}
			return false;
		}
		return false;

	}

	boolean recordIsSend = false;
	public static final int RECORD_CHANGENUM = 2;
	int recordCount = 120;
	Runnable recordrunnable = new Runnable() {
		public void run() {
			mHandler.sendEmptyMessage(RECORD_CHANGENUM);
			mHandler.postDelayed(recordrunnable, 1000);
			recordCount--;
		}
	};

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					Toast.makeText(EscortActivity.this, "发送语音需要sdcard支持！", Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					mHandler.post(recordrunnable);
					recordIsSend = false;
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingTip.setText(getString(R.string.move_up_to_cancel));
					chatinputvoiceBtn.setText(getString(R.string.mainfragment_ChatVoiceBtnedText));
					recordinganim.start();
					recordWindow.showAtLocation(ChatRL, Gravity.CENTER, 0, 0);
					voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordinganim.stop();
					recordWindow.dismiss();
					Toast.makeText(EscortActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingTip.setText(getString(R.string.release_to_cancel));
					// chatinputvoiceBtn.setText(getString(R.string.release_to_cancel));
				} else {
					recordingTip.setText(getString(R.string.move_up_to_cancel));
					// chatinputvoiceBtn.setText(getString(R.string.move_up_to_cancel));
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				if (!recordIsSend) {
					mHandler.removeCallbacks(recordrunnable);
					recordCount = 120;
					v.setPressed(false);
					recordinganim.stop();
					recordWindow.dismiss();
					if (wakeLock.isHeld())
						wakeLock.release();
					if (event.getY() < 0) {
						// discard the recorded audio.
						voiceRecorder.discardRecording();

					} else {
						// stop recording and send voice file
						try {
							int length = voiceRecorder.stopRecoding();
							if (length > 0) {
								sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername), Integer.toString(length), false);
							} else if (length == EMError.INVALID_FILE) {
								Toast.makeText(getApplicationContext(), "无录音权限", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(EscortActivity.this, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
						}
					}
				}
				return true;
			default:
				recordinganim.stop();
				recordWindow.dismiss();
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	private void loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		// 过滤掉messages seize为0的conversation
		int count = 0;
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getIsGroup() && conversation.getUserName().equals(toChatUsername)) {
				count = conversation.getUnreadMsgCount();
				if (count > 0) {
					if (count > 99) {
						mNewMsgCount = "N";
					} else {
						mNewMsgCount = count + "";
					}
				} else {
					mNewMsgCount = "";
				}
				break;
			}
		}
	}

	private void updateMsgCount() {
		int count = 0;
		EMConversation conversation = EMChatManager.getInstance().getConversation(toChatUsername);
		if (conversation != null) {
			count = conversation.getUnreadMsgCount();
			if (count > 0) {
				mTxtNewMsgCount.setVisibility(View.VISIBLE);
				if (count > 99) {
					mNewMsgCount = "N";
				} else {
					mNewMsgCount = count + "";
				}
			} else {
				mTxtNewMsgCount.setVisibility(View.GONE);
				mNewMsgCount = "";
			}
		}

		mTxtNewMsgCount.setText(mNewMsgCount);
	}

	/**
	 * 消息广播接收者
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String username = intent.getStringExtra("from");
			String msgid = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgid);
			// 如果是群聊消息，获取到group id
			if (message.getChatType() == ChatType.GroupChat) {
				username = message.getTo();
				// 记得把广播给终结掉
				abortBroadcast();
			} else {
				return;
			}

			updateMsgCount();

			// if (!username.equals(toChatUsername)) {
			// // 消息不是发给当前会话，return
			// notifyNewMessage(message);
			// return;
			// }
			// conversation =
			// EMChatManager.getInstance().getConversation(toChatUsername);
			// 通知adapter有新消息，更新ui
			if (adapter != null) {
				adapter.refresh();
			}
			chatListView.setSelection(chatListView.getCount() - 1);

		}
	}

	/**
	 * 消息送达BroadcastReceiver
	 */
	private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();

			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isDelivered = true;
				}
			}

			adapter.notifyDataSetChanged();
		}
	};

	/**
	 * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下 如果不需要，注释掉即可
	 * 
	 * @param message
	 */
	protected void notifyNewMessage(EMMessage message) {
		// 如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
		// 以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
		if (!EasyUtils.isAppRunningForeground(this)) {
			return;
		}

		String ticker = CommonUtils.getMessageDigest(message, this);
		if (message.getType() == Type.TXT)
			ticker = ticker.replaceAll("\\[.{2,3}\\]", getString(R.string.mainfragment_Chattip1));

		NotificationHelper.getInstance().showOrUpdateNotification(NotifyId.ID_CHAT_MESSAGE, getString(R.string.mainfragment_Chattip2),
				getString(R.string.mainfragment_Chattip3), message.getFrom() + ": " + ticker, 0, true, new Intent());
	}

	/**
	 * listview滑动监听listener
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
					// loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						if (chatType == CHATTYPE_SINGLE)
							messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
						else
							messages = conversation.loadMoreGroupMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
					} catch (Exception e1) {
						// loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						adapter.notifyDataSetChanged();
						chatListView.setSelection(messages.size() - 1);
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					// loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		}

	}

	private void startChat() {
		if (trip == null || trip.getIm() == null) {
			return;
		}
		haveMoreData = true;
		isloading = false;
		chatType = CHATTYPE_GROUP;
		// toChatUsername = "1420544461857";
		toChatUsername = "" + trip.getIm().getGroupId();

		conversation = EMChatManager.getInstance().getConversation(toChatUsername);
		// 把此会话的未读数置为0
		conversation.resetUnreadMsgCount();
		adapter = new MessageAdapter(this, toChatUsername, chatType);
		// 显示消息
		chatListView.setAdapter(adapter);
		chatListView.setOnScrollListener(new ListScrollListener());
		int count = chatListView.getCount();
		if (count > 0) {
			chatListView.setSelection(count - 1);
		}

		chatListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				emojiIconContainer.setVisibility(View.GONE);
				return false;
			}
		});

		// EMGroup group =
		// EMGroupManager.getInstance().getGroup(toChatUsername);
		// if (group != null) {
		// List<String> members = group.getMembers();
		// if (members != null) {
		// for (int i = 0; i < members.size(); i++) {
		// nicknameArray += CoreModel.getInstance().getNickname(members.get(i));
		// if (i < members.size() - 1) {
		// nicknameArray += ", ";
		// }
		// }
		// }
		// }
		String chatTitle = getString(R.string.activity_escort_chattitile);
		TitleTV.setText(chatTitle);
		String nicknameArray = "";
		chatTitle = String.format(chatTitle, nicknameArray);

	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	private void sendText(String content) {
		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP)
				message.setChatType(ChatType.GroupChat);
			TextMessageBody txtBody = new TextMessageBody(content);
			// 设置消息body
			message.addBody(txtBody);
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(toChatUsername);
			// 把messgage加到conversation中
			conversation.addMessage(message);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			adapter.refresh();
			chatListView.setSelection(chatListView.getCount() - 1);
			chatinputEdit.setText("");

		}
	}

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP)
				message.setChatType(ChatType.GroupChat);
			message.setReceipt(toChatUsername);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
			message.addBody(body);

			conversation.addMessage(message);
			adapter.refresh();
			chatListView.setSelection(chatListView.getCount() - 1);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(EscortActivity.this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (chatinputvoiceBtn.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName("cn.changl.safe360.android.im.hx.utils.SmileUtils");
							Field field = clz.getField(filename);
							chatinputEdit.append(SmileUtils.getSmiledText(EscortActivity.this, (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(chatinputEdit.getText())) {

								int selectionStart = chatinputEdit.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = chatinputEdit.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											chatinputEdit.getEditableText().delete(i, selectionStart);
										else
											chatinputEdit.getEditableText().delete(selectionStart - 1, selectionStart);
									} else {
										chatinputEdit.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}
}
