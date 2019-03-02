/**
 * File Picker Sample
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.filepicker1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

/**
 * PickerUtil
 * reference :  https://github.com/Angads25/android-filepicker
 */
public class PickerUtil {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "Picker";
    	private final static String TAG_SUB = "PickerUtil";

    private static final String DIR_NAME = "picker";

    	private final static String LF = "\n";

    	private final static String TEST_TEXT = "The quick brown fox jumps over the lazy dog";

    	private final static String READ_FILE_NAME = "sample.txt";
    	private final static String WRITE_FILE_NAME = "test.txt";

    private TextView mTextView;

  private Context mContext;

    private FileUtil mFileUtil;

/**
 * constractor
 */
public PickerUtil(Context context) {
    mContext = context;
    mFileUtil = new FileUtil(context);
} // PickerUtil


/**
 * setTextView
 */
public void setTextView( TextView view ) {
    mTextView = view;
} // setTextView


/**
 * setup
 */
public void setup( ) {
    boolean ret = mFileUtil.copyAssetToSTorage( DIR_NAME, READ_FILE_NAME);
    if (ret) {
        toast_long("setup Successful");
    } else {
        toast_long("setup Failed");
    }
} // setup

/**
 * showFilePicker
 */
public void showFilePicker(){
        File root_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() );
        File sub_dir = new File( root_dir, DIR_NAME);
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = sub_dir;
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{ "txt" };

        FilePickerDialog dialog = new FilePickerDialog(mContext, properties);
        dialog.setTitle("Select File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(final String[] files) {
            readFile(files);
            }
        }); // setDialogSelectionListener
        dialog.show();
    } // showFilePicker

/**
 * readFile
 */
private void readFile(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                if (files.length !=1) {
                    toast_long("select file");
                    return;
                }

                    log_d("readFile: " +files[0]);
    File file = new File(files[0]);
    String text =  mFileUtil.readTextFile(file);
    if ((text == null)||(text.length() == 0)) {
        toast_long("read Failed");
        return;
    }
    mTextView.setText(text);
    toast_long("read Successful");
} // readFile


/**
 * showDirPicke
 */
public void showDirPicker(){
        File root_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() );
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = root_dir;
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);        
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);

        FilePickerDialog dialog = new FilePickerDialog(mContext, properties);
        dialog.setTitle("Select  Dir");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(final String[] files) {
                writeFile(files);
            }
        }); // setDialogSelectionListener
        dialog.show();
    } // showDirPicke

/**
 * writeFile
 */
private void writeFile(String[] files) {
                if (files.length != 1) {
                    toast_long("not select file");
                    return;
                }
                log_d("writeFile: " + files[0]);
                log_d(files[0]);
    File dir = new File( files[0] );
    File file = new File( dir, WRITE_FILE_NAME );
    // append false
    boolean ret = mFileUtil.writeTextFile(file, TEST_TEXT, false );
    if (ret) {
        toast_long("write Successful");
    } else {
        toast_long("write Failed");
    }

} // writeFile


/**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} //  class PickerUtil
