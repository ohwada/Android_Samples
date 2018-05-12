/**
 * Mail Share Provider to Mail  Sample
 * send Share Intent to Mail  app
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.mailshareprovidersample;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * class MainActivity
 */
public class MainActivity extends AppCompatActivity {

// debug
    public final static boolean D = true; 
	public final static String TAG = "Share";
	private String TAG_SUB = "MainActivity";

	public final static String LF = "\n";

    // share intent 
  	private final static String TYPE_TEXT = "text/plain";  

    	private final static String TYPE_RFC822 = "message/rfc822";

    private final static String SCHEME_MAILTO = "mailto:";

 	private final static String SUBJECT = "Share Test"; 
    	private final static String TEXT = "This is Share Test Text";
  
      	private final static String TO_ADDR = "user@example.com";  
    	private final static String[] EMAIL_TO = { TO_ADDR };

    	private final static String ASSET_FILE_NAME = "cozmel01.jpg";

    	private final static String CHOOSER_TITLE = "Send email...";


	/* SharedPreferences */
    private static final String PREF_IMAGE_URI = "image_uri";
    private static final String DEFAULT_IMAGE_URI = "";


        private TextView mTextView1;
        private ImageView mImageView1;

    private SharedPreferences mPreferences;

    private AssetFile mAssetFile;

    private Bitmap mBitmap;

    private   String mImageUri; 

    	private Permission mPermission;


/**
 * == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     mTextView1 = (TextView) findViewById(R.id.TextView_1);
     mImageView1 = (ImageView) findViewById(R.id.ImageView_1);

        Button btnSave = (Button) findViewById(R.id.Button_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procClickSave();
            }
        }); // btnSave

        Button btnText = (Button) findViewById(R.id.Button_text);
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 sendTextMailShare();
            }
        }); // btnText

        Button btnImage = (Button) findViewById(R.id.Button_image);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 sendImageMailShare();
            }
        }); // btnImage


	mPreferences = PreferenceManager.getDefaultSharedPreferences( this );
    mImageUri = mPreferences.getString( PREF_IMAGE_URI,  DEFAULT_IMAGE_URI );

        mPermission = new Permission( this );
        mPermission.setPermWriteExternalStorage();

    mAssetFile = new AssetFile(this);

    showImage();
    } // onCreate


/**
 * == onCreateOptionsMenu ==
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    } // onCreateOptionsMenu


    /**
     * === onOptionsItemSelected ===
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            toast_short("Settings");
        } else if (id == R.id.action_share) {
           sendShare();
        } 
        return true;
    } // onOptionsItemSelected


    /**
     * === onRequestPermissionsResult ===
     */
    @Override
    public void onRequestPermissionsResult( int request, String[] permissions, int[] results ) {
    	
    	boolean ret = mPermission.procRequestPermissionsResult( request, permissions, results );
    	
        if ( ret ) {
        	 saveImage();
        }

        super.onRequestPermissionsResult(request, permissions, results);
        
    } // onRequestPermissionsResult


/**
 * showImage
 * read image fron asset
 */
private void showImage() {
    mBitmap = mAssetFile.readImageFile(ASSET_FILE_NAME);
    mImageView1.setImageBitmap(mBitmap);
} // showImage


/**
 * procClickSave
 */
private void procClickSave() {

    if ( mPermission.hasPerm() )  {
		saveImage();
	} else {
		showPermissionDialog();
	}

} // procClickSave


/**
 * saveImage
 */
private void saveImage() {

// GMAIL need image file saved on sd card

    if (!TextUtils.isEmpty(mImageUri)) {
        toast_short("Alreadry Saved");
        return;
    }

    String str_uri  =  null;
    try {
        str_uri  = MediaStore.Images.Media.insertImage( getContentResolver(), mBitmap, ASSET_FILE_NAME, null );
        } catch(Exception e) {
            e.printStackTrace();
            toast_short("save failed");
    }

    if (!TextUtils.isEmpty(str_uri)) {
        Uri uri = Uri.parse(str_uri);
        String path = queryFilePath(uri);
        toast_short( "saved \n" + str_uri  );
        mImageUri = str_uri;
        setPrefImageUri(str_uri);

      String msg = "uri: " + str_uri + LF;
        msg += "path: " + path + LF;
    log_d(msg);
    }

} // saveImage


/**
 * queryFilePath
 */
private String queryFilePath(Uri uri) {

    ContentResolver contentResolver = getContentResolver();

    String[] columns = { MediaStore.Images.Media.DATA };
    Cursor cursor = contentResolver.query(uri, columns, null, null, null);

    cursor.moveToFirst();
    String path = cursor.getString(0);
    cursor.close();
    return path;
} // queryFilePath


/**
 * sendShare
 */
private void sendShare() {
    toast_short("Share");

    Intent intent = createTextShareIntent();
    startActivity( intent );
} // psendShare

/**
 * sendTextMailShare
 */
private void sendTextMailShare() {
    toast_short("TextMail");

    // TODO : open only GMAIL

   Intent intent = createTextMailShareIntent();


// Exception No Activity occurs
 // in device where there is no mail application such as emulator
    try {

        // TODO: displayChooser title , but not display the application list
        startActivity(Intent.createChooser(intent, CHOOSER_TITLE));

        // TODO: Exception No Activity occurs
        // startActivity( intent);

    } catch(Exception e) {
            e.printStackTrace();
            toast_short("TextMail failed");
    }

} // sendTextMailShare


/**
 * sendImageMailShare
 */
private void sendImageMailShare() {
    toast_short("ImageMail");
    if (TextUtils.isEmpty(mImageUri)) {
        toast_short("please save");
        return;
    }

    Intent intent = createImageMailShareIntent();

    try {
        startActivity( intent );
    } catch(Exception e) {
            e.printStackTrace();
            toast_short("ImageMaill failed");
    }

} // sendImageMailShare


/**
 * createTextShareIntent
 */
    private Intent createTextShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType(TYPE_TEXT);
                intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT );
                intent.putExtra(Intent.EXTRA_TEXT, TEXT);   
            return intent;
    } // createTextShareIntent


/**
 * createTextMailShareIntent
 */
private Intent createTextMailShareIntent() {


// TODO : open only GMAIL
// https://stackoverflow.com/questions/8701634/send-email-intent
   Uri uri = Uri.fromParts( SCHEME_MAILTO, TO_ADDR, null );
   Intent intent = new Intent(Intent.ACTION_SENDTO,  uri );
    intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
    intent.putExtra(Intent.EXTRA_TEXT, TEXT);

        return intent;
} // createTextMailShareIntent


/**
 * createImageMailShareIntent
 */
    private Intent createImageMailShareIntent() {

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType(TYPE_RFC822);
        intent.putExtra(Intent.EXTRA_EMAIL, EMAIL_TO );
         intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT );
        intent.putExtra(Intent.EXTRA_TEXT, TEXT);

                if (!TextUtils.isEmpty(mImageUri)) {
                    Uri uri_stream = Uri.parse(mImageUri);
                    if (uri_stream != null) {
                        intent.putExtra(Intent.EXTRA_STREAM, uri_stream);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

            return intent;
} // createImageMailShareIntent



    /**
     * showPermissionDialog
     */
    private void showPermissionDialog() {
        final YesNoDialog dialog = new YesNoDialog( this );
        dialog.setTitle( R.string.dialog_permission_title );
        dialog.setMessage( R.string.dialog_permission_storage );
                    
        dialog.setOnChangedListener(
            new YesNoDialog.OnChangedListener() {
            	
            public void onClickYes() {
                dialog.dismiss();
                mPermission.requestPerm();
            }
            
            public void onClickNo() {
                dialog.dismiss();
            }
            
          } );
          
          dialog.show();
        } // showPermissionDialog  


	/**
	 * setPrefImageUri
	 */
    private void setPrefImageUri( String uri ) {
		mPreferences.edit().putString( PREF_IMAGE_URI, uri ).commit();
	} // setPrefImageUri


/**
 * toast_short
 */
	private void toast_short( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_SHORT ).show();
	} // toast_short


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class MainActivity

