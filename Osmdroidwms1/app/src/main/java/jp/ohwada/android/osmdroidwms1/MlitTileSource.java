/**
 * Osmdroid Sample
 * OSM with WMS Tile
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroidwms1;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.BitmapPool;
import org.osmdroid.tileprovider.ReusableBitmapDrawable;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.util.Counters;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.MapTileIndex;

import org.osmdroid.wms.WMSEndpoint;
import org.osmdroid.wms.WMSLayer;
import org.osmdroid.wms.WMSParser;
import org.osmdroid.wms.WMSTileSource;

import java.io.File;


/**
  * MlitTileSource
  * 国土交通省 ＷＭＳ
  * http://nlftp.mlit.go.jp/webmapc/help.html#N3
  */
public class MlitTileSource extends WMSTileSource {

        // debug
	private final static boolean D = true;
    private final static String TAG = "OSM";
    private final static String TAG_SUB = "MlitTileSource";


    // query
    private static final String NAME = "MLIT WMS";

    private static final String[] BASE_URL = { "http://nrb-www.mlit.go.jp/webmapc/gis/webmap/wms?SERVICE=WMS&" };
                                                              
    private static final String VERSION = "1.1.1";
    private static final String SRS = "EPSG%3A4326"  ; 
    private static final String STYLE = "";
    private static final int TILE_SIZE = 256;
  

    // 平成２３年度行政区域 都道府県界
    private static final String LAYER_BORDER = "N03-120331_100"  ; 


    /**
     * Constructor
     */
public MlitTileSource(String aName, String[] aBaseUrl, String layername, String version, String srs, String style, int size) {
        super( aName, aBaseUrl, layername, version, srs,  style,  size );
} // MlitTileSource

/**
 * create
 */ 
public static MlitTileSource create() {
     MlitTileSource r = new MlitTileSource(NAME, BASE_URL,  LAYER_BORDER, VERSION, SRS, STYLE, TILE_SIZE );
    return r;
} // create


/**
 * getTileURLString
 */ 
@Override
public String getTileURLString(final long pMapTileIndex) {
String url = super.getTileURLString(pMapTileIndex);
  StringBuilder sb = new StringBuilder(url);
    // sb.append( "&FORMAT=image%2Fpng" ) ;
    // sb.append( "&BGCOLOR=%23FFFFFF" ) ;
    sb.append( "&TRANSPARENT=TRUE" ) ;
    // sb.append(  "&EXCEPTION=XML" ) ;
    String ret = sb.toString();
        log_d( ret );
        return ret;
} // getTileURLString


/**
 * write into logcat
 */ 
private  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

}  // class MlitTileSource
