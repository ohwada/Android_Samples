/**
 * Osmdroid Sample
 * Bookmark
 * 2019-02-01 K.OHWADA 
 */

package jp.ohwada.android.osmdroid9;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * FileUtil
 */
public class FileUtil  {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSM";
    	private final static String TAG_SUB = "FileUtil";

                	private final static String[] CSV_HEADERS = new String[] { "Latitude", "Longitude", "Title", "Description" };

	// copy stream
	protected final static int EOF = -1;
	private final static int BUFFER_SIZE = 1024 * 4;

    private  Context mContext;
    private AssetManager mAssetManager;


/**
 * constractor
 */
public FileUtil(Context context) {
    mContext = context;
    mAssetManager = context.getAssets();
} // FileUtil


/**
 * writeCsv
 */
public boolean writeCsv(File file,   List<BookmarkRecord> records) {

    if(file == null) return false;

        boolean is_error = false;
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
            CSVWriter writer = new CSVWriter(fileWriter);
            writer.writeNext( CSV_HEADERS );
            for (BookmarkRecord r: records) {
                writer.writeNext( r.getWriteRow() );
            } // for
        }catch (Exception ex) {
            is_error = true;
            if (D )ex.printStackTrace();
        } finally {
                try {
                     if (fileWriter!=null) fileWriter.close();
                }catch (Exception ex) {
                    if (D )ex.printStackTrace();
                }
        }
        return !is_error;

} // writeCsv

/**
 * readCsv
 */
public List<BookmarkRecord> readCsv(File file) {

    if(file == null) return null;

    List<BookmarkRecord> list = new ArrayList<BookmarkRecord>();

        FileReader fileReader = null;
        CSVReader reader = null;
        try {
            fileReader = new FileReader(file);
            reader = new CSVReader(fileReader);
            // first line is header
            String[] nextLine = reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                double lat = parseDouble(nextLine[0]);
                double lon = parseDouble(nextLine[1]);
                String title = nextLine[2];
                String description = nextLine[3];

                // add record
                BookmarkRecord r = new BookmarkRecord(title, description, lat, lon);
                list.add(r);
            } // while
    }catch (Exception ex) {
                    if(D) ex.printStackTrace();
    } finally {
            try {
                    if(fileReader != null) fileReader.close();
                    if(reader != null) reader.close();
            }catch (Exception ex) {
                    if(D) ex.printStackTrace();
            }
    }
    return list;
} // readCsv


 	/**
	 * setupFile
	 */ 
public boolean copyAssetToStrage( String dirName,  String fileName) {

        File root_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() );
File sub_dir = new File(root_dir, dirName);
        if ( !sub_dir.exists() ) {
            sub_dir.mkdir();
        }
File file = new File(sub_dir, fileName );
    FileOutputStream fos  = getFileOutput( file ) ;
    InputStream is = getAssetInputStream( fileName );
   return copyStream(  is,  fos );

} // copyAssetToStrage

/**
 * getAssetInputStream
 */
private InputStream getAssetInputStream(String fileName ) {
		
		InputStream is = null;
		try {
			is = mAssetManager.open( fileName );
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		
	return is;
} //getAssetInputStream

/**
 * getFileOutput
 */
private FileOutputStream getFileOutput( File file ) {

    FileOutputStream fos = null;
    try{
        boolean append = false;
        fos = new FileOutputStream(file, append);
    } catch (Exception e) {
                        e.printStackTrace();
    }
    return fos;
} // getFileOutput

/**
 * copyStream
 */
private boolean copyStream( InputStream is, OutputStream os ) {

			if (is == null) return false;
			if (os == null) return false;
			
		byte[] buffer = new byte[BUFFER_SIZE];
		int n = 0;	
		boolean is_error = false;
		try {
			// copy input to output		
			while (EOF != (n = is.read(buffer))) {
				os.write(buffer, 0, n);
			} // while
		} catch (IOException e) {
			is_error = true;
			if (D) e.printStackTrace();
	}
	return ! is_error;
}	// copyStream

/**
 * parseDouble
 */
private double parseDouble( String str ) {

            double d = 0;
                try {
                   d = Double.parseDouble( str );
                } catch (Exception ex) {
			        if (D) ex.printStackTrace();
                }

    return d;
} // parseDouble

/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class FileUtil
