package mobi.dlys.android.familysafer.ui.clue;

import java.util.ArrayList;

import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.vo.ClueImageObject;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ViewClueActivity extends BaseExActivity {
	public static final String VIEW_CLUE_OBJECT = "view_clue_object";

	protected TitleBarHolder mTitleBar;

	GridView mGrid = null;

	myImagesAdapter mAdapter = null;

	ArrayList<String> mReceiver = null;

	ClueObject mClueObject;
	TextView mLocationTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewclue);

		initView();
		initData();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_viewclue_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

	}

	void initData() {
		mClueObject = (ClueObject) getIntent().getSerializableExtra(VIEW_CLUE_OBJECT);
		if (null == mClueObject) {
			return;
		}

		mGrid = (GridView) findViewById(R.id.gv_viewclue_images);
		mReceiver = mClueObject.getImageUrlList();
		ArrayList<ClueImageObject> cList = mClueObject.getImageList();
		setGridParams(mGrid, cList.size(), cList);
		mAdapter = new myImagesAdapter(this, cList);
		mGrid.setAdapter(mAdapter);

		mGrid.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent event) {
				return MotionEvent.ACTION_MOVE == event.getAction() ? true : false;
			}
		});

		TextView mUserNameTV = (TextView) findViewById(R.id.tv_viewclue_user_name);
		TextView mDateTV = (TextView) findViewById(R.id.tv_viewclue_date);
		TextView mContentTV = (TextView) findViewById(R.id.tv_viewclue_content);
		mLocationTV = (TextView) findViewById(R.id.tv_viewclue_location);
		TextView mPhoneTV = (TextView) findViewById(R.id.tv_viewclue_phone);
		mUserNameTV.setText(getString(R.string.activity_viewclue_user_name));
		mDateTV.setText(mClueObject.getCreateTime());
		if (TextUtils.isEmpty(mClueObject.getMessage())) {
			mContentTV.setVisibility(View.GONE);
		} else {
			mContentTV.setVisibility(View.VISIBLE);
			mContentTV.setText(mClueObject.getMessage());
		}

		mLocationTV.setText(mClueObject.getLocation());
		mPhoneTV.setText(AndroidConfig.getPhoneModel());
	}

	protected void onResume() {
		super.onResume();
	}

	private void setGridParams(GridView mGrid, int count, ArrayList<ClueImageObject> images) {
		int Width = (int) getResources().getDimension(R.dimen.one_image_width);
		int Height = (int) getResources().getDimension(R.dimen.one_image_height);
		int Screenwidth = AndroidConfig.getScreenWidth();
		LayoutParams params;
		int imageMargin = (int) getResources().getDimension(R.dimen.image_margin);
		switch (count) {
		case 1:
			ClueImageObject image = images.get(0);
			if (image.getWidth() != 0) {
				int width = image.getWidth();
				int height = image.getHeight();
				if (width < height) {
					params = new LayoutParams(Width, Height);
				} else {
					params = new LayoutParams(Height, Width);
				}
			} else {
				params = new LayoutParams(Width, Height);
			}
			params.setMargins(0, imageMargin, 0, imageMargin);
			mGrid.setLayoutParams(params);
			mGrid.setSelector(R.color.transparent);
			mGrid.setNumColumns(1);
			break;
		case 2:
			params = new LayoutParams((Screenwidth - imageMargin * 10) / 3 * 2 + 4, (Screenwidth - imageMargin * 10) / 3);
			params.setMargins(0, imageMargin, 0, imageMargin);
			mGrid.setLayoutParams(params);
			mGrid.setSelector(R.color.transparent);
			mGrid.setNumColumns(2);
			mGrid.setHorizontalSpacing(4);
			mGrid.setVerticalSpacing(4);
			break;
		case 3:
			params = new LayoutParams((Screenwidth - imageMargin * 10) + 4 * 2, (Screenwidth - imageMargin * 10) / 3);
			params.setMargins(0, imageMargin, 0, imageMargin);
			mGrid.setLayoutParams(params);
			mGrid.setSelector(R.color.transparent);
			mGrid.setNumColumns(3);
			mGrid.setHorizontalSpacing(4);
			mGrid.setVerticalSpacing(4);
			break;
		case 4:
			params = new LayoutParams((Screenwidth - imageMargin * 10) / 3 * 2 + 4, (Screenwidth - imageMargin * 10) / 3 * 2 + 4);
			params.setMargins(0, imageMargin, 0, imageMargin);
			mGrid.setLayoutParams(params);
			mGrid.setSelector(R.color.transparent);
			mGrid.setNumColumns(2);
			mGrid.setHorizontalSpacing(4);
			mGrid.setVerticalSpacing(4);
			break;
		case 5:
		case 6:
			params = new LayoutParams((Screenwidth - imageMargin * 10) + 4 * 2, (Screenwidth - imageMargin * 10) / 3 * 2 + 4);
			params.setMargins(0, imageMargin, 0, imageMargin);
			mGrid.setLayoutParams(params);
			mGrid.setSelector(R.color.transparent);
			mGrid.setNumColumns(3);
			mGrid.setHorizontalSpacing(4);
			mGrid.setVerticalSpacing(4);
			break;
		case 7:
		case 8:
			params = new LayoutParams((Screenwidth - imageMargin * 10) + 4 * 2, (Screenwidth - imageMargin * 10) + 4 * 2);
			params.setMargins(0, imageMargin, 0, imageMargin);
			mGrid.setLayoutParams(params);
			mGrid.setSelector(R.color.transparent);
			mGrid.setNumColumns(3);
			mGrid.setHorizontalSpacing(4);
			mGrid.setVerticalSpacing(4);
			break;
		}
	}

	class myImagesAdapter extends BaseAdapter {
		private Context mContext;
		public ArrayList<ClueImageObject> mList;
		int Width;
		int Height;

		public myImagesAdapter(Context contect, ArrayList<ClueImageObject> list) {
			this.mContext = contect;
			this.mList = list;
			Width = (int) mContext.getResources().getDimension(R.dimen.one_image_width);
			Height = (int) mContext.getResources().getDimension(R.dimen.one_image_height);
		}

		@Override
		public int getCount() {
			if (mList == null) {
				return 0;
			} else {
				return this.mList.size();
			}
		}

		@Override
		public ClueImageObject getItem(int position) {
			if (mList == null) {
				return null;
			} else {
				return this.mList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_clue_image, null);
			}
			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent(ViewClueActivity.this, ViewerActivity.class);
					intent.putStringArrayListExtra("clues", mClueObject.getImageUrlList());
					intent.putExtra("position", position);
					startActivity(intent);
				}
			});
			ImageView mImage = (ImageView) convertView.findViewById(R.id.item_clueimage_img);
			if (this.mList != null && mList.size() != 0) {
				ClueImageObject clueImage = this.mList.get(position);
				int Screenwidth = AndroidConfig.getScreenWidth();
				int imageMargin = (int) mContext.getResources().getDimension(R.dimen.image_margin);
				if (mImage != null && clueImage != null) {
					String imageFile = clueImage.getImage();
					if (imageFile.equals("takephoto")) {
						FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 5) / 4, (Screenwidth - imageMargin * 5) / 4);
						mImage.setLayoutParams(params);
						mImage.setScaleType(ImageView.ScaleType.FIT_XY);
						mImage.setImageResource(R.drawable.cap);
					} else if (!TextUtils.isEmpty(imageFile)) {
						if (mList.size() == 1) {
							if (clueImage.getWidth() != 0) {
								int width = clueImage.getWidth();
								int height = clueImage.getHeight();
								if (width < height) {
									FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Width, Height);
									mImage.setLayoutParams(params);
									mImage.setScaleType(ImageView.ScaleType.FIT_XY);
									ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
								} else {
									FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Height, Width);
									mImage.setLayoutParams(params);
									mImage.setScaleType(ImageView.ScaleType.FIT_XY);
									ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
								}
							} else {
								FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Width, Height);
								mImage.setLayoutParams(params);
								mImage.setScaleType(ImageView.ScaleType.FIT_XY);
								ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
							}
						} else {
							FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 10) / 3,
									(Screenwidth - imageMargin * 10) / 3);
							mImage.setLayoutParams(params);
							mImage.setScaleType(ImageView.ScaleType.FIT_XY);
							ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
						}
					} else {
						FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 10) / 3,
								(Screenwidth - imageMargin * 10) / 3);
						mImage.setLayoutParams(params);
						mImage.setScaleType(ImageView.ScaleType.FIT_XY);
						mImage.setImageResource(R.drawable.default_bg_image);
					}
				}
			}
			return convertView;
		}
	}
}