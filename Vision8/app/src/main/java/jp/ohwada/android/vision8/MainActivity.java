 /**
 * Vision Sample
 * FaceTrimming
 * 2019-02-01 K.OHWADA 
 */
package jp.ohwada.android.vision8;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.vision.face.Face;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 *  class MainActivity
 * FaceTrimming
 */
public class MainActivity extends Activity {

   		// dubug
	private final static boolean D = true; 
	private final static  String TAG = "Face";
    	private final static String TAG_SUB = "MainActivity";


/**
 * File Extention in Asset Folder 
 */
    private final static String IMAGE_EXT = ".jpg";


/**
 * Mode for Face Image
 */ 
    private final static int  MODE_ORIGINAL = 0;
    private final static int  MODE_RECTANGLE = 1;
    private final static int  MODE_LANDMARK = 2;
    private final static int  MODE_TRIMMING = 3;

    private int  mModeFaceImage = MODE_RECTANGLE;


/**
 * ListView 
 */
    private ListView mListView ;

/**
 * ListAdapter for ListView
 */
   	private ListAdapter mAdapter;

/**
 * List for  ListAdapter
 */
	private List<String> mList;


/**
 * FileUtil 
 */
    private FileUtil mFileUtil;


/**
 * FaceDetectorUtil
 */
    private FaceDetectorUtil mFaceDetectorUtill;


/**
 * FaceImage
 */
    private FaceImage mFaceImage;


/**
 * FileName for Faceimage
 */
    private String mImageFileName;


/**
 * onCreate 
 */
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        Button btnSetting = (Button)findViewById(R.id.Button_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingDialog();
            }
        }); // btnSetting

        mListView = (ListView)findViewById(R.id.list);

        mFileUtil = new FileUtil(this);
        mFaceDetectorUtill = new FaceDetectorUtil(this);
        mFaceImage  = new FaceImage();

        mList = new ArrayList<String>();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener

}// onCreate


/**
 * onResume
 */
@Override
public void onResume() {
        super.onResume();

    showList();
    boolean ret = mFaceDetectorUtill.prepareDetector();
    if(!ret) {
        String msg = mFaceDetectorUtill.getErrorMsg();
        showToast(msg);
    }

} // onResume


/**
 * onPause
 */
@Override
public void onPause() {
        super.onPause();
        mFaceDetectorUtill.releaseDetector();
} // onPause


/** 
 *   showList
 */
private void showList() {

    mList = mFileUtil.getAssetList( IMAGE_EXT );
    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();
    mListView.invalidate();

} // showList


/** 
 *  procItemClick
 */
private void procItemClick( int position, long id ) {

		// log_d( "procItemClick: " + position + ", " + id );

		// header footer
		if ( id == -1 )  return;
		// check position
		if (( position < 0 )||( position >= mList.size() )) return;

        String item = mList.get( position );
        mImageFileName = item;
        detectAndShowImage();
} // procItemClick


/** 
 *  detectAndShowImage
 */
private void detectAndShowImage() {

        if( mImageFileName == null) return;

        Bitmap bitmap_orig = mFileUtil.getAssetsBitmap(mImageFileName);

        SparseArray<Face> faces = mFaceDetectorUtill.getVisionFaces(bitmap_orig);

        Bitmap bitmap = 
        getFaceImage(bitmap_orig, faces );

        showFaceImageDialog(mImageFileName,  bitmap);

} // detectAndShowImage


/**
 * getFaceImage
 */ 
private Bitmap getFaceImage(Bitmap bitmap_orig,  SparseArray<Face> faces ) {

    Bitmap bitmap = null;
    switch(mModeFaceImage) {
        case MODE_ORIGINAL:
            bitmap = bitmap_orig;
            break;
        case MODE_LANDMARK:
            bitmap 
            = mFaceImage.createLandmarkBitmap(bitmap_orig, faces);
            break;
        case MODE_TRIMMING:
            bitmap 
            = mFaceImage.createTrimmingBitmap(bitmap_orig,  faces);
            break;
        case MODE_RECTANGLE:
        default:
            bitmap 
            = mFaceImage.createRectangleBitmap(bitmap_orig,  faces);
            break;
    }
    return bitmap;
} // getFaceImage


/** 
 *  showFaceImageDialog
 */
private void showFaceImageDialog(String title, Bitmap bitmap) {

    ImageView imageView = new ImageView(this);
    imageView.setImageBitmap( bitmap );

    new AlertDialog.Builder(this)
         .setTitle( title )
        .setView(  imageView )
        .setPositiveButton( R.string.button_ok, null ) 
        .show();

} // showFaceImageDialog


/**
 * showSettingDialog
 */ 
private void showSettingDialog() {

        Resources res = getResources();
        String[] ITEMS = res.getStringArray(R.array.setting_dialog_items);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle( R.string.setting_dialog_title );

        builder.setSingleChoiceItems( ITEMS, mModeFaceImage, 
        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mModeFaceImage = which;
                    } 
                } // OnClickListener
        ); // setSingleChoiceItems

        builder.setPositiveButton(R.string.button_ok, new  DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    detectAndShowImage();
                }
        }); // setPositiveButton

        // show it
        AlertDialog dialog = builder.create();
        dialog.show();

} // showSettingDialog


/**
	 * showToast
	 */
	private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
	} // showToast


 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class MainActivity
