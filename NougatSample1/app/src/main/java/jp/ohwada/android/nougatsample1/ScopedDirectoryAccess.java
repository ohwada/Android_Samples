/**
 * Android 7.0 Nougat
 * ScopedDirectoryAccess
 * 2018-02-01 K.OHWADA 
 */
 
package jp.ohwada.android.nougatsample1;

import android.app.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * class ScopedDirectoryAccess
 */
public class ScopedDirectoryAccess {
    
    	// debug
	    private  final static boolean D = Constant.DEBUG; 
    	private final static String TAG_SUB = "ScopedDirectoryAccess";

    // png
    private final static String EXT_PNG = 	"png";
    private final static String MIME_TYPE_PNG = 	"image/png";

    private Context mContext;
    private StorageManager mStorageManager;
    private ContentResolver mContentResolver;
    private AssetFile  mAssetFile;
	
	private int mNum = 0;
	
	/**
 	 * === constractor === 
 	 */
public ScopedDirectoryAccess(Context context) {
    mContext = context;
    mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
    mContentResolver = context.getContentResolver();
    mAssetFile = new AssetFile( context  );
} // ScopedDirectoryAccess


	/**
 	 * getScopedDir
     * @param Intent data
     * @return DocumentFile
 	 */
    public DocumentFile getScopedDir( Intent data ) {

        log_d( "getScopedDir" );
        Uri uri = data.getData();
        mContentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        DocumentFile dir = DocumentFile.fromTreeUri( mContext, uri );
        return dir;
    } // getScopedDir


	/**
 	 * opyImageFiles
     * @param Intent data
     * @return boolean
 	 */
    public boolean copyImageFiles(  Intent data  ) {
        return copyImageFiles( data, EXT_PNG, MIME_TYPE_PNG );
    } // copyImageFiles


	/**
 	 * copyImageFiles
     * @param Intent data
     * @param String ext
     * @param String mime_type
     * @return boolean
 	 */
    public boolean copyImageFiles(  Intent data, String ext, String mime_type  ) {

        log_d( "copyImageFiles" );
		List<String> list = mAssetFile.getAssetList( ext );
		
		// no result
		if  ( (list == null) ||( list.size() == 0 ) ) {
			return false;
		}

       boolean  is_error = false;

        for (String name: list ) {
            boolean ret = copyImageFile(  data, name, mime_type );
            if (!ret) {
                is_error = true;
            }
        } // for

        if( ! is_error ) {
            String msg = "copied";
            toast_short( msg );
            log_d( msg );
        }

        return ! is_error;

} // copyImageFiles



	/**
 	 * copyImageFile
     * @param Intent data
     * @param String  file_name
     * @param String mime_type
     * @return boolean
 	 */
    public boolean copyImageFile( Intent data, String file_name, String mime_type ) {

        log_d( "copyImageFile: " +   file_name );
        InputStream is = mAssetFile.getAssetInputStream(  file_name );
        if ( is == null ) {
            return false;
        }

        DocumentFile dir =  getScopedDir( data );
                DocumentFile newFile = dir.createFile(  mime_type, file_name );

                 boolean  is_error = false;

                try {
                    OutputStream os = mContentResolver.openOutputStream(newFile.getUri());
                    boolean ret = mAssetFile.copyStream( is, os );
                    if (!ret) {
                        is_error = true;
                    }
                    if (is != null ) is.close();
                     if (os != null ) os.close();
                } catch (IOException e) {
                    if (D) e.printStackTrace();
                    is_error = true;
                } // try


        return ! is_error;

    } // copyImageFile



	/**
 	 * getBitmap
     * @param Intent data
     * @return Bitmap
 	 */
    public Bitmap getBitmap( Intent data ) {

            log_d("getBitmap");

Bitmap bitmap =null;
DocumentFile dir =  getScopedDir( data );
        List<DocumentFile> list = getListDocumentFile( dir, MIME_TYPE_PNG );

        String msg_nothing = "Nothing in the directory " + dir.getName();
        if ( list == null ) {
 toast_short( msg_nothing );
log_d(  msg_nothing );
            return bitmap;
        }

        int size = list.size();
        if ( size == 0 ) {
 toast_short(  msg_nothing );
log_d(  msg_nothing );
            return bitmap;
        }

DocumentFile file = null;
    if (( mNum >= 0 )&&( mNum < size )) {
        file = list.get(mNum);
    }

        mNum ++;
        if (mNum >= size ) {
            mNum = 0;
        }

        if ( file != null ) {
            bitmap =  getBitmap(file);
        }

        return bitmap;
    } // getBitmap


	/**
 	 * getBitmap
     * @param DocumentFile file
     * @return Bitmap
 	 */
    public Bitmap getBitmap( DocumentFile file) {

        String msg = "getBitmap: " + file.getName();
            log_d(msg);

        Bitmap bitmap = null;
        try {
            InputStream is = mContentResolver.openInputStream(file.getUri());
            bitmap = ImageFile.decodeStream( is );
        } catch (IOException e) {
                    if (D) e.printStackTrace();
        }// try
        return bitmap;

    } //getBitmap


	/**
 	 * getListDocumentFile
     * @param DocumentFile dir
     * @param String mime_type
     * @return List<DocumentFile>
 	 */
    public List<DocumentFile> getListDocumentFile( DocumentFile dir, String mime_type ) {

        String msg = "getListDocumentFile: dir = " + dir.getName() + " mime_type = " + mime_type;
            log_d(msg);

        List<DocumentFile> list = new ArrayList<DocumentFile>();
        DocumentFile[] files = dir.listFiles();

        if (( files == null )||( files.length == 0 )) {
            return list;
        }

        for ( DocumentFile file: files ) {
            msg = "list: " + file.getName();
            log_d(msg);
            if ( file.canRead() && mime_type.equals( file.getType() ) ) {
                list.add(file);
            }
        } // for

        return list;

    } // getListDocumentFile


      	/**
	 *  getInternalStorageAccessIntent
     * @ param String directoryName
     * @return Intent
	 */	
    public Intent getInternalStorageAccessIntent(String directoryName){

        String msg = "getInternalStorageAccessIntent: " + directoryName;
        log_d(msg);

        // Internal shared storage
        StorageVolume volume = mStorageManager.getPrimaryStorageVolume();
        log_d( "volume: "+ volume.toString() );

        Intent intent = volume.createAccessIntent(directoryName);
	    log_d( "intent: " + intent.toString() );

        return intent;
    } // getInternalStorageAccessIntent


	/**
	 * toast short
	 */ 
	private void toast_short( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (Constant.DEBUG) Log.d( Constant.TAG, TAG_SUB + " " + msg );
	} // log_d
	
 } // class ScopedDirectoryAccess
