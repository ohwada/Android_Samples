/**
 * Osmdroid Sample
 * Bookmark
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid9;

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

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *  class MainActivity
 */
public class MainActivity extends AppCompatActivity {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "MainActivity";

    private static final String DIR_NAME = BookmarkUtil.DIR_NAME;

    private final static String FILE_NAME  = "tokyo_bay_kanagawa.csv";


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSetup = (Button) findViewById(R.id.Button_setup);
         btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupFile();
            }
        }); // btnSetup

        Button btnMap = (Button) findViewById(R.id.Button_map);
		btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewActivity( MapActivity.class );
            }
        }); // btnMap

        Button btnList = (Button) findViewById(R.id.Button_list);
		btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewActivity( BookmarkListActivity.class );
            }
        }); // btnList

    } // onCreate

 	/**
	 * startNewActivity
	 */ 
private void startNewActivity( Class cls) {
             startActivity(new Intent(this, cls));
} // startNewActivity


 	/**
	 * setupFile
	 */ 
private void setupFile() {

    FileUtil util = new FileUtil(this);
    boolean ret = util.copyAssetToStrage( DIR_NAME,   FILE_NAME );
    if (ret) {
        toast_long("setup Successful");
    } else {
        toast_long("setup Failed");
    }
} // setupFile











/**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

} // class MainActivity
