package cn.changl.safe360.android.biz.vo.upload;

import mobi.dlys.android.core.mvc.FieldsUnproguard;

public class UploadRespObject implements FieldsUnproguard {
	private static final long serialVersionUID = 2112377875503205913L;

	public FileInfo fileInfo;
	public String code;
	public String msg;
	public boolean success;

	public boolean isOK() {
		return success;
	}

	public String getFileUri() {
		if (fileInfo != null) {
			return fileInfo.fileUri;
		}

		return null;
	}

	public String getFileUrl() {
		if (fileInfo != null) {
			return fileInfo.fileUrl;
		}

		return null;
	}
}
