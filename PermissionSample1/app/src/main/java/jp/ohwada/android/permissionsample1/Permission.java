/**
 * permission sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.permissionsample1;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
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
    private static final int REQUEST_PERM_STORAGE = 11;	
    
    	    private static final String PERM_STORAGE = 
        Manifest.permission.WRITE_EXTERNAL_STORAGE;	
        
         private String[] PERMS = { PERM_STORAGE };
         			
 private AppCompatActivity mActivity;
 		
	/**
     * === constractor === 
	 */	    
	 public Permission( AppCompatActivity activity ) {
	    
		mActivity = activity;

    } // Permission


    /**
     * hasPerm
     */
    public boolean hasPerm() {
        
        int result = mActivity.checkCallingOrSelfPermission( PERM_STORAGE );
        log_d( "hasPerm " + result );
        if ( result == PackageManager.PERMISSION_GRANTED ) {
            return true;
        } // if result
        
        return false;
    } // hasPermission
    
    
    
    public void requestPerm () {
        
        log_d("requestPerm");
    	mActivity.requestPermissions ( PERMS, REQUEST_PERM_STORAGE );
    } // requestPermissions
    
    
    
     /**
     * procRequestPermissionsResult
     */
    public boolean  procRequestPermissionsResult( int request, String[] permissions, int[] results ) {
    	    	
    	    	log_d("procRequestPermissionsResult" );
    	    	
    	    	           if ( request == REQUEST_PERM_STORAGE ) {

                               for (int i = 0; i < permissions.length; i++) {
                                   String perm = permissions[i];
                                   int grant = results[i];
                                   if (PERM_STORAGE.equals(perm)) {
                                       if (grant == PackageManager.PERMISSION_GRANTED) {
                                           return true;
                                       } // if grant
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