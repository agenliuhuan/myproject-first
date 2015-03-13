package mobi.dlys.android.familysafer.ui.family;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.db.dao.FriendObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyFamilyFragment extends BaseExFragment {
    protected TitleBarHolder mTitleBar;

    private XListView mListview = null;
    private ArrayList<FriendObject> mMyfamilyList = null;
    private MyfamilyAdapter mMyfamilyAdapter = null;

    private RelativeLayout noFriendRL;
    private boolean mUpdateData = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_myfamily, null);
        initSubView();
        initData();
        return mRootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            displayTip();
            if (CoreModel.getInstance().isUpdateFriendList()) {
                CoreModel.getInstance().setUpdateFriendList(false);
                mListview.startRefresh();
            }
        } else {
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mIsFragmentVisible && !mUpdateData) {
            refreshData();
        }
    }

    private void initSubView() {
        mTitleBar = new TitleBarHolder(getActivity(), mRootView);
        mTitleBar.mTitle.setText(R.string.sliding_menu_my_family);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mRight.setText(R.string.titlebar_button_tip_add);
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).toggle();
            }
        });

        mTitleBar.mRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(getActivity(), AnalyticsHelper.index_add_family);
                    }
                }, 1000);
                Intent intent = new Intent(getActivity(), AddFamily2Activity.class);
                startActivity(intent);
            }
        });
        mTitleBar.mRight.setVisibility(View.VISIBLE);

        mListview = (XListView) this.findViewById(R.id.lv_myfamily);
        mMyfamilyList = new ArrayList<FriendObject>();

        mMyfamilyAdapter = new MyfamilyAdapter(getActivity());
        mListview.setAdapter(mMyfamilyAdapter);

        mListview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mListview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                FriendObject fo = null;
                if (position - 1 >= 0 && position - 1 < mMyfamilyList.size()) {
                    fo = mMyfamilyList.get(position - 1);
                }
                if (null != fo) {
                    FamilyDetailActivity.startActivity(getActivity(), fo.getUserId());
                }
            }
        });

        noFriendRL = (RelativeLayout) findViewById(R.id.no_friendRL);
    }

    private void initData() {
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
                    sendEmptyMessage(YSMSG.REQ_GET_CACHE_FRIEND_LIST);
                    return;
                } else {
                    sendEmptyMessage(YSMSG.REQ_GET_FRIEND_LIST);
                }
            }

            @Override
            public void onLoadMore() {
                // 上拉加载更多
                PageInfoObject pageInfo = PageInfoObjectDao.getNextCachePageNo(PageInfoObjectDao.ID_FRIEND);
                if (pageInfo != null) {
                    if (pageInfo.isLastPage() && !FriendObjectDao.hasMoreFriend(mMyfamilyList.size())) {
                        YSToast.showToast(getActivity(), R.string.toast_no_more_data);
                        mListview.stopLoadMore();
                        mListview.setPullLoadEnable(false);
                    } else {
                        sendMessage(YSMSG.REQ_GET_CACHE_FRIEND_LIST, pageInfo.getReadCachePageNo(), 0, pageInfo);
                    }
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

    private void refreshData() {
        mListview.startRefresh();
    }

    public void displayTip() {
        mTitleBar.displayTip();
    }

    void updateList() {
        if (null != mMyfamilyAdapter) {
            mMyfamilyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_FRIEND_LIST:
        case YSMSG.RESP_GET_CACHE_FRIEND_LIST: {
            mListview.stopLoadMore();
            mListview.stopRefresh();
            dismissWaitingDialog();
            if (msg.arg1 == 200) {

                if (msg.arg2 == 1) {
                    mMyfamilyList.clear();
                }
                List<FriendObject> friendlist = (List<FriendObject>) msg.obj;
                if (null != friendlist) {
                    mMyfamilyList.addAll(friendlist);
                    updateList();
                }
                mListview.setPullLoadEnable(!PageInfoObjectDao.isLastPage(PageInfoObjectDao.ID_FRIEND) || FriendObjectDao.hasMoreFriend(mMyfamilyList.size()));

                if (mMyfamilyList.size() > 0) {
                    noFriendRL.setVisibility(View.GONE);
                    mListview.setVisibility(View.VISIBLE);
                } else {
                    noFriendRL.setVisibility(View.VISIBLE);
                    mListview.setVisibility(View.GONE);
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

    private class MyfamilyAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public MyfamilyAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mMyfamilyList.size();
        }

        @Override
        public FriendObject getItem(int position) {
            return mMyfamilyList.get(position);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == mInflater) {
                mInflater = LayoutInflater.from(mContext);
            }

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_myfamily, null);

                holder.imgMyFamily = (ImageView) convertView.findViewById(R.id.img_myfamily_image);
                holder.txtName = (TextView) convertView.findViewById(R.id.tv_myfamily_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position >= 0 && position < mMyfamilyList.size()) {
                final FriendObject family = mMyfamilyList.get(position);
                if (family != null) {
                    String imageFile = family.getImage();
                    if (!TextUtils.isEmpty(imageFile)) {
                        ImageLoaderHelper.displayImage(imageFile, holder.imgMyFamily, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
                    } else {
                        holder.imgMyFamily.setImageResource(R.drawable.user);
                    }
                    if (!TextUtils.isEmpty(family.getRemarkName())) {
                        holder.txtName.setText(family.getRemarkName());
                    } else {
                        holder.txtName.setText(family.getNickname());
                    }

                }
            }

            return convertView;
        }

        public final class ViewHolder {
            public ImageView imgMyFamily;
            public TextView txtName;

        }
    }

}
