package mobi.dlys.android.familysafer.ui.family;

import java.util.Timer;
import java.util.TimerTask;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

public class FamilyRemarkActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;
	EditText mNicknameET;
	GridView mRemarkGrid;
	String[] mData;
	RemarkAdapter mAdapter;
	int userid = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_familyremark);

		initView();

	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_familyremark_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setText(R.string.titlebar_button_tip_over);
		mTitleBar.mRight.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (userid != 0) {
					sendMessage(YSMSG.REQ_SET_REMARK_NAME, userid, 0, mNicknameET.getEditableText().toString());
					closeInput();
					showWaitingDialog();
				}

			}
		});
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				closeInput();
				FamilyRemarkActivity.this.finish();
			}
		});
		mNicknameET = (EditText) findViewById(R.id.edt_familyremark_remark_ed);

		mRemarkGrid = (GridView) findViewById(R.id.edt_familyremark_grid);
		mData = getResources().getStringArray(R.array.activity_familyremark_tip);
		mAdapter = new RemarkAdapter();
		mRemarkGrid.setAdapter(mAdapter);
		if (null != getIntent()) {
			userid = getIntent().getIntExtra("userid", 0);
			String remarkname = getIntent().getStringExtra("remarkname");
			mNicknameET.setText(remarkname);
			CharSequence text = mNicknameET.getText();
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());
			}
		}
		mRemarkGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				String ed = mAdapter.getItem(position);
				mNicknameET.setText(ed);
			}
		});
		mNicknameET.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				temp = arg0;
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				CharSequence text = mNicknameET.getText();
				if (text instanceof Spannable) {
					Spannable spanText = (Spannable) text;
					Selection.setSelection(spanText, text.length());
				}
				selectionStart = mNicknameET.getSelectionStart();
				selectionEnd = mNicknameET.getSelectionEnd();
				if (temp.length() > 10) {
					arg0.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					mNicknameET.setText(arg0);
					mNicknameET.setSelection(tempSelection);
					YSToast.showToast(getApplicationContext(), getString(R.string.toast_remark_max_error));
				}
			}
		});

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mNicknameET, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300);
	}

	private void closeInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(FamilyRemarkActivity.this.getCurrentFocus().getWindowToken(), 0);
		}
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_SET_REMARK_NAME:
			dismissWaitingDialog();
			if (msg.arg1 == 200) {
				// 修改备注成功
				YSToast.showToast(this, R.string.toast_remarkname_modify_succeed);
				FamilyRemarkActivity.this.finish();
			} else {
				// 修改备注失败
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(this, result.getErrorMsg());
				} else {
					YSToast.showToast(this, R.string.network_error);
				}
			}
			break;
		}
	}

	class RemarkAdapter extends BaseAdapter {
		public int getCount() {
			return mData.length;
		}

		public String getItem(int position) {
			return mData[position];
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup root) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.grid_item_textview, null);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();
			holder.textview = (TextView) convertView.findViewById(R.id.item_textview);
			holder.textview.setText(mData[position]);
			return convertView;
		}

	}

	class ViewHolder {
		public TextView textview;

	}
}