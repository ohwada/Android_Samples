/**
 *  Voice Recorder Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.voicerecorder1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * class MainActivity 
 */
public class MainActivity extends Activity {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Voice";
    private final static String TAG_SUB = "MainActivity";


/**
 * Color 
 */
    private final static  int COLOR_ROYAL_BLUE = Color.parseColor("#804169e1");

    private final static  int COLOR_VALID = COLOR_ROYAL_BLUE;
    private final static  int COLOR_INVALID = Color.TRANSPARENT;


/**
 * AudioPerm 
 */
    private AudioPerm mAudioPerm;


/**
 * VoiceRecorder 
 */
    private VoiceRecorder mVoiceRecorder;


/**
 * AudioPlayer 
 */
    private AudioPlayer mAudioPlayer;


/**
 * FileUtil
 */
    private FileUtil mFileUtil;


/**
 * LinearLayout
 */
   private LinearLayout mPlayerControl;

/**
 * Button
 */
    private Button mButtonRecord;

    private Button mButtonPlay;
    private Button mButtonPause;
    private Button mButtonReset;

/**
 * ListView
 */
    private ListView mListView ;

/**
 * ListAdapter for ListView
 */
   	private ListAdapter mAdapter;

/**
 * List for ListAdapter
 */
	private List<String> mList;


/**
 * Flag whether recording or not 
 */
   private boolean isRecording = false;


/**
 * Flag whether save in MP3 or not 
 * true : MP3
 * false : 3GP
 */
   private boolean useMp3 = false;


/**
 * onCreate
 */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            mListView = (ListView) findViewById(R.id.list);

            mPlayerControl = (LinearLayout) findViewById(R.id.playerControl);

        Button btnSetting = (Button) findViewById(R.id.Button_setting);
    btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSettingDialog();
                }
            }); // btnSetting

            mButtonRecord = (Button) findViewById(R.id.Button_record);
            mButtonRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    procRecord();
                }
            }); // mButtonRecord

        mButtonPlay = (Button) findViewById(R.id.button_play);
            mButtonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlayer();
                }
            }); // mButtonPlay

        mButtonPause = (Button) findViewById(R.id.button_pause);
            mButtonPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   pausePlayer();
                }
            }); // mButtonPause

        mButtonReset = (Button) findViewById(R.id.button_reset);
           mButtonReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopPlayer();
                }
            }); // mButtonReset

        setPlayerControlBackgroundColor(COLOR_INVALID);

        mList = new ArrayList<String>();

		mAdapter = new ListAdapter( this, ListAdapter.LAYOUT_RESOURCE_ID, mList );
		mListView.setAdapter( mAdapter );
		mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	 		@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				procItemClick( position, id );
			}
		}); // setOnItemClickListener

            mAudioPlayer = new AudioPlayer();
            mAudioPlayer = new AudioPlayer();
            mAudioPlayer.setCallback(new AudioPlayer.PlayerCallback() {
                @Override
                public  void onPrepared(int duration) {
                    // procPrepared(duration);
                }
                @Override
                public void onCompletion() {
                    procCompletion();
                }
                @Override
                public void onUpdatePosition(int position) {
                    // procUpdatePosition(position);
                }
}); // AudioPlayer.PlayerCallback

            mAudioPerm = new AudioPerm(this);
            mVoiceRecorder = new VoiceRecorder(this);
            mFileUtil = new FileUtil(this);

    } // onCreate


/**
 * onResume
 */ 
    @Override
    protected void onResume() {
        super.onResume();
        log_d("onResume");
        mAudioPerm.requestAudioPermissions();
        showList();
} // onResume


/**
 * onPause
 */ 
    @Override
    protected void onPause() {
        log_d("onPause");
        super.onPause();
        if(isRecording) {
            stopRecord();
        }
} // onPause


/**
 * onRequestPermissionsResult
 */ 
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        mAudioPerm.onRequestPermissionsResult(requestCode, permissions,  grantResults); 
} // onRequestPermissionsResult


/** 
 *   showList
 */
private void showList() {
        mList = mFileUtil.getFileNameListInExternalFilesDir();
    mAdapter.clear();
    mAdapter.addAll(mList);
    mAdapter.notifyDataSetChanged();
    mListView.invalidate();
} // showList


/** 
 *  procItemClick
 */
private void procItemClick( int position, long id ) {
		String msg = "procItemClick: " + position + ", " + id;
		log_d(msg );

		// header footer
		if ( id == -1 )  return;
		// check position
		if (( position < 0 )||( position >= mList.size() )) return;

        String item = mList.get( position );
        // preparePlayer(item);
        showFileDialog(item);

} // procItemClick


 /**
 *  showFileDialog
 */
private void showFileDialog(final String fileName) {

    new AlertDialog.Builder(this)
        .setTitle( R.string.file_dialog_title )
        .setMessage( fileName )

    .setPositiveButton(R.string.button_play, new  DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               preparePlayer(fileName);
            }
        }) // setPositiveButton

    .setNegativeButton(R.string.button_delete, new  DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               deleteVoiceFile(fileName);
            }
        }) // setNegativeButton

    .setNeutralButton(R.string.button_cancel, null )
        .show();

} // showFileDialog


 /**
 *  deleteVoiceFile
 */
private void deleteVoiceFile(String fileName) {

        boolean ret = mFileUtil.deleteFileInExternalFilesDir(fileName);
        showList();
        if (ret) {
            showToast("delete Successful");
        }

} // deleteVoiceFile


 /**
 *  preparePlayer
 */
private void preparePlayer(String fileName) {
    log_d("preparePlayer");
    if( mAudioPlayer.isPlaying() ) {
        showToast("please STOP");
        return;
    }

        String path = mFileUtil.getPathInExternalFilesDir(fileName);
        mAudioPlayer.prepare(path);

    setPlayerControlBackgroundColor(COLOR_VALID);
    showToast(fileName);
} // preparePlayer


 /**
 *  startPlayer
 */
private void startPlayer() {
log_d("startPlayer");
    mAudioPlayer.start();
} // startPlayer


 /**
 *  pausePlayer
 */
private void pausePlayer() {
log_d("pausePlayer");
    mAudioPlayer.pause();
} // pausePlayer


 /**
 *  stopPlayer
 */
private void stopPlayer() {
    log_d("stopPlayer");
    mAudioPlayer.stop();
    setPlayerControlBackgroundColor(COLOR_INVALID);
} // stopPlayer


 /**
 * setPlayerControlBackgroundColor
 */
private void setPlayerControlBackgroundColor(int color) {

mPlayerControl.setBackgroundColor(color);
} // ssetPlayerControlBackgroundColor


 /**
 *  procCompletion
 */
private void procCompletion() {
    log_d("procCompletion");
    setPlayerControlBackgroundColor(COLOR_INVALID);
} // procCompletion


 /**
 *  procRecord
 */
private void procRecord() {
     if(isRecording) {
        isRecording = false;
        stopRecord();
    } else {
        isRecording = true;
        startRecord();
    }
} // procVideo


 /**
 *  startRecord
 */
private void startRecord() {
    log_d("startRecord");
    mVoiceRecorder.start(useMp3);
    mButtonRecord.setText(R.string.button_stop);
    showToast("start Recording");
} // startRecord


 /**
 *  stopRecord
 */
private void stopRecord() {
    log_d("stopRecord");
    File file = mVoiceRecorder.getFile();
    mVoiceRecorder.stop();
    mButtonRecord.setText(R.string.button_record);
    showList();
    if(file != null ) {
        showToast("saved: " + file.toString());
    }
} // stopRecord



/**
  * showSettingDialog
 */
private void showSettingDialog() {
String item1 = "save as MP3";
        final CharSequence[] items = {item1};
        final boolean[] checkedItems = {useMp3};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Setting");
        builder.setMultiChoiceItems(
            items,
            checkedItems,
            new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog,
                                    int which, boolean flag) {
                        procSettingMultiChoiceClick( which, flag);
                }
            }); // setMultiChoiceItems

        builder.setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                        // nop
                }
            }); // setPositiveButton

        builder.create().show();
} // showSettingDialog


/**
  * procSettingMultiChoiceClick
 */
private void procSettingMultiChoiceClick( int which, boolean flag) {
    switch(which) {
        case 0:
            useMp3 = flag;
            break;  
    }
} // procSettingMultiChoiceClick


/**
 * showToast
 */
private void showToast( String msg ) {
		ToastMaster.makeText( this, msg, Toast.LENGTH_LONG ).show();
} // showToast


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class MainActivity
