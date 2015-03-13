package cn.changl.safe360.android.map;

import mobi.dlys.android.core.image.universalimageloader.core.process.BitmapProcessor;
import mobi.dlys.android.core.utils.ImageUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.api.PPNetManager;
import cn.changl.safe360.android.utils.ImageLoaderHelper;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public class BaiduMapHelper {
	Activity activity;
	BaiduMap mBaiduMap;

	public BaiduMapHelper(Activity activity, BaiduMap mBaiduMap) {
		this.activity = activity;
		this.mBaiduMap = mBaiduMap;
	}

	public static void addPeopleInfo(Context context, final double latitude, final double longitude, final BaiduMap baidumap) {
		View popView = LayoutInflater.from(context).inflate(R.layout.layout_map_peopleinfo_window, null);
		final RelativeLayout infoRL = (RelativeLayout) popView.findViewById(R.id.map_peopleinfo_window_infoRL);
		RelativeLayout peopleRL = (RelativeLayout) popView.findViewById(R.id.map_peopleinfo_window_peopleRL);
		Button refreshBtn = (Button) popView.findViewById(R.id.map_peopleinfo_window_refreshBtn);
		TextView location = (TextView) popView.findViewById(R.id.map_peopleinfo_window_location);
		TextView time = (TextView) popView.findViewById(R.id.map_peopleinfo_window_time);
		ImageView popImg = (ImageView) popView.findViewById(R.id.map_peopleinfo_window_peopleimg);
		infoRL.setVisibility(View.GONE);
		peopleRL.setVisibility(View.VISIBLE);
		peopleRL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LatLng latlng = new LatLng(latitude, longitude);
				baidumap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
				infoRL.setVisibility(View.VISIBLE);
			}
		});
		InfoWindow window = new InfoWindow(popView, new LatLng(latitude, longitude), 1);
		baidumap.showInfoWindow(window);
	}

	public void addMarker(int userid, String imgUrl, double latitude, double longitude) {
		if (!TextUtils.isEmpty(imgUrl) && !TextUtils.isEmpty(imgUrl.replace(PPNetManager.HTTP + PPNetManager.IMAGE_DOMAIN, ""))) {
			AvatarProcessor process = new AvatarProcessor(userid);
			View popView = LayoutInflater.from(activity).inflate(R.layout.layout_map_people_window, null);
			ImageView userimg = (ImageView) popView.findViewById(R.id.people_window_img);
			ImageLoaderHelper.displayImage(imgUrl, userimg, R.drawable.icon_family, process, true, 90);
		} else {
			View popView = LayoutInflater.from(activity).inflate(R.layout.layout_map_people_window, null);
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(popView);
			OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(bitmap);
			Marker marker = (Marker) mBaiduMap.addOverlay(oop);
			Bundle bundle = new Bundle();
			bundle.putInt("userid", userid);
			marker.setExtraInfo(bundle);
		}
	}

	public class AvatarProcessor implements BitmapProcessor {
		int userid;

		public AvatarProcessor(int userid) {
			this.userid = userid;

		}

		public Bitmap process(Bitmap bitmap) {
			try {
				final Bitmap output = convertViewToBitmapEx(bitmap);
				activity.runOnUiThread(new Runnable() {
					public void run() {
						BitmapDescriptor viewbitmap;
						if (output == null) {
							View popView = LayoutInflater.from(activity).inflate(R.layout.layout_map_people_window, null);
							viewbitmap = BitmapDescriptorFactory.fromView(popView);
						} else {
							viewbitmap = BitmapDescriptorFactory.fromBitmap(output);
						}
						LatLng latlng = new LatLng(mBaiduMap.getMapStatus().target.latitude, mBaiduMap.getMapStatus().target.longitude);
						OverlayOptions oop = new MarkerOptions().position(latlng).icon(viewbitmap).visible(true);
						Marker marker = (Marker) mBaiduMap.addOverlay(oop);
						Bundle bundle = new Bundle();
						bundle.putInt("userid", userid);
						marker.setExtraInfo(bundle);
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}
	}

	public Bitmap convertViewToBitmapEx(Bitmap bitmap) {
		Bitmap roundBitmap = null;

		int width = 80;
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

}
