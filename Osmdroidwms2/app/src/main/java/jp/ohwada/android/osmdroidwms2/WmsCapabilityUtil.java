/**
 * Osmdroid Sample
 * OSM with WMS Tile
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroidwms2;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.net.HttpURLConnection;
import java.net.URL;

import org.osmdroid.util.BoundingBox;

import org.osmdroid.wms.WMSEndpoint;
import org.osmdroid.wms.WMSLayer;
import org.osmdroid.wms.WMSParser;
import org.osmdroid.wms.WMSTileSource;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


/**
 * class WmsCapabilityUtil
 * reference : https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/tilesources/SampleWMSSource.java
 * 国交省 WMS
http://nlftp.mlit.go.jp/webmapc/help.html#N3
 */ 
public class WmsCapabilityUtil  {

	// dubug
    private final static boolean D = true; 
	private final static String TAG = "OSM";
	private final static String TAG_SUB = "WmsCapabilityUtil";

    // intent key
   private  final static String KEY_BASE_URL = WmsSourceUtil.KEY_BASE_URL;
    private final static String KEY_VERSION = WmsSourceUtil.KEY_VERSION;
 private final static String KEY_LAYER_TITLE = WmsSourceUtil.KEY_LAYER_TITLE;
    private final static String KEY_LAYER_NAME = WmsSourceUtil.KEY_LAYER_NAME;
   private final static  String KEY_SRS = WmsSourceUtil.KEY_SRS;
    private final static  String KEY_STYLE = WmsSourceUtil.KEY_STYLE;
    private final static  String KEY_PIXEL_SIZE = WmsSourceUtil.KEY_PIXEL_SIZE;

	private final static String LF = "\n";

    // 国交省
	private final static String URL_CAP =  "http://nrb-www.mlit.go.jp/webmap/service/wmsRasterMap?SERVICE=WMS&REQUEST=GetCapabilities&VERSION=1.1.1";

	private final static String FILE_NAME_CAP = "mlit_wms_capabilities.xml";

	private final static String FILE_NAME_LAYER = "milt_wms_layer_list.csv" ;

    private Activity mActivity;
    private Context mContext;

    private AssetManager mAssetManager;

 // callback 
    private Callback mCallback;  

    private String mBaseurl;
    private String mVersion;
    private String mSrs;
    private String mStyles;
    private int mPixelSize;


/**
 * callback interface
 */    
    public interface Callback {
        public void onFinish( Capability cap );
    } // interface


/**
 * callback
 */ 
    public void setCallback( Callback callback ) {
        log_d("setCallback");
        mCallback = callback;
    } // setCallback


 	/**
	 * constractor
	 */ 
public WmsCapabilityUtil(Activity activity) {
    mActivity = activity;
    mContext = activity;
    mAssetManager = activity.getAssets();
} // WmsUtil

public void parse() {

     new Thread(new Runnable() {
            @Override
            public void run() {
                Capability cap = parse_onThread();
                notifyCap(cap);
            }
        }).start();

} // parse


/**
 * parse_onThread
 * run on background 
 */
private Capability parse_onThread() {

    InputStream is_layer = getAssetInputStream( FILE_NAME_LAYER );
    if (is_layer == null) {
        log_d("cannot get InputStream for layer");
        return null;
    }

    InputStreamReader reader = getInputStreamReader(  is_layer );
    HashMap hash = readCsv( reader );
    if (hash == null) {
       log_d("read csv faild");
        return null;
    }

    InputStream is_cap = getAssetInputStream( FILE_NAME_CAP );
    if (is_cap == null) {
        log_d("cannot get InputStream for cap");
        return null;
    }

    // InputStream _cap = getHttpInputStream(URL_CAP);
   
    WMSEndpoint ep = null;
    try {
            // note : about 1 minute to execute
            ep = WMSParser.parse(is_cap);
    } catch (Exception e){
			if (D) e.printStackTrace();
    }
    if (ep == null) {
        toast_long("parse faild");
        return null;
    }

    List<WMSLayer>list = changeTitle(ep, hash);

    Capability cap = new Capability();
    cap.header = makeHeader(ep );
    cap.list = list;
    return cap;
} // parse

/**
 * getHttpInputStream
 */
private InputStream getHttpInputStream(String url) {
      HttpURLConnection c = null;
      InputStream is = null;
      try {
            c = (HttpURLConnection) new URL(url).openConnection();
            is = c.getInputStream();
    } catch (Exception e){
			if (D) e.printStackTrace();
    }
    return is;
} // getHttpInputStream

/**
 * changeTitle
 */
private List<WMSLayer> changeTitle(WMSEndpoint ep, HashMap hash) {

    List<WMSLayer> list_orig = ep.getLayers();
    List<WMSLayer> list_new = new ArrayList<WMSLayer>();

    for (WMSLayer layer: list_orig) {
        String name = layer.getName();
        WMSLayer layer_new = layer;
        if ( hash.containsKey(name) ) {
            String title = (String) hash.get(name);
            log_d(  "match : " + name + " : " + title );
            layer_new.setTitle( title);
        }
        list_new.add( layer_new );
    } // for
    return list_new;
} // changeTitle


/**
 * makeHeader
 */
private String makeHeader(WMSEndpoint ep ) {

    List<WMSLayer> list = ep.getLayers();
    int size = 0;
    if (list != null) {
        // 2020
        size = list.size();
    }

    mBaseurl = ep.getBaseurl();
    mVersion = ep.getWmsVersion();

  StringBuilder sb = new StringBuilder();
    sb.append( "title : " ) ;
    sb.append( ep.getTitle() ) ;
    sb.append( LF ) ;
    sb.append( "name : " ) ;
    sb.append( ep.getName() ) ;
    sb.append( LF ) ;
    sb.append( ep.getDescription() ) ;
    sb.append( LF ) ;
    sb.append( "Baseurl : " ) ;
    sb.append( mBaseurl ) ;
    sb.append( LF ) ;
    sb.append( "Version : " ) ;
    sb.append( mVersion ) ;
    sb.append( LF ) ;
    sb.append( "number of Layers : " ) ;
    sb.append( size ) ;
    sb.append( LF ) ;
    String header = sb.toString();
    log_d("header: " + header);
    return header;
} // makeHeader


/**
 * showDialog
 */
public void showDialog( final WMSLayer layer ) {
    
    String title = layer.getTitle();

List<String> list_srs = layer.getSrs();
List<String>  list_style = layer.getStyles();

BoundingBox box =  layer.getBbox();
String bbbox = null;
if ( box != null ) {
    bbbox = box.toString();
} 

  StringBuilder sb = new StringBuilder();
    sb.append( "name : " ) ;
    sb.append( layer.getName() ) ;
    sb.append( LF ) ;
    sb.append( "description : " ) ;
    sb.append( layer.getDescription() ) ;
    sb.append( LF ) ;
    sb.append( "size : " ) ;
    sb.append( layer.getPixelSize() ) ;
    sb.append( LF ) ;
    sb.append( "bbbox : " ) ;
    sb.append( bbbox ) ;
    sb.append( LF ) ;

    sb.append( "srs : " ) ;
    sb.append( LF ) ;
    for (String str:  list_srs ) {
        sb.append( str ) ;
        sb.append( LF ) ;
    }

    sb.append( "style : " ) ;
    sb.append( LF ) ;
    for (String str2:  list_style ) {
        sb.append( str2 ) ;
        sb.append( LF ) ;
    }

    String message = sb.toString();
    log_d( title + LF + message);

new AlertDialog.Builder(mContext)
        .setTitle( title )
        .setMessage( message )
    .setPositiveButton("Open Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openMap(layer);
            }
        })
        .setNegativeButton("Cancel", null)
        .show();

} // showDialog

/**
  * openMap
  */
private void  openMap(WMSLayer layer) {

    List<String> list_srs = layer.getSrs();
    List<String> list_style = layer.getStyles();
     String srs = "";
     String style = "";
    if ((list_srs != null )&&(list_srs.size() > 0)) {
        srs = list_srs.get(0);
    }
    if ((list_style != null )&&(list_style.size() > 0)) {
        style = list_style.get(0);
    }

    Intent intent = new Intent( mContext , MapActivity.class );
    intent.putExtra( KEY_BASE_URL, mBaseurl);
    intent.putExtra( KEY_VERSION, mVersion);
    intent.putExtra( KEY_LAYER_TITLE, layer.getTitle());
    intent.putExtra( KEY_LAYER_NAME, layer.getName());
    intent.putExtra( KEY_PIXEL_SIZE, layer.getPixelSize());
    intent.putExtra( KEY_SRS, srs);
    intent.putExtra( KEY_STYLE,  style);
    mActivity.startActivity(intent);
} // openMap


    /**
     * readCsv
     */
    private HashMap readCsv(Reader inputReader ) {

    if( inputReader == null ) return null;

    HashMap hash = new HashMap(100);

     String[] nextLine;
    CSVReader reader = null;
    try {
        reader = new CSVReader( inputReader );
            while ((nextLine = reader.readNext()) != null) {
                    // name,  title
                    String name =  nextLine[0].trim() ;
                    String title =  nextLine[1].trim() ;
                    log_d( "read : " + name + " : " + title );
                    hash.put(name, title);
        } // while

        }catch (Exception ex) {
            if(D) ex.printStackTrace();
        } finally {
                try {
                     if( reader != null ) reader.close();
                      if( inputReader != null ) inputReader.close();
                }catch (Exception ex) {
                    if(D) ex.printStackTrace();
                }
        }

        try {
             if( inputReader != null ) inputReader.close();
        }catch (Exception ex) {
                    if(D) ex.printStackTrace();
        }

    return  hash;

    } // readCsv



 	/**
	 * getAssetInputStream
	 */ 
private InputStream getAssetInputStream( String fileName ) {

         InputStream is = null;
    try {
        is = mAssetManager.open(fileName);
    } catch (Exception e){
			if (D) e.printStackTrace();
    }

    return is;
} // getAssetInputStream


 	/**
	 * getInputStreamReader
	 */ 
private InputStreamReader getInputStreamReader( InputStream is ) {

    if (is == null) return null;

         InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(is);
    } catch (Exception e){
			if (D) e.printStackTrace();
    }

    return reader;
} // getAssetInputStream


/**
 * notifyCap
 */
private void notifyCap(Capability cap) {
           if ( mCallback != null ) {
            mCallback.onFinish( cap );
        } 
} // notifyCap

   /**
 * toast_long
 */
	private void toast_long( String msg ) {
		ToastMaster.makeText( mContext, msg, Toast.LENGTH_LONG ).show();
	} // toast_long

   /**
 * toast_long
 */
	private void toast_long( int res_id ) {
		ToastMaster.makeText( mContext, res_id, Toast.LENGTH_LONG ).show();
	} // toast_long

 	/**
	 * write into logcat
	 * @param String msg
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

public class Capability {
    public String header = "";
    public List<WMSLayer> list = null;
} // class Capability


} // WmsUtil