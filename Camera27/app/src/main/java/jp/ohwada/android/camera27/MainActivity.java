/**
 * Camera2 Sample
 *  using Camera2Base
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera27;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * MainActivity
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Camera2";
    	private final static String TAG_SUB = "MainActivity";


/**
 * onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button btnPreview = (Button) findViewById(R.id.Button_preview_activity);
         btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(PreviewActivity.class);
            }
        }); // btnPreview


        Button btnPicture = (Button) findViewById(R.id.Button_picture_activity);
         btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(PictureActivity.class);
            }
        }); // btnPicture

} // onCreate


/**
 * openActivity
 */
private void openActivity(Class<?> cls) {
                    Intent intent = new Intent(this, cls);
                    startActivity(intent);
} // openActivity


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} //  class MainActivity
