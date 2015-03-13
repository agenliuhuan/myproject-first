package cn.changl.safe360.android.ui.smslocation;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import cn.changl.safe360.android.ui.comm.contactslist.ContactListView;
import cn.changl.safe360.android.ui.comm.contactslist.IndexScroller;

public class CustomContactsListView extends ContactListView {

	public CustomContactsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void createScroller() {

		mScroller = new IndexScroller(getContext(), this);

		mScroller.setAutoHide(autoHide);

		// style 1
		// mScroller.setShowIndexContainer(false);
		// mScroller.setIndexPaintColor(Color.argb(255, 49, 64, 91));

		// style 2
		mScroller.setShowIndexContainer(true);
		mScroller.setIndexPaintColor(Color.WHITE);

		if (autoHide)
			mScroller.hide();
		else
			mScroller.show();

	}
}
