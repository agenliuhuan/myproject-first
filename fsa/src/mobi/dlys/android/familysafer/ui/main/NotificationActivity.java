package mobi.dlys.android.familysafer.ui.main;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.notification.ClueNotificationActivity;
import mobi.dlys.android.familysafer.ui.notification.NotificationDetail1Activity;
import mobi.dlys.android.familysafer.ui.notification.NotificationDetail2Activity;
import mobi.dlys.android.familysafer.utils.NoticePlayer;
import mobi.dlys.android.familysafer.utils.VibratorManager;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationActivity extends BaseExActivity {
    EventObjectEx eventObject;
    Button btnCancel;
    Button btnConfirm;
    TextView txtTitle;
    TextView txtContent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_mainnotification);
        initView();
        initData();

    }

    private void initData() {
        txtTitle.setText(getString(R.string.dialog_title_tip));
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
            btnConfirm.setText(getString(R.string.dialog_confim_check));
            btnCancel.setText(getString(R.string.activity_sentsos_btn_know));
            btnCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    startConnectServer();
                    VibratorManager.wakeDownAndUnlock(getBaseContext());
                    if (eventObject.getContent() != null && !eventObject.isConfirmed()) {
                        sendMessage(YSMSG.REQ_CHECK_IN_CONFIRM, eventObject.getEventId(), eventObject.getContent().id, null);
                        eventObject.setConfirmed();
                    }
                    NotificationActivity.this.finish();
                }
            });
        }
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
        if (eventObject.getType() == 5) {
            FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(eventObject.getUserId());
            if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                txtContent.setText(fo.getRemarkName() + getString(R.string.dialog_content_clue));
            } else {
                txtContent.setText(eventObject.getNickname() + getString(R.string.dialog_content_clue));
            }
            btnConfirm.setText(getString(R.string.dialog_confim_check));
            btnCancel.setText(getString(R.string.activity_sentsos_btn_know));
            btnCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    startConnectServer();
                    VibratorManager.wakeDownAndUnlock(getBaseContext());
                    NotificationActivity.this.finish();
                }
            });
        }
        btnConfirm.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                startConnectServer();
                VibratorManager.wakeDownAndUnlock(getBaseContext());
                NoticePlayer.stop();
                VibratorManager.stopVibrator(getBaseContext());
                mHandler.removeCallbacks(VibratorRun);
                if (eventObject.getType() == 1) {
                    if (eventObject.getContent() != null && !eventObject.isConfirmed()) {
                        sendMessage(YSMSG.REQ_CHECK_IN_CONFIRM, eventObject.getEventId(), eventObject.getContent().id, null);
                        eventObject.setConfirmed();
                    }
                    Intent intent = new Intent();
                    // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    // Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(getBaseContext(), NotificationDetail2Activity.class);
                    intent.putExtra("eventid", eventObject.getEventId());
                    intent.putExtra(NotificationDetail1Activity.EXTRA_EVENT_OBJECT, eventObject);
                    startActivity(intent);
                } else if (eventObject.getType() == 5) {
                    Intent intent = new Intent(getBaseContext(), ClueNotificationActivity.class);
                    startActivity(intent);
                } else {
                    if (eventObject.getType() == 3) {
                        if (eventObject.getContent() != null && !eventObject.isConfirmed()) {
                            sendMessage(YSMSG.REQ_VOICE_SOS_CONFIRM, eventObject.getEventId(), eventObject.getContent().id, null);
                            eventObject.setConfirmed();
                        }
                        Intent intent = new Intent();
                        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        // Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(getBaseContext(), NotificationDetail1Activity.class);
                        intent.putExtra("eventid", eventObject.getEventId());
                        intent.putExtra(NotificationDetail1Activity.EXTRA_EVENT_OBJECT, eventObject);
                        startActivity(intent);
                    }
                }
                NotificationActivity.this.finish();
            }
        });

        if (eventObject.getType() == 3) {
            mHandler.post(VibratorRun);
        } else {
            VibratorManager.startVibrator(getBaseContext(), false);
            VibratorManager.wakeUpAndUnlock(getBaseContext());
            NoticePlayer.playB(getBaseContext());
        }
    }

    public void startConnectServer() {
        mHandler.post(connectserver);
    }

    int CHECK_NEW_MSG_SECOND = 10000;
    private Runnable connectserver = new Runnable() {
        public void run() {
            sendEmptyMessage(YSMSG.REQ_GET_NEW_MSG_NUM);
            mHandler.postDelayed(connectserver, CHECK_NEW_MSG_SECOND);
        }
    };

    private void initView() {
        RelativeLayout mainRL = (RelativeLayout) findViewById(R.id.main_notification_mainRL);
        RelativeLayout contentRL = (RelativeLayout) findViewById(R.id.main_notification_contentRL);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        mainRL.setBackgroundDrawable(wallpaperDrawable);

        if (getIntent() != null) {
            eventObject = (EventObjectEx) getIntent().getSerializableExtra("EventObject");
            if (eventObject != null) {
                int type = eventObject.getType();
                if (type == 1) {
                    View view = getLayoutInflater().inflate(R.layout.dialog_two_button, null);
                    contentRL.addView(view);
                    btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
                    btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
                    txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
                    txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
                } else {
                    View view = getLayoutInflater().inflate(R.layout.dialog_one_button, null);
                    contentRL.addView(view);
                    txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
                    txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
                    btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
                }
            } else {
                finish();
            }
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
                VibratorManager.wakeUpAndUnlock(getBaseContext());
                VibratorManager.startVibrator(getBaseContext(), true);
                NoticePlayer.playA(getBaseContext());
                mHandler.postDelayed(this, 1000 * 60 * 5);
            }
        }
    };

    Runnable stopVibratorRun = new Runnable() {
        public void run() {
            VibratorManager.stopVibrator(getBaseContext());
            VibratorManager.wakeDownAndUnlock(getBaseContext());
            NoticePlayer.stop();
        }
    };
}
