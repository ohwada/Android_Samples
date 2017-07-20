/**
 * package info sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.packageinfosample1;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureGroupInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Parcelable;


/**
 * PackageInfoUtility
 */	 
public class PackageInfoUtility {
    
            // debug 
        private final static boolean D = Constant.DEBUG ;
	 	private final static String TAG_SUB = "PackageInfoUtility" ;
	 	
	private final static String LF = "\n";
	
	private Context mContext;

public PackageInfoUtility( Context context ) {
	mContext = context;
} // 	PackageInfoUtility


/**
 * getPackageInfo
 * @ return String
 */	 
public String getPackageInfo() {
String msg = "";	

              PackageInfo packageInfo = null;
        try {
          packageInfo = mContext.getPackageManager().getPackageInfo( mContext.getPackageName(), PackageManager.GET_META_DATA );

          
//INSTALL_LOCATION_AUTO
//Constant corresponding to auto in the installLocation attribute


   // INSTALL_LOCATION_INTERNAL_ONLY
// Constant corresponding to internalOnly in the installLocation attribute


//int	INSTALL_LOCATION_PREFER_EXTERNAL
//Constant corresponding to preferExternal in the installLocation attribute


   // REQUESTED_PERMISSION_GRANTED
// Flag for requestedPermissionsFlags: the requested permission is currently granted to theapplication


// baseRevisionCode
// The revision number of the base APK for this package, as specified by the <manifest>  
   msg += "baseRevisionCode: " + packageInfo.baseRevisionCode + LF;



// The time at which the app was first installed.
msg += "firstInstallTime: " + packageInfo.firstInstallTime + LF;
  
   
   // The install location requested by the package
msg += "installLocation: " + packageInfo.installLocation + LF;



	// lastUpdateTime
//The time at which the app was last updated.
         msg += "lastUpdateTime: " +   packageInfo.lastUpdateTime + LF;
    
         
 	// packageName
// The name of this package.
         msg +=  "packageName: " + packageInfo.packageName + LF;


	//sharedUserId
//The shared user ID name of this package, as specified by the <manifest> tag's sharedUserId attribute.
           msg += "sharedUserId: " + packageInfo.sharedUserId + LF; 
   
           
   	//sharedUserLabel
//The shared user ID label of this package, as specified by the <manifest> tag's sharedUserLabel attribute.           
             msg += "sharedUserLabel: " + packageInfo.sharedUserLabel + LF;
      
      
   // version number of this package, as specified by the <manifest> tag's // versionCode attribute.
           msg += "versionCode: " + packageInfo.versionCode + LF;


//The version name of this package, as specified by the <manifest> tag's // versionName attribute.
           msg += "versionName: " + packageInfo.versionName + LF;

} catch (Exception e) {
            if (D) e.printStackTrace();
} // try
 
      
        // CREATOR
         
    msg += "activities: " + getActivities(packageInfo) + LF;  
         
    msg += "Services: " +   getServices(packageInfo) + LF; 
              
          msg += "Providers: " +  getProviders(packageInfo) + LF;   
                
              msg += "Receivers: " +   getReceivers(packageInfo) + LF;   
              
                 msg += "Permissions: " +  getPermissions(packageInfo) + LF; 
               
               return msg;
         }//  getPackageInfo  


    
                
 /**
 * getActivities
 */	                  
//Array of all <activity> tags included under <application>, or null if there were none.
  private String getActivities(PackageInfo packageInfo) { 
      String msg = ""; 
  try {               
ActivityInfo[]	infos   = packageInfo.activities;
for (int i=0; i<infos.length; i++ ) {
    ActivityInfo info = infos[i];
    msg += info.toString() + LF;
} // for

} catch (Exception e) {
            if (D )e.printStackTrace();
} // try

return msg;
} //getActivities 
   
   
     
  /**
 *  getServices
 */	
 // Array of all <service> tags included under <application>, or null if there were none.
private String getServices( PackageInfo packageInfo) {
                          String msg = ""; 
try { 
ServiceInfo[] infos = packageInfo.services;
for (int i=0; i<infos.length;  i++) {
    ServiceInfo info = infos[i];
    msg += info.toString() + LF;
} // for

            } catch (Exception e) {
            if (D )e.printStackTrace();
} // try

return msg;
} //  getServices
     
     

 /**
 *  getReceivers
 */	
//Array of all <receiver> tags included under <application>, or null if there were none.
private String getReceivers( PackageInfo packageInfo) {
                          String msg = ""; 
try { 
ActivityInfo[] infos = packageInfo.receivers;
for (int i=0; i<infos.length;  i++) {
    ActivityInfo info = infos[i];
    msg += info.toString() + LF;
} // for

            } catch (Exception e) {
            if (D )e.printStackTrace();
} // try

return msg;
} //  getReceivers
    
    
          
  /**
 *  getProviders
 */	
 //Array of all <provider> tags included under <application>, or null if there were none.
private String getProviders( PackageInfo packageInfo) {
                          String msg = ""; 
try { 
ProviderInfo[] infos = packageInfo.providers;
for (int i=0; i<infos.length;  i++) {
    ProviderInfo info = infos[i];
    msg += info.toString() + LF;
} // for

            } catch (Exception e) {
            if (D )e.printStackTrace();
} // try

return msg;
} // getProviders



/**
 * getPermissions
 */	
 //Array of all <permission> tags included under <manifest>, or null if there were none.
private String getPermissions( PackageInfo packageInfo) {
                          String msg = ""; 
try { 
PermissionInfo[] infos = packageInfo.permissions;
for (int i=0; i<infos.length;  i++) {
    PermissionInfo info = infos[i];
    msg += info.toString() + LF;
} // for

            } catch (Exception e) {
            if (D )e.printStackTrace();
} // try

return msg;
} // getPermissions


} // class PackageInfoUtility
