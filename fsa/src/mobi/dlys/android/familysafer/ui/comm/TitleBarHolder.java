package mobi.dlys.android.familysafer.ui.comm;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.model.CoreModel;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 公用TitleBar 辅助初始Titlebar的各个子控件。
 * 
 * @author
 * 
 */
public class TitleBarHolder {

    public Button mLeft;
    public TextView mTitle;
    public Button mRight;
    public Button mRight2;
    public TextView mTip;
    public TextView mTipText;

    public TitleBarHolder(final Activity activity) {
        mLeft = (Button) activity.findViewById(R.id.btn_title_left);
        mTitle = (TextView) activity.findViewById(R.id.txt_title_center);
        mRight = (Button) activity.findViewById(R.id.btn_title_right);
        mRight2 = (Button) activity.findViewById(R.id.btn_title_right2);
        mTip = (TextView) activity.findViewById(R.id.txt_title_tip);
        mTipText = (TextView) activity.findViewById(R.id.txt_title_tip_text);

        if (null != mLeft) {

            mLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }
    }

    public TitleBarHolder(final Activity act, View group) {
        mLeft = (Button) group.findViewById(R.id.btn_title_left);
        mTitle = (TextView) group.findViewById(R.id.txt_title_center);
        mRight = (Button) group.findViewById(R.id.btn_title_right);
        mRight2 = (Button) group.findViewById(R.id.btn_title_right2);
        mTip = (TextView) group.findViewById(R.id.txt_title_tip);
        mTipText = (TextView) group.findViewById(R.id.txt_title_tip_text);

        if (null != mLeft) {

            mLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    act.finish();
                }
            });
        }
    }

    public void displayTip() {
        int eventCount = CoreModel.getInstance().getEventCount();
        int friendReqCount = CoreModel.getInstance().getFriendReqCount();
        int msgCount = CoreModel.getInstance().getMsgCount();
        if (mTip != null && mTipText != null) {
            // if (msgCount > 0) {
            // if (msgCount > 99) {
            // msgCount = 99;
            // }
            //
            // mTip.setVisibility(View.GONE);
            // mTipText.setText(msgCount + "");
            // mTipText.setVisibility(View.VISIBLE);
            // return;
            // }
            //
            // if (eventCount > 0) {
            // if (eventCount > 99) {
            // eventCount = 99;
            // }
            //
            // mTip.setVisibility(View.GONE);
            // mTipText.setText(eventCount + "");
            // mTipText.setVisibility(View.VISIBLE);
            // return;
            // }

            if (eventCount > 0 || msgCount > 0 || friendReqCount > 0) {
                mTipText.setVisibility(View.GONE);
                mTip.setVisibility(View.VISIBLE);
            } else {
                mTip.setVisibility(View.GONE);
                mTipText.setVisibility(View.GONE);
            }
        }
    }
}


