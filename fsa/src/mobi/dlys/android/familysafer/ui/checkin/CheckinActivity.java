package mobi.dlys.android.familysafer.ui.checkin;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLoc;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapListener;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView.MyMarker;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.family.AddFamily2Activity;
import mobi.dlys.android.familysafer.ui.location.LostActivity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.MyAnimationUtils;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiResult;

public class CheckinActivity extends BaseExActivity implements BaiduMapListener, OnClickListener {

    protected TitleBarHolder mTitleBar;
    Button mSendAll = null;
    Button mSendOne = null;

    BaiduMapView mBaiduMapView = null;

    public static int SendCheck_All_Action_Id = 10010;
    public static int SendCheck_Some_Action_Id = 10011;
    public static int SendCheck_Lost_Action_Id = 10012;
    public static int TakeMessage_Action_Id = 10013;

    public static int SendCheck_All_Action_Id_Result = 20010;
    public static int SendCheck_Some_Action_Id_Result = 20011;
    public static int SendCheck_Lost_Action_Id_Result = 20012;

    private View mPopview = null;
    private LinearLayout mLayoutTip = null;
    private LinearLayout mLayoutTip2 = null;
    private TextView tvName = null;
    private TextView tvName2 = null;
    private TextView tvLoc = null;
    private ImageView imgUser = null;

    private ImageView mImageMine = null;
    private ImageView mImageZoomin = null;
    private ImageView mImageZoomout = null;
    private float mZoomLevel = 16;

    private RelativeLayout mLayoutAddr = null;
    private LinearLayout mLayoutAddr2 = null;

    private List<PoiInfo> mPois = null;
    private ListView mListview = null;
    private AddrAdapter mAdapter = null;
    private int mIndex = 0;

    private LinearLayout mListviewFamilies = null;
    ArrayList<FriendObject> mFamiliesList = null;
    ArrayList<Integer> mSelectedList = null;
    private Button mSend = null;
    LinearLayout mPopSendCheck = null;
    LinearLayout mPopSendCheck2 = null;

    private LinearLayout checkinTip1;
    private LinearLayout checkinTip2;
    private LinearLayout checkinTip3;
    private LinearLayout checkinTip4;
    private LinearLayout checkinTip5;
    private LinearLayout checkinTip6;
    private Button takeMessageBtn;
    private TextView takeMessageTV;
    private String tipString;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (null != mLayoutAddr) {
                if (mLayoutAddr.isShown()) {
                    if (null != mLayoutAddr2) {
                        MyAnimationUtils.hideBottomView(mLayoutAddr, mLayoutAddr2, CheckinActivity.this);
                    }

                    return true;
                }
            }
            if (null != mPopSendCheck) {
                if (mPopSendCheck.isShown()) {
                    if (null != mPopSendCheck) {
                        MyAnimationUtils.hideBottomView(mPopSendCheck, mPopSendCheck2, CheckinActivity.this);
                    }
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public class AddrAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public AddrAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mPois.size();
        }

        @Override
        public Object getItem(int arg0) {
            return mPois.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == mInflater) {
                mInflater = LayoutInflater.from(mContext);
            }

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_addr, null);

                holder.imgPick = (ImageView) convertView.findViewById(R.id.img_pick);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);

                holder.tvAddr = (TextView) convertView.findViewById(R.id.tv_addr);

                if (0 == getItemViewType(position)) {
                    holder.tvAddr.setVisibility(View.GONE);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position >= 0 && position < mPois.size()) {
                PoiInfo poi = mPois.get(position);
                if (poi != null) {
                    if (0 == getItemViewType(position)) {
                        holder.tvName.setText(poi.address);
                        holder.tvAddr.setText(poi.name);
                    } else {
                        holder.tvName.setText(poi.name);
                        holder.tvAddr.setText(poi.address);
                    }
                    if (mIndex == position) {
                        holder.imgPick.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgPick.setVisibility(View.GONE);
                    }
                }
            }

            return convertView;
        }

        class ViewHolder {
            public ImageView imgPick;
            public TextView tvName;
            public TextView tvAddr;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        initView();
        initData();
    }

    private void initData() {
        App app = (App) getApplication();
        BaiduLoc locater = app.getLocater();

        if (CoreModel.getInstance().getUserInfo() != null) {
            String tips = getResources().getString(R.string.activity_checkin_tip);
            if (locater != null && !TextUtils.isEmpty(locater.getAddress())) {
                CoreModel.getInstance().getUserInfo().setLocation(locater.getAddress());
            } else {
                CoreModel.getInstance().getUserInfo().setLocation("");
            }
            tvName2.setText(CoreModel.getInstance().getUserInfo().getLocation());
            CoreModel.getInstance().getUserInfo().setLocation3("|" + CoreModel.getInstance().getUserInfo().getLocation());
            mBaiduMapView.setCenter(locater.getLatitude(), locater.getLongitude(), imgUser, CoreModel.getInstance().getUserInfo().getImage(), tips, CoreModel.getInstance().getUserInfo().getLocation());
        }

        mPois.clear();
        PoiInfo pi = new PoiInfo();
        pi.name = "";
        if (CoreModel.getInstance().getUserInfo() != null) {
            pi.address = CoreModel.getInstance().getUserInfo().getLocation();
        }
        mPois.add(pi);

        mIndex = 0;

        if (null != locater.getPois()) {
            mPois.addAll(locater.getPois());
            if (null != mAdapter) {
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    private void showAddFamilyDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_two_button, null);
        if (view != null) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(CheckinActivity.this, view, false, true);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.activity_checkin_addfamily));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            txtContent.setText(getString(R.string.activity_checkin_addfamily_content));

            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
            btnConfirm.setText(R.string.activity_checkin_addfamily_confirm);
            btnCancel.setText(R.string.activity_checkin_addfamily_calcel);
            btnConfirm.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    Intent intent = new Intent(getBaseContext(), AddFamily2Activity.class);
                    startActivity(intent);
                    dialog.cancel();
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

    void initView() {
        mPois = new ArrayList<PoiInfo>();
        mListview = (ListView) this.findViewById(R.id.lv_addr);
        mAdapter = new AddrAdapter(this);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mIndex = arg2;
                if (null != mAdapter) {
                    mAdapter.notifyDataSetChanged();
                }

                if (TextUtils.isEmpty(mPois.get(arg2).name)) {
                    mLayoutTip.setVisibility(View.GONE);
                    mLayoutTip2.setVisibility(View.VISIBLE);
                    tvName2.setText(mPois.get(arg2).address);
                } else {
                    mLayoutTip2.setVisibility(View.GONE);
                    mLayoutTip.setVisibility(View.VISIBLE);
                    tvName.setText(mPois.get(arg2).name);
                    tvLoc.setText(mPois.get(arg2).address);
                }

                if (CoreModel.getInstance().getUserInfo() != null) {
                    mBaiduMapView.clearPoi();
                    App app = (App) getApplication();
                    BaiduLoc locater = app.getLocater();
                    CoreModel.getInstance().getUserInfo().setLocation2(mPois.get(arg2).address);
                    String tips = getResources().getString(R.string.activity_checkin_tip);
                    CoreModel.getInstance().getUserInfo().setLocation3(mPois.get(arg2).name + "|" + mPois.get(arg2).address);
                    mBaiduMapView.setCenter(locater.getLatitude(), locater.getLongitude(), imgUser, CoreModel.getInstance().getUserInfo().getImage(), tips + mPois.get(arg2).name, mPois.get(arg2).address);
                }

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyAnimationUtils.hideBottomView(mLayoutAddr, mLayoutAddr2, CheckinActivity.this);
                    }
                }, 500);

            }
        });
        mLayoutAddr = (RelativeLayout) findViewById(R.id.layout_addr);
        mLayoutAddr2 = (LinearLayout) findViewById(R.id.layout_addr2);
        mLayoutAddr.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MyAnimationUtils.hideBottomView(mLayoutAddr, mLayoutAddr2, CheckinActivity.this);
            }
        });

        mLayoutAddr2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });

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
                    YSToast.showToast(CheckinActivity.this, R.string.toast_map_zoomout_max);
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
                    YSToast.showToast(CheckinActivity.this, R.string.toast_map_zoomin_min);
                }
                mImageZoomin.setEnabled(true);
                mBaiduMapView.setZoomLevel(mZoomLevel);
            }
        });

        mTitleBar = new TitleBarHolder(this);
        mTitleBar.mTitle.setText(R.string.activity_checkin_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mRight.setVisibility(View.INVISIBLE);

        checkinTip1 = (LinearLayout) findViewById(R.id.layout_checkin_tipLL1);
        checkinTip2 = (LinearLayout) findViewById(R.id.layout_checkin_tipLL2);
        checkinTip3 = (LinearLayout) findViewById(R.id.layout_checkin_tipLL3);
        checkinTip4 = (LinearLayout) findViewById(R.id.layout_checkin_tipLL4);
        checkinTip5 = (LinearLayout) findViewById(R.id.layout_checkin_tipLL5);
        checkinTip6 = (LinearLayout) findViewById(R.id.layout_checkin_tipLL6);
        checkinTip1.setOnClickListener(this);
        checkinTip2.setOnClickListener(this);
        checkinTip3.setOnClickListener(this);
        checkinTip4.setOnClickListener(this);
        checkinTip5.setOnClickListener(this);
        checkinTip6.setOnClickListener(this);
        if (CoreModel.getInstance().getUserInfo() != null) {
            String location = CoreModel.getInstance().getUserInfo().getLocation();
            if (TextUtils.isEmpty(location)) {
                location = (String) getResources().getText(R.string.activity_familylocations_none_tip2);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CheckinActivity.this, LostActivity.class);
                        intent.putExtra("rid", SendCheck_Lost_Action_Id_Result);
                        startActivityForResult(intent, SendCheck_Lost_Action_Id);
                    }
                }, 1000);
            }
        }

        mBaiduMapView = new BaiduMapView(this, R.id.layout_checkin_map, true, true);
        mBaiduMapView.setListener(this);
        mPopview = LayoutInflater.from(this).inflate(R.layout.layout_avatatar_pop2, mBaiduMapView.getLayout());
        mLayoutTip = (LinearLayout) mPopview.findViewById(R.id.layout_location);
        mLayoutTip2 = (LinearLayout) mPopview.findViewById(R.id.layout_location2);
        tvName = (TextView) mPopview.findViewById(R.id.tv_checkin_name);
        tvName2 = (TextView) mPopview.findViewById(R.id.tv_checkin_name2);
        tvLoc = (TextView) mPopview.findViewById(R.id.tv_checkin_location);
        mLayoutTip.setVisibility(View.GONE);

        imgUser = (ImageView) mPopview.findViewById(R.id.img_checkin_avatar);

        mPopSendCheck = (LinearLayout) findViewById(R.id.layout_sendcheck_pop);
        mPopSendCheck2 = (LinearLayout) findViewById(R.id.layout_sendcheck_pop2);
        mPopSendCheck.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (null != mPopSendCheck) {
                    if (View.VISIBLE == mPopSendCheck.getVisibility() && View.VISIBLE == mPopSendCheck2.getVisibility()) {
                        MyAnimationUtils.hideBottomView(mPopSendCheck, mPopSendCheck2, CheckinActivity.this);
                    }
                }
            }
        });

        mPopSendCheck2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });
        mListviewFamilies = (LinearLayout) findViewById(R.id.lv_checkin_families);
        mFamiliesList = new ArrayList<FriendObject>();
        mSelectedList = new ArrayList<Integer>();
        mSend = (Button) findViewById(R.id.btn_checkin_send);
        mSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mSelectedList.size() == 0) {
                    YSToast.showToast(CheckinActivity.this, R.string.activity_checkin_ttl);
                    return;
                }
                if (null != mPopSendCheck) {
                    if (View.VISIBLE == mPopSendCheck.getVisibility() && View.VISIBLE == mPopSendCheck2.getVisibility()) {
                        MyAnimationUtils.hideBottomView(mPopSendCheck, mPopSendCheck2, CheckinActivity.this);
                    }
                }
                if (mSelectedList.size() == (mFamiliesList.size() - 1)) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AnalyticsHelper.onEvent(CheckinActivity.this, AnalyticsHelper.index_send_all);
                        }
                    }, 1000);

                    Intent intent = new Intent(CheckinActivity.this, SendCheckActivity.class);
                    intent.putExtra("all", 1);
                    if (!TextUtils.isEmpty(takeMessageTV.getText().toString())) {
                        intent.putExtra("message", tipString + "，" + takeMessageTV.getText().toString());
                    } else {
                        intent.putExtra("message", tipString);
                    }
                    startActivityForResult(intent, SendCheck_All_Action_Id);
                } else {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AnalyticsHelper.onEvent(CheckinActivity.this, AnalyticsHelper.index_send_one);
                        }
                    }, 1000);
                    Intent intent = new Intent(CheckinActivity.this, SendCheckActivity.class);
                    intent.putExtra("all", 0);
                    if (!TextUtils.isEmpty(takeMessageTV.getText().toString())) {
                        intent.putExtra("message", tipString + "，" + takeMessageTV.getText().toString());
                    } else {
                        intent.putExtra("message", tipString);
                    }
                    intent.putIntegerArrayListExtra("data", mSelectedList);
                    startActivityForResult(intent, CheckinActivity.SendCheck_Some_Action_Id);
                }
                finish();
            }
        });

        takeMessageBtn = (Button) findViewById(R.id.layout_sendcheck_takemessageBtn);
        takeMessageTV = (TextView) findViewById(R.id.layout_sendcheck_tv);
        takeMessageBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CheckinActivity.this, TakeMessageActivity.class);
                startActivityForResult(intent, TakeMessage_Action_Id);
            }
        });
        takeMessageTV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CheckinActivity.this, TakeMessageActivity.class);
                intent.putExtra("tipString", takeMessageTV.getText().toString());
                startActivityForResult(intent, TakeMessage_Action_Id);
            }
        });
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
            imgFamily.setTag("family" + position);
            convertView.setTag(i);

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    int iii = (Integer) arg0.getTag();
                    if (iii == 0) {
                        int iSelect = mSelectedList.size();
                        mSelectedList.clear();
                        if ((mFamiliesList.size() - 1) == iSelect) {
                            for (int jj = 1; jj < mFamiliesList.size(); jj++) {
                                ImageView imgKuang1 = (ImageView) mListviewFamilies.findViewWithTag("Kuang" + jj);

                                if (null != imgKuang1) {
                                    imgKuang1.setVisibility(View.INVISIBLE);
                                }

                            }
                        } else {
                            for (int jj = 1; jj < mFamiliesList.size(); jj++) {
                                ImageView imgKuang1 = (ImageView) mListviewFamilies.findViewWithTag("Kuang" + jj);
                                if (null != imgKuang1) {
                                    imgKuang1.setVisibility(View.VISIBLE);
                                }
                                FriendObject fo = mFamiliesList.get(jj);
                                if (null != fo) {
                                    mSelectedList.add(fo.getUserId());
                                }
                            }
                        }
                    } else {
                        ImageView imgKuang1 = (ImageView) mListviewFamilies.findViewWithTag("Kuang" + iii);
                        if (imgKuang1.getVisibility() == View.VISIBLE) {
                            imgKuang1.setVisibility(View.INVISIBLE);
                            int iid = friend.getUserId();
                            for (int ii = 0; ii < mSelectedList.size(); ii++) {
                                if (iid == mSelectedList.get(ii)) {
                                    mSelectedList.remove(ii);
                                    break;
                                }
                            }
                        } else {
                            imgKuang1.setVisibility(View.VISIBLE);
                            int iid = friend.getUserId();
                            boolean isExists = false;
                            for (int ii = 0; ii < mSelectedList.size(); ii++) {
                                if (iid == mSelectedList.get(ii)) {
                                    isExists = true;
                                    break;
                                }
                            }
                            if (!isExists) {
                                mSelectedList.add(iid);
                            }
                        }
                    }
                    if ((mFamiliesList.size() - 1) == mSelectedList.size()) {
                        ImageView imgFamily1 = (ImageView) mListviewFamilies.findViewWithTag("family" + 0);
                        if (null != imgFamily1) {
                            imgFamily1.setImageResource(R.drawable.img_select_click);
                        }
                    } else {
                        ImageView imgFamily1 = (ImageView) mListviewFamilies.findViewWithTag("family" + 0);
                        if (null != imgFamily1) {
                            imgFamily1.setImageResource(R.drawable.img_selectall_normal);
                        }
                    }
                }
            });
            if (i == 0) {
                imgFamily.setImageResource(R.drawable.img_selectall_normal);
                imgKuang.setVisibility(View.INVISIBLE);
                tvName.setVisibility(View.INVISIBLE);
            } else {
                imgKuang.setVisibility(View.INVISIBLE);
                tvName.setVisibility(View.VISIBLE);
                FriendObject fo = friend;
                if (!TextUtils.isEmpty(fo.getRemarkName())) {
                    tvName.setText(fo.getRemarkName());
                } else if (!TextUtils.isEmpty(fo.getNickname())) {
                    tvName.setText(fo.getNickname());
                }
                String imageFile = fo.getImage();
                if (!TextUtils.isEmpty(imageFile)) {
                    ImageLoaderHelper.displayImage(imageFile, imgFamily, R.drawable.user, true);
                } else {
                    imgFamily.setImageResource(R.drawable.user);
                }
            }
            mListviewFamilies.addView(convertView);
        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onMarkerClick(MyMarker mymarker) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnalyticsHelper.onEvent(CheckinActivity.this, AnalyticsHelper.index_address_adapt);
            }
        }, 1000);

        if (mLayoutAddr.getVisibility() == View.GONE) {
            MyAnimationUtils.showBottomView(mLayoutAddr, mLayoutAddr2, CheckinActivity.this);
        } else {
            MyAnimationUtils.hideBottomView(mLayoutAddr, mLayoutAddr2, CheckinActivity.this);
        }
    }

    @Override
    public void onSnapshotReady(Bitmap snapshot) {

    }

    @Override
    public void onSearched(PoiResult result) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SendCheck_All_Action_Id) {
            if (resultCode == SendCheck_All_Action_Id_Result) {
                finish();
            }
        } else if (requestCode == SendCheck_Lost_Action_Id) {
            if (resultCode == SendCheck_Lost_Action_Id_Result) {
                initData();
            }
        } else if (requestCode == TakeMessage_Action_Id) {

            if (data != null) {
                String message = data.getStringExtra("takemessage");
                if (TextUtils.isEmpty(message)) {
                    takeMessageTV.setVisibility(View.GONE);
                    takeMessageBtn.setVisibility(View.VISIBLE);
                } else {
                    takeMessageTV.setText(message);
                    takeMessageTV.setVisibility(View.VISIBLE);
                    takeMessageBtn.setVisibility(View.GONE);
                }

            }
        }
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
    public void OnMapClick() {
    }

    public void onClick(View v) {
        ArrayList<FriendObject> af = (ArrayList<FriendObject>) CoreModel.getInstance().getFriendList();
        if (null != af && af.size() != 0) {
            mFamiliesList.clear();

            FriendObject foDummy = new FriendObject();
            foDummy.setUserId(-1);
            mFamiliesList.add(foDummy);
            mFamiliesList.addAll(af);
            updateList();
        } else {
            showAddFamilyDialog();
            return;
        }
        switch (v.getId()) {
        case R.id.layout_checkin_tipLL1:
            tipString = getString(R.string.wo) + getString(R.string.activity_checkin_notify_familyLLTip1);
            break;
        case R.id.layout_checkin_tipLL2:
            tipString = getString(R.string.wo) + getString(R.string.activity_checkin_notify_familyLLTip2);
            break;
        case R.id.layout_checkin_tipLL3:
            tipString = getString(R.string.wo) + getString(R.string.activity_checkin_notify_familyLLTip3);
            break;
        case R.id.layout_checkin_tipLL4:
            tipString = getString(R.string.wo) + getString(R.string.activity_checkin_notify_familyLLTip4);
            break;
        case R.id.layout_checkin_tipLL5:
            tipString = getString(R.string.wo) + getString(R.string.activity_checkin_notify_familyLLTip5);
            break;
        case R.id.layout_checkin_tipLL6:
            tipString = getString(R.string.wo) + getString(R.string.activity_checkin_notify_familyLL_Tip6);
            break;
        }
        mSend.setText(tipString);
        if (null != mPopSendCheck) {
            MyAnimationUtils.showBottomView(mPopSendCheck, mPopSendCheck2, CheckinActivity.this);
        }
    }

    private LatLng myPosition = new LatLng(0.0, 0.0);
    @Override
    public void OnMapChanged(MapStatus arg0) {
        if (mZoomLevel != arg0.zoom) {
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
                YSToast.showToast(CheckinActivity.this, R.string.toast_map_zoomout_max);
            } else {
                if (mZoomLevel == 3) {
                    mImageZoomin.setEnabled(true);
                    mImageZoomout.setEnabled(false);
                    YSToast.showToast(CheckinActivity.this, R.string.toast_map_zoomin_min);
                } else {
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
                YSToast.showToast(CheckinActivity.this, R.string.toast_map_zoomout_max);
            }
            if (mZoomLevel == 3) {
                YSToast.showToast(CheckinActivity.this, R.string.toast_map_zoomin_min);
            }
        }
    }
}
