package mobi.dlys.android.familysafer.ui.location;

import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.App;
import mobi.dlys.android.familysafer.R;
import mobi.dlys.android.familysafer.biz.bo.YSMSG;
import mobi.dlys.android.familysafer.biz.vo.FriendObject;
import mobi.dlys.android.familysafer.biz.vo.PageInfoObject;
import mobi.dlys.android.familysafer.biz.vo.ResultObject;
import mobi.dlys.android.familysafer.biz.vo.UserObject;
import mobi.dlys.android.familysafer.db.dao.FriendObjectDao;
import mobi.dlys.android.familysafer.db.dao.PageInfoObjectDao;
import mobi.dlys.android.familysafer.model.CoreModel;
import mobi.dlys.android.familysafer.ui.comm.BaseExActivity;
import mobi.dlys.android.familysafer.ui.comm.TitleBarHolder;
import mobi.dlys.android.familysafer.ui.comm.YSAlertDialog;
import mobi.dlys.android.familysafer.ui.comm.YSToast;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView;
import mobi.dlys.android.familysafer.ui.comm.xlistview.XListView.IXListViewListener;
import mobi.dlys.android.familysafer.ui.family.AddFamily1Activity;
import mobi.dlys.android.familysafer.utils.ImageLoaderHelper;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FamilyLocationsActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;

	private ArrayList<FriendObject> mFamiliesList = null;
	private XListView mListview = null;

	private LocationsAdapter mAdapter = null;

	static int FamilyLocate_Action_Id = 10092;

	RelativeLayout mPopTip = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations);

		initView();
		initData();
	}

	void initView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_familylocate_ttb_title);
		mTitleBar.mLeft.setBackgroundResource(R.drawable.button_left_titlebar_selector);
		mTitleBar.mRight.setVisibility(View.INVISIBLE);

		mPopTip = (RelativeLayout) findViewById(R.id.layout_locations);
		mPopTip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

		mFamiliesList = new ArrayList<FriendObject>();
		mListview = (XListView) this.findViewById(R.id.lv_locations);
		mAdapter = new LocationsAdapter(this);
		mListview.setAdapter(mAdapter);
		mListview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 <= 1) {
					return;
				}
				arg2--;
				arg2--;
				if (arg2 >= mFamiliesList.size() || arg2 < 0) {
					return;
				}
				FriendObject obj = mFamiliesList.get(arg2);
				if (null != obj) {
					if (obj.getUserId() == -1) {
						UserObject uo = CoreModel.getInstance().getUserInfo();
						String location = uo.getLocation();
						if (null == location || !uo.isLocationValid()) {
							Intent intent = new Intent(FamilyLocationsActivity.this, FamilyLocationsNoneActivity.class);
							String name = uo.getRemarkName();
							if (null == name) {
								name = uo.getNickname();
							} else {
								if (TextUtils.isEmpty(name)) {
									name = uo.getNickname();
								}
							}
							if (null == name) {
								name = "";
							}

							intent.putExtra("name", name);
							intent.putExtra("phone", uo.getPhone());
							intent.putExtra("userid", uo.getUserId());
							intent.putExtra("image", uo.getImage());
							startActivityForResult(intent, FamilyLocate_Action_Id);
						} else {
							if (TextUtils.isEmpty(location) || !uo.isLocationValid()) {
								Intent intent = new Intent(FamilyLocationsActivity.this, FamilyLocationsNoneActivity.class);
								String name = uo.getRemarkName();
								if (null == name) {
									name = uo.getNickname();
								} else {
									if (TextUtils.isEmpty(name)) {
										name = uo.getNickname();
									}
								}
								if (null == name) {
									name = "";
								}

								intent.putExtra("name", name);
								intent.putExtra("phone", uo.getPhone());
								intent.putExtra("userid", uo.getUserId());
								intent.putExtra("image", uo.getImage());
								startActivityForResult(intent, FamilyLocate_Action_Id);
							} else {
								Intent intent = new Intent(FamilyLocationsActivity.this, FamilyLocateActivity.class);
								intent.putExtra("selected", -1);
								startActivityForResult(intent, FamilyLocate_Action_Id);
							}
						}

					} else if (obj.getUserId() == -2) {
						Intent intent = new Intent(FamilyLocationsActivity.this, AddFamily1Activity.class);
						startActivity(intent);
					} else {
						String location = obj.getLocation();
						if (null == location || TextUtils.isEmpty(location) || !obj.isLocationValid() || !obj.getShowMyPosition() || obj.getHideLocation()) {
							Intent intent = new Intent(FamilyLocationsActivity.this, FamilyLocationsNoneActivity.class);
							String name = obj.getRemarkName();
							if (null == name) {
								name = obj.getNickname();
							} else {
								if (TextUtils.isEmpty(name)) {
									name = obj.getNickname();
								}
							}
							if (null == name) {
								name = "";
							}

							intent.putExtra("name", name);
							intent.putExtra("phone", obj.getPhone());
							intent.putExtra("userid", obj.getUserId());
							intent.putExtra("image", obj.getImage());
							startActivityForResult(intent, FamilyLocate_Action_Id);
						} else {
							Intent intent = new Intent(FamilyLocationsActivity.this, FamilyLocateActivity.class);
							intent.putExtra("selected", obj.getUserId());
							startActivityForResult(intent, FamilyLocate_Action_Id);
						}
					}
				}

			}

		});
		startRadar();
	}

	@Override
	public void onDestroy() {
		if (null != mFamiliesList) {
			mFamiliesList.clear();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mListview.showFooterView(false);
		mListview.setPullLoadEnable(false);
		mListview.setPullRefreshEnable(true);
		mListview.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				// 下拉刷新
				sendEmptyMessage(YSMSG.REQ_GET_FRIEND_LIST);
			}

			@Override
			public void onLoadMore() {
				// 上拉加载更多
				PageInfoObject pageInfo = PageInfoObjectDao.getNextCachePageNo(PageInfoObjectDao.ID_FRIEND);
				if (pageInfo != null && pageInfo.isLastPage() && !FriendObjectDao.hasMoreFriend(mFamiliesList.size() - 1)) {
					mListview.stopLoadMore();
					mListview.setPullLoadEnable(false);
				} else {
					sendMessage(YSMSG.REQ_GET_CACHE_FRIEND_LIST, pageInfo.getReadCachePageNo(), 0, pageInfo);
				}
			}
		});
		mListview.setVisibility(View.VISIBLE);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				sendEmptyMessage(YSMSG.REQ_GET_FRIEND_LIST);
			}
		}, 1000);
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

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case YSMSG.RESP_HIDE_LOCATION:
			break;
		case YSMSG.RESP_GET_FRIEND_LIST:
		case YSMSG.RESP_GET_CACHE_FRIEND_LIST: {
			mListview.stopLoadMore();
			mListview.stopRefresh();
			if (msg.arg1 == 200) {
				if (msg.arg2 == 1) {
					mFamiliesList.clear();

					FriendObject mySelf = new FriendObject();
					mySelf.setUserId(-1);
					mFamiliesList.add(mySelf);
				}

				List<FriendObject> friendlist = (List<FriendObject>) msg.obj;
				if (null != friendlist) {
					if (friendlist.size() > 0) {
						mFamiliesList.addAll(friendlist);
						mListview.setPullLoadEnable(!PageInfoObjectDao.isLastPage(PageInfoObjectDao.ID_FRIEND)
								|| FriendObjectDao.hasMoreFriend(mFamiliesList.size() - 1));
					} else {
						mListview.setPullLoadEnable(false);
						FriendObject mySelf = new FriendObject();
						mySelf.setUserId(-2);
						mFamiliesList.add(mySelf);
					}

					updateList();
				}

				mListview.setVisibility(View.VISIBLE);
				clearAnim();
				mPopTip.setVisibility(View.GONE);

			} else {
				// failed
				if (msg.obj instanceof ResultObject) {
					ResultObject result = (ResultObject) msg.obj;
					YSToast.showToast(FamilyLocationsActivity.this, result.getErrorMsg());
				} else {
					YSToast.showToast(FamilyLocationsActivity.this, R.string.network_error);
				}
			}
			break;
		}
		}
	}

	void updateList() {
		if (null != mAdapter) {
			mAdapter.notifyDataSetChanged();
		}
	}

	public class LocationsAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;

		public LocationsAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return mFamiliesList.size() + 1;
		}

		@Override
		public Object getItem(int arg0) {
			if (arg0 > 0) {
				return mFamiliesList.get(arg0 - 1);
			} else {
				return mFamiliesList.get(arg0 + 1);
			}
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (null == mInflater) {
				mInflater = LayoutInflater.from(mContext);
			}

			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_locations, null);

				holder.imgAvatar = (ImageView) convertView.findViewById(R.id.img_locations_image);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_locations_name);
				holder.tvLocation = (TextView) convertView.findViewById(R.id.tv_locations_tip);
				holder.tvLastTime = (TextView) convertView.findViewById(R.id.tv_locations_status);

				holder.imgAvatar2 = (ImageView) convertView.findViewById(R.id.img_locations_image2);
				holder.tvName2 = (TextView) convertView.findViewById(R.id.tv_locations_name2);
				holder.tvLocation2 = (TextView) convertView.findViewById(R.id.tv_locations_addr);
				holder.tvLastTime2 = (TextView) convertView.findViewById(R.id.tv_locations_last);

				holder.imgHide = (ImageView) convertView.findViewById(R.id.img_locations_hide);
				holder.imgShow = (ImageView) convertView.findViewById(R.id.img_locations_show);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (0 == getItemViewType(position)) {
				RelativeLayout layout_image = (RelativeLayout) convertView.findViewById(R.id.layout_locations_image);
				layout_image.setVisibility(View.VISIBLE);
				layout_image.setBackgroundResource(R.drawable.background_familylocate);
				LinearLayout layout_content = (LinearLayout) convertView.findViewById(R.id.layout_locations_content);
				layout_content.setVisibility(View.GONE);
				LinearLayout layout_content2 = (LinearLayout) convertView.findViewById(R.id.layout_locations_content2);
				layout_content2.setVisibility(View.GONE);
			} else {
				RelativeLayout layout_image = (RelativeLayout) convertView.findViewById(R.id.layout_locations_image);
				layout_image.setVisibility(View.GONE);
				LinearLayout layout_content = (LinearLayout) convertView.findViewById(R.id.layout_locations_content);
				LinearLayout layout_content2 = (LinearLayout) convertView.findViewById(R.id.layout_locations_content2);

				if (position >= 1 && position <= mFamiliesList.size()) {
					layout_content2.setVisibility(View.GONE);
					layout_content.setVisibility(View.VISIBLE);

					FriendObject family = mFamiliesList.get(position - 1);
					if (family != null) {
						if (family.getUserId() == -1) {
							layout_content.setVisibility(View.GONE);
							layout_content2.setVisibility(View.VISIBLE);

							UserObject uo = CoreModel.getInstance().getUserInfo();
							String imageFile = uo.getImage();
							if (!TextUtils.isEmpty(imageFile)) {
								ImageLoaderHelper.displayImage(imageFile, holder.imgAvatar2, R.drawable.user, true);
							} else {
								holder.imgAvatar2.setImageResource(R.drawable.user);
							}
							if (!TextUtils.isEmpty(uo.getRemarkName())) {
								holder.tvName2.setText(uo.getRemarkName());
							} else {
								holder.tvName2.setText(uo.getNickname());
							}
							if (TextUtils.isEmpty(uo.getLocation())) {
								holder.tvLocation2.setText(getResources().getText(R.string.activity_familylocations_none_tip2));
								
							} else {
								holder.tvLocation2.setText(uo.getLocation());
								
							}

							UserObject user = CoreModel.getInstance().getUserInfo();
							if (user != null) {
								if (user.getHideLocation()) {
									holder.imgHide.setVisibility(View.VISIBLE);
									holder.imgShow.setVisibility(View.GONE);
									holder.tvLastTime2.setText(getString(R.string.activity_familylocate_hided));
								} else {
									holder.imgHide.setVisibility(View.GONE);
									holder.imgShow.setVisibility(View.VISIBLE);
									holder.tvLastTime2.setText("");									
								}
							}
							holder.imgHide.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									sendMessage(YSMSG.REQ_HIDE_LOCATION, 0, 0, false);
									holder.imgHide.setVisibility(View.GONE);
									holder.imgShow.setVisibility(View.VISIBLE);
									holder.tvLastTime2.setText("");
								}
							});

							holder.imgShow.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									View view = getLayoutInflater().inflate(R.layout.dialog_two_button, null);
									if (view != null) {
										final Dialog dialog = YSAlertDialog.createBaseDialog(App.getInstance().getForegroundActivity(), view, false, false);
										TextView txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
										txtTitle.setText(getString(R.string.dialog_title_tip));
										TextView txtContent = (TextView) view.findViewById(R.id.txt_dialog_content);
										txtContent.setText(getString(R.string.dialog_hide_content));
										Button btnConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
										Button btnCancel = (Button) view.findViewById(R.id.btn_dialog_cancel);
										btnConfirm.setText(getString(R.string.dialog_hide_yes));
										btnCancel.setText(getString(R.string.dialog_hide_no));
										btnConfirm.setOnClickListener(new OnClickListener() {
											public void onClick(View arg0) {
												dialog.cancel();
												sendMessage(YSMSG.REQ_HIDE_LOCATION, 0, 0, true);
												holder.imgHide.setVisibility(View.VISIBLE);
												holder.imgShow.setVisibility(View.GONE);
												holder.tvLastTime2.setText(getString(R.string.activity_familylocate_hided));
											}
										});
										btnCancel.setOnClickListener(new OnClickListener() {
											public void onClick(View arg0) {

												dialog.cancel();

											}
										});
										dialog.show();
									}									

								}
							});

						} else if (family.getUserId() == -2) {
							holder.imgAvatar.setImageResource(R.drawable.img_user_add);
							holder.tvName.setText(getResources().getText(R.string.fragment_main_addfamily_title));
							holder.tvLocation.setText(getResources().getText(R.string.fragment_main_addfamily_tip));
							holder.tvLastTime.setText("");
						} else {
							String imageFile = family.getImage();
							if (!TextUtils.isEmpty(imageFile)) {
								ImageLoaderHelper.displayImage(imageFile, holder.imgAvatar, R.drawable.user, true);
							} else {
								holder.imgAvatar.setImageResource(R.drawable.user);
							}
							if (null != family.getRemarkName()) {
								if (!TextUtils.isEmpty(family.getRemarkName())) {
									holder.tvName.setText(family.getRemarkName());
								} else {
									holder.tvName.setText(family.getNickname());
								}
							} else {
								holder.tvName.setText(family.getNickname());
							}
							if (TextUtils.isEmpty(family.getLocation()) || !family.getShowMyPosition() || family.getHideLocation()) {
								holder.tvLocation.setText(getResources().getText(R.string.activity_familylocations_none_tip2));
								holder.tvLastTime.setText("");
							} else {
								String addr = family.getLocation();
								if (addr.startsWith("|")) {
									addr = addr.substring(1);
								}
								if (addr.endsWith("|")) {
									addr = addr.substring(0, addr.length() - 1);
								}

								if (addr.contains("|")) {
									int pos = addr.indexOf("|");
									addr = addr.substring(pos + 1, addr.length());
								}
								holder.tvLocation.setText(addr);
								holder.tvLastTime.setText(family.getLastMoveTime());
							}
						}
					}
				}
			}

			return convertView;
		}

		class ViewHolder {
			public ImageView imgAvatar;
			public TextView tvName;
			public TextView tvLocation;
			public TextView tvLastTime;

			public ImageView imgAvatar2;
			public TextView tvName2;
			public TextView tvLocation2;
			public TextView tvLastTime2;
			public ImageView imgHide;
			public ImageView imgShow;
		}
	}
}
