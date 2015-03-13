package mobi.dlys.android.familysafer.ui.main;

import java.util.ArrayList;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.HandlerUtils.MessageListener;
import mobi.dlys.android.core.utils.HandlerUtils.StaticHandler;
import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UpdateObject;
import mobi.dlys.android.familysafer.db.DatabaseManager;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.player.service.PlayerService;
import mobi.dlys.android.familysafer.receiver.ConnectionChangeReceiver;
import mobi.dlys.android.familysafer.service.ReadContactsService;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.slidingmenu.app.BaseSlidingActivity;
import mobi.dlys.android.familysafer.ui.comm.slidingmenu.lib.SlidingMenu;
import mobi.dlys.android.familysafer.ui.comm.slidingmenu.lib.SlidingMenu.OnOpenListener;
import mobi.dlys.android.familysafer.ui.family.AddFamily2Activity;
import mobi.dlys.android.familysafer.utils.NotificationHelper;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import mobi.dlys.android.familysafer.utils.UpdateVersionUtils;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BaseSlidingActivity implements MessageListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1000;

    protected StaticHandler mHandler = new StaticHandler(this);
    private Fragment mMainFragment = null;
    private MainMenuListFragment mMenuFragment = null;
    private Fragment mContentFragment = null;

    private Activity mActivity;

    private ConnectionChangeReceiver mNetworkStateReceiver = new ConnectionChangeReceiver();

    private ArrayList<OnFragmentTouchListener> mOnTouchListeners = new ArrayList<OnFragmentTouchListener>();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (null != mMainFragment && keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMainFragment instanceof MainFragment) {
                if (false == ((MainFragment) mMainFragment).onKeyDown(keyCode, event)) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    return true;
                }
            }

        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CoreModel.getInstance().isLogined()) {
            SplashActivity.startActivity(this);
            finish();
            return;
        }

        mActivity = this;
        initSlidingMenu(savedInstanceState);
        initView();
        initData();

    }

    private void initSlidingMenu(Bundle savedInstanceState) {
        // set the Above View
        if (savedInstanceState != null)
            mContentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if (mContentFragment == null)
            mContentFragment = mMainFragment = new MainFragment();

        if (mMenuFragment == null) {
            mMenuFragment = new MainMenuListFragment();
        }

        // set the Above View
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, mContentFragment).commit();

        // set the Behind View
        setBehindContentView(R.layout.layout_slidingmenu);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_slidingmenu_frame, mMenuFragment).commit();

        // customize the SlidingMenu
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContentFragment);
    }

    public void switchContent(Fragment fragment) {
        if (null == fragment) {
            fragment = mMainFragment;
        }
        if (null == fragment) {
            fragment = mMainFragment = new MainFragment();
        }

        Fragment from = mContentFragment;
        if (!fragment.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
            getSupportFragmentManager().beginTransaction().hide(from).add(R.id.layout_main_frame, fragment).commit();
        } else {
            // 隐藏当前的fragment，显示下一个
            getSupportFragmentManager().beginTransaction().hide(from).show(fragment).commit();
        }
        mContentFragment = fragment;
        getSlidingMenu().showContent();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = false;
        for (OnFragmentTouchListener listener : mOnTouchListeners) {
            if (listener.onTouch(event)) {
                result = true;
            }
        }

        if (result) {
            return true;
        } else {
            return super.dispatchTouchEvent(event);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        App.getInstance().setForegroundActivity(this);
        AnalyticsHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // mHomeWatcher.stopWatch();
        AnalyticsHelper.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BaseController.getInstance().removeOutboxHandler(mHandler);

        // 停止播放
        App.getInstance().getApplicationContext().stopService(new Intent(App.getInstance(), PlayerService.class));

        // 取消网络状态监听
        try {
            unregisterReceiver(mNetworkStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        App.getInstance().getApplicationContext().stopService(new Intent(App.getInstance(), ReadContactsService.class));
        CoreModel.getInstance().release();
        DatabaseManager.getInstance().releaseHelper();
    }

    @Override
    public void onBackPressed() {
        if (isMenuShowing()) {
            moveBackAndNotify();
            return;
        } else {
            if (mMainFragment == mContentFragment) {
                moveBackAndNotify();
                return;
            }
            toggle();
            return;
        }
    }

    private void moveBackAndNotify() {
        moveTaskToBack(true);
        // showNotification(getString(R.string.app_name) +
        // getString(R.string.notification_tip));
    }

    private void initData() {
        BaseController.getInstance().addOutboxHandler(mHandler);

        // 监听SlidingMenu打开
        getSlidingMenu().setOnOpenListener(new OnOpenListener() {

            @Override
            public void onOpen() {
                setFriendRequestStatus(CoreModel.getInstance().getFriendReqCount());
                setEventCount(CoreModel.getInstance().getEventCount() );
                setMsgCount(CoreModel.getInstance().getMsgCount());
            }
        });

        // 显示状态栏通知
        showNotification(getString(R.string.app_name) + getString(R.string.notification_tip_first), true);

        // 注册网络监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);

        /*boolean isReg = getIntent().getBooleanExtra("fromregist", false);
        if (isReg) {
            showAddDialog();
        }*/
    }

    private void showNotification(String message, boolean autoCancel) {
        Intent intent = new Intent(this, MainActivity.class);
        NotificationHelper.getInstance().showOrUpdateNotification(NOTIFICATION_ID, message, getString(R.string.app_name), message, -1, true, intent);
        if (autoCancel) {
            NotificationHelper.getInstance().cancelNotification(NOTIFICATION_ID);
        }
    }

    public void registerMyOnTouchListener(OnFragmentTouchListener onTouchListener) {
        mOnTouchListeners.add(onTouchListener);
    }

    public void unregisterMyOnTouchListener(OnFragmentTouchListener onTouchListener) {
        mOnTouchListeners.remove(onTouchListener);
    }

    public interface OnFragmentTouchListener {
        public boolean onTouch(MotionEvent ev);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_NEW_MSG_NUM: {
            if (msg.arg1 == 200) {
                setFriendRequestStatus(CoreModel.getInstance().getFriendReqCount());
                setEventCount(CoreModel.getInstance().getEventCount());
                setMsgCount(CoreModel.getInstance().getMsgCount());
            }
        }
            break;
        case YSMSG.RESP_CHECK_VERSION: {
            UpdateVersionUtils.handleMessage(mActivity, msg);
        }
            break;
        case YSMSG.RESP_LOGIN: {
            if (msg.arg1 == 200) {
                BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_GET_FRIEND_LIST);
            } else {
                // 登录失败
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(this, result.getErrorMsg());
                } else {
                    YSToast.showToast(this, R.string.network_error);
                }
            }
        }
            break;
        case YSMSG.RESP_SHOW_NEW_MSG_NOTIFICATION: {
            if (!ActivityUtils.isAppInForeground(this) || !PreferencesUtils.isEnterChatActivity()) {
                showNotification(getString(R.string.notification_tip_msg, CoreModel.getInstance().getMsgCount() + ""), false);
            }
        }
            break;
        }

    }

    private void setFriendRequestStatus(int count) {
        if (mMenuFragment != null) {
            if (count > 0) {
                mMenuFragment.updateFriendRequestStatus(R.drawable.icon_menu_red_point);
            } else {
                mMenuFragment.updateFriendRequestStatus(0);
            }
        }
    }

    private void setEventCount(int count) {
        if (mMenuFragment != null) {
            if (count > 0) {
                if (count > 99) {
                    count = 99;
                }
                mMenuFragment.updateEventCount(count);
            } else {
                mMenuFragment.updateEventCount(0);
            }
        }
    }

    public void setMsgCount(int count) {
        if (mMenuFragment != null) {
            if (count > 0) {
                if (count > 99) {
                    count = 99;
                }
                mMenuFragment.updateMsgCount(count);
            } else {
                mMenuFragment.updateMsgCount(0);
            }
        }
    }

    private void showUpdateDialog(final UpdateObject updateObject) {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_two_button, null);
        if (view != null && updateObject != null) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(this, view, false, true);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.update_title));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            txtContent.setText(updateObject.getUpdateLog());

            final boolean forceUpdate = updateObject.forceUpdate();

            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);

            btnConfirm.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    downloadPackege(updateObject.getDownloadUrl());
                    dialog.cancel();
                    if (forceUpdate) {
                        App.getInstance().exitApp();
                    }
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dialog.cancel();
                    if (forceUpdate) {
                        App.getInstance().exitApp();
                    }
                }
            });

            dialog.show();
        }
    }

    private void showAddDialog() {
        View view = this.getLayoutInflater().inflate(R.layout.dialog_two_button, null);
        if (view != null) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(this, view, false, true);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.update_title));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            txtContent.setText(getString(R.string.activity_registered_tv_tip_3));

            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);

            btnConfirm.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dialog.cancel();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AnalyticsHelper.onEvent(MainActivity.this, AnalyticsHelper.index_add_family);
                        }
                    }, 1000);
                    Intent intent = new Intent(MainActivity.this, AddFamily2Activity.class);
                    startActivity(intent);
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }
    }

    private void downloadPackege(String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
            this.startActivity(downloadIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        mContentFragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    };
}
