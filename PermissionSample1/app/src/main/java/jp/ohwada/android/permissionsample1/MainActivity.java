/**
 * Runtime Permission Sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.permissionsample1;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * MainActivity
 */
 public class MainActivity extends Activity {
   
    	// debug
    	private final static String TAG_SUB = "MainActivity";
    	
    	private Permission mPermission;
    	
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
        
            mPermission = new Permission( this );
            mPermission.setPermWriteExternalStorage();
     
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
     * === onRequestPermissionsResult ===
     */
    @Override
    public void onRequestPermissionsResult( int request, String[] permissions, int[] results ) {
    	
    	boolean ret = mPermission.procRequestPermissionsResult( request, permissions, results );
    	
        if ( ret ) {
        	procCopy();
        }
        
        super.onRequestPermissionsResult(request, permissions, results);
        
    } // onRequestPermissionsResult
    
    
    
    
      	/**
	 * clickButtonCopy
	 */	
    private void clickButtonCopy() {
    	
    	if ( mPermission.hasPerm() )  {
		procCopy();
	} else {
		showPermissionDialog();
	}

} // 	clickButtonCopy


/**
 * procCopy
 */
 private void procCopy() {
   		mImageFile.mkdirsExternal("images");
		boolean ret = mImageFile.copyAssetsFilesToExternal("png"); 
        if (ret) {
            toast_short("copied");
        } else {
            toast_short("copy faild");
        }
} // procCopy
   

    	/**
	 * clickImageView1
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
				       } // if bitmap
	
} // clickImageView1


    /**
     * showPermissionDialog
     */
    private void showPermissionDialog() {

        final YesNoDialog dialog = new YesNoDialog( this );
        dialog.setTitle( R.string.dialog_permission_title );
        dialog.setMessage( R.string.dialog_permission_storage );
                    
        dialog.setOnChangedListener(
            new YesNoDialog.OnChangedListener() {
            	
            public void onClickYes() {
                dialog.dismiss();
                mPermission.requestPerm();
            }
            
                        public void onClickNo() {
                dialog.dismiss();
                // dummy
            }
            
          } );
          
          dialog.show();
        } // showPermissionDialog  
  
          
/**
 * toast_short
 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d
	
 } // class MainActivity
