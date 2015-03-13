package mobi.dlys.android.familysafer.ui.family;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.FriendRequestObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.db.dao.FriendRequestObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.Relation;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.UserInfo;
import mobi.dlys.android.familysafer.ui.comm.BaseExFragment;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView.IXListViewListener;
import mobi.dlys.android.familysafer.ui.main.MainActivity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FamilyRequestFragment extends BaseExFragment {
    protected TitleBarHolder mTitleBar;

    private XListView mListview = null;

    private ArrayList<FriendRequestObject> mRequestsList = null;
    private RequestsAdapter mRequestsAdapter = null;

    private RelativeLayout noRequestRL;
    private FriendRequestObject fro;

    private boolean mUpdateData = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_familyrequest, null);
        initSubView();
        initData();
        return mRootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            displayTip();
            if (CoreModel.getInstance().isUpdateFriendRequestList()) {
                CoreModel.getInstance().setUpdateFriendRequestList(false);
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
        mTitleBar.mTitle.setText(R.string.fragment_familyrequest_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).toggle();
            }
        });
        mTitleBar.mRight.setVisibility(View.INVISIBLE);

        mListview = (XListView) this.findViewById(R.id.lv_familyrequest);
        mRequestsList = new ArrayList<FriendRequestObject>();

        mRequestsAdapter = new RequestsAdapter(getActivity());
        mListview.setAdapter(mRequestsAdapter);

        mListview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mListview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                if (position - 1 >= 0 && position - 1 < mRequestsAdapter.getCount()) {
                    fro = mRequestsAdapter.getItem(position - 1);
                }

                List<String> list = new ArrayList<String>();
                list.add(fro.getPhone());
                sendMessage(YSMSG.REQ_CHECK_USER_RELATION, 0, 0, list);
                showWaitingDialog();
            }
        });
        noRequestRL = (RelativeLayout) findViewById(R.id.no_requestRL);
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
                sendEmptyMessage(YSMSG.REQ_GET_FRIEND_REQUEST_LIST);

                if (CoreModel.getInstance().getFriendReqCount() > 0) {
                    sendEmptyMessage(YSMSG.REQ_GET_FRIEND_LIST);
                    clearFriendReqCount();
                }
            }

            @Override
            public void onLoadMore() {
                // 上拉加载更多
                PageInfoObject pageInfo = PageInfoObjectDao.getNextCachePageNo(PageInfoObjectDao.ID_FRIEND_REQUEST);
                if (pageInfo != null) {
                    if (pageInfo.isLastPage() && !FriendRequestObjectDao.hasMoreFriendRequest(mRequestsList.size())) {
                        YSToast.showToast(getActivity(), R.string.toast_no_more_data);
                        mListview.stopLoadMore();
                        mListview.setPullLoadEnable(false);
                    } else {
                        sendMessage(YSMSG.REQ_GET_CACHE_FRIEND_REQUEST_LIST, pageInfo.getReadCachePageNo(), 0, pageInfo);
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
        if (null != mRequestsAdapter) {
            mRequestsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_FRIEND_REQUEST_LIST:
        case YSMSG.RESP_GET_CACHE_FRIEND_REQUEST_LIST: {
            mListview.stopLoadMore();
            mListview.stopRefresh();
            dismissWaitingDialog();
            if (msg.arg1 == 200) {
                // success
                if (msg.arg2 == 1) {
                    mRequestsList.clear();
                }

                List<FriendRequestObject> friendRequestlist = (List<FriendRequestObject>) msg.obj;
                if (null != friendRequestlist) {
                    mRequestsList.addAll(friendRequestlist);
                    updateList();
                }
                mListview.setPullLoadEnable(!PageInfoObjectDao.isLastPage(PageInfoObjectDao.ID_FRIEND_REQUEST) || FriendRequestObjectDao.hasMoreFriendRequest(mRequestsList.size()));

                if (mRequestsList.size() > 0) {
                    noRequestRL.setVisibility(View.GONE);
                    mListview.setVisibility(View.VISIBLE);
                } else {
                    noRequestRL.setVisibility(View.VISIBLE);
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
        case YSMSG.RESP_AGREE_FRIEND_REQUEST: {
            if (msg.arg1 == 200) {
                // success
                sendEmptyMessage(YSMSG.REQ_GET_FRIEND_REQUEST_LIST);
                YSToast.showToast(getActivity(), R.string.fragment_familyrequest_tv_result_2);
            } else {
                // failed
                dismissWaitingDialog();
                if (msg.obj instanceof ResultObject) {
                    ResultObject result = (ResultObject) msg.obj;
                    YSToast.showToast(getActivity(), result.getErrorMsg());
                } else {
                    YSToast.showToast(getActivity(), R.string.network_error);
                }
            }
        }
            break;
        case YSMSG.RESP_CHECK_USER_RELATION:
            dismissWaitingDialog();
            if (msg.arg1 == 200) {
                // success
                FamilySaferPb pb = (FamilySaferPb) msg.obj;
                if (null != pb && null != pb.getUserInfosList()) {
                    UserInfo userInfo = pb.getUserInfosList().get(0);
                    Relation r = userInfo.getRelation();
                    int num = r.getNumber();
                    Intent intent = new Intent();
                    if (num == 1) {
                        intent.setClass(getActivity(), FamilyOut2Activity.class);
                        intent.putExtra("name", fro.getNickname());
                        intent.putExtra("phone", fro.getPhone());
                        startActivity(intent);
                    } else if (num == 2) {
                        if (fro.getReceive() && fro.getStatus() == 0) {
                            intent.setClass(getActivity(), FamilyIn2Activity.class);
                            intent.putExtra("isreceive", true);
                            intent.putExtra("extra_user_id", fro.getUserId());
                            startActivity(intent);
                        } else {
                            intent.setClass(getActivity(), FamilyIn2Activity.class);
                            intent.putExtra("extra_user_id", fro.getUserId());
                            startActivity(intent);
                        }
                    } else if (num == 3) {
                        intent.setClass(getActivity(), FamilyDetailActivity.class);
                        intent.putExtra("extra_user_id", fro.getUserId());
                        startActivity(intent);
                    }
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
            break;
        case YSMSG.RESP_GET_NEW_MSG_NUM:
            if (msg.arg1 == 200) {
                displayTip();
            }
            break;
        }
    }

    private class RequestsAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public RequestsAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mRequestsList.size();
        }

        @Override
        public FriendRequestObject getItem(int position) {
            return mRequestsList.get(position);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == mInflater) {
                mInflater = LayoutInflater.from(mContext);
            }

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_request, null);

                holder.imgRequest = (ImageView) convertView.findViewById(R.id.img_request_image);
                holder.txtName = (TextView) convertView.findViewById(R.id.tv_request_name);
                holder.txtStatus = (TextView) convertView.findViewById(R.id.tv_request_tip);

                holder.tvR1 = (TextView) convertView.findViewById(R.id.tv_request_1_status);
                holder.btnR2 = (Button) convertView.findViewById(R.id.btn_request_2_accept);
                holder.imgR3 = (ImageView) convertView.findViewById(R.id.img_request_3_icon);
                holder.tvR3 = (TextView) convertView.findViewById(R.id.tv_request_3_status);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position >= 0 && position < mRequestsList.size()) {
                final FriendRequestObject friendRequest = mRequestsList.get(position);
                if (friendRequest != null) {
                    String imageFile = friendRequest.getImage();
                    if (!TextUtils.isEmpty(imageFile)) {
                        ImageLoaderHelper.displayImage(imageFile, holder.imgRequest, R.drawable.user, true, ImageLoaderHelper.HEADIMAGE_CORNER_RADIUS);
                    } else {
                        holder.imgRequest.setImageResource(R.drawable.user);
                    }
                    holder.txtName.setText(friendRequest.getNickname());

                    RelativeLayout layoutR1 = (RelativeLayout) convertView.findViewById(R.id.layout_request_1);
                    RelativeLayout layoutR2 = (RelativeLayout) convertView.findViewById(R.id.layout_request_2);
                    RelativeLayout layoutR3 = (RelativeLayout) convertView.findViewById(R.id.layout_request_3);

                    int type = friendRequest.getStatus();

                    if (1 == type) {
                        if (friendRequest.getReceive()) {
                            layoutR1.setVisibility(View.GONE);
                            holder.tvR1.setVisibility(View.GONE);
                            layoutR2.setVisibility(View.GONE);
                            holder.btnR2.setVisibility(View.GONE);
                            layoutR3.setVisibility(View.VISIBLE);
                            holder.imgR3.setVisibility(View.VISIBLE);
                            holder.tvR3.setVisibility(View.VISIBLE);
                            holder.txtStatus.setText(R.string.fragment_familyrequest_tv_type_2);
                            holder.imgR3.setImageResource(R.drawable.sendok);
                            holder.tvR3.setText(R.string.fragment_familyrequest_tv_result_2);

                        } else {
                            layoutR1.setVisibility(View.GONE);
                            holder.tvR1.setVisibility(View.GONE);
                            layoutR2.setVisibility(View.GONE);
                            holder.btnR2.setVisibility(View.GONE);
                            layoutR3.setVisibility(View.VISIBLE);
                            holder.imgR3.setVisibility(View.VISIBLE);
                            holder.tvR3.setVisibility(View.VISIBLE);
                            holder.txtStatus.setText(R.string.fragment_familyrequest_tv_type_1);
                            holder.imgR3.setImageResource(R.drawable.sendok);
                            holder.tvR3.setText(R.string.fragment_familyrequest_tv_result_2);
                        }

                    } else if (0 == type) {
                        if (friendRequest.getReceive()) {
                            layoutR1.setVisibility(View.GONE);
                            holder.tvR1.setVisibility(View.GONE);
                            layoutR2.setVisibility(View.VISIBLE);
                            holder.btnR2.setVisibility(View.VISIBLE);
                            layoutR3.setVisibility(View.GONE);
                            holder.imgR3.setVisibility(View.GONE);
                            holder.tvR3.setVisibility(View.GONE);
                            holder.txtStatus.setText(R.string.fragment_familyrequest_tv_type_2);
                            holder.btnR2.setOnClickListener(new OnClickListener() {
                                public void onClick(View arg0) {
                                    sendMessage(YSMSG.REQ_AGREE_FRIEND_REQUEST, friendRequest.getUserId(), 0, null);
                                    showWaitingDialog();
                                }
                            });
                        } else {
                            layoutR1.setVisibility(View.VISIBLE);
                            holder.tvR1.setVisibility(View.VISIBLE);
                            layoutR2.setVisibility(View.GONE);
                            holder.btnR2.setVisibility(View.GONE);
                            layoutR3.setVisibility(View.GONE);
                            holder.imgR3.setVisibility(View.GONE);
                            holder.tvR3.setVisibility(View.GONE);
                            holder.txtStatus.setText(R.string.fragment_familyrequest_tv_type_1);
                            holder.tvR1.setText(R.string.fragment_familyrequest_tv_result_1);
                        }

                    } else if (2 == type) {
                        if (friendRequest.getReceive()) {
                            layoutR1.setVisibility(View.GONE);
                            holder.tvR1.setVisibility(View.GONE);
                            layoutR2.setVisibility(View.GONE);
                            holder.btnR2.setVisibility(View.GONE);
                            layoutR3.setVisibility(View.VISIBLE);
                            holder.imgR3.setVisibility(View.VISIBLE);
                            holder.tvR3.setVisibility(View.VISIBLE);
                            holder.txtStatus.setText(R.string.fragment_familyrequest_tv_type_2);
                            holder.imgR3.setImageResource(R.drawable.family_request_refuse);
                            holder.tvR3.setText(R.string.fragment_familyrequest_tv_result_3);
                        } else {
                            layoutR1.setVisibility(View.GONE);
                            holder.tvR1.setVisibility(View.GONE);
                            layoutR2.setVisibility(View.GONE);
                            holder.btnR2.setVisibility(View.GONE);
                            layoutR3.setVisibility(View.VISIBLE);
                            holder.imgR3.setVisibility(View.VISIBLE);
                            holder.tvR3.setVisibility(View.VISIBLE);
                            holder.txtStatus.setText(R.string.fragment_familyrequest_tv_type_1);
                            holder.imgR3.setImageResource(R.drawable.family_request_refuse);
                            holder.tvR3.setText(R.string.fragment_familyrequest_tv_result_3);
                        }
                    }
                }
            }

            return convertView;
        }

        public final class ViewHolder {
            public ImageView imgRequest;
            public TextView txtName;
            public TextView txtStatus;

            public TextView tvR1;
            public Button btnR2;
            public ImageView imgR3;
            public TextView tvR3;

        }
    }
}
