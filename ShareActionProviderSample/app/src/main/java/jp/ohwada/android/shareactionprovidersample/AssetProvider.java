/**
 * Share Action Provider Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.shareactionprovidersample;



import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * class AssetProvider
 * A simple ContentProvider which can serve files from this application's assets. The majority of
 * functionality is in {@link #openAssetFile(android.net.Uri, String)}.
 *  original : https://github.com/googlesamples/android-ActionBarCompat-ShareActionProvider
 */
public class AssetProvider extends ContentProvider {

	// dubug
    public final static boolean D = true; 
	public final static String TAG = "Share";
	private  static final String TAG_SUB = "AssetProvider";


    public static final String SCHEME = "content://";
    public  static final String CONTENT_URI = "jp.ohwada.android.shareactionprovidersample";


/**
 *  constractor
 */
public AssetProvider() {
    super();
} // AssetProvider


/**
 *  == onCreate ==
 */
    @Override
    public boolean onCreate() {
        log_d("onCreate");
        return true;
    } // onCreate


/**
 *  == delete ==
 *  Do not support delete requests.
 */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        log_d("delete");
        return 0;
    } // delete


/**
 *  == getType ==
 *  Do not support returning the data type
 */
    @Override
    public String getType(Uri uri) {
        log_d("getType");
        return null;
    } // getType


/**
 *  == insert ==
 *  Do not support insert requests.
 */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        log_d("insert");
        return null;
    } // insert


/**
 *  == query ==
 *  Do not support query requests.
 */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        log_d("query");
        return null;
    } // query


/**
 *  == update ==
 *   Do not support update requests.
 */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        log_d("update");
        return 0;
    } // update


/**
 *  ==  openAssetFile ==
 *  The majority of functionality
 */
    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        log_d("openAssetFile");
        // The asset file name should be the last path segment
        final String assetName = uri.getLastPathSegment();
        log_d("assetName:" + assetName);

        // If the given asset name is empty, throw an exception
        if (TextUtils.isEmpty(assetName)) {
            throw new FileNotFoundException();
        }

        try {
            // Try and return a file descriptor for the given asset name
            AssetManager am = getContext().getAssets();
            return am.openFd(assetName);
        } catch (IOException e) {
            e.printStackTrace();
            return super.openAssetFile(uri, mode);
        }
    } //  openAssetFile


 /**
     * getContentUri
     * @return Uri to the content
     */
    public static Uri getContentUri(String assetFileName) {
        log_d("getContentUri");
        if (!TextUtils.isEmpty(assetFileName)) {
            // If this content has an asset, then return a AssetProvider Uri
            String str_uri = SCHEME + CONTENT_URI + "/" + assetFileName;
            return Uri.parse(str_uri);
        } else {
            return null;
        }
    } // getContentUri


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class AssetProvider
