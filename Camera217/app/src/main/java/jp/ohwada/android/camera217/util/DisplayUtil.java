/**
 * Camera2 Sample
 * DisplayUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera217.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
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


/**
 * getViewRotationDegrees
 */ 
public static int getViewRotationDegrees(Context context) {

    int rotation = getDisplayRotation( context);
    int degree = 0;
    switch (rotation) {
        case Surface.ROTATION_0:
            degree =  0;
            break;
        case Surface.ROTATION_90: 
            degree =  -90;
            break;
        case Surface.ROTATION_180: 
            degree =  180;
            break;
        case Surface.ROTATION_270:
            degree =  90;
            break;
    }
    return  degree;
}


} // class DisplayUtil

