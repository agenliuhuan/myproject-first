package mobi.dlys.android.familysafer.baidumapsdk;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.image.universalimageloader.core.process.BitmapProcessor;
import mobi.dlys.android.core.utils.ImageUtils;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

public class BaiduMapView implements OnGetPoiSearchResultListener, OnMapStatusChangeListener, OnMapClickListener {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;

	private MyMarker mMyself = null;

	private ArrayList<MyMarker> mMarkers = null;

	private BaiduMapListener mListener = null;

	private LinearLayout mLayout = null;

	private PoiSearch mPoiSearch = null;

	private Activity mAct = null;

	private View mPop = null;

	private int mPoiType = 0;

	public BaiduMapView(Activity myActivity, int resid, boolean canmove, boolean canzoom) {
		try {

			BaiduMapOptions bo = new BaiduMapOptions().compassEnabled(false).overlookingGesturesEnabled(false).rotateGesturesEnabled(false)
					.scaleControlEnabled(false).scrollGesturesEnabled(canmove).zoomControlsEnabled(false).zoomGesturesEnabled(canzoom);
			mMapView = new MapView(myActivity, bo);
			mLayout = (LinearLayout) myActivity.findViewById(resid);
			mLayout.addView(mMapView);

			mBaiduMap = mMapView.getMap();
			MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
			mBaiduMap.setMapStatus(msu);

			mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

				public void onMapLoaded() {
					if (null != mListener) {
						mListener.onMapLoaded();
					}
				}
			});
			mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

				public boolean onMarkerClick(Marker marker) {
					if (null != mMyself) {
						if (marker == mMyself.mMarker) {
							if (null != mListener) {
								mListener.onMarkerClick(mMyself);

							}
						}
					}
					int count = mMarkers.size();
					for (int i = 0; i < count; i++) {
						MyMarker mymarker = mMarkers.get(i);
						if (marker == mymarker.mMarker) {
							if (null != mListener) {
								mListener.onMarkerClick(mymarker);

							}
						}
					}

					return true;
				}
			});
			mBaiduMap.setOnMapClickListener(this);
			mBaiduMap.setOnMapStatusChangeListener(this);

			mPoiSearch = PoiSearch.newInstance();
			mPoiSearch.setOnGetPoiSearchResultListener(this);

			mMarkers = new ArrayList<MyMarker>();

			mAct = myActivity;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LinearLayout getLayout() {
		return mLayout;
	}

	// 地图缩放级别 3~19
	public void setZoomLevel(float level) {
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(level);
		mBaiduMap.setMapStatus(msu);
	}

	public void setCenter(double latitude, double longitude) {
		mBaiduMap = mMapView.getMap();
		LatLng location = new LatLng(latitude, longitude);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));
	}

	public void setCenter(int id, double latitude, double longitude) {
		mBaiduMap = mMapView.getMap();
		LatLng location = new LatLng(latitude, longitude);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));
		for (int i = 0; i < mMarkers.size(); i++) {
			MyMarker mm = mMarkers.get(i);
			if (null != mm) {
				if (mm.mTag == id) {
					if (null != mm.mMarker) {
						mm.mMarker.setZIndex(mMarkers.size());
					}
				} else {
					if (null != mm.mMarker) {
						mm.mMarker.setZIndex(i);
					}
				}
			}
		}
	}

	public void setCenterAndMove(int id, double latitude, double longitude) {
		mBaiduMap = mMapView.getMap();
		LatLng location = new LatLng(latitude, longitude);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));

		for (int i = 0; i < mMarkers.size(); i++) {
			MyMarker mm = mMarkers.get(i);
			if (null != mm) {
				if (mm.mTag == id) {
					if (null != mm.mMarker) {
						mm.mMarker.setZIndex(mMarkers.size());
						mm.mMarker.setPosition(location);
					}
				} else {
					if (null != mm.mMarker) {
						mm.mMarker.setZIndex(i);
					}
				}
			}
		}
	}

	public void setListener(BaiduMapListener listenter) {
		mListener = listenter;
	}

	public void clearPoi() {
		try {
			if (null != mBaiduMap) {
				mBaiduMap.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ImageView mDummy = null;
	private int radius = 0;

	public void setAvatar(int tag, double latitude, double longitude, ImageView imageView, String imagefile, View popview) {
		try {

			if (null != mBaiduMap) {
				mPop = popview;

				LatLng location = new LatLng(latitude, longitude);
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));

				mMyself = new MyMarker();
				mMyself.mName = "";
				mMyself.mTag = tag;

				mDummy = imageView;

				radius = (int) (mAct.getResources().getDimension(R.dimen.avatar_round_radius));

				if (!TextUtils.isEmpty(imagefile)) {
					AvatarProcessor post = new AvatarProcessor();
					ImageLoaderHelper.displayImage(imagefile, imageView, R.drawable.user, post, true, radius);
				} else {
					Bitmap bitmap = BitmapFactory.decodeResource(mAct.getResources(), R.drawable.user);
					final Bitmap output = convertViewToBitmapEx(bitmap);

					if (null != mMyself && null != mBaiduMap && null != mPop && null != mDummy && output != null) {
						mAct.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mMyself.mBitmap = BitmapDescriptorFactory.fromBitmap(output);
								OverlayOptions oop = new MarkerOptions().position(
										new LatLng(mBaiduMap.getMapStatus().target.latitude, mBaiduMap.getMapStatus().target.longitude)).icon(mMyself.mBitmap);
								mMyself.mMarker = (Marker) mBaiduMap.addOverlay(oop);
							}
						});
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAvatar(ImageView imageView, String imagefile, View popview) {
		try {

			if (null != mBaiduMap) {
				mPop = popview;

				mMyself = new MyMarker();
				mMyself.mName = "";

				mDummy = imageView;

				radius = (int) (mAct.getResources().getDimension(R.dimen.avatar_round_radius));

				if (!TextUtils.isEmpty(imagefile)) {
					MyPostprocessor post = new MyPostprocessor();
					ImageLoaderHelper.displayImage(imagefile, imageView, R.drawable.user, post, true, radius);
				} else {
					Bitmap roundBitmap = null;
					try {
						Bitmap bitmap = BitmapFactory.decodeResource(mAct.getResources(), R.drawable.user);
						// Bitmap bitmap2 = ImageUtils.getCircleBitmap(bitmap,
						// bitmap.getWidth());
						radius = bitmap.getWidth() / 2;
						roundBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
						Canvas canvas = new Canvas(roundBitmap);
						int color = 0xff424242;
						Paint paint = new Paint();
						Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
						RectF rectF = new RectF(rect);
						float roundPx = radius;
						paint.setAntiAlias(true);
						canvas.drawARGB(0, 0, 0, 0);
						paint.setColor(color);
						canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
						paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
						canvas.drawBitmap(bitmap, rect, rect, paint);
						ImageUtils.recycleBitmap(bitmap);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

					final Bitmap output = roundBitmap;

					if (null != mMyself && null != mBaiduMap && null != mPop && null != mDummy) {
						mAct.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mDummy.setImageBitmap(output);
								mMyself.mBitmap = BitmapDescriptorFactory.fromView(mPop);
								OverlayOptions oop = new MarkerOptions().position(
										new LatLng(mBaiduMap.getMapStatus().target.latitude, mBaiduMap.getMapStatus().target.longitude)).icon(mMyself.mBitmap);
								mMyself.mMarker = (Marker) mBaiduMap.addOverlay(oop);
							}
						});
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyPostprocessorX implements BitmapProcessor {

		double mLat;
		double mLng;
		String mTitle;
		String mDesc;

		public MyPostprocessorX(double a, double b, String title, String desc) {

			mLat = a;
			mLng = b;
			mTitle = title;
			mDesc = desc;
		}

		@Override
		public Bitmap process(Bitmap bitmap) {
			if (0 == mLat && 0 == mLng && TextUtils.isEmpty(mDesc)) {
				return bitmap;
			}
			int width = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_bk_w);
			int height = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_bk_h);
			try {
				// get logo bitmap
				Bitmap avatar = convertViewToBitmapEx(bitmap);
				// get backgroud drawable
				NinePatchDrawable dwBk = (NinePatchDrawable) mAct.getResources().getDrawable(R.drawable.img_pop_checkin);
				// create pop rect
				Rect popRect = new Rect();
				popRect.set(0, 0, width, height);
				// create logo rect
				Rect avaRect = new Rect();
				avaRect.set((width - avatar.getWidth()) / 2, height, avatar.getWidth(), avatar.getHeight() + height);
				// create main rect
				Rect cavRect = new Rect();
				cavRect.set(0, 0, popRect.right, avaRect.bottom);

				Bitmap bitmapFinal = Bitmap.createBitmap(cavRect.width(), cavRect.height(), Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmapFinal);
				canvas.drawARGB(0, 0, 0, 0);
				dwBk.setBounds(popRect);
				dwBk.draw(canvas);
				Paint avaPaint = new Paint();
				avaPaint.setAntiAlias(true);
				canvas.drawBitmap(avatar, avaRect.left, avaRect.top, avaPaint);

				if (!TextUtils.isEmpty(mTitle)) {
					// create title rect
					Rect titleRect = new Rect();
					int x1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_x);
					int y1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_y);
					int r1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_r);
					int b1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_b);
					int b2 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_desc_b);
					titleRect.set(x1, y1, r1, b1);

					Paint titlePaint = new Paint();
					titlePaint.setAntiAlias(true);
					titlePaint.setColor(Color.rgb(57, 57, 57));

					titlePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17, mAct.getResources().getDisplayMetrics()));
					titlePaint.setTextAlign(Align.LEFT);

					String title2 = mTitle;
					Rect testRect = new Rect();
					titlePaint.getTextBounds(title2, 0, title2.length(), testRect);
					boolean hasCut = false;
					while (testRect.width() > titleRect.width()) {
						title2 = title2.substring(0, title2.length() - 1);
						String test = title2 + "...";
						titlePaint.getTextBounds(test, 0, test.length(), testRect);
						hasCut = true;
					}
					if (hasCut) {
						title2 += "...";
					}
					FontMetricsInt fontMetrics = titlePaint.getFontMetricsInt();
					int baseline = titleRect.top + (titleRect.bottom - titleRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
					canvas.drawText(title2, titleRect.left, baseline, titlePaint);

					// create desc rect
					Rect descRect = new Rect();
					descRect.set(x1, b1, r1, b2);

					Paint descPaint = new Paint();
					descPaint.setAntiAlias(true);
					descPaint.setColor(Color.rgb(99, 99, 99));

					descPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, mAct.getResources().getDisplayMetrics()));
					descPaint.setTextAlign(Align.LEFT);

					String desc2 = mDesc;
					Rect test2Rect = new Rect();
					descPaint.getTextBounds(desc2, 0, desc2.length(), test2Rect);
					hasCut = false;
					while (test2Rect.width() > descRect.width()) {
						desc2 = desc2.substring(0, desc2.length() - 1);
						String test = desc2 + "...";
						descPaint.getTextBounds(test, 0, test.length(), test2Rect);
						hasCut = true;
					}
					if (hasCut) {
						desc2 += "...";
					}

					FontMetricsInt fontMetrics2 = descPaint.getFontMetricsInt();
					int baseline2 = descRect.top + (descRect.bottom - descRect.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
					canvas.drawText(desc2, descRect.left, baseline2, descPaint);
				} else {
					// create desc rect
					Rect descRect = new Rect();
					int x1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_x);
					int r1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_r);
					int b1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_desc_b);
					descRect.set(x1, 0, r1, b1);

					Paint descPaint = new Paint();
					descPaint.setAntiAlias(true);
					descPaint.setColor(Color.rgb(57, 57, 57));

					descPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, mAct.getResources().getDisplayMetrics()));
					descPaint.setTextAlign(Align.LEFT);

					String desc2 = mDesc;
					Rect test2Rect = new Rect();
					descPaint.getTextBounds(desc2, 0, desc2.length(), test2Rect);
					boolean hasCut = false;
					while (test2Rect.width() > descRect.width()) {
						desc2 = desc2.substring(0, desc2.length() - 1);
						String test = desc2 + "...";
						descPaint.getTextBounds(test, 0, test.length(), test2Rect);
						hasCut = true;
					}
					if (hasCut) {
						desc2 += "...";
					}

					FontMetricsInt fontMetrics2 = descPaint.getFontMetricsInt();
					int baseline2 = descRect.top + (descRect.bottom - descRect.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
					canvas.drawText(desc2, descRect.left, baseline2, descPaint);
				}
				if (bitmapFinal != null) {
					addPeople(-1, mLat, mLng, bitmapFinal);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}
	}

	public class MyPostprocessorX2 implements BitmapProcessor {

		double mLat;
		double mLng;
		String mTitle;
		String mDesc;

		public MyPostprocessorX2(double a, double b, String title, String desc) {

			mLat = a;
			mLng = b;
			mTitle = title;
			mDesc = desc;
		}

		@Override
		public Bitmap process(Bitmap bitmap) {
			if (0 == mLat && 0 == mLng && TextUtils.isEmpty(mDesc)) {
				return bitmap;
			}
			int width = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_bk_w);
			int height = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_bk_h);
			try {
				// get logo bitmap
				Bitmap avatar = convertViewToBitmapEx(bitmap);
				// get backgroud drawable
				NinePatchDrawable dwBk = (NinePatchDrawable) mAct.getResources().getDrawable(R.drawable.img_pop_viewcheckin);
				// create pop rect
				Rect popRect = new Rect();
				popRect.set(0, 0, width, height);
				// create logo rect
				Rect avaRect = new Rect();
				avaRect.set((width - avatar.getWidth()) / 2, height, avatar.getWidth(), avatar.getHeight() + height);
				// create main rect
				Rect cavRect = new Rect();
				cavRect.set(0, 0, popRect.right, avaRect.bottom);

				Bitmap bitmapFinal = Bitmap.createBitmap(cavRect.width(), cavRect.height(), Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmapFinal);
				canvas.drawARGB(0, 0, 0, 0);
				dwBk.setBounds(popRect);
				dwBk.draw(canvas);
				Paint avaPaint = new Paint();
				avaPaint.setAntiAlias(true);
				canvas.drawBitmap(avatar, avaRect.left, avaRect.top, avaPaint);

				if (!TextUtils.isEmpty(mTitle)) {
					// create title rect
					Rect titleRect = new Rect();
					int x1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_x);
					int y1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_y);
					int r1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_r);
					int b1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_b);
					int b2 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_desc_b);
					titleRect.set(x1, y1, r1, b1);

					Paint titlePaint = new Paint();
					titlePaint.setAntiAlias(true);
					titlePaint.setColor(Color.rgb(57, 57, 57));

					titlePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17, mAct.getResources().getDisplayMetrics()));
					titlePaint.setTextAlign(Align.LEFT);

					String title2 = mTitle;
					Rect testRect = new Rect();
					titlePaint.getTextBounds(title2, 0, title2.length(), testRect);
					boolean hasCut = false;
					while (testRect.width() > titleRect.width()) {
						title2 = title2.substring(0, title2.length() - 1);
						String test = title2 + "...";
						titlePaint.getTextBounds(test, 0, test.length(), testRect);
						hasCut = true;
					}
					if (hasCut) {
						title2 += "...";
					}
					FontMetricsInt fontMetrics = titlePaint.getFontMetricsInt();
					int baseline = titleRect.top + (titleRect.bottom - titleRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
					canvas.drawText(title2, titleRect.left, baseline, titlePaint);

					// create desc rect
					Rect descRect = new Rect();
					descRect.set(x1, b1, r1, b2);

					Paint descPaint = new Paint();
					descPaint.setAntiAlias(true);
					descPaint.setColor(Color.rgb(99, 99, 99));

					descPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, mAct.getResources().getDisplayMetrics()));
					descPaint.setTextAlign(Align.LEFT);

					String desc2 = mDesc;
					Rect test2Rect = new Rect();
					descPaint.getTextBounds(desc2, 0, desc2.length(), test2Rect);
					hasCut = false;
					while (test2Rect.width() > descRect.width()) {
						desc2 = desc2.substring(0, desc2.length() - 1);
						String test = desc2 + "...";
						descPaint.getTextBounds(test, 0, test.length(), test2Rect);
						hasCut = true;
					}
					if (hasCut) {
						desc2 += "...";
					}

					FontMetricsInt fontMetrics2 = descPaint.getFontMetricsInt();
					int baseline2 = descRect.top + (descRect.bottom - descRect.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
					canvas.drawText(desc2, descRect.left, baseline2, descPaint);
				} else {
					// create desc rect
					Rect descRect = new Rect();
					int x1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_x);
					int r1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_r);
					int b1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_desc_b);
					descRect.set(x1, 0, r1, b1);

					Paint descPaint = new Paint();
					descPaint.setAntiAlias(true);
					descPaint.setColor(Color.rgb(57, 57, 57));

					descPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, mAct.getResources().getDisplayMetrics()));
					descPaint.setTextAlign(Align.LEFT);

					String desc2 = mDesc;
					Rect test2Rect = new Rect();
					descPaint.getTextBounds(desc2, 0, desc2.length(), test2Rect);
					boolean hasCut = false;
					while (test2Rect.width() > descRect.width()) {
						desc2 = desc2.substring(0, desc2.length() - 1);
						String test = desc2 + "...";
						descPaint.getTextBounds(test, 0, test.length(), test2Rect);
						hasCut = true;
					}
					if (hasCut) {
						desc2 += "...";
					}

					FontMetricsInt fontMetrics2 = descPaint.getFontMetricsInt();
					int baseline2 = descRect.top + (descRect.bottom - descRect.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
					canvas.drawText(desc2, descRect.left, baseline2, descPaint);
				}
				if (bitmapFinal != null) {
					addPeople(-1, mLat, mLng, bitmapFinal);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}
	}

	public void setCenter(double latitude, double longitude, ImageView imageView, String imagefile, String title, String desc) {
		try {
			if (null != mBaiduMap) {
				LatLng location = new LatLng(latitude, longitude);
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));

				if (!TextUtils.isEmpty(imagefile)) {
					MyPostprocessorX post = new MyPostprocessorX(latitude, longitude, title, desc);
					ImageLoaderHelper.displayImage(imagefile, imageView, R.drawable.user, post, true);
				} else {
					try {
						int width2 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.layout_avatatat_pop_img_checkin_avatar_frame_layout_width);
						Bitmap normalBitmap = ImageUtils.getResBitmap(App.getInstance().getResources(), R.drawable.user, width2);

						int width = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_bk_w);
						int height = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_bk_h);

						// get logo bitmap
						Bitmap avatar = convertViewToBitmapEx(normalBitmap);
						// get backgroud drawable
						NinePatchDrawable dwBk = (NinePatchDrawable) mAct.getResources().getDrawable(R.drawable.img_pop_checkin);
						// create pop rect
						Rect popRect = new Rect();
						popRect.set(0, 0, width, height);
						// create logo rect
						Rect avaRect = new Rect();
						avaRect.set((width - avatar.getWidth()) / 2, height, avatar.getWidth(), avatar.getHeight() + height);
						// create main rect
						Rect cavRect = new Rect();
						cavRect.set(0, 0, popRect.right, avaRect.bottom);

						Bitmap bitmapFinal = Bitmap.createBitmap(cavRect.width(), cavRect.height(), Config.ARGB_8888);
						Canvas canvas = new Canvas(bitmapFinal);
						canvas.drawARGB(0, 0, 0, 0);
						dwBk.setBounds(popRect);
						dwBk.draw(canvas);
						Paint avaPaint = new Paint();
						avaPaint.setAntiAlias(true);
						canvas.drawBitmap(avatar, avaRect.left, avaRect.top, avaPaint);

						if (!TextUtils.isEmpty(title)) {
							// create title rect
							Rect titleRect = new Rect();
							int x1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_x);
							int y1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_y);
							int r1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_r);
							int b1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_b);
							int b2 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_desc_b);
							titleRect.set(x1, y1, r1, b1);

							Paint titlePaint = new Paint();
							titlePaint.setAntiAlias(true);
							titlePaint.setColor(Color.rgb(57, 57, 57));

							titlePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17, mAct.getResources().getDisplayMetrics()));
							titlePaint.setTextAlign(Align.LEFT);

							String title2 = title;
							Rect testRect = new Rect();
							titlePaint.getTextBounds(title2, 0, title2.length(), testRect);
							boolean hasCut = false;
							while (testRect.width() > titleRect.width()) {
								title2 = title2.substring(0, title2.length() - 1);
								String test = title2 + "...";
								titlePaint.getTextBounds(test, 0, test.length(), testRect);
								hasCut = true;
							}
							if (hasCut) {
								title2 += "...";
							}
							FontMetricsInt fontMetrics = titlePaint.getFontMetricsInt();
							int baseline = titleRect.top + (titleRect.bottom - titleRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
							canvas.drawText(title2, titleRect.left, baseline, titlePaint);

							// create desc rect
							Rect descRect = new Rect();
							descRect.set(x1, b1, r1, b2);

							Paint descPaint = new Paint();
							descPaint.setAntiAlias(true);
							descPaint.setColor(Color.rgb(99, 99, 99));

							descPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, mAct.getResources().getDisplayMetrics()));
							descPaint.setTextAlign(Align.LEFT);

							String desc2 = desc;
							Rect test2Rect = new Rect();
							descPaint.getTextBounds(desc2, 0, desc2.length(), test2Rect);
							hasCut = false;
							while (test2Rect.width() > descRect.width()) {
								desc2 = desc2.substring(0, desc2.length() - 1);
								String test = desc2 + "...";
								descPaint.getTextBounds(test, 0, test.length(), test2Rect);
								hasCut = true;
							}
							if (hasCut) {
								desc2 += "...";
							}

							FontMetricsInt fontMetrics2 = descPaint.getFontMetricsInt();
							int baseline2 = descRect.top + (descRect.bottom - descRect.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
							canvas.drawText(desc2, descRect.left, baseline2, descPaint);
						} else {
							// create desc rect
							Rect descRect = new Rect();
							int x1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_x);
							int r1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_r);
							int b1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_desc_b);
							descRect.set(x1, 0, r1, b1);

							Paint descPaint = new Paint();
							descPaint.setAntiAlias(true);
							descPaint.setColor(Color.rgb(57, 57, 57));

							descPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, mAct.getResources().getDisplayMetrics()));
							descPaint.setTextAlign(Align.LEFT);

							String desc2 = desc;
							Rect test2Rect = new Rect();
							descPaint.getTextBounds(desc2, 0, desc2.length(), test2Rect);
							boolean hasCut = false;
							while (test2Rect.width() > descRect.width()) {
								desc2 = desc2.substring(0, desc2.length() - 1);
								String test = desc2 + "...";
								descPaint.getTextBounds(test, 0, test.length(), test2Rect);
								hasCut = true;
							}
							if (hasCut) {
								desc2 += "...";
							}

							FontMetricsInt fontMetrics2 = descPaint.getFontMetricsInt();
							int baseline2 = descRect.top + (descRect.bottom - descRect.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
							canvas.drawText(desc2, descRect.left, baseline2, descPaint);
						}
						if (bitmapFinal != null) {
							addPeople(-1, latitude, longitude, bitmapFinal);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCenter2(double latitude, double longitude, ImageView imageView, String imagefile, String title, String desc) {
		try {
			if (null != mBaiduMap) {
				LatLng location = new LatLng(latitude, longitude);
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));

				if (!TextUtils.isEmpty(imagefile)) {
					MyPostprocessorX2 post = new MyPostprocessorX2(latitude, longitude, title, desc);
					ImageLoaderHelper.displayImage(imagefile, imageView, R.drawable.user, post, true);
				} else {
					try {
						int width2 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.layout_avatatat_pop_img_checkin_avatar_frame_layout_width);
						Bitmap normalBitmap = ImageUtils.getResBitmap(App.getInstance().getResources(), R.drawable.user, width2);

						int width = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_bk_w);
						int height = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_bk_h);

						// get logo bitmap
						Bitmap avatar = convertViewToBitmapEx(normalBitmap);
						// get backgroud drawable
						NinePatchDrawable dwBk = (NinePatchDrawable) mAct.getResources().getDrawable(R.drawable.img_pop_viewcheckin);
						// create pop rect
						Rect popRect = new Rect();
						popRect.set(0, 0, width, height);
						// create logo rect
						Rect avaRect = new Rect();
						avaRect.set((width - avatar.getWidth()) / 2, height, avatar.getWidth(), avatar.getHeight() + height);
						// create main rect
						Rect cavRect = new Rect();
						cavRect.set(0, 0, popRect.right, avaRect.bottom);

						Bitmap bitmapFinal = Bitmap.createBitmap(cavRect.width(), cavRect.height(), Config.ARGB_8888);
						Canvas canvas = new Canvas(bitmapFinal);
						canvas.drawARGB(0, 0, 0, 0);
						dwBk.setBounds(popRect);
						dwBk.draw(canvas);
						Paint avaPaint = new Paint();
						avaPaint.setAntiAlias(true);
						canvas.drawBitmap(avatar, avaRect.left, avaRect.top, avaPaint);

						if (!TextUtils.isEmpty(title)) {
							// create title rect
							Rect titleRect = new Rect();
							int x1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_x);
							int y1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_y);
							int r1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_r);
							int b1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_b);
							int b2 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_desc_b);
							titleRect.set(x1, y1, r1, b1);

							Paint titlePaint = new Paint();
							titlePaint.setAntiAlias(true);
							titlePaint.setColor(Color.rgb(57, 57, 57));

							titlePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 17, mAct.getResources().getDisplayMetrics()));
							titlePaint.setTextAlign(Align.LEFT);

							String title2 = title;
							Rect testRect = new Rect();
							titlePaint.getTextBounds(title2, 0, title2.length(), testRect);
							boolean hasCut = false;
							while (testRect.width() > titleRect.width()) {
								title2 = title2.substring(0, title2.length() - 1);
								String test = title2 + "...";
								titlePaint.getTextBounds(test, 0, test.length(), testRect);
								hasCut = true;
							}
							if (hasCut) {
								title2 += "...";
							}
							FontMetricsInt fontMetrics = titlePaint.getFontMetricsInt();
							int baseline = titleRect.top + (titleRect.bottom - titleRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
							canvas.drawText(title2, titleRect.left, baseline, titlePaint);

							// create desc rect
							Rect descRect = new Rect();
							descRect.set(x1, b1, r1, b2);

							Paint descPaint = new Paint();
							descPaint.setAntiAlias(true);
							descPaint.setColor(Color.rgb(99, 99, 99));

							descPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, mAct.getResources().getDisplayMetrics()));
							descPaint.setTextAlign(Align.LEFT);

							String desc2 = desc;
							Rect test2Rect = new Rect();
							descPaint.getTextBounds(desc2, 0, desc2.length(), test2Rect);
							hasCut = false;
							while (test2Rect.width() > descRect.width()) {
								desc2 = desc2.substring(0, desc2.length() - 1);
								String test = desc2 + "...";
								descPaint.getTextBounds(test, 0, test.length(), test2Rect);
								hasCut = true;
							}
							if (hasCut) {
								desc2 += "...";
							}

							FontMetricsInt fontMetrics2 = descPaint.getFontMetricsInt();
							int baseline2 = descRect.top + (descRect.bottom - descRect.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
							canvas.drawText(desc2, descRect.left, baseline2, descPaint);
						} else {
							// create desc rect
							Rect descRect = new Rect();
							int x1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_x);
							int r1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_title_r);
							int b1 = App.getInstance().getResources().getDimensionPixelSize(R.dimen.checkin_pop_desc_b);
							descRect.set(x1, 0, r1, b1);

							Paint descPaint = new Paint();
							descPaint.setAntiAlias(true);
							descPaint.setColor(Color.rgb(57, 57, 57));

							descPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, mAct.getResources().getDisplayMetrics()));
							descPaint.setTextAlign(Align.LEFT);

							String desc2 = desc;
							Rect test2Rect = new Rect();
							descPaint.getTextBounds(desc2, 0, desc2.length(), test2Rect);
							boolean hasCut = false;
							while (test2Rect.width() > descRect.width()) {
								desc2 = desc2.substring(0, desc2.length() - 1);
								String test = desc2 + "...";
								descPaint.getTextBounds(test, 0, test.length(), test2Rect);
								hasCut = true;
							}
							if (hasCut) {
								desc2 += "...";
							}

							FontMetricsInt fontMetrics2 = descPaint.getFontMetricsInt();
							int baseline2 = descRect.top + (descRect.bottom - descRect.top - fontMetrics2.bottom + fontMetrics2.top) / 2 - fontMetrics2.top;
							canvas.drawText(desc2, descRect.left, baseline2, descPaint);
						}
						if (bitmapFinal != null) {
							addPeople(-1, latitude, longitude, bitmapFinal);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCenter(double latitude, double longitude, ImageView imageView, String imagefile, View popview) {
		try {

			if (null != mBaiduMap) {
				mPop = popview;

				LatLng location = new LatLng(latitude, longitude);
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));

				mMyself = new MyMarker();
				mMyself.mName = "";

				mDummy = imageView;

				radius = (int) (mAct.getResources().getDimension(R.dimen.avatar_round_radius));

				if (!TextUtils.isEmpty(imagefile)) {
					MyPostprocessor post = new MyPostprocessor();
					ImageLoaderHelper.displayImage(imagefile, imageView, R.drawable.user, post, true, radius);
				} else {
					Bitmap roundBitmap = null;
					try {
						Bitmap bitmap = BitmapFactory.decodeResource(mAct.getResources(), R.drawable.user);
						// Bitmap bitmap2 = ImageUtils.getCircleBitmap(bitmap,
						// bitmap.getWidth());
						radius = bitmap.getWidth() / 2;
						roundBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
						Canvas canvas = new Canvas(roundBitmap);
						int color = 0xff424242;
						Paint paint = new Paint();
						Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
						RectF rectF = new RectF(rect);
						float roundPx = radius;
						paint.setAntiAlias(true);
						canvas.drawARGB(0, 0, 0, 0);
						paint.setColor(color);
						canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
						paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
						canvas.drawBitmap(bitmap, rect, rect, paint);
						ImageUtils.recycleBitmap(bitmap);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

					final Bitmap output = roundBitmap;

					if (null != mMyself && null != mBaiduMap && null != mPop && null != mDummy) {
						mAct.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mDummy.setImageBitmap(output);
								mMyself.mBitmap = BitmapDescriptorFactory.fromView(mPop);
								OverlayOptions oop = new MarkerOptions().position(
										new LatLng(mBaiduMap.getMapStatus().target.latitude, mBaiduMap.getMapStatus().target.longitude)).icon(mMyself.mBitmap);
								mMyself.mMarker = (Marker) mBaiduMap.addOverlay(oop);
							}
						});
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCenter(double latitude, double longitude, Bitmap bitmap) {
		try {
			if (null != mBaiduMap && null != bitmap) {
				LatLng location = new LatLng(latitude, longitude);
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));

				mMyself = new MyMarker();
				mMyself.mName = "";
				mMyself.mBitmap = BitmapDescriptorFactory.fromBitmap(bitmap);

				OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(mMyself.mBitmap);
				mMyself.mMarker = (Marker) mBaiduMap.addOverlay(oop);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCenter(double latitude, double longitude, View popview) {
		try {
			if (null != mBaiduMap && null != popview) {
				LatLng location = new LatLng(latitude, longitude);
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));

				mMyself = new MyMarker();
				mMyself.mName = "";
				mMyself.mBitmap = BitmapDescriptorFactory.fromView(popview);

				OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(mMyself.mBitmap);
				mMyself.mMarker = (Marker) mBaiduMap.addOverlay(oop);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPeople(int id, double latitude, double longitude, int imageres, String lasttime) {
		try {
			int width = App.getInstance().getResources().getDimensionPixelSize(R.dimen.layout_avatatat_pop_img_checkin_avatar_frame_layout_width);
			Bitmap normalBitmap = ImageUtils.getResBitmap(App.getInstance().getResources(), imageres, width);
			Bitmap tempBitmap = convertViewToBitmapEx(normalBitmap);

			if (null != mBaiduMap) {
				MyMarker newMarker = new MyMarker();
				newMarker.mName = "";
				if (!TextUtils.isEmpty(lasttime)) {
					NinePatchDrawable dwBk = (NinePatchDrawable) mAct.getResources().getDrawable(R.drawable.img_family_location_pop);
					Paint stringPaint = new Paint();
					Rect stringRect = new Rect();

					stringPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, mAct.getResources().getDisplayMetrics()));
					stringPaint.setColor(Color.WHITE);
					stringPaint.setTextAlign(Align.CENTER);
					stringPaint.getTextBounds(lasttime, 0, lasttime.length(), stringRect);
					stringRect.bottom += Math.abs(stringRect.top);
					stringRect.right += Math.abs(stringRect.left);
					stringRect.bottom *= 2;
					stringRect.right *= 2;
					stringRect.top = 0;
					stringRect.left = 0;

					FontMetricsInt fontMetrics = stringPaint.getFontMetricsInt();
					int baseline = stringRect.top + (stringRect.bottom - stringRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

					dwBk.setBounds(stringRect);

					Rect popRect = new Rect();
					popRect.set(stringRect.left, stringRect.top, stringRect.right, stringRect.bottom);
					if (popRect.width() < tempBitmap.getWidth()) {
						popRect.right = tempBitmap.getWidth();
					}
					popRect.bottom = popRect.height() + tempBitmap.getHeight();
					Bitmap bitmapFinal = Bitmap.createBitmap(popRect.width(), popRect.height(), Config.ARGB_8888);
					Canvas canvas = new Canvas(bitmapFinal);
					stringPaint.setAntiAlias(true);

					canvas.drawARGB(0, 0, 0, 0);
					dwBk.draw(canvas);
					canvas.drawText(lasttime, stringRect.centerX(), baseline, stringPaint);
					canvas.drawBitmap(tempBitmap, (popRect.width() - tempBitmap.getWidth()) / 2, stringRect.bottom, stringPaint);
					if (bitmapFinal != null) {
						newMarker.mBitmap = BitmapDescriptorFactory.fromBitmap(bitmapFinal);
					}
				} else {
					newMarker.mBitmap = BitmapDescriptorFactory.fromBitmap(tempBitmap);
				}
				newMarker.mTag = id;

				OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(newMarker.mBitmap);
				newMarker.mMarker = (Marker) mBaiduMap.addOverlay(oop);

				if (null != mMarkers) {
					mMarkers.add(newMarker);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPeople(int id, double latitude, double longitude, View popview) {
		try {
			if (null != mBaiduMap) {
				MyMarker newMarker = new MyMarker();
				newMarker.mName = "";
				newMarker.mBitmap = BitmapDescriptorFactory.fromView(popview);
				newMarker.mTag = id;

				OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(newMarker.mBitmap);
				newMarker.mMarker = (Marker) mBaiduMap.addOverlay(oop);

				if (null != mMarkers) {
					mMarkers.add(newMarker);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPeople(int id, double latitude, double longitude, Bitmap bitmap) {
		try {
			if (null != mBaiduMap) {
				MyMarker newMarker = new MyMarker();
				newMarker.mName = "";
				newMarker.mBitmap = BitmapDescriptorFactory.fromBitmap(bitmap);
				newMarker.mTag = id;

				OverlayOptions oop = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(newMarker.mBitmap);
				newMarker.mMarker = (Marker) mBaiduMap.addOverlay(oop);

				if (null != mMarkers) {
					mMarkers.add(newMarker);
				}
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searchPolicestation(double latitude, double longitude) {
		try {
			PoiNearbySearchOption opt = new PoiNearbySearchOption();
			opt.location(new LatLng(latitude, longitude));
			opt.keyword("派出所");
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
			opt.keyword("医院");
			opt.radius(2000);
			opt.pageCapacity(100);

			mPoiType = 1;
			mPoiSearch.searchNearby(opt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPoi(PoiInfo info) {
		try {
			if (null != mBaiduMap) {
				MyMarker newMarker = new MyMarker();
				newMarker.mName = info.name;
				newMarker.mCity = info.city;
				newMarker.mAddr = info.address;
				newMarker.mPhone = info.phoneNum;
				if (0 == mPoiType) {
					newMarker.mBitmap = BitmapDescriptorFactory.fromResource(R.drawable.img_policestation);
					newMarker.mTag = 0;
				} else if (1 == mPoiType) {
					newMarker.mBitmap = BitmapDescriptorFactory.fromResource(R.drawable.img_hospital);
					newMarker.mTag = 1;
				}
				OverlayOptions oop = new MarkerOptions().position(info.location).icon(newMarker.mBitmap);
				newMarker.mMarker = (Marker) mBaiduMap.addOverlay(oop);

				if (null != mMarkers) {
					mMarkers.add(newMarker);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void snapshot() {
		try {
			if (null != mBaiduMap) {
				mBaiduMap.snapshot(new SnapshotReadyCallback() {

					public void onSnapshotReady(Bitmap snapshot) {
						if (null != mListener) {
							mListener.onSnapshotReady(snapshot);
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onPause() {
		if (null != mMapView) {
			mMapView.onPause();
		}
	}

	public void onResume() {
		if (null != mMapView) {
			mMapView.onResume();
		}
	}

	public void onDestroy() {
		if (null != mPoiSearch) {
			mPoiSearch.destroy();
		}
		if (null != mBaiduMap) {
			mBaiduMap.clear();
		}

		if (null != mMyself) {
			if (null != mMyself.mBitmap) {
				mMyself.mBitmap.recycle();
			}
		}
		if (null != mMarkers) {
			int count = mMarkers.size();
			for (int i = 0; i < count; i++) {
				MyMarker tmp = mMarkers.get(i);
				if (null != tmp) {
					if (null != tmp.mBitmap) {
						tmp.mBitmap.recycle();
					}
				}
			}
			mMarkers.clear();
		}

	}

	public class MyMarker {
		public Marker mMarker;
		public BitmapDescriptor mBitmap;
		public String mName;
		public String mCity;
		public String mAddr;
		public String mPhone;
		public int mTag;
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

		if (result.error != SearchResult.ERRORNO.NO_ERROR) {

		} else {

		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			if (null != mListener) {
				mListener.onSearched(result);
			}
			return;
		}

		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			List<PoiInfo> infos = result.getAllPoi();
			int count = infos.size();

			for (int i = 0; i < count; i++) {
				PoiInfo info = infos.get(i);

				addPoi(info);

			}
			if (null != mListener) {
				mListener.onSearched(result);
			}
			return;
		}

	}

	public class MyPostprocessor implements BitmapProcessor {

		@Override
		public Bitmap process(Bitmap bitmap) {
			try {
				final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
				Bitmap bitmap2 = ImageUtils.getCircleBitmap(bitmap, bitmap.getWidth());
				radius = bitmap2.getWidth() / 2;
				Canvas canvas = new Canvas(output);
				final int color = 0xff424242;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
				final RectF rectF = new RectF(rect);
				final float roundPx = radius;
				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bitmap2, rect, rect, paint);
				ImageUtils.recycleBitmap(bitmap2);

				if (null != mMyself && null != mBaiduMap && null != mPop && null != mDummy) {
					mAct.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mDummy.setImageBitmap(output);
							mMyself.mBitmap = BitmapDescriptorFactory.fromView(mPop);
							OverlayOptions oop = new MarkerOptions().position(
									new LatLng(mBaiduMap.getMapStatus().target.latitude, mBaiduMap.getMapStatus().target.longitude)).icon(mMyself.mBitmap);
							mMyself.mMarker = (Marker) mBaiduMap.addOverlay(oop);
						}
					});
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

	}

	public class AvatarProcessor implements BitmapProcessor {

		@Override
		public Bitmap process(Bitmap bitmap) {
			try {
				final Bitmap output = convertViewToBitmapEx(bitmap);

				if (null != mMyself && null != mBaiduMap && null != mPop && null != mDummy) {
					mAct.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (output == null) {
								mDummy.setImageBitmap(output);
								mMyself.mBitmap = BitmapDescriptorFactory.fromView(mPop);
							} else {
								mMyself.mBitmap = BitmapDescriptorFactory.fromBitmap(output);
							}
							OverlayOptions oop = new MarkerOptions().position(
									new LatLng(mBaiduMap.getMapStatus().target.latitude, mBaiduMap.getMapStatus().target.longitude)).icon(mMyself.mBitmap);
							mMyself.mMarker = (Marker) mBaiduMap.addOverlay(oop);
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

	}

	public Bitmap convertViewToBitmapEx(Bitmap bitmap) {
		Bitmap roundBitmap = null;

		int width = App.getInstance().getResources().getDimensionPixelSize(R.dimen.layout_avatatat_pop_img_checkin_avatar_frame_layout_width);
		int height = App.getInstance().getResources().getDimensionPixelSize(R.dimen.layout_avatatat_pop_img_checkin_avatar_frame_layout_height);
		int padding = 2;
		Bitmap destBitmap = null;
		final Paint paint = new Paint();
		try {
			// 把view中的内容绘制在画布上
			Bitmap frameBitmap = ImageUtils.getResBitmap(App.getInstance().getResources(), R.drawable.avatar, width);
			if (frameBitmap != null) {
				roundBitmap = ImageUtils.getCircleBitmap(bitmap, frameBitmap.getWidth() - padding);
				destBitmap = Bitmap.createBitmap(frameBitmap.getWidth(), frameBitmap.getHeight(), Bitmap.Config.ARGB_8888);
				// 利用bitmap生成画布
				Canvas canvas = new Canvas(destBitmap);
				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);

				canvas.drawBitmap(roundBitmap, padding, padding, paint);
				canvas.drawBitmap(frameBitmap, 0, 0, paint);

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

	@Override
	public void onMapStatusChange(MapStatus arg0) {
	}

	@Override
	public void onMapStatusChangeFinish(MapStatus arg0) {
		if (null != mListener) {
			mListener.OnMapChanged(arg0);
		}
	}

	@Override
	public void onMapStatusChangeStart(MapStatus arg0) {

	}

	@Override
	public void onMapClick(LatLng arg0) {
		if (null != mListener) {
			mListener.OnMapClick();
		}
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}
}