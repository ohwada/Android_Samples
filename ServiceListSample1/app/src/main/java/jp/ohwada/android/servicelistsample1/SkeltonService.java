/**
 * service list sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.servicelistsample1;

import android.app.Service;

import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;


/**
 * SkeltonService
 */
public class SkeltonService extends Service {

    private final static boolean D = Constant.DEBUG ;
 	private final static String TAG = Constant.TAG ;
 	private final static String TAG_SUB = "SkeltonService" ;


	
			
	/**
	 * === onCreate ===
	 * Called by the system when the service is first created
	 */     
    @Override
    public void onCreate() {
    	log_d( "onCreate" );
        super.onCreate();		
    } // onCreate



	/**
	 * === onStartCommand ===
	 * Called by the system every time a client explicitly starts the service 
	 * by calling startService(Intent), providing the arguments it supplied 
	 * and a unique integer token representing the start request
	 * @param Intent intent
	 * @param int flags
	 * @param int startId
	 * @return int START_STICKY_COMPATIBILITY
	 */ 
    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
    	log_d( "onStartCommand" );
        super.onStartCommand( intent, flags, startId );
// compatibility version of START_STICKY that does not guarantee that 
// onStartCommand() will be called again after being killed.
        return Service.START_STICKY_COMPATIBILITY;

    } // onStartCommand



    /**
	 * === onDestroy ===
	 */    
    @Override
    public void onDestroy() {
    	log_d( "onDestroy" );
		super.onDestroy();
    } // onDestroy
    
    
    
    /**
	 * === onBind ===
	 * @param Intent intent
	 * @return IBinder : null
	 */    
    @Override
    public IBinder onBind( Intent intent ) {
    	log_d( "onBind" );
        return null;
    } // onBind
 


	/**
	 * write log
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	}  // 	log_d
	
	
} // SkeltonService

