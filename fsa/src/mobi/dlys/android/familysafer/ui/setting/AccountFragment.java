package mobi.dlys.android.familysafer.ui.setting;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import mobi.dlys.android.core.utils.ImageUtils;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.camera.Snapshot;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.MyAnimationUtils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AccountFragment extends BaseExFragment {

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
    RelativeLayout mPhoneLayout = null;
    RelativeLayout mNicknameLayout = null;
    RelativeLayout mPasswordLayout = null;

    TextView mAccountTxt;
    TextView mPhoneTxt;
    TextView mNicknameTxt;

    ImageView mImage = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_account, null);

        initView();
        initData();
        return mRootView;
    }

    void initView() {
        mTitleBar = new TitleBarHolder(getActivity(), mRootView);
        mTitleBar.mTitle.setText(R.string.activity_account_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).toggle();
            }
        });
        mTitleBar.mRight.setVisibility(View.INVISIBLE);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mSnapshot = new Snapshot();

        mImage = (ImageView) findViewById(R.id.img_account_image);

        mImagePop = (LinearLayout) findViewById(R.id.layout_account_image_pop);
        mImagePopView = (LinearLayout) findViewById(R.id.layout_account_image_pop_view);
        mImagePop.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (null != mImagePop) {
                    MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, getActivity());
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
                    MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, getActivity());
                }

                String strImgPath = FileUtils.COVER;
                File out = new File(strImgPath);
                if (!out.exists()) {
                    out.mkdirs();
                }
                String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
                String photoFilePath = strImgPath + fileName;
                if (null != mSnapshot) {
                    mSnapshot.takePhoto(getActivity(), photoFilePath, Snapshot_Action_Id, Snapshot.AVATAR_PERCENT);
                }
            }

        });

        mAlbumButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (null != mImagePop) {
                    MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, getActivity());
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
                    MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, getActivity());
                }
            }

        });

        mImageLayout = (RelativeLayout) findViewById(R.id.layout_account_image);
        mImageLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MyAnimationUtils.showBottomView(mImagePop, mImagePopView, getActivity());
            }

        });

        mPhoneLayout = (RelativeLayout) findViewById(R.id.layout_account_phone);
        mPhoneLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), Phone1Activity.class);
                startActivity(intent);
            }

        });

        mNicknameLayout = (RelativeLayout) findViewById(R.id.layout_account_nickname);
        mNicknameLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), NicknameActivity.class);
                intent.putExtra("nickname", CoreModel.getInstance().getUserInfo().getNickname());
                startActivity(intent);
            }

        });

        mPasswordLayout = (RelativeLayout) findViewById(R.id.layout_account_newpassword);
        mPasswordLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), PasswordActivity.class);
                startActivity(intent);
            }

        });

        mAccountTxt = (TextView) findViewById(R.id.tv_account_name);
        mPhoneTxt = (TextView) findViewById(R.id.tv_account_phone2);
        mNicknameTxt = (TextView) findViewById(R.id.tv_account_nickname);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Snapshot_Action_Id) {
            String imageFile = "";
            if (null != mSnapshot) {
                DisplayMetrics dm = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

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
            mAccountTxt.setText(user.getUserId() + "");
            mPhoneTxt.setText(user.getPhone());
            mNicknameTxt.setText(user.getNickname());
            ImageLoaderHelper.displayAvatar(user.getImage(), mImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    // @Override
    // public void onBackPressed() {
    // if (mImagePop.getVisibility() == View.VISIBLE) {
    // MyAnimationUtils.hideBottomView(mImagePop, mImagePopView, getActivity());
    // return;
    // }
    // super.onBackPressed();
    // }

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
                YSToast.showToast(getActivity(), R.string.toast_avatar_modify_succeed);
                UserObject user = CoreModel.getInstance().getUserInfo();
                if (user != null) {
                    ImageLoaderHelper.displayAvatar(user.getImage(), mImage);
                }
            } else {
                // 失败
                YSToast.showToast(getActivity(), R.string.toast_avatar_modify_failed);
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(getActivity(), result.getErrorMsg());
                } else {
                    YSToast.showToast(getActivity(), R.string.network_error);
                }
            }
        }
            break;
        }
    }

}