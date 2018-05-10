/**
 * Runtime Permission Sample
 * with ContextCompat and ActivityCompat
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.permissionsample1;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;


/**
 * Permission
 */
public class Permission {
	
	// debug
	private static final String TAG_SUB = "Permission";
	
	    /* permission request codes */
    private static final int REQUEST_CODE = 11;	
    

     	    private String mPerm =
        Manifest.permission.WRITE_EXTERNAL_STORAGE;	
         			
    private Activity mActivity;
    private Context mContext;
		
	/**
     * === constractor === 
	 */	    
	 public Permission( Activity activity ) {
		mActivity = activity;
        mContext = (Context)activity;
    } // Permission


    /**
     * setPermWriteExternalStorage
     */
    public void setPermWriteExternalStorage() {
        setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE );
} // setPermWriteExternalStorage


    /**
     * setPermReadExternalStorage
     */
    public void setPermReadExternalStorage() {
        setPermission(Manifest.permission.READ_EXTERNAL_STORAGE );
} // setPermReadExternalStorage


    /**
     * setPermission
     */
    public void setPermission(String perm) {
        log_d( "setPermission: " + perm );   
        mPerm= perm;
} // setPermission

    /**
     * hasPerm
     */
    public boolean hasPerm() {
        return checkSelfPermission( mContext, mPerm);
    } // hasPerm


    /**
     * checkSelfPermission
     */
    public boolean checkSelfPermission(Context context, String perm) {

        log_d( "checkSelfPermission: " + perm );        
        int result = ContextCompat.checkSelfPermission(context, perm);

        log_d( "checkSelfPermission: " + result );
        if ( result == PackageManager.PERMISSION_GRANTED ) {
            return true;
        } // if result
        
        return false;
    } // checkSelfPermission
    
 
     /**
     * requestPerm
     */    
    public void requestPerm() {
        String[] perms = { mPerm };
        requestPermissions( mActivity, perms, REQUEST_CODE );
   } // requestPerm


     /**
     * requestPermissions
     */    
    public void requestPermissions( Activity activity, String[] perms, int requestCode ) {
        
        log_d("requestPermissions: " + perms[0] );

    	ActivityCompat.requestPermissions( activity, perms, requestCode );

    } // requestPermissions
    
    
    
     /**
     * procRequestPermissionsResult
     */
    public boolean  procRequestPermissionsResult( int request, String[] permissions, int[] results ) {
    	    	
    	    	log_d("procRequestPermissionsResult" );
    	    	
    	    	           if ( request == REQUEST_CODE ) {

                               for (int i = 0; i < permissions.length; i++) {
                                   String perm = permissions[i];
                                   int result = results[i];
                                   if (mPerm.equals(perm)) {
                                       if (result == PackageManager.PERMISSION_GRANTED) {
                                           return true;
                                       } // if result
                                   } // if perm
                               } // for  permissions
                           } // if request
        
            return false;
    } // procRequestPermissionsResult
    


    /**
     * log_d
     */
    private void log_d( String str ) {
        if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + str );
    } // log_d
    
    } // class Permission