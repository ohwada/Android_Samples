/**
 * Osmdroid Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid8;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *  class MainActivity
 */
public class MainActivity extends Activity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "MainActivity";

/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMap = (Button) findViewById(R.id.Button_map);
		btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startNewActivity( MapActivity.class );
            }
        }); // btnMap

        Button btnCustom = (Button) findViewById(R.id.Button_custom);
		btnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startNewActivity( CustomMapActivity.class );
            }
        }); // btnCustom

    } // onCreate

 	/**
	 * startNewActivity
	 */ 
private void startNewActivity( Class cls) {
             startActivity(new Intent(this, cls));
} // startNewActivity


} // class MainActivity
