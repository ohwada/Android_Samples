/**
 * Camera2 Sample
  * VideoParam
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera213.util;


import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;

import java.io.File;


/**
  *  class VideoParam
  *  Parameters for MediaRecoder
  */
public class VideoParam {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "VideoParam";

/**
  *  File prefix for output file
  */
    public static final String VIDEO_FILE_PREFIX = "video_";


/**
  *  File extension  for MPEG4
  */
    public static final String VIDEO_FILE_EXT_MP4 = ".mp4";


/**
  *  File extension  for 3GPP
  */
    public static final String VIDEO_FILE_EXT_3GP = ".3gp";


/**
  *  File extension  for WEBM
  */
    public static final String VIDEO_FILE_EXT_WEBM = ".webm";


/**
  *  Constant for VideoEncoder
  */
// Value: 0
    public  static final int VIDEO_ENCODER_DEFAULT
= MediaRecorder.VideoEncoder.DEFAULT;
// Value: 1
    public  static final int VIDEO_ENCODER_H263
= MediaRecorder.VideoEncoder.H263;
// Value: 2
    public  static final int VIDEO_ENCODER_H264 
= MediaRecorder.VideoEncoder.H264;
  // Value: 3
    public  static final int VIDEO_ENCODER_MPEG_4_SP
= MediaRecorder.VideoEncoder.MPEG_4_SP;
// Value: 4 : Added in API level 21
    public  static final int VIDEO_ENCODER_VP8
= MediaRecorder.VideoEncoder.VP8;
// Value: 5 : Added in API level 24
    public  static final int VIDEO_ENCODER_HEVC
= MediaRecorder.VideoEncoder.HEVC;


/**
  *  Constant for OutputFormat
  */
//Value: 0
    public  static final int VIDEO_OUTPUT_FORMAT_DEFAULT 	= MediaRecorder.OutputFormat.DEFAULT;
// Value: 1
    public  static final int VIDEO_OUTPUT_FORMAT_THREE_GPP
= MediaRecorder.OutputFormat.THREE_GPP;
// Value: 2
	public  static final int VIDEO_OUTPUT_FORMAT_MPEG_4 = MediaRecorder.OutputFormat.MPEG_4;
// Value: 3
	public  static final int VIDEO_OUTPUT_FORMAT_AMR_NB = MediaRecorder.OutputFormat.AMR_NB;
// Value: 4
	public  static final int VIDEO_OUTPUT_FORMAT_AMR_wB = MediaRecorder.OutputFormat.AMR_WB;
// Value: 6
	public  static final int VIDEO_OUTPUT_FORMAT_AAC_ADTS = MediaRecorder.OutputFormat.AAC_ADTS;
// Value: 8 : Added in API level 26
	public  static final int VIDEO_OUTPUT_FORMAT_MPEG_2_TS = MediaRecorder.OutputFormat.MPEG_2_TS;
// Value: 9 : Added in API level 21
	public  static final int VIDEO_OUTPUT_FORMAT_WEBM = MediaRecorder.OutputFormat.WEBM;


 /**
  *  Constant for VideoSource
  */  
// Value: 0
     public final static int VIDEO_SOURCE_DEFAULT
    = MediaRecorder.VideoSource.DEFAULT;
// Value: 1
     public final static int VIDEO_SOURCE_CAMERA
    = MediaRecorder.VideoSource.CAMERA;
// Value: 2
     public final static int VIDEO_SOURCE_SURFACE
    = MediaRecorder.VideoSource.SURFACE;

 /**
  *  Constant for AudioSource
  */  
    // Value: 0
    public  static final int AUDIO_SOURCE_DEFAULT = MediaRecorder.AudioSource.DEFAULT;
    // Value: 1
    // require RECORD_AUDIO permission
    public  static final int AUDIO_SOURCE_MIC = MediaRecorder.AudioSource.MIC;


 /**
  *  Constant for AudioEncoder
  */  
//Value: 0
    public  static final int AUDIO_ENCODER_DEFAULT 
    = MediaRecorder.AudioEncoder.DEFAULT;
// Value: 1
    public  static final int AUDIO_ENCODER_AMR_NB
= MediaRecorder.AudioEncoder.AMR_NB;
// Value: 2
    public  static final int AUDIO_ENCODER_AMR_WB
= MediaRecorder.AudioEncoder.AMR_WB;
// Value: 3 
    public  static final int AUDIO_ENCODER_AAC = MediaRecorder.AudioEncoder.AAC;

 /**
  *  Constant for AudioChannels
  */  
   public static final int AUDIO_CHANNELS_MONO = 1;
    public static final int AUDIO_CHANNELS_STEREO = 2;


 /**
  *  Constant for Video Bit Rate
  */  
     private final static int VIDEO_DEFAULT_BIT_RATE = 10000000;


 /**
  *  Constant for Video frame Rate
  */  
     private final static int VIDEO_DEFAULT_FRAME_RATE = 30;


 /**
  *  Constant for Sensor Orientation
  */ 
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

// char LF
    private static final String LF = "\n";


/**
  *  AudioSource: default not use
  */
    private int mAudioSource = AUDIO_SOURCE_DEFAULT;


/**
  *  AudioEncoder: default not use
  */
    private int mAudioEncoder = AUDIO_ENCODER_DEFAULT;


/**
  * OutputFormat: default MPEG4
  */
    private int mOutputFormat  = VIDEO_OUTPUT_FORMAT_MPEG_4 ;


/**
  * VideoEncoder: default H264 for MPEG4
  */
    private int mVideoEncoder = VIDEO_ENCODER_H264;


/**
  * VideoEncodingBitRate
  */
    private int mVideoEncodingBitRate = VIDEO_DEFAULT_BIT_RATE;


/**
  * VideoFrameRate
  */
    private int mVideoFrameRate = VIDEO_DEFAULT_FRAME_RATE;


/**
  * AudioChannels
  * default : not specify
  */
    private int mAudioChannels = 0;


/**
  * AudioSamplingRate
  * default : not specify
  */
    private int mAudioSamplingRate = 0;


/**
  * AudioEncodingBitRate
  * default : not specify
  */
    private int mAudioEncodingBitRate = 0;


/**
  * OutputFile: default null
  */
    private File mOutputFile;


/**
  *  setAudioSource
  */
    public void setAudioSource(int audioSource) {
        mAudioSource = audioSource;
    }

/**
  *  getAudioSource
  */
    public int getAudioSource() {
        return mAudioSource;
    }

/**
  *  setAudioEncoder
  */
    public void setAudioEncoder(int audioEncoder) {
        mAudioEncoder = audioEncoder;
    }

/**
  *  getAudioEncoder
  */
    public  int getAudioEncoder() {
        return mAudioEncoder;
    }

/**
  *  setVideoEncoder
  */
    public void setVideoEncoder(int videoEncoder) {
        mVideoEncoder = videoEncoder;
    }

/**
  *  getVideoEncoder
  */
    public int getVideoEncoder() {
        return mVideoEncoder;
    }

/**
  *  setOutputFormat
  */
    public void setOutputFormat(int outputFormat) {
        mOutputFormat = outputFormat;
    }

/**
  *  getOutputFormat
  */
    public int getOutputFormat() {
        return mOutputFormat;
    }

/**
  *  setVideoEncodingBitRate
  */
    public void setVideoEncodingBitRate(int bitRate) {
        mVideoEncodingBitRate = bitRate;
    }

/**
  *  getVideoEncodingBitRate
  */
    public int getVideoEncodingBitRate() {
        return mVideoEncodingBitRate;
    }


/**
  *  setVideoFrameRate
  */
    public void setVideoFrameRate(int bitRate) {
        mVideoFrameRate = bitRate;
    }

/**
  *  getVideoFrameRate
  */
    public int getVideoFrameRate() {
        return mVideoFrameRate;
    }





/**
  *  setAudioChannels
  */
    public void setAudioChannels(int audioChannels) {
        mAudioChannels = audioChannels;
    }

/**
  *  getAudioChannels
  */
    public int getAudioChannels() {
        return mAudioChannels;
    }

/**
  *  setAudioSamplingRate
  */
    public void setAudioSamplingRate(int samplingRate) {
        mAudioSamplingRate = samplingRate;
    }

/**
  *  getAudioSamplingRate
  */
    public int getAudioSamplingRate() {
        return mAudioSamplingRate;
    }

/**
  *  setAudioEncodingBitRate
  */
    public void setAudioEncodingBitRate(int bitRate) {
        mAudioEncodingBitRate = bitRate;
    }

/**
  *  getAudioEncodingBitRate
  */
    public int getAudioEncodingBitRate() {
        return mAudioEncodingBitRate;
    }

/**
  *  setOutputFile
  */
    public void setOutputFile(File outputFile) {
        mOutputFile = outputFile;
    }

/**
  *  getOutputFile
  */
    public File getOutputFile() {
        return mOutputFile;
    }

/**
  *  getOutputFile
  *  return the set File, if set
  *  otherwise return the default File
  */
    public File getOutputFile(Context context) {
        File outputfile = null;
        if( mOutputFile != null) {
            // return set File
            outputfile = mOutputFile;
        } else {
            // return the default File
            outputfile = getDefaultOutputFile(context) ;
        }
        return outputfile;
    }


/**
  *  getDefaultOutputFile
  */
    public File getDefaultOutputFile(Context context) {
        File file = FileUtil.getOutputFileInExternalFilesDir(context, VIDEO_FILE_PREFIX, VIDEO_FILE_EXT_MP4);
        return file;
    }


/**
  * getOrientationHint
  */
public static int getOrientationHint(int sensorOrientation, int displayRotation) {
    int orientation = 0;
    switch (sensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
               orientation = DEFAULT_ORIENTATIONS.get(displayRotation);
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                orientation = INVERSE_ORIENTATIONS.get(displayRotation);
                break;
        }
        log_d("OrientationHint: " + orientation);
    return orientation;
}

    public String toString() {
        StringBuilder sb = new StringBuilder();
          sb.append("VideoParam: ");
          sb.append("OutputFormat= ");
          sb.append(mOutputFormat);
          sb.append(", VideoEncoder= ");
          sb.append(mVideoEncoder);
          sb.append(", AudioSource= ");
          sb.append(mAudioSource);
          sb.append(", AudioEncoder= ");
          sb.append(mAudioEncoder);
          sb.append(", OutputFile= ");
          sb.append(mOutputFile);
        return sb.toString();
    }

/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class VideoParam

