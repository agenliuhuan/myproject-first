package mobi.dlys.android.familysafer.ui.main;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.clue.MyClueFragment;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.slidingmenu.lib.SlidingMenu.OnOpenListener;
import mobi.dlys.android.familysafer.ui.communication.TidingFragment;
import mobi.dlys.android.familysafer.ui.family.FamilyRequestFragment;
import mobi.dlys.android.familysafer.ui.family.MyFamilyFragment;
import mobi.dlys.android.familysafer.ui.notification.NotificationFragment;
import mobi.dlys.android.familysafer.ui.setting.AccountFragment;
import mobi.dlys.android.familysafer.ui.setting.SettingFragment;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.NotificationHelper;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainMenuListFragment extends BaseExFragment implements OnClickListener {

    private AccountFragment mAccountFragment = null;
    private MyFamilyFragment mMyFamilyFragment = null;
    private MySOSFragment mSOSFragment = null;
    private FamilyRequestFragment mFamilyRequestFragment = null;
    private NotificationFragment mMyNotificationFragment = null;
    private MyClueFragment mMyClueFragment = null;
    private SettingFragment mSettingFragment = null;
    private TidingFragment mTidingsFragment = null;
    private TextView notificationNumTV;
    private TextView msgNumTV;
    private TextView requestNumTV;
    private ImageView userimg;
    private TextView username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.list_main_menu, container, false);

        ((MainActivity) getActivity()).getSlidingMenu().setOnOpenListener(new OnOpenListener() {

            @Override
            public void onOpen() {
                if (CoreModel.getInstance().ismChangeUserImg()) {
                    sendEmptyMessage(YSMSG.REQ_GET_USER_INFO);
                    // if (userid != 0) {
                    //
                    // }
                }
                if (CoreModel.getInstance().getFriendReqCount() > 0) {
                    updateFriendRequestStatus(CoreModel.getInstance().getFriendReqCount());
                } else {
                    updateFriendRequestStatus(0);
                }
            }
        });
        initView();

        return mRootView;
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_USER_INFO:
            if (msg.arg1 == 200) {
                CoreModel.getInstance().setmChangeUserImg(false);
                if (msg.obj instanceof UserObject) {
                    UserObject mUser = (UserObject) msg.obj;
                    if (mUser != null && mUser.getUserId() == CoreModel.getInstance().getUserId()) {
                        if (!TextUtils.isEmpty(mUser.getImage())) {
                            ImageLoaderHelper.displayImage(mUser.getImage(), userimg, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
                        } else {
                            userimg.setImageResource(R.drawable.user);
                        }
                        username.setText(mUser.getNickname());
                    }
                }
            }
            break;
        }
    }

    private void initView() {
        RelativeLayout userRL = (RelativeLayout) findViewById(R.id.main_menu_user);
        LinearLayout myfamilyLL = (LinearLayout) findViewById(R.id.main_menu_myfamily);
        LinearLayout sosLL = (LinearLayout) findViewById(R.id.main_menu_mysosvoice);
        LinearLayout myclueLL = (LinearLayout) findViewById(R.id.main_menu_myclue);
        RelativeLayout homeRL = (RelativeLayout) findViewById(R.id.main_menu_home);
        RelativeLayout notificationRL = (RelativeLayout) findViewById(R.id.main_menu_notification);
        RelativeLayout msgRL = (RelativeLayout) findViewById(R.id.main_menu_msg);
        RelativeLayout requestRL = (RelativeLayout) findViewById(R.id.main_menu_request);
        LinearLayout settingLL = (LinearLayout) findViewById(R.id.main_menu_setting);
        notificationNumTV = (TextView) findViewById(R.id.main_menu_notification_numtv);
        msgNumTV = (TextView) findViewById(R.id.main_menu_msg_numtv);
        requestNumTV = (TextView) findViewById(R.id.main_menu_request_numtv);
        userRL.setOnClickListener(this);
        myfamilyLL.setOnClickListener(this);
        sosLL.setOnClickListener(this);
        myclueLL.setOnClickListener(this);
        homeRL.setOnClickListener(this);
        notificationRL.setOnClickListener(this);
        msgRL.setOnClickListener(this);
        requestRL.setOnClickListener(this);
        settingLL.setOnClickListener(this);

        userimg = (ImageView) findViewById(R.id.main_menu_user_img);
        username = (TextView) findViewById(R.id.main_menu_user_name);

        int userid = CoreModel.getInstance().getUserId();
        if (userid != 0) {
            sendMessage(YSMSG.REQ_GET_USER_INFO, userid, 0, null);
        }
    }

    public void updateFriendRequestStatus(int num) {
        if (num != 0 && requestNumTV != null) {
            requestNumTV.setVisibility(View.VISIBLE);
        } else {
            requestNumTV.setVisibility(View.GONE);
        }
    }

    public void updateEventCount(int num) {
        if (num != 0 && notificationNumTV != null) {
            notificationNumTV.setVisibility(View.VISIBLE);
            notificationNumTV.setText(num + "");
        } else {
            notificationNumTV.setVisibility(View.GONE);
        }
    }

    public void updateMsgCount(int num) {
        if (num != 0 && requestNumTV != null) {
            msgNumTV.setVisibility(View.VISIBLE);
            msgNumTV.setText(num + "");
        } else {
            msgNumTV.setVisibility(View.GONE);
        }
    }

    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                activity.switchContent(fragment);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Fragment newContent = null;
        switch (v.getId()) {
        case R.id.main_menu_user:
            if (null == mAccountFragment) {
                mAccountFragment = new AccountFragment();
            }
            newContent = mAccountFragment;
            break;
        case R.id.main_menu_myfamily:
            if (null == mMyFamilyFragment) {
                mMyFamilyFragment = new MyFamilyFragment();
            }
            newContent = mMyFamilyFragment;
            break;
        case R.id.main_menu_mysosvoice:
            if (null == mSOSFragment) {
                mSOSFragment = new MySOSFragment();
            }
            newContent = mSOSFragment;
            break;
        case R.id.main_menu_myclue:
            if (null == mMyClueFragment) {
                mMyClueFragment = new MyClueFragment();
            }
            newContent = mMyClueFragment;
            break;
        case R.id.main_menu_home:
            newContent = null;
            break;
        case R.id.main_menu_notification:
            if (null == mMyNotificationFragment) {
                mMyNotificationFragment = new NotificationFragment();
            }
            if (CoreModel.getInstance().getEventCount() > 0) {
                CoreModel.getInstance().setUpdateEventList(true);
            }
            newContent = mMyNotificationFragment;
            mMyNotificationFragment.clearEventCount();
            updateEventCount(0);
            break;
        case R.id.main_menu_msg:
            NotificationHelper.getInstance().cancelAllNotification();
            if (null == mTidingsFragment) {
                mTidingsFragment = new TidingFragment();
            }
            if (CoreModel.getInstance().getMsgCount() > 0) {
                CoreModel.getInstance().setUpdateMsgList(true);
            }
            newContent = mTidingsFragment;
            break;
        case R.id.main_menu_request:
            if (null == mFamilyRequestFragment) {
                mFamilyRequestFragment = new FamilyRequestFragment();
            }
            newContent = mFamilyRequestFragment;
            if (CoreModel.getInstance().getFriendReqCount() > 0) {
                CoreModel.getInstance().setUpdateFriendList(true);
                CoreModel.getInstance().setUpdateFriendRequestList(true);
                BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_GET_FRIEND_LIST);
            }
            mFamilyRequestFragment.clearFriendReqCount();
            updateFriendRequestStatus(0);
            break;
        case R.id.main_menu_setting:
            if (null == mSettingFragment) {
                mSettingFragment = new SettingFragment();
            }
            newContent = mSettingFragment;
            break;
        }
        switchFragment(newContent);
    }

}
