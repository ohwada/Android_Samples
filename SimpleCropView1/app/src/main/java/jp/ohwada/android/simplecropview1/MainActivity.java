/**
 * SimpleCropView Sample
 * 2020-01-01 K.OHWADA
 * original : https://github.com/igreenwood/SimpleCropView
 */
package jp.ohwada.android.simplecropview1;


// 【Android】画像の切り抜きをシンプルに実装できるライブラリを公開しました-SimpleCropView
// https://qiita.com/issei_aoki/items/810f491da2e3d077b478

//https://github.com/igreenwood/SimpleCropView


import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


/** 
 *   class MainActivity
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // debug
    private final static String TAG = "MainActivity";

    public static final int REQUEST_CODE_COPY_PERMISSIONS = 103;

    private final static String FILE_EXT = ".png";

    private ActivityPermission mCopyPerm;

/** 
 *   onCreate
 */
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.basic_sample_button).setOnClickListener(this);

    findViewById(R.id.rx_sample_button).setOnClickListener(this);

    findViewById(R.id.Button_copy).setOnClickListener(this);

    // apply custom font
    FontUtils.setFont(findViewById(R.id.root_layout));

    initToolbar();

        mCopyPerm = new ActivityPermission(this);
        mCopyPerm.setRequestCode(REQUEST_CODE_COPY_PERMISSIONS);
        mCopyPerm.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
  }


/** 
 *  onRequestPermissionsResult
 */
@Override
public void onRequestPermissionsResult(
            int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_COPY_PERMISSIONS:
                if (mCopyPerm.isGrantRequestPermissionsResult(requestCode, grantResults)) {
                            copyFiles();
                }
                break;
        }
}



/** 
 *   onClick
 */
  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.basic_sample_button:
        startActivity(BasicActivity.createIntent(this));
        break;
      case R.id.rx_sample_button:
        //startActivity(RxActivity.createIntent(this));
        showToast("NOT Support");
        break;
      case R.id.Button_copy:
        copyFiles();
        break;
    } //  switch
  }

  private void initToolbar() {
    //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    //setSupportActionBar(toolbar);
    //ActionBar actionBar = getSupportActionBar();
    //FontUtils.setTitle(actionBar, "SimpleCropView");
  }


/** 
 *   copyFiles from Asset folder to ExternalStoragePublicPictures
 */
private void copyFiles() {
        if (mCopyPerm.requestPermissions()) {
            log_d("copyFiles: not perm");
            return;
        }

    AssetFile assetFile = new AssetFile(this);
    assetFile.mkDirInExternalStoragePublicPictures();
    boolean ret = assetFile.copyFilesAssetToExternalStoragePublicPictures( FILE_EXT );
    if(ret) {
        showToast("copy successful");
    } else {
        showToast("copy faild");
    }
}


/**
 * showToast
 */
private void showToast( String msg ) {
		Toast.makeText( this, msg, Toast.LENGTH_LONG ).show();
} 


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	   Log.d( TAG, msg );
} 


}
