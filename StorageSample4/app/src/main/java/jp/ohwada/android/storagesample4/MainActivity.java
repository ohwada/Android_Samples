/**
 * write Storage in Android4.4 earlier style
 * 2017-06-01 K.OHWADA 
 */
 
 package jp.ohwada.android.storagesample4;

import android.app.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * MainActivity
 */
public class MainActivity extends Activity {
    
    	// debug
    	private final static String TAG_SUB = "MainActivity";
    	
	private ImageFile mImageFile;
	
		private ImageView mImageView1;
	
	private int mNum = 0;
	
	/**
 	 * === onCreate === 
 	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        		mImageFile = new ImageFile( this );	
        		
	        Button btnCopy = (Button) findViewById( R.id.Button_copy );
				btnCopy.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				clickButtonCopy();
			}
		});	
		
        mImageView1 = (ImageView) findViewById( R.id.ImageView_1 );
				mImageView1.setOnClickListener( new View.OnClickListener() {
	 		@Override
			public void onClick( View v ) {
				clickImageView1();
			}
		});
		
		

    }


      	/**
	 * clickButtonCopy
	 */	
    private void clickButtonCopy() {
		mImageFile.mkdirsExternal("images");
		mImageFile.copyAssetsFilesToExternal("png");
	}
    
    
    	/**
	 * clickPhoto1
	 */	
    private void clickImageView1() {
    	 mNum ++;
    	 if (mNum  > 10) {
    	   mNum  = 1; 
    	 }
    	
  
				       String name =  "image_" + mNum + ".png";
				       Bitmap bitmap = mImageFile.getExternalBitmap( name );
				       if (bitmap != null ) {
				       			mImageView1.setImageBitmap( bitmap );
				       }
	
}

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d
	
 } // class MainActivity
