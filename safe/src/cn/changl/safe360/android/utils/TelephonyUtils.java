package cn.changl.safe360.android.utils;

import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;

public class TelephonyUtils {

	/**
	 * 发送短信
	 * 
	 * @param phone
	 * @param content
	 * @param secret
	 *            是否秘密发送
	 */
	public static void sendSms(Context context, String phone, String content, boolean secret) {
		if (!secret) {
			Uri uri = Uri.parse("smsto:" + phone);
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			intent.putExtra("sms_body", content);

			context.startActivity(intent);
		} else {
			String strMobile = phone;
			String strContent = content;
			SmsManager smsManager = SmsManager.getDefault();
			if (strContent.length() > 70) {
				List<String> contents = smsManager.divideMessage(strContent);
				for (String sms : contents) {
					smsManager.sendTextMessage(strMobile, null, sms, null, null);
				}
			} else {
				smsManager.sendTextMessage(strMobile, null, strContent, null, null);
			}
		}
	}

	/**
	 * 拨打电话
	 * 
	 * @param context
	 * @param phone
	 */
	public static void call(Context context, String phone) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
		context.startActivity(intent);
	}

	/**
	 * 移动:134X(0-8)、135、136、137、138、139、150、151、157X(0-7\9)(TD)、158、159、182、183、
	 * 184、187(3G\4G)、188(3G)、147(数据卡)、178(4G)
	 * 联通:130、131、132、152、155、156、185(3G)、186(3G)、145(数据卡)、176(4G)
	 * 电信:180(3G)、181(3G)、189(3G)、133、153、（1349卫通） 、177（4G）
	 */
	private static final Pattern MOBILER = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[6-8])|(18[0-9]))\\d{8}$");

	/**
	 * @return 是否手机号
	 */
	public static boolean isMobile(String mobile) {
		if (!TextUtils.isEmpty(mobile)) {
			return MOBILER.matcher(mobile).matches();
		}
		return false;
	}

}