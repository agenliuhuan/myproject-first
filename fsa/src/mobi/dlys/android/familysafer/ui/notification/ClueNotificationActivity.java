package mobi.dlys.android.familysafer.ui.notification;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ClueImageObject;
import mobi.dlys.android.familysafer.biz.vo.EventObjectEx;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.db.dao.EventContentDao;
import mobi.dlys.android.familysafer.db.dao.EventObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.clue.ViewerActivity;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView.IXListViewListener;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.PreferencesUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ClueNotificationActivity extends BaseExActivity {
    protected TitleBarHolder mTitleBar;
    private RelativeLayout mNoNotificationRL = null;
    private XListView mListview = null;
    private ArrayList<EventObjectEx> mNotificationsList = null;
    private boolean mUpdateData = true;
    MyclueListViewAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initSubView();
        initData();
    }

    private void initData() {
        mListview.showFooterView(false);
        mListview.setPullLoadEnable(false);
        mListview.setPullRefreshEnable(true);
        mListview.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                if (mUpdateData) {
                    mUpdateData = false;
                    sendMessage(YSMSG.REQ_GET_CACHE_CHECKIN_MSG_LIST, 0, 0, PageInfoObjectDao.getFirstPageInfo(PageInfoObjectDao.ID_EVENT_CLUE));
                } else {
                    sendMessage(YSMSG.REQ_GET_CHECKIN_MSG_LIST, 0, 0, PageInfoObjectDao.getFirstPageInfo(PageInfoObjectDao.ID_EVENT_CLUE));
                    PreferencesUtils.setNewEventCount(CoreModel.getInstance().getUserId(), 0);
                }
            }

            @Override
            public void onLoadMore() {
                PageInfoObject pageInfo = PageInfoObjectDao.getNextCachePageNo(PageInfoObjectDao.ID_EVENT_CLUE);
                if (pageInfo != null) {
                    if (pageInfo.isLastPage() && !EventObjectDao.hasMoreEvent(mNotificationsList.size(), PageInfoObjectDao.ID_EVENT_CLUE)) {
                        YSToast.showToast(ClueNotificationActivity.this, R.string.toast_no_more_data);
                        mListview.stopLoadMore();
                        mListview.setPullLoadEnable(false);
                    } else {
                        sendMessage(YSMSG.REQ_GET_CACHE_CHECKIN_MSG_LIST, 0, 0, pageInfo);
                    }
                }
            }
        });
        if (new EventContentDao().count() <= 0) {
            PreferencesUtils.setNewEventCount(CoreModel.getInstance().getUserId(), 0);
            CoreModel.getInstance().setUpdateEventList(false);
        }
        mListview.setVisibility(View.VISIBLE);
        
    }

    private void initSubView() {
        mTitleBar = new TitleBarHolder(this);
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
        mTitleBar.mTitle.setText(R.string.fragment_notification_clue_title);
        mTitleBar.mRight.setVisibility(View.INVISIBLE);
        mNoNotificationRL = (RelativeLayout) findViewById(R.id.no_notificationRL);
        mListview = (XListView) this.findViewById(R.id.lv_mynotifycation);
        mNotificationsList = new ArrayList<EventObjectEx>();
        adapter = new MyclueListViewAdapter(mNotificationsList);
        mListview.setAdapter(adapter);
    }

    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (CoreModel.getInstance().isUpdateEventList()) {
                    CoreModel.getInstance().setUpdateEventList(false);
                }
                mUpdateData = false;
                mListview.startRefresh();
            }
        }, 200);
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_CHECKIN_MSG_LIST:
        case YSMSG.RESP_GET_CACHE_CHECKIN_MSG_LIST: {
            mListview.stopRefresh();
            mListview.stopLoadMore();
            if (msg.arg1 == 200) {
                if (msg.arg2 == 1) {
                    mNotificationsList.clear();
                }

                List<EventObjectEx> list = (ArrayList<EventObjectEx>) msg.obj;
                if (list != null) {
                    mNotificationsList.addAll(list);
                    updateList();
                }
                mListview.setPullLoadEnable(!PageInfoObjectDao.isLastPage(PageInfoObjectDao.ID_EVENT_CLUE) || EventObjectDao.hasMoreEvent(mNotificationsList.size(), PageInfoObjectDao.ID_EVENT_CLUE));

                if (mNotificationsList.size() > 0) {
                    mNoNotificationRL.setVisibility(View.GONE);
                    mListview.setVisibility(View.VISIBLE);
                } else {
                    mListview.setVisibility(View.GONE);
                    mNoNotificationRL.setVisibility(View.VISIBLE);
                }
            } else {
                // failed
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(getBaseContext(), result.getErrorMsg());
                } else {
                    YSToast.showToast(getBaseContext(), R.string.network_error);
                }
            }
        }
            break;
        case YSMSG.RESP_CHECK_IN_CONFIRM:
            if (msg.arg1 == 200) {

            } else {

            }
            break;
        case YSMSG.RESP_VOICE_SOS_CONFIRM:
            if (msg.arg1 == 200) {

            } else {

            }
            break;
        case YSMSG.RESP_GET_NEW_MSG_NUM:
            if (msg.arg1 == 200) {

            }
            break;
        }

    }

    void updateList() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    private class MyclueListViewAdapter extends BaseAdapter {
        ArrayList<EventObjectEx> data;

        public MyclueListViewAdapter(ArrayList<EventObjectEx> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.list_item_myclue, null, false);
                holder.mImage = (ImageView) convertView.findViewById(R.id.img_myclue_user);
                holder.mName = (TextView) convertView.findViewById(R.id.tv_myclue_nickname);
                holder.mDate = (TextView) convertView.findViewById(R.id.tv_myclue_date);
                holder.mContent = (TextView) convertView.findViewById(R.id.tv_myclue_content);
                holder.mLocation = (TextView) convertView.findViewById(R.id.tv_myclue_location);
                holder.mImages = (GridView) convertView.findViewById(R.id.gv_myclue_images);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (data != null) {
                EventObjectEx notification = data.get(position);
                if (null != notification) {
                    if (holder.mImage != null && CoreModel.getInstance().getUserInfo() != null) {
                        String imageFile = notification.getImage();
                        if (!TextUtils.isEmpty(imageFile)) {
                            ImageLoaderHelper.displayImage(imageFile, holder.mImage, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
                        } else {
                            holder.mImage.setImageResource(R.drawable.user);
                        }
                    }
                    if (holder.mName != null) {
                        FriendObject fo = CoreModel.getInstance().getFriendObjectByUserId(notification.getUserId());
                        if (fo != null && !TextUtils.isEmpty(fo.getRemarkName())) {
                            holder.mName.setText(fo.getRemarkName());
                        } else {
                            holder.mName.setText(notification.getNickname());
                        }
                    }
                    if (holder.mDate != null) {
                        String date = notification.getCreateTime();
                        if (!TextUtils.isEmpty(date)) {
                            holder.mDate.setText(date);
                            holder.mDate.setVisibility(View.VISIBLE);
                        } else {
                            holder.mDate.setVisibility(View.GONE);
                        }
                    }
                    if (holder.mContent != null) {
                        String content = notification.getClue().getMessage();
                        if (!TextUtils.isEmpty(content)) {
                            holder.mContent.setText(content);
                            holder.mContent.setVisibility(View.VISIBLE);
                        } else {
                            holder.mContent.setVisibility(View.GONE);
                        }
                    }
                    if (holder.mLocation != null) {
                        String location = notification.getClue().getLocation();
                        if (!TextUtils.isEmpty(location)) {
                            holder.mLocation.setText(location);
                            holder.mLocation.setVisibility(View.VISIBLE);
                        } else {
                            holder.mLocation.setVisibility(View.GONE);
                        }
                    }
                    if (holder.mImages != null) {
                        ArrayList<ClueImageObject> images = notification.getClue().getImageList();

                        if (null != images) {
                            MyClueGridAdapter gridViewAdapter = new MyClueGridAdapter(getBaseContext(), images, position);
                            holder.mImages.setAdapter(gridViewAdapter);
                            setGridParams(holder.mImages, images.size(), images);
                            holder.mImages.setOnTouchListener(new OnTouchListener() {
                                public boolean onTouch(View arg0, MotionEvent event) {
                                    return MotionEvent.ACTION_MOVE == event.getAction() ? true : false;
                                }
                            });
                        }
                    }
                }
            }
            return convertView;

        }

        private void setGridParams(GridView mGrid, int count, ArrayList<ClueImageObject> images) {
            int Width = (int) getBaseContext().getResources().getDimension(R.dimen.one_image_width);
            int Height = (int) getBaseContext().getResources().getDimension(R.dimen.one_image_height);
            int Screenwidth = AndroidConfig.getScreenWidth();
            LayoutParams params;
            int imageMargin = (int) getResources().getDimension(R.dimen.image_margin);
            switch (count) {
            case 1:
                ClueImageObject image = images.get(0);
                if (image.getWidth() != 0) {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    if (width < height) {
                        params = new LayoutParams(Width, Height);
                    } else {
                        params = new LayoutParams(Height, Width);
                    }
                } else {
                    params = new LayoutParams(Width, Height);
                }
                params.setMargins(0, imageMargin * 2, 0, imageMargin * 2);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(1);
                break;
            case 2:
                params = new LayoutParams((Screenwidth - imageMargin * 5) / 2 + 5, (Screenwidth - imageMargin * 5) / 4);
                params.setMargins(0, imageMargin * 2, 0, imageMargin * 2);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(2);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            case 3:
                params = new LayoutParams((Screenwidth - imageMargin * 5) / 4 * 3 + imageMargin + 5, (Screenwidth - imageMargin * 5) / 4);
                params.setMargins(0, imageMargin * 2, 0, imageMargin * 2);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(3);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            case 4:
                params = new LayoutParams((Screenwidth - imageMargin * 5) / 2 + 5, (Screenwidth - imageMargin * 5) / 2 + imageMargin);
                params.setMargins(0, imageMargin * 2, 0, imageMargin * 2);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(2);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            case 5:
            case 6:
                params = new LayoutParams((Screenwidth - imageMargin * 5) / 4 * 3 + imageMargin + 5, (Screenwidth - imageMargin * 5) / 4 * 2 + imageMargin);
                params.setMargins(0, imageMargin * 2, 0, imageMargin * 2);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(3);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            case 7:
            case 8:
                params = new LayoutParams((Screenwidth - imageMargin * 5) / 4 * 3 + imageMargin + 5, (Screenwidth - imageMargin * 5) / 4 * 3 + imageMargin * 2);
                params.setMargins(0, imageMargin * 2, 0, imageMargin * 2);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(3);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            }
        }

        public final class ViewHolder {
            ImageView mImage;
            TextView mName;
            TextView mDate;
            TextView mContent;
            TextView mLocation;
            GridView mImages;
        }
    }

    private class MyClueGridAdapter extends BaseAdapter {
        private Context mContext;
        public ArrayList<ClueImageObject> mList;
        int Width;
        int Height;
        int rootposition;

        public MyClueGridAdapter(Context contect, ArrayList<ClueImageObject> list, int position) {
            this.mContext = contect;
            this.mList = list;
            Width = (int) mContext.getResources().getDimension(R.dimen.one_image_width);
            Height = (int) mContext.getResources().getDimension(R.dimen.one_image_height);
            this.rootposition = position;
        }

        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            } else {
                return this.mList.size();
            }
        }

        @Override
        public ClueImageObject getItem(int position) {
            if (mList == null) {
                return null;
            } else {
                return this.mList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_clue_image, null);
            }
            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    Intent intent = new Intent(getBaseContext(), ViewerActivity.class);
                    intent.putStringArrayListExtra("clues", mNotificationsList.get(rootposition).getClue().getImageUrlList());
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
            ImageView mImage = (ImageView) convertView.findViewById(R.id.item_clueimage_img);
            if (this.mList != null && mList.size() != 0) {
                ClueImageObject clueImage = this.mList.get(position);
                int Screenwidth = AndroidConfig.getScreenWidth();
                int imageMargin = (int) mContext.getResources().getDimension(R.dimen.image_margin);
                if (mImage != null && clueImage != null) {
                    String imageFile = clueImage.getImage();
                    if (!TextUtils.isEmpty(imageFile)) {
                        if (mList.size() == 1) {
                            if (clueImage.getWidth() != 0) {
                                int width = clueImage.getWidth();
                                int height = clueImage.getHeight();
                                if (width < height) {
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Width, Height);
                                    mImage.setLayoutParams(params);
                                    mImage.setScaleType(ImageView.ScaleType.FIT_XY);
                                    ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
                                } else {
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Height, Width);
                                    mImage.setLayoutParams(params);
                                    mImage.setScaleType(ImageView.ScaleType.FIT_XY);
                                    ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
                                }
                            } else {
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Width, Height);
                                mImage.setLayoutParams(params);
                                mImage.setScaleType(ImageView.ScaleType.FIT_XY);
                                ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
                            }
                        } else {
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 5) / 4, (Screenwidth - imageMargin * 5) / 4);
                            mImage.setLayoutParams(params);
                            mImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
                        }
                    } else {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 5) / 4, (Screenwidth - imageMargin * 5) / 4);
                        mImage.setLayoutParams(params);
                        mImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        mImage.setImageResource(R.drawable.default_bg_image);
                    }
                }
            }
            return convertView;
        }
    }

}
