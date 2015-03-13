package mobi.dlys.android.familysafer.ui.register;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.core.utils.ImageUtils;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.RegisterObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.camera.Snapshot;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.YSWaitingDialog;
import mobi.dlys.android.familysafer.ui.comm.YSWaitingDialog2;
import mobi.dlys.android.familysafer.ui.login.Fogot1Activity;
import mobi.dlys.android.familysafer.ui.main.GuideActivity;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.MyAnimationUtils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Register3Activity extends BaseExActivity implements OnClickListener {
	protected TitleBarHolder mTitleBar;

	private Snapshot mSnapshot = null;

	public int Snapshot_Action_Id = 10087;
	public int Photos_Action_Id = 10088;
	public int Clip_Action_Id = 10089;

	Button mSnapshotButton = null;
	Button mAlbumButton = null;
	Button mCancelButton = null;

	LinearLayout mPopImage = null;
	LinearLayout mPopImageView = null;

	ImageView mImage = null;

	EditText mNickName = null;

	boolean mHasNickName = false;

	boolean mHasAvatar = false;
	
	private YSWaitingDialog2 mWaitingDialog2;

	@Override
	protected void onDestroy() {
		super.onDestroy();

		dismissWaitingDialog2();
	}
	
	@Override
	public void onBackPressed() {
		if (mPopImage.getVisibility() == View.VISIBLE) {
			MyAnimationUtils.hideBottomView(mPopImage, mPopImageView, Register3Activity.this);
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_3);

		initView();
	}

	private void initView() {

		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_register_3_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setText(R.string.titlebar_button_tip_over);
		mTitleBar.mRight.setEnabled(false);
		mTitleBar.mRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				submit();
			}

		});

		mSnapshot = new Snapshot();

		mPopImage = (LinearLayout) findViewById(R.id.layout_register_3_bk);
		mPopImageView = (LinearLayout) findViewById(R.id.layout_register_3_bk_view);
		mPopImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mPopImage) {
					MyAnimationUtils.hideBottomView(mPopImage, mPopImageView, Register3Activity.this);
				}
			}
		});
		mPopImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});
		mImage = (ImageView) findViewById(R.id.img_register_3_image);
		mImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mNickName.getWindowToken(), 0);

				if (null != mPopImage) {
					MyAnimationUtils.showBottomView(mPopImage, mPopImageView, Register3Activity.this);

				}

				mSnapshotButton = (Button) findViewById(R.id.btn_image_snapshot);
				mAlbumButton = (Button) findViewById(R.id.btn_image_album);
				mCancelButton = (Button) findViewById(R.id.btn_image_cancel);

				mSnapshotButton.setOnClickListener(Register3Activity.this);
				mAlbumButton.setOnClickListener(Register3Activity.this);
				mCancelButton.setOnClickListener(Register3Activity.this);
			}
		});

		mNickName = (EditText) this.findViewById(R.id.edt_register_3_nickname);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mNickName, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300);

		mNickName.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void afterTextChanged(Editable arg0) {
				if (null == temp) {
					return;
				}
				selectionStart = mNickName.getSelectionStart();
				selectionEnd = mNickName.getSelectionEnd();
				if (temp.length() > 0) {
					mHasNickName = true;
					if (temp.length() > 10) {
						arg0.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						mNickName.setText(arg0);
						mNickName.setSelection(tempSelection);
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_nickname_max_error));
					}
				} else {
					mHasNickName = false;
				}
				if (mHasNickName) {
					if (null != mTitleBar) {
						mTitleBar.mRight.setEnabled(true);
					}
				} else {
					if (null != mTitleBar) {
						mTitleBar.mRight.setEnabled(false);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				temp = arg0;

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == Snapshot_Action_Id) {
				String imageFile = "";
				if (null != mSnapshot) {
					DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);

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

			} else if (requestCode == Clip_Action_Id) {
				Bitmap bitmap = data.getExtras().getParcelable("data");
				ImageUtils.saveBitmapToFile(bitmap, FileUtils.AVATAR, 90);
				Bitmap bitmap2 = ImageUtils.roundCorners(bitmap, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
				mImage.setImageBitmap(bitmap2);
				mHasAvatar = true;
				ImageUtils.recycleBitmap(bitmap);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_image_snapshot:
			if (null != mPopImage) {
				MyAnimationUtils.hideBottomView(mPopImage, mPopImageView, Register3Activity.this);
			}
			String strImgPath = FileUtils.COVER;
			File out = new File(strImgPath);
			if (!out.exists()) {
				out.mkdirs();
			}
			String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
			String photoFilePath = strImgPath + fileName;
			if (null != mSnapshot) {
				mSnapshot.takePhoto(this, photoFilePath, Snapshot_Action_Id, Snapshot.AVATAR_PERCENT);
			}
			break;
		case R.id.btn_image_album:
			if (null != mPopImage) {
				MyAnimationUtils.hideBottomView(mPopImage, mPopImageView, Register3Activity.this);
			}
			Intent album = new Intent(Intent.ACTION_GET_CONTENT);
			album.setType("image/*");
			startActivityForResult(album, Photos_Action_Id);
			break;
		case R.id.btn_image_cancel:
			if (null != mPopImage) {
				MyAnimationUtils.hideBottomView(mPopImage, mPopImageView, Register3Activity.this);
			}
			if (null != mNickName) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mNickName, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
			break;
		}

	}

	private void submit() {
		// 隐藏软键盘
		AndroidConfig.hiddenInput(this, mNickName);

		// 保存用户注册信息
		RegisterObject regObject = CoreModel.getInstance().getRegisterObject();
		if (regObject != null) {
			regObject.setNickname(mNickName.getEditableText().toString());
			if (mHasAvatar) {
				regObject.setImage(FileUtils.AVATAR);
			}
			regObject.setLat(String.valueOf(App.getInstance().getLocater().getLat()));
			regObject.setLng(String.valueOf(App.getInstance().getLocater().getLng()));
		}
		// 发送注册请求
		sendEmptyMessage(YSMSG.REQ_REG_USER_ACCOUNT);
		showWaitingDialog2();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_REG_USER_ACCOUNT: {
			dismissWaitingDialog2();
			if (msg.arg1 == 200) {
				// 注册成功
				App.getInstance().clearActivity();
				Intent intent = new Intent(Register3Activity.this, MainActivity.class);
				intent.putExtra("fromregist", true);
				startActivity(intent);
				finish();
			} else {
				// 注册失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		}
	}
	
	public void showWaitingDialog2() {
		if (null == mWaitingDialog2) {
			mWaitingDialog2 = new YSWaitingDialog2(this);
		}

		if (!mWaitingDialog2.isShowing()) {
			mWaitingDialog2.show();
		}
	}
	
	public void dismissWaitingDialog2() {

		if (null != mWaitingDialog2 && mWaitingDialog2.isShowing()) {
			mWaitingDialog2.dismiss();
			mWaitingDialog2 = null;
		}
	}
}
