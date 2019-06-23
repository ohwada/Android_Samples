/**
 * File Provider Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.fileprovider1;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** 
 *  class MainActivity
 *  reference : https://www.petitmonte.com/java/android_fileprovider.html
 */
public class MainActivity extends Activity {
 
    // debug
	private final static boolean D = true;
    private final static String TAG = "FileProvider";
    private final static String TAG_SUB = "MainActivity";


/**
 * Flag whether to use EXTRA_OUTPUT
 * for debug
 */ 
	private final static boolean USE_CAMERA_EXTRA_OUTPUT = true;


/**
 * File extension of File in Assets folder
 */ 
    private final static String FILE_EXT = ".jpg";


/**
 * Request Code
 */ 
    private static final int REQUEST_CODE_CAMERA_PERMISSIONS = 101;
    private static final int  REQUEST_CODE_CAMERA_IMAGE = 102;


/**
 * Constant for authority of FileProvider
 * refer : android:authorities
 */ 
    private static final String AUTHORITY_PROVIDER = ".provider";


/**
 * MIME type for JPEG 
 */ 
	private final static String MIME_TYPE = "image/jpeg";



/**
 * Maximum of image size 
 */
    private static final int MAX_WIDTH = 360;
    private static final int MAX_HEIGHT = 240;

/**
 * ListView
 */ 
    private ListView mListView ;


/**
 * ListAdapter for ListView
 */ 
   	private ListAdapter mAdapter;


/**
 * List for ListAdapter
 */ 
	private List<File> mList;


/**
 * utility for File
 */ 
      private FileUtil mFileUtil;


/**
 * utility for FileProvider
 */ 
      private FileProviderUtil mFileProviderUtil;


/**
 * Permission for Camera
 */ 
    private Permission mCameraPerm;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
  
        Button btnCopy = (Button)findViewById(R.id.Button_copy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFiles();
            }
        }); // btnCopy


        Button btnCamera = (Button)findViewById(R.id.Button_camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        }); // btnCamera
 
        mList = new ArrayList<File>();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );

        mListView = (ListView)findViewById(R.id.list);
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener

        mFileUtil = new FileUtil(this);
        mFileProviderUtil = new FileProviderUtil(this);
        mCameraPerm = new Permission(this);
        mCameraPerm.setRequestCode(REQUEST_CODE_CAMERA_PERMISSIONS);
        mCameraPerm.setPermission( Manifest.permission.CAMERA);

} // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        showList();
}



/** 
 *  onRequestPermissionsResult
 */
@Override
public void onRequestPermissionsResult(
            int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mCameraPerm.isGrantRequestPermissionsResult(requestCode, grantResults)) {
                    // start Camera, when granted
                    startCamera();
        }
} // onRequestPermissionsResul


/** 
 *  onActivityResult
 */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        log_d("onActivityResult: " + requestCode + " , " + data);
        if (requestCode ==  REQUEST_CODE_CAMERA_IMAGE && resultCode == RESULT_OK) {
            // show list , if RESULT_OK
            showList();
            showCameraResultDialog(data);
            showToast("camera successful");
        }
} // onActivityResult



/** 
 * showCameraResultDialog
 * show BItmap in Dialog, if Camera returns
 */
private void showCameraResultDialog(Intent data) {

    if (data == null) return;
    Bundle bundle = data.getExtras();
    if (bundle == null) return;

    Bitmap bitmap = (Bitmap)bundle.get("data");
    String title = "";
    if(bitmap != null) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        title = w + " x " + h;
    }
    ImageView imageView = new ImageView(this);
    imageView.setImageBitmap( bitmap );

    new AlertDialog.Builder(this)
            .setTitle(  title )
            .setView(  imageView )
            .show();

} // showImageDialog



/** 
 *   copyFiles from Asset folder to ExternalStoragePublicPictures
 */
private void copyFiles() {
    boolean ret = mFileUtil.copyFilesAssetsToExternalFilesDir( FILE_EXT );
    if(ret) {
        showList();
        showToast("copy successful");
    } else {
        showToast("copy faild");
    }
} // copyFiles



/** 
 *   showList
 *   show File Name in ExternalFilesDir
 */
private void showList() {

    mList = mFileUtil.getFileListInExternalFilesDir();
    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();
    mListView.invalidate();
} // showList


/** 
 *  startCamera
 *  start Camera App
 */
private void startCamera() {

        if (mCameraPerm.requestPermissions()) {
            log_d("startCamera not perm");
            return;
        }
        // create File to save Picture
        File file = mFileUtil.createOutputFileInExternalFilesDir();
        Uri uri = convUri(file); 
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if( USE_CAMERA_EXTRA_OUTPUT ) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivityForResult(intent,  REQUEST_CODE_CAMERA_IMAGE);

} // startCamera



/**
 * start FileViewer
 *  such as Gallery App
 */ 
private void startFileViewer(File file) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = convUri(file); 
                intent.setDataAndType(uri, MIME_TYPE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String title = getString(R.string.chooser_title_viewer);
                Intent chooser = Intent.createChooser(intent, title);

                try {
                    startActivity(chooser);
                } catch (Exception e) {
                    e.printStackTrace();
                }

} // startFileViewer


/**
 * start ShareFile
 *  such as Mail App
 */ 
private void startShareFile(File file) {
 
                Uri uri = convUri(file); 
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, "");
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType(MIME_TYPE);
                String title = getString(R.string.chooser_title_share);
                Intent chooser = Intent.createChooser(intent, title);

                try {
                    startActivity(chooser);
                }catch (Exception e) {
                    e.printStackTrace();
                }

} // startShareFile


/**
 * convUri
 * convert URI from File
 */ 
private Uri convUri(File file) {
    Uri uri = mFileProviderUtil.getUri(file);
    log_d("convUri: " + file.toString() + " , " + uri.toString() );
    return uri;
} // convUri


/** 
 *  procItemClick
 */
private void procItemClick( int position, long id ) {
		String msg = "procItemClick: " + position + ", " + id;
		log_d(msg );

		// header footer
		if ( id == -1 )  return;
		// check position
		if (( position < 0 )||( position >= mList.size() )) return;

        File file = mList.get( position );
       showSelectDialog(file);

} // procItemClick


/** 
 * showSelectDialog
 * show image in Dialog
 * and select the next procedure
 */
private void showSelectDialog(final File file) {

	Bitmap bitmap =  BitmapUtil.getScaledBitmap(file, MAX_WIDTH, MAX_HEIGHT);

    ImageView imageView = new ImageView(this);
    imageView.setImageBitmap( bitmap );

    new AlertDialog.Builder(this)
            .setTitle( R.string.dialog_select_title )
            .setView(  imageView )
            .setPositiveButton(R.string.dialog_select_view, new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startFileViewer(file);
                }
            }) // setPositiveButton
            .setNegativeButton(R.string.dialog_select_send, new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                     startShareFile(file);
                }
            }) // setNegativeButton
            .show();

} // showSelectDialog


/**
 * showToast
 */
private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class MainActivity
