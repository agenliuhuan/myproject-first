package mobi.dlys.android.familysafer.ui.clue;

import mobi.dlys.android.core.image.universalimageloader.core.ImageLoader;
import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapListener;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView.MyMarker;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.ui.checkin.CheckinActivity;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.search.poi.PoiResult;

public class SentClueActivity extends BaseExActivity implements BaiduMapListener {
	public static final String EXTRA_CLUE_OBJECT = "extra_clue_object";

	protected TitleBarHolder mTitleBar;
	Button mSentMapBtn;
	BaiduMapView mBaiduMapView;
	TextView mSentclueTip2;
	TextView mSentclueTip3;
	TextView mSentclueTip4;
	TextView mSentclueTip5;
	TextView mSentclueTip6;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private ClueObject mClueObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sentclue);

		initView();
		initData();
	}

	void initView() {

		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_clue_ttb_title);
		mTitleBar.mTitle.setTextColor(getResources().getColor(R.color.title_green_line));
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		mBaiduMapView = new BaiduMapView(this, R.id.img_sentclue_map, false, false);
		mBaiduMapView.setListener(this);

		mSentclueTip2 = (TextView) findViewById(R.id.tv_sentclue_tip_2);
		mSentclueTip3 = (TextView) findViewById(R.id.tv_sentclue_tip_3);
		mSentclueTip4 = (TextView) findViewById(R.id.tv_sentclue_tip_4);
		mSentclueTip5 = (TextView) findViewById(R.id.tv_sentclue_tip_5);
		mSentclueTip6 = (TextView) findViewById(R.id.tv_sentclue_tip_6);

		mSentMapBtn = (Button) findViewById(R.id.sentclue_capBtn);
		mSentMapBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(SentClueActivity.this, ViewClueActivity.class);
				intent.putExtra(ViewClueActivity.VIEW_CLUE_OBJECT, mClueObject);
				startActivity(intent);
			}
		});

	}

	private void initData() {
		mClueObject = (ClueObject) getIntent().getSerializableExtra(EXTRA_CLUE_OBJECT);
		if (null == mClueObject) {
			return;
		}

		double lat = mClueObject.getLat2();
		double lon = mClueObject.getLng2();
		if (0.0 != lat && 0.0 != lon && 4.9E-324 != lat && 4.9E-324 != lon) {
			mBaiduMapView.setCenter(-1, lat, lon);
		}
		LinearLayout mapLL = (LinearLayout) findViewById(R.id.img_sentclue_map);
		ViewGroup.LayoutParams params = mapLL.getLayoutParams();
		int height = params.height;
		RelativeLayout.LayoutParams newparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		newparams.setMargins(0, (height - 124) / 2, 0, 0);
		newparams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		newparams.addRule(RelativeLayout.BELOW, R.id.titlebar_sentclue);
		mSentMapBtn.setLayoutParams(newparams);
		mSentMapBtn.setBackgroundResource(R.drawable.sendclue_cap_selector);

		mSentclueTip2.setText(getString(R.string.activity_sentclue_tv_tip_2) + mClueObject.getClueId());
		mSentclueTip3.setText(getString(R.string.activity_sentclue_tv_tip_3) + mClueObject.getCreateTime());
		mSentclueTip4.setText(mClueObject.getLocation());

		mSentclueTip5.setText(getString(R.string.activity_sentclue_tv_tip_5) + AndroidConfig.getPhoneModel());
		mSentclueTip6.setText(getString(R.string.activity_sentclue_tv_tip_6));
	}

	public void onMapLoaded() {

	}

	public void onSearched(PoiResult result) {

	}

	public void onMarkerClick(MyMarker mymarker) {

	}

	@Override
	public void onSnapshotReady(Bitmap snapshot) {

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


	@Override
	public void OnMapChanged(MapStatus arg0) {

	}

	@Override
	public void OnMapClick() {
	}
}
