/**
 * Screen Capture Sample
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.screencapture2;


import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * class DisplayUtil
 * 
 */
public class DisplayUtil  {


/**
 * getDisplayMetrics
 */
public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources res = context.getResources();
        DisplayMetrics metrics =  res.getDisplayMetrics();
        return  metrics;
}


} // class DisplayUtil
