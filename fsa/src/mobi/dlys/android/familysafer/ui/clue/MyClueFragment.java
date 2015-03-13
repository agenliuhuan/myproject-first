package mobi.dlys.android.familysafer.ui.clue;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ClueImageObject;
import mobi.dlys.android.familysafer.biz.vo.ClueObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.db.dao.ClueObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.slidingmenu.lib.SlidingMenu.OnClosedListener;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView.IXListViewListener;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
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

public class MyClueFragment extends BaseExFragment {
    protected TitleBarHolder mTitleBar;

    private XListView mListview = null;
    private RelativeLayout mNoCluesRL = null;
    private MyclueListViewAdapter mListViewAdapter = null;

    private ArrayList<ClueObject> mMyClues = null;
    private boolean mUpdateData = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_myclue, null);
        initSubView();
        initData();
        return mRootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            displayTip();
            CoreModel.getInstance().setmUpdateMyClueList(false);
            sendEmptyMessage(YSMSG.REQ_GET_CLUE_LIST);
//            if (mIsFragmentVisible && !mUpdateData && CoreModel.getInstance().ismUpdateMyClueList()) {
//                
//            }
        } else {
        }

    }

    public void onResume() {
        super.onResume();
        if (mIsFragmentVisible && !mUpdateData && CoreModel.getInstance().ismUpdateMyClueList()) {
            CoreModel.getInstance().setmUpdateMyClueList(false);
            sendEmptyMessage(YSMSG.REQ_GET_CLUE_LIST);
        }
    }

    private void initSubView() {
        mTitleBar = new TitleBarHolder(getActivity(), mRootView);
        mTitleBar.mTitle.setText(R.string.fragment_myclue_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).toggle();
            }
        });
        mTitleBar.mRight.setBackgroundResource(R.drawable.saferadar_cap_selector);
        mTitleBar.mRight.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(getActivity(), AnalyticsHelper.index_click_shotsnap);
                    }
                }, 1000);

                Intent intent = new Intent(getActivity(), ClueActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "takephoto");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mMyClues = new ArrayList<ClueObject>();

        mListview = (XListView) this.findViewById(R.id.lv_myclue);
        mListview.setSelector(R.drawable.on_item_selected);
        mListview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        mListViewAdapter = new MyclueListViewAdapter(getActivity());

        mListview.setAdapter(mListViewAdapter);

        mNoCluesRL = (RelativeLayout) findViewById(R.id.no_clueRL);

    }

    void initData() {
        displayTip();

        mListview.showFooterView(false);
        mListview.setPullLoadEnable(false);
        mListview.setPullRefreshEnable(true);
        mListview.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                // 下拉刷新
                if (mUpdateData) {
                    mUpdateData = false;
                    sendEmptyMessage(YSMSG.REQ_GET_CACHE_CLUE_LIST);
                    return;
                } else {
                    sendEmptyMessage(YSMSG.REQ_GET_CLUE_LIST);
                }
            }

            @Override
            public void onLoadMore() {
                // 上拉加载更多
                PageInfoObject pageInfo = PageInfoObjectDao.getNextCachePageNo(PageInfoObjectDao.ID_MY_CLUE);
                if (pageInfo != null) {
                    if (pageInfo.isLastPage() && !ClueObjectDao.hasMoreClue(mMyClues.size())) {
                        YSToast.showToast(getActivity(), R.string.toast_no_more_data);
                        mListview.stopLoadMore();
                        mListview.setPullLoadEnable(false);
                    } else {
                        sendMessage(YSMSG.REQ_GET_CACHE_CLUE_LIST, pageInfo.getReadCachePageNo(), 0, pageInfo);
                    }
                }
            }
        });

        ((MainActivity) getActivity()).getSlidingMenu().setOnClosedListener(new OnClosedListener() {

            @Override
            public void onClosed() {
                if (CoreModel.getInstance().isUpdateClueList()) {
                    CoreModel.getInstance().setUpdateClueList(false);
                    mListview.startRefresh();
                }
            }
        });

        mListview.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mListview.startRefresh();
            }
        }, 200);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_CLUE_LIST:
        case YSMSG.RESP_GET_CACHE_CLUE_LIST: {
            mListview.stopLoadMore();
            mListview.stopRefresh();
            dismissWaitingDialog();
            if (msg.arg1 == 200) {

                if (msg.arg2 == 1) {
                    mMyClues.clear();
                }
                List<ClueObject> friendlist = (List<ClueObject>) msg.obj;
                if (null != friendlist) {
                    mMyClues.addAll(friendlist);
                    updateList();
                }
                mListview.setPullLoadEnable(!PageInfoObjectDao.isLastPage(PageInfoObjectDao.ID_MY_CLUE) || ClueObjectDao.hasMoreClue(mMyClues.size()));

                if (mMyClues.size() > 0) {
                    mListview.setVisibility(View.VISIBLE);
                    mNoCluesRL.setVisibility(View.GONE);
                } else {
                    mListview.setVisibility(View.GONE);
                    mNoCluesRL.setVisibility(View.VISIBLE);
                }
            } else {
                // failed
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(getActivity(), result.getErrorMsg());
                } else {
                    YSToast.showToast(getActivity(), R.string.network_error);
                }
            }

        }
            break;
        case YSMSG.RESP_GET_NEW_MSG_NUM:
            if (msg.arg1 == 200) {
                displayTip();
            }
            break;
        }
    }

    @Override
    public void onDestroy() {
        if (null != mMyClues) {
            mMyClues.clear();
        }
        super.onDestroy();
    }

    public void displayTip() {
        mTitleBar.displayTip();
    }

    private void updateList() {
        if (null != mListViewAdapter) {
            mListViewAdapter.notifyDataSetChanged();
        }
    }

    private class MyclueListViewAdapter extends BaseAdapter {
        private Context mContext;

        public MyclueListViewAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mMyClues.size();
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
                convertView = LayoutInflater.from(this.mContext).inflate(R.layout.list_item_myclue, null, false);
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

            if (mMyClues != null) {
                ClueObject gridItem = mMyClues.get(position);
                if (null != gridItem) {
                    if (holder.mImage != null && CoreModel.getInstance().getUserInfo() != null) {
                        String imageFile = CoreModel.getInstance().getUserInfo().getImage();
                        if (!TextUtils.isEmpty(imageFile)) {
                            ImageLoaderHelper.displayImage(imageFile, holder.mImage, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
                        }
                    }
                    if (holder.mName != null) {
                        String name = CoreModel.getInstance().getUserInfo().getNickname();
                        if (!TextUtils.isEmpty(name)) {
                            holder.mName.setText(name);
                            holder.mName.setVisibility(View.VISIBLE);
                        } else {
                            holder.mName.setVisibility(View.GONE);
                        }
                    }
                    if (holder.mDate != null) {
                        String date = gridItem.getCreateTime();
                        if (!TextUtils.isEmpty(date)) {
                            holder.mDate.setText(date);
                            holder.mDate.setVisibility(View.VISIBLE);
                        } else {
                            holder.mDate.setVisibility(View.GONE);
                        }
                    }
                    if (holder.mContent != null) {
                        String content = gridItem.getMessage();
                        if (!TextUtils.isEmpty(content)) {
                            holder.mContent.setText(content);
                            holder.mContent.setVisibility(View.VISIBLE);
                        } else {
                            holder.mContent.setVisibility(View.GONE);
                        }
                    }
                    if (holder.mLocation != null) {
                        String location = gridItem.getLocation();
                        if (!TextUtils.isEmpty(location)) {
                            holder.mLocation.setText(location);
                            holder.mLocation.setVisibility(View.VISIBLE);
                        } else {
                            holder.mLocation.setVisibility(View.GONE);
                        }
                    }
                    // if (holder.mPhone != null) {
                    // String phone = gridItem.getModel();
                    // if (TextUtils.isEmpty(phone)) {
                    // phone = AndroidConfig.getPhoneModel();
                    // }
                    // if (!TextUtils.isEmpty(phone)) {
                    // holder.mPhone.setText(phone);
                    // holder.mPhone.setVisibility(View.VISIBLE);
                    // } else {
                    // holder.mPhone.setVisibility(View.GONE);
                    // }
                    // }
                    if (holder.mImages != null) {
                        ArrayList<ClueImageObject> images = gridItem.getImageList();

                        if (null != images) {
                            MyClueGridAdapter gridViewAdapter = new MyClueGridAdapter(mContext, images, position);
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
            int Width = (int) mContext.getResources().getDimension(R.dimen.one_image_width);
            int Height = (int) mContext.getResources().getDimension(R.dimen.one_image_height);
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
                    Intent intent = new Intent(getActivity(), ViewerActivity.class);
                    intent.putStringArrayListExtra("clues", mMyClues.get(rootposition).getImageUrlList());
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
                    if (imageFile.equals("takephoto")) {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 5) / 4, (Screenwidth - imageMargin * 5) / 4);
                        mImage.setLayoutParams(params);
                        mImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        mImage.setImageResource(R.drawable.cap);
                    } else if (!TextUtils.isEmpty(imageFile)) {
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
