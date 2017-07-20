/**
 * package info sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.packageinfosample1;



import android.app.Activity;

import android.os.Bundle;
import android.os.Environment;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    
     	// debug
    	private final static String TAG_SUB = "MainActivity";   
		
		private TextView mTextView1;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log_d("onCreate");
        setContentView(R.layout.activity_main);
    
            	        mTextView1 = (TextView) findViewById( R.id.TextView_1 );
            	            
        	        Button btnPackage = (Button) findViewById( R.id.Button_package );
				btnPackage.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				procButtonPackage();
			}
		});	
		
    } // onCreate
    
    
       	/**
	 * procButtonPackage
	 */	
    private void procButtonPackage() {
    	        log_d("procButtonPackage");
    PackageInfoUtility   util  = new PackageInfoUtility( this );
    String msg =  util.getPackageInfo();
    log_d(msg);
    	mTextView1.setText( msg );
} // 	procButtonPackage
   
    	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d
	
} // class MainActivity
