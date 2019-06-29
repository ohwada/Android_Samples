 
package jp.ohwada.android.camera215.util;

import android.content.Context;
import android.widget.Toast;


/** 
 * ToastMaster
 */
public class ToastMaster {

    private static Toast sToast = null;

    /**
     * === Construct ===
     */
    private ToastMaster() {
		// dummy
    }

	/** 
	 * setToast
	 * @param Toast toast
	 */
    public static void setToast( Toast toast ) {
        if (sToast != null)
            sToast.cancel();
        sToast = toast;
    }

	/** 
	 * cancelToast
	 */
    public static void cancelToast() {
        if (sToast != null)
            sToast.cancel();
        sToast = null;
    }

	/**
     * Make a standard toast that just contains a text view.
     *
     * @param context  The context to use.  
     * @param text     The text to show.  Can be formatted text.
     * @param duration How long to display the message.  
	 * @return Toast
     *
     */
    public static Toast makeText( Context context, CharSequence text, int duration ) {
		Toast toast = Toast.makeText( context, text, duration );
        setToast( toast );
        return toast;
	}
    
	/**
     * Make a standard toast that just contains a text view.
     *
     * @param context  The context to use.  
     * @param resId    The resource id of the string resource to use.  
     * @param duration How long to display the message.  
	 * @return Toast
     *
     */
    public static Toast makeText( Context context, int resId , int duration ) {
		Toast toast = Toast.makeText( context, resId, duration );
        setToast( toast );
        return toast;
	}
}
