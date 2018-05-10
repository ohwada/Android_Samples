/**
 * Share Action Provider Sample
 * with_v7_ShareActionProvider
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.shareactionprovidersample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

 
/**
 * class MainActivity
 * original : https://github.com/googlesamples/android-ActionBarCompat-ShareActionProvider
 */
public class MainActivity extends AppCompatActivity {

	// dubug
    public final static boolean D = true; 
	public final static String TAG = "Share";
	private String TAG_SUB = "MainActivity";

    // share intent for text
 	private final static String SUBJECT = "Share Test"; 
    	private final static String TEXT = "This is Share Test Text";
    	private final static String TEXT_TYPE = "text/plain";              
    	private final static String[] EMAIL_TO = {"user@example.com"}; 

    // share intent for image
    	private final static String IMAGE_TYPE = "image/jpg";   
    	private final static String ASSET_FILE_NAME = "photo_01.jpg";

        private ImageView mImageView1;

    // Keep reference to the ShareActionProvider from the menu
    private ShareActionProvider mShareActionProvider;


/**
 * == onCreate ==
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     mImageView1 = (ImageView) findViewById(R.id.ImageView_1);
    showImage();
    } // onCreate


/**
 * == onCreateOptionsMenu ==
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Retrieve the share menu item
        MenuItem shareItem = menu.findItem(R.id.action_share);

      // Now get the ShareActionProvider from the item
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        // getShareIntent
        // Intent intent = getTextShareIntent();
        Intent intent = getImageShareIntent();
        // setShareIntent
        mShareActionProvider.setShareIntent( intent );

        return super.onCreateOptionsMenu(menu);
    } // onCreateOptionsMenu


/**
 * showImage
 */
private void showImage() {
    Uri uri = getContentUri(ASSET_FILE_NAME);
    mImageView1.setImageURI(uri);
} // showImage


/**
 * getTextShareIntent
 */
    private Intent getTextShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType(TEXT_TYPE);
                intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT );
                intent.putExtra(Intent.EXTRA_TEXT, TEXT);
                intent.putExtra(Intent.EXTRA_EMAIL, EMAIL_TO );
            return intent;
    } // getTextShareIntent

/**
 * getImageShareIntent
 */
    private Intent getImageShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
                 intent.setType(IMAGE_TYPE);
                intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT );
                intent.putExtra(Intent.EXTRA_TEXT, TEXT);
                intent.putExtra(Intent.EXTRA_EMAIL, EMAIL_TO );

                // TODO : can not send images to gmail app
                intent.putExtra(Intent.EXTRA_STREAM, getContentUri(ASSET_FILE_NAME));

            return intent;
} // getImageShareIntent


    /**
     * getContentUri
     * @return Uri to the content
     */
    private Uri getContentUri(String assetFileName) {
        return AssetProvider.getContentUri(assetFileName);
    } // getContentUri


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class MainActivity

