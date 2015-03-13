package mobi.dlys.android.familysafer.ui.login;

import mobi.dlys.android.familysafer.service.CoreInterface;

public class LoginHelper {
	public static void autoLogin(CoreInterface coreInterface) {
		try {
			coreInterface.IAutoLogin();
		} catch (Exception e) {
		}
	}

	public static boolean isLogined(CoreInterface coreInterface) {
		try {
			return coreInterface.IIsLogin();
		} catch (Exception e) {
		}

		return false;
	}

}
