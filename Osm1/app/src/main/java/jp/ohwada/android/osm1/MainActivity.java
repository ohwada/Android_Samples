/**
 * Osm Sample
 * Beef Bowl Map
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osm1;

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

    private NodeUtil mNodeUtil;

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
                setupDb() ;
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
                startNewActivity( NodeListActivity.class );
            }
        }); // btnList


    mNodeUtil = new NodeUtil(this);

    } // onCreate


 	/**
	 *  etupDb
	 */ 
private void setupDb() {
    long count = mNodeUtil.setupNode();
    if (count == 0) {
        toast_long("already exist");
    } else if (count == -1){
        toast_long("can not parse Json");
    } else {
        toast_long( "inserted recors: " +  count );
    }
} //  etupDb


 	/**
	 * startNewActivity
	 */ 
private void startNewActivity( Class cls) {
             startActivity(new Intent(this, cls));
} // startNewActivity


/**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

} // class MainActivity
