/**
 * MIDI Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.midiplayersample1.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * FileBinary
 */
public class FileBinary {

// debug
    private final static boolean D = true;
	private final static String TAG = "Midi";
	private final static String TAG_SUB = "FileBinary";

	private Context mContext;
	
	/**
	 * constractor
	 * @param Context context
	 */						
	public FileBinary( Context context ) {
		mContext = context;
	}


	/**
	 * readBinaryFile
	 * @param String name
     * @return byte[]
	 */	
	public byte[] readBinaryFile( File file ) {
		log_d( "readBinaryFile" );
		FileInputStream is = null;
		try {
            is = new FileInputStream(file);
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		if ( is == null )	return null; 
		 byte[]  bytes =  readBinaryFile( is );

    try {
            if (is != null ) {
                is.close();
            }
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 

        return bytes;
	} // readBinaryFile


	/**
	 * readAssetFile
	 * @param String name
     * @return byte[]
	 */	
	public byte[] readAssetBinaryFile( String name ) {  
		log_d( "readAssetFile" );
		AssetManager as = mContext.getResources().getAssets();
		InputStream is = null;
		try {
			is = as.open( name );
		} catch (IOException e) {
			if (D) e.printStackTrace();
		} 
		if ( is == null )	return null; 
		return readBinaryFile( is );
	} // readAssetBinaryFile


    /** 
     * readBinaryFile
     * @param InputStream in
     * @return byte[]
     */  
    private byte[] readBinaryFile( InputStream in ) {  
    	int size = 0;
        byte[] data = new byte[1024];  
        ByteArrayOutputStream out = null;    
        try {  
            out = new ByteArrayOutputStream();  
            while ((size = in.read(data)) != -1 ) {  
                out.write(data, 0, size);  
            }
            return out.toByteArray();
        } catch (Exception e) {  
			if (D) e.printStackTrace();
        } finally {  
            try {  
                if (in != null) in.close();  
                if (out != null) out.close();  
            } catch (Exception e) {
    			if (D) e.printStackTrace();
            }  
        }
		return null;
    } 

	/**
	 * write log
	 * @param String msg
	 * @return void
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} 
					
}
