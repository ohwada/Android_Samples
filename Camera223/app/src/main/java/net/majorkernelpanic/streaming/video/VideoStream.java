/**
 * Changlog
 * 2019-08-01 K.OHWADA
 *  modify for Camera2 API
 */
package net.majorkernelpanic.streaming.video;


import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import net.majorkernelpanic.streaming.MediaStream;
import net.majorkernelpanic.streaming.Stream;
import net.majorkernelpanic.streaming.exceptions.CameraInUseException;
import net.majorkernelpanic.streaming.exceptions.ConfNotSupportedException;
import net.majorkernelpanic.streaming.exceptions.InvalidSurfaceException;

// import net.majorkernelpanic.streaming.gl.SurfaceView;

import net.majorkernelpanic.streaming.hw.EncoderDebugger;
import net.majorkernelpanic.streaming.hw.NV21Convertor;
import net.majorkernelpanic.streaming.rtp.MediaCodecInputStream;
import net.majorkernelpanic.streaming.video.VideoQuality;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;


import jp.ohwada.android.camera223.SettingsFragment;


/** 
 * VideoStream
 * Don't use this class directly.
 * original : net.majorkernelpanic.streaming.video.VideoStream
 */
public class VideoStream extends VideoStreamBase {


    // debug
	private final static boolean D = true;
    private final static String TAG = "Stream";
    private final static String TAG_SUB = "VideoStream";


    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding

    private static final int IFRAME_INTERVAL = 10;          // 10 

    // unit microseconds: 500ms
    private static final long INPUT_TIMEOUT = 500000;


/**
 * for Encoder Type
 */
    private final static String KEY_ENCODER =  SettingsFragment.KEY_ENCODER;

    public final static String[] VIDEO_ENCODER_TYPE_ARRAY = {"video/avc", "video/3gpp", "video/x-vnd.on2.vp8"};


    protected final static int COLOR_FORMAT =
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;


/** 
 * setPreviewFrame
 */
	private MediaCodec mMediaCodec;

	private boolean isPreviewRunning = false;

    protected Context mContext;


/** 
 * constractor
 */
	public VideoStream() {
        super();
		mQuality =  null;
	}


/** 
 * constractor
 */
	public VideoStream(int camera) {
		super(camera);
		mQuality =  null;
	}


/** 
 * setContext
 */
	public void setContext(Context context) {
		mContext = context;
	}


/** 
 * setPreviewFrame
 */
    public  void setPreviewFrame(byte[] data, long timestamp) {
        //log_d("setPreviewFrame");
        if(isPreviewRunning) {
                procPreviewFrame(data, timestamp);
        }
    }


/**
 * procPreviewFrame
 */ 
private void procPreviewFrame(byte[] data, long timestamp) {
//log_d("procPreviewFrame");
 
                ByteBuffer inputBuffer = null;
				try {
					    int index = mMediaCodec.dequeueInputBuffer(INPUT_TIMEOUT);
			            //log_d("input buffer  index; " + index);
					    if (index >= 0) {
                                inputBuffer = mMediaCodec.getInputBuffer(index);

                                int size = data.length;
				                inputBuffer.clear();
                                inputBuffer.put(data, 0, size);
						        mMediaCodec.queueInputBuffer(index, 0, size, timestamp, 0);
                                //log_d("queueInputBuffer: " + size);

					} else {
						    //log_d("No input buffer available !");
					}
		        } catch (Exception e) {
                        // nop
				}
				
}


/**
 *  setVideoQuality
 */
    @Override
	public void setVideoQuality(VideoQuality videoQuality) {
			mQuality = videoQuality.clone();
            log_d( "setVideoQuality: " + mQuality.toString());
	}


/**
 *  configure
 */
    @Override
	public synchronized void configure() throws IllegalStateException, IOException {
		super.configure();
		log_d("configure");
    }


/**
 *  setCamera
 */
    @Override
	public void setCamera(int camera) {
        // nop
    }

/**
 *  stop
 */
    @Override
    public synchronized void stop() {
	    log_d("stop");
        // nop
}

/**
 *  procStop
 */
    private void procStop() {
	    log_d("procStop");
        isPreviewRunning = false;
		if (mStreaming) {
			try {
                if(mPacketizer != null) {
                    mPacketizer.stop();
                }
                if(mMediaCodec != null) {
					mMediaCodec.stop();
					mMediaCodec.release();
					mMediaCodec = null;
                }
			} catch (Exception e) {
				// nop
			}	
			mStreaming = false;
        }
    } 




/**
 * Video encoding is done by a MediaRecorder.
 */
    @Override
	protected void encodeWithMediaRecorder() {
	    log_d("encodeWithMediaRecorder");
        encodeWithMediaCodecMethod1();
	}


/**
 * Video encoding is done by a MediaCodec.
 */
    @Override
	protected void encodeWithMediaCodec() {
			log_d("encodeWithMediaCodec");
			encodeWithMediaCodecMethod1();
    }


	/**
	 * Video encoding is done by a MediaCodec.
	 */
    @Override
	protected void encodeWithMediaCodecMethod1() {

		log_d("encodeWithMediaCodecMethod1");

		//EncoderDebugger debugger = EncoderDebugger.debug(mSettings, mQuality.resX, mQuality.resY);

        //String codecName = debugger.getEncoderName();
        //log_d("codecName: " + codecName);

        mMediaCodec = createMediaCodec(mMimeType, mQuality.resX, mQuality.resY);

        MediaFormat inputMediaFormat = mMediaCodec.getInputFormat();
        String mime = inputMediaFormat.getString(MediaFormat.KEY_MIME);
        //  video/raw
        log_d("input mime: " + mime);

		mMediaCodec.start();

		// The packetizer encapsulates the bit stream in an RTP stream and send it over the network
		mPacketizer.setInputStream(new MediaCodecInputStream(mMediaCodec));
		mPacketizer.start();
		mStreaming = true;
        isPreviewRunning = true;
}


/**
 * getEncoderType
 */	
private String getEncoderType() {

        // default : H264
        String strEncoder = mSettings.getString(KEY_ENCODER, "0");
        int numEncoder = parseInt(strEncoder);
        String encoderType = VIDEO_ENCODER_TYPE_ARRAY[numEncoder];
        return encoderType;
}


/**
 * parseInt
 */ 
private int parseInt(String str) {
        int val = 0;
        try {
                val = Integer.parseInt(str);
        } catch (NumberFormatException e) {
                e.printStackTrace();
        }
        return val;
}


/**
 * createMediaCodec
 */	
protected MediaCodec createMediaCodec(String mimeType, int width, int height) {
        log_d("createMediaCodec: " + mimeType + " , " + width + " x " + height);

        String codecName = getCodecName(mimeType);
        log_d("codecName: " + codecName);

        MediaFormat mediaFormat = createMediaFormat(mimeType, width, height, mQuality.framerate, mQuality.bitrate);
		MediaCodec mediaCodec = null;
		try {
		    mediaCodec = MediaCodec.createByCodecName(codecName);
		    mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaCodec;
}


/**
 * createMediaFormat
 */	
protected MediaFormat createMediaFormat(String type, int width, int height, int frameRate, int bitRate) {
        log_d("createMediaFormat: " + type + " , " + width + " x "+ height + " , " + frameRate  + " , " + bitRate);
		MediaFormat mediaFormat = MediaFormat.createVideoFormat(type, width, height);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);	
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,  COLOR_FORMAT);
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        return mediaFormat;
}


/**
  * getCodecName
  */
protected String getCodecName(String encoderType) {
    MediaCodecInfo mediaCodecInfo = selectCodec(encoderType);
    String codecName = mediaCodecInfo.getName();
    return codecName;
}


/**
  * selectCodec 
  */
protected MediaCodecInfo selectCodec(String mimeType) {
        log_d("selectCodec");
        MediaCodecInfo mediaCodecInfo = null;
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
       
            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                String type = types[j];
                if (type.equalsIgnoreCase(mimeType)) {
                    mediaCodecInfo = codecInfo;
                    //break;
                }
            } // for j
        } // for i
        return mediaCodecInfo;
} // selectCodec


	/**
	 * Video encoding is done by a MediaCodec.
	 * But here we will use the buffer-to-surface method
	 */	
    @Override
    protected void encodeWithMediaCodecMethod2()  {
        // nop
    }


/**
 * openCamera
 */
    @Override
    protected void openCamera() {
        // nop
    }

 /**
  * createCamera
  */
    @Override
    protected synchronized void createCamera() {
        // nop
    }


 /**
  * destroyCamera
  */
    @Override
	protected synchronized void destroyCamera() {
       stop();
    } 


 /**
  * updateCamera
  */
    @Override
	protected synchronized void updateCamera() throws RuntimeException {
        // nop
	} 


 /**
  * lockCamera
  */
    @Override
	protected void lockCamera() {
        // nop
	}


 /**
  * unlockCamera
  */
    @Override
	protected void unlockCamera() {
        // nop
	}


/**
 * measureFramerate
 */
    @Override
	protected void measureFramerate() {
		// nop
	}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class Camera2VideoStream
