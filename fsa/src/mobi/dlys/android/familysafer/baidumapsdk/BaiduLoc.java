package mobi.dlys.android.familysafer.baidumapsdk;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class BaiduLoc implements OnGetGeoCoderResultListener {

	private LocationClient mLocationClient;
	private MyLocationListener mMyLocationListener;
	private BDLocation mLocation;
	private List<BaiduLocListener> mBaiduLocListenerList;
	private GeoCoder mSearch;
	private String mAddress;
	private String mAddress2;
	private List<PoiInfo> mPois = null;

	public BaiduLoc(Context context) {
		try {
			SDKInitializer.initialize(context);

			mLocationClient = new LocationClient(context);
			mMyLocationListener = new MyLocationListener();
			mLocationClient.registerLocationListener(mMyLocationListener);

			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Battery_Saving);
			option.setCoorType("bd09ll");
			option.setScanSpan(3000);
			option.setIsNeedAddress(true);
			mLocationClient.setLocOption(option);

			mSearch = GeoCoder.newInstance();
			mSearch.setOnGetGeoCodeResultListener(this);

			mBaiduLocListenerList = new ArrayList<BaiduLocListener>();

			startLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setLocationHigh() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(3000);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		stopLocation();
		startLocation();
	}

	public void setLocationLow() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);
		option.setCoorType("bd09ll");
		option.setScanSpan(120000);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		stopLocation();
		startLocation();
	}

	public void startLocation() {
		try {
			if (null != mLocationClient) {
				mLocationClient.start();
				mLocationClient.requestLocation();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopLocation() {
		try {
			if (null != mLocationClient) {
				mLocationClient.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAddress() {

		if (null != mAddress) {
			return mAddress;
		} else {
			return "";
		}

	}

	public String getAddress2() {

		if (null != mAddress2) {
			return mAddress2;
		} else {
			return "";
		}

	}

	public List<PoiInfo> getPois() {
		if (null != mPois) {
			return mPois;
		}
		return null;
	}

	public double getLatitude() {
		try {
			if (null != mLocation) {
				return mLocation.getLatitude();
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public double getLongitude() {
		try {
			if (null != mLocation) {
				return mLocation.getLongitude();
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getLng() {
		try {
			if (null != mLocation) {
				return new java.text.DecimalFormat("#.000000").format(mLocation.getLongitude());
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getLat() {
		try {
			if (null != mLocation) {
				return new java.text.DecimalFormat("#.000000").format(mLocation.getLatitude());
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void addBaiduLocListener(BaiduLocListener baiduLocListener) {

		if (null != mBaiduLocListenerList) {
			if (!mBaiduLocListenerList.contains(baiduLocListener)) {
				mBaiduLocListenerList.add(baiduLocListener);
			}
		}

	}

	public void removeBaiduLocListener(BaiduLocListener baiduLocListener) {

		if (null != mBaiduLocListenerList) {
			mBaiduLocListenerList.remove(baiduLocListener);
		}

	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			mLocation = location;

			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())));

		}

	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
			for (BaiduLocListener baiduLocListener : mBaiduLocListenerList) {
				baiduLocListener.onLocationChanged(false, null);
			}
			return;
		}

		if (null == arg0.getAddressDetail().province && null == arg0.getAddressDetail().city && null == arg0.getAddressDetail().district
				&& null == arg0.getAddressDetail().street && null == arg0.getAddressDetail().streetNumber) {
			for (BaiduLocListener baiduLocListener : mBaiduLocListenerList) {
				baiduLocListener.onLocationChanged(false, null);
			}
			return;
		}

		if (TextUtils.isEmpty(arg0.getAddressDetail().province) && TextUtils.isEmpty(arg0.getAddressDetail().city)
				&& TextUtils.isEmpty(arg0.getAddressDetail().district) && TextUtils.isEmpty(arg0.getAddressDetail().street)
				&& TextUtils.isEmpty(arg0.getAddressDetail().streetNumber)) {
			for (BaiduLocListener baiduLocListener : mBaiduLocListenerList) {
				baiduLocListener.onLocationChanged(false, null);
			}
			return;
		}

		mAddress = arg0.getAddressDetail().province;
		mAddress += arg0.getAddressDetail().city;
		mAddress += arg0.getAddressDetail().district;
		mAddress += arg0.getAddressDetail().street;
		mAddress += arg0.getAddressDetail().streetNumber;

		mAddress2 = arg0.getAddressDetail().city;
		mAddress2 += arg0.getAddressDetail().district;
		mAddress2 += arg0.getAddressDetail().street;
		mAddress2 += arg0.getAddressDetail().streetNumber;

		if (mAddress.endsWith("0") || mAddress.endsWith("1") || mAddress.endsWith("2") || mAddress.endsWith("3") || mAddress.endsWith("4")
				|| mAddress.endsWith("5") || mAddress.endsWith("6") || mAddress.endsWith("7") || mAddress.endsWith("8") || mAddress.endsWith("9")) {
			mAddress += "号";

		}

		if (mAddress2.endsWith("0") || mAddress2.endsWith("1") || mAddress2.endsWith("2") || mAddress2.endsWith("3") || mAddress2.endsWith("4")
				|| mAddress2.endsWith("5") || mAddress2.endsWith("6") || mAddress2.endsWith("7") || mAddress2.endsWith("8") || mAddress2.endsWith("9")) {
			mAddress2 += "号";

		}

		mPois = arg0.getPoiList();

		for (BaiduLocListener baiduLocListener : mBaiduLocListenerList) {
			baiduLocListener.onLocationChanged(true, arg0);
		}
	}

	/*
	 * 判断经纬度是否有效
	 */
	public static boolean isLocationValid(String lng, String lat) {
		if (isLngValid(lng) && isLatValid(lat)) {
			return true;
		}

		return false;
	}

	public static boolean isLngValid(String lng) {
		if (!TextUtils.isEmpty(lng) && !lng.equalsIgnoreCase("0.0") && !lng.equalsIgnoreCase("4.9E-324") && !lng.equalsIgnoreCase("0")
				&& !lng.equalsIgnoreCase(".000000") && !lng.equalsIgnoreCase("0.000000")) {
			return true;
		}

		return false;
	}

	public static boolean isLatValid(String lat) {
		if (!TextUtils.isEmpty(lat) && !lat.equalsIgnoreCase("0.0") && !lat.equalsIgnoreCase("4.9E-324") && !lat.equalsIgnoreCase("0")
				&& !lat.equalsIgnoreCase(".000000") && !lat.equalsIgnoreCase("0.000000")) {
			return true;
		}

		return false;
	}
}