package mobi.dlys.android.familysafer.ui.setting;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class FeedbackActivity extends BaseExActivity {

	protected TitleBarHolder mTitleBar;

	EditText mContent = null;
	boolean mHasContent = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		initView();

	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_feedback_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setText(R.string.titlebar_button_tip_commit);

		mTitleBar.mRight.setEnabled(false);
		mTitleBar.mRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

//				Intent intent = new Intent(FeedbackActivity.this, AccountActivity.class);
//				startActivity(intent);
//				FeedbackActivity.this.finish();

			}
		});

		mContent = (EditText) this.findViewById(R.id.edt_feedback_content);
		mContent.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void afterTextChanged(Editable arg0) {
				if (null == temp) {
					return;
				}
				selectionStart = mContent.getSelectionStart();
				selectionEnd = mContent.getSelectionEnd();
				if (temp.length() > 0) {
					mHasContent = true;
					if (temp.length() > 200) {
						arg0.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionEnd;
						mContent.setText(arg0);
						mContent.setSelection(tempSelection);
						YSToast.showToast(getApplicationContext(), getString(R.string.toast_feedback_max_error));
					}
				} else {
					mHasContent = false;
				}
				if (mHasContent) {
					if (null != mTitleBar) {
						mTitleBar.mRight.setEnabled(true);
					}
				} else {
					if (null != mTitleBar) {
						mTitleBar.mRight.setEnabled(false);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				temp = arg0;

			}
		});

	}
}