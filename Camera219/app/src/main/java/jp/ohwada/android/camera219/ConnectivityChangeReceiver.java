/**
 * Camera2 Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera219;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * class ConnectivityChangeReceiver
 * original : https://github.com/arktronic/cameraserve
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {
    public static volatile boolean Changed = false;

/**
 * onReceive
 */ 
    @Override
    public void onReceive(Context context, Intent intent) {
        Changed = true;
    }

} // class ConnectivityChangeReceiver
