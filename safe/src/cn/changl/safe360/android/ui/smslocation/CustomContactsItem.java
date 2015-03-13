package cn.changl.safe360.android.ui.smslocation;

import cn.changl.safe360.android.ui.comm.contactslist.ContactItemInterface;

public class CustomContactsItem implements ContactItemInterface {

	private String name;
	private String phone;
	private String index;

	public CustomContactsItem(String name, String phone, String index) {
		super();
		this.name = name;
		this.phone = phone;
		this.index = index;
	}

	// index the list by nickname
	@Override
	public String getItemForIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
