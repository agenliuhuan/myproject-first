/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.changl.safe360.android.im.hx.controller;

import android.content.Intent;
import cn.changl.safe360.android.im.hx.model.DefaultHXSDKModel;
import cn.changl.safe360.android.im.hx.model.HXSDKModel;
import cn.changl.safe360.android.im.hx.utils.CommonUtils;
import cn.changl.safe360.android.ui.escort.EscortActivity;
import cn.changl.safe360.android.ui.main.MainActivity;

import com.easemob.EMCallBack;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * 
 * @author easemob
 * 
 */
public class Safe360HXSDKHelper extends HXSDKHelper {

	/**
	 * contact list in cache
	 */

	@Override
	protected void initHXOptions() {
		super.initHXOptions();
		// you can also get EMChatOptions to set related SDK options
		// EMChatOptions options = EMChatManager.getInstance().getChatOptions();
	}

	@Override
	protected OnMessageNotifyListener getMessageNotifyListener() {
		// 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
		return new OnMessageNotifyListener() {

			@Override
			public String onNewMessageNotify(EMMessage message) {
				// 设置状态栏的消息提示，可以根据message的类型做相应提示
				String ticker = CommonUtils.getMessageDigest(message, appContext);
				if (message.getType() == Type.TXT)
					ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
				return message.getFrom() + ": " + ticker;
			}

			@Override
			public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
				return null;
				// return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
			}

			@Override
			public String onSetNotificationTitle(EMMessage message) {
				// 修改标题,这里使用默认
				return null;
			}

			@Override
			public int onSetSmallIcon(EMMessage message) {
				// 设置小图标
				return 0;
			}
		};
	}

	@Override
	protected OnNotificationClickListener getNotificationClickListener() {
		return new OnNotificationClickListener() {

			@Override
			public Intent onNotificationClick(EMMessage message) {
				ChatType chatType = message.getChatType();
				if (chatType == ChatType.Chat) { // 单聊信息
					Intent intent = new Intent(appContext, MainActivity.class);
					intent.putExtra("userId", message.getFrom());
					intent.putExtra("chatType", EscortActivity.CHATTYPE_SINGLE);
					return intent;
				} else { // 群聊信息
							// message.getTo()为群聊id
					Intent intent = new Intent(appContext, EscortActivity.class);
					intent.putExtra("groupId", message.getTo());
					intent.putExtra("chatType", EscortActivity.CHATTYPE_GROUP);
					return intent;
				}

			}
		};
	}

	@Override
	protected void onConnectionConflict() {
		Intent intent = new Intent(appContext, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("conflict", true);
		appContext.startActivity(intent);
	}

	@Override
	protected void initListener() {
		super.initListener();
		// IntentFilter callFilter = new
		// IntentFilter(EMChatManager.getInstance().getIncomingVoiceCallBroadcastAction());
		// appContext.registerReceiver(new VoiceCallReceiver(), callFilter);
	}

	@Override
	protected HXSDKModel createModel() {
		return new DefaultHXSDKModel(appContext);
	}

	/**
	 * get HX SDK Model
	 */
	public DefaultHXSDKModel getModel() {
		return (DefaultHXSDKModel) hxModel;
	}

	@Override
	public void logout(final EMCallBack callback) {
		super.logout(new EMCallBack() {

			@Override
			public void onSuccess() {
				if (callback != null) {
					callback.onSuccess();
				}
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

		});
	}
}
