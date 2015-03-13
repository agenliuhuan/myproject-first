package mobi.dlys.android.familysafer.ui.family;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FamilyIn2Activity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	Button mAddFamily = null;
	Button mAccept = null;
	Button mReject = null;
	int mUserId = 0;
	UserObject mUser;
	ImageView mUserImage;
	TextView nicknameTv;
	TextView remarkName;

	@Override
	protected void onCreate(Bundle outState) {
		super.onCreate(outState);
		setContentView(R.layout.activity_familyin);

		initView();
		initData();
	}

	private void initData() {
		mUserId = getIntent().getIntExtra("extra_user_id", 0);

		if (mUserId != 0) {
			sendMessage(YSMSG.REQ_GET_USER_INFO, mUserId, 0, null);
			showWaitingDialog();
		}
		boolean isReceive = getIntent().getBooleanExtra("isreceive", false);
		if (isReceive) {
			mAddFamily.setVisibility(View.GONE);
			mAccept.setVisibility(View.VISIBLE);
			mReject.setVisibility(View.VISIBLE);
		} else {
			mAddFamily.setVisibility(View.VISIBLE);
			mAccept.setVisibility(View.GONE);
			mReject.setVisibility(View.GONE);
		}
	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_familyin_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				FamilyIn2Activity.this.finish();
			}
		});
		mAddFamily = (Button) this.findViewById(R.id.btn_familyin_add);
		mReject = (Button) this.findViewById(R.id.btn_familyin_reject);
		mAccept = (Button) this.findViewById(R.id.btn_familyin_accept);

		mUserImage = (ImageView) findViewById(R.id.img_familyin_image);
		nicknameTv = (TextView) findViewById(R.id.tv_familyin_nickname);
		remarkName = (TextView) findViewById(R.id.tv_familyin_name);

		mAddFamily.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(null!=AddFamily2Activity.contacts){
					AddFamily2Activity.contacts.setType(5);
				}
				if (0 != mUserId) {
					sendMessage(YSMSG.REQ_ADD_FRIEND, mUserId, 0, null);
					showWaitingDialog();
				}
			}
		});

		mAccept.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (0 != mUserId) {
					sendMessage(YSMSG.REQ_AGREE_FRIEND_REQUEST, mUserId, 0, null);
					showWaitingDialog();
				}
			}
		});
		mReject.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (0 != mUserId) {
					sendMessage(YSMSG.REQ_REFUSE_FRIEND_REQUEST, mUserId, 0, null);
					showWaitingDialog();
				}
			}
		});

	}

	private void update() {
		if (mUser != null) {
			ImageLoaderHelper.displayImage(mUser.getImage(), mUserImage, R.drawable.user, true);
			String contactname=CoreModel.getInstance().getContactsName(mUser.getPhone());
			if(!TextUtils.isEmpty(contactname)){
				remarkName.setText(getString(R.string.activity_familyin_tv_contact)+contactname);
			}else{
				remarkName.setText(getString(R.string.activity_familyin_tv_isnotcontact));
			}
			nicknameTv.setText(mUser.getNickname());
		}
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_GET_USER_INFO: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// success
				if (msg.obj instanceof UserObject) {
					mUser = (UserObject) msg.obj;
				}
				update();
			} else {
				// failed
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_ADD_FRIEND: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 添加好友成功
				FamilyIn2Activity.this.finish();
				YSToast.showToast(this, R.string.toast_send_add_friend_success);
			} else {
				// 添加好友失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_AGREE_FRIEND_REQUEST: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 同意好友成功
				FamilyIn2Activity.this.finish();
				YSToast.showToast(this, R.string.toast_agree_friend_success);
			} else {
				// 同意好友失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
		}
			break;
		case YSMSG.RESP_REFUSE_FRIEND_REQUEST: {
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 拒绝好友成功
				FamilyIn2Activity.this.finish();
				YSToast.showToast(this, R.string.toast_refuse_friend_success);
			} else {
				// 拒绝好友失败
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
