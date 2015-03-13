package cn.changl.safe360.android.ui.smslocation;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.ui.comm.contactslist.ContactItemInterface;
import cn.changl.safe360.android.ui.comm.contactslist.ContactListAdapter;

public class CustomContactsAdapter extends ContactListAdapter {

	public CustomContactsAdapter(Context _context, int _resource, List<ContactItemInterface> _items) {
		super(_context, _resource, _items);

	}

	// override this for custom drawing
	public void populateDataForRow(View parentView, ContactItemInterface item, int position) {
		// default just draw the item only
		TextView txtName = (TextView) parentView.findViewById(R.id.txt_contacts_list_item_name);
		TextView txtPhone = (TextView) parentView.findViewById(R.id.txt_contact_list_item_phone);

		if (item instanceof CustomContactsItem) {
			CustomContactsItem contactsItem = (CustomContactsItem) item;
			txtName.setText(contactsItem.getName());
			txtPhone.setText(contactsItem.getPhone());
		}

	}

}
