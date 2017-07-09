/**
 * permission sample
 * 2017-06-01 K.OHWADA 
 */
 
package jp.ohwada.android.permissionsample1;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * FileUtility
 */
public class FileUtility {
							
// indexof
 private final static int INDEX_NOT_FOUND = -1;
 private final static int INDEX_OFFSET = 1;
 		
	/**
     * === constractor === 
	 */	    
	 public FileUtility() {
		// dummy
	} 


	/**
	 * parseExt
	 * @param String fileName
	 	 * @param String ext
	 	 * @return boolean
	 */
public static boolean matchExt( String fileName, String ext ) {
	
	String name_ext = parseExt( fileName );

if ( ( ext != null )&&( ext.equals( name_ext )) ) {
		return true;
}		
		return false;		
} // matchExt


	/**
	 * parseExt
	 * @param String fileName
	 	 * @return String
	 */
public static String parseExt( String fileName ) {
	String ext = "";


        if (fileName == null) return ext;

	// find point index
    int point = fileName.lastIndexOf(".");
    if ( point != INDEX_NOT_FOUND ) {
        ext = fileName.substring( point + INDEX_OFFSET );
    } // if point

    return ext;
} // parseExt



} // class FileUtility