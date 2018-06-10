/**
 * Media Player  Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.mediaplayermidisample;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * class FileSelector 
 */
public class FileSelector {

// debug
    private final static boolean D = true; 
	private final static String TAG = "MIDI";
	private final static  String TAG_SUB = "FileSelector ";

    private final static int LAUOYT_RESOURCE_ID = R.layout.list_item;
    private final static int TEXT_VIEW_RESOURCE_ID = R.id.TextView_item_name;

	private final static  String  MIDI_DIR_NAME = "midi";

    private final static int HEADER_FOOTER_ID = -1;

    private Context mContext;

    private ListView mListView ;

	private ArrayAdapter<String> mAdapter;

	private List<String> mList = new ArrayList<String>();

  // callback 
    private OnClickListener mListener;

            /*
     * callback interface
     */    
    public interface OnClickListener {
        public void onItemClick(File file);
    } // interface


/**
 * == constractor ==
 */
public FileSelector(Context context) {
        mContext = context;
} // FileSelector


    /*
     * setOnItemClickListener
     */ 
    public void setOnClickListener( OnClickListener listener ) {
        log_d("setOnClickListener");
        mListener = listener;
    } // setOnClickListener


/**
 * setListView
 */
public void setListView(ListView view) {
    mListView = view;
} // setListView

public void setup() {
    mAdapter = new ArrayAdapter<String>( mContext, LAUOYT_RESOURCE_ID,  TEXT_VIEW_RESOURCE_ID );
	mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener

} // setup


	/** 
	 *  procItemClick
	 * @param int position
	 * @param long id 
	 */
	private void procItemClick( int position, long id ) {
		String msg = "procItemClick: " + position + ", " + id;
		log_d(msg );
		// header footer
		if ( id == HEADER_FOOTER_ID )  return;
		
		// check position
		int n = position;
		if (( n < 0 )||( n >= mList.size() )) return;

        String item = mList.get( n );

        File file = getFile(item);

    if (mListener != null ) {
        mListener.onItemClick(file);
    }

	} // procItemClick

private File getFile(String name) {

   File midi_dir = getMidiDir();

        File input = new File(midi_dir, name);
        return input;
} //  getFile

    /**
     * showList
     */
public boolean showList() {

    log_d("showList");

   File midi_dir = getMidiDir();

    File[] files = midi_dir.listFiles();
    if ((files == null)||(files.length == 0)) {
        log_d("no files");
        return false;
    }

    mList = new ArrayList<String>();
    for (File f: files) {
        String name = f.getName();
        log_d( name );
        mList.add( name );
    }

    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();

    mListView.setAdapter(mAdapter);
        mListView.invalidateViews();
    return true;

} // showList


 	/**
	 * getMidiDir
	 */ 
private File getMidiDir() {
    File sd_dir = Environment.getExternalStorageDirectory();

   File music_dir = new File(sd_dir, Environment.DIRECTORY_MUSIC);

   File midi_dir = new File(music_dir, MIDI_DIR_NAME);
    return midi_dir;
} // getMidiDir

 	/**
	 * write into logcat
	 */ 
	private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d


} // class FileSelector 

