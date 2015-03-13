package mobi.dlys.android.familysafer.ui.checkin;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class TakeMessageActivity extends BaseExActivity implements OnClickListener {
    private TitleBarHolder mTitleBar;
    private EditText editText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takemessage);
        initView();
        initData();
    }

    private void initData() {
        if (getIntent() != null) {
            String tipString = getIntent().getStringExtra("tipString");
            if (!TextUtils.isEmpty(tipString)) {
                editText.setText(tipString);
                editText.setSelection(editText.getText().length());
            }
        }
    }

    private void closeInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(TakeMessageActivity.this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void initView() {
        mTitleBar = new TitleBarHolder(this);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mTitle.setText(R.string.activity_checkin_takemessage);
        mTitleBar.mRight.setText(R.string.titlebar_button_tip_over);
        mTitleBar.mRight.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                closeInput();
                Intent intent = new Intent();
                String edittext = editText.getEditableText().toString();
                intent.putExtra("takemessage", edittext);
                setResult(Activity.RESULT_OK, intent);
                TakeMessageActivity.this.finish();
            }
        });
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                closeInput();
                Intent intent = new Intent();
                intent.putExtra("takemessage", "");
                setResult(Activity.RESULT_OK, intent);
                TakeMessageActivity.this.finish();
            }
        });
        editText = (EditText) findViewById(R.id.takeMessage_edittext);
        editText.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                if (null == temp) {
                    return;
                }
                editText.setSelection(temp.length());
                if (temp.length() > 15) {
                    YSToast.showToast(getApplicationContext(), getString(R.string.toast_takemessage_max_error));
                }

            }
        });

        LinearLayout tipLL1 = (LinearLayout) findViewById(R.id.takeMessage_tipLL1);
        LinearLayout tipLL2 = (LinearLayout) findViewById(R.id.takeMessage_tipLL2);
        LinearLayout tipLL3 = (LinearLayout) findViewById(R.id.takeMessage_tipLL3);
        LinearLayout tipLL4 = (LinearLayout) findViewById(R.id.takeMessage_tipLL4);
        LinearLayout tipLL5 = (LinearLayout) findViewById(R.id.takeMessage_tipLL5);
        LinearLayout tipLL6 = (LinearLayout) findViewById(R.id.takeMessage_tipLL6);
        tipLL1.setOnClickListener(this);
        tipLL2.setOnClickListener(this);
        tipLL3.setOnClickListener(this);
        tipLL4.setOnClickListener(this);
        tipLL5.setOnClickListener(this);
        tipLL6.setOnClickListener(this);

    }

    public void onClick(View v) {
        String edittext = editText.getEditableText().toString();
        switch (v.getId()) {
        case R.id.takeMessage_tipLL1:
            edittext = edittext + getString(R.string.takemessage_tip1);
            break;
        case R.id.takeMessage_tipLL2:
            edittext = edittext + getString(R.string.takemessage_tip2);
            break;
        case R.id.takeMessage_tipLL3:
            edittext = edittext + getString(R.string.takemessage_tip3);
            break;
        case R.id.takeMessage_tipLL4:
            edittext = edittext + getString(R.string.takemessage_tip4);
            break;
        case R.id.takeMessage_tipLL5:
            edittext = edittext + getString(R.string.takemessage_tip5);
            break;
        case R.id.takeMessage_tipLL6:
            edittext = edittext + getString(R.string.takemessage_tip6);
            break;
        }
        editText.setText(edittext);

    }
}
