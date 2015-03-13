package mobi.dlys.android.familysafer.ui.location;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.image.universalimageloader.core.process.BitmapProcessor;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapListener;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView.MyMarker;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.communication.CommunicationActivity;
import mobi.dlys.android.familysafer.ui.family.AddFamily2Activity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.MyAnimationUtils;
import mobi.dlys.android.familysafer.utils.TelephonyUtils;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiResult;

public class FamilyLocateActivity extends BaseExActivity implements BaiduMapListener {
	protected TitleBarHolder mTitleBar;
	BaiduMapView mBaiduMapView = null;

	LinearLayout mPopFamilyLocation = null;
	LinearLayout mPopFamilyLocation2 = null;

	RelativeLayout mPopFamilies = null;

	TextView mName = null;
	TextView mLocation = null;
	TextView mLasttime = null;

	Button mCallButton = null;
	Button mEnterButton = null;
	Button mCancel = null;

	private LinearLayout mListviewFamilies = null;
	ArrayList<FriendObject> mFamiliesList = null;

	private int mListIndex = -1;
	private int mListIndex2 = -1;

	private ImageView mImageMine = null;
	private ImageView mImageZoomin = null;
	private ImageView mImageZoomout = null;
	private float mZoomLevel = 16;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != mPopFamilyLocation) {
				if (mPopFamilyLocation.isShown()) {
					if (null != mPopFamilyLocation) {
						MyAnimationUtils.hideBottomView(mPopFamilyLocation, mPopFamilyLocation2, FamilyLocateActivity.this);
					}
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_familylocate);

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
					YSToast.showToast(FamilyLocateActivity.this, R.string.toast_map_zoomout_max);
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
					YSToast.showToast(FamilyLocateActivity.this, R.string.toast_map_zoomin_min);
				}
				mImageZoomin.setEnabled(true);
				mBaiduMapView.setZoomLevel(mZoomLevel);
			}
		});

		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_familylocate_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		mBaiduMapView = new BaiduMapView(this, R.id.layout_familylocate, true, true);
		mBaiduMapView.setListener(this);

		mPopFamilyLocation = (LinearLayout) findViewById(R.id.layout_familylocate_pop);
		mPopFamilyLocation2 = (LinearLayout) findViewById(R.id.layout_familylocate_pop2);
		mPopFamilies = (RelativeLayout) findViewById(R.id.layout_familylocate_families);

		mName = (TextView) findViewById(R.id.tv_familylocate_name);
		mLocation = (TextView) findViewById(R.id.tv_familylocate_location);
		mLasttime = (TextView) findViewById(R.id.tv_lasttime);

		mCallButton = (Button) findViewById(R.id.btn_common_call);
		mEnterButton = (Button) findViewById(R.id.btn_common_entercom);
		mCancel = (Button) findViewById(R.id.btn_familylocate_cancel);

		mPopFamilyLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mPopFamilyLocation) {
					if (View.VISIBLE == mPopFamilyLocation.getVisibility() && View.VISIBLE == mPopFamilyLocation2.getVisibility()) {
						MyAnimationUtils.hideBottomView(mPopFamilyLocation, mPopFamilyLocation2, FamilyLocateActivity.this);
					}
				}
			}
		});

		mPopFamilyLocation2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

		mCallButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						AnalyticsHelper.onEvent(FamilyLocateActivity.this, AnalyticsHelper.index_call_family2);
					}
				}, 1000);

				if (null != mPopFamilyLocation) {
					MyAnimationUtils.hideBottomView(mPopFamilyLocation, mPopFamilyLocation2, FamilyLocateActivity.this);
				}
				if (0 == mListIndex) {
					if (CoreModel.getInstance().getUserInfo() != null) {
						TelephonyUtils.call(FamilyLocateActivity.this, CoreModel.getInstance().getUserInfo().getPhone());
					}
				} else {
					FriendObject fo = mFamiliesList.get(mListIndex);
					if (null != fo) {
						TelephonyUtils.call(FamilyLocateActivity.this, fo.getPhone());
					}
				}
			}
		});
		mEnterButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						AnalyticsHelper.onEvent(FamilyLocateActivity.this, AnalyticsHelper.index_chat_family);
					}
				}, 1000);

				if (null != mPopFamilyLocation) {
					MyAnimationUtils.hideBottomView(mPopFamilyLocation, mPopFamilyLocation2, FamilyLocateActivity.this);
				}
				if (mListIndex2 > 0) {
					FriendObject fo = mFamiliesList.get(mListIndex2);
					if (null != fo) {
						Intent intent = new Intent();
						intent.setClass(FamilyLocateActivity.this, CommunicationActivity.class);
						int userid = fo.getUserId();
						intent.putExtra("userid", userid);
						if (!TextUtils.isEmpty(fo.getRemarkName())) {
							intent.putExtra("nickname", fo.getRemarkName());
						} else if (!TextUtils.isEmpty(fo.getNickname())) {
							intent.putExtra("nickname", fo.getNickname());
						}
						intent.putExtra("avatar", fo.getImage());
						startActivity(intent);
						mListIndex2 = 0;
					}
				} else if (mListIndex > 0) {
					FriendObject fo = mFamiliesList.get(mListIndex);
					if (null != fo) {
						Intent intent = new Intent();
						intent.setClass(FamilyLocateActivity.this, CommunicationActivity.class);
						int userid = fo.getUserId();
						intent.putExtra("userid", userid);
						if (!TextUtils.isEmpty(fo.getRemarkName())) {
							intent.putExtra("nickname", fo.getRemarkName());
						} else if (!TextUtils.isEmpty(fo.getNickname())) {
							intent.putExtra("nickname", fo.getNickname());
						}
						intent.putExtra("avatar", fo.getImage());
						startActivity(intent);
					}

				}
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mPopFamilyLocation) {
					MyAnimationUtils.hideBottomView(mPopFamilyLocation, mPopFamilyLocation2, FamilyLocateActivity.this);
				}
			}
		});

		mListviewFamilies = (LinearLayout) findViewById(R.id.lv_familylocate_families);
		mFamiliesList = new ArrayList<FriendObject>();

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		int selected = getIntent().getIntExtra("selected", 0);

		ArrayList<FriendObject> af = (ArrayList<FriendObject>) CoreModel.getInstance().getFriendList();

		if (null != af) {
			mFamiliesList.clear();

			FriendObject mySelf = new FriendObject();
			mySelf.setUserId(-1);
			mFamiliesList.add(mySelf);

			mFamiliesList.addAll(af);
			FriendObject addFamily = new FriendObject();
			mFamiliesList.add(addFamily);

			mListIndex = -1;
			for (int i = 0; i < mFamiliesList.size(); i++) {
				if (selected == mFamiliesList.get(i).getUserId()) {
					mListIndex = i;
					break;
				}
			}
			updateList();

			if (mListIndex >= 0 && mListIndex < (mFamiliesList.size() - 1)) {
				FriendObject fo = mFamiliesList.get(mListIndex);
				if (null != mBaiduMapView && null != fo) {
					if (-1 == fo.getUserId()) {
						UserObject uo = CoreModel.getInstance().getUserInfo();
						if (null != uo) {
							if (uo.isLocationValid()) {
								mBaiduMapView.setCenter((uo.getLat2()), (uo.getLng2()));
							}
						}
					} else {
						if (fo.isLocationValid() && fo.getShowMyPosition() && !fo.getHideLocation()) {
							mBaiduMapView.setCenter((fo.getLat2()), (fo.getLng2()));
						}
					}
				}
			}

			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mListIndex >= 0 && mListIndex < (mFamiliesList.size() - 1)) {
						FriendObject fo = mFamiliesList.get(mListIndex);
						if (null != mBaiduMapView && null != fo) {
							if (-1 == fo.getUserId()) {
								UserObject uo = CoreModel.getInstance().getUserInfo();
								if (null != uo) {
									if (uo.isLocationValid()) {
										mBaiduMapView.setCenter(-1, (uo.getLat2()), (uo.getLng2()));
									}
								}
							} else {
								if (fo.isLocationValid() && fo.getShowMyPosition() && !fo.getHideLocation()) {
									mBaiduMapView.setCenter(fo.getUserId(), (fo.getLat2()), (fo.getLng2()));
								}
							}
						}
					}
				}
			}, 1500);
		}
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_GET_USER_INFO:
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				if (msg.obj instanceof UserObject) {
					UserObject uo = (UserObject) msg.obj;
					if (uo.isLocationValid() && uo.getShowMyPosition() && !uo.getHideLocation()) {
						mBaiduMapView.setCenterAndMove(uo.getUserId(), (uo.getLat2()), (uo.getLng2()));
					} else {
						String name = uo.getRemarkName();
						if (TextUtils.isEmpty(name)) {
							name = uo.getNickname();
						}
						if (TextUtils.isEmpty(name)) {
							name = uo.getPhone();
						}
						String title = getResources().getString(R.string.dialog_title_tip);
						String content = getResources().getString(R.string.toast_location_error1) + name
								+ getResources().getString(R.string.toast_location_error2);
						dialogOnLocationError(title, content);
					}
				}
			}
			break;
		}
	}

	private void dialogOnLocationError(String title, String message) {
		View view = getLayoutInflater().inflate(R.layout.dialog_one_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(FamilyLocateActivity.this, view, false, false);
			TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			txtTitle.setText(title);
			TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			txtContent.setText(message);
			Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			btnConfirm.setText(getString(R.string.activity_sentsos_btn_know));
			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.cancel();
				}
			});
			dialog.show();
		}
	}

	private void updateList() {
		mListviewFamilies.removeAllViews();
		handleFamiliesListView(mFamiliesList);
	}

	private void handleFamiliesListView(final List<FriendObject> friendList) {

		for (int i = 0; i < friendList.size(); i++) {
			final FriendObject friend = friendList.get(i);
			final int position = i;

			View convertView = LayoutInflater.from(this).inflate(R.layout.list_item_image3, null);
			ImageView imgFamily = (ImageView) convertView.findViewById(R.id.img_image_family);
			ImageView imgKuang = (ImageView) convertView.findViewById(R.id.img_image_kuang);
			TextView tvName = (TextView) convertView.findViewById(R.id.tv_image_name);
			imgKuang.setTag("Kuang" + position);
			if (mListIndex == position) {
				imgKuang.setVisibility(View.VISIBLE);
			}

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mListIndex != position) {
						ImageView imgKuang1 = (ImageView) mListviewFamilies.findViewWithTag("Kuang" + mListIndex);
						if (imgKuang1 != null) {
							imgKuang1.setVisibility(View.INVISIBLE);
						}
					}
					if (position == (friendList.size() - 1)) {
						Intent intent = new Intent(FamilyLocateActivity.this, AddFamily2Activity.class);
						FamilyLocateActivity.this.startActivity(intent);
					} else {
						ImageView imgKuang2 = (ImageView) mListviewFamilies.findViewWithTag("Kuang" + position);
						if (imgKuang2 != null) {
							imgKuang2.setVisibility(View.VISIBLE);
						}

						FriendObject fo = friend;
						if (null != mBaiduMapView && null != fo) {
							if (-1 == fo.getUserId()) {
								UserObject uo = CoreModel.getInstance().getUserInfo();
								if (null != uo) {
									if (uo.isLocationValid()) {
										mBaiduMapView.setCenter(-1, (uo.getLat2()), (uo.getLng2()));
									}
								}
							} else {
								sendMessage(YSMSG.REQ_GET_USER_INFO, fo.getUserId(), 0, null);
								showWaitingDialog();
							}
						}
					}
					mListIndex = position;

				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {

					if (mListIndex != position) {
						ImageView imgKuang1 = (ImageView) mListviewFamilies.findViewWithTag("Kuang" + mListIndex);
						if (imgKuang1 != null) {
							imgKuang1.setVisibility(View.INVISIBLE);
						}
					}
					if (position == 0) {

					} else if (position == (friendList.size() - 1)) {

					} else {
						ImageView imgKuang2 = (ImageView) mListviewFamilies.findViewWithTag("Kuang" + position);
						if (imgKuang2 != null) {
							imgKuang2.setVisibility(View.VISIBLE);
						}

						FriendObject fo = friend;
						if (null != fo) {
							if (null != mBaiduMapView) {
								if (-1 == fo.getUserId()) {

								} else {
									if (fo.isLocationValid() && fo.getShowMyPosition() && !fo.getHideLocation()) {
										mBaiduMapView.setCenter(fo.getUserId(), (fo.getLat2()), (fo.getLng2()));
									}
									if (null != mName) {
										if (!TextUtils.isEmpty(fo.getRemarkName())) {
											mName.setText(fo.getRemarkName());
										} else {
											mName.setText(fo.getNickname());
										}
									}

									if (!fo.getShowMyPosition() || fo.getHideLocation()) {
										mLocation.setText(getResources().getText(R.string.activity_familylocations_none_tip2));
										mLasttime.setText("");
									} else {
										if (TextUtils.isEmpty(fo.getLocation())) {
											mLocation.setText(getResources().getText(R.string.activity_familylocations_none_tip2));
											mLasttime.setText("");
										} else {
											String addr = fo.getLocation();
											if (addr.startsWith("|")) {
												addr = addr.substring(1);
											}
											if (addr.endsWith("|")) {
												addr = addr.substring(0, addr.length() - 1);
											}

											if (addr.contains("|")) {
												int pos = addr.indexOf("|");
												addr = addr.substring(pos + 1, addr.length());
											}
											mLocation.setText(addr);
											mLasttime.setText(fo.getLastMoveTime());
										}
									}
								}
							}

							if (null != mPopFamilyLocation) {
								MyAnimationUtils.showBottomView(mPopFamilyLocation, mPopFamilyLocation2, FamilyLocateActivity.this);
							}

						}

					}
					mListIndex = position;
					return true;
				}
			});

			if (position == (friendList.size() - 1)) {
				imgFamily.setImageResource(R.drawable.image_addfamily_selector);
				tvName.setVisibility(View.VISIBLE);
				tvName.setText(getResources().getString(R.string.activity_addfamily_btn_add));
			} else {
				imgKuang.setImageResource(R.drawable.img_family_selected);
				tvName.setVisibility(View.VISIBLE);
				FriendObject fo = friend;
				if (null != fo) {
					if (-1 == fo.getUserId()) {
						UserObject uo = CoreModel.getInstance().getUserInfo();
						if (null != uo) {
							String imageFile = uo.getImage();
							if (!TextUtils.isEmpty(imageFile)) {
								ImageLoaderHelper.displayImage(imageFile, imgFamily, R.drawable.user, new AvatarProcessor2(-1, (uo.getLat2()), (uo.getLng2()),
										uo.getLastMoveTime()), true);
							} else {
								imgFamily.setImageResource(R.drawable.user);
								mBaiduMapView.addPeople(-1, (uo.getLat2()), (uo.getLng2()), R.drawable.user, uo.getLastMoveTime());
							}
							tvName.setText(uo.getNickname());
						}
					} else {
						String imageFile = fo.getImage();
						if (!TextUtils.isEmpty(imageFile)) {
							if (fo.isLocationValid() && fo.getShowMyPosition() && !fo.getHideLocation()) {
								ImageLoaderHelper.displayImage(imageFile, imgFamily, R.drawable.user,
										new AvatarProcessor2(fo.getUserId(), (fo.getLat2()), (fo.getLng2()), fo.getLastMoveTime()), true);
							} else {
								ImageLoaderHelper.displayImage(imageFile, imgFamily, R.drawable.user, true);
							}
						} else {
							imgFamily.setImageResource(R.drawable.user);
							if (fo.isLocationValid() && fo.getShowMyPosition() && !fo.getHideLocation()) {
								mBaiduMapView.addPeople(fo.getUserId(), (fo.getLat2()), (fo.getLng2()), R.drawable.user, fo.getLastMoveTime());
							}
						}
						if (!TextUtils.isEmpty(fo.getRemarkName())) {
							tvName.setText(fo.getRemarkName());
						} else if (!TextUtils.isEmpty(fo.getNickname())) {
							tvName.setText(fo.getNickname());
						}

					}
				}
			}

			mListviewFamilies.addView(convertView);
		}
	}

	public class AvatarProcessor2 implements BitmapProcessor {

		int mId;
		double mLat;
		double mLng;
		String mTime;

		public AvatarProcessor2(int id, double a, double b, String lasttime) {
			mId = id;
			mLat = a;
			mLng = b;
			mTime = lasttime;
		}

		@Override
		public Bitmap process(Bitmap bitmap) {
			if (0 == mLat && 0 == mLng && TextUtils.isEmpty(mTime)) {
				return bitmap;
			}
			try {
				final Bitmap output = mBaiduMapView.convertViewToBitmapEx(bitmap);
				if (!TextUtils.isEmpty(mTime)) {
					NinePatchDrawable dwBk = (NinePatchDrawable) getResources().getDrawable(R.drawable.img_family_location_pop);
					Paint stringPaint = new Paint();
					Rect stringRect = new Rect();

					stringPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
					stringPaint.setColor(Color.WHITE);
					stringPaint.setTextAlign(Align.CENTER);
					stringPaint.getTextBounds(mTime, 0, mTime.length(), stringRect);
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
					if (popRect.width() < output.getWidth()) {
						popRect.right = output.getWidth();
					}
					popRect.bottom = popRect.height() + output.getHeight();
					Bitmap bitmapFinal = Bitmap.createBitmap(popRect.width(), popRect.height(), Config.ARGB_8888);
					Canvas canvas = new Canvas(bitmapFinal);
					stringPaint.setAntiAlias(true);

					canvas.drawARGB(0, 0, 0, 0);
					dwBk.draw(canvas);
					canvas.drawText(mTime, stringRect.centerX(), baseline, stringPaint);
					canvas.drawBitmap(output, (popRect.width() - output.getWidth()) / 2, stringRect.bottom, stringPaint);
					if (bitmapFinal != null) {
						mBaiduMapView.addPeople(mId, mLat, mLng, bitmapFinal);
					}
				} else {
					if (output != null) {
						mBaiduMapView.addPeople(mId, mLat, mLng, output);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}
	}

	@Override
	public void onMapLoaded() {

	}

	@Override
	public void onMarkerClick(MyMarker mymarker) {
		int user_id = mymarker.mTag;
		if (-1 == user_id) {
			return;
		} else {
			int count = mFamiliesList.size();
			for (int i = 0; i < count; i++) {
				FriendObject fo = mFamiliesList.get(i);
				if (null == fo) {
					continue;
				}
				if (user_id == fo.getUserId()) {
					if (null != mName) {
						if (!TextUtils.isEmpty(fo.getRemarkName())) {
							mName.setText(fo.getRemarkName());
						} else {
							mName.setText(fo.getNickname());
						}
					}

					if (!fo.getShowMyPosition() || fo.getHideLocation()) {
						mLocation.setText(getResources().getText(R.string.activity_familylocations_none_tip2));
						mLasttime.setText("");
					} else {
						if (TextUtils.isEmpty(fo.getLocation())) {
							mLocation.setText(getResources().getText(R.string.activity_familylocations_none_tip2));
						} else {
							String addr = fo.getLocation();
							if (addr.startsWith("|")) {
								addr = addr.substring(1);
							}
							if (addr.endsWith("|")) {
								addr = addr.substring(0, addr.length() - 1);
							}

							if (addr.contains("|")) {
								int pos = addr.indexOf("|");
								addr = addr.substring(pos + 1, addr.length());
							}							
							mLocation.setText(addr);
							mLasttime.setText(fo.getLastMoveTime());
						}
					}
					mListIndex2 = i;
					break;
				}
			}
		}

		if (null != mPopFamilyLocation) {
			MyAnimationUtils.showBottomView(mPopFamilyLocation, mPopFamilyLocation2, FamilyLocateActivity.this);
		}
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
		if (null != mFamiliesList) {
			mFamiliesList.clear();
		}

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
	            YSToast.showToast(FamilyLocateActivity.this, R.string.toast_map_zoomout_max);
	        }		
	        else
	        {
	            if (mZoomLevel == 3) {
	            	mImageZoomin.setEnabled(true);
	                mImageZoomout.setEnabled(false);
	                YSToast.showToast(FamilyLocateActivity.this, R.string.toast_map_zoomin_min);
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
                YSToast.showToast(FamilyLocateActivity.this, R.string.toast_map_zoomout_max);
            }
            if (mZoomLevel == 3) {
                YSToast.showToast(FamilyLocateActivity.this, R.string.toast_map_zoomin_min);
            }
        }
	}

	@Override
	public void OnMapClick() {
	}
}
