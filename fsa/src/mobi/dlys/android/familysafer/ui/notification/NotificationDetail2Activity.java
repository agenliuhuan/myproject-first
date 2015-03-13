package mobi.dlys.android.familysafer.ui.notification;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapListener;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView.MyMarker;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.communication.CommunicationActivity;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiResult;

public class NotificationDetail2Activity extends BaseExActivity implements BaiduMapListener {
    protected TitleBarHolder mTitleBar;
    BaiduMapView mBaiduMapView = null;
    EventObjectEx notification;

    private View mPopview = null;
    private LinearLayout mLayoutTip = null;
    private LinearLayout mLayoutTip2 = null;
    private TextView tvName = null;
    private TextView tvName2 = null;
    private TextView tvLoc = null;
    private ImageView imgUser = null;

    private ImageView imgAvatar = null;
    private View mAvatar = null;

    private ImageView mImageMine = null;
    private ImageView mImageZoomin = null;
    private ImageView mImageZoomout = null;
    private float mZoomLevel = 16;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationdetail_2);

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
                    YSToast.showToast(NotificationDetail2Activity.this, R.string.toast_map_zoomout_max);
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
                    YSToast.showToast(NotificationDetail2Activity.this, R.string.toast_map_zoomin_min);
                }
                mImageZoomin.setEnabled(true);
                mBaiduMapView.setZoomLevel(mZoomLevel);
            }
        });
        mTitleBar = new TitleBarHolder(NotificationDetail2Activity.this);
        mTitleBar.mTitle.setText(R.string.activity_notificationdetail_2_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                NotificationDetail2Activity.this.finish();
            }
        });
        mTitleBar.mRight.setVisibility(View.INVISIBLE);

        mBaiduMapView = new BaiduMapView(this, R.id.img_notificationdetail_2_map, true, true);
        mBaiduMapView.setListener(this);

        if (null != getIntent()) {
            notification = (EventObjectEx) getIntent().getSerializableExtra(NotificationDetail1Activity.EXTRA_EVENT_OBJECT);
        }

        mAvatar = LayoutInflater.from(this).inflate(R.layout.layout_avatatar_pop2, mBaiduMapView.getLayout());
        mLayoutTip = (LinearLayout) mAvatar.findViewById(R.id.layout_location);
        mLayoutTip2 = (LinearLayout) mAvatar.findViewById(R.id.layout_location2);
        tvName = (TextView) mAvatar.findViewById(R.id.tv_checkin_name);
        tvName2 = (TextView) mAvatar.findViewById(R.id.tv_checkin_name2);
        tvLoc = (TextView) mAvatar.findViewById(R.id.tv_checkin_location);
        mLayoutTip.setVisibility(View.GONE);
        mLayoutTip2.setVisibility(View.VISIBLE);

        imgAvatar = (ImageView) mAvatar.findViewById(R.id.img_checkin_avatar);

        Button callBtn = (Button) findViewById(R.id.btn_common_call);
        callBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                int userid = notification.getUserId();
                FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(userid);
                if (null != fo) {
                    TelephonyUtils.call(NotificationDetail2Activity.this, fo.getPhone());
                }
            }
        });
        Button enterBtn = (Button) findViewById(R.id.btn_common_entercom);
        enterBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(NotificationDetail2Activity.this, CommunicationActivity.class);
                int userid = notification.getUserId();
                intent.putExtra("userid", userid);
                intent.putExtra("nickname", notification.getNickname());
                intent.putExtra("avatar", notification.getImage());
                startActivity(intent);
            }
        });
        if (notification.getType() == 2) {
            enterBtn.setEnabled(false);
            callBtn.setEnabled(false);
        }
    }

    private void initData() {
        if (null != notification) {
            String addrText = notification.getLocation();
            if (addrText.contains("|")) {
                String[] addr = addrText.split("\\|");
                // addrText = addrText.replace("|", "");
                tvLoc.setText(addr[0]);
                String adr = "";
                FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(notification.getUserId());
                if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                    adr = fo.getRemarkName() + getString(R.string.activity_notificationdetail_2_tv_arrived) + addr[0];
                    // tvName.setText();

                } else {
                    adr = notification.getNickname() + getString(R.string.activity_notificationdetail_2_tv_arrived) + addr[0];
                    // tvName.setText();
                }

                double lat = notification.getContent().getLat2();
                double lon = notification.getContent().getLng2();
                String img = notification.getImage();
                if (0.0 != lat && 0.0 != lon && 4.9E-324 != lat && 4.9E-324 != lon) {
                    mBaiduMapView.setCenter2((lat), (lon), imgAvatar, img, adr, addr[1]);
                }
            } else {
                tvLoc.setText(addrText);
                FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(notification.getUserId());
                if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                    tvName2.setText(fo.getRemarkName() + getString(R.string.activity_notificationdetail_2_tv_arrived) + addrText);

                } else {
                    tvName2.setText(notification.getNickname() + getString(R.string.activity_notificationdetail_2_tv_arrived) + addrText);
                }

                double lat = notification.getContent().getLat2();
                double lon = notification.getContent().getLng2();
                String img = notification.getImage();
                if (0.0 != lat && 0.0 != lon && 4.9E-324 != lat && 4.9E-324 != lon) {
                    mBaiduMapView.setCenter2((lat), (lon), imgAvatar, img, "", addrText);
                }
            }

        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onMarkerClick(MyMarker mymarker) {

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
        mBaiduMapView.onDestroy();
        super.onDestroy();
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
	            YSToast.showToast(NotificationDetail2Activity.this, R.string.toast_map_zoomout_max);
	        }		
	        else
	        {
	            if (mZoomLevel == 3) {
	            	mImageZoomin.setEnabled(true);
	                mImageZoomout.setEnabled(false);
	                YSToast.showToast(NotificationDetail2Activity.this, R.string.toast_map_zoomin_min);
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
                YSToast.showToast(NotificationDetail2Activity.this, R.string.toast_map_zoomout_max);
            }
            if (mZoomLevel == 3) {
                YSToast.showToast(NotificationDetail2Activity.this, R.string.toast_map_zoomin_min);
            }
        }
	}

    @Override
    public void OnMapClick() {
    }
}