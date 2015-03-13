package cn.changl.safe360.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class ReportLocationService extends Service implements BDLocationListener {
    public static final String ReportInterval = "reportInterval";
    public static final int DefaultInterval = 120 * 1000;
    public static final int AlarmInterval = 120 * 1000;

    public int mReportInterval = DefaultInterval;

    public LocationClient mLocationClient;

    public static void startService(Context context, int interval) {
        Intent intent = new Intent(context, ReportLocationService.class);
        intent.putExtra(ReportInterval, interval);
        context.startService(intent);
    }

    private void startLocation() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this.getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
            option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
            option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
            option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
            option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
            mLocationClient.setLocOption(option);
            mLocationClient.registerLocationListener(this);
            if (mLocationClient != null && mLocationClient.isStarted()) {
                mLocationClient.start();
                mLocationClient.requestLocation();
            } else {
                Log.d("LocSDK5", "locClient is null or not started");
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mReportInterval = intent.getIntExtra(ReportInterval, DefaultInterval);
        }

        startLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        
        try {
            if (null != mLocationClient) {
                mLocationClient.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onReceiveLocation(BDLocation arg0) {

    }
}
