package cn.changl.safe360.android.ui.escort;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.image.universalimageloader.core.process.BitmapProcessor;
import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.ImageUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.api.PPNetManager;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.LocalTripObject;
import cn.changl.safe360.android.biz.vo.LocalUserObject;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.TripObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.db.dao.LocalTripObjectDao;
import cn.changl.safe360.android.map.BaiduLoc;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.DialogHelper;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.utils.DateUtils;
import cn.changl.safe360.android.utils.FileUtils;
import cn.changl.safe360.android.utils.ImageLoaderHelper;
import cn.changl.safe360.android.utils.PreferencesUtils;
import cn.changl.safe360.android.utils.TelephonyUtils;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.Trip;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public class StartEscortActivity extends BaseExActivity {

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	protected TitleBarHolder mTitleBar;
	GridView familyGrid;
	Button StartEscortBtn;
	boolean selectedALL = false;
	ArrayList<Integer> selectedArray = new ArrayList<Integer>();
	ArrayList<UserInfo> friendList;
	List<UserInfo> mLocalTripList;
	ImageView mMyAvatar;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, StartEscortActivity.class);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startescort);
		initView();
		showGuide();
	}

	private void showGuide() {
		final RelativeLayout startGuide = (RelativeLayout) findViewById(R.id.startescort_guide);
		Button startGuideBtn = (Button) findViewById(R.id.startescort_guide_knowBtn);
		boolean show = PreferencesUtils.getShowGuideValue("startGuide");
		if (show) {
			startGuide.setVisibility(View.VISIBLE);
		}
		startGuideBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startGuide.setVisibility(View.GONE);
				PreferencesUtils.setShowGuideValue("startGuide", false);
			}
		});
		startGuide.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(StartEscortActivity.this);
		mTitleBar.mTitle.setText(getString(R.string.activity_startescort_titile));

		mLocalTripList = new ArrayList<UserInfo>();
		mMyAvatar = new ImageView(this);

		familyGrid = (GridView) findViewById(R.id.startescort_familiesGrid);
		StartEscortBtn = (Button) findViewById(R.id.startescort_startBtn);
		friendList = new ArrayList<UserInfo>();
		sendEmptyMessage(YSMSG.REQ_GET_IDLE_FRIEND);

		initMap();

		FamilyGridAdapter adapter = new FamilyGridAdapter(getBaseContext());
		familyGrid.setAdapter(adapter);

		familyGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					if (selectedALL) {
						selectedALL = false;
						selectedArray.clear();
					} else {
						if (friendList.size() > 5) {
							YSToast.showToast(StartEscortActivity.this, R.string.activity_startescort_notmore);
						} else {
							selectedALL = true;
							selectedArray.clear();
							addallposition();
						}

					}
				} else {
					if (selectedALL) {
						selectedALL = false;
						modifyPosition(position);
					} else {
						modifyPosition(position);
					}
				}
				FamilyGridAdapter adapter = (FamilyGridAdapter) familyGrid.getAdapter();
				adapter.notifyDataSetChanged();
				// if (selectedArray.size() == 0) {
				// StartEscortBtn.setEnabled(false);
				// } else {
				// StartEscortBtn.setEnabled(true);
				// }
			}
		});
		StartEscortBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveArray();
			}
		});
		getArray();
		// if (selectedArray.size() == 0) {
		// StartEscortBtn.setEnabled(false);
		// } else {
		// StartEscortBtn.setEnabled(true);
		// }
	}

	private void startTrip(ArrayList<UserInfo> escortList) {
		showWaitingDialog();
		TripObject tripObj = new TripObject();
		BaiduLoc locater = App.getInstance().getLocater();
		tripObj.setBeginAdress(locater.getAddress());
		tripObj.setBeginLat(locater.getLat());
		tripObj.setBeginLng(locater.getLng());
		tripObj.setUserinfos(escortList);
		sendMessage(YSMSG.REQ_START_TRIP, 0, 0, tripObj);

		// StartEscortActivity.this.finish();
		// EscortActivity.startActivity(StartEscortActivity.this, true);

		// TripObject tripObj = new TripObject();
		// tripObj.setTripId(0);
		// BaiduLoc locater = App.getInstance().getLocater();
		// tripObj.setEndAdress(locater.getAddress());
		// tripObj.setEndLat(locater.getLat());
		// tripObj.setEndLng(locater.getLng());
		// sendMessage(YSMSG.REQ_FINISH_TRIP, 0, 0, tripObj);

	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();

		mBaiduMap.clear();
		if (CoreModel.getInstance().getUserInfo() != null) {
			UserObject myselef = CoreModel.getInstance().getUserInfo();
			addMarker(myselef.getImage(), myselef.getLat2(), myselef.getLng2());
		}
		showMapinfo();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		dismissWaitingDialog();
	}

	private void saveArray() {
		mLocalTripList.clear();
		PreferencesUtils.setEscortUserSize(FileUtils.APP, "escort_user_size", selectedArray.size());
		ArrayList<UserInfo> escortList = new ArrayList<UserInfo>();
		for (int index = 0; index < selectedArray.size(); index++) {
			FamilyGridAdapter adapter = (FamilyGridAdapter) familyGrid.getAdapter();
			if (adapter.getCount() > 0 && selectedArray.get(index) < adapter.getCount()) {
				UserInfo info = adapter.getItem(selectedArray.get(index));
				if (info.getUserId() != LocalUserObject.LOCAL_USER_ID) {
					escortList.add(info);
					PreferencesUtils.setEscortUserValue(FileUtils.APP, "escort_userid" + index, info.getUserId());
				} else {
					mLocalTripList.add(info);
				}
			}
		}
		startTrip(escortList);
	}

	private void getArray() {
		int size = PreferencesUtils.getEscortUserSize(FileUtils.APP, "escort_user_size");

		for (int index = 0; index < size; index++) {
			int userid = PreferencesUtils.getEscortUserValue(FileUtils.APP, "escort_userid" + index);
			for (int position = 0; position < friendList.size(); position++) {
				if (userid == friendList.get(position).getUserId()) {
					UserInfo userinfo = friendList.get(position);
					if (checkUserinfoStatus(userinfo)) {
						selectedArray.add(position + 1);
					}
				}
			}
		}
		FamilyGridAdapter adapter = (FamilyGridAdapter) familyGrid.getAdapter();
		adapter.notifyDataSetChanged();
	}

	private void modifyPosition(int position) {
		boolean exist = false;
		for (int index = 0; index < selectedArray.size(); index++) {
			if (selectedArray.get(index) == position) {
				exist = true;
				selectedArray.remove(index);
				break;
			}
		}

		if (!exist) {
			if (selectedArray.size() >= 5) {
				YSToast.showToast(StartEscortActivity.this, R.string.activity_startescort_notmore);
			} else {
				selectedArray.add(position);
			}

		}
	}

	private void addallposition() {
		for (int index = 1; index < friendList.size() + 1; index++) {
			UserInfo userinfo = friendList.get(index - 1);
			if (checkUserinfoStatus(userinfo)) {
				selectedArray.add(index);
			}
		}
	}

	private boolean checkUserinfoStatus(UserInfo userinfo) {
		// if (userinfo.getTripStatus() == 0) {
		// return true;
		// }
		// if (userinfo.getTripStatus() == 1) {
		// String toast = String.format(getString(R.string.toast_user_escorted),
		// userinfo.getNickname());
		// YSToast.showToast(StartEscortActivity.this, toast);
		// return false;
		// }
		// if (userinfo.getTripStatus() == 2) {
		// String toast =
		// String.format(getString(R.string.toast_user_escorting),
		// userinfo.getNickname());
		// YSToast.showToast(StartEscortActivity.this, toast);
		// return false;
		// }
		return true;
	}

	private void initMap() {
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		BaiduMapOptions bo = new BaiduMapOptions().compassEnabled(false).overlookingGesturesEnabled(false).rotateGesturesEnabled(false)
				.scaleControlEnabled(false).scrollGesturesEnabled(false).zoomControlsEnabled(false).zoomGesturesEnabled(false);
		mMapView = new MapView(StartEscortActivity.this, bo);
		FrameLayout fLayout = (FrameLayout) findViewById(R.id.startescort_map);
		fLayout.addView(mMapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
		mBaiduMap.setMapStatus(msu);
	}

	public void showMapinfo() {
		if (CoreModel.getInstance().getUserInfo() != null) {
			UserObject myselef = CoreModel.getInstance().getUserInfo();
			View popView = LayoutInflater.from(StartEscortActivity.this).inflate(R.layout.layout_map_info_window, null);
			Button refreshBtn = (Button) popView.findViewById(R.id.btn_info_window_refresh);
			TextView location = (TextView) popView.findViewById(R.id.tv_info_window_location);
			TextView time = (TextView) popView.findViewById(R.id.tv_info_window_time);
			refreshBtn.setVisibility(View.GONE);
			BaiduLoc loc = App.getInstance().getLocater();
			if (TextUtils.isEmpty(loc.getAddress())) {
				location.setText(myselef.getLocation());
			} else {
				location.setText(loc.getAddress());
			}
			String date = DateUtils.getshowDateString(myselef.getLocationChangeTime());
			if (!TextUtils.isEmpty(date)) {
				time.setText(getString(R.string.mainfragment_window_updatetiem) + date);
			}
			LatLng latlng = new LatLng(myselef.getLat2(), myselef.getLng2());
			InfoWindow window = new InfoWindow(popView, latlng, -102);
			mBaiduMap.showInfoWindow(window);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
		}
	}

	public void addMarker(String imgUrl, double latitude, double longitude) {
		String tempimg = imgUrl.replace(PPNetManager.HTTP, "").replace(PPNetManager.IMAGE_DOMAIN, "").replace(PPNetManager.IMAGE_DOMAIN2, "")
				.replace("!avatar.def", "");
		if (!TextUtils.isEmpty(imgUrl) && !TextUtils.isEmpty(tempimg)) {
			AvatarProcessor process = new AvatarProcessor();
			// View popView =
			// LayoutInflater.from(StartEscortActivity.this).inflate(R.layout.layout_map_people_window,
			// null);
			// ImageView userimg = (ImageView)
			// popView.findViewById(R.id.people_window_img);
			ImageLoaderHelper.displayImage(imgUrl, mMyAvatar, R.drawable.icon_family, process, true, 90);
		} else {
			View popView = LayoutInflater.from(StartEscortActivity.this).inflate(R.layout.layout_map_people_window, null);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(popView);
			OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(bitmap);
			mBaiduMap.addOverlay(oop);
		}
	}

	public class AvatarProcessor implements BitmapProcessor {

		public Bitmap process(Bitmap bitmap) {
			try {
				Bitmap output = convertViewToBitmapEx(bitmap);
				BitmapDescriptor viewbitmap;
				if (output == null) {
					View popView = LayoutInflater.from(StartEscortActivity.this).inflate(R.layout.layout_map_people_window, null);
					viewbitmap = BitmapDescriptorFactory.fromView(popView);
				} else {
					viewbitmap = BitmapDescriptorFactory.fromBitmap(output);
				}
				UserObject myselef = CoreModel.getInstance().getUserInfo();
				LatLng latlng = new LatLng(myselef.getLat2(), myselef.getLng2());
				OverlayOptions oop = new MarkerOptions().position(latlng).icon(viewbitmap).visible(true);
				mBaiduMap.addOverlay(oop);
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

	class FamilyGridAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public FamilyGridAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {

			return friendList.size() + 1;
		}

		public UserInfo getItem(int position) {

			return friendList.get(position - 1);
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.family_griditem, null);
				// 102*102
				holder.contactimg = (ImageView) convertView.findViewById(R.id.griditem_contact_img);
				holder.contactSelect = (ImageView) convertView.findViewById(R.id.griditem_contact_exitimg);
				holder.contactRL = (RelativeLayout) convertView.findViewById(R.id.griditem_contactRL);
				holder.contactname = (TextView) convertView.findViewById(R.id.griditem_contact_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (selectedArray.size() >= friendList.size()) {
				selectedALL = true;
			}
			if (selectedALL) {
				if (position == 0) {
					holder.contactname.setVisibility(View.GONE);
					holder.contactRL.setBackgroundResource(R.drawable.startescort_griditem_allbgselected);
				} else {
					holder.contactname.setVisibility(View.VISIBLE);
					holder.contactimg.setBackgroundResource(R.drawable.icon_escort_family);
					holder.contactRL.setBackgroundResource(R.drawable.startescort_griditem_selectedbg);
					selectContact(holder.contactSelect, true);
				}
			} else {
				if (position == 0) {
					holder.contactname.setVisibility(View.GONE);
					holder.contactRL.setBackgroundResource(R.drawable.startescort_griditem_allbg);
				} else if (selectedArray.contains(position)) {
					holder.contactname.setVisibility(View.VISIBLE);
					holder.contactimg.setBackgroundResource(R.drawable.icon_escort_family);
					holder.contactRL.setBackgroundResource(R.drawable.startescort_griditem_selectedbg);
					selectContact(holder.contactSelect, true);
				} else {
					holder.contactname.setVisibility(View.VISIBLE);
					holder.contactimg.setBackgroundResource(R.drawable.icon_escort_family);
					holder.contactRL.setBackgroundResource(R.drawable.startescort_griditem_bg);
					selectContact(holder.contactSelect, false);
				}
			}
			if (position != 0) {
				UserInfo info = friendList.get(position - 1);
				UserObject userobj = UserObject.createFromPb(info);
				if (!TextUtils.isEmpty(userobj.getUploadImage())) {
					ImageLoaderHelper.displayImage(userobj.getDisplayImage(), holder.contactimg, R.drawable.icon_family, true);
				} else {
					holder.contactimg.setImageResource(R.drawable.icon_family);
				}
				if (!TextUtils.isEmpty(userobj.getNickname())) {
					holder.contactname.setText(userobj.getNickname());
				}
			}
			return convertView;
		}

		private void selectContact(ImageView contact, boolean select) {
			if (select) {
				contact.setBackgroundResource(R.drawable.startescort_griditem_selectedbg);
				contact.setVisibility(View.VISIBLE);
			} else {
				contact.setVisibility(View.INVISIBLE);
			}
		}

		public final class ViewHolder {
			public ImageView contactimg;
			public RelativeLayout contactRL;
			public ImageView contactSelect;
			public TextView contactname;
		}
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case 2: {

		}
			break;
		case YSMSG.RESP_GET_IDLE_FRIEND: {
			if (msg.arg1 == 200) {
				// 成功
				List<UserInfo> list = (List<UserInfo>) msg.obj;
				if (list != null) {
					friendList.clear();
					getIdleFriend(list);
					FamilyGridAdapter adapter = (FamilyGridAdapter) familyGrid.getAdapter();
					adapter.notifyDataSetChanged();
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
		case YSMSG.RESP_START_TRIP:
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 成功
				Trip trip = (Trip) msg.obj;
				if (trip != null) {
					LocalTripObjectDao tripDao = new LocalTripObjectDao();
					for (UserInfo userInfo : mLocalTripList) {
						tripDao.insert(new LocalTripObject(trip.getTripId(), userInfo.getPhone()));
					}
					CoreModel.getInstance().setTrip(trip);
					EscortActivity.startActivity(StartEscortActivity.this);
					StartEscortActivity.this.finish();
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
		}
	}

	private void getIdleFriend(List<UserInfo> list) {
		ArrayList<Integer> useridlist = new ArrayList<Integer>();
		for (UserInfo info : list) {
			useridlist.add(info.getUserId());
		}
		List<UserInfo> mainList = CoreModel.getInstance().getFriendList();
		if (mainList != null) {
			for (UserInfo info : mainList) {
				if (useridlist.contains(info.getUserId())) {
					friendList.add(info);
				}
			}
		}
	}

	private void sendEscortDialog(final int position, final String phone) {
		DialogHelper.showTwoDialog(StartEscortActivity.this, false, null, getString(R.string.dialog_addtoescort_content),
				getString(R.string.dialog_addtoescort_yes), getString(R.string.dialog_addtoescort_no), true, new OnClickListener() {
					public void onClick(View arg0) {
						String smsContent = getString(R.string.activity_startescort_smscontent);
						TelephonyUtils.sendSms(StartEscortActivity.this, phone, smsContent, false);
						selectedArray.add(position);
					}
				}, null);
	}
}
