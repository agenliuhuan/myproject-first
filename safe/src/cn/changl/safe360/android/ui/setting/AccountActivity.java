package cn.changl.safe360.android.ui.setting;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.ImageUtils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.camera.Snapshot;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;
import cn.changl.safe360.android.utils.FileUtils;
import cn.changl.safe360.android.utils.ImageLoaderHelper;
import cn.changl.safe360.android.utils.MyAnimationUtils;

public class AccountActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	private Snapshot mSnapshot = null;

	LinearLayout mImagePop = null;
	LinearLayout mImagePopView = null;

	Button mSnapshotButton = null;
	Button mAlbumButton = null;
	Button mCancelButton = null;

	public int Snapshot_Action_Id = 10087;
	public int Photos_Action_Id = 10088;
	public int Clip_Action_Id = 10089;

	RelativeLayout mImageLayout = null;
	RelativeLayout mNicknameLayout = null;
	RelativeLayout mPasswordLayout = null;

	TextView mAccountTxt;
	TextView mPhoneTxt;
	TextView mNicknameTxt;
	TextView sexTxt;

	ImageView mImage = null;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, AccountActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_account);

		initView();
		initData();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_account_ttb_title);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AccountActivity.this.finish();
			}
		});
		mSnapshot = new Snapshot();

		mImage = (ImageView) findViewById(R.id.img_account_image);

		mImagePop = (LinearLayout) findViewById(R.id.layout_account_image_pop);
		mImagePopView = (LinearLayout) findViewById(R.id.layout_account_image_pop_view);
		mImagePop.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (null != mImagePop) {
					MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, AccountActivity.this);
				}
			}
		});
		mImagePopView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

			}
		});
		mSnapshotButton = (Button) findViewById(R.id.btn_account_image_snapshot);
		mAlbumButton = (Button) findViewById(R.id.btn_account_image_album);
		mCancelButton = (Button) findViewById(R.id.btn_account_image_cancel);

		mSnapshotButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mImagePop) {
					MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, AccountActivity.this);
				}

				String strImgPath = FileUtils.COVER;
				File out = new File(strImgPath);
				if (!out.exists()) {
					out.mkdirs();
				}
				String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
				String photoFilePath = strImgPath + fileName;
				if (null != mSnapshot) {
					mSnapshot.takePhoto(AccountActivity.this, photoFilePath, Snapshot_Action_Id, Snapshot.AVATAR_PERCENT);
				}
			}

		});

		mAlbumButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mImagePop) {
					MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, AccountActivity.this);
				}
				Intent album = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				album.setType("image/*");
				startActivityForResult(album, Photos_Action_Id);
			}

		});

		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mImagePop) {
					MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, AccountActivity.this);
				}
			}

		});

		mImageLayout = (RelativeLayout) findViewById(R.id.layout_account_image);
		mImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyAnimationUtils.showBottomView(mImagePop, mImagePopView, AccountActivity.this);
			}

		});

		mNicknameLayout = (RelativeLayout) findViewById(R.id.layout_account_nickname);
		mNicknameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(AccountActivity.this,
				// NicknameActivity.class);
				// intent.putExtra("nickname",
				// CoreModel.getInstance().getUserInfo().getNickname());
				// startActivity(intent);
				NicknameActivity.startActivity(AccountActivity.this);
			}

		});

		RelativeLayout mSexLayout = (RelativeLayout) findViewById(R.id.layout_account_sex);
		sexTxt = (TextView) findViewById(R.id.tv_account_sex);
		mSexLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(AccountActivity.this,
				// NicknameActivity.class);
				// intent.putExtra("nickname",
				// CoreModel.getInstance().getUserInfo().getNickname());
				// startActivity(intent);
				SexActivity.startActivity(AccountActivity.this);
			}

		});

		mPasswordLayout = (RelativeLayout) findViewById(R.id.layout_account_newpassword);
		mPasswordLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AccountActivity.this, PasswordActivity.class);
				startActivity(intent);
			}

		});

		mPhoneTxt = (TextView) findViewById(R.id.tv_account_phone);
		mNicknameTxt = (TextView) findViewById(R.id.tv_account_nickname);
		mAccountTxt = (TextView) findViewById(R.id.tv_account_image);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Snapshot_Action_Id) {
			String imageFile = "";
			if (null != mSnapshot) {
				DisplayMetrics dm = new DisplayMetrics();
				AccountActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);

				imageFile = mSnapshot.onActivityResultProc(dm.widthPixels > dm.heightPixels ? dm.widthPixels : dm.heightPixels);
			}

			Intent intent = new Intent("com.android.camera.action.CROP");
			File temp = new File(imageFile);
			intent.setDataAndType(Uri.fromFile(temp), "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", Snapshot.AVATAR_WIDTH_HEIGHT);
			intent.putExtra("outputY", Snapshot.AVATAR_WIDTH_HEIGHT);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, Clip_Action_Id);

		} else if (requestCode == Photos_Action_Id) {
			if (data != null) {
				Uri uri = (Uri) data.getData();
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(uri, "image/*");
				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", Snapshot.AVATAR_WIDTH_HEIGHT);
				intent.putExtra("outputY", Snapshot.AVATAR_WIDTH_HEIGHT);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, Clip_Action_Id);
			}

		} else if (requestCode == Clip_Action_Id) {
			if (data != null) {
				Bitmap bitmap = data.getExtras().getParcelable("data");
				Bitmap bitmap2 = ImageUtils.roundCorners(bitmap, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
				mImage.setImageBitmap(bitmap2);
				if (ImageUtils.saveBitmapToFile(bitmap2, FileUtils.AVATAR, Snapshot.AVATAR_PERCENT)) {
					setAvatar(FileUtils.AVATAR);
				} else {

				}
				ImageUtils.recycleBitmap(bitmap);
			}
		}

	}

	private void initData() {
		UserObject user = CoreModel.getInstance().getUserInfo();
		if (user != null) {
			mAccountTxt.setText(user.getNickname());
			mPhoneTxt.setText(user.getPhone());
			mNicknameTxt.setText(user.getNickname());
			if (user.getGender() == 2) {
				sexTxt.setText(getString(R.string.activity_register_3_choicewoman));
			} else {
				sexTxt.setText(getString(R.string.activity_register_3_choiceman));
			}
			// ImageLoaderHelper.displayImage(user.getBigDisplayImage(), mImage,
			// R.drawable.account_headimg_user, true);
			ImageLoaderHelper.displayAvatar(user.getBigDisplayImage(), mImage);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	@Override
	public void onBackPressed() {
		if (mImagePop.getVisibility() == View.VISIBLE) {
			MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, AccountActivity.this);
			return;
		}
		super.onBackPressed();
	}

	private void setAvatar(String filePath) {
		// 创建临时UserObject
		UserObject user = CoreModel.getInstance().getUserInfo().clone();
		if (user != null) {
			user.setImage(filePath);
		}

		// 发送请求
		sendMessage(YSMSG.REQ_MODIFY_USER_INFO, 0, 0, user);
		showWaitingDialog();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_MODIFY_USER_INFO: {
			dismissWaitingDialog();

			if (msg.arg1 == 200) {
				// 成功
				CoreModel.getInstance().setmChangeUserImg(true);
				YSToast.showToast(getBaseContext(), R.string.toast_avatar_modify_succeed);
				UserObject user = CoreModel.getInstance().getUserInfo();
				if (user != null) {
					ImageLoaderHelper.displayAvatar(user.getDisplayImage(), mImage);
				}
			} else {
				// 失败
				YSToast.showToast(AccountActivity.this, R.string.toast_avatar_modify_failed);
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(getBaseContext(), result.getErrorMsg());
				} else {
					YSToast.showToast(getBaseContext(), R.string.network_error);
				}
			}
		}
			break;
		}
	}

}