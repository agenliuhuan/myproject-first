package mobi.dlys.android.familysafer.ui.clue;

import java.io.File;
import java.util.ArrayList;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ClueImageActivity extends BaseExActivity {
	private Button mLeftBtn;
	private TextView mTitle;
	private Button mRightBtn;
	private ArrayList<String> mData;
	int curposition = 0;
	ImageView imageview;
	final int RIGHT = 0;
	final int LEFT = 1;
	private GestureDetector gestureDetector;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_clueimage);
		initView();
		Intent intent = getIntent();
		if (null != intent) {
			Bundle bundle = intent.getExtras();
			mData = bundle.getStringArrayList("clues");
			curposition = intent.getIntExtra("position", 0);
			mTitle.setText((curposition + 1) + "/" + mData.size());
		}
		ImageLoaderHelper.displayImage(mData.get(curposition), imageview, R.drawable.default_bg_image, false);
	}

	private void initView() {
		mLeftBtn = (Button) findViewById(R.id.ClueImage_leftBtn);
		mTitle = (TextView) findViewById(R.id.ClueImage_center_title);
		mRightBtn = (Button) findViewById(R.id.ClueImage_rightBtn);

		mRightBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String path = mData.get(curposition);
				File file = new File(path);
				if (file.exists()) {
					if (file.delete()) {
						mData.remove(curposition);
						if (mData.size() == 0) {
							Intent intent = new Intent();
							intent.putStringArrayListExtra("mdata", mData);
							setResult(Activity.RESULT_OK, intent);
							ClueImageActivity.this.finish();
						} else {
							if (curposition == 0) {
								curposition = mData.size() - 1;
							} else {
								curposition--;
							}
							ImageLoaderHelper.displayImage(mData.get(curposition), imageview, R.drawable.default_bg_image, false);
							mTitle.setText((curposition + 1) + "/" + mData.size());
						}
					}
				}
			}
		});
		mLeftBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putStringArrayListExtra("mdata", mData);
				setResult(Activity.RESULT_OK, intent);
				ClueImageActivity.this.finish();
			}
		});
		imageview = (ImageView) findViewById(R.id.ClueImage_image);
		gestureDetector = new GestureDetector(ClueImageActivity.this, onGestureListener);
	}

	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (mData.size() == 1) {
				return true;
			}
			float x = e2.getX() - e1.getX();
			// float y = e2.getY() - e1.getY();
			if (x > 0) {
				doResult(RIGHT);
			} else if (x < 0) {
				doResult(LEFT);
			}
			return true;
		}
	};

	public void doResult(int action) {
		switch (action) {
		case RIGHT:
			if (curposition == 0) {
				curposition = mData.size() - 1;
			} else {
				curposition--;
			}
			ImageLoaderHelper.displayImage(mData.get(curposition), imageview, R.drawable.default_bg_image, false);
			mTitle.setText((curposition + 1) + "/" + mData.size());
			break;
		case LEFT:
			if (curposition == mData.size() - 1) {
				curposition = 0;
			} else {
				curposition++;
			}
			ImageLoaderHelper.displayImage(mData.get(curposition), imageview, R.drawable.default_bg_image, false);
			mTitle.setText((curposition + 1) + "/" + mData.size());
			break;

		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
}
