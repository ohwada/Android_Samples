/**
 * File Provider Sample
 * BitmapUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.fileprovider1;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * class BitmapUtil
 */
public class BitmapUtil {


/**
 * getBitmap
 */	
public static Bitmap getBitmap(File file ) {

		if(file == null) return null;
		return getBitmap( file.toString() );

} // getBitmap


/**
 * getBitmap
 */	
private static Bitmap getBitmap( String path ) {

		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile( path );
		} catch (Exception e) {
			// OutOfMemoryError
			e.printStackTrace();
		}
		return bitmap;

} // getBitmap


/**
 * getScaledBitmap
 */	
public static Bitmap getScaledBitmap( File file, int maxWidth, int maxHeight ) {

    FileInputStream fis = null;
    try {
        fis = new FileInputStream(file);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    if (fis == null) {
        return null;
    }

	// get image size
	BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = true;
	BitmapFactory.decodeStream(fis, null, options);
    
	// close fis
	try {
        if( fis != null ) fis.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

	int scale = calcScale(options.outWidth, options.outHeight, maxWidth, maxHeight);
	String path = file.toString();
	if(scale > 1) {
		return getScaledBitmap( path, scale );
	}
	return getBitmap( path );

} // getScaledBitmap


/**
 * getScaledBitmap
 */	
private static Bitmap getScaledBitmap( String path, int scale ) {

	BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = false;
	options.inSampleSize =  scale;
	Bitmap bitmap = BitmapFactory.decodeFile(path, options);
	return bitmap;

} // getScaledBitmap


/** 
 * calcScale
 */
public static int calcScale(int imageWidth, int imageHeight, int maxWidth, int maxHeight) {

    float scaleX = imageWidth / maxWidth;
    float scaleY = imageHeight / maxHeight;
    int scale = (int) Math.floor(Float.valueOf(Math.max(scaleX, scaleY)).doubleValue());

    if (scale > 0 && (scale & (scale - 1)) == 0) {	
    // nop if Power of 2
    } else {	
		// Round to power of 2
		scale = (int) Math.pow(2.0,
			(Math.floor(Math.log(scale - 1) / Math.log(2.0))));
    }
    return scale;

} // calcScale


} // class BitmapUtil