/**
 * BitmapRegionDecoder Sample
 * 2020-01-01 K.OHWADA
 */
package jp.ohwada.android.bitmapregiondecoder1;



import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * class MainActivity
 */
public class MainActivity extends Activity {


    private final static String IMAGE_FILE_NAME = "sample.png";
    
    private ImageView mImageView;

    private Bitmap mBitmap;


/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView  = (ImageView) findViewById(R.id.imageView);
    }


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        mBitmap = readImage(this, IMAGE_FILE_NAME);
        mImageView.setImageBitmap(mBitmap);
    }


/**
  * onTouchEvent
 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Rect rect = getCenterRect(mBitmap);
        Bitmap bitmap = cropImage(this, IMAGE_FILE_NAME, rect);
        mImageView.setImageBitmap(bitmap);

        return true;
}


/**
 * readImage
 */
private Bitmap readImage(Context context, String fileName) {

    AssetManager assetManager = context.getAssets();

    Bitmap bitmap = null;
    try {
            InputStream is = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
            if(is != null) is.close();
    } catch (IOException ex) {
            ex.printStackTrace();
    }

    return bitmap;
}


/**
  * cropImage
 */
private Bitmap cropImage(Context context, String fileName, Rect rect) {

    AssetManager assetManager = context.getAssets();

    BitmapFactory.Options options = new BitmapFactory.Options();

    // read image and allocate the memory for its pixels
    options.inJustDecodeBounds = false;

    Bitmap bitmap = null;
    try {
        InputStream is = assetManager.open(fileName);
         BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(is, true);
        bitmap = regionDecoder.decodeRegion(rect, options);
        if(is != null) is.close();
    } catch (IOException e) {
        e.printStackTrace();
    }

      return bitmap;
}


/**
  * getCenterRect
 */
private Rect getCenterRect(Bitmap bitmap) {

            // bitmap size
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // center
            int cx = w/2;
            int cy = h/2;

            // rect
            int rw = w/4;
            int rh = h/4;
            int left = cx - rw/2;
            int top = cy - rh/2;
            int right = left + rw;
            int bottom = top + rh;
            Rect rect = new Rect(left, top, right, bottom);
            return rect;
}


} // class MainActivity

