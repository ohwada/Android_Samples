/**
 * Osmdroid Sample
 * Bookmark
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid9;
 
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
 * BookmarkHelper
 */	
public class BookmarkHelper extends SQLiteOpenHelper {
 
	// dubug
    public final static boolean D = true; 
	public final static String TAG = "SQLite";
	private final static String TAG_SUB = "BookmarkHelper";

	/** database */
    private static final String DB_NAME = "osm1.db";
    private static final int DB_VERSION = 1;

	/** table */
    private static final String TBL_NAME = "bookmark";

    /** column */
	private static final String COL_ID = "_id";
	private static final String COL_TITLE = "title";
	private static final String COL_DESC = "description";
	private static final String COL_LAT = "lat";
	private static final String COL_LON = "lon";

	private	static final String[] COLUMNS =
			new String[]
			{ COL_ID, COL_TITLE, COL_DESC, COL_LAT,   COL_LON } ;

	/** query */
private static final String ORDER_BY_ID_ASC =  "_id asc" ;

    private static final String CREATE_SQL =
    	"CREATE TABLE IF NOT EXISTS " 
		+ TBL_NAME 
		+ " ( " 
		+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
		+ COL_TITLE + " TEXT, " 
		+ COL_DESC + " TEXT, "
		+ COL_LAT + " REAL, "
		+ COL_LON + " REAL )" ;

    private static final String DROP_SQL =
		"DROP TABLE IF EXISTS " + TBL_NAME ;

    /**
     * === constractor ===
     */		
    public BookmarkHelper(Context context) {
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
    public long insert( BookmarkRecord record ) {
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
	public int update( BookmarkRecord record  ) {
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
	public int delete( BookmarkRecord record ) {
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
public List<BookmarkRecord> getAllList() {
    return getList( null, ORDER_BY_ID_ASC, 0, 0 );
} // getAllRecordList


	/**
     * toString
     */ 
private String toString(double d) {
    return BigDecimal.valueOf(d).toPlainString();
} // toString


	/**
     * get record list
     */ 
public List<BookmarkRecord> getList( String where, String orderby, int limit, int offset ) {
		SQLiteDatabase db = getReadableDatabase();
		if ( db == null ) return null;
        Cursor c = getCursor(
			db, where,  orderby, buildLimit( limit, offset ) );
		if ( c == null ) return null;

		 List<BookmarkRecord> list = new ArrayList<BookmarkRecord>();
		int count = c.getCount();
		if ( count == 0 ) {
			log_d( "buildRecordList no data" );
			return list;
		}		
        c.moveToFirst();           
		for ( int i = 0; i < count; i++ ) {
		BookmarkRecord r = new BookmarkRecord( c.getInt(0), c.getString(1), c.getString(2), c.getFloat(3), c.getFloat(4) );
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
    private ContentValues buildValues(BookmarkRecord r ) {
		ContentValues v = new ContentValues();
        v.put( COL_TITLE, r.title );    
        v.put( COL_DESC, r.description);	    
        v.put( COL_LAT, r.lat);
        v.put( COL_LON, r.lon);	
		return v;
	} // buildValues


	/**
     * build where
     */ 
	private String buildWhereId( BookmarkRecord r ) {
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

} //  BookmarkHelper