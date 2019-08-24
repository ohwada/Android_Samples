/**
 * Camera2 Sample
 * MediaScanner
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.camera218.util;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;


/**
 * class MediaScanner
 */ 
public class MediaScanner {

        // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MediaScanner";


/**
 * scanFile
 * egister into Content Provider
 */ 
public static void scanFile(Context context, File file) {
        String[] paths = new String[]{ file.getPath() };
        MediaScannerConnection.scanFile(context, paths,
                /*mimeTypes*/null, mMediaScannerConnectionClient );
} // scanFile


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * MediaScannerConnection.MediaScannerConnectionClient
 */ 
private static MediaScannerConnection.MediaScannerConnectionClient mMediaScannerConnectionClient =
new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        // Do nothing
                    }
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        String msg = "Scanned " + path + " -> uri=" + uri;
                        log_d(msg);
                    }
}; // MediaScannerConnectionClient


} // class MediaScanner

