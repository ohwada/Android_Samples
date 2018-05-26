/**
 *  SVG Sample
 *  2018-05-01 K.OHWADA
 */

package jp.ohwada.android.svgsample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;

import java.io.InputStream;

/**
 *  class MainActivity
 */
public class MainActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "SVG";
    	private final static String TAG_SUB = "MainActivity";


 private final static String FILE_NAME = "droid.svg";

    private ImageView mImageViewSvg;

/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    mImageViewSvg = (ImageView) findViewById(R.id.ImageView_svg);

        Button btnSvg = (Button) findViewById(R.id.Button_svg);
       btnSvg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSvg();
            }
        }); // btnSvg

    } // onCreate


/**
 *  showSvg
 */
private void showSvg() {
    log_d("showSvg");
        Bitmap bitmap = null;
    try {
        bitmap = resdSvg();
	} catch (Exception e) {
			e.printStackTrace();
	} 

    if (bitmap== null) {
        toast_short( "cannot read" );
        return;
    }

    mImageViewSvg.setImageBitmap(bitmap);
} // showSvg


/**
 *  resdSvg
 */
private Bitmap resdSvg() throws Exception {
    log_d("resdSvg");
// Read an SVG from the assets folder
SVG  svg = SVG.getFromAsset( getAssets(), FILE_NAME );
if (svg == null) {
     log_d("svg null");   
    return null;
}
    log_d("svg: " + svg.toString());

// Create a canvas to draw onto
    float width = svg.getDocumentWidth();
    float height = svg.getDocumentHeight();

// TODO : width=-1.0 , height=-1.0
    String msg = "width=" + Float.toString(width);
    msg += " , height=" + Float.toString( height);
    log_d(msg);
if ((width == -1)||( height == -1)) {
     log_d("cannot get svg size");   
    return null;
}

    int w = (int) Math.ceil(width);
    int h = (int) Math.ceil(height);
      Bitmap  bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

   Canvas  canvas = new Canvas(bitmap);

   // Clear background to white
   canvas.drawRGB(255, 255, 255);

   // Render our document onto our canvas
   svg.renderToCanvas(canvas);

    return bitmap;
} // resdSvg




/**
 * toast_short
 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short


 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} //  class MainActivity
