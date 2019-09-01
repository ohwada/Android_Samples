/**
 * Cloud Vision Sample
 * ImageUrlUtil
 * 2019-02-01 K.OHWADA
 */
package jp.ohwada.android.cloudvision3;


import android.content.Context;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ohwada.android.cloudvision3.util.FileUtil;


/**
 * class ImageUrlUtil
 */
public class ImageUrlUtil {

/**
 * File Name of csv file in Assets folder
 */
        private final static String FILE_NAME = "sample.csv";

/**
 * Regular Expression of URL
 */
        private final static String REGEXP_URL = "^https?://[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+$";

/**
 * utility for File
 */ 
      private FileUtil mFileUtil;


/** 
 * constractor
 */
public ImageUrlUtil(Context context) {
        mFileUtil = new FileUtil(context);
}


/** 
 *  createFromCsv
 */
public List<ImageUrlItem> createFromCsv() {

    InputStream is = mFileUtil.getAssetsInputStream( FILE_NAME );
    if( is == null) return null;

    boolean IS_HEADER = false;
    List<String[]> list_csv = CsvUtil.readCsv(is, IS_HEADER );
    if( list_csv == null) return null;

    List<ImageUrlItem> list = new ArrayList<ImageUrlItem>();
    for(String[] arr:  list_csv) {
            if(arr.length < 2 ) continue;
            String name = arr[0];
            String url = arr[1];

            ImageUrlItem item = new ImageUrlItem( name.trim(), url.trim() );
            list.add( item );
    } // for
    return list;

} // createFromCsv


/** 
 *  checkUrlFormat
 */
public static boolean checkUrlFormat(String url) {

        if(url == null) return false;
        Pattern p = Pattern.compile(REGEXP_URL);
        Matcher m = p.matcher(url);
        boolean ret = m.find();
        return ret;

} // checkUrlFormat


} // class class ImageUrlUtil
