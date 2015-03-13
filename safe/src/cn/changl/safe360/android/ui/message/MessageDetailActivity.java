package cn.changl.safe360.android.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.vo.MessageObject;
import cn.changl.safe360.android.db.dao.MessageObjectDao;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class MessageDetailActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;
	MapView mMapView;
	BaiduMap mBaiduMap;
	GeoCoder mSearch;
	LatLng ptCenter;
	MessageObject mMsgObject;
	ImageView mMySelfAvatar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messagedetial);
		initView();
		initData();
	}

	private void initData() {
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(listener);
		if (getIntent() != null) {
			int msgId = getIntent().getIntExtra("messageId", -1);
			mMsgObject = new MessageObjectDao().findById(msgId);
			if (mMsgObject != null) {
				try {
					ptCenter = new LatLng(Double.valueOf(mMsgObject.getLat()), Double.valueOf(mMsgObject.getLng()));
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ptCenter));
	}

	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			}
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result != null || result.error == SearchResult.ERRORNO.NO_ERROR) {
				String adress = result.getAddress();
				showMapinfo(adress);
			}
		}
	};

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent != null) {
			mSearch = GeoCoder.newInstance();
			mSearch.setOnGetGeoCodeResultListener(listener);
			int msgId = getIntent().getIntExtra("messageId", -1);
			mMsgObject = new MessageObjectDao().findById(msgId);
			if (mMsgObject != null) {
				try {
					ptCenter = new LatLng(Double.valueOf(mMsgObject.getLat()), Double.valueOf(mMsgObject.getLng()));
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ptCenter));
		}
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_message_titile);

		mMySelfAvatar = new ImageView(this);
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		BaiduMapOptions bo = new BaiduMapOptions().compassEnabled(false).overlookingGesturesEnabled(false).rotateGesturesEnabled(false)
				.scaleControlEnabled(false).scrollGesturesEnabled(true).zoomControlsEnabled(false).zoomGesturesEnabled(true);
		mMapView = new MapView(MessageDetailActivity.this, bo);

		FrameLayout fLayout = (FrameLayout) findViewById(R.id.messagedetail_map);
		fLayout.addView(mMapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
		mBaiduMap.setMapStatus(msu);
	}

	public void showMapinfo(String info) {
		View popView = LayoutInflater.from(MessageDetailActivity.this).inflate(R.layout.layout_map_sos_window, null);
		TextView location = (TextView) popView.findViewById(R.id.map_sos_window_location);
		TextView time = (TextView) popView.findViewById(R.id.map_sos_window_time);
		ImageView animImg = (ImageView) popView.findViewById(R.id.map_sos_window_peopleimg);
		Animation anim = AnimationUtils.loadAnimation(MessageDetailActivity.this, R.anim.sosmap_centerscale_anim);
		animImg.startAnimation(anim);
		location.setText(info);
		time.setText(mMsgObject.getTime());
		InfoWindow window = new InfoWindow(popView, ptCenter, 0);
		mBaiduMap.showInfoWindow(window);
	}

}
