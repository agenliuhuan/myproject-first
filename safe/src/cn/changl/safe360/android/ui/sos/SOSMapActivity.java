package cn.changl.safe360.android.ui.sos;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class SOSMapActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	LatLng mPoiLatLng;
	String mPoiName;
	String mPoiAdress;

	public static void startActivity(Context context, Bundle extras) {
		ActivityUtils.startActivity(context, SOSMapActivity.class, extras);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sos_map);
		locatePoi();
		initView();
		setUpMapIfNeeded();
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(getString(R.string.activity_sosmap_title));
	}

	private void locatePoi() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mPoiName = bundle.getString("name");
			mPoiAdress = bundle.getString("adress");
			mPoiLatLng = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
		}
	}

	private void setUpMapIfNeeded() {
		BaiduMapOptions bo = new BaiduMapOptions().compassEnabled(false).overlookingGesturesEnabled(false).rotateGesturesEnabled(false)
				.scaleControlEnabled(false).scrollGesturesEnabled(true).zoomControlsEnabled(false).zoomGesturesEnabled(true);
		mMapView = new MapView(SOSMapActivity.this, bo);
		FrameLayout fLayout = (FrameLayout) findViewById(R.id.sos_map_map);
		fLayout.addView(mMapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
		mBaiduMap.setMapStatus(msu);

	}

	public void showMapinfo() {
		if (CoreModel.getInstance().getUserInfo() != null) {
			View popView = LayoutInflater.from(SOSMapActivity.this).inflate(R.layout.layout_map_info_window, null);
			Button refreshBtn = (Button) popView.findViewById(R.id.btn_info_window_refresh);
			TextView location = (TextView) popView.findViewById(R.id.tv_info_window_location);
			TextView time = (TextView) popView.findViewById(R.id.tv_info_window_time);
			refreshBtn.setVisibility(View.GONE);
			location.setText(mPoiName);
			time.setText(mPoiAdress);
			InfoWindow window = new InfoWindow(popView, mPoiLatLng, 0);
			mBaiduMap.showInfoWindow(window);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(mPoiLatLng));
		}
	}

	public void onResume() {
		super.onResume();
		mMapView.onResume();
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
	}

}
