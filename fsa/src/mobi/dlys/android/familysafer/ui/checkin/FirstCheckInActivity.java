package mobi.dlys.android.familysafer.ui.checkin;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.utils.NoticePlayer;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FirstCheckInActivity extends BaseExActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstcheckin);
        initView();
    }

    private void initView() {
        NoticePlayer.playCheckin(getBaseContext());
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                showtwoDialog();
            }
        };
    };

    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessageDelayed(1, 4000);
    };

    protected void onPause() {
        super.onPause();
        handler.removeMessages(1);
    }

    private void showtwoDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_two_button, null);
        if (view != null) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(FirstCheckInActivity.this, view, false, false);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.activity_firstcheckin_dialog_title));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            txtContent.setText(getString(R.string.activity_firstcheckin_twodialog_content));

            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            final Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
            btnConfirm.setText(R.string.activity_firstcheckin_twodialog_confirm);
            btnCancel.setText(R.string.activity_firstcheckin_twodialog_concancel);
            btnConfirm.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    dialog.cancel();
                    Intent intent = new Intent(getBaseContext(), CheckinActivity.class);
                    startActivity(intent);
                    FirstCheckInActivity.this.finish();
                    overridePendingTransition(R.anim.view_translate_bottom_in, R.anim.view_translate_top_out);
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    dialog.cancel();
                    shownoeDialog();
                }
            });
            dialog.show();
        }
    }

    private void shownoeDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_one_button, null);
        if (view != null) {
            final Dialog dialog = YSAlertDialog.createBaseDialog(FirstCheckInActivity.this, view, false, false);
            final TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
            txtTitle.setText(getString(R.string.activity_firstcheckin_dialog_title));
            final TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
            txtContent.setText(getString(R.string.activity_firstcheckin_onedialog_content));

            final Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
            btnConfirm.setText(R.string.activity_firstcheckin_onedialog_concancel);

            btnConfirm.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    dialog.cancel();
                    Intent intent = new Intent(getBaseContext(), CheckinActivity.class);
                    startActivity(intent);
                    FirstCheckInActivity.this.finish();
                    overridePendingTransition(R.anim.view_translate_bottom_in, R.anim.view_translate_top_out);
                }
            });
            dialog.show();
        }
    }
}
