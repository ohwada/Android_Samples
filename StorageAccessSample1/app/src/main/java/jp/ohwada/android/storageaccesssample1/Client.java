/**
Storage Access Framework
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.storageaccesssample1;


import android.app.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * 
 */
 
 // https://developer.android.com/guide/topics/providers/document-provider.html?hl=ja
public class Client {
    
    	// debug
    	private final static boolean D = true;
    	private final static String TAG_SUB = "Client";
    	
     	private final static String LF = "\n";
     	
// ドキュメントを検索する   	
private static final int READ_REQUEST_CODE = 42;
    	
    		  private Activity mActivity;
    ContentResolver mContentResolver;

	
	 public Client( Activity activity ) {	 
	 mActivity = activity;
         mContentResolver = mActivity.getContentResolver();
	 } // Client
	
		
    	
    	// ドキュメント メタデータを確認する
  public String dumpImageMetaData( Uri uri ) {
            
            log_d( "dumpImageMetaData" );
            
            String data = "";
                        String msg = "";
                        
    // The query, since it only applies to a single document, will only return
    // one row. There's no need to filter, sort, or select fields, since we want
    // all fields for one document.
    Cursor cursor =  mContentResolver
            .query(uri, null, null, null, null, null);

    try {
    // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
    // "if there's anything to look at, look at it" conditionals.
        if (cursor != null && cursor.moveToFirst()) {

            // Note it's called "Display Name".  This is
            // provider-specific, and might not necessarily be the file name.
            String displayName = cursor.getString(
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                     msg = "Display Name: " + displayName;
log_d( msg );
data += msg + LF;

            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
              String size = null;
            if (!cursor.isNull(sizeIndex)) {
                // Technically the column stores an int, but cursor.getString()
                // will do the conversion automatically.
                size = cursor.getString(sizeIndex);
            } else {
                size = "Unknown";
            }// if cursor.isNull
            msg = "Size: " + size ;
            log_d( msg );
            data += msg + LF;
    } // i f (cursor != null
    
    } finally {
        cursor.close(  );
    } // try
    
    return data;
} // dumpImageMetaData
          
     
     
              	


/**
* ドキュメントを検索する
 * Fires an intent to spin up the "file chooser" UI and select an image.
 */
public void performFileSearch() {

log_d( "performFileSearch" );

    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
    // browser.
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

    // Filter to only show results that can be "opened", such as a
    // file (as opposed to a list of contacts or timezones)
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    // Filter to show only images, using the image MIME data type.
    // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
    // To search for all documents available via installed storage providers,
    // it would be "*/*".
    intent.setType("image/*");

    mActivity.startActivityForResult(intent, READ_REQUEST_CODE);
    
} // performFileSearch




// プロセスの結果
public Uri procActivityResult(int requestCode, int resultCode,
        Intent resultData) {

    log_d( "procActivityResult" );
            Uri uri = null;
            
    // The ACTION_OPEN_DOCUMENT intent was sent with the request code
    // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
    // response to some other intent, and the code below shouldn't run at all.

    if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        

        
        // The document selected by the user won't be returned in the intent.
        // Instead, a URI to that document will be contained in the return intent
        // provided to this method as a parameter.
        // Pull that URI using resultData.getData().

        if (resultData != null) {
            uri = resultData.getData();
                      log_d( "Uri: " + uri.toString() );
                      
//            showImage(uri);
// dumpImageMetaData(uri);
} //  if resultData

        } // if requestCode
        
        return uri;
    } // procActivityResult
    
  
  //ドキュメントを開く
  public Bitmap getBitmapFromUri(Uri uri)  {
  	     Bitmap image = null;
  	    try {
    ParcelFileDescriptor parcelFileDescriptor =
            mActivity.getContentResolver().openFileDescriptor(uri, "r");
    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
    image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
    parcelFileDescriptor.close();
				} catch (IOException e) {
			if (D) e.printStackTrace();
 } // try
   
    return image;
} // getBitmapFromUri
  
    
    

             
 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d

	
 } // class Client
