/**
 * Camera2 Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera219;


import android.app.Application;

/**
 * class AndroidApplication
 */
public class AndroidApplication extends Application {
    private static AndroidApplication instance;

/**
 * getInstance
 */
    public static AndroidApplication getInstance(){
        return instance;
    }

/**
 * onCreate
 */
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }

} // class AndroidApplication
