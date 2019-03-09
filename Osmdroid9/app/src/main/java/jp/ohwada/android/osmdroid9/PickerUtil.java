/**
 * Osmdroid Sample
 * Bookmark
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid9;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;

import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.util.StorageUtils;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.SimpleLocationOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * class PickerUtil
 */
public class PickerUtil { 

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "PickerUtil";

    private Activity mActivity;
    private  Context mContext;

    private MapView mMapView;

    private BookmarkHelper mHelper = null;

    private FileUtil mFileUtil;

    // AlertDialog
    private EditText mEditTextFileName;


/**
 * constractor
 */
public PickerUtil(Activity activity) {
    mActivity = activity;
    mContext = activity;
    mHelper = new BookmarkHelper(activity);
mFileUtil = new FileUtil(activity);
} // PickerUtil


/**
 * setMapView
 */
public void setMapView( MapView view ) {
        mMapView = view;
} // setMapView

/**
 * onDestroy
 */
public void onDestroy() {
        if (mHelper != null)
           mHelper.close();
       mHelper = null;
} // onDestroy

/**
 * showExportPicker
 */
public void showExportPicker(){

        File root_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() );
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = root_dir;
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);

        FilePickerDialog filePickerDialog = new FilePickerDialog(mContext, properties);
        filePickerDialog.setTitle("Save CSV File");
        filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(final String[] files) {
                showAlertDialog(files);
            }
        }); // setDialogSelectionListener
        filePickerDialog.show();
} // showExportPicker


/**
 * showAlertDialog
 * enter output filename
 */
private void showAlertDialog( final String[] files) {

                if (files.length != 1) return;

                //now prompt for a file name
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Enter file name (.csv)");

                // Set up the input
                mEditTextFileName = new EditText(mContext);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                mEditTextFileName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                mEditTextFileName.setLines(1);
                mEditTextFileName.setText("export.csv");
                    builder.setView( mEditTextFileName );
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface filePickerDialog, int which) {
                            exportCsv_onThead( files, mEditTextFileName );
                        }
                    }); // setPositiveButton

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface filePickerDialog, int which) {
                            filePickerDialog.cancel();
                        }
                    }); // setNegativeButton

                    builder.show();

} // showAlertDialog


/**
 * exportCsv_onThead
 */
private void exportCsv_onThead(String[] files, EditText editFileName ) {
                            //save the file here.
                        String fileName = editFileName.getText().toString();
                            if (!fileName.toLowerCase().endsWith(".csv")) {
                                fileName = fileName + ".csv";
                            }
        File exportDir = new File( files[0] );
        final File outputFile = new File( exportDir, fileName );

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    List<BookmarkRecord> list = mHelper.getAllList();
                                    boolean status = mFileUtil.writeCsv( outputFile, list );
                                    showExportMsg_onUI(status);
                                }
                            }).start();
} // exportCsv_onThead

/**
 * toastExport Msg
 */
    private void showExportMsg_onUI( final boolean status) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status) {
                        toast_long("Export Complete");
                    } else
                        toast_long("Export Failed");
                }
            }); // runOnUiThread
} // toastExport Msg

/**
 * showImportPicker
 */
public void showImportPicker( String dirName ){
        File root_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() );
        File sub_dir = new File( root_dir, dirName);
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = sub_dir;
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);

        Set<String> registeredExtensions = ArchiveFileFactory.getRegisteredExtensions();

        registeredExtensions.add("csv");


        String[] ret = new String[registeredExtensions.size()];
        ret = registeredExtensions.toArray(ret);
        properties.extensions = ret;

        FilePickerDialog filePickerDialog = new FilePickerDialog(mContext, properties);
        filePickerDialog.setTitle("Select a CSV File");
        filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(final String[] files) {
                //files is the array of the paths of files selected by the Application User.
                importCsv_onThead(files);
            }
        }); // setDialogSelectionListener
        filePickerDialog.show();

} // showImportPicker

/**
 * importCsv_onThead
 */
private void importCsv_onThead(final String[] files) {
                //files is the array of the paths of files selected by the Application User.
                if (files.length != 1) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        importCsv( files );
                    }
                }).start();
} // importCsv_onThead

    /**
     * importFromCsv
     * call me from a background thread
     */
private void importCsv( String[] files ){

    if( files.length != 1 ) return;

    File file = new File(files[0]);

    final List<BookmarkRecord> list = mFileUtil.readCsv(file);
    if (list == null) return;
    AtomicInteger imported= new AtomicInteger();
    AtomicInteger failed= new AtomicInteger();
        for (BookmarkRecord r: list) {
            // add bookmark
                   long id = mHelper.insert(r);
                if (id > 0) {
                       imported.getAndIncrement();;
                } else {
                     failed.getAndIncrement();;
                }

    // show marker
                    Marker m = new Marker( mMapView );
                    m.setPosition(new GeoPoint( r.lat, r.lon ));
                    m.setTitle(r.title);
                    m.setSnippet(r.description);
                    mMapView.getOverlayManager().add(m);
        } // for
        showImportMsg_onUI( imported.get(), failed.get() );

    } // importFromCsv

    /**
     * showImportMsg_onUI
     */
private void showImportMsg_onUI( int imported, int failed ) {
    final String msg = "Import Complete: " + imported + "/" + failed + "(imported/failed)";
        mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMapView.invalidate();
                    toast_long(msg);
                    log_d(msg);
                }
            }); // runOnUiThread
} // showImportMsg_onUI


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

} // class PickerUtil
