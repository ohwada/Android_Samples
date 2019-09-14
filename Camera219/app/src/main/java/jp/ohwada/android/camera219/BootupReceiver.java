/**
 * Camera2 Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera219;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * class BootupReceiver 
 * original : https://github.com/arktronic/cameraserve
 */
public class BootupReceiver extends BroadcastReceiver {

   private final static String KEY_RUN_ON_BOOT = SettingsFragment.KEY_RUN_ON_BOOT;

/**
 * onReceive
 */ 
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && shouldRunOnBoot()) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }

/**
 * shouldRunOnBoot
 */ 
    private boolean shouldRunOnBoot() {
        Context ctx = AndroidApplication.getInstance().getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        return preferences.getBoolean(KEY_RUN_ON_BOOT, true);
    }

} // class BootupReceiver
