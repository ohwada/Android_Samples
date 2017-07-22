/**
 * service list sample
 * 2017-07-01 K.OHWADA 
 */
 
package jp.ohwada.android.servicelistsample1;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.util.Log;


/**
 * Service List
 */
public class ServiceList  {
        
    private final static boolean D = Constant.DEBUG ;
 	private final static String TAG = Constant.TAG ;
 	private final static String TAG_SUB = "ServiceList" ;
 	  
	private final static String LF = "\n" ;

private Context mContext;



	/**
	 * === constractor ===
	 * @param Context context
	 */
    public ServiceList( Context context ) {
    	mContext = context;
    }



	/**
	 * get String
	 * @return String
	 */  	
	public String getString() {
		String msg = "";
		List<String> list = getServiceNameList();
if ( ( list == null )||( list.size() == 0 )) return msg;
	    	for ( String name : list ) {
	    		msg += name + LF;
	   } // for
	return msg;
} // getString





	/**
	 * isRunning
	 	 * @ param String serviceName
	 * @return boolean
	 */  	
	public boolean isRunning( String serviceName ) {
if ( (serviceName  == null )||( serviceName.length() == 0 ) ) return false;
		List<String> list = getServiceNameList();
if ( ( list == null )||( list.size() == 0 )) return false;
	    	for ( String name : list ) {
	    		if ( serviceName.equals( name ) ) return true;
	   } // for
		return false;
} // isRunning    		

	/**
	 * geSserviceNameList
	 * @return List<String>
	 */  	
	public List<String> getServiceNameList( ) {
		
		List<String> list = new ArrayList<String>();
		List<RunningServiceInfo> list_info = getServiceInfoList();
if ( (list_info  == null )||( list_info.size() == 0 ) ) return list;
	    	for ( RunningServiceInfo info : list_info ) {
	         		String name = info.service.getClassName();
list.add( name );
	   } // for
	   return list;		
} // geSserviceNameList


	/**
	 * getServiceInfoList
	 * @return List<RunningServiceInfo>
	 */  	
	public List<RunningServiceInfo> getServiceInfoList() {
				ActivityManager manager = (ActivityManager) mContext.getSystemService( Context.ACTIVITY_SERVICE );
return manager.getRunningServices( Integer.MAX_VALUE );

} // getServiceInfoServices     		
  
  
  
     		




	
	/**
	 * write log
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	}  // 	log_d
		  
} // ServiceList
