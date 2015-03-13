package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseController;
import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.api.ApiClient;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;

/**
 * 网络请求结果类
 * 
 * @author rocksen
 * 
 */
public class ResultObject extends BaseObject {
	private static final long serialVersionUID = 3097458616949415222L;

	// result
	private boolean result;

	// error code
	private String errorCode;

	// error message
	private String errorMsg;

	public static ResultObject createByPb(FamilySaferPb pb) {
		ResultObject resultObject = new ResultObject();

		if (pb != null) {
			if (ApiClient.isOK(pb)) {
				resultObject.setResult(true);
			} else {
				resultObject.setResult(false);
				if (!ApiClient.isTokenValid(pb)) {
					BaseController.getInstance().sendEmptyMessage(YSMSG.REQ_REFRESH_TOKEN);
				} else {
					if (pb.getResponseStatus() != null) {
						resultObject.setErrorCode(pb.getResponseStatus().getCode());
						resultObject.setErrorMsg(pb.getResponseStatus().getMsg());
					}
				}
			}
		} else {
			resultObject.setErrorMsg("网络错误");
		}

		return resultObject;
	}

	public boolean isOK() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
