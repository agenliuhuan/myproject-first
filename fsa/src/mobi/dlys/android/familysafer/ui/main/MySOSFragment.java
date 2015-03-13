package mobi.dlys.android.familysafer.ui.main;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.image.universalimageloader.core.assist.FailReason;
import mobi.dlys.android.core.image.universalimageloader.core.listener.ImageLoadingListener;
import mobi.dlys.android.core.image.universalimageloader.core.listener.ImageLoadingProgressListener;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.MySOSObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.db.dao.MySOSObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.player.PlayerManage;
import mobi.dlys.android.familysafer.player.PlayerMusicInfo;
import mobi.dlys.android.familysafer.player.utils.Player;
import mobi.dlys.android.familysafer.player.utils.Player.PlayState;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.slidingmenu.lib.SlidingMenu.OnClosedListener;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView.IXListViewListener;
import mobi.dlys.android.familysafer.ui.notification.NotificationDetail1Activity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MySOSFragment extends BaseExFragment {
    protected TitleBarHolder mTitleBar;
    private RelativeLayout mNoSOSRL;
    private XListView mListView;
    private boolean mUpdateData = true;
    private List<MySOSObject> mySOSList = null;
    private SOSAdapter sosAdapter = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_mysos, null);
        initSubView();
        initData();
        return mRootView;

    }

    private void initData() {
        mListView.showFooterView(false);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);

        mListView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                // 下拉刷新
                if (mUpdateData) {
                    mUpdateData = false;
                    sendEmptyMessage(YSMSG.REQ_GET_CACHE_VOICE_SOS_LIST);
                    return;
                } else {
                    sendEmptyMessage(YSMSG.REQ_GET_VOICE_SOS_LIST);
                }
            }

            @Override
            public void onLoadMore() {
                // 上拉加载更多
                PageInfoObject pageInfo = PageInfoObjectDao.getNextCachePageNo(PageInfoObjectDao.ID_MY_SOS);
                if (pageInfo != null) {
                    if (pageInfo.isLastPage() && !MySOSObjectDao.hasMoreSOS(mySOSList.size())) {
                        YSToast.showToast(getActivity(), R.string.toast_no_more_data);
                        mListView.stopLoadMore();
                        mListView.setPullLoadEnable(false);
                    } else {
                        sendMessage(YSMSG.REQ_GET_CACHE_VOICE_SOS_LIST, pageInfo.getReadCachePageNo(), 0, pageInfo);
                    }
                }
            }
        });

        ((MainActivity) getActivity()).getSlidingMenu().setOnClosedListener(new OnClosedListener() {

            @Override
            public void onClosed() {
                if (CoreModel.getInstance().isUpdateClueList()) {
                    CoreModel.getInstance().setUpdateClueList(false);
                    mListView.startRefresh();
                }
            }
        });
        mListView.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mListView.startRefresh();
            }
        }, 200);
    }

    private void startAnimation(ImageView view) {
        if (null == view) {
            return;
        }
        view.setBackgroundResource(R.anim.voiceto);
        AnimationDrawable anim = (AnimationDrawable) view.getBackground();
        if (anim != null) {
            anim.start();
        }
    }

    private void stopAnimation(ImageView view) {

        if (null == view) {
            return;
        }

        if (view.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable anim = (AnimationDrawable) view.getBackground();
            if (anim != null && anim.isRunning()) {
                anim.stop();
                anim.selectDrawable(2);
            }
        }
    }

    /**
     * 接收音乐播放广播
     */
    private BroadcastReceiver mPlayerStatusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String strAction = "";

            try {
                strAction = intent.getAction();
                if (strAction.equals(PlayerManage.ACTION_NET_NOT_CONNECTION)) {

                } else if (strAction.equals(PlayerManage.ACTION_PLAYER)) {
                    Bundle bundle = intent.getExtras();
                    int nState = bundle.getInt(PlayerManage.ACTION_PLAYER_STATE);
                    int nMusicId = bundle.getInt(PlayerManage.ACTION_PLAYER_MUSICID);
                    int nEventId = bundle.getInt(PlayerManage.ACTION_PLAYER_EVENTID);
                    if ((nState == PlayState.STOP) || (nState == PlayState.PREPARE)) {
                        ImageView btnRecord = (ImageView) mListView.findViewWithTag("voice" + nMusicId);
                        stopAnimation(btnRecord);
                    } else if (nState == PlayState.NEW_SONG) {
                        ImageView btnRecord = (ImageView) mListView.findViewWithTag("voice" + nMusicId);
                        startAnimation(btnRecord);
                    } else if (nState == PlayState.BUFFER || nState == PlayState.PLAY) {
                        ImageView btnRecord = (ImageView) mListView.findViewWithTag("voice" + nMusicId);
                        startAnimation(btnRecord);
                    }
                }
            } catch (Exception e) {
            }
        }
    };

    private void playOrPauseMusic(MySOSObject mysos) {
        PlayerMusicInfo musicInfo = new PlayerMusicInfo();
        Player.getMusicInfo(musicInfo);

        if (TextUtils.isEmpty(mysos.getVoiceUrl())) {
            return;
        }

        if (Player.isPlaying() && musicInfo.nMusicID == mysos.getVoiceSosId()) {
            Player.stop();
        } else {
            Player.replaceMusic(PlayerMusicInfo.convert(mysos));
        }
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_VOICE_SOS_LIST:
        case YSMSG.RESP_GET_CACHE_VOICE_SOS_LIST:
            mListView.stopLoadMore();
            mListView.stopRefresh();
            dismissWaitingDialog();
            if (msg.arg1 == 200) {

                if (msg.arg2 == 1) {
                    mySOSList.clear();
                }
                List<MySOSObject> friendlist = (List<MySOSObject>) msg.obj;
                if (null != friendlist) {
                    mySOSList.addAll(friendlist);
                    updateList();
                }
                mListView.setPullLoadEnable(!PageInfoObjectDao.isLastPage(PageInfoObjectDao.ID_MY_SOS) || MySOSObjectDao.hasMoreSOS(mySOSList.size()));

                if (mySOSList.size() > 0) {
                    mListView.setVisibility(View.VISIBLE);
                    mNoSOSRL.setVisibility(View.GONE);
                } else {
                    mListView.setVisibility(View.GONE);
                    mNoSOSRL.setVisibility(View.VISIBLE);
                }
            } else {
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(getActivity(), result.getErrorMsg());
                } else {
                    YSToast.showToast(getActivity(), R.string.network_error);
                }
            }

            break;
        }
    }

    public void onResume() {
        super.onResume();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mIsFragmentVisible && !mUpdateData && CoreModel.getInstance().ismUpdateMyClueList()) {
                CoreModel.getInstance().setmUpdateMyClueList(false);
                sendEmptyMessage(YSMSG.REQ_GET_VOICE_SOS_LIST);
            }
            if (Player.isPlaying()) {
                Player.stop();
            }
        } else {
            if (Player.isPlaying()) {
                Player.pause();
            }
        }
    }

    public void onDestroy() {
        if (null != mySOSList) {
            mySOSList.clear();
        }
        Player.stop();
        getActivity().unregisterReceiver(mPlayerStatusReceiver);
        super.onDestroy();
    }

    public void onPause() {
        super.onPause();

    }

    private void initSubView() {
        mTitleBar = new TitleBarHolder(getActivity(), mRootView);
        mTitleBar.mTitle.setText(R.string.fragment_mysos_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).toggle();
            }
        });
        mTitleBar.mRight.setVisibility(View.INVISIBLE);

        mNoSOSRL = (RelativeLayout) findViewById(R.id.no_mysosRL);
        mListView = (XListView) this.findViewById(R.id.lv_mysos);
        mySOSList = new ArrayList<MySOSObject>();
        sosAdapter = new SOSAdapter(getActivity());
        mListView.setAdapter(sosAdapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerManage.ACTION_PLAYER);
        intentFilter.addAction(PlayerManage.ACTION_NET_NOT_CONNECTION);
        getActivity().registerReceiver(mPlayerStatusReceiver, intentFilter);
    }

    private void updateList() {
        if (null != sosAdapter) {
            sosAdapter.notifyDataSetChanged();
        }
    }

    class SOSAdapter extends BaseAdapter {
        private Context mContext;

        public SOSAdapter(Context context) {
            this.mContext = context;
        }

        public int getCount() {
            return mySOSList.size();
        }

        public MySOSObject getItem(int position) {
            return mySOSList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(this.mContext).inflate(R.layout.list_item_mysos, null, false);
                holder.userImage = (ImageView) convertView.findViewById(R.id.item_mysos_user_image);
                holder.mapImage = (ImageView) convertView.findViewById(R.id.item_mysos_mapimage);
                holder.mapImageDian = (ImageView) convertView.findViewById(R.id.item_mysos_mapimage_dian);
                holder.statusImage = (ImageView) convertView.findViewById(R.id.item_mysos_status_img);
                holder.statusTV = (TextView) convertView.findViewById(R.id.item_mysos_status_tv);
                holder.voiceLL = (RelativeLayout) convertView.findViewById(R.id.item_mysos_voice_LL);
                holder.voiceTV = (TextView) convertView.findViewById(R.id.item_mysos_voiceTV);
                holder.voiceImg = (ImageView) convertView.findViewById(R.id.item_mysos_voiceImg);
                holder.dateTV = (TextView) convertView.findViewById(R.id.item_mysos_date_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    MySOSObject sosObj = mySOSList.get(position);
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), NotificationDetail1Activity.class);
                    intent.putExtra(NotificationDetail1Activity.EXTRA_EVENT_OBJECT, sosObj);
                    startActivity(intent);
                }
            });

            final View contentView = convertView;

            if (mySOSList != null) {
                final MySOSObject sosObj = mySOSList.get(position);
                if (holder.userImage != null && CoreModel.getInstance().getUserInfo() != null) {
                    String imageFile = CoreModel.getInstance().getUserInfo().getImage();
                    if (!TextUtils.isEmpty(imageFile)) {
                        ImageLoaderHelper.displayImage(imageFile, holder.userImage, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
                    }
                }

                holder.mapImageDian.setTag("dian" + sosObj.getVoiceSosId());
                holder.mapImage.setTag(sosObj.getVoiceSosId());
                holder.mapImageDian.setVisibility(View.GONE);

                if (holder.mapImage != null && sosObj.getLat() != null && sosObj.getLng() != null) {
                    int width = getResources().getDimensionPixelSize(R.dimen.list_item_mysos_map_width);
                    int height = getResources().getDimensionPixelSize(R.dimen.list_item_mysos_map_height);
                    String mapurl = "http://api.map.baidu.com/staticimage?center=" + sosObj.getLng() + "," + sosObj.getLat() + "&width=" + width + "&height=" + height + "&zoom=19";
                    ImageLoaderHelper.displayImage(mapurl, holder.mapImage, R.drawable.default_bg_image, false, new ImageLoadingListener() {
                        public void onLoadingStarted(String imageUri, View view) {
                            String tag = "dian" + view.getTag(); // sosObj.getVoiceSosId();
                            View dianview = contentView.findViewWithTag(tag);
                            if (dianview != null) {
                                dianview.setVisibility(View.GONE);
                            }
                        }

                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            String tag = "dian" + view.getTag(); // sosObj.getVoiceSosId();
                            View dianview = contentView.findViewWithTag(tag);
                            if (dianview != null) {
                                dianview.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    }, new ImageLoadingProgressListener() {
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {

                        }
                    });
                }
                if (holder.dateTV != null && !TextUtils.isEmpty(sosObj.getCreateTime())) {
                    holder.dateTV.setText(sosObj.getCreateTime());
                }

                if (holder.statusTV != null && !TextUtils.isEmpty(sosObj.getLocation())) {
                    holder.statusImage.setImageResource(R.drawable.icon_sos);
                    String location = sosObj.getLocation();
                    if (location.contains("|")) {
                        location = location.replace("|", "");
                    }
                    holder.statusTV.setText(location);
                }

                if (holder.voiceTV != null && !TextUtils.isEmpty(sosObj.getDuration())) {
                    holder.voiceTV.setText(sosObj.getDuration());
                }
                if (!TextUtils.isEmpty(sosObj.getVoiceUrl())) {
                    holder.voiceImg.setTag("voice" + sosObj.getVoiceSosId());
                    if (Player.isPlaying()) {
                        PlayerMusicInfo musicInfo = new PlayerMusicInfo();
                        Player.getMusicInfo(musicInfo);
                        if (musicInfo.nMusicID == sosObj.getVoiceSosId()) {
                            startAnimation(holder.voiceImg);
                        } else {
                            stopAnimation(holder.voiceImg);
                        }
                    } else {
                        stopAnimation(holder.voiceImg);
                    }
                } else {
                    holder.voiceLL.setVisibility(View.GONE);
                }
            }
            holder.voiceLL.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    MySOSObject sosObj = mySOSList.get(position);
                    playOrPauseMusic(sosObj);
                }
            });
            return convertView;
        }

        public final class ViewHolder {
            ImageView userImage;
            ImageView mapImage;
            ImageView mapImageDian;
            ImageView statusImage;
            TextView statusTV;
            RelativeLayout voiceLL;
            TextView voiceTV;
            ImageView voiceImg;
            TextView dateTV;
        }

    }

}
