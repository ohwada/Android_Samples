 /**
 * ImageDialog Sample
 * 2019-02-01 K.OHWADA 
 */
 
package jp.ohwada.android.imagedialog1;

 import android.app.AlertDialog;
 import android.content.Context;
 import android.graphics.Bitmap;
 import android.graphics.BitmapFactory;
 import android.graphics.Point;
 import android.util.DisplayMetrics;
 import android.view.Display;
 import android.view.WindowManager;
 import android.widget.ImageView;
import android.util.Log;

 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;

 public class ImageDialog {

	// dubug
	private final static boolean D = true; 
	private final static String TAG = "ImageDialog";
	private final static String TAG_SUB = "ImageDialog";

 private static final int MAX_WIDTH = 360;

 private static final int MAX_HEIGHT = 240;

private Context mContext;


/** 
 * constractor
 */
public  ImageDialog(Context context) {
    mContext = context;
} // ImageDialog


/** 
 * showImageDialog
 */
public void showImage(File file) {

    String title = file.getName();

    Bitmap bitmap  = getScaledBitmap(file);
    ImageView imageView = new ImageView(mContext);
    imageView.setImageBitmap( bitmap );

    new AlertDialog.Builder(mContext)
         .setTitle( title )
        .setView(  imageView )
        .setPositiveButton( R.string.button_ok, null ) 
        .show();
} // showImageDialog


/**
 * getScaledBitmap
 */	
public Bitmap getScaledBitmap( File file ) {

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
    try {
        if( fis != null ) fis.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

	int scale = calcScale( options.outWidth, options.outHeight );
	String path = file.toString();

 	BitmapFactory.Options options_2 = new BitmapFactory.Options();
	options_2.inJustDecodeBounds = false;
	options_2.inSampleSize =  scale;
	Bitmap bitmap = BitmapFactory.decodeFile(path, options_2);
	return bitmap;

} // getScaledBitmap


/** 
 * calcScale
 */
private int calcScale(int width, int height) {

    float scaleX = width / MAX_WIDTH;
    float scaleY = height / MAX_HEIGHT;

    int scale = (int) Math.floor(Float.valueOf(Math.max(scaleX, scaleY)).doubleValue());

    if (scale > 0 && (scale & (scale - 1)) == 0) {	
    // nop if Power of 2
    } else {	
		// Round to power of 2
		scale = (int) Math.pow(2.0,
			(Math.floor(Math.log(scale - 1) / Math.log(2.0))));
    }
    return scale;

} // calcScall


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class ImageUtil





