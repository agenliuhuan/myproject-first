package cn.changl.safe360.android.ui.main;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import mobi.dlys.android.core.image.universalimageloader.core.process.BitmapProcessor;
import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.net.extendcmp.ojm.OJMFactory;
import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.HandlerUtils.MessageListener;
import mobi.dlys.android.core.utils.ImageUtils;
import mobi.dlys.android.core.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import cn.changl.safe360.android.biz.vo.ContactsObject;
import cn.changl.safe360.android.biz.vo.LocalUserObject;
import cn.changl.safe360.android.biz.vo.MessageDataObject;
import cn.changl.safe360.android.biz.vo.MessageObject;
import cn.changl.safe360.android.biz.vo.PushMessageObject;
import cn.changl.safe360.android.biz.vo.PushMsgObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.TripObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.db.DatabaseManager;
import cn.changl.safe360.android.db.dao.LocalUserObjectDao;
import cn.changl.safe360.android.db.dao.MessageObjectDao;
import cn.changl.safe360.android.db.dao.PushMsgObjectDao;
import cn.changl.safe360.android.im.hx.utils.CommonUtils;
import cn.changl.safe360.android.im.hx.utils.SmileUtils;
import cn.changl.safe360.android.map.BaiduLoc;
import cn.changl.safe360.android.map.BaiduLocListener;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.receiver.ConnectionChangeReceiver;
import cn.changl.safe360.android.sensor.SensorManage;
import cn.changl.safe360.android.service.PushMsgHandleService;
import cn.changl.safe360.android.service.PushMsgHandleService.CMD;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.DialogHelper;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSAlertDialog;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.ui.escort.EscortActivity;
import cn.changl.safe360.android.ui.escort.StartEscortActivity;
import cn.changl.safe360.android.ui.family.AddFamilyActivity;
import cn.changl.safe360.android.ui.login.SplashActivity;
import cn.changl.safe360.android.ui.message.MessageDetailActivity;
import cn.changl.safe360.android.ui.sos.SOSActivity;
import cn.changl.safe360.android.utils.DateUtils;
import cn.changl.safe360.android.utils.ExtraName;
import cn.changl.safe360.android.utils.ImageLoaderHelper;
import cn.changl.safe360.android.utils.MyAnimationUtils;
import cn.changl.safe360.android.utils.NotificationHelper;
import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.android.utils.TelephonyUtils;
import cn.changl.safe360.android.utils.UpdateVersionUtils;
import cn.changl.safe360.android.utils.VibratorManager;
import cn.changl.safe360.android.utils.umeng.AnalyticsHelper;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Location;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Trip;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.EasyUtils;
import com.easemob.util.VoiceRecorder;

public class MainActivity extends BaseExActivity implements MessageListener, OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	SensorManage sensorManager;
	PushMessageObject escortMsg;

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;

	protected TitleBarHolder mTitleBar;
	private Button showFamilyBtn;
	private Button addFamilyBtn;
	private Button editFamilyBtn;
	private ListView mFamilyList;
	private LinearLayout mFamilyLL;
	private LinearLayout ChatLL;
	private LinearLayout PhoneLL;
	private LinearLayout BottomLL;
	private RelativeLayout escortRL;
	private ImageView escortAnim;
	private ImageView escortText;

	private TextView mTempLocation;

	private RelativeLayout ChatRL;

	private TextView chatinputTopTitle;
	private Button chatinputSendBtn;
	private EditText chatinputEdit;
	private Button chatinputvoiceBtn;
	private Button expressionBtn;
	private Button voicetextBtn;
	private ListView chatListView;
	private Button ClostChatBtn;

	private TextView recordingTip;
	private TextView recordingCount;
	private ImageView recordingimg;
	private AnimationDrawable recordinganim;

	boolean isRecord = false;
	boolean isEdit = false;
	Animation rotateAnimation1;
	Animation rotateAnimation2;
	Animation rotateAnimation3;

	ArrayList<UserInfo> userList;
	// 当前选择的用户
	private UserInfo curUserInfo;

	PopupWindow popupWindow;
	Button ShowMyLocationBtn;

	String mUrl;

	private HashMap<Integer, String> mMsgCountMap;

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

	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	List<Marker> marketList;

	private ImageView mMySelfAvatar;
	private boolean mInitMapAvatar = false;

	private View mPopView;
	private Button refreshBtn;
	private InfoWindow mInfoWindow;
	private Marker mInfoMarker;
	private ConnectionChangeReceiver mNetworkStateReceiver = new ConnectionChangeReceiver();

	private boolean mShowSOSAddFamilyDialog;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, MainActivity.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getBoolean(ExtraName.EN_RELOGIN)) {
			SplashActivity.startActivity(MainActivity.this);
			finish();
		}

		if (!CoreModel.getInstance().isLogined()) {
			SplashActivity.startActivity(MainActivity.this);
			finish();
		}
		setContentView(R.layout.activity_main);
		initView();
		initSubView();
		initPopWindow();
		showGuide();
		initData();
	}

	private void showGuide() {
		final RelativeLayout mainGuide = (RelativeLayout) findViewById(R.id.layout_main_guide);
		Button mainGuideBtn = (Button) findViewById(R.id.layout_main_guide_knowBtn);
		boolean show = PreferencesUtils.getShowGuideValue("mainGuide");
		if (show) {
			mainGuide.setVisibility(View.VISIBLE);
		} else {
			sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
		}
		mainGuideBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mainGuide.setVisibility(View.GONE);
				PreferencesUtils.setShowGuideValue("mainGuide", false);
				sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
			}
		});
		mainGuide.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});
	}

	private void initPopWindow() {
		View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.recording_popview, null);
		if (view != null) {
			recordingTip = (TextView) view.findViewById(R.id.txt_recording_tip);
			recordingCount = (TextView) view.findViewById(R.id.txt_recording_count);
			recordingimg = (ImageView) view.findViewById(R.id.img_recording_tip);
			recordinganim = (AnimationDrawable) recordingimg.getBackground();
		}
		DisplayMetrics dm = new DisplayMetrics();
		MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		popupWindow = new PopupWindow(view, (int) density * 260, (int) density * 270);
	}

	int mTickCount = 0;

	private void initSubView() {
		manager = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
		mTitleBar = new TitleBarHolder(MainActivity.this);
		mTitleBar.mTitle.setText(R.string.app_name);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_home_selector);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				LeftMenuActivity.startActivity(MainActivity.this);
				MainActivity.this.overridePendingTransition(R.anim.translate_between_interface_left_in, R.anim.translate_between_interface_right_out);
			}
		});

		showFamilyBtn = (Button) findViewById(R.id.layout_main_showfamilyBtn);
		mFamilyList = (ListView) findViewById(R.id.layout_main_familyList);
		mFamilyLL = (LinearLayout) findViewById(R.id.layout_main_familyListLL);
		addFamilyBtn = (Button) findViewById(R.id.layout_main_addfamilyBtn);
		editFamilyBtn = (Button) findViewById(R.id.layout_main_editfamilyBtn);
		showFamilyBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mFamilyLL.getVisibility() != View.VISIBLE) {
					MyAnimationUtils.showLinearLayout(mFamilyLL, showFamilyBtn, MainActivity.this);
				} else {
					MyAnimationUtils.hideLinearLayout(mFamilyLL, showFamilyBtn, MainActivity.this);
				}
			}
		});
		addFamilyBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (CoreModel.getInstance().checkUserLogined(MainActivity.this)) {
					if (userList.size() >= 9) {
						YSToast.showToast(MainActivity.this, R.string.mainfragment_notmore);
					} else {
						AddFamilyActivity.startActivity(MainActivity.this);
					}
				}
			}
		});
		editFamilyBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isEdit) {
					isEdit = false;
					editFamilyBtn.setBackgroundResource(R.drawable.mainfragment_editfamily_btn);
				} else {
					isEdit = true;
					editFamilyBtn.setBackgroundResource(R.drawable.mainfragment_editedfamily_btn);
				}
				refreshFamilyAdapter();
			}
		});
		rotateAnimation1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.view_rotate_loop);
		rotateAnimation2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.view_rotate_loop);
		rotateAnimation3 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.view_rotate_loop_2);
		userList = new ArrayList<UserInfo>();
		FamilyAdapter adapter = new FamilyAdapter(MainActivity.this);
		mFamilyList.setAdapter(adapter);
		mFamilyList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FamilyAdapter adapter = (FamilyAdapter) mFamilyList.getAdapter();
				if (adapter == null) {
					return;
				}
				UserInfo info = adapter.getItem(position);
				if (info != null && info.getUserId() == LocalUserObject.LOCAL_USER_ID) {
					mTempLocation.setText(R.string.mainfragment_locationTV);
				} else {
					mTempLocation.setText(R.string.mainfragment_chatTV);
				}

				if (info != null) {
					curUserInfo = info;
					if (info.getIm() != null) {
						toChatUsername = info.getIm().getUsername();
					}

					if (info.getUserId() != LocalUserObject.LOCAL_USER_ID) {
						refreshMapMarker(info.getUserId(), info.getPhone(), true);
						showMapInfoWindow(info.getUserId(), info.getPhone());
					} else {
						if (info.getLocation() == null || TextUtils.isEmpty(info.getLocation().getAddress())) {
							// YSToast.showToast(mActivity, "暂时获取不到对方的位置信息");
							showWaitingDialog();
							sendMessage(YSMSG.REQ_GET_TEMP_FRIEND_LOCATION, 0, 0, info.getPhone());
						} else {
							refreshMapMarker(info.getUserId(), info.getPhone(), true);
							showMapInfoWindow(info.getUserId(), info.getPhone());
						}
					}

					boolean isEscort = CoreModel.getInstance().hasEscorting();
					if (CoreModel.getInstance().getTrip() != null && isEscort) {
						EscortActivity.startActivity(MainActivity.this, false);
					}

					if (info.getIm() == null || TextUtils.isEmpty(getMsgCount(info.getUserId()))) {
						if (adapter.curposition == position) {
							if (BottomLL.getVisibility() != View.VISIBLE) {
								MyAnimationUtils.showBottomView(BottomLL, MainActivity.this);
							} else {
								MyAnimationUtils.hideBottomView(BottomLL, MainActivity.this);
							}
						} else {
							if (BottomLL.getVisibility() != View.VISIBLE) {
								MyAnimationUtils.showBottomView(BottomLL, MainActivity.this);
							}
						}
					} else {
						showChatPanel(info.getIm().getUsername());
					}
				}

				adapter.curposition = position;
				refreshFamilyAdapter();
			}
		});
		BottomLL = (LinearLayout) findViewById(R.id.mainfragment_bottomLL);
		ChatLL = (LinearLayout) findViewById(R.id.mainfragment_bottomChatLL);
		chatinputTopTitle = (TextView) findViewById(R.id.layout_main_chatTitle);
		PhoneLL = (LinearLayout) findViewById(R.id.mainfragment_bottomPhoneLL);
		escortRL = (RelativeLayout) findViewById(R.id.mainfragment_escortRL);
		escortAnim = (ImageView) findViewById(R.id.mainfragment_escortanim_img);
		escortText = (ImageView) findViewById(R.id.mainfragment_escortext);
		escortRL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (CoreModel.getInstance().checkUserLogined(MainActivity.this)) {
					if (userList.size() == 0) {
						showTripAddFamilyDialog();
					} else {
						boolean isEscort = CoreModel.getInstance().hasEscorting();
						// 0空闲 2护航
						if (CoreModel.getInstance().getTrip() != null && isEscort) {
							EscortActivity.startActivity(MainActivity.this, false);
						} else {
							StartEscortActivity.startActivity(MainActivity.this);
						}
					}

				}
			}
		});

		ChatRL = (RelativeLayout) findViewById(R.id.layout_main_chatRL);
		ChatRL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});
		chatinputSendBtn = (Button) findViewById(R.id.layout_main_chatinput_SendBtn);
		chatinputEdit = (EditText) findViewById(R.id.layout_main_chatinput_edit);
		chatinputvoiceBtn = (Button) findViewById(R.id.layout_main_chatinput_voiceBtn);
		expressionBtn = (Button) findViewById(R.id.layout_main_chatinput_ExpressionBtn);
		voicetextBtn = (Button) findViewById(R.id.layout_main_chatinput_voicetextBtn);

		chatListView = (ListView) findViewById(R.id.layout_main_chatList);
		// chatListView.setPullLoadEnable(false);
		// chatListView.setPullRefreshEnable(false);
		ClostChatBtn = (Button) findViewById(R.id.layout_main_chatRLColseBtn);
		ChatLL.setOnClickListener(this);
		PhoneLL.setOnClickListener(this);
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
		chatinputEdit.clearFocus();
		setUpMapIfNeeded();
		ShowMyLocationBtn = (Button) findViewById(R.id.mainfragment_myLocationBtn);
		ShowMyLocationBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				locationMeSelf(true);
			}
		});

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
		chatinputEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				emojiIconContainer.setVisibility(View.GONE);
			}
		});

		mTempLocation = (TextView) findViewById(R.id.txt_mainfragment_location);
	}

	private void locationMeSelf(boolean showcenter) {
		if (BottomLL.isShown()) {
			MyAnimationUtils.hideBottomView(BottomLL, MainActivity.this);
		}
		if (CoreModel.getInstance().getUserInfo() != null) {
			UserObject userObj = CoreModel.getInstance().getUserInfo();
			int userid = userObj.getUserId();
			BaiduLoc loc = App.getInstance().getLocater();
			if (loc != null) {
				LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
				userObj.setLocation(loc.getAddress());
				userObj.setLng(loc.getLng());
				userObj.setLat(loc.getLat());
			}
			CoreModel.getInstance().setUserInfo(userObj);
			// showCenter(latlng);
			refreshMapMarker(userid, userObj.getPhone(), showcenter);
			showMapInfoWindow(userid, userObj.getPhone());
			sendEmptyMessage(YSMSG.REQ_MODIFY_LOCATION);
		} else {
			BaiduLoc loc = App.getInstance().getLocater();
			if (loc != null) {
				LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
				addMarker(-1, "", "", loc.getLatitude(), loc.getLongitude());
				showMapInfoWindow(-1, "");
				showCenter(latlng);
			}
		}

	}

	private boolean checkIsEscorting(int userid) {
		if (CoreModel.getInstance().getTrip() != null) {
			Trip trip = CoreModel.getInstance().getTrip();
			if (trip != null) {
				List<UserInfo> infoList = trip.getUserInfosList();
				for (UserInfo info : infoList) {
					if (info.getUserId() == userid || userid == trip.getUserInfo().getUserId()) {
						return true;
					}
				}
			}

			return false;
		} else {
			return false;
		}
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
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					Toast.makeText(MainActivity.this, "发送语音需要sdcard支持！", Toast.LENGTH_SHORT).show();
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
					popupWindow.showAtLocation(ChatRL, Gravity.CENTER, 0, 0);
					voiceRecorder.startRecording(null, toChatUsername, MainActivity.this.getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordinganim.stop();
					popupWindow.dismiss();
					Toast.makeText(MainActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
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
					recordIsSend = true;
					mHandler.removeCallbacks(recordrunnable);
					recordCount = 120;
					chatinputvoiceBtn.setText(getString(R.string.mainfragment_ChatVoiceBtnText));
					v.setPressed(false);
					recordinganim.stop();
					popupWindow.dismiss();
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
								Toast.makeText(MainActivity.this.getApplicationContext(), "无录音权限", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(MainActivity.this.getApplicationContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(MainActivity.this, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
						}
					}
				}
				return true;
			default:
				recordinganim.stop();
				popupWindow.dismiss();
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mainfragment_bottomChatLL:
			if (curUserInfo != null && curUserInfo.getUserId() == LocalUserObject.LOCAL_USER_ID) {
				showWaitingDialog();
				sendMessage(YSMSG.REQ_INVITE_TEMP_FRIEND, 0, 0, curUserInfo.getPhone());
			} else {
				startChat();
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

			break;
		case R.id.mainfragment_bottomPhoneLL:
			if (curUserInfo != null) {
				showCallPhoneDialog(curUserInfo.getPhone());
			}
			break;
		case R.id.layout_main_chatinput_SendBtn: {
			// send text
			sendText(chatinputEdit.getEditableText().toString());
		}
			break;
		case R.id.layout_main_chatRLColseBtn:
			if (conversation != null) {
				conversation.resetUnreadMsgCount();
			}
			ChatRL.setVisibility(View.GONE);
			closeInput();
			break;
		case R.id.layout_main_chatinput_ExpressionBtn:
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
		case R.id.layout_main_chatinput_voicetextBtn:
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
		}
	}

	protected void showSendSMSDialog(final String phone) {
		DialogHelper.showTwoDialog(MainActivity.this, true, null, getString(R.string.dialog_mainsendsms_content), getString(R.string.dialog_mainsendsms_yes),
				getString(R.string.dialog_mainsendsms_no), true, new OnClickListener() {
					public void onClick(View v) {
						TelephonyUtils.sendSms(MainActivity.this, phone, getString(R.string.mainfragment_smscontent), false);
					}
				}, null);
	}

	protected void showCallPhoneDialog(final String phone) {
		DialogHelper.showTwoDialog(MainActivity.this, true, null, phone, getString(R.string.dialog_callphone_yes), getString(R.string.dialog_callphone_no),
				true, new OnClickListener() {
					public void onClick(View v) {
						TelephonyUtils.call(MainActivity.this, phone);
					}
				}, null);
	}

	private void closeInput() {
		View view = MainActivity.this.getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private void sortUser() {
		if (null != userList) {
			Comparator<UserInfo> contactsComparator = new Comparator<UserInfo>() {
				public int compare(UserInfo f1, UserInfo f2) {
					String msg1 = getMsgCount(f1.getUserId());
					String msg2 = getMsgCount(f2.getUserId());
					if (TextUtils.isEmpty(msg1) && TextUtils.isEmpty(msg2)) {
						return 0;
					} else if (!TextUtils.isEmpty(msg1) && !TextUtils.isEmpty(msg2)) {
						return 0;
					} else if (!TextUtils.isEmpty(msg1) && TextUtils.isEmpty(msg2)) {
						return -1;
					} else {
						return 1;
					}
				}
			};
			Collections.sort(userList, contactsComparator);

			contactsComparator = new Comparator<UserInfo>() {
				public int compare(UserInfo f1, UserInfo f2) {
					boolean online1 = f1.getOnline();
					boolean online2 = f2.getOnline();
					if (online1 && online2) {
						return 0;
					} else if (!online1 && !online2) {
						return 0;
					} else if (!online1 && online2) {
						return 1;
					} else {
						return -1;
					}
				}
			};
			Collections.sort(userList, contactsComparator);

		}

	}

	class FamilyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		public int curposition = -1;
		ArrayList<Integer> escortingUeridList;

		public FamilyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
			escortingUeridList = new ArrayList<Integer>();
		}

		public int getCount() {

			return userList.size();
		}

		public UserInfo getItem(int position) {

			return userList.get(position);
		}

		public long getItemId(int position) {

			return position;
		}

		public int getCurposition() {
			return curposition;
		}

		public void setCurposition(int position) {
			this.curposition = position;
		}

		public void setEscortingposition(ArrayList<Integer> list) {
			this.escortingUeridList = list;
		}

		public void clearEscorting() {
			this.escortingUeridList.clear();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			OnClick listener = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_main_contact, null);
				// 92*92
				holder.contactRL = (RelativeLayout) convertView.findViewById(R.id.listitem_contactRL);
				holder.contactimg = (ImageView) convertView.findViewById(R.id.listitem_contact_img);
				holder.contactname = (TextView) convertView.findViewById(R.id.listitem_contact_name);
				holder.isEscortedimg = (ImageView) convertView.findViewById(R.id.listitem_contact_escortImg);
				holder.statusimg = (ImageView) convertView.findViewById(R.id.listitem_contact_status_img);
				holder.delimg = (ImageView) convertView.findViewById(R.id.listitem_contact_del_img);
				holder.msgCount = (TextView) convertView.findViewById(R.id.listitem_contact_msg_count);
				listener = new OnClick();
				holder.delimg.setOnClickListener(listener);
				convertView.setTag(holder);
				convertView.setTag(holder.delimg.getId(), listener);
			} else {
				holder = (ViewHolder) convertView.getTag();
				listener = (OnClick) convertView.getTag(holder.delimg.getId());
			}

			if (isEdit) {
				holder.delimg.setVisibility(View.VISIBLE);
			} else {
				holder.delimg.setVisibility(View.GONE);
			}
			listener.setPosition(position);
			UserInfo info = userList.get(position);
			if (info != null) {
				UserObject userObj = UserObject.createFromPb(info);
				if (!TextUtils.isEmpty(userObj.getUploadImage())) {
					ImageLoaderHelper.displayAvatar(userObj.getDisplayImage(), holder.contactimg);
				} else {
					holder.contactimg.setImageResource(R.drawable.icon_family);
				}
				if (info.getUserId() == LocalUserObject.LOCAL_USER_ID) {
					LocalUserObject localUser = new LocalUserObjectDao().getLocalUserByPhone(info.getPhone());
					if (localUser != null) {
						addMarker(localUser.getUserId(), localUser.getPhone(), null, localUser.getLat2(), localUser.getLng2(), holder.contactimg);
					}
				} else {
					if (userObj.isLocationValid()) {
						addMarker(userObj.getUserId(), userObj.getPhone(), userObj.getDisplayImage(), userObj.getLat2(), userObj.getLng2(), holder.contactimg);
					}
				}
				if (!TextUtils.isEmpty(userObj.getNickname())) {
					holder.contactname.setText(userObj.getNickname());
				}
				if (info.getOnline()) {
					holder.statusimg.setImageResource(R.drawable.listitem_contactstatus_inline);
				} else {
					holder.statusimg.setImageResource(R.drawable.listitem_contactstatus_outline);
				}
				if (position == curposition) {
					holder.contactRL.setBackgroundResource(R.drawable.startescort_griditem_selectedbg);
				} else {
					holder.contactRL.setBackgroundColor(Color.TRANSPARENT);
				}
				if (escortingUeridList.contains(info.getUserId())) {
					holder.isEscortedimg.setVisibility(View.VISIBLE);
					holder.isEscortedimg.startAnimation(rotateAnimation2);
				} else {
					holder.isEscortedimg.clearAnimation();
					holder.isEscortedimg.setVisibility(View.GONE);
				}
				String msgCount = getMsgCount(info.getUserId());
				if (!TextUtils.isEmpty(msgCount)) {
					holder.msgCount.setVisibility(View.VISIBLE);
					holder.msgCount.setText(msgCount);
				} else {
					holder.msgCount.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		public final class ViewHolder {
			public RelativeLayout contactRL;
			public ImageView isEscortedimg;
			public ImageView contactimg;
			public TextView contactname;
			public ImageView statusimg;
			public ImageView delimg;
			public TextView msgCount;
		}

		class OnClick implements OnClickListener {
			int position;

			public void setPosition(int position) {
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				UserInfo info = userList.get(position);
				if (info != null) {
					if (info.getUserId() == LocalUserObject.LOCAL_USER_ID) {
						ShowDelLocalFriendDialog(info.getPhone(), info.getUserId());
					} else {
						ShowDelFriendDialog(info.getNickname(), info.getUserId());
					}
				}
			}
		}
	}

	private void ShowDelLocalFriendDialog(final String fname, final int friendId) {
		View view = App.getInstance().getForegroundActivity().getLayoutInflater().inflate(R.layout.dialog_two_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(App.getInstance().getForegroundActivity(), view, false, true);
			final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			txtTitle.setText(getString(R.string.dialog_delfri_title));
			final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			String content = String.format(getString(R.string.dialog_delfri_local_content), fname);
			txtContent.setText(content);
			final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
			btnConfirm.setText(getString(R.string.dialog_delfri_yes));
			btnCancel.setText(getString(R.string.dialog_delfri_no));
			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
					new LocalUserObjectDao().delete(new LocalUserObject(fname));
					CoreModel.removeLocalFriend(userList, fname);
					CoreModel.removeLocalFriend(CoreModel.getInstance().getFriendList(), fname);
					updateList();
					// sendMessage(YSMSG.REQ_REMOVE_FRIEND, 0, friendId, null);
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();

				}
			});
			dialog.show();
		}
	}

	private void ShowDelFriendDialog(String fname, final int friendId) {
		View view = App.getInstance().getForegroundActivity().getLayoutInflater().inflate(R.layout.dialog_two_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(App.getInstance().getForegroundActivity(), view, false, true);
			final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			txtTitle.setText(getString(R.string.dialog_delfri_title));
			final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			String content = String.format(getString(R.string.dialog_delfri_content), fname);
			txtContent.setText(content);
			final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
			btnConfirm.setText(getString(R.string.dialog_delfri_yes));
			btnCancel.setText(getString(R.string.dialog_delfri_no));
			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
					sendMessage(YSMSG.REQ_REMOVE_FRIEND, 0, friendId, null);
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();

				}
			});
			dialog.show();
		}
	}

	private void setUpMapIfNeeded() {

		BaiduMapOptions bo = new BaiduMapOptions().compassEnabled(false).overlookingGesturesEnabled(false).rotateGesturesEnabled(false)
				.scaleControlEnabled(false).scrollGesturesEnabled(true).zoomControlsEnabled(false).zoomGesturesEnabled(true);

		mMapView = new MapView(MainActivity.this, bo);
		FrameLayout fLayout = (FrameLayout) findViewById(R.id.layout_main_map);
		fLayout.addView(mMapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setBuildingsEnabled(false);

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
		mBaiduMap.setMapStatus(msu);
		marketList = new ArrayList<Marker>();

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				if (marker.getExtraInfo() != null) {
					int userid = marker.getExtraInfo().getInt("userid");
					String phone = marker.getExtraInfo().getString("phone");
					showMapInfoWindow(userid, phone);
					refreshMapMarker(userid, phone, true);
				}
				return true;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public boolean onMapPoiClick(MapPoi arg0) {

				return false;
			}

			public void onMapClick(LatLng latlng) {
				if (CoreModel.getInstance().isLogined()) {
					mBaiduMap.hideInfoWindow();
					mInfoMarker = null;
				}
				if (BottomLL.isShown()) {
					MyAnimationUtils.hideBottomView(BottomLL, MainActivity.this);
				}
			}
		});
		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				if (CoreModel.getInstance().isLogined()) {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// addAllMapMarker();
						}
					}, 1000);
				}

			}
		});
	}

	private void showMapInfoWindow(final int userid, final String phone) {
		try {
			mBaiduMap.hideInfoWindow();
			Marker mMarker = null;
			int markerId = -1;
			String phone1 = "";
			for (int i = 0; i < marketList.size(); i++) {
				Marker marker = marketList.get(i);
				markerId = marketList.get(i).getExtraInfo().getInt("userid");
				phone1 = marketList.get(i).getExtraInfo().getString("phone");
				if (markerId == userid || phone1.equals(phone)) {
					mMarker = marker;
					break;
				}
			}
			if (mPopView == null) {
				mPopView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_map_info_window, null);
			}
			refreshBtn = (Button) mPopView.findViewById(R.id.btn_info_window_refresh);
			TextView location = (TextView) mPopView.findViewById(R.id.tv_info_window_location);
			TextView time = (TextView) mPopView.findViewById(R.id.tv_info_window_time);
			View markerView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_map_people_window, null);
			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			markerView.measure(w, h);
			int height = markerView.getMeasuredHeight();
			if (userid == -1) {
				if (refreshBtn != null) {
					refreshBtn.setVisibility(View.GONE);
				}
				BaiduLoc loc = App.getInstance().getLocater();
				location.setText(loc.getAddress());
				time.setVisibility(View.GONE);
				LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
				mInfoWindow = new InfoWindow(mPopView, latlng, height * -1);
				mBaiduMap.showInfoWindow(mInfoWindow);
			} else if (userid == CoreModel.getInstance().getUserInfo().getUserId()) {
				// refreshBtn.setVisibility(View.GONE);
				UserObject myself = CoreModel.getInstance().getUserInfo();
				if (TextUtils.isEmpty(myself.getLocation())) {
					location.setText(App.getInstance().getLocater().getAddress());
				} else {
					location.setText(myself.getLocation());
				}
				String date = DateUtils.getshowDateString(myself.getLocationChangeTime());
				if (!TextUtils.isEmpty(date)) {
					time.setText(getString(R.string.mainfragment_window_updatetiem) + date);
				} else {
					date = DateUtils.getCurrDateString("yyyy-MM-dd HH:mm");
					time.setText(getString(R.string.mainfragment_window_updatetiem) + date);
				}
			} else {
				for (UserInfo info : userList) {
					if (info.getUserId() == userid && info.getUserId() != LocalUserObject.LOCAL_USER_ID) {
						curUserInfo = info;
						if (!TextUtils.isEmpty(info.getLocation().getAddress())) {
							location.setText(info.getLocation().getAddress());
						}
						String date = DateUtils.getshowDateString(info.getLocation().getUpdateTime());
						if (!TextUtils.isEmpty(date)) {
							time.setText(getString(R.string.mainfragment_window_updatetiem) + date);
						}
						if (BottomLL.getVisibility() != View.VISIBLE) {
							MyAnimationUtils.showBottomView(BottomLL, MainActivity.this);
						}
					} else if (phone.equals(info.getPhone()) && info.getUserId() == LocalUserObject.LOCAL_USER_ID) {
						curUserInfo = info;
						LocalUserObject localUser = new LocalUserObjectDao().getLocalUserByPhone(phone);
						if (localUser != null) {
							if (!TextUtils.isEmpty(info.getLocation().getAddress())) {
								location.setText(info.getLocation().getAddress());
							}
							String date = DateUtils.getDateString(localUser.getCreateTime(), "yyyy-MM-dd HH:mm");
							if (!TextUtils.isEmpty(date)) {
								time.setText(getString(R.string.mainfragment_window_updatetiem) + date);
							}
							if (BottomLL.getVisibility() != View.VISIBLE) {
								MyAnimationUtils.showBottomView(BottomLL, MainActivity.this);
							}
						}
					}
				}
			}
			if (refreshBtn != null) {
				refreshBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						refreshBtn.startAnimation(rotateAnimation3);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								// mBaiduMap.hideInfoWindow();
								if (userid == CoreModel.getInstance().getUserInfo().getUserId()) {
									locationMeSelf(false);
								} else if (userid != LocalUserObject.LOCAL_USER_ID) {
									sendMessage(YSMSG.REQ_GET_USER_LOCATION, userid, 0, null);
								} else {
									sendMessage(YSMSG.REQ_GET_TEMP_FRIEND_LOCATION, 0, 0, phone);
								}
							}
						}, 200);

					}
				});
			}
			if (mMarker != null) {
				mInfoMarker = mMarker;
				mInfoWindow = new InfoWindow(mPopView, mMarker.getPosition(), height * -1);
				mBaiduMap.showInfoWindow(mInfoWindow);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addAllMapMarker() {

		mBaiduMap.clear();
		if (CoreModel.getInstance().getUserInfo() != null) {
			UserObject myselef = CoreModel.getInstance().getUserInfo();
			addUserObject(myselef);

			for (UserInfo info : userList) {
				UserObject user = UserObject.createFromPb(info);
				addUserObject(user);
			}

			showMapInfoWindow(myselef.getUserId(), myselef.getPhone());
			refreshMapMarker(myselef.getUserId(), myselef.getPhone(), false);
		}

	}

	private void showMarker(int userid) {
		for (int i = 0; i < marketList.size(); i++) {
			Marker marker = marketList.get(i);
			int marketid = marketList.get(i).getExtraInfo().getInt("userid");
			if (marketid == userid) {
				marker.setVisible(true);
			}
		}
	}

	private void refreshMapMarker(int userid, String phone, boolean makeCenter) {
		boolean exist = false;
		UserInfo userInfo = null;
		int marketid;
		String localPhone;
		for (int i = 0; i < marketList.size(); i++) {
			Marker marker = marketList.get(i);
			marketid = marketList.get(i).getExtraInfo().getInt("userid");
			localPhone = marketList.get(i).getExtraInfo().getString("phone");
			if (marketid == userid && marketid != LocalUserObject.LOCAL_USER_ID) {
				exist = true;
				LatLng latlng = null;
				if (marketid == CoreModel.getInstance().getUserInfo().getUserId()) {
					UserObject myselef = CoreModel.getInstance().getUserInfo();
					latlng = new LatLng(myselef.getLat2(), myselef.getLng2());
				} else {
					userInfo = CoreModel.getInstance().getFriendByUserid(marketid);
					if (userInfo != null) {
						UserObject user = UserObject.createFromPb(userInfo);
						latlng = new LatLng(user.getLat2(), user.getLng2());
					}
				}
				if (latlng != null) {
					if (makeCenter) {
						showCenter(latlng);
					}
					marker.setPosition(latlng);
					showMarker(marketid);
					marker.setZIndex(marketList.size());
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
					marker.setZIndex(marketList.size());
				}

			} else {
				marker.setZIndex(i);
			}
		}
	}

	private void refreshMapMarkerPosition(int userid, String phone) {
		boolean exist = false;
		UserInfo userInfo = null;
		int marketid;
		String localPhone;
		Marker marker;
		for (int i = 0; i < marketList.size(); i++) {
			try {
				marker = marketList.get(i);
				if (marker == null) {
					continue;
				}
				marketid = marketList.get(i).getExtraInfo().getInt("userid");
				localPhone = marketList.get(i).getExtraInfo().getString("phone");
				if (marketid == userid && marketid != LocalUserObject.LOCAL_USER_ID) {
					exist = true;
					LatLng latlng = null;
					if (marketid == CoreModel.getInstance().getUserInfo().getUserId()) {
						UserObject myselef = CoreModel.getInstance().getUserInfo();
						latlng = new LatLng(myselef.getLat2(), myselef.getLng2());
					} else {
						userInfo = CoreModel.getInstance().getFriendByUserid(marketid);
						if (userInfo != null) {
							UserObject user = UserObject.createFromPb(userInfo);
							latlng = new LatLng(user.getLat2(), user.getLng2());
						}
					}
					if (latlng != null) {
						marker.setPosition(latlng);
						showMarker(marketid);
					}
				} else if (!TextUtils.isEmpty(localPhone) && phone.equals(localPhone) && marketid == LocalUserObject.LOCAL_USER_ID) {
					exist = true;
					LatLng latlng = null;
					LocalUserObject localUser = new LocalUserObjectDao().getLocalUserByPhone(phone);
					if (localUser != null) {
						latlng = new LatLng(localUser.getLat2(), localUser.getLng2());
						marker.setPosition(latlng);
						showMarker(marketid);
					}

				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public Marker getMarker(int userId, String phone) {
		Marker marker = null;
		for (int i = 0; i < marketList.size(); i++) {
			marker = marketList.get(i);

			if (marker != null && marker.getExtraInfo() != null) {
				int markerid = marketList.get(i).getExtraInfo().getInt("userid");
				if (markerid == userId) {
					return marker;
				}
			}
		}

		return marker;
	}

	private void showCenter(LatLng latlng) {
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng), 600);
		// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
	}

	private void addUserObject(UserObject userobj) {
		addMarker(userobj.getUserId(), userobj.getPhone(), userobj.getDisplayImage(), userobj.getLat2(), userobj.getLng2());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();
		}

		if (new LocalUserObjectDao().count() > 0) {
			CoreModel.updateLocalFriendList(CoreModel.getInstance().getFriendList(), new LocalUserObjectDao().getLocalUserInfoList());
			CoreModel.updateLocalFriendList(userList, new LocalUserObjectDao().getLocalUserInfoList());

			updateList();
		}

		if (!CoreModel.getInstance().hasEscorting()) {
			escortAnim.setBackgroundResource(R.drawable.mainfragment_escortbg);
			escortText.setBackgroundResource(R.drawable.mainfragment_escorttext);
			escortAnim.clearAnimation();
		} else {
			escortAnim.setBackgroundResource(R.drawable.mainfragment_escortingbg);
			escortText.setBackgroundResource(R.drawable.mainfragment_escortingtext);
			escortAnim.startAnimation(rotateAnimation1);
		}
		if (CoreModel.getInstance().getTrip() != null) {
			Trip trip = CoreModel.getInstance().getTrip();
			int tripuserid = trip.getUserInfo().getUserId();
			List<UserInfo> infolist = trip.getUserInfosList();
			ArrayList<Integer> newlist = new ArrayList<Integer>();
			newlist.add(tripuserid);
			for (UserInfo info : infolist) {
				newlist.add(info.getUserId());
			}
			if (newlist.size() > 0) {
				FamilyAdapter adapter = (FamilyAdapter) mFamilyList.getAdapter();
				adapter.setEscortingposition(newlist);
				refreshFamilyAdapter();
			}
		} else {
			FamilyAdapter adapter = (FamilyAdapter) mFamilyList.getAdapter();
			adapter.clearEscorting();
			refreshFamilyAdapter();
		}
		updateMsgCount();

		App.getInstance().setForegroundActivity(this);
		AnalyticsHelper.onResume(this);
		updateUserMarker();

	}

	private void clearEscortStatus() {
		if (!CoreModel.getInstance().hasEscorting()) {
			escortAnim.setBackgroundResource(R.drawable.mainfragment_escortbg);
			escortText.setBackgroundResource(R.drawable.mainfragment_escorttext);
			escortAnim.clearAnimation();
		}

		if (CoreModel.getInstance().getTrip() == null) {
			FamilyAdapter adapter = (FamilyAdapter) mFamilyList.getAdapter();
			adapter.clearEscorting();
			refreshFamilyAdapter();
		}
	}

	private void updateUserMarker() {
		if (CoreModel.getInstance().ismChangeUserImg()) {
			UserObject user = CoreModel.getInstance().getUserInfo();
			if (null != user) {
				if (!TextUtils.isEmpty(user.getUploadImage())) {
					// addAllMapMarker();
					updateList();
				}
			}
			CoreModel.getInstance().setmChangeUserImg(false);
		}
	}

	public void onPause() {
		super.onPause();
		mMapView.onPause();
		AnalyticsHelper.onPause(this);

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
				popupWindow.dismiss();
			}
		} catch (Exception e) {
		}
	}

	private void unregister(BroadcastReceiver recv) {
		try {
			MainActivity.this.unregisterReceiver(recv);
		} catch (Exception e) {

		}
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// init chat
		voiceRecorder = new VoiceRecorder(micImageHandler);
		wakeLock = ((PowerManager) MainActivity.this.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "safe360");

		// 注册接收消息广播
		receiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		MainActivity.this.registerReceiver(receiver, intentFilter);

		// 注册一个消息送达的BroadcastReceiver
		IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getDeliveryAckMessageBroadcastAction());
		deliveryAckMessageIntentFilter.setPriority(5);
		MainActivity.this.registerReceiver(deliveryAckMessageReceiver, deliveryAckMessageIntentFilter);

		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, new SMSContentObserver(this, new Handler()));

		BaiduLoc baiduloc = App.getInstance().getLocater();
		LatLng latlng = new LatLng(baiduloc.getLatitude(), baiduloc.getLongitude());
		if (!CoreModel.getInstance().isLogined()) {
			addMarker(-1, "", "", baiduloc.getLatitude(), baiduloc.getLongitude());
			showMapInfoWindow(-1, "");
		}
		mMySelfAvatar = new ImageView(this);
		UserObject me = CoreModel.getInstance().getUserInfo();
		if (me != null) {
			addMarker(me.getUserId(), me.getPhone(), me.getDisplayImage(), baiduloc.getLatitude(), baiduloc.getLongitude(), mMySelfAvatar);
		}
		showCenter(latlng);

		App.getInstance().addBaiduLocListener(new BaiduLocListener() {

			@Override
			public void onLocationChanged(boolean result, ReverseGeoCodeResult geoResult) {
				if (result && App.getInstance().getLocater() != null) {
					if (CoreModel.getInstance().getUserInfo() != null) {
						UserObject user = CoreModel.getInstance().getUserInfo();
						user.setLocation(App.getInstance().getLocater().getAddress());
						user.setLng(App.getInstance().getLocater().getLng());
						user.setLat(App.getInstance().getLocater().getLat());
						refreshMapMarkerPosition(user.getUserId(), user.getPhone());
						if (mInfoMarker != null && mInfoMarker.getExtraInfo() != null && CoreModel.getInstance().getUserInfo() != null
								&& mInfoMarker.getExtraInfo().getInt("userid") == CoreModel.getInstance().getUserInfo().getUserId()) {
							showMapInfoWindow(CoreModel.getInstance().getUserInfo().getUserId(), CoreModel.getInstance().getUserInfo().getPhone());
						}
					}
				}
			}
		});

		mMsgCountMap = new HashMap<Integer, String>();

		// 监听SlidingMenu打开

		// 注册网络监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkStateReceiver, filter);

		IntentFilter pushMsgfilter = new IntentFilter();
		pushMsgfilter.addAction(PushMsgHandleService.ACTION_PUSH_MSG);
		registerReceiver(mPushMsgReceiver, pushMsgfilter);

		IntentFilter sensorfilter = new IntentFilter();
		sensorfilter.addAction(SensorManage.ACTION_DO_ACTION);
		registerReceiver(mSensorReceiver, sensorfilter);

		App.getInstance().mInitMainActivity = true;
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				handleUnHandlePushMsg();
			}
		}, 3000);

		if (PreferencesUtils.isTripException()) {
			clearTrip(PreferencesUtils.isTripRunnerException());
		}
	}

	private void clearTrip(boolean tripRunner) {
		if (tripRunner) {
			TripObject tripObj = new TripObject();
			tripObj.setTripId(0);
			BaiduLoc locater = App.getInstance().getLocater();
			if (locater != null) {
				tripObj.setEndAdress(locater.getAddress());
				tripObj.setEndLat(locater.getLat());
				tripObj.setEndLng(locater.getLng());
			}
			BaseController.getInstance().sendMessage(YSMSG.REQ_FINISH_TRIP, 0, 0, tripObj);
		} else {
			sendMessage(YSMSG.REQ_EXIT_TRIP, 0, PreferencesUtils.getTripId(), null);
		}
		PreferencesUtils.resetTripId();
	}

	private void handleUnHandlePushMsg() {
		PushMsgObjectDao msgDao = new PushMsgObjectDao();
		List<PushMsgObject> msgList = msgDao.findAll();
		String msg = null;
		String content = null;
		if (msgList != null && msgList.size() > 0) {
			for (PushMsgObject msgObj : msgList) {
				msg = msgObj.getMsg();
				content = msgObj.getContent();

				Intent intent2 = new Intent(PushMsgHandleService.ACTION_PUSH_MSG);
				intent2.putExtra(PushMsgHandleService.MSG, msg);
				intent2.putExtra(PushMsgHandleService.CONTENT, content);
				sendBroadcast(intent2);
				msgDao.delete(msgObj);
			}
		}
	}

	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			// micImage.setImageDrawable(micImages[msg.what]);
		}
	};

	private void loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		// 过滤掉messages seize为0的conversation
		int count = 0;
		for (EMConversation conversation : conversations.values()) {
			UserInfo userInfo = null;
			for (int i = 0; i < userList.size(); i++) {
				userInfo = userList.get(i);
				if (userInfo != null && userInfo.getIm() != null && !TextUtils.isEmpty(userInfo.getIm().getUsername())
						&& userInfo.getIm().getUsername().equals(conversation.getUserName())) {

					count = conversation.getUnreadMsgCount();
					if (count > 0) {
						if (count > 99) {
							mMsgCountMap.put(userInfo.getUserId(), "N");
						} else {
							mMsgCountMap.put(userInfo.getUserId(), count + "");
						}
					} else {
						mMsgCountMap.put(userInfo.getUserId(), "");
					}

				}
			}
		}
	}

	private void updateMsgCount() {
		UserInfo userInfo = null;
		int count = 0;
		for (int i = 0; i < userList.size(); i++) {
			userInfo = userList.get(i);

			if (userInfo != null && userInfo.getIm() != null && !TextUtils.isEmpty(userInfo.getIm().getUsername())) {

				EMConversation conversation = EMChatManager.getInstance().getConversation(userInfo.getIm().getUsername());
				if (conversation != null) {
					count = conversation.getUnreadMsgCount();
					if (count > 0) {
						if (count > 99) {
							mMsgCountMap.put(userInfo.getUserId(), "N");
						} else {
							mMsgCountMap.put(userInfo.getUserId(), count + "");
						}
					} else {
						mMsgCountMap.put(userInfo.getUserId(), "");
					}
				}

			}
		}

		refreshFamilyAdapter();
	}

	private String getMsgCount(Integer userId) {
		if (mMsgCountMap.containsKey(userId)) {
			return (String) mMsgCountMap.get(userId);
		}
		return "";
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
				return;
			}

			// 记得把广播给终结掉
			abortBroadcast();

			// if (!username.equals(toChatUsername)) {
			// // 消息不是发给当前会话，return
			// notifyNewMessage(message);
			// return;
			// }
			updateMsgCount();
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
			refreshFamilyAdapter();
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
		if (!EasyUtils.isAppRunningForeground(MainActivity.this)) {
			return;
		}

		String ticker = CommonUtils.getMessageDigest(message, MainActivity.this);
		if (message.getType() == Type.TXT)
			ticker = ticker.replaceAll("\\[.{2,3}\\]", getString(R.string.mainfragment_Chattip1));

		NotificationHelper.getInstance().showOrUpdateNotification(NotifyId.ID_CHAT_MESSAGE, getString(R.string.mainfragment_Chattip2),
				getString(R.string.mainfragment_Chattip3), message.getFrom() + ": " + ticker, 0, true, new Intent());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (ChatRL.getVisibility() == View.VISIBLE) {
				ChatRL.setVisibility(View.GONE);
				closeInput();
				return true;
			}
			showExitDialog();
			return false;
		}
		return false;

	}

	public UserInfo createTempUser(String phone) {
		UserInfo.Builder ub = UserInfo.newBuilder();
		ub.setPhone(phone);
		ub.setUserId(-1);

		return ub.build();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_GET_GROUP_INFO: {
			if (msg.arg1 == 200) {
				List<UserInfo> list = (List<UserInfo>) msg.obj;
				if (list != null && CoreModel.getInstance().getUserInfo() != null) {
					userList.clear();
					int userid = CoreModel.getInstance().getUserInfo().getUserId();
					for (int i = 0; i < list.size(); ++i) {
						UserInfo info = list.get(i);
						if (info.getUserId() != userid) {
							userList.add(info);
							// UserObject userObj =
							// UserObject.createFromPb(info);
						} else {
							UserObject userObj = CoreModel.getInstance().getUserInfo();
							if (userObj != null) {
								UserObject.updateUserInfo(CoreModel.getInstance().getUserInfo(), info);
							} else {
								UserObject userinfo = UserObject.createFromPb(info);
								CoreModel.getInstance().setUserInfo(userinfo);
							}
						}
					}
					CoreModel.getInstance().setFriendList(userList);
				}
				if (userList.size() == 0) {
					editFamilyBtn.setVisibility(View.GONE);
				} else {
					editFamilyBtn.setVisibility(View.VISIBLE);
				}
				updateList();
				if (!mInitMapAvatar && CoreModel.getInstance().getUserInfo() != null) {
					mInitMapAvatar = true;
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							BaiduLoc loc = App.getInstance().getLocater();
							LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
							showCenter(latlng);
							refreshMapMarker(CoreModel.getInstance().getUserInfo().getUserId(), CoreModel.getInstance().getUserInfo().getPhone(), false);
							showMapInfoWindow(CoreModel.getInstance().getUserInfo().getUserId(), CoreModel.getInstance().getUserInfo().getPhone());
						}
					}, 500);

				}
				// addAllMapMarker();
				loadConversationsWithRecentChat();
			} else {
				// 失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(MainActivity.this, result.getErrorMsg());
				} else {
					YSToast.showToast(MainActivity.this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_REMOVE_FRIEND: {
			if (msg.arg1 == 200) {
				sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
			} else {
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(MainActivity.this, result.getErrorMsg());
				} else {
					YSToast.showToast(MainActivity.this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_GET_USER_LOCATION: {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (refreshBtn != null) {
						refreshBtn.clearAnimation();
					}
				}
			}, 1000);
			if (msg.arg1 == 200) {
				UserObject info = (UserObject) msg.obj;
				if (CoreModel.getInstance().getUserInfo() != null && info != null) {
					if (info.getUserId() == CoreModel.getInstance().getUserInfo().getUserId()) {
						UserObject myselef = CoreModel.getInstance().getUserInfo();
						myselef.setLat(info.getLat());
						myselef.setLng(info.getLng());
						myselef.setLocation(info.getLocation());
						myselef.setLocationChangeTime(info.getLocationChangeTime());
						CoreModel.getInstance().setUserInfo(myselef);
					} else {
						UserInfo updateinfo = null;
						int index = -1;
						for (int i = 0; i < userList.size(); i++) {
							UserInfo friend = userList.get(i);
							if (info.getUserId() == friend.getUserId()) {
								UserInfo.Builder ub = friend.toBuilder();
								Location.Builder lb = Location.newBuilder();
								if (!TextUtils.isEmpty(info.getLocation())) {
									lb.setAddress(info.getLocation());
									lb.setUpdateTime(info.getLocationChangeTime());
									lb.setLat(info.getLat());
									lb.setLng(info.getLng());
									ub.setLocation(lb.build());
									updateinfo = ub.build();
									index = i;
								}
							}
						}
						if (updateinfo != null && index != -1) {
							ArrayList<UserInfo> newList = new ArrayList<UserInfo>();
							for (int i = 0; i < userList.size(); i++) {
								if (i == index) {
									newList.add(updateinfo);
								} else {
									newList.add(userList.get(i));
								}
							}
							userList.clear();
							userList.addAll(newList);
							CoreModel.getInstance().setFriendList(userList);
							refreshMapMarker(info.getUserId(), info.getPhone(), false);
							showMapInfoWindow(info.getUserId(), info.getPhone());
						}
					}
				}
			} else {
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(MainActivity.this, result.getErrorMsg());
				} else {
					YSToast.showToast(MainActivity.this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_JOIN_GROUP: {
			BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
		}
			break;
		case YSMSG.RESP_GET_TRIP_INFO: {
			if (msg.arg1 == 200) {
				Trip trip = (Trip) msg.obj;
				if (trip != null) {
					int userId = -1;
					if (escortMsg != null) {
						JSONObject json = new JSONObject();
						try {
							userId = trip.getUserInfo().getUserId();
							json.put("userId", trip.getUserInfo().getUserId());
							json.put("lat", trip.getBeginLocation().getLat());
							json.put("lng", trip.getBeginLocation().getLng());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						escortMsg.setData(json.toString());
						insertPushMsg(escortMsg, CoreModel.getInstance().getFriendByUserid(userId), 1);
					}
					if (trip.getStatus()) {
						CoreModel.getInstance().setTrip(trip);
					}
					if (!ActivityUtils.isActivityForeground(App.getInstance().getApplicationContext(), EscortActivity.class)) {
						if (trip.getStatus()) {
							EscortActivity.startActivity(App.getInstance().getForegroundActivity(), false);
						}
					}
				}
			} else {
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_CHECK_VERSION: {
			UpdateVersionUtils.handleMessage(App.getInstance().getForegroundActivity(), msg);
		}
			break;
		case YSMSG.RESP_GET_TEMP_FRIEND_LOCATION: {
			if (refreshBtn != null) {
				refreshBtn.clearAnimation();
			}
			if (msg.arg1 == 200) {
				final String phone = (String) msg.obj;
				if (new LocalUserObjectDao().count() > 0 && !TextUtils.isEmpty(phone)) {
					CoreModel.updateLocalFriendList(CoreModel.getInstance().getFriendList(), new LocalUserObjectDao().getLocalUserInfoList());
					CoreModel.updateLocalFriendList(userList, new LocalUserObjectDao().getLocalUserInfoList());

					updateList();
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							dismissWaitingDialog();
							refreshMapMarker(LocalUserObject.LOCAL_USER_ID, phone, true);
							showMapInfoWindow(LocalUserObject.LOCAL_USER_ID, phone);
						}
					}, 100);
				}

			} else {
				dismissWaitingDialog();
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
					TelephonyUtils.sendSms(this, contacts.getPhone(), smsContent, false);
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
		case YSMSG.RESP_MODIFY_LOCATION: {
			if (refreshBtn != null) {
				refreshBtn.clearAnimation();
			}
		}
			break;
		case YSMSG.RESP_START_TRIP:
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 成功
				Trip trip = (Trip) msg.obj;
				if (trip != null) {
					CoreModel.getInstance().setTrip(trip);
					EscortActivity.startActivity(this);
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
			break;
		case RECORD_CHANGENUM:
			if (recordCount == 0) {
				if (!recordIsSend) {
					recordIsSend = true;
					mHandler.removeCallbacks(recordrunnable);
					chatinputvoiceBtn.setText(getString(R.string.mainfragment_ChatVoiceBtnText));
					recordinganim.stop();
					popupWindow.dismiss();
					if (wakeLock.isHeld()) {
						wakeLock.release();
					}
					// stop recording and send voice file
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername), Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(MainActivity.this.getApplicationContext(), "无录音权限", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this.getApplicationContext(), "录音时间太短", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(MainActivity.this, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
					}
					recordCount = 120;
				}
			} else if (recordCount <= 60) {
				recordingCount.setText(recordCount + "'");
				recordingCount.setVisibility(View.VISIBLE);
			} else {
				recordingCount.setVisibility(View.INVISIBLE);
			}
			break;
		}
	}

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
					smsContent = String.format(smsContent, mUrl);
					if (msgtext.equals(smsContent)) {
						if (curUserInfo != null && curUserInfo.getLocation() == null || TextUtils.isEmpty(curUserInfo.getLocation().getAddress())) {
							// YSToast.showToast(mActivity, "暂时获取不到对方的位置信息");
							sendMessage(YSMSG.REQ_GET_TEMP_FRIEND_LOCATION, 0, 0, curUserInfo.getPhone());
						}
					}
				}

			}
		}
	}

	private void updateList() {
		marketList.clear();
		mBaiduMap.clear();
		UserObject me = CoreModel.getInstance().getUserInfo();
		if (me != null) {
			BaiduLoc baiduloc = App.getInstance().getLocater();
			addMarker(me.getUserId(), me.getPhone(), me.getDisplayImage(), baiduloc.getLatitude(), baiduloc.getLongitude(), mMySelfAvatar);
		}

		int length = userList.size();
		DisplayMetrics dm = new DisplayMetrics();
		MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		if (length >= 4) {
			int listHeight = (int) (60 * 4 * density);
			int listWidth = (int) (60 * density);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(listWidth, listHeight);
			mFamilyList.setLayoutParams(params);
		} else {
			int listHeight = (int) (60 * length * density);
			int listWidth = (int) (60 * density);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(listWidth, listHeight);
			mFamilyList.setLayoutParams(params);
		}
		refreshFamilyAdapter();
	}

	private void refreshFamilyAdapter() {
		if (mFamilyList != null) {
			FamilyAdapter adapter = (FamilyAdapter) mFamilyList.getAdapter();
			if (null != adapter) {
				sortUser();
				adapter.notifyDataSetChanged();
			}
		}
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
						refreshFamilyAdapter();
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
		haveMoreData = true;
		isloading = false;
		chatType = CHATTYPE_SINGLE;

		String chatTitle = getString(R.string.mainfragment_ChatTopTitleText);
		chatTitle = String.format(chatTitle, CoreModel.getInstance().getNickname(toChatUsername));
		chatinputTopTitle.setText(chatTitle);

		conversation = EMChatManager.getInstance().getConversation(toChatUsername);
		// 把此会话的未读数置为0
		conversation.resetUnreadMsgCount();
		adapter = new MessageAdapter(MainActivity.this, toChatUsername, chatType);
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

		updateMsgCount();
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

	public void addMarker(int userid, String phone, String imgUrl, double latitude, double longitude) {
		String tempimg = null;
		if (!TextUtils.isEmpty(imgUrl)) {
			tempimg = imgUrl.replace(PPNetManager.HTTP, "").replace(PPNetManager.IMAGE_DOMAIN, "").replace(PPNetManager.IMAGE_DOMAIN2, "")
					.replace("!avatar.def", "");
		}
		if (userid == -1) {
			View popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_map_people_window, null);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(popView);
			OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(bitmap);
			mBaiduMap.addOverlay(oop);
		} else if (!TextUtils.isEmpty(imgUrl) && !TextUtils.isEmpty(tempimg)) {
			LatLng latlng = new LatLng(latitude, longitude);
			// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
			AvatarProcessor process = new AvatarProcessor(userid, phone, latlng);
			View popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_map_people_window, null);
			ImageView userimg = (ImageView) popView.findViewById(R.id.people_window_img);
			ImageLoaderHelper.displayImage(imgUrl, userimg, R.drawable.icon_family, process, true, 90);
		} else {
			View popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_map_people_window, null);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(popView);
			OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(bitmap);
			Marker marker = (Marker) mBaiduMap.addOverlay(oop);
			if (userid == CoreModel.getInstance().getUserInfo().getUserId()) {
				marker.setVisible(true);
			} else {
				marker.setVisible(false);
			}
			addMarker(userid, phone, marker);
			// if (!isMarkerExist(userid, phone)) {
			//
			// } else {
			// refreshMapMarker(userid, phone, false);
			// }
		}
	}

	public void addMarker(int userid, String phone, String imgUrl, double latitude, double longitude, ImageView imageView) {
		String tempimg = null;
		if (!TextUtils.isEmpty(imgUrl)) {
			tempimg = imgUrl.replace(PPNetManager.HTTP, "").replace(PPNetManager.IMAGE_DOMAIN, "").replace(PPNetManager.IMAGE_DOMAIN2, "")
					.replace("!avatar.def", "");
		}
		if (userid == -1) {
			View popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_map_people_window, null);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(popView);
			OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(bitmap);
			mBaiduMap.addOverlay(oop);
		} else if (!TextUtils.isEmpty(imgUrl) && !TextUtils.isEmpty(tempimg)) {
			LatLng latlng = new LatLng(latitude, longitude);
			// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
			AvatarProcessor process = new AvatarProcessor(userid, phone, latlng);
			ImageLoaderHelper.displayImage(imgUrl, imageView, R.drawable.icon_family, process, true, 90);
		} else {
			View popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_map_people_window, null);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(popView);
			OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(bitmap);
			Marker marker = (Marker) mBaiduMap.addOverlay(oop);
			// if (userid ==
			// CoreModel.getInstance().getUserInfo().getUserId())
			// {
			// marker.setVisible(true);
			// } else {
			// marker.setVisible(false);
			// }
			addMarker(userid, phone, marker);
			// if (!isMarkerExist(userid, phone)) {
			//
			// } else {
			// refreshMapMarker(userid, phone, false);
			// }
		}
	}

	private void addMarker(int userId, String phone, Marker marker) {
		boolean exist = false;

		for (Marker m : marketList) {
			if (m.getExtraInfo() != null && (m.getExtraInfo().getInt("userid") == userId || m.getExtraInfo().getString("phone").equals(phone))) {
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
			if (m.getExtraInfo() != null && (m.getExtraInfo().getInt("userid") == userId || m.getExtraInfo().getString("phone").equals(phone))) {
				exist = true;
				break;
			}
		}

		return exist;
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

		@Override
		public Bitmap process(Bitmap bitmap) {
			LogUtils.e("avatar", "enter process bitmap: userId= " + userid);
			try {
				if (!isMarkerExist(userid, phone)) {
					final Bitmap output = convertViewToBitmapEx(bitmap);

					BitmapDescriptor viewbitmap;
					if (output == null) {
						View popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_map_people_window, null);
						viewbitmap = BitmapDescriptorFactory.fromView(popView);
					} else {
						viewbitmap = BitmapDescriptorFactory.fromBitmap(output);
					}
					// new LatLng(mBaiduMap.getMapStatus().target.latitude,
					// mBaiduMap.getMapStatus().target.longitude)
					OverlayOptions oop = new MarkerOptions().position(latlng).icon(viewbitmap).visible(true);
					Marker marker = (Marker) mBaiduMap.addOverlay(oop);
					// if (userid ==
					// CoreModel.getInstance().getUserInfo().getUserId()) {
					// marker.setVisible(true);
					// } else {
					// marker.setVisible(false);
					// }
					LogUtils.e("avatar", "addMarker(userid, phone, marker); userId= " + userid);
					addMarker(userid, phone, marker);
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
		MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
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

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (MainActivity.this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (MainActivity.this.getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(MainActivity.this, 1, list);
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
							chatinputEdit.append(SmileUtils.getSmiledText(MainActivity.this, (String) field.get(null)));
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

	public void showChatPanel(String userId) {
		if (TextUtils.isEmpty(userId)) {
			return;
		}

		if (ChatRL.getVisibility() == View.VISIBLE) {
			if (!userId.equals(toChatUsername)) {
				if (conversation != null) {
					conversation.resetUnreadMsgCount();
				}
				ChatRL.setVisibility(View.GONE);
				closeInput();
			}
		}

		toChatUsername = userId;

		startChat();
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

	SensorManager mSensorManager;
	Sensor mSensor;

	private void initView() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		sensorManager = new SensorManage(App.getInstance().getApplicationContext());
		mSensorManager = (SensorManager) App.getInstance().getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		mSensorManager.registerListener(eventListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
	}

	SensorEventListener eventListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			float range = event.values[0];
			LogUtils.i("changeVoiceType", range + "");
			if (range < 20) {
				LogUtils.i("changeVoiceType", "call");
				EMChatManager.getInstance().getChatOptions().setUseSpeaker(true);
			} else {
				LogUtils.i("changeVoiceType", "music");
				EMChatManager.getInstance().getChatOptions().setUseSpeaker(false);
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	private void showExitDialog() {
		View view = App.getInstance().getForegroundActivity().getLayoutInflater().inflate(R.layout.dialog_two_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(App.getInstance().getForegroundActivity(), view, false, true);
			final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			txtContent.setText(R.string.exit_tip_content);
			final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
			btnConfirm.setText(getString(R.string.exit));
			btnCancel.setText(getString(R.string.hide));
			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
					showOrUpdateWaitingDialog(R.string.exiting);
					App.getInstance().exitApp();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
					moveTaskToBack(true);
				}
			});
			dialog.show();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 取消网络状态监听
		try {
			unregisterReceiver(mNetworkStateReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			unregisterReceiver(mSensorReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			unregisterReceiver(mPushMsgReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sensorManager != null) {
			sensorManager.destroy();
		}
		mSensorManager.unregisterListener(eventListener);
		CoreModel.getInstance().release();
		DatabaseManager.getInstance().releaseHelper();
		mMapView.onDestroy();

		unregister(receiver);
		unregister(deliveryAckMessageReceiver);

		App.getInstance().logout(null);
	}

	private BroadcastReceiver mPushMsgReceiver = new BroadcastReceiver() {

		public void onReceive(Context paramContext, Intent paramIntent) {
			handlePushMessage(paramIntent);
		}
	};

	private BroadcastReceiver mSensorReceiver = new BroadcastReceiver() {
		public void onReceive(Context paramContext, Intent paramIntent) {
			if (CoreModel.getInstance().getFriendList() != null && CoreModel.getInstance().getFriendList().size() != 0) {
				SOSActivity.startActivity(App.getInstance().getForegroundActivity());
			} else {
				showSOSAddFamilyDialog();
			}
		}
	};

	protected void showSOSAddFamilyDialog() {
		if (!mShowSOSAddFamilyDialog) {
			mShowSOSAddFamilyDialog = true;

			DialogHelper.showTwoDialog(App.getInstance().getForegroundActivity(), true, null, getString(R.string.dialog_nofamilyescort_content),
					getString(R.string.dialog_nofamilyescort_yes), getString(R.string.dialog_nofamilyescort_no), false, new OnClickListener() {
						public void onClick(View v) {
							mShowSOSAddFamilyDialog = false;
							AddFamilyActivity.startActivity(App.getInstance().getForegroundActivity());
						}
					}, new OnClickListener() {
						public void onClick(View v) {
							mShowSOSAddFamilyDialog = false;
						}
					});
		}
	}

	protected void showTripAddFamilyDialog() {
		DialogHelper.showTwoDialog(App.getInstance().getForegroundActivity(), true, null, getString(R.string.dialog_nofamily_content),
				getString(R.string.dialog_nofamilyescort_yes), getString(R.string.dialog_nofamilyescort_no), true, new OnClickListener() {
					public void onClick(View v) {
						AddFamilyActivity.startActivity(App.getInstance().getForegroundActivity());
					}
				}, new OnClickListener() {
					public void onClick(View v) {
						// StartEscortActivity.startActivity(MainActivity.this);
						startTrip(new ArrayList<UserInfo>());
					}
				});
	}

	private void handlePushMessage(Intent intent) {
		final String msg = intent.getStringExtra(PushMsgHandleService.MSG);

		PushMessageObject pushMsg = null;
		try {
			pushMsg = OJMFactory.createOJM().fromJson(msg, PushMessageObject.class);
		} catch (Exception e) {
		}
		escortMsg = null;
		if (pushMsg != null) {
			switch (CMD.toCmd(pushMsg.getCmd())) {
			case invite_friend: {
				if (null != pushMsg.getData()) {

					// data:"userId":11,"phone":"13700000001","nickname":"1371"
					try {
						JSONObject json = new JSONObject(pushMsg.getData());
						final int userid = json.getInt("userId");
						String phone = json.getString("phone");
						final String nickname = json.getString("nickname");
						String content = "";
						if (CoreModel.getInstance().getFriendList() != null && CoreModel.getInstance().getFriendList().size() != 0) {
							content = nickname + "(" + phone + ")" + getString(R.string.activity_message_addfri_content2);
						} else {
							content = nickname + "(" + phone + ")" + getString(R.string.activity_message_addfri_content1);
						}
						final String title = pushMsg.getTitle();
						DialogHelper.showTwoDialog(App.getInstance().getForegroundActivity(), true, title, content, getString(R.string.dialog_confim_refuse),
								getString(R.string.dialog_confim_agree), true, new OnClickListener() {
									public void onClick(View v) {
										PushMessageObject pushObj = new PushMessageObject();
										pushObj.setTitle(title);
										String desc = String.format(getString(R.string.activity_message_refuse), CoreModel.getInstance().getNickname());
										pushObj.setDescription(desc);
										ArrayList<Integer> idList = new ArrayList<Integer>();
										idList.add(userid);
										pushObj.setUseridList(idList);
										JSONObject json = new JSONObject();
										int userId = -1;
										try {
											userId = CoreModel.getInstance().getUserInfo().getUserId();
											json.put("userid", CoreModel.getInstance().getUserInfo().getUserId());
											json.put("type", MessageDataObject.TYPE_REFUSE);
											pushObj.setData(json.toString());
										} catch (JSONException e) {
											e.printStackTrace();
										}
										sendMessage(YSMSG.REQ_PUSH_MSG, 0, 0, pushObj);
										insertPushMsg(pushObj, CoreModel.getInstance().getFriendByUserid(userId), 2);

										if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
											ActivityUtils.moveTaskToFront(mActivity);
										}
									}
								}, new OnClickListener() {
									public void onClick(View v) {
										BaseController.getInstance().sendMessage(YSMSG.REQ_JOIN_GROUP, 0, userid, null);
										PushMessageObject pushObj = new PushMessageObject();
										pushObj.setTitle(title);
										String desc = String.format(getString(R.string.activity_message_addfri), nickname);
										pushObj.setDescription(desc);
										insertPushMsg(pushObj, CoreModel.getInstance().getFriendByUserid(userid), 2);
										YSToast.showToast(App.getInstance().getForegroundActivity(), desc);

										if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
											ActivityUtils.moveTaskToFront(mActivity);
										}
									}
								});
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
				break;
			case remove_friend: {
				insertPushMsg(pushMsg, null, 2);
				// showOneButtonDialog(pushMsg.getTitle(),
				// pushMsg.getDescription());
				BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
				if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
					ActivityUtils.moveTaskToFront(mActivity);
				}
			}
				break;
			case join_group: {
				if (null != pushMsg.getData()) {
					insertPushMsg(pushMsg, null, 2);
					MessageDataObject dataObj = null;
					try {
						dataObj = OJMFactory.createOJM().fromJson(pushMsg.getData(), MessageDataObject.class);
					} catch (Exception e) {
					}
					if (null != dataObj) {
						final String userid = dataObj.getUserId();
						DialogHelper.showTwoDialog(App.getInstance().getForegroundActivity(), true, pushMsg.getTitle(), pushMsg.getDescription(),
								getString(R.string.dialog_confim_refuse), getString(R.string.dialog_confim_agree), true, null, new OnClickListener() {
									public void onClick(View v) {
										if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
											ActivityUtils.moveTaskToFront(mActivity);
										}
										BaseController.getInstance().sendMessage(YSMSG.REQ_JOIN_GROUP, 0, Integer.valueOf(userid), null);
									}
								});
					}
				} else {
					BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
				}
			}
				break;
			case exit_group: {
				insertPushMsg(pushMsg, null, 2);
				DialogHelper.showOneDialog(App.getInstance().getForegroundActivity(), true, pushMsg.getTitle(), pushMsg.getDescription(),
						getString(R.string.dialog_confim_know), true, new OnClickListener() {
							public void onClick(View v) {
								if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
									ActivityUtils.moveTaskToFront(mActivity);
								}
								BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
							}
						});
			}
				break;
			case start_trip: {
				// 切换到护航界面
				escortMsg = pushMsg;
				try {
					int userId = pushMsg.getFrom();
					if (userId != 0 && !CoreModel.getInstance().isFriend(userId)) {
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					JSONObject json = new JSONObject(pushMsg.getData());
					final int tripid = json.getInt("tripId");
					DialogHelper.showOneDialog(App.getInstance().getForegroundActivity(), true, pushMsg.getTitle(), pushMsg.getDescription(),
							getString(R.string.dialog_confim_check), false, new OnClickListener() {
								public void onClick(View v) {
									if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
										ActivityUtils.moveTaskToFront(mActivity);
									}
									BaseController.getInstance().sendMessage(YSMSG.REQ_GET_TRIP_INFO, 0, tripid, null);
								}
							});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
				break;
			case finish_trip: {
				// insertPushMsg(pushMsg, 2);
				if (CoreModel.getInstance().getTrip() == null) {
					break;
				}
				try {
					int userId = pushMsg.getFrom();
					if (userId != 0 && !CoreModel.getInstance().isFriend(userId)) {
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				DialogHelper.showOneDialog(App.getInstance().getForegroundActivity(), true, pushMsg.getTitle(), pushMsg.getDescription(),
						getString(R.string.dialog_confim_know), false, new OnClickListener() {
							public void onClick(View v) {
								if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
									ActivityUtils.moveTaskToFront(mActivity);
								}
								CoreModel.getInstance().setEscorting(false);
								CoreModel.getInstance().setTrip(null);
								clearEscortStatus();
								Message message = App.getInstance().obtainMessage();
								message.what = YSMSG.REQ_FINISH_TRIP_FROM_MAIN;
								CoreModel.getInstance().notifyOutboxHandlers(message);
							}
						});
			}
				break;
			case exit_trip: {
				// insertPushMsg(pushMsg, 2);
				// showOneButtonDialog(pushMsg.getTitle(),
				// pushMsg.getDescription());
				DialogHelper.showOneDialog(App.getInstance().getForegroundActivity(), true, pushMsg.getTitle(), pushMsg.getDescription(),
						getString(R.string.dialog_confim_know), false, new OnClickListener() {
							public void onClick(View v) {
								if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
									ActivityUtils.moveTaskToFront(mActivity);
								}
								Message message = App.getInstance().obtainMessage();
								message.what = YSMSG.REQ_REFRESH_TRIP_FROM_MAIN;
								CoreModel.getInstance().notifyOutboxHandlers(message);
							}
						});
			}
				break;
			case sos: {
				if (null != pushMsg.getData()) {
					int userId = -1;
					try {
						JSONObject json = new JSONObject(pushMsg.getData());
						UserObject user = new UserObject();
						userId = json.getInt("userId");
						int msgId = insertPushMsg(pushMsg, CoreModel.getInstance().getFriendByUserid(userId), 1);
						user.setUserId(userId);
						user.setLat(json.getString("lat"));
						user.setLng(json.getString("lng"));
						String miUIName = VibratorManager.getSystemProperty("ro.miui.ui.version.name");
						if (!TextUtils.isEmpty(miUIName)) {

						} else {
							
						}
						showSOSDialog(pushMsg.getDescription(), user, msgId);
					} catch (Exception e) {
					}
				}
			}
				break;
			case close_hide: {

			}
				break;
			case open_hide: {

			}
				break;
			case online: {
				BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
			}
				break;
			case offline: {
				BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
			}
				break;
			case client_custom: {
				if (null != pushMsg.getData()) {

					JSONObject json;
					try {
						json = new JSONObject(pushMsg.getData());
						int jsonid = json.getInt("userid");
						String type = json.getString("type");
						if (CoreModel.getInstance().getTrip() != null) {
							if (type.equals(MessageDataObject.TYPE_ARRIVATE) && jsonid != CoreModel.getInstance().getUserInfo().getUserId()) {
								insertPushMsg(pushMsg, CoreModel.getInstance().getFriendByUserid(jsonid), 2);
								showOneButtonDialog(pushMsg.getTitle(), pushMsg.getDescription());
							}
							if (type.equals(MessageDataObject.TYPE_NOCHANGE) && jsonid != CoreModel.getInstance().getUserInfo().getUserId()) {
								insertPushMsg(pushMsg, CoreModel.getInstance().getFriendByUserid(jsonid), 2);
								showOneButtonDialog(pushMsg.getTitle(), pushMsg.getDescription());
							}
						}

						if (type.equals(MessageDataObject.TYPE_REFUSE) && jsonid != CoreModel.getInstance().getUserInfo().getUserId()) {
							insertPushMsg(pushMsg, CoreModel.getInstance().getFriendByUserid(jsonid), 2);
							showOneButtonDialog(pushMsg.getTitle(), pushMsg.getDescription());
						}
					} catch (JSONException e) {

						e.printStackTrace();
					}
				}
			}
				break;
			case NOVALUE:
				break;
			}
		}
	}

	private int insertPushMsg(PushMessageObject pushMsg, UserInfo userInfo, int type) {
		MessageObject msgObj = new MessageObject();
		msgObj.setTitle(pushMsg.getTitle());
		msgObj.setDescription(pushMsg.getDescription());
		msgObj.setData(pushMsg.getData());
		String time = getCurTime();
		msgObj.setTime(time);
		msgObj.setType(type);

		if (userInfo != null) {
			if (userInfo.getLocation() != null) {
				msgObj.setAddressName(userInfo.getLocation().getName());
				msgObj.setLocation(userInfo.getLocation().getAddress());
			}
			msgObj.setUserName(userInfo.getNickname());
			msgObj.setUserAvatar(userInfo.getImage());

		}
		msgObj.parseData();
		msgObj.setInsertTime(System.nanoTime());
		return insertMessage(msgObj);
	}

	private int insertMessage(MessageObject msg) {
		MessageObject msgObj = new MessageObjectDao().insert(msg);
		if (msgObj != null) {
			return msgObj.getId();
		} else {
			return -1;
		}
		// Safe360MessageDataBase database = new
		// Safe360MessageDataBase(getApplicationContext());
		// database.insertRow(msg);
		// database.close();
	}

	private String getCurTime() {
		SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return myFmt2.format(date);
	}

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

	private void showSOSDialog(String content, final UserObject user, final int msgId) {
		View view = App.getInstance().getForegroundActivity().getLayoutInflater().inflate(R.layout.dialog_sos_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(App.getInstance().getApplicationContext(), view, true, true);
			TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			txtContent.setText(TextUtils.isEmpty(content) ? "" : content);
			Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			btnConfirm.setText(getString(R.string.dialog_confim_know));
			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();

					if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
						ActivityUtils.moveTaskToFront(mActivity);
					}
					Intent intent = new Intent(App.getInstance().getForegroundActivity(), MessageDetailActivity.class);
					intent.putExtra("messageId", msgId);
					startActivity(intent);
				}
			});

			dialog.show();
		}
	}

	private void showOneButtonDialog(String title, String content) {
		View view = App.getInstance().getForegroundActivity().getLayoutInflater().inflate(R.layout.dialog_one_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(App.getInstance().getApplicationContext(), view, true, true);
			TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			txtTitle.setText(TextUtils.isEmpty(title) ? "" : title);
			TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			txtContent.setText(TextUtils.isEmpty(content) ? "" : content);

			Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);

			btnConfirm.setText(getString(R.string.dialog_confim_know));
			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
					BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_GET_GROUP_INFO);
					if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
						ActivityUtils.moveTaskToFront(mActivity);
					}
				}
			});

			dialog.show();
		}
	}

	private void startTrip(ArrayList<UserInfo> escortList) {
		showWaitingDialog();
		TripObject tripObj = new TripObject();
		BaiduLoc locater = App.getInstance().getLocater();
		if (locater != null) {
			tripObj.setBeginAdress(locater.getAddress());
			tripObj.setBeginLat(locater.getLat());
			tripObj.setBeginLng(locater.getLng());
			tripObj.setUserinfos(escortList);
			sendMessage(YSMSG.REQ_START_TRIP, 0, 0, tripObj);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(ExtraName.EN_RELOGIN, true);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

	}
}
