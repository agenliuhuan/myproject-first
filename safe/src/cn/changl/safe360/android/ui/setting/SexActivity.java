package cn.changl.safe360.android.ui.setting;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.YSMSG;
import cn.changl.safe360.android.biz.vo.ResultObject;
import cn.changl.safe360.android.biz.vo.UserObject;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.ui.comm.YSToast;

public class SexActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;
	ImageView manimg;
	ImageView womanimg;
	int gender;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, SexActivity.class);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sex);

		initView();
		initData();
	}

	private void initData() {
		RelativeLayout manRL = (RelativeLayout) findViewById(R.id.sexmanLL);
		RelativeLayout womanRL = (RelativeLayout) findViewById(R.id.sexwomanLL);
		manimg = (ImageView) findViewById(R.id.sex_manImg);
		womanimg = (ImageView) findViewById(R.id.sex_womanImg);
		manRL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				manimg.setVisibility(View.VISIBLE);
				womanimg.setVisibility(View.INVISIBLE);
				gender = 1;
			}
		});
		womanRL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				manimg.setVisibility(View.INVISIBLE);
				womanimg.setVisibility(View.VISIBLE);
				gender = 2;
			}
		});
		UserObject user = CoreModel.getInstance().getUserInfo().clone();
		if (user != null) {
			gender = user.getGender();
			if (gender == 2) {
				manimg.setVisibility(View.INVISIBLE);
				womanimg.setVisibility(View.VISIBLE);
			} else {
				manimg.setVisibility(View.VISIBLE);
				womanimg.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_sex_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mRight2.setText(R.string.titlebar_button_tip_over);
		mTitleBar.mRight2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				complete();
			}
		});

	}

	private void complete() {
		// 创建临时UserObject
		UserObject user = CoreModel.getInstance().getUserInfo().clone();
		if (user != null) {
			user.setGender(gender);
		}

		// 发送请求
		sendMessage(YSMSG.REQ_MODIFY_USER_INFO, 0, 0, user);
		showWaitingDialog();
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_MODIFY_USER_INFO: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 成功
				YSToast.showToast(this, R.string.toast_sex_modify_succeed);
				finish();
			} else {
				// 失败
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
}
