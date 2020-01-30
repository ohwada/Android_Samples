/**
 * SimpleCropView Sample
 * 2020-01-01 K.OHWADA
 * original : https://github.com/igreenwood/SimpleCropView
 */
package jp.ohwada.android.simplecropview1;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


/** 
 *   class BasicActivity
 */
public class BasicActivity extends AppCompatActivity {
  private static final String TAG = BasicActivity.class.getSimpleName();

  public static Intent createIntent(Activity activity) {
    return new Intent(activity, BasicActivity.class);
  }


/** 
 *   onCreate
 */
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_basic);

    if(savedInstanceState == null){
      getSupportFragmentManager().beginTransaction().add(R.id.container, BasicFragment.newInstance()).commit();
    }

    // apply custom font
    FontUtils.setFont(findViewById(R.id.root_layout));
    // initToolbar();
  }

/** 
 *   onConfigurationChanged
 */
  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

/** 
 *   onSupportNavigateUp
 */
  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return super.onSupportNavigateUp();
  }


/** 
 *   startResultActivity
 */
  public void startResultActivity(Uri uri) {
    if (isFinishing()) return;
    // Start ResultActivity
    startActivity(ResultActivity.createIntent(this, uri));
  }

} // class BasicActivity
