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
 * ExternalFile
 */
public class  ExternalFile extends PrivateFile {
		
	/**
     * === constractor ===
	 * @param Context context  
	 */	    
	 public ExternalFile( Context context  ) {
	 	super( context  );
	 	TAG_SUB = "ExternalFile";
	 	File dir  = Environment.getExternalStorageDirectory();
	 			setBaseDir( dir );
		
	} 


} // class ExternalFile