package greendao.wislie.com.greendaolearning.slide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
/**
 * author : wislie
 * e-mail : 254457234@qq.com
 * date   : 2016/08/23 10:03 AM
 * desc   : 全局Application
 * version: 1.0
 */
public class DensityUtil {

    /**
     * 获取屏幕的宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        return width;
    }

    /**
     * 获取屏幕的高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;       // 屏幕高度（像素）
        return height;
    }

    /**
     * 获取屏幕的宽度
     * @param context
     * @return
     */
    public static int getWidth(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        return outSize.x;
    }

    /**
     * 获取屏幕的高度
     * @param context
     * @return
     */
    public static int getHeight(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        return outSize.y;
    }

    /**
     * dp转换成px
     */
    public static float dp2px(Context context, float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }

    /**
     * px转换成dp
     */
    public static float px2dp(Context context, float pxValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale;
    }

    /**
     * sp转换成px
     */
    public static float sp2px(Context context, float spValue){
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale;
    }

    /**
     * px转换成sp
     */
    public static float px2sp(Context context, float pxValue){
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return pxValue / fontScale;
    }

    /**
     * 获取View的坐标
     * @param view
     * @return 返回矩形View的位置Rect
     */
    public static RectF calcViewScreenLocation(View view) {
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }
}
