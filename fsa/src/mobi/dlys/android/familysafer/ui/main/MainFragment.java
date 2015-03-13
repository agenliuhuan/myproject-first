package mobi.dlys.android.familysafer.ui.main;

import java.util.List;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.audio.Recorder;
import mobi.dlys.android.familysafer.baidumapsdk.BaiduLocListener;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.checkin.CheckinActivity;
import mobi.dlys.android.familysafer.ui.checkin.FirstCheckInActivity;
import mobi.dlys.android.familysafer.ui.clue.ClueActivity;
import mobi.dlys.android.familysafer.ui.clue.FirstStartClueActivity;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder2;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.slidingmenu.lib.SlidingMenu.OnClosedListener;
import mobi.dlys.android.familysafer.ui.family.AddFamily1Activity;
import mobi.dlys.android.familysafer.ui.location.FamilyLocationsActivity;
import mobi.dlys.android.familysafer.ui.main.MainActivity.OnFragmentTouchListener;
import mobi.dlys.android.familysafer.ui.notification.ClueNotificationActivity;
import mobi.dlys.android.familysafer.ui.notification.NotificationDetail1Activity;
import mobi.dlys.android.familysafer.ui.notification.NotificationDetail2Activity;
import mobi.dlys.android.familysafer.ui.sos.MapSOSActivity;
import mobi.dlys.android.familysafer.ui.sos.VoiceSOSActivity;
import mobi.dlys.android.familysafer.utils.NoticePlayer;
import mobi.dlys.android.familysafer.utils.VibratorManager;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class MainFragment extends BaseExFragment implements BaiduLocListener {

    protected TitleBarHolder2 mTitleBar;

    private LinearLayout mPopGuide = null;

    private LinearLayout mPopGuide2 = null;

    private Button mGuideButton = null;

    private MainActivity.OnFragmentTouchListener mOnFragmentTouchListener;

    private RelativeLayout mLayoutRecord;

    private TextView mRecTip1 = null;
    private TextView mRecTip11 = null;
    private TextView mRecTip211 = null;
    private TextView mRecTip111 = null;
    private TextView mRecTip2 = null;

    private RelativeLayout mFamilylocate = null;
    private RelativeLayout mCheckin = null;
    private RelativeLayout mClue = null;

    private RelativeLayout mLayoutSOSRed;
    private ImageView mImgSOSRedBig;
    private ImageView mImageSOS;

    static int MapSOS_Action_Id = 10090;
    static int VoiceSOS_Action_Id = 10091;

    private ImageView mRecoding = null;
    private AnimationDrawable mRecodingAni = null;

    private String mVoicePath;
    private Recorder mRecorder;

    private boolean isShow = false;
    private Animation topoutanim;
    private Animation topinanim;
    private boolean mIsInLayoutRecord = false;
    private boolean mIsAutoSend = false;

    private TextView mMyLocation = null;
    private ImageView mLocationStatus = null;
    private ImageView mLocationStatus2 = null;
    private TextView mLocated = null;
    private Animation mLocatingAni = null;
    private boolean bFirst = true;

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPopGuide.isShown()) {
                hideUpView(mPopGuide, mPopGuide2, getActivity());
                return true;
            }
            return false;
        }
        return false;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnFragmentTouchListener = new OnFragmentTouchListener() {

            @Override
            public boolean onTouch(MotionEvent event) {

                if (!mIsAning && mLayoutRecord.getVisibility() != View.VISIBLE) {
                    return false;
                }

                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (inRangeOfView(mLayoutRecord, event)) {
                        showSOSButtonStatus(2);
                        mRecTip2.setBackgroundResource(R.drawable.img_red_canncel);
                        mRecTip2.setText(R.string.fragment_main_recode_tv_tip_3);
                        mIsInLayoutRecord = true;

                    } else {
                        mRecTip2.setBackgroundResource(0);
                        mRecTip2.setText(R.string.fragment_main_recode_tv_tip_2);
                        showSOSButtonStatus(1);
                        mIsInLayoutRecord = false;
                    }
                    return true;

                case MotionEvent.ACTION_UP: {
                    mIsAning = false;
                    stopRecord();

                    if (null != mRecodingAni) {
                        if (mRecodingAni.isRunning()) {
                            mRecodingAni.stop();
                        }
                    }
                    handler.removeCallbacks(runnable);

                    if (mIsInLayoutRecord) {
                        if (null != mLayoutRecord) {
                            hideUpView(mLayoutRecord, false);
                        }
                        showSOSButtonStatus(0);
                        YSToast.showToast(getActivity().getApplicationContext(), getString(R.string.toast_sos_voice_canceled));

                    } else {
                        if (false == mIsAutoSend) {
                            if (mTickCount <= 0) {
                                if (null != mLayoutRecord) {
                                    hideUpView(mLayoutRecord, false);
                                }
                                showSOSButtonStatus(0);
                                YSToast.showToast(getActivity().getApplicationContext(), getString(R.string.toast_sos_voice_fail));
                            } else {
                                Intent intent = new Intent(getActivity(), VoiceSOSActivity.class);
                                intent.putExtra(VoiceSOSActivity.EXTRA_VOICE_PATH, mVoicePath);
                                intent.putExtra(VoiceSOSActivity.EXTRA_VOICE_DURATION, mTickCount);
                                startActivityForResult(intent, VoiceSOS_Action_Id);
                            }
                        }
                    }

                }
                    return false;
                }
                return true;
            }
        };

        ((MainActivity) getActivity()).registerMyOnTouchListener(mOnFragmentTouchListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDetach() {
        super.onDetach();

        ((MainActivity) getActivity()).unregisterMyOnTouchListener(mOnFragmentTouchListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main, null);

        initSubView();

        initData();
        return mRootView;
    }

    int mTickCount = 0;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (1 == msg.what) {
                if (null != mRecTip1 && null != mRecTip11 && null != mRecTip211 && null != mRecTip111) {
                    if (mTickCount >= 0) {
                        if (mTickCount >= 50 && mTickCount < 60) {
                            mRecTip1.setText(getString(R.string.fragment_main_recode_tv_tip_11));
                            if (View.GONE == mRecTip211.getVisibility()) {
                                mRecTip211.setVisibility(View.VISIBLE);
                            }
                            if (View.VISIBLE == mRecTip11.getVisibility()) {
                                mRecTip11.setVisibility(View.GONE);
                            }
                            mRecTip211.setText(String.valueOf(60 - mTickCount));

                        } else {
                            mRecTip1.setText(getString(R.string.fragment_main_recode_tv_tip_1));

                            if (View.VISIBLE == mRecTip211.getVisibility()) {
                                mRecTip211.setVisibility(View.GONE);
                            }
                            if (View.GONE == mRecTip11.getVisibility()) {
                                mRecTip11.setVisibility(View.VISIBLE);
                            }
                            mRecTip11.setText(String.valueOf(mTickCount));
                        }
                    }
                }
            }
        }
    };

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
            mTickCount++;

            if (mTickCount >= 60) {
                mIsAutoSend = true;

                Intent intent = new Intent(getActivity(), VoiceSOSActivity.class);
                intent.putExtra(VoiceSOSActivity.EXTRA_VOICE_PATH, mVoicePath);
                intent.putExtra(VoiceSOSActivity.EXTRA_VOICE_DURATION, mTickCount);
                startActivityForResult(intent, VoiceSOS_Action_Id);
            } else if (mTickCount >= 0) {
                mIsAutoSend = false;
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void initSubView() {
        mTitleBar = new TitleBarHolder2(getActivity(), mRootView);
        mTitleBar.mLeft2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).toggle();
            }
        });

        mImgSOSRedBig = (ImageView) this.findViewById(R.id.img_main_sos_red_big);
        mImageSOS = (ImageView) findViewById(R.id.img_main_sos_red);
        // anim
        topoutanim = AnimationUtils.loadAnimation(getActivity(), R.anim.view_translate_top_out);
        topinanim = AnimationUtils.loadAnimation(getActivity(), R.anim.view_translate_top_in);

        mLayoutSOSRed = (RelativeLayout) findViewById(R.id.layout_main_sos_red);
        mLayoutSOSRed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(getActivity(), AnalyticsHelper.index_sos_click);
                    }
                }, 1000);
                Intent intent = new Intent(getActivity(), MapSOSActivity.class);
                startActivityForResult(intent, MapSOS_Action_Id);

            }
        });

        mLayoutSOSRed.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                AnalyticsHelper.onEvent(getActivity(), AnalyticsHelper.index_sos_longclick);

                // start to record
                mVoicePath = Recorder.getVoiceFilePath();
                if (!startRecord(mVoicePath)) {
                    YSToast.showToast(getActivity(), R.string.fragment_main_recode_fail);
                    return true;
                }

                VibratorManager.startVibrator(getActivity());
                if (null != mRecodingAni) {
                    mRecodingAni.setOneShot(false);
                    if (mRecodingAni.isRunning()) {
                        mRecodingAni.stop();
                    }
                    mRecodingAni.start();
                }

                showUpView(mLayoutRecord);
                mTickCount = 0;
                if (mTickCount >= 0) {
                    mRecTip1.setText(getString(R.string.fragment_main_recode_tv_tip_1));
                    if (View.VISIBLE == mRecTip211.getVisibility()) {
                        mRecTip211.setVisibility(View.GONE);
                    }
                    if (View.GONE == mRecTip11.getVisibility()) {
                        mRecTip11.setVisibility(View.VISIBLE);
                    }
                    mRecTip11.setText(String.valueOf(mTickCount));

                }
                mRecTip2.setText(R.string.fragment_main_recode_tv_tip_2);
                showSOSButtonStatus(1);

                handler.postDelayed(runnable, 1000);

                return true;

            }
        });

        mFamilylocate = (RelativeLayout) this.findViewById(R.id.layout_family);
        mFamilylocate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(getActivity(), AnalyticsHelper.index_family_locate);
                    }
                }, 1000);

                Intent intent = new Intent(getActivity(), FamilyLocationsActivity.class);
                getActivity().startActivity(intent);

                return;

            }
        });

        mCheckin = (RelativeLayout) this.findViewById(R.id.layout_checkin);
        mCheckin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(getActivity(), AnalyticsHelper.index_checkin);
                    }
                }, 1000);
                SharedPreferences userInfo = getActivity().getSharedPreferences("application_user_info", 0);
                boolean isCheckInFirst = userInfo.getBoolean("checkin_first_run_" + AndroidConfig.getVersionName(), true);
                isCheckInFirst = true;
                if (isCheckInFirst) {
                    userInfo.edit().putBoolean("checkin_first_run_" + AndroidConfig.getVersionName(), false).commit();
                    Intent intent = new Intent(getActivity(), FirstCheckInActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), CheckinActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });

        mClue = (RelativeLayout) this.findViewById(R.id.layout_clue);
        mClue.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(getActivity(), AnalyticsHelper.index_click_shotsnap);
                    }
                }, 1000);

                SharedPreferences userInfo = getActivity().getSharedPreferences("application_user_info", 0);
                boolean isClueFirst = userInfo.getBoolean("clue_first_run_" + AndroidConfig.getVersionName(), true);
                isClueFirst = true;
                if (isClueFirst) {
                    userInfo.edit().putBoolean("clue_first_run_" + AndroidConfig.getVersionName(), false).commit();
                    Intent intent = new Intent(getActivity(), FirstStartClueActivity.class);
                    intent.putExtra("firststartclue", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ClueActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "takephoto");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        mPopGuide = (LinearLayout) findViewById(R.id.layout_main_guide);
        mPopGuide2 = (LinearLayout) findViewById(R.id.layout_main_guide2);
        mPopGuide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                hideBottomView(mPopGuide, mPopGuide2, getActivity());
            }
        });
        mPopGuide2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideBottomView(mPopGuide, mPopGuide2, getActivity());
            }
        });

        mGuideButton = (Button) findViewById(R.id.btn_main_guide_add);

        mGuideButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideBottomView(mPopGuide, mPopGuide2, getActivity());
                Intent intent = new Intent(getActivity(), AddFamily1Activity.class);
                getActivity().startActivity(intent);
            }

        });

        mLocated = (TextView) findViewById(R.id.tv_main_located);
        mLocationStatus = (ImageView) findViewById(R.id.img_main_location);
        mLocationStatus2 = (ImageView) findViewById(R.id.img_main_location2);
        mMyLocation = (TextView) findViewById(R.id.tv_main_location);

        mLocationStatus.setVisibility(View.VISIBLE);
        mLocationStatus2.setVisibility(View.GONE);
        mLocated.setVisibility(View.GONE);

        mLocationStatus.setImageResource(R.drawable.img_locating);
        mLocatingAni = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mLocatingAni.setDuration(1000);
        mLocatingAni.setRepeatCount(Animation.INFINITE);
        mLocatingAni.setRepeatMode(Animation.RESTART);
        mLocatingAni.setInterpolator(new LinearInterpolator());

        mLocationStatus.setAnimation(mLocatingAni);
        mLocatingAni.startNow();

        String tip = getActivity().getResources().getString(R.string.fragment_main_tv_locating);
        mMyLocation.setText(tip);

        mLayoutRecord = (RelativeLayout) findViewById(R.id.layout_main_recode);

        mRecTip1 = (TextView) findViewById(R.id.tv_main_recode_tip_1);
        mRecTip11 = (TextView) findViewById(R.id.tv_main_recode_tip_11);
        mRecTip211 = (TextView) findViewById(R.id.tv_main_recode_tip2_11);
        mRecTip111 = (TextView) findViewById(R.id.tv_main_recode_tip_111);
        mRecTip2 = (TextView) findViewById(R.id.tv_main_recode_tip_2);

        mRecTip211.setVisibility(View.GONE);
        mRecTip11.setVisibility(View.VISIBLE);

        mRecoding = (ImageView) findViewById(R.id.img_main_recode_tip);
        if (null == mRecodingAni) {
            mRecodingAni = (AnimationDrawable) mRecoding.getBackground();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (CoreModel.getInstance().getFriendList().size() < 0) {
                sendEmptyMessage(YSMSG.REQ_GET_FRIEND_LIST);
            }
            showSOSButtonStatus(0);
            displayTip();
        } else {
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        showSOSButtonStatus(0);
    }

    @Override
    public void onPause() {
        super.onPause();

        stopAnimation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        App.getInstance().getLocater().removeBaiduLocListener(this);
        handler.removeCallbacks(connectserver);
        super.onDestroy();
    }

    private void hideUpView(final View view, final boolean startAct) {
        view.startAnimation(topoutanim);

        topoutanim.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation arg0) {

            }

            public void onAnimationRepeat(Animation arg0) {

            }

            public void onAnimationEnd(Animation arg0) {
                view.setVisibility(View.GONE);
                if (startAct) {
                    Intent intent = new Intent(getActivity(), ClueActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "takephoto");
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            }
        });

    }

    public void hideUpView(final View rootview, final View v, final Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_top_out);
        v.startAnimation(anim);

        anim.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation arg0) {

            }

            public void onAnimationRepeat(Animation arg0) {

            }

            public void onAnimationEnd(Animation arg0) {
                v.setVisibility(View.GONE);
            }
        });

        Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_out);
        rootview.startAnimation(anim2);
        rootview.setVisibility(View.GONE);
    }

    public void hideBottomView(final View rootview, final View v, final Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_bottom_out);
        v.startAnimation(anim);

        anim.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation arg0) {

            }

            public void onAnimationRepeat(Animation arg0) {

            }

            public void onAnimationEnd(Animation arg0) {
                v.setVisibility(View.GONE);
            }
        });

        Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_out);
        rootview.startAnimation(anim2);
        rootview.setVisibility(View.GONE);
    }

    public void showUpView(final View rootview, final View v, final Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_top_in);
        v.startAnimation(anim);
        v.setVisibility(View.VISIBLE);

        Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_in);
        rootview.startAnimation(anim2);
        rootview.setVisibility(View.VISIBLE);
    }

    public void showBottomView(final View rootview, final View v, final Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_bottom_in);
        v.startAnimation(anim);
        v.setVisibility(View.VISIBLE);

        Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_in);
        rootview.startAnimation(anim2);
        rootview.setVisibility(View.VISIBLE);
    }

    private boolean mIsAning = false;

    private void showUpView(final View view) {
        view.startAnimation(topinanim);
        topinanim.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation arg0) {
                mIsAning = true;
            }

            public void onAnimationRepeat(Animation arg0) {

            }

            public void onAnimationEnd(Animation arg0) {

                view.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        App.getInstance().getLocater().addBaiduLocListener(this);
        handler.postDelayed(connectserver, 1000);
        if (CoreModel.getInstance().getFriendList().size() < 0) {
            sendEmptyMessage(YSMSG.REQ_GET_FRIEND_LIST);
        }

        ((MainActivity) getActivity()).getSlidingMenu().setOnClosedListener(new OnClosedListener() {

            @Override
            public void onClosed() {

            }
        });

        displayTip();

        final boolean isReg = getActivity().getIntent().getBooleanExtra("fromregist", false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences userInfo = getActivity().getSharedPreferences("application_user_info", 0);
                boolean isGuideRun = userInfo.getBoolean("sos_guide_run", true);
                if ((CoreModel.getInstance().getFriendList().size() == 0 && isGuideRun) || isReg) {
                    if (isGuideRun) {
                        userInfo.edit().putBoolean("sos_guide_run", false).commit();
                    }
                    showBottomView(mPopGuide, mPopGuide2, getActivity());
                }
            }
        }, 2000);

    }

    private boolean startRecord(String voiceFilePath) {
        if (TextUtils.isEmpty(voiceFilePath)) {
            return false;
        }
        if (null == mRecorder) {
            mRecorder = new Recorder();
        }

        if (null != mRecorder) {
            mRecorder.startRecord(voiceFilePath);
            return true;
        }
        return false;
    }

    private void stopRecord() {
        if (null != mRecorder) {
            mRecorder.stopRecord();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_NEW_MSG_NUM:
            if (msg.arg1 == 200) {
                // successs
                displayTip();

                if (CoreModel.getInstance().getNewSOSCount() > 0) {
                    sendMessage(YSMSG.REQ_GET_NEW_EVENT_LIST, 0, 0, PageInfoObjectDao.getFirstPageInfo(PageInfoObjectDao.ID_EVENT_FRIEND_SOS));
                    isShow = true;
                } else if (CoreModel.getInstance().getNewEventClueCount() > 0) {
                    sendMessage(YSMSG.REQ_GET_NEW_EVENT_LIST, 0, 0, PageInfoObjectDao.getFirstPageInfo(PageInfoObjectDao.ID_EVENT_CLUE));
                    isShow = true;
                } else if (CoreModel.getInstance().getNewCheckinCount() > 0) {
                    sendMessage(YSMSG.REQ_GET_NEW_EVENT_LIST, 0, 0, PageInfoObjectDao.getFirstPageInfo(PageInfoObjectDao.ID_EVENT_FRIEND_CHECKIN));
                    isShow = true;
                } else if (CoreModel.getInstance().getNewConfirmCount() > 0) {
                    sendMessage(YSMSG.REQ_GET_NEW_EVENT_LIST, 0, 0, PageInfoObjectDao.getFirstPageInfo(PageInfoObjectDao.ID_EVENT_CONFIRM));
                    isShow = true;
                }
            }
            break;
        case YSMSG.RESP_GET_NEW_EVENT_LIST:
            if (msg.arg1 == 200) {
                // successs
                if (isShow) {
                    if (null != mLayoutRecord) {
                        if (mLayoutRecord.getVisibility() != View.VISIBLE) {
                            List<EventObjectEx> list = (List<EventObjectEx>) msg.obj;
                            if (null != list && list.size() > 0) {
                                handler.removeCallbacks(connectserver);
                                startNotificationAct(list.get(0));
                            }
                        }
                    }
                    isShow = false;
                }

            } else {
                handler.postDelayed(connectserver, 1000);
            }
            break;
        }
    }

    public void displayTip() {
        mTitleBar.displayTip();
    }

    int CHECK_NEW_MSG_SECOND = 10000;
    private Runnable connectserver = new Runnable() {
        public void run() {

            sendEmptyMessage(YSMSG.REQ_GET_NEW_MSG_NUM);
            handler.postDelayed(this, CHECK_NEW_MSG_SECOND);
        }
    };

    /**
     * 显示通知对话框
     * 
     * @param eventObject
     */
    private void show2EventDialog(final EventObjectEx eventObject, Context context, boolean systemAlert) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_two_button, null);
        if (view != null && eventObject != null) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(context, view, systemAlert, false);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.dialog_title_tip));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            if (eventObject.getType() == 1) {
                FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(eventObject.getUserId());
                String content = "";
                if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                    content = fo.getRemarkName();
                } else {
                    content = eventObject.getNickname();
                }
                if (!TextUtils.isEmpty(eventObject.getContent().getMsg())) {
                    content = content + " : " + eventObject.getContent().getMsg();
                } else {
                    content = content + " : " + getString(R.string.wo) + getString(R.string.activity_checkin_notify_familyLL_Tip6);
                }
                txtContent.setText(content);
            }
            if (eventObject.getType() == 5) {
                FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(eventObject.getUserId());
                if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                    txtContent.setText(fo.getRemarkName() + getString(R.string.dialog_content_clue));
                } else {
                    txtContent.setText(eventObject.getNickname() + getString(R.string.dialog_content_clue));
                }
            }
            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
            btnConfirm.setText(getString(R.string.dialog_confim_check));
            btnCancel.setText(getString(R.string.activity_sentsos_btn_know));
            btnConfirm.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    handler.postDelayed(connectserver, 1000);
                    VibratorManager.wakeDownAndUnlock(getActivity());
                    dialog.cancel();
                    if (eventObject.getType() == 1 && eventObject.getContent() != null && !eventObject.isConfirmed()) {
                        sendMessage(YSMSG.REQ_CHECK_IN_CONFIRM, eventObject.getEventId(), eventObject.getContent().id, null);
                        eventObject.setConfirmed();
                        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
                            ActivityUtils.moveTaskToFront(getActivity());
                        }
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), NotificationDetail2Activity.class);
                        intent.putExtra("eventid", eventObject.getEventId());
                        intent.putExtra(NotificationDetail1Activity.EXTRA_EVENT_OBJECT, eventObject);
                        startActivity(intent);
                    }
                    if (eventObject.getType() == 5) {
                        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
                            ActivityUtils.moveTaskToFront(getActivity());
                        }
                        Intent intent = new Intent(getActivity(), ClueNotificationActivity.class);
                        startActivity(intent);
                    }
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    handler.postDelayed(connectserver, 1000);
                    VibratorManager.wakeDownAndUnlock(getActivity());
                    dialog.cancel();
                    if (eventObject.getContent() != null && !eventObject.isConfirmed()) {
                        sendMessage(YSMSG.REQ_CHECK_IN_CONFIRM, eventObject.getEventId(), eventObject.getContent().id, null);
                        eventObject.setConfirmed();
                    }
                }
            });
            dialog.show();
        }
    }

    private void show1EventDialog(final EventObjectEx eventObject, Context context, boolean systemAlert) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_one_button, null);
        if (view != null && eventObject != null) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(context, view, systemAlert, false);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.dialog_title_tip));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            if (eventObject.getType() == 2) {
                FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(eventObject.getUserId());
                if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                    txtContent.setText(fo.getRemarkName() + getString(R.string.dialog_content_arrivte_confim));
                } else {
                    txtContent.setText(eventObject.getNickname() + getString(R.string.dialog_content_arrivte_confim));
                }
                btnConfirm.setText(getString(R.string.activity_sentsos_btn_know));
            }
            if (eventObject.getType() == 4) {
                FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(eventObject.getUserId());
                if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                    txtContent.setText(fo.getRemarkName() + getString(R.string.dialog_content_getsos_confim));
                } else {
                    txtContent.setText(eventObject.getNickname() + getString(R.string.dialog_content_getsos_confim));
                }
                btnConfirm.setText(getString(R.string.activity_sentsos_btn_know));
            }
            if (eventObject.getType() == 3) {
                FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(eventObject.getUserId());
                if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                    txtContent.setText(fo.getRemarkName() + getString(R.string.dialog_content_getsos));
                } else {
                    txtContent.setText(eventObject.getNickname() + getString(R.string.dialog_content_getsos));
                }
                btnConfirm.setText(getString(R.string.dialog_confim_check));
            }
            btnConfirm.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    handler.postDelayed(connectserver, 1000);
                    VibratorManager.wakeDownAndUnlock(getActivity());
                    NoticePlayer.stop();
                    VibratorManager.stopVibrator(getActivity());
                    mHandler.removeCallbacks(VibratorRun);
                    dialog.cancel();
                    if (eventObject.getType() == 3) {
                        if (eventObject.getContent() != null && !eventObject.isConfirmed()) {
                            sendMessage(YSMSG.REQ_VOICE_SOS_CONFIRM, eventObject.getEventId(), eventObject.getContent().id, null);
                            eventObject.setConfirmed();
                        }
                        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
                            ActivityUtils.moveTaskToFront(getActivity());
                        }

                        Intent intent = new Intent();
                        intent.setClass(getActivity(), NotificationDetail1Activity.class);
                        intent.putExtra("eventid", eventObject.getEventId());
                        intent.putExtra(NotificationDetail1Activity.EXTRA_EVENT_OBJECT, eventObject);
                        startActivity(intent);
                    }
                }
            });
            dialog.show();
        }
    }

    int vibratorcount = 0;
    Runnable VibratorRun = new Runnable() {
        public void run() {
            if (vibratorcount == 3) {
                mHandler.removeCallbacks(this);
            } else {
                vibratorcount++;
                mHandler.postDelayed(stopVibratorRun, 1000 * 20);
                VibratorManager.wakeUpAndUnlock(getActivity());
                VibratorManager.startVibrator(getActivity(), true);
                NoticePlayer.playA(getActivity());
                mHandler.postDelayed(this, 1000 * 60 * 5);
            }
        }
    };

    Runnable stopVibratorRun = new Runnable() {
        public void run() {
            VibratorManager.stopVibrator(getActivity());
            VibratorManager.wakeDownAndUnlock(getActivity());
            NoticePlayer.stop();
        }
    };

    private void startNotificationAct(EventObjectEx eo) {
        String miUIName = VibratorManager.getSystemProperty("ro.miui.ui.version.name");
        if (!TextUtils.isEmpty(miUIName)) {
            if (ActivityUtils.isAppInForeground(App.getInstance().getApplicationContext())) {
                if (eo.getType() == 3) {
                    mHandler.post(VibratorRun);
                } else {
                    VibratorManager.startVibrator(getActivity(), false);
                    VibratorManager.wakeUpAndUnlock(getActivity());
                    NoticePlayer.playB(getActivity());
                }
                if (eo.getType() == 1 || eo.getType() == 5) {
                    show2EventDialog(eo, App.getInstance().getForegroundActivity(), false);
                } else {
                    show1EventDialog(eo, App.getInstance().getForegroundActivity(), false);
                }
            } else {
                if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 13) {
                    ActivityUtils.moveTaskToFront(getActivity());
                }
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EventObject", eo);
                startActivity(intent);
            }
        } else {
            if (eo.getType() == 3) {
                mHandler.post(VibratorRun);
            } else {
                VibratorManager.startVibrator(getActivity(), false);
                VibratorManager.wakeUpAndUnlock(getActivity());
                NoticePlayer.playB(getActivity());
            }

            if (eo.getType() == 1 || eo.getType() == 5) {
                show2EventDialog(eo, App.getInstance().getApplicationContext(), true);
            } else {
                show1EventDialog(eo, App.getInstance().getApplicationContext(), true);
            }

        }
    }

    @Override
    public void onLocationChanged(boolean result, ReverseGeoCodeResult geoResult) {
        mLocatingAni.cancel();
        mLocationStatus.clearAnimation();
        mLocationStatus.setAnimation(null);

        mLocationStatus.setVisibility(View.GONE);
        mLocationStatus2.setVisibility(View.VISIBLE);

        if (false == result) {
            mLocated.setVisibility(View.GONE);

            mLocationStatus2.setImageResource(R.drawable.img_location_fail2);
            String tip = getActivity().getResources().getString(R.string.fragment_main_tv_location_fail);
            mMyLocation.setText(tip);
        } else {

            String addr = App.getInstance().getLocater().getAddress2();
            if (TextUtils.isEmpty(addr)) {
                mLocated.setVisibility(View.GONE);

                mLocationStatus2.setImageResource(R.drawable.img_location_fail2);
                String tip = getActivity().getResources().getString(R.string.fragment_main_tv_location_fail);
                mMyLocation.setText(tip);
            } else {
                mLocated.setVisibility(View.VISIBLE);
                mLocationStatus2.setImageResource(R.drawable.img_location2);
                mMyLocation.setText(addr);
                if (bFirst) {
                    bFirst = false;
                    App.getInstance().getLocater().setLocationLow();
                }
            }

        }
    }

    /**
     * 判断坐标是否某个View内
     * 
     * @param view
     * @param ev
     * @return
     */
    private boolean inRangeOfView(View view, MotionEvent ev) {
        if (view == null || ev == null) {
            return false;
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MapSOS_Action_Id) {

        } else if (requestCode == VoiceSOS_Action_Id) {
            if (null != mLayoutRecord) {
                mLayoutRecord.setVisibility(View.GONE);
            }
            showSOSButtonStatus(0);
        }

    }

    /**
     * 显示sos按钮
     * 
     * @param status
     *            0: 默认 1:按下 2:取消
     */
    private void showSOSButtonStatus(int status) {
        switch (status) {
        case 0: {
            mHandler.removeCallbacks(AnimRunable);
            mHandler.postDelayed(AnimRunable, 500);
        }
            break;
        case 1: {
        }
            break;
        case 2: {
        }
            break;

        default:
            break;
        }
    }

    private Runnable AnimRunable = new Runnable() {

        @Override
        public void run() {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.sos_button_anim_set);
            mImgSOSRedBig.startAnimation(animation);
            mImgSOSRedBig.setVisibility(View.VISIBLE);

        }
    };

    private void stopAnimation() {
        mHandler.removeCallbacks(AnimRunable);
        mImgSOSRedBig.clearAnimation();
        mImgSOSRedBig.setVisibility(View.INVISIBLE);
    }
}
