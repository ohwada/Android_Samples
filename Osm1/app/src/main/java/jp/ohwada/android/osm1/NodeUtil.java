/**
 * Osm Sample
 * Beef Bowl Map
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osm1;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import  org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import jp.ohwada.android.osm1.model.*;


/**
 *  class NodeUtil
 */
public class NodeUtil  {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "NodeUtil";

    	private final static String FILE_NAME = "beef_bowl.json";

    	private final static String LF  = "\n";

	 private AssetManager mAssetManager;

    private NodeHelper mHelper;

/**
 *  constractor
 */
public NodeUtil(Context context) {
	mAssetManager = context.getAssets();
    mHelper = new NodeHelper(context);
} // NodeUtil


/**
 *  setupNode
 * parse Json in aseets fonlder
 * and insert record
 */
    public long setupNode(){

    long count = mHelper.getCountAll();
    log_d("already recors: " +  count);
    // skip, if already records
    if (count > 0) return 0;

List<Element> elements = parseJson();
if ( (elements == null)||(elements.size()==0) ) {
    log_d("can not parse json");
    return -1;
}

// inset recors
for (Element ele: elements) {
    log_d( ele.toString() );
        NodeRecord r = new NodeRecord( ele.getName(), ele.getLat(), ele.getLon(), ele.getInfo() );
        mHelper.insert( r );
    } // for

    count = mHelper.getCountAll();
    log_d("inserted recors: " +  count);
    return count;

} // ssetupNode


/**
 *  parseJson
 * parse Json in aseets fonlder
 */
private List<Element> parseJson() {
    String text = readAsset(FILE_NAME);

    Gson gson = new Gson();

List<Element> elements = null;
try {
        OsmJson osm = gson.fromJson( text, OsmJson.class);
        log_d(osm.toString());
        elements = osm.getElements();
    } catch (Exception e){
			if (D) e.printStackTrace();
    }

    return elements;
} // parseJson


/**
 *  getMarkers
 *  create Markers from NodeRecords
 */
    public List<OverlayItem> getMarkers(double lat, double lon, int distance ) {

    List<OverlayItem> items = new ArrayList<>();

  List<NodeRecord> records = mHelper.serchNode( lat, lon, distance );

    // create markers
    for(NodeRecord r: records) {
			items.add(new OverlayItem(r.name,  r.info, new GeoPoint(r.lat, r.lon)));
    }

    return items;
} // getMarkers


 	/**
	 *  readAsset
	 */ 
private  String readAsset(String fileName ) {

    StringBuilder builder = new StringBuilder();

InputStream is = null;
BufferedReader br = null;

try {
    try {
        is = mAssetManager.open(fileName);
        br = new BufferedReader(new InputStreamReader(is));

        String str;
        while ((str = br.readLine()) != null) {
         builder.append(str);
         builder.append(LF);
        }
    } finally {
        if (is != null) is.close();
        if (br != null) br.close();
    }
    } catch (Exception e){
			if (D) e.printStackTrace();
    }

    return builder.toString();
    } // eadAsset



 	/**
	 * write into logcat
	 */ 
	private  void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} // class NodeUtil
