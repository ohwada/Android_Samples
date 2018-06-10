/**
 * Media Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.mediaplayermidisample;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	protected  final static boolean D = true; 
	protected String TAG = "MIDI";
	private static final String TAG_SUB = "Permission";
	
    // dialog
    private static final int RES_ID_TITLE = R.string.dialog_permission_title;

  private static final int RES_ID_MESSAGE = R.string.dialog_permission_message;

  private static final int RES_ID_YES = R.string.button_dialog_yes;

  private static final int RES_ID_NO = R.string.button_dialog_no;


	    // permission request codes
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
        log_d("setPermWriteExternalStorage");
        setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE );
} // setPermWriteExternalStorage


    /**
     * setPermReadExternalStorage
     */
    public void setPermReadExternalStorage() {
        log_d("setPermReadExternalStorage");
        setPermission(Manifest.permission.READ_EXTERNAL_STORAGE );
} // setPermReadExternalStorage


    /**
     * setPermission
     */
    public void setPermission(String perm) {
        log_d( "setPermission: " + perm );   
        mPerm = perm;
} // setPermission


    /**
     * hasPerm
     */
    public boolean hasPerm( boolean show_dialog ) {
        log_d("hasPerm");
       boolean hasPerm = checkSelfPermission( mContext, mPerm);
        if (!hasPerm && show_dialog) {
            showDialog();
        }
        return hasPerm;
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
        log_d("requestPerm");
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
     * showDialog
     */    
public void showDialog() {
    log_d("showDialog");
AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle( RES_ID_TITLE );
        builder.setMessage( RES_ID_MESSAGE );
        builder.setPositiveButton( RES_ID_YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                log_d("onClick Yes");
                requestPerm();
            }
       });

        builder.setNegativeButton( RES_ID_NO, null);

        builder.show();
} // showDialog



    /**
     * log_d
     */
    private void log_d( String str ) {
        if (D) Log.d( TAG, TAG_SUB + " " + str );
    } // log_d
    
    } // class Permission