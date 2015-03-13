package cn.changl.safe360.android.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.core.utils.AndroidConfig;
import cn.changl.safe360.api.protobuf.Safe360Protobuf.Safe360Pb.ClientVersion;

public class UpdateObject extends BaseObject {
	private static final long serialVersionUID = -3837403877494316120L;

	// 版本ID
	int versionId;

	// 版本号
	int versionCode;

	// 版本名
	String versionName;

	// 下载地址
	String downloadUrl;

	// 更新日志
	String updateLog;

	// 是否强制升级
	int force;

	// 封面是否更新
	boolean coverUpdate;

	// 封面开始时间
	String coverStartDate;

	// 封面结束时间
	String coverEndDate;

	// 封面地址
	String coverUrl;

	public static UpdateObject createFromPb(ClientVersion version) {
		UpdateObject update = new UpdateObject();

		if (version != null) {
			update.setVersionId(version.getVersionId());
			update.setVersionCode(version.getVersionCode());
			update.setVersionName(version.getVersionName());
			update.setDownloadUrl(version.getDownloadUrl());
			update.setUpdateLog(version.getUpdateLog());
			update.setForceUpdate(version.getForce());
			update.setCoverUpdate(version.getCoverUpdate());
			update.setCoverStartDate(version.getCoverStartDate());
			update.setCoverEndDate(version.getCoverEndDate());
			update.setCoverUrl(version.getCoverUrl());
		}

		return update;
	}

	public int getVersionId() {
		return versionId;
	}

	public void setVersionId(int id) {
		versionId = id;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int code) {
		versionCode = code;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String name) {
		versionName = name;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String url) {
		downloadUrl = url;
	}

	public String getUpdateLog() {
		return updateLog;
	}

	public void setUpdateLog(String update) {
		updateLog = update;
	}

	public int getForceUpdate() {
		return force;
	}

	public void setForceUpdate(int update) {
		force = update;
	}

	public boolean getCoverUpdate() {
		return coverUpdate;
	}

	public void setCoverUpdate(boolean update) {
		coverUpdate = update;
	}

	public String getCoveStartDate() {
		return coverStartDate;
	}

	public void setCoverStartDate(String date) {
		coverStartDate = date;
	}

	public String getCoverEndDate() {
		return coverEndDate;
	}

	public void setCoverEndDate(String date) {
		coverEndDate = date;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String url) {
		coverUrl = url;
	}

	public boolean canUpdate() {
		if (forceUpdate() || AndroidConfig.getVersionCode() < versionCode) {
			return true;
		}

		return false;
	}

	public boolean forceUpdate() {
		return (force == 1) ? true : false;
	}
}
