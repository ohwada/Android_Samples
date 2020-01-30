/**
 * SimpleCropView Sample
 * 2020-01-01 K.OHWADA
 * original : https://github.com/igreenwood/SimpleCropView
 */
package jp.ohwada.android.simplecropview1;



import android.Manifest;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;
import com.isseiaoki.simplecropview.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/** 
 *   class BasicFragment
 */
public class BasicFragment extends Fragment {

    // debug
  private static final String TAG = BasicFragment.class.getSimpleName();


  private static final int REQUEST_PICK_IMAGE = 10011;

  private static final int REQUEST_SAF_PICK_IMAGE = 10012;

  private static final String PROGRESS_DIALOG = "ProgressDialog";

  private static final String KEY_FRAME_RECT = "FrameRect";

  private static final String KEY_SOURCE_URI = "SourceUri";


    private static final int REQUEST_PICK_IMAGE_PERMISSION = 10013;

    private static final int REQUEST_CROP_IMAGE_PERMISSION = 10014;

    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;


  // Views
  private CropImageView mCropView;

  private RectF mFrameRect = null;

  private Uri mSourceUri = null;


/**
 * Permission
 */ 
      private FragmentPermission mPickImagePerm;
      private FragmentPermission mCropImagePerm;


/**
 * Constructor
 * Note: only the system can call this constructor by reflection.
 */
  public BasicFragment() {
        // nop
  }


/**
 * newInstance
 */
  public static BasicFragment newInstance() {
    BasicFragment fragment = new BasicFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }


/**
 * onCreate
 */
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);

        //Activity activity = getActivity();

        mPickImagePerm = new FragmentPermission(this);
        mPickImagePerm.setRequestCode(REQUEST_PICK_IMAGE_PERMISSION);
        mPickImagePerm.setPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        mCropImagePerm = new FragmentPermission(this);
        mCropImagePerm.setRequestCode(REQUEST_CROP_IMAGE_PERMISSION);
        mCropImagePerm.setPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

  }


/**
 * onCreateView
 */
  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_basic, null, false);
  }

/**
 * onViewCreated
 */
  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // bind Views
    bindViews(view);

    mCropView.setDebug(true);

    if (savedInstanceState != null) {
      // restore data
      mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT);
      mSourceUri = savedInstanceState.getParcelable(KEY_SOURCE_URI);
    }

    if (mSourceUri == null) {
      // default data
      mSourceUri = getUriFromDrawableResId(getContext(), R.drawable.sample5);
      Log.e("aoki", "mSourceUri = "+mSourceUri);
    }
    // load image
    mCropView.load(mSourceUri)
        .initialFrameRect(mFrameRect)
        .useThumbnail(true)
        .execute(mLoadCallback);
  }


/**
 * onSaveInstanceState
 */
  @Override 
    public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // save data
    outState.putParcelable(KEY_FRAME_RECT, mCropView.getActualCropRect());
    outState.putParcelable(KEY_SOURCE_URI, mCropView.getSourceUri());
  }


/**
 * onActivityResult
 */
  @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
    super.onActivityResult(requestCode, resultCode, result);
    if (resultCode == Activity.RESULT_OK) {
      // reset frame rect
      mFrameRect = null;

      switch (requestCode) {
        case REQUEST_PICK_IMAGE:
          mSourceUri = result.getData();
          mCropView.load(mSourceUri)
              .initialFrameRect(mFrameRect)
              .useThumbnail(true)
              .execute(mLoadCallback);
          break;
        case REQUEST_SAF_PICK_IMAGE:
            log_d("REQUEST_SAF_PICK_IMAGE");
          mSourceUri = Utils.ensureUriPermission(getContext(), result);
            log_d("SourceUri:"  + mSourceUri.toString());
          mCropView.load(mSourceUri)
              .initialFrameRect(mFrameRect)
              .useThumbnail(true)
              .execute(mLoadCallback);
          break;
      } // switch
    } // if resultCode
  }


/**
 * onRequestPermissionsResult
 */
  @Override 
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
log_d("onRequestPermissionsResult requestCode= " + requestCode);
    //BasicFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

        switch (requestCode) {
            case REQUEST_PICK_IMAGE_PERMISSION:
                if (mPickImagePerm.isGrantRequestPermissionsResult(requestCode, grantResults)) {
                    pickImage();
                }
                break;
            case REQUEST_CROP_IMAGE_PERMISSION:
                if( mCropImagePerm.isGrantRequestPermissionsResult(grantResults) ) {
                    cropImage();
                }
                break;
        } // switch
    
  }


/**
 * bindViews
 */
  private void bindViews(View view) {
    mCropView = (CropImageView) view.findViewById(R.id.cropImageView);
    view.findViewById(R.id.buttonDone).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonFitImage).setOnClickListener(btnListener);
    view.findViewById(R.id.button1_1).setOnClickListener(btnListener);
    view.findViewById(R.id.button3_4).setOnClickListener(btnListener);
    view.findViewById(R.id.button4_3).setOnClickListener(btnListener);
    view.findViewById(R.id.button9_16).setOnClickListener(btnListener);
    view.findViewById(R.id.button16_9).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonFree).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonPickImage).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonCircle).setOnClickListener(btnListener);
    view.findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(btnListener);
  }


/**
 * pickImageWithCheck
 */
private void pickImageWithCheck() {

    if ( mPickImagePerm.requestPermissions() ) {
            log_d("pickImage: not perm");
            return;
    }
    pickImage();
}


  // @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
  public void pickImage() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
          REQUEST_PICK_IMAGE);
    } else {
      Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("image/*");
      startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
    }
  }

/**
 * cropImageWithCheck
 */
private void cropImageWithCheck() {

    if ( mCropImagePerm.requestPermissions() ) {
            log_d("cropImage: not perm");
            return;
    }
    cropImage();
}


  //@NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  public void cropImage() {
    showProgress();
    mCropView.crop(mSourceUri).execute(mCropCallback);
  }


/**
  * showProgress
 */ 
  public void showProgress() {
    ProgressDialogFragment f = ProgressDialogFragment.getInstance();
    getFragmentManager().beginTransaction().add(f, PROGRESS_DIALOG).commitAllowingStateLoss();
  }


/**
  * dismissProgress
 */ 
  public void dismissProgress() {
    //if (!isResumed()) return;
    android.support.v4.app.FragmentManager manager = getFragmentManager();
    if (manager == null) return;
    ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
    if (f != null) {
      getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
    }
  }


/**
  * createSaveUri
 */ 
  public Uri createSaveUri() {
    CropFile.mkDir();
    return CropFile.createNewUri(getContext(), COMPRESS_FORMAT);
  }


/**
  * getUriFromDrawableResId
 */
  public static Uri getUriFromDrawableResId(Context context, int drawableResId) {
    StringBuilder builder = new StringBuilder().append(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .append("://")
        .append(context.getResources().getResourcePackageName(drawableResId))
        .append("/")
        .append(context.getResources().getResourceTypeName(drawableResId))
        .append("/")
        .append(context.getResources().getResourceEntryName(drawableResId));
    return Uri.parse(builder.toString());
  }


/**
  * createTempUri
 */ 
  public static Uri createTempUri(Context context) {
    return Uri.fromFile(new File(context.getCacheDir(), "cropped"));
  }



/**
 * OnClickListener
 * handle button event
 */
  private final View.OnClickListener btnListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.buttonDone:
          //BasicFragmentPermissionsDispatcher.cropImageWithCheck(BasicFragment.this);
            cropImageWithCheck();
          break;
        case R.id.buttonFitImage:
          mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
          break;
        case R.id.button1_1:
          mCropView.setCropMode(CropImageView.CropMode.SQUARE);
          break;
        case R.id.button3_4:
          mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
          break;
        case R.id.button4_3:
          mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
          break;
        case R.id.button9_16:
          mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
          break;
        case R.id.button16_9:
          mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
          break;
        case R.id.buttonCustom:
          mCropView.setCustomRatio(7, 5);
          break;
        case R.id.buttonFree:
          mCropView.setCropMode(CropImageView.CropMode.FREE);
          break;
        case R.id.buttonCircle:
          mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
          break;
        case R.id.buttonShowCircleButCropAsSquare:
          mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
          break;
        case R.id.buttonRotateLeft:
          mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
          break;
        case R.id.buttonRotateRight:
          mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
          break;
        case R.id.buttonPickImage:
          //BasicFragmentPermissionsDispatcher.pickImageWithCheck(BasicFragment.this);
            pickImageWithCheck();
            break;
      }
    }
  }; // OnClickListener


/**
 * LoadCallback
 */ 
  private final LoadCallback mLoadCallback = new LoadCallback() {
    @Override public void onSuccess() {
            log_d("LoadCallback onSuccess");
    }

    @Override public void onError(Throwable e) {
            log_d("LoadCallback onError: " + e.getMessage());
    }
  }; // LoadCallback

/**
 * CropCallback
 */ 
  private final CropCallback mCropCallback = new CropCallback() {
    @Override public void onSuccess(Bitmap cropped) {
    log_d("CropCallback onSuccess");
      mCropView.save(cropped)
          .compressFormat(COMPRESS_FORMAT)
          .execute(createSaveUri(), mSaveCallback);
    }

    @Override public void onError(Throwable e) {
            log_d("CropCallback onError: " + e.getMessage());
    }
  }; // CropCallback


/**
 * SaveCallback
 */ 
  private final SaveCallback mSaveCallback = new SaveCallback() {
    @Override public void onSuccess(Uri outputUri) {
            log_d("SaveCallback onSuccess: " + outputUri.toString());
        String path = CropFile.getPath(getContext(), outputUri);
        String msg = "saved " + path;
        showToast( msg );
        log_d(msg);
      dismissProgress();
      ((BasicActivity) getActivity()).startResultActivity(outputUri);
    }

    @Override public void onError(Throwable e) {
            log_d("SaveCallback onError: " + e.getMessage());
      dismissProgress();
    }
  }; // SaveCallback


/**
 * showToast
 */
private void showToast( String msg ) {
		Toast.makeText( getContext(), msg, Toast.LENGTH_LONG ).show();
} 


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    Log.d( TAG, msg );
} 


} // class BasicFragment