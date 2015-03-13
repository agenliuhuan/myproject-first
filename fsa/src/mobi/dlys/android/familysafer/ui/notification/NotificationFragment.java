package mobi.dlys.android.familysafer.ui.notification;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationFragment extends BaseExFragment {
    protected TitleBarHolder mTitleBar;
    TextView sosnumTV;
    TextView innumTV;
    TextView tipnumTV;
    TextView cluenumTV;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_notification, null);
		initSubView();
		initData();
		return mRootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			displayTip();
		} else {
		}
	}

	public void displayTip() {
		mTitleBar.displayTip();

        if (CoreModel.getInstance().getNewSOSCount() > 0) {
            sosnumTV.setVisibility(View.VISIBLE);
        } else {
            sosnumTV.setVisibility(View.GONE);
        }
        if (CoreModel.getInstance().getNewCheckinCount() > 0) {
            innumTV.setVisibility(View.VISIBLE);
        } else {
            innumTV.setVisibility(View.GONE);
        }
        if (CoreModel.getInstance().getNewConfirmCount() > 0) {
            tipnumTV.setVisibility(View.VISIBLE);
        } else {
            tipnumTV.setVisibility(View.GONE);
        }
        if (CoreModel.getInstance().getNewEventClueCount() > 0) {
            cluenumTV.setVisibility(View.VISIBLE);
        } else {
            cluenumTV.setVisibility(View.GONE);
        }
    }

	public void onResume() {
		super.onResume();

		displayTip();
	}

    private void initData() {
        RelativeLayout sosRL = (RelativeLayout) findViewById(R.id.fragment_notification_sos_RL);
        sosnumTV = (TextView) findViewById(R.id.fragment_notification_sos_numtv);
        RelativeLayout inRL = (RelativeLayout) findViewById(R.id.fragment_notification_in_RL);
        innumTV = (TextView) findViewById(R.id.fragment_notification_in_numtv);
        RelativeLayout tipRL = (RelativeLayout) findViewById(R.id.fragment_notification_tip_RL);
        tipnumTV = (TextView) findViewById(R.id.fragment_notification_tip_numtv);
        RelativeLayout clueRL = (RelativeLayout) findViewById(R.id.fragment_notification_clue_RL);
        cluenumTV = (TextView) findViewById(R.id.fragment_notification_clue_numtv);
        sosRL.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CoreModel.getInstance().setNewSOSCount(0);
                Intent intent = new Intent(getActivity(), MyNotificationActivity.class);
                intent.putExtra("type", "sos");
                startActivity(intent);
            }
        });
        inRL.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CoreModel.getInstance().setNewCheckinCount(0);
                Intent intent = new Intent(getActivity(), MyNotificationActivity.class);
                intent.putExtra("type", "in");
                startActivity(intent);
            }
        });
        tipRL.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CoreModel.getInstance().setNewConfirmCount(0);
                Intent intent = new Intent(getActivity(), MyNotificationActivity.class);
                intent.putExtra("type", "tip");
                startActivity(intent);
            }
        });
        clueRL.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CoreModel.getInstance().setNewEventClueCount(0);
                Intent intent = new Intent(getActivity(), ClueNotificationActivity.class);
                startActivity(intent);
            }
        });
    }

	private void initSubView() {
		mTitleBar = new TitleBarHolder(getActivity(), mRootView);
		mTitleBar.mTitle.setText(R.string.fragment_mynotification_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				((MainActivity) getActivity()).toggle();
			}
		});
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

	}

}
