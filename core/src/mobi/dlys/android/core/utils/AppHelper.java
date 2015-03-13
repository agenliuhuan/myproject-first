package mobi.dlys.android.core.utils;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 操作 APK包的工具
 * 
 * @author
 * 
 */
public class AppHelper {

	private static final String TAG = "AppHelper";

	private static Map<String, SoftReference<ApkInfo>> mApkInfoCache = new HashMap<String, SoftReference<ApkInfo>>();

	private static Pattern mPattern = Pattern.compile("\\d+(\\.\\d+)?"); // 版本号正则表达式

	public static final class ApkInfo {
		private PackageInfo packageInfo;

		private Resources resources;

		public ApkInfo(PackageInfo pinfo, Resources res) {
			packageInfo = pinfo;
			resources = res;
		}

		/**
		 * 获取Apk的应用名称
		 * 
		 * @param apkInfo
		 * @return
		 */
		public CharSequence getApkLabel() {
			CharSequence label = null;
			if (packageInfo != null && resources != null) {
				// 1 系统安装器采用的方法
				label = packageInfo.applicationInfo.nonLocalizedLabel;
				if (label == null) {
					// 2 自己解析
					int resid = packageInfo.applicationInfo.labelRes;
					if (resid != 0) {
						try {
							label = resources.getText(resid);
						} catch (NotFoundException ignore) {
						}
					} else {
						// 3 没辙了，用包名吧
						label = packageInfo.applicationInfo.packageName;
					}
				}
			}
			return label;
		}

		/**
		 * 获取Apk的应用图标
		 * 
		 * @param apkInfo
		 * @return
		 */
		public Drawable getApkIcon() {
			Drawable icon = null;
			if (packageInfo != null && resources != null) {
				int resid = packageInfo.applicationInfo.icon;
				if (resid != 0) {
					try {
						icon = resources.getDrawable(resid);
					} catch (NotFoundException ignore) {
					}
				}
			}
			return icon;
		}

		public String getPackageName() {
			if (null != packageInfo) {
				return packageInfo.packageName;
			}
			return null;
		}

		/**
		 * 获取Apk的应用版本号 String 只取数字主次版本号
		 * 
		 * @param apkInfo
		 * @return
		 */
		public String getApkVerName() {
			String version = null;
			if (packageInfo != null) {
				version = getMainVerName(packageInfo.versionName);
			}
			return version;
		}

		/**
		 * 获取Apk的应用版本号 int
		 * 
		 * @param apkInfo
		 * @return
		 */
		public int getApkVerCode() {
			int version = 0;
			if (packageInfo != null) {
				version = packageInfo.versionCode;
			}
			return version;
		}
	}

	/**
	 * 通过apk路径获取一个ApkInfo对象，通过该对象可以获取Apk的应用名称、图标等信息
	 * 
	 * @param path
	 * @return
	 */
	private static ApkInfo getApkInfoByPath(Context context, String path) {
		PackageInfo packageInfo = context.getPackageManager()
				.getPackageArchiveInfo(
						path,
						PackageManager.GET_ACTIVITIES
								| PackageManager.GET_SERVICES);
		ApkInfo apkInfo = null;
		if (packageInfo != null && packageInfo.applicationInfo != null) {
			Resources res = null;
			try {
				Class<?> aassetMgrCls = Class
						.forName("android.content.res.AssetManager");
				Constructor<?> assetMgrCt = aassetMgrCls
						.getConstructor((Class<?>[]) null);
				Object assetMgr = assetMgrCt.newInstance((Object[]) null);
				Class<?>[] typeArgs = { String.class };
				Method assetMag_addAssetPathMtd = aassetMgrCls
						.getDeclaredMethod("addAssetPath", typeArgs);
				Object[] valueArgs = { path };
				assetMag_addAssetPathMtd.invoke(assetMgr, valueArgs);
				res = context.getResources();
				Class<?>[] typeArgs2 = { aassetMgrCls, DisplayMetrics.class,
						Configuration.class };
				Constructor<?> resCt = Resources.class
						.getConstructor(typeArgs2);
				Object[] valueArgs2 = { assetMgr, res.getDisplayMetrics(),
						res.getConfiguration() };
				res = (Resources) resCt.newInstance(valueArgs2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (res != null) {
				apkInfo = new ApkInfo(packageInfo, res);
			}
		}

		return apkInfo;
	}

	private static ApkInfo addApkInfo(Context ctx, String path) {
		ApkInfo apkInfo = getApkInfoByPath(ctx, path);
		if (null == apkInfo) {
			return null;
		}

		SoftReference<ApkInfo> _apkInfoRef = new SoftReference<ApkInfo>(apkInfo);
		mApkInfoCache.put(path, _apkInfoRef);
		return apkInfo;
	}

	public static ApkInfo getApkInfo(Context context, String path) {
		SoftReference<ApkInfo> _apkInfoRef;
		if (null == mApkInfoCache.get(path)) {
			return addApkInfo(context, path);
		} else {
			_apkInfoRef = mApkInfoCache.get(path);

			if (null == _apkInfoRef.get()) {
				return addApkInfo(context, path);
			} else {
				return _apkInfoRef.get();
			}
		}
	}

	public static String getHomeName(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		String homeName = null;
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		List<ResolveInfo> list = pm.queryIntentActivities(intent,
				PackageManager.GET_RESOLVED_FILTER);
		ResolveInfo res = list.get(0);
		homeName = res.activityInfo.name;
		return homeName;
	}

	/**
	 * 安装包不合法
	 */
	public static final int INNER_INVALID_PARAM = 1;

	/**
	 * 本地未安装
	 */
	public static final int INNER_NOT_INSTALL = INNER_INVALID_PARAM + 1;

	/**
	 * 本地已经安装旧版本
	 */
	public static final int INNER_OLDER_VERSION = INNER_NOT_INSTALL + 1;

	/**
	 * 本地已经安装相同版本
	 */
	public static final int INNER_EQUAL_VERSION = INNER_OLDER_VERSION + 1;

	/**
	 * 本地已经安装新版本
	 */
	public static final int INNER_NEWER_VERSION = INNER_EQUAL_VERSION + 1;

	/**
	 * 比较本地已经安装的版本信息
	 * 
	 * @param ai
	 *            ApkInfo
	 * @return {@link #INNER_INVALID_PARAM}, {@link #INNER_NOT_INSTALL},
	 *         {@link #INNER_OLDER_VERSION}, {@link #INNER_EQUAL_VERSION},
	 *         {@link #INNER_NEWER_VERSION}
	 */
	public static int compareLocalApp(Context ctx, ApkInfo ai) {
		int ret = INNER_INVALID_PARAM;
		if (ai != null && ai.packageInfo != null) {
			PackageInfo pi = null;
			try {
				pi = ctx.getPackageManager().getPackageInfo(
						ai.getPackageName(), 0);
				if (pi != null) {
					if (pi.versionCode > ai.packageInfo.versionCode) {
						ret = INNER_NEWER_VERSION;
					} else if (pi.versionCode < ai.packageInfo.versionCode) {
						ret = INNER_OLDER_VERSION;
					} else {
						// 防止不按规则出牌的开发人员不升级vercode
						int cmpare = compareVerName(pi.versionName,
								ai.packageInfo.versionName);
						if (cmpare > 0) {
							ret = INNER_NEWER_VERSION;
						} else if (cmpare < 0) {
							ret = INNER_OLDER_VERSION;
						} else {
							ret = INNER_EQUAL_VERSION;
						}
					}
				}
			} catch (NameNotFoundException e) {
				ret = INNER_NOT_INSTALL;
			}
		}

		return ret;
	}

	/**
	 * 版本号比较
	 * 
	 * @param ver1
	 *            已安装的应用的VersionName
	 * @param ver2
	 *            已下载的应用的VersionName
	 * @return
	 */
	private static int compareVerName(String ver1, String ver2) {
		if (null == ver1 || null == ver2) {
			return 0;
		}
		// 这里需要分段判断，注意误判 3.10 < 3.2
		// int ret = ver1.compareTo(ver2);
		int ret = 0;
		String[] str1 = ver1.split("\\.");
		String[] str2 = ver2.split("\\.");
		int l1 = str1.length;
		int l2 = str2.length;
		int len = (l1 < l2) ? l1 : l2;
		for (int i = 0; i < len; i++) {
			int num1 = Integer.parseInt(getMainVerName(str1[i]));
			int num2 = Integer.parseInt(getMainVerName(str2[i]));
			if (num1 != num2) {
				ret = num1 - num2;
				break;
			}
		}
		if (ret == 0) {
			ret = l1 - l2;
		}
		return ret;
	}

	/**
	 * 取本地已安装应用程序的版本号，只取主次数字版本 eg:2.3 未安装的时候返回null
	 * 
	 * @param ctx
	 * @param packagename
	 * @return
	 */
	public static String getInstalledAppVersion(Context ctx, String packagename) {
		String ver = null;
		PackageInfo pi = null;
		try {
			pi = ctx.getPackageManager()
					.getPackageInfo(
							packagename,
							PackageManager.GET_ACTIVITIES
									| PackageManager.GET_SERVICES);
			if (pi != null) {
				ver = getMainVerName(pi.versionName);
			}
		} catch (NameNotFoundException e) {
		}
		return ver;
	}

	/**
	 * 如果versionName为null，或者如果完全没数字，则返回"0"； 否则取数字主(.次)版本号 eg： 2.30
	 */
	public static String getMainVerName(String verName) {
		String rlt = "0";
		if (verName != null) {
			Matcher m = mPattern.matcher(verName);
			if (m.find()) {
				rlt = m.group();
			}
		}
		return rlt;
	}

	// 截取版本号,没考虑非数字情况
	// public static String getMainVerName(String verName) {
	// String name = verName;
	// if (verName != null) {
	// int idx1 = verName.indexOf('.');
	// if (idx1 != -1) {
	// int idx2 = verName.indexOf('.', idx1 + 1);
	// if (idx2 != -1) {
	// name = verName.substring(0, idx2);
	// }
	// }
	// }
	// return name;
	// }

	public static boolean isPackageInstalled(Context context,
			final String apkPath) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo apkinfo = packageManager.getPackageArchiveInfo(apkPath,
					PackageManager.GET_ACTIVITIES);
			if (apkinfo == null) {

				LogUtils.e(TAG, "invalid package");

				return false;
			}

			ApplicationInfo appInfo = apkinfo.applicationInfo;
			String packageName = appInfo.packageName;
			String version = apkinfo.versionName;

			LogUtils.e(TAG, " packageName = " + packageName + ", version = "
					+ version);

			PackageInfo installedPackageInfo = packageManager.getPackageInfo(
					packageName, PackageManager.GET_ACTIVITIES);
			if (installedPackageInfo == null) {

				LogUtils.e(TAG, "package not found : " + packageName);

				return false;
			}

			if (installedPackageInfo.versionCode == apkinfo.versionCode) {

				LogUtils.e(TAG, "package installed found! apk = " + apkPath);

				return true;
			}

			LogUtils.e(TAG, "version not match, installed version = "
					+ installedPackageInfo.versionCode + ", apk version = "
					+ apkinfo.versionCode);

		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isAppInstalledByPkgName(Context context,
			String pkgname) {
		PackageInfo packageInfo = null;
		boolean ret = false;

		PackageManager pm = context.getPackageManager();
		if (pkgname == null)
			return ret;
		try {
			packageInfo = pm.getPackageInfo(pkgname, 0);
		} catch (NameNotFoundException e) {
			// e.printStackTrace();
			return ret;
		}
		if (packageInfo == null)
			return ret;

		return true;
	}

	/**
	 * 启动一个应用程序
	 * 
	 * @param ctx
	 * @param packagename
	 * @return
	 */
	public static boolean launchAppByPackageName(Context ctx, String packagename) {
		boolean ret = false;
		Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(
				packagename);
		if (intent != null) {
			ret = true;
			ctx.startActivity(intent);
		}
		return ret;
	}

	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;
			try {
				return appInfo.loadIcon(pm);
			} catch (OutOfMemoryError e) {
				Log.e("Util", e.toString());
			}
		}
		return null;
	}

}
