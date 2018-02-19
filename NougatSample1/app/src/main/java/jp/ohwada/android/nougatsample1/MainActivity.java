/**
 * Android 7.0 Nougat
 * ScopedDirectoryAccess
 * 2018-02-01 K.OHWADA 
 */

package jp.ohwada.android.nougatsample1;

import android.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * MainActivity
 */
public class MainActivity extends Activity {
    
    	// debug
    	private final static String TAG_SUB = "MainActivity";
    	
    // REQUEST_CODE
    private final static int REQUEST_CODE_WRITE = 1;
    private final static int REQUEST_CODE_READ = 2;

    private  ScopedDirectoryAccess mScopedDirectoryAccess;

	private ImageView mImageView1;
	
	/**
 	 * === onCreate === 
 	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mScopedDirectoryAccess = new ScopedDirectoryAccess( this );
    		
	        Button btnCopy = (Button) findViewById( R.id.Button_copy );
				btnCopy.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
                startInternalStorageAccessIntent(REQUEST_CODE_WRITE);
			}
		});	
		
        mImageView1 = (ImageView) findViewById( R.id.ImageView_1 );
				mImageView1.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
            startInternalStorageAccessIntent(REQUEST_CODE_READ);
			}
		});
		
    } // onCreate


	/**
 	 * ===onActivityResult === 
 	 */
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

        String msg = "onActivityResultt: requestCode = " + requestCode + "resultCode = " + resultCode;
        log_d(msg);

        if (( requestCode == REQUEST_CODE_WRITE )&&(resultCode == RESULT_OK )) {
                mScopedDirectoryAccess.copyImageFiles(data);
        } else if (( requestCode == REQUEST_CODE_READ )&&( resultCode == RESULT_OK )) {
                    Bitmap bitmap = mScopedDirectoryAccess.getBitmap(data);
                            mImageView1.setImageBitmap(bitmap); 
        }
    } // onActivityResult



      	/**
	 *  startInternalStorageAccessIntent
	 */	
    private void startInternalStorageAccessIntent( int requestCode ) {

        String msg = "startInternalStorageAccessIntent: " + requestCode;
        log_d(msg);
        Intent intent =  mScopedDirectoryAccess.getInternalStorageAccessIntent(Environment.DIRECTORY_PICTURES);
        startActivityForResult(intent, requestCode);
        } // startInternalStorageAccessIntent



 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d
	
 } // class MainActivity
