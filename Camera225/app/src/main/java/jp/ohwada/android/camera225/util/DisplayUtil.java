/**
 * Camera2 Sample
 * 2019-10-01 K.OHWADA
 */
package jp.ohwada.android.camera225.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;


 /**
  *  class DisplayUtil
 * original : https://github.com/EzequielAdrianM/Camera2Vision
  */
public class DisplayUtil {


 /**
  *  dpToPx
  */
    public static int dpToPx(int dp) {
        float density = getDensity();
        return (int) (dp * density);
    }


 /**
  *  getDensity
  */
    public static float getDensity() {
        DisplayMetrics displayMetrics = getDisplayMetrics();
        return displayMetrics.density;
    }


/**
  * getDisplayRatio
 */
    public static  float getDisplayRatio() {
            DisplayMetrics metrics = getDisplayMetrics();
            float ratio = ((float)metrics.heightPixels / (float)metrics.widthPixels);
        return ratio;
    }


 /**
  *  getDisplayMetrics
  */
    public static DisplayMetrics getDisplayMetrics() {
        return Resources.getSystem().getDisplayMetrics();
    }


 /**
  *  getDisplaySize
  */
    public static Point getDisplaySize(Context context) {
        Display display = getDefaultDisplay(context);
        Point size = new Point();
        display.getSize(size);
        return size;
    }




 /**
  *  getDefaultDisplay
  */
    public static Display getDefaultDisplay(Context context) {
        WindowManager wm = getWindowManager(context);
        return wm.getDefaultDisplay();
    }


 /**
  *  getWindowManager
  */
    public static WindowManager getWindowManager(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm;
    }


 /**
  *  getDisplayRotation
  */
    public static int getDisplayRotation(Context context) {
        Display display = getDefaultDisplay(context);
        return display.getRotation();
    }


} // class DisplayUtil

