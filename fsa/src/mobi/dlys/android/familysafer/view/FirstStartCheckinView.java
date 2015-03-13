package mobi.dlys.android.familysafer.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mobi.dlys.android.familysafer.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 图片浏览器
 * 
 * @author chengen
 * @create 2014-10-21 上午10:55:23
 * @package mobi.dlys.android.familysafer
 */
public class FirstStartCheckinView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    // 图片集
    private List<Integer> mList = null;
    // 运行状态
    public boolean mLoop = false;
    // 获取画布
    private SurfaceHolder mSurfaceHolder = null;
    // 图片索引
    private int mCount = 0;
    // 时间间隔
    private long speed = 200;

    private static Matrix matrix = new Matrix();

    /**
     * @param context
     *            <see>容器</see>
     * @param list
     *            <see>图片地址列表 </see>
     * @param rate
     *            <see>图片切换时间　单位:毫秒</see>
     * 
     */

    public FirstStartCheckinView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mLoop = true;
        mList = new ArrayList<Integer>();

    }

    public FirstStartCheckinView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public FirstStartCheckinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    // 图像创建时
    public void surfaceCreated(SurfaceHolder holder) {
        mList.add(R.drawable.img_startcheckin1);
        mList.add(R.drawable.img_startcheckin2);
        mList.add(R.drawable.img_startcheckin3);
        mList.add(R.drawable.img_startcheckin4);
        mList.add(R.drawable.img_startcheckin5);
        mList.add(R.drawable.img_startcheckin6);
        mList.add(R.drawable.img_startcheckin7);
        mList.add(R.drawable.img_startcheckin8);
        Thread thread = new Thread(this);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mList = null;
        mLoop = false;
    }

    // 画图方法
    private void drawImg() {
        if (mCount == 8) {
            return;
        }
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas == null || mSurfaceHolder == null) {
            return;
        }
        Bitmap bitmap = null;

        try {
            int path = mList.get(mCount++);
            bitmap = readBitmap(getResources(), path);
            if (bitmap != null) {
                int height = getHeight();
                int width = getWidth();
                bitmap = getReduceBitmap(bitmap, width, height);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Style.FILL);
                paint.setColor(Color.BLACK);
                canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                canvas.drawBitmap(bitmap, matrix, paint);
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        } catch (Exception ex) {
            Log.e("ImageSurfaceView", ex.getMessage());
            return;
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    // 刷新图片
    public void run() {
        while (mLoop) {
            synchronized (mSurfaceHolder) {
                drawImg();
            }
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                Log.e("ImageSurfaceView_Thread", e.getMessage());
            }
        }
    }

    // 缩放图片
    private Bitmap getReduceBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int hight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float wScake = ((float) w / width);
        float hScake = ((float) h / hight);
        matrix.postScale(wScake, hScake);
        return Bitmap.createBitmap(bitmap, 0, 0, width, hight, matrix, true);
    }

    public static Bitmap readBitmap(Resources r, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = r.openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

}
