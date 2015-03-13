package cn.changl.safe360.android.ui.message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.vo.MessageObject;
import cn.changl.safe360.android.db.dao.MessageObjectDao;
import cn.changl.safe360.android.map.BaiduLoc;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.utils.ImageLoaderHelper;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.UserInfo;

public class MessageActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;
	ListView mListView;
	List<MessageObject> mList;
	RelativeLayout messagenoRL;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, MessageActivity.class);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		initView();

	}

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_message_titile);

		mListView = (ListView) findViewById(R.id.messageXList);
		messagenoRL = (RelativeLayout) findViewById(R.id.message_nullRL);

		mList = new ArrayList<MessageObject>();
	}

	protected void onResume() {
		super.onResume();
		initData();
	}

	private void initData() {
		// Safe360MessageDataBase database = new
		// Safe360MessageDataBase(getApplicationContext());
		mList.clear();
		MessageObjectDao msgDao = new MessageObjectDao();
		if (msgDao.count() > 0) {
			for (MessageObject msg : msgDao.findAll()) {
				mList.add(msg);
			}
		}
		// database.close();
		Comparator<MessageObject> contactsComparator = new Comparator<MessageObject>() {
			public int compare(MessageObject f1, MessageObject f2) {
				if (f1 == null || f2 == null) {
					if (f1 == null) {
						return -1;
					} else {
						return 1;
					}
				} else {
					String time1 = f1.getTime();
					String time2 = f2.getTime();
					long l1 = parseString(time1);
					long l2 = parseString(time2);
					return l1 < l2 ? 1 : -1;
				}
			}
		};
		Collections.sort(mList, contactsComparator);
		if (mList != null && mList.size() != 0) {
			MsgAdapter adapter = new MsgAdapter();
			mListView.setAdapter(adapter);
			messagenoRL.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		} else {
			messagenoRL.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}

	long previousTime = 0;

	class MsgAdapter extends BaseAdapter {
		public int getCount() {
			return mList.size();
		}

		public MessageObject getItem(int position) {
			return mList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			MessageObject msgObj = mList.get(position);
			boolean show = true;
			if (position != 0) {
				MessageObject premsgObj = mList.get(position - 1);
				long preTime = parseString(premsgObj.getTime());
				long curTime = parseString(msgObj.getTime());
				long jTime = preTime - curTime;
				LogUtils.i("time", jTime + "");
				if (jTime < 1000 * 60 * 10) {
					show = false;
				}
			}
			if (msgObj.getType() == 1) {
				convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_message_sos, null);
				TextView datetv = (TextView) convertView.findViewById(R.id.item_message_sos_date);
				ImageView userimg = (ImageView) convertView.findViewById(R.id.item_message_sos_user_img);
				TextView contentTV = (TextView) convertView.findViewById(R.id.item_message_sos_contenttv);
				ImageView contentimg = (ImageView) convertView.findViewById(R.id.item_message_sos_contentimg);
				String mapurl = "http://api.map.baidu.com/staticimage?center=" + msgObj.getLng() + "," + msgObj.getLat() + "&width=" + 500 + "&height=" + 300
						+ "&zoom=19";
				ImageLoaderHelper.displayImage(mapurl, contentimg, R.drawable.default_account_image, false);
				datetv.setText(msgObj.getTime());
				if (show) {
					datetv.setVisibility(View.VISIBLE);
				} else {
					datetv.setVisibility(View.GONE);
				}
				contentTV.setText(msgObj.getDescription());
				try {
					JSONObject json = new JSONObject(msgObj.getData());
					String userid = json.getString("userId");
					UserInfo info = CoreModel.getInstance().getFriendByUserid(Integer.valueOf(userid));
					ImageLoaderHelper.displayImage(info.getImage(), userimg, R.drawable.default_account_image, true);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
				}
				OnClick listener = new OnClick();
				listener.setPosition(position);
				convertView.setOnClickListener(listener);
			}
			if (msgObj.getType() == 2) {
				convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_message_text, null);
				TextView datetv = (TextView) convertView.findViewById(R.id.item_message_text_date);
				TextView contentTV = (TextView) convertView.findViewById(R.id.item_message_text_content);
				datetv.setText(msgObj.getTime());
				if (show) {
					datetv.setVisibility(View.VISIBLE);
				} else {
					datetv.setVisibility(View.GONE);
				}
				contentTV.setText(msgObj.getDescription());
			}
			return convertView;
		}

		class OnClick implements OnClickListener {
			int position;

			public void setPosition(int position) {
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				MessageObject msgObj = mList.get(position);
				if (msgObj != null) {
					Intent intent = new Intent(MessageActivity.this, MessageDetailActivity.class);
					intent.putExtra("messageId", msgObj.getId());
					startActivity(intent);
				}
				// try {
				// JSONObject json = new JSONObject(msgObj.getData());
				// UserObject user = new UserObject();
				// user.setUserId(json.getInt("userId"));
				// user.setLat(json.getString("lat"));
				// user.setLng(json.getString("lng"));
				// Intent intent = new Intent(MessageActivity.this,
				// MessageDetailActivity.class);
				// intent.putExtra("userinfo", user);
				// startActivity(intent);
				// } catch (JSONException e) {
				// e.printStackTrace();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }

			}
		}
	}

	private long parseString(String s) {
		SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = myFmt2.parse(s);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
