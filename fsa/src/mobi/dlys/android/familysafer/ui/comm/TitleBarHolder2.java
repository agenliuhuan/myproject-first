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

public class TitleBarHolder2 {

    public Button mLeft2;
    public TextView mTitle2;
    public Button mRight2;
    public Button mRight22;
    public TextView mTip2;
    public TextView mTipText2;

    public TitleBarHolder2(final Activity activity) {
        mLeft2 = (Button) activity.findViewById(R.id.btn_title_left2);
        mTitle2 = (TextView) activity.findViewById(R.id.txt_title_center2);
        mRight2 = (Button) activity.findViewById(R.id.btn_title_right2);
        mRight22 = (Button) activity.findViewById(R.id.btn_title_right22);
        mTip2 = (TextView) activity.findViewById(R.id.txt_title_tip2);
        mTipText2 = (TextView) activity.findViewById(R.id.txt_title_tip_text2);

        if (null != mLeft2) {

            mLeft2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }
    }

    public TitleBarHolder2(final Activity act, View group) {
        mLeft2 = (Button) group.findViewById(R.id.btn_title_left2);
        mTitle2 = (TextView) group.findViewById(R.id.txt_title_center2);
        mRight2 = (Button) group.findViewById(R.id.btn_title_right2);
        mRight22 = (Button) group.findViewById(R.id.btn_title_right22);
        mTip2 = (TextView) group.findViewById(R.id.txt_title_tip2);
        mTipText2 = (TextView) group.findViewById(R.id.txt_title_tip_text2);

        if (null != mLeft2) {

            mLeft2.setOnClickListener(new View.OnClickListener() {
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
        if (mTip2 != null && mTipText2 != null) {
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
                mTipText2.setVisibility(View.GONE);
                mTip2.setVisibility(View.VISIBLE);
            } else {
                mTip2.setVisibility(View.GONE);
                mTipText2.setVisibility(View.GONE);
            }
        }
    }
}