/**
 * Cloud Vision Sample
 * CsvUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision3;


import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * class CsvUtil
 * reference : http://opencsv.sourceforge.net/
 */
public class CsvUtil  {

    // debug
	private final static boolean D = true;
    private final static String TAG = "CloudVision";
    private final static String TAG_SUB = "CsvUtil";


/**
 * constractor
 */
public CsvUtil() {
    // no action
} // CsvUtil


/**
  * readCsv
 */
public static List<String[]> readCsv( InputStream is, boolean is_header ) {

        if( is == null ) return null;

        List<String[]> list = new ArrayList<String[]>();

        String[] nextLine;
        InputStreamReader isReader = null;
        CSVReader reader = null;
        try {
                isReader = new InputStreamReader( is );
                reader = new CSVReader( isReader );
                if (is_header ) {
                    // read first line
                    nextLine = reader.readNext();
                }
                while ((nextLine = reader.readNext()) != null) {
                        list.add(nextLine);
                } // while

        }catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
                if( is != null ) is.close();
                if( isReader != null ) isReader.close();
                if( reader != null ) reader.close();
        }catch (Exception ex) {
             ex.printStackTrace();
        }

        return list;
    } // readCsv


/**
 * write into logcat
 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class CsvUtil
