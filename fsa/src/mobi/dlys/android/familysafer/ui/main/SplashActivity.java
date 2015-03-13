package mobi.dlys.android.familysafer.ui.main;

import java.util.List;

import mobi.dlys.android.core.mvc.BaseActivity;
import mobi.dlys.android.core.utils.LogUtils;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.api.PPNetManager;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.LoginObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.HostInfo;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.HostType;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.login.LoginActivity;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import mobi.dlys.android.familysafer.utils.UpdateVersionUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

public class SplashActivity extends BaseActivity {
    private static int ENTER_MINI_TIME = 3 * 1000;
    private static int ENTER_TIMEOUT = 20 * 1000;
    private static int DEFAULT_TIME_OUT = 5 * 1000;
    private long time = 0L;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 后台运行
        Intent intent = getIntent();
        if (null != intent) {
            boolean move = intent.getBooleanExtra("movetasktoback", false);
            LogUtils.i("movetasktoback", String.valueOf(move));
            if (move) {
            }
        }

        setContentView(R.layout.activity_splash);

        PPNetManager.getInstance().setConnectTimeOut(DEFAULT_TIME_OUT);
        PPNetManager.getInstance().setSoTimeOut(DEFAULT_TIME_OUT);
        sendEmptyMessage(YSMSG.REQ_GET_SERVER_DYNAMIC_IP);
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {

        }
        return versionName;
    }

    private boolean isLoginOnNewVersion = false;

    private void initData() {
        PPNetManager.getInstance().resetTimeOut();
        String appFirst = "app_first_run_" + getAppVersionName(this.getApplicationContext());
        SharedPreferences userInfo = getSharedPreferences("application_user_info", 0);
        boolean isAppFirst = userInfo.getBoolean(appFirst, true);
        if (isAppFirst) {
            userInfo.edit().putBoolean(appFirst, false).commit();
            String phone = PreferencesUtils.getLoginPhone();
            String pwd = PreferencesUtils.getLoginPwd();
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
                isLoginOnNewVersion = true;
                LoginObject loginObject = new LoginObject();
                loginObject.setPhone(phone);
                loginObject.setPassword(pwd);
                CoreModel.getInstance().setLoginObject(loginObject);
                sendEmptyMessage(YSMSG.REQ_LOGIN);
                time = System.currentTimeMillis();
            } else {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                        startActivity(intent);
                        finish();
                        UpdateVersionUtils.firstCheckVersion();
                    }
                }, 2 * 1000);
            }
        } else {
            String phone = PreferencesUtils.getLoginPhone();
            String pwd = PreferencesUtils.getLoginPwd();
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
                // 自动登录
                LoginObject loginObject = new LoginObject();
                loginObject.setPhone(phone);
                loginObject.setPassword(pwd);
                CoreModel.getInstance().setLoginObject(loginObject);
                sendEmptyMessage(YSMSG.REQ_LOGIN);
                time = System.currentTimeMillis();

            } else {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        GuideActivity.startActivity(SplashActivity.this);
                        finish();
                        UpdateVersionUtils.firstCheckVersion();
                    }
                }, 2 * 1000);
            }
        }
    }

    private Runnable startRun = new Runnable() {
        public void run() {
            startMain();
        }
    };

    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_SERVER_DYNAMIC_IP: {
            if (msg.arg1 == 200 && msg.obj instanceof FamilySaferPb) {
                FamilySaferPb pb = (FamilySaferPb) msg.obj;
                if (pb.getEnvironment() != null) {
                    List<HostInfo> hostInfoList = pb.getEnvironment().getHostInfosList();
                    if (hostInfoList != null && hostInfoList.size() > 0) {
                        HostInfo hostInfo = null;
                        for (int i = 0; i < hostInfoList.size(); i++) {
                            hostInfo = hostInfoList.get(i);
                            if (hostInfo != null) {
                                if (hostInfo.getHostType() == HostType.API) {
                                    PPNetManager.getInstance().setIp(hostInfo.getDomain());
                                    PPNetManager.getInstance().setPort(hostInfo.getPort());
                                } else if (hostInfo.getHostType() == HostType.RES && !TextUtils.isEmpty(hostInfo.getDomain())) {
                                    PPNetManager.getInstance().setUploadIpPort(hostInfo.getDomain() + ":" + hostInfo.getPort());
                                }
                            }
                        }
                    }
                }
            }

            initData();
        }
            break;
        case YSMSG.RESP_LOGIN: {
            mHandler.removeCallbacks(startRun);
            if (msg.arg1 == 200) {
                // 登录成功
                mHandler.removeCallbacks(startRun);
                long curtime = System.currentTimeMillis();
                long dfsdtime = curtime - time;
                if (dfsdtime < ENTER_MINI_TIME) {
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            startMain();

                        }
                    }, ENTER_MINI_TIME - dfsdtime);
                } else {
                    startMain();
                }
            } else {
                // 登录失败
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(this, result.getErrorMsg());
                } else {
                    YSToast.showToast(this, R.string.network_error);
                }
                LoginActivity.startActivity(SplashActivity.this);
                finish();
                UpdateVersionUtils.firstCheckVersion();
            }
        }
            break;
        }
    }

    private void startMain() {
        if (isLoginOnNewVersion) {
            Intent intent = new Intent(SplashActivity.this, Guide2Activity.class);
            intent.putExtra("LoginOnNewVersion", true);
            startActivity(intent);
            SplashActivity.this.finish();
            UpdateVersionUtils.firstCheckVersion();
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
            UpdateVersionUtils.firstCheckVersion();
        }
    }
}
