package cn.changl.safe360.android.ui.setting;

import java.util.ArrayList;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.biz.vo.HelperObject;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;
import cn.changl.safe360.android.utils.umeng.FeedbackHelper;

public class HelperActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;
	ListView helperList;
	ArrayList<HelperObject> mList;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, HelperActivity.class);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helper);
		initSubView();
		initData();
	}

	private void initData() {
		HelperObject obj1 = new HelperObject();
		obj1.setTitle(getString(R.string.activity_helper_itemtitle1));
		obj1.setContent(getString(R.string.activity_helper_itemcontent1));
		obj1.setShow(false);
		mList.add(obj1);

		HelperObject obj2 = new HelperObject();
		obj2.setTitle(getString(R.string.activity_helper_itemtitle2));
		obj2.setContent(getString(R.string.activity_helper_itemcontent2));
		obj2.setShow(false);
		mList.add(obj2);

		HelperObject obj3 = new HelperObject();
		obj3.setTitle(getString(R.string.activity_helper_itemtitle3));
		obj3.setContent(getString(R.string.activity_helper_itemcontent3));
		obj3.setShow(false);
		mList.add(obj3);

		HelperObject obj4 = new HelperObject();
		obj4.setTitle(getString(R.string.activity_helper_itemtitle4));
		obj4.setContent(getString(R.string.activity_helper_itemcontent4));
		obj4.setShow(false);
		mList.add(obj4);

		HelperObject obj5 = new HelperObject();
		obj5.setTitle(getString(R.string.activity_helper_itemtitle5));
		obj5.setContent(getString(R.string.activity_helper_itemcontent5));
		obj5.setShow(false);
		mList.add(obj5);

		HelperObject obj6 = new HelperObject();
		obj6.setTitle(getString(R.string.activity_helper_itemtitle6));
		obj6.setContent(getString(R.string.activity_helper_itemcontent6));
		obj6.setShow(false);
		mList.add(obj6);

		HelperObject obj7 = new HelperObject();
		obj7.setTitle(getString(R.string.activity_helper_itemtitle7));
		obj7.setContent(getString(R.string.activity_helper_itemcontent7));
		obj7.setShow(false);
		mList.add(obj7);

		HelperObject obj8 = new HelperObject();
		obj8.setTitle(getString(R.string.activity_helper_itemtitle8));
		obj8.setContent(getString(R.string.activity_helper_itemcontent8));
		obj8.setShow(false);
		mList.add(obj8);

		helperAdapter adpater = new helperAdapter();
		helperList.setAdapter(adpater);
	}

	private void initSubView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_helper_title);

		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		helperList = (ListView) findViewById(R.id.helper_listview);
		mList = new ArrayList<HelperObject>();
	}

	class helperAdapter extends BaseAdapter {

		public int getCount() {
			return mList.size();
		}

		public HelperObject getItem(int position) {
			return mList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			OnClick listener = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_helper, null);
				// 92*92
				holder.titleTV = (TextView) convertView.findViewById(R.id.item_helper_title);
				holder.showOrHideImg = (ImageView) convertView.findViewById(R.id.showOrHideImg);
				holder.contentTV = (TextView) convertView.findViewById(R.id.item_helper_content);
				listener = new OnClick();
				convertView.setTag(holder);
				convertView.setTag(holder.showOrHideImg.getId(), listener);
			} else {
				holder = (ViewHolder) convertView.getTag();
				listener = (OnClick) convertView.getTag(holder.showOrHideImg.getId());

			}
			listener.setPosition(position);
			HelperObject info = mList.get(position);
			holder.titleTV.setText(info.getTitle());
			holder.contentTV.setText(info.getContent());
			holder.showOrHideImg.setOnClickListener(listener);
			convertView.setOnClickListener(listener);
			if (info.isShow()) {
				holder.showOrHideImg.setBackgroundResource(R.drawable.icon_up);
				holder.contentTV.setVisibility(View.VISIBLE);
			} else {
				holder.showOrHideImg.setBackgroundResource(R.drawable.icon_down);
				holder.contentTV.setVisibility(View.GONE);
			}
			return convertView;
		}

		public final class ViewHolder {
			public TextView titleTV;
			public ImageView showOrHideImg;
			public TextView contentTV;
		}

		class OnClick implements OnClickListener {
			int position;

			public void setPosition(int position) {
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				HelperObject info = mList.get(position);
				if (info.isShow()) {
					info.setShow(false);
				} else {
					info.setShow(true);
				}
				helperAdapter adapter = (helperAdapter) helperList.getAdapter();
				adapter.notifyDataSetChanged();
			}
		}
	}

}
