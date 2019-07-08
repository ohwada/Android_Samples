/**
 *  Voice Recorder Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.voicerecorder1;


import android.content.Context;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
  * VoiceRecorder
  */
public class VoiceRecorder {


/**
  * for File Name
  */
    private static final String FILE_PREFIX = "voice_";

    private static final String FILE_EXT_3GP = ".3gp";
    private static final String FILE_EXT_MP3 = ".mp3";


/**
  * for MediaRecorder
  */
    private static final int AUDIO_CHANNELS_MONO = 1;
    private static final int AUDIO_CHANNELS_STEREO = 2;

/**
  * for CurrentDateTime
  */
    private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss_SSS";

/**
  * Context
  */
    private Context mContext;

/**
  * MediaRecorder
  */
    private MediaRecorder mMediaRecorder;

/**
  * Output File
  */
    private File mFile;


/**
  * constractor
  */
public VoiceRecorder(Context context) {
    mContext = context;
}


/**
  * start to recording
  */
public void start(boolean is_mp3) {
    mFile = createOutputFile(mContext, is_mp3);
    mMediaRecorder = createMediaRecorder(mFile.toString(), is_mp3);
    try {
        mMediaRecorder.prepare();
        mMediaRecorder.start();
    } catch (IOException e) {
            e.printStackTrace();
    } catch(IllegalStateException ex) {
            ex.printStackTrace();
    }
} // start


/**
  * stop
  */
public void stop() {
    if( mMediaRecorder == null) return;
    try{
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
    } catch(IllegalStateException ex) {
                ex.printStackTrace();
    }
} // stop


/**
  * getFile
  */
public File getFile() {
    return mFile;
}


/**
  * createMediaRecorder
  */
private MediaRecorder createMediaRecorder(String outputFile, boolean is_mp3) {

    int outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
    int audioEncoder = MediaRecorder.AudioEncoder.AMR_NB;
    if(is_mp3) {
        // strictly MPEG4 format
        // most media players play MP3 format
        outputFormat = MediaRecorder.OutputFormat.MPEG_4;
        audioEncoder = MediaRecorder.AudioEncoder.AAC;
    } 

// Note there is an order for specifying parameters
// setSource: call this only before setOutputFormat()
// setEncoder: call this after setOutputFormat() but before prepare().
        MediaRecorder mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(outputFormat);
            mediaRecorder.setAudioEncoder(audioEncoder);
            mediaRecorder.setOutputFile(outputFile);
        } catch(IllegalStateException ex) {
                ex.printStackTrace();
        }
        return mediaRecorder;

} // createMediaRecorder


/**
  * createOutputFile
  */
private File createOutputFile(Context context, boolean is_mp3) {

    String ext = FILE_EXT_3GP;
    if(is_mp3) {
        // MP3 extension
        ext = FILE_EXT_MP3;
    } 

        File dir = context.getExternalFilesDir(null);
        String filename = getOutputFileName(FILE_PREFIX, ext);
        File file = new File(dir, filename);
        return file;
}


/**
 * getOutputFileName
 */
public static String getOutputFileName(String prefix, String ext) {
            String  currentDateTime= getCurrentDateTime();
            String filename = prefix + currentDateTime + ext;
    return filename;
} // getOutputFileName

/**
  *getCurrentDateTime
 */
public static String getCurrentDateTime() {
   SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    String currentDateTime =  sdf.format(new Date());
    return currentDateTime;
}

} // class VoiceRecorder

