package cn.changl.safe360.android.ui.escort;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.ActivityUtils;
import mobi.dlys.android.core.utils.LogUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.changl.safe360.android.App;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionResult.SuggestionInfo;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

public class SetPositionActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;
	Button sureBtn;
	EditText posiEdit;
	SuggestionSearch mSuggestionSearch;
	ListView listview;
	GeoCoder mSearch;
	String mLastResult;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, SetPositionActivity.class);
	}

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setposition);
		initView();
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(listener);
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(serchlistener);
	}

	private void startPlan() {
		LogUtils.i("SetPositionActivity", "startPlan");
		String curcity = App.getInstance().getLocater().getCurCity();
		mSearch.geocode(new GeoCodeOption().city(curcity).address(posiEdit.getEditableText().toString()));
	}

	OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
		public void onGetSuggestionResult(SuggestionResult res) {
			if (res == null || res.getAllSuggestions() == null || res.getAllSuggestions().size() <= 1) {
				if (!listview.getAdapter().isEmpty()) {
//					startPlan();
				}
				return;
			}
			textAdapter adapter = (textAdapter) listview.getAdapter();
			List<SuggestionInfo> infoList = res.getAllSuggestions();
			adapter.infoList = infoList;
			adapter.notifyDataSetChanged();
		}
	};

	OnGetGeoCoderResultListener serchlistener = new OnGetGeoCoderResultListener() {
		public void onGetGeoCodeResult(GeoCodeResult result) {
			LogUtils.i("SetPositionActivity", "serchlistener");
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				LatLng latlng = result.getLocation();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putDouble("lat", latlng.latitude);
				bundle.putDouble("lng", latlng.longitude);
				bundle.putString("adress", result.getAddress());
				intent.putExtras(bundle);
				setResult(20, intent);
				finish();
			} else {
//				YSToast.showToast(mActivity, "没有检索到结果");
			}
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				// 没有找到检索结果
			}
			// 获取反向地理编码结果
		}
	};

	private void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_setposi_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.btn_titlebar_left_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeInput();
				SetPositionActivity.this.finish();
			}
		});

		posiEdit = (EditText) findViewById(R.id.edt_setposition);
		sureBtn = (Button) findViewById(R.id.btn_setposition);

		sureBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startPlan();
			}
		});
		posiEdit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			public void afterTextChanged(Editable s) {
				mLastResult = posiEdit.getEditableText().toString();
				String curcity = App.getInstance().getLocater().getCurCity();
				mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(posiEdit.getEditableText().toString()).city(curcity));
			}
		});
		listview = (ListView) findViewById(R.id.setposition_List);
		List<SuggestionInfo> infoList = new ArrayList<SuggestionResult.SuggestionInfo>();
		textAdapter adapter = new textAdapter(infoList);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				textAdapter adapter = (textAdapter) listview.getAdapter();
				SuggestionInfo info = adapter.getItem(position);
				String keyword = info.city + info.district + info.key;
				if (!TextUtils.isEmpty(mLastResult) && mLastResult.equals(keyword)) {
					startPlan();
				}
				posiEdit.setText(keyword);
			}
		});
	}

	class textAdapter extends BaseAdapter {
		public List<SuggestionInfo> infoList;

		public textAdapter(List<SuggestionInfo> infoList) {
			this.infoList = infoList;
		}

		public int getCount() {
			return infoList.size();
		}

		public SuggestionInfo getItem(int position) {
			return infoList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(SetPositionActivity.this).inflate(R.layout.item_setposition_textview, null);
			TextView text = (TextView) convertView.findViewById(R.id.item_textview);
			SuggestionInfo info = infoList.get(position);
			text.setText(info.city + info.district + info.key);
			return convertView;
		}

	}

	protected void onStop() {
		super.onStop();
		if (null != mSuggestionSearch) {
			mSuggestionSearch.destroy();
		}
		if (null != mSearch) {
			mSearch.destroy();
		}
	}

	private void closeInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(SetPositionActivity.this.getCurrentFocus().getWindowToken(), 0);
		}
	}

}
