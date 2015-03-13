package mobi.dlys.android.familysafer.ui.sos;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiResult;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapListener;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView.MyMarker;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ShowMapActivity extends BaseExActivity implements BaiduMapListener {

	BaiduMapView mBaiduMapView = null;

	private ImageView mImageMine = null;
	private ImageView mImageZoomin = null;
	private ImageView mImageZoomout = null;
	private float mZoomLevel = 16;

	private View mPopview = null;
	private ImageView imgUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showmap);

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
					YSToast.showToast(ShowMapActivity.this, R.string.toast_map_zoomout_max);
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
					YSToast.showToast(ShowMapActivity.this, R.string.toast_map_zoomin_min);
				}
				mImageZoomin.setEnabled(true);
				mBaiduMapView.setZoomLevel(mZoomLevel);
			}
		});

		mBaiduMapView = new BaiduMapView(this, R.id.img_showmap_map, true, true);
		mBaiduMapView.setListener(this);

		mPopview = LayoutInflater.from(this).inflate(R.layout.layout_avatatar_pop, mBaiduMapView.getLayout());
		imgUser = (ImageView) mPopview.findViewById(R.id.img_checkin_avatar);

		double lat = getIntent().getDoubleExtra("lat", 0.0);
		double lng = getIntent().getDoubleExtra("lng", 0.0);
		String avatar = getIntent().getStringExtra("avatar");
		
		mBaiduMapView.clearPoi();

		if (0.0 == lat || 0.0 == lng) {
			App app = (App) getApplication();
			BaiduLoc locater = app.getLocater();

			mBaiduMapView.setCenter(locater.getLatitude(), locater.getLongitude());

			if (null != avatar) {
				mBaiduMapView.setAvatar(0, locater.getLatitude(), locater.getLongitude(), imgUser, avatar, mPopview);
			}
		} else {
			mBaiduMapView.setCenter(lat, lng);

			if (null != avatar) {
				mBaiduMapView.setAvatar(0, lat, lng, imgUser, avatar, mPopview);
			}
		}
	}

	@Override
	public void onMapLoaded() {
	}

	@Override
	public void onSearched(PoiResult result) {
	}

	@Override
	public void onMarkerClick(MyMarker mymarker) {
	}

	@Override
	public void onSnapshotReady(Bitmap snapshot) {
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
	            YSToast.showToast(ShowMapActivity.this, R.string.toast_map_zoomout_max);
	        }		
	        else
	        {
	            if (mZoomLevel == 3) {
	            	mImageZoomin.setEnabled(true);
	                mImageZoomout.setEnabled(false);
	                YSToast.showToast(ShowMapActivity.this, R.string.toast_map_zoomin_min);
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
                YSToast.showToast(ShowMapActivity.this, R.string.toast_map_zoomout_max);
            }
            if (mZoomLevel == 3) {
                YSToast.showToast(ShowMapActivity.this, R.string.toast_map_zoomin_min);
            }
        }
	}

	@Override
	public void OnMapClick() {
	}
}
