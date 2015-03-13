package cn.changl.safe360.android.ui.sos;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.api.PPNetManager;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.LocalUserObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.map.BaiduLoc;
import cn.changl.safe360.android.map.BaiduLocListener;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.DialogHelper;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.utils.DateUtils;
import cn.changl.safe360.android.utils.MyAnimationUtils;
import cn.changl.safe360.android.utils.TelephonyUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;

public class SOSActivity extends BaseExActivity implements OnGetPoiSearchResultListener {
	protected TitleBarHolder mTitleBar;

	private Animation mUpRotateAnim;
	private Animation mDownRotateAnim;
	// private ImageView mCircleUpView;
	private ImageView mCircleDownView;

	private ImageView mSecondView;
	private int[] mResId = { R.drawable.sos_0, R.drawable.sos_1, R.drawable.sos_2, R.drawable.sos_3, R.drawable.sos_4, R.drawable.sos_5, R.drawable.sos_6,
			R.drawable.sos_7, R.drawable.sos_8, R.drawable.sos_9 };
	private int mTickCount = 9;

	private Button mCancelBtn;
	private Button mOKBtn;

	private MapView mMapView;
	private BaiduMap mBaiduMap;

	private Button mCallBtn;
	private Button mNearbyBtn;
	private Button mConfirmBtn;

	private RelativeLayout sosnearRL;
	private ListView nearListView;

	private boolean mCountdown = false;

	List<PoiInfo> poiItems;
	LatLng mLatLng;
	private PoisAdapter mAdapter;
	private PoiSearch mPoiSearch = null;
	BaiduLoc locater;
	private int mPoiType = 0;

	private Vibrator mVibrator = null; // 震动

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, SOSActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sos);

		initView();
		initData();
		initMap();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_sos_title);

		// mCircleUpView = (ImageView)
		// this.findViewById(R.id.img_sos_circle_up);
		mCircleDownView = (ImageView) this.findViewById(R.id.img_sos_circle_down);
		mSecondView = (ImageView) this.findViewById(R.id.img_sos_second);

		mCancelBtn = (Button) this.findViewById(R.id.btn_sos_cancel);
		mCancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		mOKBtn = (Button) this.findViewById(R.id.btn_sos_ok);
		mOKBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mHandler.removeCallbacks(runnable);
				showSOSResult();
			}
		});

		mUpRotateAnim = AnimationUtils.loadAnimation(this, R.anim.sos_circle_rotate_up);
		mDownRotateAnim = AnimationUtils.loadAnimation(this, R.anim.sos_circle_rotate_down);

		// result layout
		mCallBtn = (Button) this.findViewById(R.id.btn_sos_result_110);
		mCallBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showCallPhoneDialog(String.valueOf(110));
			}
		});

		mNearbyBtn = (Button) this.findViewById(R.id.btn_sos_result_nearby);
		mNearbyBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				MyAnimationUtils.showRightView(sosnearRL, nearListView, SOSActivity.this);
			}
		});
		mConfirmBtn = (Button) this.findViewById(R.id.btn_sos_result_confirm);
		mConfirmBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		poiItems = new ArrayList<PoiInfo>();
		mAdapter = new PoisAdapter(this);
		sosnearRL = (RelativeLayout) findViewById(R.id.nearsosRL);
		nearListView = (ListView) findViewById(R.id.lv_nearsos);
		nearListView.setAdapter(mAdapter);
		sosnearRL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (sosnearRL.isShown()) {
					MyAnimationUtils.hideRightView(sosnearRL, nearListView, SOSActivity.this);
				}
			}
		});
	}

	private void initData() {
		mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

		getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, new SMSContentObserver(this, new Handler()));

		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		startSearch();

		mHandler.removeMessages(1);
		mHandler.removeCallbacks(animRunnable);
		mHandler.removeCallbacks(runnable);
		mHandler.postDelayed(animRunnable, 500);
		mHandler.postDelayed(runnable, 500);
		mSecondView.setImageResource(R.drawable.sos_9);
	}

	private void vibrator() {
		mVibrator.vibrate(500);
	}

	private void startSearch() {

		locater = App.getInstance().getLocater();
		locater.addBaiduLocListener(myLocListener);
		mLatLng = new LatLng(locater.getLatitude(), locater.getLongitude());
		searchPolicestation(locater.getLatitude(), locater.getLongitude());
	}

	public void searchPolicestation(double latitude, double longitude) {
		try {
			PoiNearbySearchOption opt = new PoiNearbySearchOption();
			opt.location(new LatLng(latitude, longitude));
			opt.keyword(getString(R.string.activity_nearsos_police));
			opt.radius(2000);
			opt.pageCapacity(100);

			mPoiType = 0;
			mPoiSearch.searchNearby(opt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searchHospital(double latitude, double longitude) {
		try {
			PoiNearbySearchOption opt = new PoiNearbySearchOption();
			opt.location(new LatLng(latitude, longitude));
			opt.keyword(getString(R.string.activity_nearsos_Hospital));
			opt.radius(2000);
			opt.pageCapacity(100);

			mPoiType = 1;
			mPoiSearch.searchNearby(opt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showSOSResult() {
		boolean onlyLocalUser = true;
		String phoneList = "";
		for (UserInfo userInfo : CoreModel.getInstance().getFriendList()) {
			if (userInfo.getUserId() != LocalUserObject.LOCAL_USER_ID) {
				onlyLocalUser = false;
			} else {
				phoneList = phoneList + userInfo.getPhone() + ";";
			}
		}
		if (onlyLocalUser) {
			String smsContent = getResources().getString(R.string.sms_invite);
			smsContent = String.format(smsContent, PPNetManager.getInstance().getDownloadPage());
			TelephonyUtils.sendSms(SOSActivity.this, phoneList, smsContent, false);
			return;
		} else {
			((LinearLayout) this.findViewById(R.id.layout_sos_countdown)).setVisibility(View.GONE);
			((LinearLayout) this.findViewById(R.id.layout_sos_result)).setVisibility(View.VISIBLE);
			UserObject sosObj = new UserObject();
			BaiduLoc loc = App.getInstance().getLocater();
			sosObj.setLat(loc.getLat());
			sosObj.setLng(loc.getLng());
			sosObj.setLocation(loc.getAddress());
			sendMessage(YSMSG.REQ_SEND_SOS, 0, 0, sosObj);
			// vibrator();
		}
	}

	private void stopAnim() {
		mCountdown = false;
		mCircleDownView.clearAnimation();
		mHandler.removeMessages(1);
		mHandler.removeCallbacks(animRunnable);
		mHandler.removeCallbacks(runnable);
		// mSecondView.setImageResource(R.drawable.sos_9);
	}

	Runnable animRunnable = new Runnable() {

		@Override
		public void run() {
			// mCircleUpView.startAnimation(mUpRotateAnim);
			mCircleDownView.startAnimation(mDownRotateAnim);
		}
	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			mCountdown = true;
			Message message = new Message();
			message.what = 1;
			mHandler.sendMessage(message);
			mTickCount--;
			if (mTickCount >= 0) {
				mHandler.postDelayed(this, 1000);
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();

		showMapinfo();
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopAnim();
		mMapView.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (((LinearLayout) this.findViewById(R.id.layout_sos_result)).getVisibility() != View.VISIBLE) {
			mHandler.removeCallbacks(runnable);
			showSOSResult();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopAnim();
		mMapView.onDestroy();
	}

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == YSMSG.RESP_SEND_SOS) {
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

		if (1 == msg.what) {
			if (mTickCount >= 0) {
				mCountdown = true;
				if (mTickCount < 9) {
					mSecondView.setImageResource(mResId[mTickCount]);
				}
			} else {
				mCountdown = false;
				mHandler.removeCallbacks(runnable);
				showSOSResult();

			}
		}
	}

	/**
	 * 初始化AMap对象
	 */
	private void initMap() {
		setUpMapIfNeeded();
		ImageView mapCenterImg = (ImageView) findViewById(R.id.sos_map_centerImg);
		Animation anim = AnimationUtils.loadAnimation(SOSActivity.this, R.anim.sosmap_centerscale_anim);
		mapCenterImg.startAnimation(anim);
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMapIfNeeded() {
		BaiduMapOptions bo = new BaiduMapOptions().compassEnabled(false).overlookingGesturesEnabled(false).rotateGesturesEnabled(false)
				.scaleControlEnabled(false).scrollGesturesEnabled(false).zoomControlsEnabled(false).zoomGesturesEnabled(false);
		mMapView = new MapView(SOSActivity.this, bo);
		FrameLayout fLayout = (FrameLayout) findViewById(R.id.sos_map);
		fLayout.addView(mMapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
		mBaiduMap.setMapStatus(msu);

	}

	public void showMapinfo() {
		if (CoreModel.getInstance().getUserInfo() != null) {
			UserObject myselef = CoreModel.getInstance().getUserInfo();
			View popView = LayoutInflater.from(SOSActivity.this).inflate(R.layout.layout_map_info_window, null);
			Button refreshBtn = (Button) popView.findViewById(R.id.btn_info_window_refresh);
			TextView location = (TextView) popView.findViewById(R.id.tv_info_window_location);
			TextView time = (TextView) popView.findViewById(R.id.tv_info_window_time);
			refreshBtn.setVisibility(View.GONE);
			location.setText(myselef.getLocation());

			String date = DateUtils.getCurrDateString("yyyy-MM-dd HH:mm");
			time.setText(date);
			LatLng latlng = new LatLng(myselef.getLat2(), myselef.getLng2());
			InfoWindow window = new InfoWindow(popView, latlng, -20);
			mBaiduMap.showInfoWindow(window);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
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
					smsContent = String.format(smsContent, PPNetManager.getInstance().getDownloadPage());
					if (msgtext.equals(smsContent)) {
						((LinearLayout) SOSActivity.this.findViewById(R.id.layout_sos_countdown)).setVisibility(View.GONE);
						((LinearLayout) SOSActivity.this.findViewById(R.id.layout_sos_result)).setVisibility(View.VISIBLE);
					}
				}

			}
		}
	}

	public class PoisAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;

		public PoisAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return poiItems.size();
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
				convertView = mInflater.inflate(R.layout.list_item_pois, null);

				holder.imgCall = (ImageView) convertView.findViewById(R.id.img_sos_pois_call);
				holder.tvName = (TextView) convertView.findViewById(R.id.txt_sos_pois_name);
				holder.tvHowFar = (TextView) convertView.findViewById(R.id.txt_sos_pois_distance);
				holder.tvAddr = (TextView) convertView.findViewById(R.id.txt_sos_pois_address);
				holder.tvPhone = (TextView) convertView.findViewById(R.id.txt_sos_pois_phone);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position >= 0 && position < poiItems.size()) {
				final PoiInfo poi = poiItems.get(position);
				if (poi != null) {
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View paramView) {
							startSOSMapActivity(poi);
						}
					});

					if (!TextUtils.isEmpty(poi.phoneNum)) {
						holder.tvPhone.setText(poi.phoneNum);
						holder.imgCall.setImageResource(R.drawable.sosnearby_phone);
						holder.imgCall.setEnabled(true);
						holder.imgCall.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								showCallPhoneDialog(poi.phoneNum);
							}
						});
					} else {
						holder.tvPhone.setText(getString(R.string.activity_nearsos_nophone));
						holder.imgCall.setImageResource(R.drawable.sosnearby_nophone);
						holder.imgCall.setEnabled(false);
					}
					holder.tvName.setText(poi.name);
					holder.tvHowFar.setText((int) DistanceUtil.getDistance(mLatLng, poi.location) + getString(R.string.activity_nearsos_mi));
					holder.tvAddr.setText(poi.address);
				}
			}

			return convertView;
		}

		private void startSOSMapActivity(PoiInfo poi) {
			if (poi != null) {
				Bundle bundle = new Bundle();
				bundle.putString("name", poi.name);
				bundle.putString("adress", poi.address);
				bundle.putDouble("latitude", poi.location.latitude);
				bundle.putDouble("longitude", poi.location.longitude);
				SOSMapActivity.startActivity(mActivity, bundle);
			}
		}

		class ViewHolder {
			public ImageView imgCall;
			public TextView tvName;
			public TextView tvHowFar;
			public TextView tvAddr;
			public TextView tvPhone;
		}
	}

	protected void showCallPhoneDialog(final String phone) {
		DialogHelper.showTwoDialog(mActivity, false, null, phone, getString(R.string.dialog_callphone_yes), getString(R.string.dialog_callphone_no), true,
				new OnClickListener() {
					public void onClick(View v) {
						TelephonyUtils.call(mActivity, phone);
					}
				}, null);
	}

	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {

		}

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {

			return;
		}

		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			List<PoiInfo> infos = result.getAllPoi();
			poiItems.clear();
			poiItems.addAll(infos);
			mAdapter.notifyDataSetChanged();
			return;
		}

	}

	BaiduLocListener myLocListener = new BaiduLocListener() {
		public void onLocationChanged(boolean result, ReverseGeoCodeResult geoResult) {
			startSearch();
		}
	};
}
