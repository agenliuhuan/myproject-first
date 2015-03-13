package mobi.dlys.android.familysafer.ui.clue;

import java.util.List;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.utils.NoticePlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SendClueOKActivity extends BaseExActivity {

    ClueObject mClueObject;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentcheck);

        initView();

        initData();
    }

    void initView() {
        Button btnKnow = (Button) this.findViewById(R.id.btn_sentcheck_know);
        btnKnow.setText(getString(R.string.fragment_main_first_btn_know));
        btnKnow.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                SendClueOKActivity.this.finish();
            }
        });
        NoticePlayer.playClueUpload(getBaseContext());
    }

    void initData() {
        if (getIntent() != null) {
            mClueObject = (ClueObject) getIntent().getSerializableExtra(SendClueActivity.EXTRA_CLUE_OBJECT);
        }
        TextView text = (TextView) findViewById(R.id.tv_sentcheck_target);
        TextView tip = (TextView) findViewById(R.id.tv_sentcheck_tip);
        tip.setText(getString(R.string.activity_sendclueok_tv_tip1));
        if (mClueObject != null) {
            List<FriendObject> list = CoreModel.getInstance().getFriendList();
            if (mClueObject.isEvent() && list != null && list.size() > 0) {
                text.setText(R.string.activity_sendclueok_tv_tip3);
            } else {
                text.setText(R.string.activity_sendclueok_tv_tip2);
            }
        }
    }
}
