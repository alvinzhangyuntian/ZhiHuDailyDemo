package www.zhihudemo.com.zhihudemo.utility;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class UIUtils {
	public static DisplayMetrics metric = new DisplayMetrics();

	/**
	 * 得到屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Activity context) {
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	 /**
     * 获取设备屏幕分辨率及屏幕密度
     * 
     * @param act
     * @return
     */
    public static float[] calScreenPixels(Activity act) {
        float[] results = new float[3];
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        results[0] = dm.widthPixels;
        results[1] = dm.heightPixels;
        results[2] = dm.density;
        return results;
    }
}
