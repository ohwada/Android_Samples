/**
 * HTML Sample
 * 2019-02-01 K.OHWADA 
 */
package jp.ohwada.android.html1;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * File Utility
 */
public class FileUtil {

	// dubug
	private final static boolean D = true; 
    private final static String TAG = "HTML";
	private final static String TAG_SUB = "FileUtil";


/**
  * Char 
 */		
    private final static String LF = "\n";


/**
  * Context 
 */	
	private Context mContext;

/**
  * AssetManager 
 */	
	private AssetManager mAssetManager;


		
/**
  * constractor 
 */	    
public FileUtil( Context context  ) {
		mContext = context;
		mAssetManager = context.getAssets();
} 
	

/**
  * read Text File in Assets folder
 */	
public String readTextInAssets(String fileName) {

    InputStream is = getAssetsInputStream(fileName );
    if(is == null) return null;

    List<String> list = readText(is);

    try {
        if (is != null) is.close();
    } catch (Exception e){
			e.printStackTrace();
    }

    String str = joinStringList(list, LF);
    return str;
}



/**
 * read Text File
 */ 
public List<String> readText(InputStream is) {

    List<String> list = new ArrayList<String>();
    String str;
    BufferedReader br = null;
    try {
        br = new BufferedReader(new InputStreamReader(is));
        while ((str = br.readLine()) != null) {
            list.add(str);
        }
    } catch (Exception e){
			e.printStackTrace();
    }

    try {
        if (br != null) br.close();
    } catch (Exception e){
			e.printStackTrace();
    }

    return list;
}

		
/**
 * getAssetsInputStream
 */ 	
	private InputStream getAssetsInputStream( String fileName ) {
		
		InputStream is = null;
		try {
			is = mAssetManager.open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	return is;
} //getAssetsInputStream


/**
 * joinStringList
 * join String List into single String
 */ 
private String joinStringList(List<String> list, String delimiter) {

StringBuffer sb = new StringBuffer();

    for(String s: list) {
        sb.append(s);
        sb.append(delimiter);
    }
    return sb.toString();
}


/**
 * write into logcat
 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class FileUtil