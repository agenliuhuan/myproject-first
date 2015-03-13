package mobi.dlys.android.familysafer.camera;

import java.io.File;

import mobi.dlys.android.core.utils.ImageUtils;
import mobi.dlys.android.familysafer.utils.FileUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;

public class Snapshot {
	public static final int AVATAR_PERCENT = 90;
	public static final int CLUE_PERCENT = 50;
	public static final int AVATAR_WIDTH_HEIGHT = 320;
	public static final int CLUE_IMAGE_WIDTH = 480;

	private String mImgPath1 = "";
	private String mImgPath2 = "";
	private int mQualityPercent = 90;

	// 启动拍照并设置拍照参数
	// act onActivityResult回调处理方法所在的activity实例对象
	// photoFilePath 图像保存的全路径
	// photoQty 拍照图像保存的质量，取值范围1-10
	// w 设置保存的图像宽度
	// intentCode onActivityResult回调返回的requestCode数值

	public void takePhoto(Activity act, String photoFilePath, int intentCode, int percent) {
		mImgPath1 = photoFilePath + "_src.jpg";
		mImgPath2 = photoFilePath + FileUtils.JPG;
		mQualityPercent = percent;

		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File out = new File(mImgPath1);
		if (out.exists()) {
			out.delete();
		}
		Uri uri = Uri.fromFile(out);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		act.startActivityForResult(imageCaptureIntent, intentCode);
	}

	public String onActivityResultProc() {
		Bitmap bMapRotate = null;
		Bitmap bitmap = null;

		bitmap = ImageUtils.getBitmapFromFileEx(mImgPath1, -1);
		bMapRotate = ImageUtils.fixSnapshotOrder(bitmap, mImgPath1);
		ImageUtils.saveBitmapToFile(bMapRotate, mImgPath2, mQualityPercent);

		ImageUtils.recycleBitmap(bitmap);
		ImageUtils.recycleBitmap(bMapRotate);
		FileUtils.delFile(mImgPath1);
		return mImgPath2;
	}

	public String onActivityResultProc(int w) {
		Bitmap bMapRotate = null;
		Bitmap bitmap = null;
		int width = w;

		int orientation = ImageUtils.readPictureDegree(mImgPath1);
		if (orientation == 90 || orientation == 270) {
			Point pt = ImageUtils.getBitmapWidthAndHeight(mImgPath1);
			if (pt != null && pt.x > 0 && pt.y > 0) {
				width = width * pt.x / pt.y;
			}
		}
		bitmap = ImageUtils.getBitmapFromFileEx(mImgPath1, width);
		bMapRotate = ImageUtils.fixSnapshotOrder(bitmap, mImgPath1);
		ImageUtils.saveBitmapToFile(bMapRotate, mImgPath2, mQualityPercent);

		ImageUtils.recycleBitmap(bitmap);
		ImageUtils.recycleBitmap(bMapRotate);
		FileUtils.delFile(mImgPath1);
		return mImgPath2;
	}
}
