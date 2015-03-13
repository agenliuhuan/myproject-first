package mobi.dlys.android.familysafer.ui.clue;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.core.utils.AndroidConfig;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.ClueImageObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.PublicClueObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.db.dao.PublicClueObjectDao;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView.IXListViewListener;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView2;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView2.IXListViewListener2;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import mobi.dlys.android.familysafer.utils.umeng.AnalyticsHelper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RadarClueActivity extends BaseExActivity {
    TitleBarHolder mTitleBar;
    XListView mListview = null;
    RelativeLayout mNoCluesRL = null;
    ArrayList<PublicClueObject> mMyClues = null;
    MyclueListViewAdapter mListAdapter = null;
    private LinearLayout mPopClue = null;
    private LinearLayout mPopClue2 = null;
    private Button mClueButton = null;
    RelativeLayout loadingIV;
    TextView loadingTV;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radarclue);
        intiView();
        initData();
    }

    private void initData() {
        mListview.showFooterView(false);
        mListview.setPullLoadEnable(false);
        mListview.setPullRefreshEnable(true);
        mListview.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                // 下拉刷新
                sendEmptyMessage(YSMSG.REQ_GET_PUBLIC_CLUE_LIST);
                //imgPull.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadMore() {
                // 上拉加载更多
                PageInfoObject pageInfo = PageInfoObjectDao.getNextCachePageNo(PageInfoObjectDao.ID_PUBLIC_CLUE);
                if (pageInfo != null) {
                    if (pageInfo.isLastPage() && !PublicClueObjectDao.hasMoreClue(mMyClues.size())) {
                        YSToast.showToast(RadarClueActivity.this, R.string.toast_no_more_data);
                        mListview.stopLoadMore();
                        mListview.setPullLoadEnable(false);
                    } else {
                        sendMessage(YSMSG.REQ_GET_CACHE_PUBLIC_CLUE_LIST, pageInfo.getReadCachePageNo(), 0, pageInfo);
                    }
                }

            }
        });
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                sendEmptyMessage(YSMSG.REQ_GET_CACHE_PUBLIC_CLUE_LIST);
            }
        }, 1000);
    }

    private void intiView() {
        mTitleBar = new TitleBarHolder(this);
        mTitleBar.mTitle.setText(R.string.activity_saferadar_ttb_title);
        mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
        mTitleBar.mRight.setBackgroundResource(R.drawable.saferadar_cap_selector);
        mTitleBar.mTitle.setTextColor(getResources().getColor(R.color.title_green_line));
        mTitleBar.mLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                RadarClueActivity.this.finish();
            }
        });
        mTitleBar.mRight.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnalyticsHelper.onEvent(RadarClueActivity.this, AnalyticsHelper.index_click_shotsnap);
                    }
                }, 1000);

                SharedPreferences userInfo = RadarClueActivity.this.getSharedPreferences("application_user_info", 0);
                boolean isClueFirst = userInfo.getBoolean("clue_first_run", true);
                if (isClueFirst) {
                    userInfo.edit().putBoolean("clue_first_run", false).commit();
                    showUpView(mPopClue, mPopClue2, RadarClueActivity.this);
                } else {
                    Intent intent = new Intent(RadarClueActivity.this, ClueActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "takephoto");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        mMyClues = new ArrayList<PublicClueObject>();
        mListview = (XListView) findViewById(R.id.lv_radarclue);
        mListAdapter = new MyclueListViewAdapter(getBaseContext());
        mListview.setAdapter(mListAdapter);
        mNoCluesRL = (RelativeLayout) findViewById(R.id.no_radarclue);

        mPopClue = (LinearLayout) findViewById(R.id.layout_main_clue);
        mPopClue2 = (LinearLayout) findViewById(R.id.layout_main_clue2);
        mPopClue.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });
        mPopClue2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });

        mClueButton = (Button) findViewById(R.id.btn_main_clue_know);
        mClueButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                hideUpView(mPopClue, mPopClue2, RadarClueActivity.this);
            }
        });
        loadingIV = (RelativeLayout) findViewById(R.id.radarclue_loading);
        loadingTV = (TextView) findViewById(R.id.radarclue_loadingtv);
        
        startRadar();
    }

    ImageView radar_dian1;
    ImageView radar_dian2;
    ImageView radar_dian3;
    ImageView radar_dian4;
    ImageView radar_scaning;
    Animation dianAnim1;
    Animation dianAnim2;
    Animation dianAnim3;
    Animation dianAnim4;
    Animation scaningAnim;

    private void startRadar() {
        radar_scaning = (ImageView) findViewById(R.id.im_scan);
        radar_dian1 = (ImageView) findViewById(R.id.im_dian1);
        radar_dian2 = (ImageView) findViewById(R.id.im_dian2);
        radar_dian3 = (ImageView) findViewById(R.id.im_dian3);
        radar_dian4 = (ImageView) findViewById(R.id.im_dian4);

        scaningAnim = AnimationUtils.loadAnimation(this, R.anim.radar_scaning_anim);
        dianAnim1 = AnimationUtils.loadAnimation(this, R.anim.dian_anim1);
        dianAnim2 = AnimationUtils.loadAnimation(this, R.anim.dian_anim2);
        dianAnim3 = AnimationUtils.loadAnimation(this, R.anim.dian_anim3);
        dianAnim4 = AnimationUtils.loadAnimation(this, R.anim.dian_anim4);

        radar_scaning.startAnimation(scaningAnim);
        radar_dian1.startAnimation(dianAnim1);
        radar_dian2.startAnimation(dianAnim2);
        radar_dian3.startAnimation(dianAnim3);
        radar_dian4.startAnimation(dianAnim4);

    }

    private void clearAnim() {
        radar_scaning.clearAnimation();
        radar_dian1.clearAnimation();
        radar_dian2.clearAnimation();
        radar_dian3.clearAnimation();
        radar_dian4.clearAnimation();
    }

    public void hideUpView(final View rootview, final View v, Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_top_out);
        v.startAnimation(anim);
        Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_out);
        rootview.startAnimation(anim2);
        
        anim.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation arg0) {

            }

            public void onAnimationRepeat(Animation arg0) {

            }

            public void onAnimationEnd(Animation arg0) {
                rootview.setVisibility(View.GONE);
                v.setVisibility(View.GONE);
                Intent intent = new Intent(RadarClueActivity.this, ClueActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "takephoto");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    public void showUpView(final View rootview, final View v, Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_top_in);
        v.startAnimation(anim);
        v.setVisibility(View.VISIBLE);

        Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_in);
        rootview.startAnimation(anim2);
        rootview.setVisibility(View.VISIBLE);
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
        case YSMSG.RESP_GET_PUBLIC_CLUE_LIST:
        case YSMSG.RESP_GET_CACHE_PUBLIC_CLUE_LIST: {
            clearAnim();
            mListview.stopLoadMore();
            mListview.stopRefresh();
            loadingIV.setVisibility(View.GONE);
            loadingTV.setVisibility(View.GONE);
            if (msg.arg2 == 1) {
                mMyClues.clear();
            }

            if (msg.arg1 == 200) {
                mListview.setPullLoadEnable(!PageInfoObjectDao.isLastPage(PageInfoObjectDao.ID_PUBLIC_CLUE) || PublicClueObjectDao.hasMoreClue(mMyClues.size()));
                List<PublicClueObject> list = (List<PublicClueObject>) msg.obj;
                if (list.size() > 0) {
                    mMyClues.addAll(list);
                    updateList();
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
                    YSToast.showToast(RadarClueActivity.this, result.getErrorMsg());
                } else {
                    YSToast.showToast(RadarClueActivity.this, R.string.network_error);
                }
            }

        }
            break;

        }
    }

    private void updateList() {
        if (null != mListAdapter) {
            mListAdapter.notifyDataSetChanged();
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
                convertView = LayoutInflater.from(this.mContext).inflate(R.layout.list_item_publicclue, null, false);
                holder.mDate = (TextView) convertView.findViewById(R.id.tv_publicclue_date);
                holder.mContent = (TextView) convertView.findViewById(R.id.tv_publicclue_content);
                holder.mLocation = (TextView) convertView.findViewById(R.id.tv_publicclue_location);
                holder.mImages = (GridView) convertView.findViewById(R.id.gv_publicclue_images);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (mMyClues != null) {
                PublicClueObject gridItem = mMyClues.get(position);
                if (null != gridItem) {
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
                    if (holder.mImages != null) {
                        ArrayList<ClueImageObject> images = gridItem.getImageList();

                        if (null != images) {
                            setGridParams(holder.mImages, images.size(), images);
                            RadarGridAdapter gridViewAdapter = new RadarGridAdapter(mContext, images, position);
                            holder.mImages.setAdapter(gridViewAdapter);
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
                params.setMargins(0, imageMargin * 2, 0, 0);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(1);
                break;
            case 2:
                params = new LayoutParams((Screenwidth - imageMargin * 10) / 3 * 2 + 4, (Screenwidth - imageMargin * 10) / 3);
                params.setMargins(0, imageMargin * 2, 0, 0);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(2);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            case 3:
                params = new LayoutParams((Screenwidth - imageMargin * 10) + 8, (Screenwidth - imageMargin * 10) / 3);
                params.setMargins(0, imageMargin * 2, 0, 0);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(3);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            case 4:
                params = new LayoutParams((Screenwidth - imageMargin * 10) / 3 * 2 + 4, (Screenwidth - imageMargin * 10) / 3 * 2 + 4);
                params.setMargins(0, imageMargin * 2, 0, 0);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(2);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            case 5:
            case 6:
                params = new LayoutParams((Screenwidth - imageMargin * 10) + 8, (Screenwidth - imageMargin * 10) / 3 * 2 + 4);
                params.setMargins(0, imageMargin * 2, 0, 0);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(3);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            case 7:
            case 8:
                params = new LayoutParams((Screenwidth - imageMargin * 10) + 8, (Screenwidth - imageMargin * 10) + 8);
                params.setMargins(0, imageMargin * 2, 0, 0);
                mGrid.setLayoutParams(params);
                mGrid.setSelector(R.color.transparent);
                mGrid.setNumColumns(3);
                mGrid.setHorizontalSpacing(4);
                mGrid.setVerticalSpacing(4);
                break;
            }
        }

        public final class ViewHolder {
            TextView mDate;
            TextView mContent;
            TextView mLocation;
            GridView mImages;
        }
    }

    class RadarGridAdapter extends BaseAdapter {
        private Context mContext;
        public ArrayList<ClueImageObject> mList;
        int Width;
        int Height;
        int rootposition;

        public RadarGridAdapter(Context contect, ArrayList<ClueImageObject> list, int position) {
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

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_clue_image, null);
            }
            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AnalyticsHelper.onEvent(RadarClueActivity.this, AnalyticsHelper.index_view_upload);
                        }
                    }, 1000);
                    Intent intent = new Intent(RadarClueActivity.this, ViewerActivity.class);
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
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 10) / 3, (Screenwidth - imageMargin * 10) / 3);
                            mImage.setLayoutParams(params);
                            mImage.setScaleType(ImageView.ScaleType.FIT_XY);

                            ImageLoaderHelper.displayImage(imageFile, mImage, R.drawable.default_bg_image, false);
                        }
                    } else {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((Screenwidth - imageMargin * 10) / 3, (Screenwidth - imageMargin * 10) / 3);
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
