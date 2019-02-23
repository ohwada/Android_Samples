/**
 * Osm Sample
 * Beef Bowl Map
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osm1;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

 import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * NodeHelper
 */	
public class NodeHelper extends SQLiteOpenHelper {
 
	// dubug
    public final static boolean D = true; 
	public final static String TAG = "SQLite";
	private final static String TAG_SUB = "NodeHelper";

	/** database */
    private static final String DB_NAME = "osm1.db";
    private static final int DB_VERSION = 1;

	/** table */
    private static final String TBL_NAME = "node";

    /** column */
	private static final String COL_ID = "_id";
	private static final String COL_NAME = "name";
	private static final String COL_LAT = "lat";
	private static final String COL_LON = "lon";
	private static final String COL_INFO = "info";

	private	static final String[] COLUMNS =
			new String[]
			{ COL_ID, COL_NAME, COL_LAT,   COL_LON, COL_INFO } ;

	/** query */
private static final String ORDER_BY_ID_ASC =  "_id asc" ;

    private static final String CREATE_SQL =
    	"CREATE TABLE IF NOT EXISTS " 
		+ TBL_NAME 
		+ " ( " 
		+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
		+ COL_NAME + " TEXT, " 
		+ COL_LAT + " REAL, "
		+ COL_LON + " REAL, "
		+ COL_INFO + " TEXT )" ;

    private static final String DROP_SQL =
		"DROP TABLE IF EXISTS " + TBL_NAME ;

// Latitude longitude degree per 1 m
    private static final double LAT_DISTANCE = 9e-6;
    private static final double LON_DISTANCE = 11e-6;


    /**
     * === constractor ===
     */		
    public NodeHelper(Context context) {
        super( context, DB_NAME, null, DB_VERSION );
    }

 	/**
     * === onCreate ===
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( CREATE_SQL );
    } // onCreate
 
	/**
     * === onUpgrade ===
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL( DROP_SQL );
        db.execSQL( CREATE_SQL );
    } // onUpgrade
 
	/**
     * --- insert  ---
     */
    public long insert( NodeRecord record ) {
    	SQLiteDatabase db = getWritableDatabase();
    	if ( db == null ) return 0;
		long ret = db.insert( 
			TBL_NAME, 
			null, 
			buildValues( record ) );
		db.close();
		return ret;
    } // insert

	/**
     * --- update  ---
     */    	
	public int update( NodeRecord record  ) {
    	SQLiteDatabase db = getWritableDatabase();
    	if ( db == null ) return 0;
		int ret = db.update(
			TBL_NAME, 
			buildValues( record ),
			buildWhereId( record ), 
			null );
		db.close();
		return ret;
	} // update

	/**
     * --- delete  ---
     */ 			
	public int delete( NodeRecord record ) {
		return deleteWhere( buildWhereId( record ) );
	}

	/**
     * --- delete  ---
     */ 	
	public int delete( int id  ) {
		return deleteWhere( buildWhereId( id ) );
	} // delete

	/**
     * --- delete all ---
     */ 
	public int deleteAll() {
		return deleteWhere( "" );
	}

	/**
     * delete  for common
     */ 	
	public int deleteWhere( String where )  {
		SQLiteDatabase db = getWritableDatabase();
		if ( db == null ) return 0;
		int ret = db.delete(
			TBL_NAME, 
			where, 
			null );		
		db.close();
		return ret;	
	} //  deleteWhere


	/**
     * getCountAll
     */ 
    public long getCountAll() {
		SQLiteDatabase db = getWritableDatabase();
		if ( db == null ) return 0;
        return DatabaseUtils.queryNumEntries(db, TBL_NAME);
    } // getCountAll

	/**
     * get record list
     */ 
public List<NodeRecord> getAllList() {
    return getList( null, ORDER_BY_ID_ASC, 0, 0 );
} // getAllRecordList

 /**
 *  getList
 */ 

// 1m unit
public List<NodeRecord> serchNode(double lat, double lon, int distance ) {
    log_d( "serchNode: " + lat + " " + lon + " " + distance );
    double lat_distance = distance * LAT_DISTANCE;
    double lat_min = lat - lat_distance;
    double lat_max = lat + lat_distance;
 String str_lat_min = toString(lat_min);
 String str_lat_max = toString(lat_max);

    double lon_distance = distance * LON_DISTANCE;
    double lon_min = lon - lon_distance;
    double lon_max = lon + lon_distance;
 String str_lon_min = toString(lon_min);
 String str_lon_max = toString(lon_max);

    String where =  " ( " + COL_LAT + " > " +  str_lat_min  ;
    where +=  " AND " + COL_LAT + " < " + str_lat_max  ;
    where +=  " AND " + COL_LON + " > " +  str_lon_min  ;
    where +=  " AND " + COL_LON + " < " +   str_lon_max + " ) "  ;
    log_d("serchNode: " + where);

    return getList( where, ORDER_BY_ID_ASC, 0, 0 );
} // getList


	/**
     * toString
     */ 
private String toString(double d) {
    return BigDecimal.valueOf(d).toPlainString();
} // toString


	/**
     * get record list
     */ 
public List<NodeRecord> getList( String where, String orderby, int limit, int offset ) {
		SQLiteDatabase db = getReadableDatabase();
		if ( db == null ) return null;
        Cursor c = getCursor(
			db, where,  orderby, buildLimit( limit, offset ) );
		if ( c == null ) return null;

		 List<NodeRecord> list = new ArrayList<NodeRecord>();
		int count = c.getCount();
		if ( count == 0 ) {
			log_d( "buildRecordList no data" );
			return list;
		}		
        c.moveToFirst();           
		for ( int i = 0; i < count; i++ ) {
		NodeRecord r = new NodeRecord( c.getInt(0), c.getString(1), c.getFloat(2), c.getFloat(3), c.getString(4) );
			list.add( r );
			c.moveToNext();
 		} 		
		c.close();	
		db.close();	
		return list;
	} // getRecordList

	/**
     * build ContentValues
     */ 
    private ContentValues buildValues(NodeRecord r ) {
		ContentValues v = new ContentValues();
        v.put( COL_NAME, r.name );        
        v.put( COL_LAT, r.lat);
        v.put( COL_LON, r.lon);	
        v.put( COL_INFO, r.info);	
		return v;
	} // buildValues


	/**
     * build where
     */ 
	private String buildWhereId( NodeRecord r ) {
        return buildWhereId( r.id );
	} // buildWhereId

	/**
     * build where
     */ 
	private String buildWhereId( int id ) {
		String s = COL_ID + "=" + id ;
		return s;
	} // buildWhereId

	/**
     * getCursor
     */ 
	private Cursor getCursor( SQLiteDatabase db, String where, String orderby, String limit ) {		
		String[] param = null;
		String groupby = null;
		String having = null;				
       return db.query( 
        	TBL_NAME,
        	COLUMNS,
			where , 
			param, 
			groupby , 
			having, 
			orderby , 
			limit );
	} // getCursor


	/**
     * build limit
     */ 
	private String buildLimit( int limit, int offset ) {
		String limit_str = Integer.toString( limit );
		String offset_str = Integer.toString( offset );
		String str = null;
		if (( limit > 0 )&&( offset > 0 )) {
			str = limit_str+ ", " + offset_str;
		} else if (( limit > 0 )&&( offset== 0 )) {
			str = limit_str;
		} else if (( limit == 0 )&&( offset > 0 )) {
			str = "0, " + offset_str;
		}
        log_d("buildLimit: " + str);
		return str;
	} // buildLimit


 	/**
	 * write into logcat
	 * @param String msg
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

} //  NodeHelper