package cn.changl.safe360.android.ui.sos;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.map.BaiduLoc;
import cn.changl.safe360.android.map.BaiduLocListener;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.utils.ExtraName;
import cn.changl.safe360.android.utils.TelephonyUtils;

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

public class NearSOSActivity extends BaseExActivity implements OnGetPoiSearchResultListener {
	private static final int PAGE_SIZE = 20;
	protected TitleBarHolder mTitleBar;

	private PoiSearch mPoiSearch = null;
	private String city = "";

	private ListView mListView;
	private PoisAdapter mAdapter;
	private int mPoiType = 0;
	List<PoiInfo> poiItems;
	LatLng mLatLng;
	BaiduLoc locater;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, NearSOSActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_sos);

		initView();
		initData();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_sos_result_nearby);

		poiItems = new ArrayList<PoiInfo>();
		mListView = (ListView) this.findViewById(R.id.lv_locations);
		mAdapter = new PoisAdapter(this);
		mListView.setAdapter(mAdapter);
	}

	private void initData() {

		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		startSearch();
		showWaitingDialog();
	}

	private void startSearch() {

		locater = App.getInstance().getLocater();
		locater.addBaiduLocListener(myLocListener);
		mLatLng = new LatLng(locater.getLatitude(), locater.getLongitude());
		searchPolicestation(locater.getLatitude(), locater.getLongitude());
	}

	protected void onPause() {
		super.onPause();

	}

	protected void onStop() {
		super.onStop();
		locater.removeBaiduLocListener(myLocListener);
	}

	protected void onDestroy() {
		super.onDestroy();
		locater.removeBaiduLocListener(myLocListener);
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

						holder.imgCall.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								TelephonyUtils.call(mActivity, poi.phoneNum);
							}
						});
					} else {
						holder.tvPhone.setText(getString(R.string.activity_nearsos_nophone));
						holder.imgCall.setImageResource(R.drawable.sosnearby_nophone);
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
				// bundle.putParcelable(ExtraName.EN_POI, poi);
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

	public void onGetPoiDetailResult(PoiDetailResult arg0) {

	}

	public void onGetPoiResult(PoiResult result) {
		dismissWaitingDialog();

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

		}
	};
}
