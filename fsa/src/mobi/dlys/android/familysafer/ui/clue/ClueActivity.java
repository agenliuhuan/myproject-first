package mobi.dlys.android.familysafer.ui.clue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.core.utils.ImageUtils;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.vo.ClueImageObject;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.camera.Snapshot;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.utils.FileUtils;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.ResUtils;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ClueActivity extends BaseExActivity {
    String mTakePhoto = "takephoto";

    TitleBarHolder mTitleBar;
    Button mUploadButton = null;

    GridView mGrid = null;

    ClueAdapter mAdapter = null;

    ArrayList<ClueImageObject> mClueImageList = null;

    ClueObject mClueObject = null;

    TextView mLocation = null;
    EditText mContentEdt;

    Snapshot mSnapshot = null;

    ClueObject mClueItem = null;
    ImageView mClueImage = null;
    private int Snapshot_Action_Id = 10088;
    private int Viewer_Action_Id = 10089;

    boolean firstTakePhoto = false;
    CheckBox NotifyFamilyCBox;
    boolean isNotifyFamily = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue);

        intiView();
    }

    void intiView() {
        mTitleBar = new TitleBarHolder(this);
        mTitleBar.mTitle.setText(R.string.activity_clue_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mRight.setVisibility(View.INVISIBLE);

        mTitleBar.mTitle.setText(ResUtils.getString(R.string.activity_clue_ttb_title));
        mTitleBar.mTitle.setTextColor(getResources().getColor(R.color.title_green_line));
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                exitConfirm();
            }
        });
        mUploadButton = (Button) findViewById(R.id.btn_clue_upload);
        mUploadButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(ClueActivity.this, AnalyticsHelper.index_upload_netmon);
                    }
                }, 1000);

                uploadClue();
            }
        });

        mContentEdt = (EditText) findViewById(R.id.edt_clue_content);
        mContentEdt.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                temp = arg0;
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            public void afterTextChanged(Editable arg0) {
                selectionStart = mContentEdt.getSelectionStart();
                selectionEnd = mContentEdt.getSelectionEnd();
                if (temp.length() > 200) {
                    arg0.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    mContentEdt.setText(arg0);
                    mContentEdt.setSelection(tempSelection);
                    YSToast.showToast(getApplicationContext(), getString(R.string.toast_clue_edit_length_error));
                }
            }
        });

        mGrid = (GridView) findViewById(R.id.gv_clue_images);
        mClueImageList = new ArrayList<ClueImageObject>();
        addTakePhoto();
        mAdapter = new ClueAdapter(this, mClueImageList);
        mGrid.setAdapter(mAdapter);

        mGrid.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent event) {
                return MotionEvent.ACTION_MOVE == event.getAction() ? true : false;
            }
        });
        setGridParams(mGrid, mClueImageList.size());
        mLocation = (TextView) findViewById(R.id.tv_clue_location);

        mSnapshot = new Snapshot();
        Intent intent = getIntent();
        if (null != intent) {
            if (null != intent.getExtras()) {
                if (!TextUtils.isEmpty(intent.getExtras().getString("type"))) {
                    if (intent.getExtras().getString("type").equals("takephoto")) {
                        firstTakePhoto = true;
                        takePhoto();
                    }
                }
            }
        }
        mClueObject = new ClueObject();
        NotifyFamilyCBox = (CheckBox) findViewById(R.id.clue_notifyfamily_checkBox);
        NotifyFamilyCBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNotifyFamily = true;
                } else {
                    showNotifyFamilyDialog();
                }

            }
        });
        TextView whasUploadTV = (TextView) findViewById(R.id.activity_clue_whats_upload);
        whasUploadTV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FirstStartClueActivity.class);
                intent.putExtra("firststartclue", false);
                startActivity(intent);
            }
        });
    }

    protected void showNotifyFamilyDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_two_button, null);
        if (view != null) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(ClueActivity.this, view, false, true);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.dialog_title_tip));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            txtContent.setText(getString(R.string.activity_clue_notifyfamily_dialog_content));

            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
            btnConfirm.setText(getString(R.string.activity_clue_notifyfamily_dialog_confirm));
            btnCancel.setText(getString(R.string.activity_clue_notifyfamily_dialog_cancel));
            btnConfirm.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    isNotifyFamily = false;
                    dialog.cancel();
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    isNotifyFamily = true;
                    NotifyFamilyCBox.setChecked(true);
                    dialog.cancel();
                }
            });

            dialog.show();
        }
    }

    private void showDelDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_two_button, null);
        if (view != null) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(ClueActivity.this, view, false, true);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.dialog_title_tip));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            txtContent.setText(getString(R.string.dialog_editclue_content));

            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
            btnConfirm.setText(getString(R.string.dialog_editclue_yes));
            btnCancel.setText(getString(R.string.dialog_editclue_no));
            btnConfirm.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    dialog.cancel();
                    ClueActivity.this.finish();
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

    private void setGridParams(GridView mGrid, int count) {
        int Screenwidth = AndroidConfig.getScreenWidth();
        int imageMargin = (int) getResources().getDimension(R.dimen.image_margin);
        LayoutParams params;
        if (count <= 4) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, (Screenwidth - imageMargin * 5) / 4 + imageMargin);
            params.setMargins(imageMargin, imageMargin, imageMargin, 0);
            mGrid.setLayoutParams(params);
            mGrid.setSelector(R.color.transparent);
            mGrid.setNumColumns(4);
            mGrid.setHorizontalSpacing(imageMargin);
            mGrid.setVerticalSpacing(imageMargin);
        } else {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, (Screenwidth - imageMargin * 5) / 2 + imageMargin * 2);
            params.setMargins(imageMargin, imageMargin, imageMargin, 0);
            mGrid.setLayoutParams(params);
            mGrid.setSelector(R.color.transparent);
            mGrid.setNumColumns(4);
            mGrid.setHorizontalSpacing(imageMargin);
            mGrid.setVerticalSpacing(imageMargin);
        }
    }

    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        mAdapter.mList = mClueImageList;
        mAdapter.notifyDataSetChanged();
        refreshButtonStatus();
        UserObject user = CoreModel.getInstance().getUserInfo();
        if (null != user) {
            mLocation.setText(user.getLocation());
            mClueObject.setLocation(user.getLocation());
            mClueObject.setModel(AndroidConfig.getPhoneModel());
        }

    }

    private void uploadClue() {
        mClueObject.setModel(AndroidConfig.getPhoneModel());
        mClueObject.setMessage(mContentEdt.getEditableText().toString());
        mClueObject.setImageList(mClueImageList);
        if (mClueObject.getImageList() != null) {
            mClueObject.getImageList().remove(mTakePhoto);
        }
        mClueObject.setEvent(isNotifyFamily);
        finish();
        closeInput();
        Intent intent = new Intent(ClueActivity.this, SendClueActivity.class);
        intent.putExtra(SentClueActivity.EXTRA_CLUE_OBJECT, mClueObject);
        startActivity(intent);
    }

    private void closeInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(ClueActivity.this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    void takePhoto() {
        String strImgPath = FileUtils.COVER;
        String imgname = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        if (null != mSnapshot) {
            mSnapshot.takePhoto(ClueActivity.this, strImgPath + imgname, Snapshot_Action_Id, Snapshot.CLUE_PERCENT);
        }
    }

    OnClickListener OnClick_Snapshot = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            mClueImage = (ImageView) arg0;
            String strImgPath = FileUtils.COVER;
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String photoFilePath = strImgPath + fileName;
            if (null != mSnapshot) {
                mSnapshot.takePhoto(ClueActivity.this, photoFilePath, Snapshot_Action_Id, Snapshot.CLUE_PERCENT);
            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Snapshot_Action_Id) {
                firstTakePhoto = false;
                String imageFile = "";
                if (null != mSnapshot) {
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                    imageFile = mSnapshot.onActivityResultProc(Snapshot.CLUE_IMAGE_WIDTH);
                }
                Point pt = ImageUtils.getBitmapWidthAndHeight(imageFile);
                ClueImageObject cb = new ClueImageObject();
                cb.setImage(imageFile);
                cb.setWidth(pt.x);
                cb.setHeight(pt.y);

                mClueImageList.add(mClueImageList.size() - 1, cb);
            }
            if (requestCode == Viewer_Action_Id) {
                mClueImageList.clear();
                List<String> imageList = data.getStringArrayListExtra("mdata");
                for (String imagePath : imageList) {
                    Point pt = ImageUtils.getBitmapWidthAndHeight(imagePath);
                    ClueImageObject cb = new ClueImageObject();
                    cb.setImage(imagePath);
                    cb.setWidth(pt.x);
                    cb.setHeight(pt.y);
                    mClueImageList.add(mClueImageList.size(), cb);
                }
                addTakePhoto();
            }
        } else {
            if (firstTakePhoto) {
                ClueActivity.this.finish();
            }
        }
    }

    private void addTakePhoto() {
        ClueImageObject cb = new ClueImageObject();
        cb.setImage(mTakePhoto);
        mClueImageList.add(cb);
    }

    private void refreshButtonStatus() {
        setGridParams(mGrid, mClueImageList.size());
        if (mClueImageList.size() <= 1) {
            mUploadButton.setClickable(false);
            mUploadButton.setBackgroundResource(R.drawable.btn_disable);
        } else {
            mUploadButton.setClickable(true);
            mUploadButton.setBackgroundResource(R.drawable.button_green_selector);
        }
    }

    public void onBackPressed() {
        exitConfirm();
    }

    private void exitConfirm() {
        if (mClueImageList.size() > 1 || mContentEdt.getText().length() > 0) {
            showDelDialog();
        } else {
            ClueActivity.this.finish();
        }
    }

    private class ClueAdapter extends BaseAdapter {
        private Context mContext;
        public ArrayList<ClueImageObject> mList;

        public ClueAdapter(Context contect, ArrayList<ClueImageObject> list) {
            this.mContext = contect;
            this.mList = list;
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
                    ClueImageObject clueImage = mAdapter.getItem(position);
                    if (clueImage != null) {
                        String path = clueImage.getImage();
                        if (path.equals(mTakePhoto)) {
                            if (mClueImageList.size() == 9) {
                                YSToast.showToast(ClueActivity.this, R.string.activity_clue_img_outof8);
                            } else {
                                takePhoto();
                            }
                        } else {
                            ArrayList<String> mList = new ArrayList<String>();
                            for (int i = 0; i < mClueImageList.size(); i++) {
                                clueImage = mClueImageList.get(i);
                                if (clueImage != null) {
                                    String mClue = clueImage.getImage();
                                    if (!mClue.equals(mTakePhoto)) {
                                        mList.add(mClue);
                                    }
                                }
                            }
                            Intent intent = new Intent(ClueActivity.this, ClueImageActivity.class);
                            intent.putExtra("position", position);
                            intent.putStringArrayListExtra("clues", mList);
                            startActivityForResult(intent, Viewer_Action_Id);
                        }
                    }
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
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 5) / 4, (Screenwidth - imageMargin * 5) / 4);
                        mImage.setLayoutParams(params);
                        mImage.setScaleType(ImageView.ScaleType.CENTER);
                        ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
                    } else {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 5) / 4, (Screenwidth - imageMargin * 5) / 4);
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
