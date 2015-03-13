package cn.changl.safe360.android.biz.vo;

import java.util.ArrayList;

import mobi.dlys.android.core.mvc.BaseObject;

public class PushMessageObject extends BaseObject {
	private static final long serialVersionUID = 7284817191557523128L;

	private String cmd;
	private String description;
	private String title;
	private String data;
	private ArrayList<Integer> useridList;
	private int from;

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public ArrayList<Integer> getUseridList() {
		return useridList;
	}

	public void setUseridList(ArrayList<Integer> useridList) {
		this.useridList = useridList;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
