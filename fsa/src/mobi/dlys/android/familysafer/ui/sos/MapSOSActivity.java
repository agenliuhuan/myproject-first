package mobi.dlys.android.familysafer.ui.sos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapListener;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView.MyMarker;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.location.LostActivity;
import mobi.dlys.android.familysafer.utils.MyAnimationUtils;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.utils.DistanceUtil;

public class MapSOSActivity extends BaseExActivity implements BaiduMapListener {
	protected TitleBarHolder mTitleBar;
	BaiduMapView mBaiduMapView = null;

	Button mCallButton = null;
	Button mCancelButton = null;

	Button mCall110Button = null;

	RelativeLayout mPopTip = null;
	LinearLayout mPopCall = null;
	LinearLayout mPopCall110 = null;

	LinearLayout mPopCallView = null;

	TextView tvName = null;
	TextView tvLocation = null;

	String mPhone = "";

	private TextView tvLoc = null;
	private ImageView imgUser = null;

	private boolean mSearchAgin = false;

	RelativeLayout mPoisList1 = null;
	RelativeLayout mPoisList2 = null;

	private int mTag = 0;

	class PoiObject implements Comparable {
		public String mName;
		public int mHowFar;
		public String mAddr;
		public String mPhone;
		public LatLng mLoc;

		@Override
		public int compareTo(Object another) {
			PoiObject p = (PoiObject) another;
			if (mHowFar > p.mHowFar) {
				return 1;
			}
			if (mHowFar < p.mHowFar) {
				return -1;
			}
			return 0;
		}
	}

	LatLng mLatLng = null;
	ArrayList<PoiObject> mPois = null;

	private ImageView mImageMine = null;
	private ImageView mImageZoomin = null;
	private ImageView mImageZoomout = null;
	private float mZoomLevel = 16;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != mPopCall) {
				if (mPopCall.isShown()) {
					if (null != mPopCall) {
						MyAnimationUtils.hideBottomView(mPopCall, mPopCallView, MapSOSActivity.this);
					}

					return true;
				}
			}
			if (null != mPoisList1) {
				if (mPoisList1.isShown()) {
					if (null != mPoisList2) {
						MyAnimationUtils.hideRightView(mPoisList1, mPoisList2, MapSOSActivity.this);
					}

					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private View mPopview = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapsos);

		initView();
		initData();
	}

	void initView() {
		mImageMine = (ImageView) findViewById(R.id.img_mapsos_mine);
		mImageMine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				App app = (App) getApplication();
				BaiduLoc locater = app.getLocater();
				myPosition = new LatLng(locater.getLatitude(), locater.getLongitude());     
				mBaiduMapView.setCenter(-1, locater.getLatitude(), locater.getLongitude());
				mImageMine.setImageResource(R.drawable.img_map_located);
			}
		});

		mImageZoomin = (ImageView) findViewById(R.id.img_mapsos_zoomin);
		mImageZoomin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mZoomLevel++;
				if (mZoomLevel > 19) {
					mZoomLevel = 19;
					mImageZoomin.setEnabled(false);
					YSToast.showToast(MapSOSActivity.this, R.string.toast_map_zoomout_max);
				}
				mImageZoomout.setEnabled(true);
				mBaiduMapView.setZoomLevel(mZoomLevel);
			}
		});

		mImageZoomout = (ImageView) findViewById(R.id.img_mapsos_zoomout);
		mImageZoomout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mZoomLevel--;
				if (mZoomLevel < 3) {
					mZoomLevel = 3;
					mImageZoomout.setEnabled(false);
					YSToast.showToast(MapSOSActivity.this, R.string.toast_map_zoomin_min);
				}
				mImageZoomin.setEnabled(true);
				mBaiduMapView.setZoomLevel(mZoomLevel);
			}
		});

		mPoisList1 = (RelativeLayout) findViewById(R.id.layout_mapsos_list);
		mPoisList2 = (RelativeLayout) findViewById(R.id.layout_mapsos_list2);
		mPoisList1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyAnimationUtils.hideRightView(mPoisList1, mPoisList2, MapSOSActivity.this);
			}
		});

		mPoisList2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_mapsos_titlebar_tip);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setBackgroundResource(R.drawable.button_titlebar_right_list_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);
		mTitleBar.mRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						AnalyticsHelper.onEvent(MapSOSActivity.this, AnalyticsHelper.index_click_list);
					}
				}, 1000);
				if (mPopTip.getVisibility() == View.GONE) {
					if (mPoisList1.getVisibility() == View.GONE) {
						MyAnimationUtils.showRightView(mPoisList1, mPoisList2, MapSOSActivity.this);
					} else {
						MyAnimationUtils.hideRightView(mPoisList1, mPoisList2, MapSOSActivity.this);
					}
				}
			}
		});

		mBaiduMapView = new BaiduMapView(this, R.id.layout_mapsos, true, true);
		mBaiduMapView.setListener(this);

		tvName = (TextView) findViewById(R.id.tv_mapsos_name);
		tvLocation = (TextView) findViewById(R.id.tv_mapsos_location);

		mCallButton = (Button) findViewById(R.id.btn_mapsos_call);
		mCancelButton = (Button) findViewById(R.id.btn_mapsos_cancel);
		mCall110Button = (Button) findViewById(R.id.btn_call_110);

		mPopTip = (RelativeLayout) findViewById(R.id.layout_mapsos_locate);
		mPopCall = (LinearLayout) findViewById(R.id.layout_mapsos_call);
		mPopCallView = (LinearLayout) findViewById(R.id.layout_mapsos_call_view);

		mPopCall.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (View.VISIBLE == mPopCall.getVisibility() && View.VISIBLE == mPopCallView.getVisibility()) {
					MyAnimationUtils.hideBottomView(mPopCall, mPopCallView, MapSOSActivity.this);
				}
			}
		});

		mPopCallView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

			}
		});

		mPopCall110 = (LinearLayout) findViewById(R.id.layout_mapsos_call_110);

		mCallButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (0 == mTag) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							AnalyticsHelper.onEvent(MapSOSActivity.this, AnalyticsHelper.index_call_policestation);
						}
					}, 1000);
				} else {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							AnalyticsHelper.onEvent(MapSOSActivity.this, AnalyticsHelper.index_call_hospital);
						}
					}, 1000);
				}
				MyAnimationUtils.hideBottomView(mPopCall, mPopCallView, MapSOSActivity.this);

				if (!TextUtils.isEmpty(mPhone)) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
					startActivity(intent);
				}
			}

		});

		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mPopCall) {
					MyAnimationUtils.hideBottomView(mPopCall, mPopCallView, MapSOSActivity.this);

				}
			}

		});

		mCall110Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						AnalyticsHelper.onEvent(MapSOSActivity.this, AnalyticsHelper.index_call_110);
					}
				}, 1000);
				TelephonyUtils.call(MapSOSActivity.this, "110");
			}

		});

		mPopTip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		mPopTip.setVisibility(View.VISIBLE);
		mPopCall110.setVisibility(View.VISIBLE);

		mPopview = LayoutInflater.from(this).inflate(R.layout.layout_avatatar_pop, mBaiduMapView.getLayout());
		tvLoc = (TextView) mPopview.findViewById(R.id.tv_checkin_location);
		tvLoc.setVisibility(View.GONE);
		imgUser = (ImageView) mPopview.findViewById(R.id.img_checkin_avatar);

		mPois = new ArrayList<PoiObject>();
		mListview = (ListView) this.findViewById(R.id.lv_pois);
		mAdapter = new PoisAdapter(this);
		mListview.setAdapter(mAdapter);
		mListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				PoiObject poi = mPois.get(arg2);
				if (poi != null) {
					mBaiduMapView.setCenter(-1, poi.mLoc.latitude, poi.mLoc.longitude);
				}
				MyAnimationUtils.hideRightView(mPoisList1, mPoisList2, MapSOSActivity.this);
			}
		});

		if (CoreModel.getInstance().getUserInfo() != null) {
			String location = CoreModel.getInstance().getUserInfo().getLocation();
			if (TextUtils.isEmpty(location)) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(MapSOSActivity.this, LostActivity.class);
						intent.putExtra("rid", MapSOS_Lost_Action_Id_Result);
						startActivityForResult(intent, MapSOS_Lost_Action_Id);
					}
				}, 1000);
			}
		}

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				startRadar();
			}
		}, 500);
	}

	ImageView radar_dian1;
	ImageView radar_dian2;
	ImageView radar_dian3;
	ImageView radar_dian4;
	ImageView radar_scaning;
	Animation dianAnim1;
	Animation dianAnim2;
	Animation dianAnim3;
	Animation dianAnim4;
	Animation scaningAnim;

	private void startRadar() {
		radar_scaning = (ImageView) findViewById(R.id.im_scan);
		radar_dian1 = (ImageView) findViewById(R.id.im_dian1);
		radar_dian2 = (ImageView) findViewById(R.id.im_dian2);
		radar_dian3 = (ImageView) findViewById(R.id.im_dian3);
		radar_dian4 = (ImageView) findViewById(R.id.im_dian4);

		scaningAnim = AnimationUtils.loadAnimation(this, R.anim.radar_scaning_anim);
		dianAnim1 = AnimationUtils.loadAnimation(this, R.anim.dian_anim1);
		dianAnim2 = AnimationUtils.loadAnimation(this, R.anim.dian_anim2);
		dianAnim3 = AnimationUtils.loadAnimation(this, R.anim.dian_anim3);
		dianAnim4 = AnimationUtils.loadAnimation(this, R.anim.dian_anim4);

		radar_scaning.startAnimation(scaningAnim);
		radar_dian1.startAnimation(dianAnim1);
		radar_dian2.startAnimation(dianAnim2);
		radar_dian3.startAnimation(dianAnim3);
		radar_dian4.startAnimation(dianAnim4);

	}

	private void clearAnim() {
		radar_scaning.clearAnimation();
		radar_dian1.clearAnimation();
		radar_dian2.clearAnimation();
		radar_dian3.clearAnimation();
		radar_dian4.clearAnimation();
	}

	private int MapSOS_Lost_Action_Id_Result = 100001;
	private int MapSOS_Lost_Action_Id = 100002;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MapSOS_Lost_Action_Id) {
			if (resultCode == MapSOS_Lost_Action_Id_Result) {
				initData();
			}
		}
	}

	private void initData() {
		if (CoreModel.getInstance().getUserInfo() != null) {
			App app = (App) getApplication();
			BaiduLoc locater = app.getLocater();
			mBaiduMapView.clearPoi();
			mBaiduMapView.setCenter(locater.getLatitude(), locater.getLongitude());
			mLatLng = new LatLng(locater.getLatitude(), locater.getLongitude());
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					App app = (App) getApplication();
					BaiduLoc locater = app.getLocater();
					mBaiduMapView.setAvatar(0, locater.getLatitude(), locater.getLongitude(), imgUser, CoreModel.getInstance().getUserInfo().getImage(),
							mPopview);
					mBaiduMapView.searchPolicestation(locater.getLatitude(), locater.getLongitude());
					//mSearchAgin = true;
				}
			}, 1000);
		}
	}

	@Override
	public void onMapLoaded() {

	}

	@Override
	public void onMarkerClick(MyMarker mymarker) {
		if (0 == mymarker.mTag) {
			mTag = 0;
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					AnalyticsHelper.onEvent(MapSOSActivity.this, AnalyticsHelper.index_click_policestation);
				}
			}, 1000);
		} else {
			mTag = 1;
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					AnalyticsHelper.onEvent(MapSOSActivity.this, AnalyticsHelper.index_click_hospital);
				}
			}, 1000);
		}

		if (TextUtils.isEmpty(mymarker.mName)) {
			return;
		}

		if (null != mPopCall) {
			MyAnimationUtils.showBottomView(mPopCall, mPopCallView, MapSOSActivity.this);
		}
		if (null != tvName) {
			tvName.setText(mymarker.mName);
		}
		if (null != tvLocation) {
			tvLocation.setText(mymarker.mCity + mymarker.mAddr);
		}

		if (TextUtils.isEmpty(mymarker.mPhone)) {
			if (null != mCallButton) {
				mCallButton.setBackgroundResource(R.drawable.btn_disable);
				mCallButton.setEnabled(false);
			}
			mPhone = "110";
		} else {
			if (null != mCallButton) {
				mCallButton.setBackgroundResource(R.drawable.button_green_selector);
				mCallButton.setEnabled(true);
			}
			mPhone = mymarker.mPhone;
		}

	}

	@Override
	public void onSnapshotReady(Bitmap snapshot) {

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
		mBaiduMapView.onDestroy();
		super.onDestroy();
	}

	private ListView mListview = null;
	private PoisAdapter mAdapter = null;

	public class PoisAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;

		public PoisAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return mPois.size();
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

				holder.imgCall = (ImageView) convertView.findViewById(R.id.img_call);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tvHowFar = (TextView) convertView.findViewById(R.id.tv_howfar);
				holder.tvAddr = (TextView) convertView.findViewById(R.id.tv_addr);
				holder.tvPhone = (TextView) convertView.findViewById(R.id.tv_phone);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.imgCall.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					PoiObject poi = mPois.get(position);
					if (poi != null) {
						if (!TextUtils.isEmpty(poi.mPhone)) {
							TelephonyUtils.call(MapSOSActivity.this, poi.mPhone);
						}
					}

				}
			});

			if (position >= 0 && position < mPois.size()) {
				PoiObject poi = mPois.get(position);
				if (poi != null) {
					if (!TextUtils.isEmpty(poi.mPhone)) {
						holder.tvPhone.setText(poi.mPhone);
						holder.imgCall.setVisibility(View.VISIBLE);
					} else {
						holder.tvPhone.setText("暂无电话");
						holder.imgCall.setVisibility(View.INVISIBLE);
					}
					holder.tvName.setText(poi.mName);
					holder.tvHowFar.setText(poi.mHowFar + "米");
					holder.tvAddr.setText(poi.mAddr);
				}
			}

			return convertView;
		}

		class ViewHolder {
			public ImageView imgCall;
			public TextView tvName;
			public TextView tvHowFar;
			public TextView tvAddr;
			public TextView tvPhone;
		}
	}

	@Override
	public void onSearched(PoiResult result) {
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			List<PoiInfo> infos = result.getAllPoi();
			int count = infos.size();

			for (int i = 0; i < count; i++) {
				PoiInfo info = infos.get(i);
				PoiObject obj = new PoiObject();
				obj.mName = info.name;
				obj.mHowFar = (int) DistanceUtil.getDistance(mLatLng, info.location);
				obj.mAddr = info.address;
				obj.mPhone = info.phoneNum;
				obj.mLoc = info.location;
				mPois.add(obj);
			}
		}
		if (mSearchAgin) {
			App app = (App) getApplication();
			BaiduLoc locater = app.getLocater();
			mBaiduMapView.searchHospital(locater.getLatitude(), locater.getLongitude());
			mSearchAgin = false;
		} else {
			clearAnim();
			mPopTip.setVisibility(View.GONE);
			mTitleBar.mRight.setVisibility(View.VISIBLE);

			Collections.sort(mPois);
			if (null != mAdapter) {
				mAdapter.notifyDataSetChanged();
			}
		}

	}

	private LatLng myPosition = new LatLng(0.0, 0.0);
	@Override
	public void OnMapChanged(MapStatus arg0) {
		if(mZoomLevel != arg0.zoom){
			mZoomLevel = arg0.zoom;
	        if (mZoomLevel > 19) {
	            mZoomLevel = 19;
	        }
	        if (mZoomLevel < 3) {
	            mZoomLevel = 3;
	        }
	        
	        if (mZoomLevel == 19) {
	            mImageZoomin.setEnabled(false);  
	            mImageZoomout.setEnabled(true);
	            YSToast.showToast(MapSOSActivity.this, R.string.toast_map_zoomout_max);
	        }		
	        else
	        {
	            if (mZoomLevel == 3) {
	            	mImageZoomin.setEnabled(true);
	                mImageZoomout.setEnabled(false);
	                YSToast.showToast(MapSOSActivity.this, R.string.toast_map_zoomin_min);
	            }        
	            else
	            {
	                mImageZoomin.setEnabled(true);  
	                mImageZoomout.setEnabled(true);
	            }
	        }			
		}        
        if(myPosition.latitude != arg0.target.latitude || myPosition.longitude != arg0.target.longitude) {
        	if (null != mImageMine) {
        		mImageMine.setImageResource(R.drawable.mapbar_mine_selector);
        	}
        	myPosition = new LatLng(arg0.target.latitude, arg0.target.longitude);
        }
        else
        {
            if (mZoomLevel == 19) {
                YSToast.showToast(MapSOSActivity.this, R.string.toast_map_zoomout_max);
            }
            if (mZoomLevel == 3) {
                YSToast.showToast(MapSOSActivity.this, R.string.toast_map_zoomin_min);
            }
        }
	}

	@Override
	public void OnMapClick() {
	}

}
