/**
 * Osmdroid Sample
 * Tile  Downloader for Offline Map 
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid7;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.modules.SqliteArchiveTileWriter;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;


/**
 *   class CacheDownloaderArchive
 *   original : https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/cache/SampleCacheDownloaderArchive.java
 */
public class CacheDownloaderArchive  
implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, TextWatcher {

    private static final String DIR_NAME = MapActivity.DIR_NAME;

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "CacheDownloaderArchive";

        // zoom level
    	private final static int ZOOM_MIN_DEFAULT = 0;
    	private final static int ZOOM_MAX_DEFAULT = 2;

    Activity mActivity;
    Resources mResources;
 MapView  mMapView;

/**
 *  constractor
 */
public CacheDownloaderArchive(Activity activity) {
    mActivity = activity;
    mResources =  activity.getResources();
} // CacheDownloaderArchive


    Button btnCache,executeJob;
    SeekBar zoom_min;
    SeekBar zoom_max;
    EditText cache_north, cache_south, cache_east,cache_west, cache_output;
    TextView cache_estimate;
    CacheManager mgr=null;
    AlertDialog downloadPrompt=null;
    AlertDialog alertDialog=null;
    SqliteArchiveTileWriter writer=null;

/**
 * createView
 */
    public View createView() {

LayoutInflater inflater = LayoutInflater.from( mActivity);

        View root = inflater.inflate(R.layout.sample_cachemgr, null, false);

        //prevent the action bar/toolbar menu in order to prevent tile source changes.
        //if this is enabled, playstore users could actually download large volumes of tiles
        //from tile sources that do not allow it., causing our app to get banned, which would be
        //bad

        mMapView = new MapView(mActivity);
        ((LinearLayout) root.findViewById(R.id.mapview)).addView(mMapView);
        btnCache = (Button)root.findViewById(R.id.btnCache);
        btnCache.setOnClickListener(this);
        return root;
} // createView

/**
 * getMapView
 */
public MapView getMapView() {
            return mMapView;
} // getMapView


/**
 * onClick
 */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.executeJob:
                updateEstimate(true);
                break;

            case R.id.btnCache:
                showCacheManagerDialog();
                break;

        }
    } // onClick


/**
 * showCacheManagerDialog
 */
    private void showCacheManagerDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mActivity);


        // set title
        alertDialogBuilder.setTitle(R.string.cache_manager);
        //.setMessage(R.string.cache_manager_description);

        // set dialog message
        alertDialogBuilder.setItems(new CharSequence[]{
                        mResources.getString(R.string.cache_current_size),
                        mResources.getString(R.string.cache_download),
                        mResources.getString(R.string.cancel)
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                mgr = new CacheManager(mMapView);
                                showCurrentCacheInfo();
                                break;
                            case 1:
                                downloadJobAlert();
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                }
        );


        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


        //mgr.possibleTilesInArea(mMapView.getBoundingBox(), 0, 18);
        // mgr.
    } // showCacheManagerDialog


/**
 * downloadJobAlert
 */
    private void downloadJobAlert() {
        //prompt for input params
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        View view = View.inflate(mActivity, R.layout.sample_cachemgr_input, null);
        view.findViewById(R.id.cache_archival_section).setVisibility(View.VISIBLE);

        BoundingBox boundingBox = mMapView.getBoundingBox();
        log_d( "BoundingBox : " + boundingBox );
        zoom_max= (SeekBar)view.findViewById(R.id.slider_zoom_max);

    double zoomLevel = mMapView.getMaxZoomLevel();
    int zoomMaxLevel = (int)zoomLevel;
    log_d( "ZoomLevel : " + zoomLevel );

        zoom_max.setMax( zoomMaxLevel );
        zoom_max.setProgress( ZOOM_MAX_DEFAULT );
        zoom_max.setOnSeekBarChangeListener(this);

        zoom_min= (SeekBar)view.findViewById(R.id.slider_zoom_min);
        zoom_min.setMax( zoomMaxLevel );
        zoom_min.setProgress( ZOOM_MIN_DEFAULT );
        zoom_min.setOnSeekBarChangeListener(this);

        cache_east= (EditText)view.findViewById(R.id.cache_east);
        cache_east.setText(boundingBox.getLonEast() +"");
        cache_north= (EditText)view.findViewById(R.id.cache_north);
        cache_north.setText(boundingBox.getLatNorth()  +"");
        cache_south= (EditText)view.findViewById(R.id.cache_south);
        cache_south.setText(boundingBox.getLatSouth()  +"");
        cache_west= (EditText)view.findViewById(R.id.cache_west);
        cache_west.setText(boundingBox.getLonWest()  +"");
        cache_estimate = (TextView)view.findViewById(R.id.cache_estimate);
        cache_output= (EditText)view.findViewById(R.id.cache_output);

        //change listeners for both validation and to trigger the download estimation
        cache_east.addTextChangedListener(this);
        cache_north.addTextChangedListener(this);
        cache_south.addTextChangedListener(this);
        cache_west.addTextChangedListener(this);
        executeJob= (Button)view.findViewById(R.id.executeJob);
        executeJob.setOnClickListener(this);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cache_east=null;
                cache_south=null;
                cache_estimate=null;
                cache_north=null;
                cache_west=null;
                executeJob=null;
                zoom_min=null;
                zoom_max=null;
                cache_output=null;
            }
        });
        downloadPrompt=builder.create();
        downloadPrompt.show();


    } // downloadJobAlert


    /**
     * if true, start the job
     * if false, just update the dialog box
     */
    private void updateEstimate(boolean startJob) {
        try {
            if (cache_east != null &&
                    cache_west != null &&
                    cache_north != null &&
                    cache_south != null &&
                    zoom_max != null &&
                    zoom_min != null &&
                    cache_output!=null) {
                double n = Double.parseDouble(cache_north.getText().toString());
                double s = Double.parseDouble(cache_south.getText().toString());
                double e = Double.parseDouble(cache_east.getText().toString());
                double w = Double.parseDouble(cache_west.getText().toString());
                if (startJob) {
                    // TODO ; if not exists "osmdroid" directory
                    String outputName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DIR_NAME + File.separator + cache_output.getText().toString();
log_d( "outputName: " + outputName );
                    writer =new SqliteArchiveTileWriter(outputName);
                    mgr = new CacheManager(mMapView, writer);
                } else {
                    if (mgr==null)
                        mgr = new CacheManager(mMapView);
                }
                int zoommin = zoom_min.getProgress();
                int zoommax = zoom_max.getProgress();
                //nesw
                BoundingBox bb= new BoundingBox(n, e, s, w);
                int tilecount = mgr.possibleTilesInArea(bb, zoommin, zoommax);
                cache_estimate.setText(tilecount + " tiles");
                if (startJob)
                {
                    if ( downloadPrompt!=null) {
                        downloadPrompt.dismiss();
                        downloadPrompt=null;
                    }

                    //this triggers the download
// downloadAreaAsync(Context ctx, BoundingBox bb, final int zoomMin, final int zoomMax, final CacheManagerCallback callback)
 String msg = "zoommin : " + zoommin + " ; zoommax : " + zoommax +  " ; " + bb.toString();
log_d( msg );
                    mgr.downloadAreaAsync(mActivity, bb, zoommin, zoommax, new CacheManager.CacheManagerCallback() {
                        @Override
                        public void onTaskComplete() {
            toast_long("Download complete!");
                            if (writer!=null)
                                writer.onDetach();
                        }

                        @Override
                        public void onTaskFailed(int errors) {
String msg = "Download complete with " + errors + " errors";
                    toast_long(msg);
                    log_d(msg);
                            if (writer!=null)
                                writer.onDetach();
                        }

                        @Override
                        public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                            //NOOP since we are using the build in UI
                        }

                        @Override
                        public void downloadStarted() {
                            //NOOP since we are using the build in UI
                        }

                        @Override
                        public void setPossibleTilesInArea(int total) {
                            //NOOP since we are using the build in UI
                            log_d(  "setPossibleTilesInArea: " + total );
                        }
                    });
                }

            }
        }catch (Exception ex){
            //TODO something better?
            if (D) ex.printStackTrace();
        }
    } // updateEstimate


/**
 *  showCurrentCacheInfo
 */
    private void showCurrentCacheInfo() {
        toast_long("Calculating...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mActivity);


                // set title
                alertDialogBuilder.setTitle(R.string.cache_manager)
                        .setMessage("Cache Capacity (bytes): " + mgr.cacheCapacity() + "\n"+
                                "Cache Usage (bytes): " + mgr.currentCacheUsage());

                // set dialog message
                alertDialogBuilder.setItems(new CharSequence[]{

                                mResources.getString(R.string.cancel)
                        }, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );




                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // show it
                        // create alert dialog
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });

            }
        }).start();

    } // showCurrentCacheInfo

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateEstimate(false);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // nop
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // nop
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // nop
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateEstimate(false);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // nop
    }

/**
 *  onPause
 */
    public void onPause(){
        // super.onPause();
        if (alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        if (downloadPrompt!=null && downloadPrompt.isShowing()){
            downloadPrompt.dismiss();
        }
    } // onPause

   /**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( mActivity, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

 	/**
	 * write into logcat
	 */ 
	private  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // CacheDownloaderArchive
