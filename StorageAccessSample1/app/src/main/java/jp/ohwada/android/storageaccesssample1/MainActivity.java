/**
Storage Access Framework
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.storageaccesssample1;


import android.app.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;

/**
 * MainActivity
 */
 
 // https://developer.android.com/guide/topics/providers/document-provider.html?hl=ja
public class MainActivity extends Activity {
    
    	// debug
    	private final static String TAG_SUB = "MainActivity";


	 private Client mClient;
	
			private TextView mTextView1;
		private ImageView mImageView1;

	
	/**
 	 * === onCreate === 
 	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        		 mClient = new Client( this );
        		
           mTextView1 = (TextView) findViewById( R.id.TextView_1 );
           
                		
	        Button btnOpen = (Button) findViewById( R.id.Button_open );
				btnOpen.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procClickButtonOpen();
			}
		});	
		
		
        mImageView1 = (ImageView) findViewById( R.id.ImageView_1 );
				mImageView1.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procClickImageView1();
			}
		});
		
		

    }



@Override
public void onActivityResult(int requestCode, int resultCode,
        Intent resultData) {
Uri uri = mClient.procActivityResult( requestCode, resultCode,
        resultData );
	if ( uri != null ) {
	String data = mClient.dumpImageMetaData(uri);
	mTextView1.setText( data);
	
	Bitmap bitmap = mClient.getBitmapFromUri( uri );
		if ( bitmap != null ) {
				mImageView1.setImageBitmap( bitmap );
	} // bitmap 
	
	} // uri
	
    }	// onActivityResult
    
    


                 	
      	/**
	 * procClickButtonOpen
	 */	
    private void procClickButtonOpen() {
    	mClient.performFileSearch();
	} // procClickButtonOpen
    
    
    	/**
	 * procClickImageView1
	 */	
    private void procClickImageView1() {
	    // dummy
} // procClickImageView1


         
            
 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d


	
 } // class MainActivity
