package mobi.dlys.android.familysafer.utils.umeng;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

public class AnalyticsHelper {

	public static boolean ENABLE = true;

	// ============================== 事件 ==================================
	// 首页

	/** SOS单击次数 */
	public static final String index_sos_click = "index_sos_single";
	/** SOS长按次数 */
	public static final String index_sos_longclick = "index_sos_long";
	/** 家人定位 */
	public static final String index_family_locate = "index_family_location";
	/** 到达通知 */
	public static final String index_checkin = "index_check_in";
	/** 安全雷达 */
	public static final String index_radar_clue = "index_take_clue";

//SOS单击
	//拨打医院电话
	public static final String index_call_hospital = "sos_single_call_hospital";
	//拨打派出所电话
	public static final String index_call_policestation = "sos_single_call_police";
	//拨打110
	public static final String index_call_110 = "sos_single_call_110";
	//点击医院图标
	public static final String index_click_hospital = "sos_single_click_hospital";
	//点击派出所图标
	public static final String index_click_policestation = "sos_single_click_police";
	//点击列表按钮
	public static final String index_click_list = "sos_single_click_maplist";
	
//SOS长按	
	//SOS确认发送
	public static final String index_send_accept = "sos_long_confirm_send";
	//SOS取消发送
	public static final String index_send_cancel = "sos_long_cancel_send";
	//播放语音
	public static final String index_play_voice = "sos_long_play_voice";
	//拨打110
	public static final String index_call_1102 = "sos_long_call_110";
	
//家人定位	
	//拨打家人电话
	public static final String index_call_family2 = "location_call_family";
	//给家人发送语音
	public static final String index_chat_family = "location_send_voice";
	
//到达通知	
	//通知所有人
	public static final String index_send_all = "checkin_notice_all";
	//通知某人
	public static final String index_send_one = "checkin_notice_someone";
	//点击地址调整按钮
	public static final String index_address_adapt = "checkin_address_confirm";
	
//安全雷达	
	//点击拍照按钮
	public static final String index_click_shotsnap = "clue_click_photograph";
	//查看上传成功的照片
	public static final String index_view_upload = "clue_check_photo";
	//上传到公安联网监控中心
	public static final String index_upload_netmon = "clue_upload_center";
	
//家人	
	//添加家人
	public static final String index_add_family = "friend_add_family";
	//拨打家人电话
	public static final String index_call_family = "friend_call_family";
	//给家人发语音
	public static final String index_send_voice = "friend_send_voice";
	
//设置		
	//隐藏我的地理位置
	public static final String index_hide_myself = "config_hide_location";

	// ============================== 接口 ==================================

	/** 设置是否debug模式，在debug模式下，会有log输出，方便调试 (默认true) */
	public static void setDebugMode(boolean debug) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.setDebugMode(debug);
			setAutoLocation(false);
		}
	}

	/** 设置是否允许收集地理位置信息 （默认true） */
	public static void setAutoLocation(boolean auto) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.setAutoLocation(auto);
		}
	}

	/** 联网获取在线配置 */
	public static void updateOnlineConfig(Context context) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.updateOnlineConfig(context);
		}
	}

	public static void onResume(Context context) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onResume(context);
		}
	}

	public static void onPause(Context context) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onPause(context);
		}
	}

	public static void onPageStart(String page) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onPageStart(page);
		}
	}

	public static void onPageEnd(String page) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onPageEnd(page);
		}
	}

	/**
	 * 手动提交错误报告
	 * 
	 * @param context
	 * @param error
	 */
	public static void reportError(Context context, String error) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.reportError(context, error);
		}
	}

	/**
	 * 跟踪自定义事件
	 * 
	 * @param context
	 * @param event_id
	 *            为当前统计的事件ID,注意要先在友盟网站上注册此事件ID。<br/>
	 *            例如："send" 代表发送事件,"submit" 代表提交事件,"back" 代表返回事件
	 * @param map
	 *            为当前事件的属性和取值集合（key-value）<br/>
	 *            例如："send",<"id","102">,<"name","一片"> 代表发送事件触发时的传参值
	 */
	public static void onEvent(Context context, String event_id, Map<String, String> map) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onEvent(context, event_id, map);
		}
	}

	public static void onEvent(Context context, String event_id, String state, String errorInfo, String reqUrl) {
		if (AnalyticsHelper.ENABLE) {
			Map<String, String> error = new HashMap<String, String>();
			if (error != null) {
				error.put("state", state);
				error.put("error", errorInfo);
				error.put("src", reqUrl);
				MobclickAgent.onEvent(context, event_id, error);
			}
		}
	}

	/**
	 * 跟踪自定义事件
	 * 
	 * @param context
	 * @param event_id
	 *            为当前统计的事件ID,注意要先在友盟网站上注册此事件ID。<br/>
	 *            例如："send" 代表发送事件,"submit" 代表提交事件,"back" 代表返回事件
	 */
	public static void onEvent(Context context, String event_id) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onEvent(context, event_id);
		}
	}

	/**
	 * 跟踪自定义事件
	 * 
	 * @param context
	 * @param event_id
	 *            为当前统计的事件ID,注意要先在友盟网站上注册此事件ID。<br/>
	 *            例如："send" 代表发送事件,"submit" 代表提交事件,"back" 代表返回事件
	 * @param label
	 *            事件的一个属性描述
	 */
	public static void onEvent(Context context, String event_id, String label) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onEvent(context, event_id, label);
		}
	}

	/**
	 * 事件开始时间
	 * 
	 * @param context
	 * @param event_id
	 */
	public static void onEventBegin(Context context, String event_id) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onEventBegin(context, event_id);
		}
	}

	/**
	 * 事件开始时间<br/>
	 * 跟踪时长的事件包含多个属性
	 * 
	 * @param context
	 * @param event_id
	 *            为当前统计的事件ID ,注意要先在友盟网站上注册此事件ID.
	 * @param map
	 *            为当前事件的属性和取值集合 （key-value）
	 * @param ekvFlag
	 *            事件标示符
	 */
	public static void onEventBegin(Context context, String event_id, Map<String, String> map, String ekvFlag) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onKVEventBegin(context, event_id, map, ekvFlag);
		}
	}

	/**
	 * 事件结束时间
	 * 
	 * @param context
	 * @param event_id
	 */
	public static void onEventEnd(Context context, String event_id) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onEventEnd(context, event_id);
		}
	}

	/**
	 * 事件结束时间
	 * 
	 * @param context
	 * @param event_id
	 *            为当前统计的事件ID ,注意要先在友盟网站上注册此事件ID.
	 * @param ekvFlag
	 *            事件标示符
	 */
	public static void onEventEnd(Context context, String event_id, String ekvFlag) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onKVEventEnd(context, event_id, ekvFlag);
		}
	}

	/**
	 * 自己计算Event时长
	 * 
	 * @param context
	 * @param event_id
	 *            为当前统计的事件ID,注意要先在友盟网站上注册此事件ID
	 * @param duration
	 *            事件持续时长，单位毫秒，您需要手动计算并传入时长，作为事件的时长参数
	 */
	public static void onEventDuration(Context context, String event_id, long duration) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onEventDuration(context, event_id, duration);
		}
	}

	/**
	 * 自己计算Event时长
	 * 
	 * @param context
	 * @param event_id
	 *            为当前统计的事件ID,注意要先在友盟网站上注册此事件ID
	 * @param label
	 *            事件的一个属性描述
	 * @param duration
	 *            事件持续时长，单位毫秒，您需要手动计算并传入时长，作为事件的时长参数
	 */
	public static void onEventDuration(Context context, String event_id, String label, long duration) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onEventDuration(context, event_id, label, duration);

		}
	}

	/**
	 * 自己计算Event时长
	 * 
	 * @param context
	 * @param event_id
	 *            为当前统计的事件ID,注意要先在友盟网站上注册此事件ID
	 * @param map
	 *            为当前事件的属性和取值集合（key-value）
	 * @param duration
	 *            事件持续时长，单位毫秒，您需要手动计算并传入时长，作为事件的时长参数
	 */
	public static void onEventDuration(Context context, String event_id, Map<String, String> map, long duration) {
		if (AnalyticsHelper.ENABLE) {
			MobclickAgent.onEventDuration(context, event_id, map, duration);
		}
	}
}
