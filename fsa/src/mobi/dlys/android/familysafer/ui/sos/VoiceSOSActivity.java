package mobi.dlys.android.familysafer.ui.sos;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.audio.LocalPlayer;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapListener;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView.MyMarker;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.checkin.CheckinActivity;
import mobi.dlys.android.familysafer.ui.checkin.SendCheckActivity;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.family.AddFamily1Activity;
import mobi.dlys.android.familysafer.ui.location.LostActivity;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.search.poi.PoiResult;

public class VoiceSOSActivity extends BaseExActivity implements BaiduMapListener {
	public static final String EXTRA_VOICE_PATH = "extra_voice_path";
	public static final String EXTRA_VOICE_DURATION = "extra_voice_duration";
	public static final int SendSOS_Action_Id = 10100;
	public static final int SendSOS_Action_Result = 100;

	ImageView mImagePlay = null;
	TextView mVoiceTip = null;
	TextView mLocation = null;
	Button mCancel = null;
	Button mSend = null;
	BaiduMapView mBaiduMapView = null;

	LocalPlayer mLocalPlayer;
	boolean mIsPlay = false;

	int mVoiceDuration = 0;
	String mVoiceFilePath;

	private TextView tvLoc = null;
	private ImageView imgUser = null;
	private View mAvatar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voicesos);

		initView();
		initData();

	}

	private void initView() {
		mImagePlay = (ImageView) findViewById(R.id.img_voicesos_play);
		mVoiceTip = (TextView) findViewById(R.id.tv_voicesos_duration);
		mLocation = (TextView) findViewById(R.id.tv_voicesos_loc);
		mCancel = (Button) findViewById(R.id.btn_voicesos_cancel);
		mSend = (Button) findViewById(R.id.btn_voicesos_send);

		mBaiduMapView = new BaiduMapView(this, R.id.layout_voicesos_map, false, false);
		mBaiduMapView.setListener(this);

		mAvatar = LayoutInflater.from(this).inflate(R.layout.layout_avatatar_pop, mBaiduMapView.getLayout());
		tvLoc = (TextView) mAvatar.findViewById(R.id.tv_checkin_location);
		tvLoc.setVisibility(View.GONE);
		imgUser = (ImageView) mAvatar.findViewById(R.id.img_checkin_avatar);

		if (CoreModel.getInstance().getUserInfo() != null) {
			String location = CoreModel.getInstance().getUserInfo().getLocation();
			if (TextUtils.isEmpty(location)) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(VoiceSOSActivity.this, LostActivity.class);
						intent.putExtra("rid", VoiceSOS_Lost_Action_Id_Result);
						startActivityForResult(intent, VoiceSOS_Lost_Action_Id);
					}
				}, 1000);
			}
		}

	}

	private int VoiceSOS_Lost_Action_Id_Result = 100001;
	private int VoiceSOS_Lost_Action_Id = 100002;

	private void initData() {
		mVoiceDuration = getIntent().getIntExtra(EXTRA_VOICE_DURATION, 0);
		mVoiceFilePath = getIntent().getStringExtra(EXTRA_VOICE_PATH);

		if (null != mImagePlay) {
			mImagePlay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							AnalyticsHelper.onEvent(VoiceSOSActivity.this, AnalyticsHelper.index_play_voice);
						}
					}, 1000);
					if (mIsPlay) {

						stop();
					} else {

						play(mVoiceFilePath);
					}
				}
			});
		}

		if (null != mVoiceTip) {
			mVoiceTip.setText(mVoiceDuration + "''");
		}

		if (CoreModel.getInstance().getUserInfo() != null) {
			App app = (App) getApplication();
			BaiduLoc locater = app.getLocater();
			mBaiduMapView.setCenter(locater.getLatitude(), locater.getLongitude());
		}

		if (null != mLocation) {
			if (CoreModel.getInstance().getUserInfo() != null) {
				String location = CoreModel.getInstance().getUserInfo().getLocation();
				mLocation.setText(location);
			}
		}

		if (null != mCancel) {
			mCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					showCancelDialog();

				}
			});
		}

		if (null != mSend) {
			mSend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (CoreModel.getInstance().getFriendList().size() > 0) {
						Intent intent = new Intent(VoiceSOSActivity.this, SendSOSActivity.class);
						intent.putExtra(VoiceSOSActivity.EXTRA_VOICE_PATH, mVoiceFilePath);
						intent.putExtra(VoiceSOSActivity.EXTRA_VOICE_DURATION, mVoiceDuration);
						startActivityForResult(intent, VoiceSOSActivity.SendSOS_Action_Id);
					} else {
						Intent intent = new Intent(VoiceSOSActivity.this, VoiceSOSNoneActivity.class);
						startActivity(intent);
					}
				}
			});
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				if (CoreModel.getInstance().getFriendList().size() <= 0){
					View view = getLayoutInflater().inflate(R.layout.dialog_two_button, null);
					if (view != null) {
						final Dialog dialog = YSAlertDialog.createBaseDialog(App.getInstance().getForegroundActivity(), view, false, false);
						TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
						txtTitle.setText(getString(R.string.dialog_title_tip));
						TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
						txtContent.setText(getString(R.string.activity_voicesos_dlg_nobody));
						Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
						Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
						btnConfirm.setText(getString(R.string.activity_voicesos_dlg_nobody_yes));
						btnCancel.setText(getString(R.string.activity_voicesos_dlg_nobody_no));
						btnConfirm.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {
								dialog.cancel();
								Intent intent = new Intent(VoiceSOSActivity.this, AddFamily1Activity.class);
								startActivity(intent);
							}
						});
						btnCancel.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {
		
								dialog.cancel();
		
							}
						});
						dialog.show();
					}		
				}
			}
		}, 1000);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showCancelDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showCancelDialog() {
		View view = getLayoutInflater().inflate(R.layout.dialog_two_button, null);
		if (view != null) {
			final Dialog dialog = YSAlertDialog.createBaseDialog(VoiceSOSActivity.this, view, false, true);
			final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
			txtTitle.setText(getString(R.string.activity_voicesos_btn_cancel));
			final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
			txtContent.setText(getString(R.string.activity_voicesos_dlg_cancel));

			final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
			btnConfirm.setText(R.string.activity_voicesos_dlg_yes);
			final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
			btnCancel.setText(R.string.activity_voicesos_dlg_no);

			btnConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							AnalyticsHelper.onEvent(VoiceSOSActivity.this, AnalyticsHelper.index_send_accept);
						}
					}, 1000);

					dialog.cancel();

					YSToast.showToast(getApplicationContext(), getString(R.string.toast_sos_voice_canceled));

					Intent intent = new Intent(VoiceSOSActivity.this, MainActivity.class);
					startActivity(intent);
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							AnalyticsHelper.onEvent(VoiceSOSActivity.this, AnalyticsHelper.index_send_cancel);
						}
					}, 1000);

					dialog.cancel();
				}
			});

			dialog.show();
		}

	}

	@Override
	public void onMapLoaded() {
		App app = (App) getApplication();
		BaiduLoc locater = app.getLocater();
		mBaiduMapView.setAvatar(0, locater.getLatitude(), locater.getLongitude(), imgUser, CoreModel.getInstance().getUserInfo().getImage(), mAvatar);
	}

	@Override
	public void onMarkerClick(MyMarker mymarker) {
		Intent intent = new Intent(VoiceSOSActivity.this, ShowMapActivity.class);
		intent.putExtra("lat", 0.0);
		intent.putExtra("lng", 0.0);
		intent.putExtra("avatar", CoreModel.getInstance().getUserInfo().getImage());
		startActivity(intent);
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
		stop();
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

	private void play(String voiceFilePath) {
		if (TextUtils.isEmpty(voiceFilePath)) {
			return;
		}
		if (null == mLocalPlayer) {
			mLocalPlayer = new LocalPlayer();
		}

		if (null != mLocalPlayer) {
			mIsPlay = true;
			mImagePlay.setImageResource(R.drawable.notification_pause_selector);
			mLocalPlayer.startPlay(voiceFilePath, new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer arg0) {
					stop();
				}
			});
		}
	}

	private void stop() {
		mIsPlay = false;
		mImagePlay.setImageResource(R.drawable.notification_play_selector);
		if (null != mLocalPlayer) {
			mLocalPlayer.stopPlay();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SendSOS_Action_Id) {
			if (resultCode == SendSOS_Action_Result) {
				finish();
			}
		} else if (requestCode == VoiceSOS_Lost_Action_Id) {
			if (resultCode == VoiceSOS_Lost_Action_Id_Result) {
				initData();
			}
		}
	}


	@Override
	public void OnMapChanged(MapStatus arg0) {

	}

	@Override
	public void OnMapClick() {
		Intent intent = new Intent(VoiceSOSActivity.this, ShowMapActivity.class);
		intent.putExtra("lat", 0.0);
		intent.putExtra("lng", 0.0);
		intent.putExtra("avatar", CoreModel.getInstance().getUserInfo().getImage());
		startActivity(intent);
	}
}
