package cn.changl.safe360.android.utils;

import mobi.dlys.android.core.image.universalimageloader.core.DisplayImageOptions;
import mobi.dlys.android.core.image.universalimageloader.core.ImageLoader;
import mobi.dlys.android.core.image.universalimageloader.core.assist.ImageScaleType;
import mobi.dlys.android.core.image.universalimageloader.core.display.CircleBitmapDisplayer;
import mobi.dlys.android.core.image.universalimageloader.core.display.RoundedBitmapDisplayer;
import mobi.dlys.android.core.image.universalimageloader.core.listener.ImageLoadingListener;
import mobi.dlys.android.core.image.universalimageloader.core.listener.ImageLoadingProgressListener;
import mobi.dlys.android.core.image.universalimageloader.core.process.BitmapProcessor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;
import cn.changl.safe360.android.R;

public class ImageLoaderHelper {
	public static final int HEADIMAGE_CORNER_RADIUS = 90;

	/**
	 * 图像选项类
	 * 
	 * @param defResId
	 *            默认图片id
	 * @param round
	 *            是否圆角
	 * @return
	 */
	public static DisplayImageOptions imageOptionWithCircle(int defResId, boolean round, int cornerRadius) {
		DisplayImageOptions.Builder displayOptions = new DisplayImageOptions.Builder();

		displayOptions.cacheInMemory(true);
		displayOptions.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
		displayOptions.bitmapConfig(Bitmap.Config.RGB_565);
		// 防止内存溢出的，图片太多就这这个。还有其他设置
		// 如Bitmap.Config.ARGB_8888
		if (defResId != 0) {
			displayOptions.showImageOnLoading(defResId); // 默认图片
			displayOptions.showImageForEmptyUri(defResId); // url爲空會显示该图片，自己放在drawable里面的
			displayOptions.showImageOnFail(defResId); // 加载失败显示的图片
		}
		displayOptions.cacheOnDisk(true);
		if (round) {
			displayOptions.displayer(new CircleBitmapDisplayer(cornerRadius)); // 圆角
		}

		return displayOptions.build();
	}

	/**
	 * 图像选项类
	 * 
	 * @param defResId
	 *            默认图片id
	 * @param round
	 *            是否圆角
	 * @return
	 */
	public static DisplayImageOptions imageOptionWithRound(int defResId, boolean round, int cornerRadius) {
		DisplayImageOptions.Builder displayOptions = new DisplayImageOptions.Builder();

		displayOptions.cacheInMemory(true);
		displayOptions.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
		displayOptions.bitmapConfig(Bitmap.Config.RGB_565);
		// 防止内存溢出的，图片太多就这这个。还有其他设置
		// 如Bitmap.Config.ARGB_8888
		if (defResId != 0) {
			displayOptions.showImageOnLoading(defResId); // 默认图片
			displayOptions.showImageForEmptyUri(defResId); // url爲空會显示该图片，自己放在drawable里面的
			displayOptions.showImageOnFail(defResId); // 加载失败显示的图片
		}
		displayOptions.cacheOnDisk(true);
		if (round) {
			displayOptions.displayer(new RoundedBitmapDisplayer(cornerRadius)); // 圆角
		}

		return displayOptions.build();
	}

	public static DisplayImageOptions imageOptionWithRound(int defResId, boolean round, BitmapProcessor post) {
		DisplayImageOptions.Builder displayOptions = new DisplayImageOptions.Builder();
		displayOptions.cacheInMemory(true);
		displayOptions.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
		displayOptions.bitmapConfig(Bitmap.Config.RGB_565);// 防止内存溢出的，图片太多就这这个。还有其他设置
		// 如Bitmap.Config.ARGB_8888
		if (defResId != 0) {
			displayOptions.showImageOnLoading(defResId); // 默认图片
			displayOptions.showImageForEmptyUri(defResId); // url爲空會显示该图片，自己放在drawable里面的
			displayOptions.showImageOnFail(defResId);// 加载失败显示的图片
		}
		displayOptions.cacheOnDisk(true);
		displayOptions.postProcessor(post);
		if (round) {
			displayOptions.displayer(new RoundedBitmapDisplayer(HEADIMAGE_CORNER_RADIUS)); // 圆角
		}

		return displayOptions.build();
	}

	public static DisplayImageOptions imageOptionWithRound(int defResId, BitmapProcessor post, int cornerRadius) {
		DisplayImageOptions.Builder displayOptions = new DisplayImageOptions.Builder();
		displayOptions.cacheInMemory(true);
		displayOptions.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
		displayOptions.bitmapConfig(Bitmap.Config.RGB_565);// 防止内存溢出的，图片太多就这这个。还有其他设置
		// 如Bitmap.Config.ARGB_8888
		if (defResId != 0) {
			displayOptions.showImageOnLoading(defResId); // 默认图片
			displayOptions.showImageForEmptyUri(defResId); // url爲空會显示该图片，自己放在drawable里面的
			displayOptions.showImageOnFail(defResId);// 加载失败显示的图片
		}
		displayOptions.cacheOnDisk(true);
		displayOptions.postProcessor(post);
		displayOptions.displayer(new RoundedBitmapDisplayer(cornerRadius)); // 圆角

		return displayOptions.build();
	}

	/**
	 * 显示用户头像 (默认图片是用户头像)
	 * 
	 * @param uri
	 * @param imageView
	 */
	public static void displayAvatar(String uri, ImageView imageView) {
		if (TextUtils.isEmpty(uri) || null == imageView) {
			return;
		}

		// 本地如果存在，则从本地读取
		String filepath = FileUtils.getFilePath(FileUtils.COVER, MD5.encoderForString(uri), FileUtils.JPG);
		if (FileUtils.exists(filepath)) {
			uri = "file:/" + filepath;
		} else {
			if (FileUtils.exists(uri)) {
				uri = "file:/" + uri;
			}
		}

		ImageLoader.getInstance().displayImage(uri, imageView, imageOptionWithCircle(R.drawable.user, true, HEADIMAGE_CORNER_RADIUS));

	}

	/**
	 * 显示图片
	 * 
	 * @param uri
	 * @param imageView
	 * @param defResId
	 *            默认图片id
	 * @param round
	 *            是否圆角
	 */
	public static void displayImage(String uri, ImageView imageView, int defResId, boolean round) {
		if (null == imageView) {
			return;
		}

		if (TextUtils.isEmpty(uri)) {
			imageView.setImageResource(defResId);
			return;
		}

		// 本地如果存在，则从本地读取
		String filepath = FileUtils.getFilePath(FileUtils.COVER, MD5.encoderForString(uri), FileUtils.JPG);
		if (FileUtils.exists(filepath)) {
			uri = "file:/" + filepath;
		} else {
			if (FileUtils.exists(uri)) {
				uri = "file:/" + uri;
			}
		}

		ImageLoader.getInstance().displayImage(uri, imageView, imageOptionWithRound(defResId, round, HEADIMAGE_CORNER_RADIUS));
	}

	public static void displayImage(String uri, ImageView imageView, int defResId, boolean round, ImageLoadingListener listener,
			ImageLoadingProgressListener progressListener) {
		if (null == imageView) {
			return;
		}

		if (TextUtils.isEmpty(uri)) {
			imageView.setImageResource(defResId);
			return;
		}

		// 本地如果存在，则从本地读取
		String filepath = FileUtils.getFilePath(FileUtils.COVER, MD5.encoderForString(uri), FileUtils.JPG);
		if (FileUtils.exists(filepath)) {
			uri = "file:/" + filepath;
		} else {
			if (FileUtils.exists(uri)) {
				uri = "file:/" + uri;
			}
		}

		ImageLoader.getInstance().displayImage(uri, imageView, imageOptionWithRound(defResId, round, HEADIMAGE_CORNER_RADIUS), listener, progressListener);
	}

	public static void displayImage(String uri, ImageView imageView, int defResId, BitmapProcessor post, boolean round) {
		if (null == imageView) {
			return;
		}

		if (TextUtils.isEmpty(uri)) {
			imageView.setImageResource(defResId);
			return;
		}

		// 本地如果存在，则从本地读取
		String filepath = FileUtils.getFilePath(FileUtils.COVER, MD5.encoderForString(uri), FileUtils.JPG);
		if (FileUtils.exists(filepath)) {
			uri = "file:/" + filepath;
		} else {
			if (FileUtils.exists(uri)) {
				uri = "file:/" + uri;
			}
		}

		ImageLoader.getInstance().displayImage(uri, imageView, imageOptionWithRound(defResId, round, post));
	}

	public static void displayImage(String uri, ImageView imageView, int defResId, boolean round, int cornerRadius) {
		if (null == imageView) {
			return;
		}

		if (TextUtils.isEmpty(uri)) {
			return;
		}

		// 本地如果存在，则从本地读取
		String filepath = FileUtils.getFilePath(FileUtils.COVER, MD5.encoderForString(uri), FileUtils.JPG);
		if (FileUtils.exists(filepath)) {
			uri = "file:/" + filepath;
		} else {
			if (FileUtils.exists(uri)) {
				uri = "file:/" + uri;
			}
		}

		ImageLoader.getInstance().displayImage(uri, imageView, imageOptionWithRound(defResId, round, cornerRadius));
	}

	public static void displayImage(String uri, ImageView imageView, int defResId, BitmapProcessor post, boolean round, int cornerRadius) {
		if (null == imageView) {
			return;
		}

		if (TextUtils.isEmpty(uri)) {
			return;
		}

		// 本地如果存在，则从本地读取
		String filepath = FileUtils.getFilePath(FileUtils.COVER, MD5.encoderForString(uri), FileUtils.JPG);
		if (FileUtils.exists(filepath)) {
			uri = "file:/" + filepath;
		} else {
			if (FileUtils.exists(uri)) {
				uri = "file:/" + uri;
			}
		}

		ImageLoader.getInstance().displayImage(uri, imageView, imageOptionWithRound(defResId, post, cornerRadius));
	}
}
