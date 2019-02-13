/**
 * Osmdroid Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid7;

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

    private static final String DIR_NAME = MapActivity.DIR_NAME;

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "MainActivity";

	// copy stream
	protected final static int EOF = -1;
	private final static int BUFFER_SIZE = 1024 * 4;

    private final static String TILE_FILE_NAME  = "japan.sqlite";


/**
 *  onCreate
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnDownloader = (Button) findViewById(R.id.Button_downloader);
         btnDownloader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startNewActivity( DownloaderActivity.class );
            }
        }); // btnDownloader

        Button btnMap = (Button) findViewById(R.id.Button_map);
		btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startNewActivity( MapActivity.class );
            }
        }); // btnMap


    } // onCreate


 	/**
	 * startMapActivity
	 */ 
private void startMapActivity() {
             startActivity(new Intent(this, MapActivity.class));
} // startMapActivity

 	/**
	 * startNewActivity
	 */ 
private void startNewActivity( Class cls) {
             startActivity(new Intent(this, cls));
} // startNewActivity

 	/**
	 * setupTile
	 */ 
private void setupTile() {

        File main_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() );
File sub_dir = new File(main_dir, DIR_NAME);
        if ( !sub_dir.exists() ) {
            sub_dir.mkdir();
        }
File file = new File(sub_dir, TILE_FILE_NAME );
    FileOutputStream fos  = getFileOutput( file ) ;
    InputStream is = getAssetInputStream( TILE_FILE_NAME );
    boolean ret = copyStream(  is,  fos );
    if (ret) {
        toast_long("Successful");
    } else {
        toast_long("Failed");
    }
} // setupTile


/**
 * getFileOutput
 */
private FileOutputStream getFileOutput( File file ) {

FileOutputStream fos = null;

        try{
fos = new FileOutputStream(file, true);
          } catch (Exception e) {
                        e.printStackTrace();
            }

return fos;
} // getFileOutput


/**
 * getAssetInputStream
 */
private InputStream getAssetInputStream( String fileName ) {
		
		InputStream is = null;
		try {
			is = getAssets().open( fileName );
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		
	return is;
} //getAssetInputStream


/**
 * copyStream
 */
private boolean copyStream( InputStream is, OutputStream os ) {
			
		byte[] buffer = new byte[BUFFER_SIZE];
		int n = 0;	
			boolean is_error = false;
			
		try {
			// copy input to output		
			while (EOF != (n = is.read(buffer))) {
				os.write(buffer, 0, n);
			} // while
		} catch (IOException e) {
			is_error = true;
			if (D) e.printStackTrace();
	}

	return ! is_error;
}	// copyStream


/**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

} // class MainActivity
