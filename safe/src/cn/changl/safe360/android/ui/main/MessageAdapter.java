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
package cn.changl.safe360.android.ui.main;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.im.hx.model.Constant;
import cn.changl.safe360.android.im.hx.utils.CommonUtils;
import cn.changl.safe360.android.im.hx.utils.SmileUtils;
import cn.changl.safe360.android.model.CoreModel;
import cn.changl.safe360.android.utils.ImageLoaderHelper;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.DateUtils;

public class MessageAdapter extends BaseAdapter {

	private final static String TAG = "msg";

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
	private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
	private static final int MESSAGE_TYPE_SENT_FILE = 10;
	private static final int MESSAGE_TYPE_RECV_FILE = 11;
	private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
	private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";
	public static final String VIDEO_DIR = "chat/video";

	private String username;
	private LayoutInflater inflater;
	private Activity activity;

	// reference to conversation object in chatsdk
	private EMConversation conversation;

	private Context context;

	private Map<String, Timer> timers = new Hashtable<String, Timer>();

	public MessageAdapter(Context context, String username, int chatType) {
		this.username = username;
		this.context = context;
		inflater = LayoutInflater.from(context);
		activity = (Activity) context;
		this.conversation = EMChatManager.getInstance().getConversation(username);
	}

	// public void setUser(String user) {
	// this.user = user;
	// }

	/**
	 * 获取item数
	 */
	public int getCount() {
		return conversation.getMsgCount();
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		notifyDataSetChanged();
	}

	public EMMessage getItem(int position) {
		return conversation.getMessage(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		EMMessage message = conversation.getMessage(position);
		if (message.getType() == EMMessage.Type.TXT) {
			if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

		}
		if (message.getType() == EMMessage.Type.LOCATION) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == EMMessage.Type.VIDEO) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
		}
		if (message.getType() == EMMessage.Type.FILE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
		}

		return -1;// invalid
	}

	public int getViewTypeCount() {
		return 14;
	}

	private View createViewByMessage(EMMessage message, int position) {
		switch (message.getType()) {

		case VOICE:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.item_chat_from_voice, null) : inflater.inflate(
					R.layout.item_chat_to_voice, null);
		default:
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.item_chat_from_text, null) : inflater.inflate(
					R.layout.item_chat_to_text, null);
		}
	}

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		ChatType chatType = message.getChatType();
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);
			if (message.getType() == EMMessage.Type.TXT) {

				try {
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					// holder.staus_iv = (ImageView)
					// convertView.findViewById(R.id.msg_status);
					holder.head_iv = (ImageView) convertView.findViewById(R.id.item_chattotext_userimg);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.txt_chat_username);
					// 这里是文字内容
					holder.tv = (TextView) convertView.findViewById(R.id.item_chattotext_content);
				} catch (Exception e) {
				}

			} else if (message.getType() == EMMessage.Type.VOICE) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.item_chatvoice_voiceImg));
					holder.head_iv = (ImageView) convertView.findViewById(R.id.item_chattovoice_userimg);
					// holder.tv = (TextView)
					// convertView.findViewById(R.id.tv_length);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.voiceBg = (RelativeLayout) convertView.findViewById(R.id.item_chatvoice_voiceRL);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.txt_chat_username);
					holder.timeLength = (TextView) convertView.findViewById(R.id.item_chatvoice_duration);
					// holder.staus_iv = (ImageView)
					// convertView.findViewById(R.id.msg_status);
					// holder.iv_read_status = (ImageView)
					// convertView.findViewById(R.id.iv_unread_voice);
				} catch (Exception e) {
				}
			}

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {
			// 群聊时，显示接收的消息的发送人的名称
			if (chatType == ChatType.GroupChat) {
				if (holder.tv_userId != null) {
					holder.tv_userId.setText(CoreModel.getInstance().getNickname(message.getFrom()));
					holder.tv_userId.setVisibility(View.VISIBLE);
				}
			} else {
				if (holder.tv_userId != null) {
					holder.tv_userId.setVisibility(View.GONE);
				}
			}
			if (!TextUtils.isEmpty(CoreModel.getInstance().getAvatar(message.getFrom()))) {
				ImageLoaderHelper.displayAvatar(CoreModel.getInstance().getAvatar(message.getFrom()), holder.head_iv);
			}
		} else {
			if (CoreModel.getInstance().getUserInfo() != null) {
				ImageLoaderHelper.displayAvatar(CoreModel.getInstance().getUserInfo().getDisplayImage(), holder.head_iv);
			}
		}

		// 如果是发送的消息并且不是群聊消息，显示已读textview
		// if (message.direct == EMMessage.Direct.SEND && chatType !=
		// ChatType.GroupChat) {
		// holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
		// holder.tv_delivered = (TextView)
		// convertView.findViewById(R.id.tv_delivered);
		// if (holder.tv_ack != null) {
		// if (message.isAcked) {
		// if (holder.tv_delivered != null) {
		// holder.tv_delivered.setVisibility(View.INVISIBLE);
		// }
		// holder.tv_ack.setVisibility(View.VISIBLE);
		// } else {
		// holder.tv_ack.setVisibility(View.INVISIBLE);
		//
		// // check and display msg delivered ack status
		// if (holder.tv_delivered != null) {
		// if (message.isDelivered) {
		// holder.tv_delivered.setVisibility(View.VISIBLE);
		// } else {
		// holder.tv_delivered.setVisibility(View.INVISIBLE);
		// }
		// }
		// }
		// }
		// } else {
		// // 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
		// if ((message.getType() == Type.TXT || message.getType() ==
		// Type.LOCATION) && !message.isAcked && chatType != ChatType.GroupChat)
		// {
		// // 不是语音通话记录
		// if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,
		// false)) {
		// try {
		// EMChatManager.getInstance().ackMessageRead(message.getFrom(),
		// message.getMsgId());
		// // 发送已读回执
		// message.isAcked = true;
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// }

		switch (message.getType()) {
		// 根据消息type显示item
		case IMAGE: // 图片
			break;
		case TXT: // 文本
			if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
				handleTextMessage(message, holder, position);
			else
				// 语音电话
				handleVoiceCallMessage(message, holder, position);
			break;
		case LOCATION: // 位置
			break;
		case VOICE: // 语音
			handleVoiceMessage(message, holder, position, convertView);
			break;
		case VIDEO: // 视频
			break;
		case FILE: // 一般文件
			break;
		default:
			// not supported
		}

		// if (message.direct == EMMessage.Direct.SEND) {
		// View statusView = convertView.findViewById(R.id.msg_status);
		// // 重发按钮点击事件
		// statusView.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		//
		// // 显示重发消息的自定义alertdialog
		// Intent intent = new Intent(activity, AlertDialog.class);
		// intent.putExtra("msg", activity.getString(R.string.confirm_resend));
		// intent.putExtra("title", activity.getString(R.string.resend));
		// intent.putExtra("cancel", true);
		// intent.putExtra("position", position);
		// if (message.getType() == EMMessage.Type.TXT)
		// activity.startActivityForResult(intent,
		// ChatActivity.REQUEST_CODE_TEXT);
		// else if (message.getType() == EMMessage.Type.VOICE)
		// activity.startActivityForResult(intent,
		// ChatActivity.REQUEST_CODE_VOICE);
		// else if (message.getType() == EMMessage.Type.IMAGE)
		// activity.startActivityForResult(intent,
		// ChatActivity.REQUEST_CODE_PICTURE);
		// else if (message.getType() == EMMessage.Type.LOCATION)
		// activity.startActivityForResult(intent,
		// ChatActivity.REQUEST_CODE_LOCATION);
		// else if (message.getType() == EMMessage.Type.FILE)
		// activity.startActivityForResult(intent,
		// ChatActivity.REQUEST_CODE_FILE);
		// else if (message.getType() == EMMessage.Type.VIDEO)
		// activity.startActivityForResult(intent,
		// ChatActivity.REQUEST_CODE_VIDEO);
		//
		// }
		// });
		//
		// } else {
		//
		// }

		TextView timestamp = (TextView) convertView.findViewById(R.id.item_chat_date_tv);
		if (timestamp != null) {
			if (position == 0) {
				timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			} else {
				// 两条消息时间离得如果稍长，显示时间
				if (DateUtils.isCloseEnough(message.getMsgTime(), conversation.getMessage(position - 1).getMsgTime())) {
					timestamp.setVisibility(View.GONE);
				} else {
					timestamp.setText(CommonUtils.getTimestampString(new Date(message.getMsgTime())));
					timestamp.setVisibility(View.VISIBLE);
				}
			}
		}

		return convertView;
	}

	/**
	 * 文本消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(EMMessage message, ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
		// 设置内容
		if (holder.tv != null) {
			holder.tv.setText(span, BufferType.SPANNABLE);
			// 设置长按事件监听
			holder.tv.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					// activity.startActivityForResult(
					// (new Intent(activity,
					// ContextMenu.class)).putExtra("position",
					// position).putExtra("type", EMMessage.Type.TXT.ordinal()),
					// ChatActivity.REQUEST_CODE_CONTEXT_MENU);
					return true;
				}
			});
		}

		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				if (holder.timeLength != null)
					holder.timeLength.setVisibility(View.VISIBLE);
				if (holder.staus_iv != null)
					holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				if (holder.timeLength != null)
					holder.timeLength.setVisibility(View.VISIBLE);
				if (holder.staus_iv != null)
					holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				if (holder.timeLength != null)
					holder.timeLength.setVisibility(View.GONE);
				if (holder.staus_iv != null)
					holder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
	}

	/**
	 * 语音通话记录
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleVoiceCallMessage(EMMessage message, ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		holder.tv.setText(txtBody.getMessage());

	}

	private String tiem2String(int time) {
		int hour = (time / 3600);
		int munite = ((time - hour * 3600) / 60);
		int second = (time % 60);
		if (hour == 0) {
			if (munite == 0) {
				return second + "'";
			} else {
				return munite + "'" + second + "''";
			}
		} else {
			return hour + "'" + munite + "''" + second + "'''";
		}
	}

	private int getWidthByDuration(int time) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) (this.context)).getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		float baseWidth = density * 50;
		float increase = density * 120;
		double d = (time) / ((double) (60));
		double tan = (Math.PI / 2) * d;
		int width = (int) ((int) ((double) (Math.sin(tan) * (double) increase)) + baseWidth);
		return width;
	}

	/**
	 * 语音消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
		// holder.tv.setText(voiceBody.getLength() + "\"");
		VoicePlayClickListener voiceListener = new VoicePlayClickListener(message, holder.iv, holder.iv_read_status, this, activity, username);
		if (holder.voiceBg != null) {
			holder.voiceBg.setOnClickListener(voiceListener);
			if (voiceBody.getLength() != 0) {
				LayoutParams params = holder.voiceBg.getLayoutParams();
				int width = getWidthByDuration(voiceBody.getLength());
				params.width = width;
				holder.voiceBg.setLayoutParams(params);
			}
		}
		holder.iv.setOnClickListener(voiceListener);
		if (holder.timeLength != null) {
			holder.timeLength.setText(tiem2String(voiceBody.getLength()));
		}
		if (CoreModel.getInstance().playMsgId != null && CoreModel.getInstance().playMsgId.equals(message.getMsgId()) && VoicePlayClickListener.isPlaying) {
			AnimationDrawable voiceAnimation;
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(R.anim.chat_from_voice_anim);
			} else {
				holder.iv.setImageResource(R.anim.chat_to_voice_anim);
			}
			voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
			voiceAnimation.start();
		} else {
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(R.drawable.chat_from_voice3);
			} else {
				holder.iv.setImageResource(R.drawable.chat_to_voice3);
			}
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (message.isListened()) {
				// 隐藏语音未听标志
				if (holder.iv_read_status != null)
					holder.iv_read_status.setVisibility(View.INVISIBLE);
			} else {
				if (holder.iv_read_status != null)
					holder.iv_read_status.setVisibility(View.VISIBLE);
			}
			System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				holder.pb.setVisibility(View.VISIBLE);
				System.err.println("!!!! back receive");
				((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {

					@Override
					public void onSuccess() {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
								notifyDataSetChanged();
							}
						});

					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(int code, String message) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
							}
						});

					}
				});

			} else {
				if (holder.pb != null)
					holder.pb.setVisibility(View.INVISIBLE);

			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			if (holder.timeLength != null)
				holder.timeLength.setVisibility(View.VISIBLE);
			if (holder.staus_iv != null)
				holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			if (holder.timeLength != null)
				holder.timeLength.setVisibility(View.VISIBLE);
			if (holder.staus_iv != null)
				holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.pb.setVisibility(View.VISIBLE);
			if (holder.timeLength != null)
				holder.timeLength.setVisibility(View.GONE);
			if (holder.staus_iv != null)
				holder.staus_iv.setVisibility(View.GONE);
			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
		if (holder.staus_iv != null) {
			holder.staus_iv.setVisibility(View.GONE);
		}
		if (holder.pb != null) {
			holder.pb.setVisibility(View.VISIBLE);
		}

		final long start = System.currentTimeMillis();
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				updateSendedView(message, holder);
			}

			@Override
			public void onError(int code, String error) {
				updateSendedView(message, holder);
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	/**
	 * 更新ui上消息发送状态
	 * 
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final EMMessage message, final ViewHolder holder) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				if (message.status == EMMessage.Status.SUCCESS) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// holder.staus_iv.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// holder.staus_iv.setVisibility(View.GONE);
					// }

				} else if (message.status == EMMessage.Status.FAIL) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// }
					// holder.staus_iv.setVisibility(View.VISIBLE);
					Toast.makeText(activity, "消息发送失败", 0).show();
				}

				notifyDataSetChanged();
			}
		});
	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView head_iv;
		TextView tv_userId;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;

		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;

		RelativeLayout voiceBg;
	}

}