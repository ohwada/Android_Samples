/**
 * SimpleCropView Sample
 * 2020-01-01 K.OHWADA
 * original : https://github.com/igreenwood/SimpleCropView
 */
package jp.ohwada.android.simplecropview1;



import android.app.Application;

/** 
 *   class AppController
 */
@SuppressWarnings("unused") public class AppController extends Application {

  private static final String TAG = AppController.class.getSimpleName();
  private static AppController instance;

/** 
 *   onCreate
 */
  @Override public void onCreate() {
    super.onCreate();
    instance = this;
    // load custom font
    FontUtils.loadFont(getApplicationContext(), "Roboto-Light.ttf");
  }

/** 
 *   getInstance
 */
  public static AppController getInstance() {
    return instance;
  }

} // class AppController