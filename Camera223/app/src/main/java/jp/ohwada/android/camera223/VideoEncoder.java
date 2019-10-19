/**
 * Camera2 Sample
 * VideoEncoder
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera223;


import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;



/**
 * class VideoEncoder
 */
public class VideoEncoder implements Runnable {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "VideoEncoder";


 /**
  * MIME_TYPE
  */
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding


 /**
  * TimeOut
  */
    // unit microseconds: 500ms
    private static final long INPUT_TIMEOUT = 500000;
    private static final long OUTPUT_TIMEOUT = 500000;

 /**
  * interface EncoderCallback
  */
    public interface EncoderCallback {
        void onOutput(byte[] data);
    }

    private static EncoderCallback mCallback;


/**
  * MediaCodec 
  */
   private static MediaCodec   mCodec ;


/**
  * OutputThread
 */
    private Thread mOutputThread;

    private  boolean isRunning = false;


/**
  * Video param
  */
    private  int mVideoWidth= 640;  // VGA
    private  int mVideoHeight = 480;

    private  int mFrameRate = 10;   // 10 fps
    private  int mBitRate = 1200000; // 1.2Mbps
    private  int mIframeInterval = 10;


/**
 * constractor
 */ 
public VideoEncoder()   {
    // nop
}

/**
   *  setCallback
  */
public void setCallback(EncoderCallback callback) {
    mCallback = callback;  
}


 /**
 *  setVideoSize
 */
public void setVideoSize(int width, int height) {
    mVideoWidth = width;
    mVideoHeight = height;
}

 /**
 *  setFrameRate
 */
public void setFrameRate(int frameRate) {
    mFrameRate = frameRate;
}

 /**
 *  setBitRate
 */
public void setBitRate(int bitRate) {
    mBitRate = bitRate;
}

 /**
 *  setIframeInterval
 */
public void setIframeInterval(int interval) {
    mIframeInterval = interval;
}


 /**
 *  start
 */
public void start() {
        log_d("start");
        try {
                mCodec = createMediaCodec(MIME_TYPE);
                mCodec.start();
        } catch (IllegalStateException e) {
            // nop
     }

        isRunning = true;
        mOutputThread = new Thread(this);
        mOutputThread.start();

}


/**
 * stop
 */ 
public void stop() {
    log_d("stop");     
    isRunning = false;
    try {
        if(mCodec != null){
            mCodec.stop();
            mCodec.release();
            mCodec = null;
        }
        if(mOutputThread != null){
            mOutputThread.join();
            mOutputThread = null;
        }
    } catch (Exception ex) {
            // nop
    }
} // stop


/**
 * setFrame
 */
public void setFrame(byte[] frame, long timestamp) {
    //log_d("setFrame");
    queueInputBuffer(frame, timestamp);
} // setFrame


 /**
 *  run
 */
public void run() {
    log_d("run");
    while(isRunning) {
            byte[] data = getOutputData();
            if((mCallback != null)&&( data!= null)) {
                mCallback.onOutput( data);
            }
    } // while   

} // run


/**
  * createMediaCodec
  */
public MediaCodec createMediaCodec(String mimeType) {

        log_d("createMediaCodec: " + mimeType);
        MediaFormat mediaFormat = createMediaFormat(mimeType, mVideoWidth, mVideoHeight, mFrameRate, mBitRate, mIframeInterval);

    // codecName: OMX.google.h264.encoder
    String codecName = getCodecName(mimeType);
    log_d("codecName: " + codecName);

    MediaCodec codec = null;
    try {
            codec = MediaCodec.createByCodecName(codecName);
            codec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    } catch (IOException e) {
            e.printStackTrace();
    }

    MediaFormat inputMediaFormat = codec.getInputFormat();
    int inputColorFormat = inputMediaFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
    // 19 : COLOR_FormatYUV420Planar
    log_d("input colorFormat: " + inputColorFormat);

        return codec;
}



/**
  * create a MediaFormat with the basic set of values.
  */
private MediaFormat createMediaFormat(String type, int width, int height, int frameRate, int bitRate, int iframeInterval) {
        log_d("createMediaFormat: " + type + " , " + width + " x " + height +  " , " + frameRate + " , "+ bitRate + " , "+ iframeInterval);
        MediaFormat format = MediaFormat.createVideoFormat(type, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iframeInterval);
        return format;
}


/**
  * getCodecName
  * 
  * Returns the first codec capable of encoding the specified MIME type, or null if no
   * match was found.
  */
private String getCodecName(String mimeType) {
        log_d("getCodecName");
        MediaCodecInfo mediaCodecInfo = null;
        int numCodecs = MediaCodecList.getCodecCount();
        // select Codec
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

    String codecName = mediaCodecInfo.getName();
    return codecName;
} // selectCodec


/**
 * queueInputBuffer
 * input data if can get InputBuffer
 */
private void queueInputBuffer(byte[] frame, long timestamp) {
    ByteBuffer inputBuffer = null;
    int index = -1;
    try {
		    index = mCodec.dequeueInputBuffer(INPUT_TIMEOUT);
			if (index>=0) {
                    inputBuffer = mCodec.getInputBuffer(index);
            }
    } catch (IllegalStateException e) {
            // nop
    }

    if(inputBuffer == null) return;

    int size = frame.length;
	inputBuffer.clear();
    inputBuffer.put(frame, 0 ,size);

    try {
	    mCodec.queueInputBuffer(index, 0, size, timestamp, 0);
    } catch (IllegalStateException e) {
            // nop
    }

} // queueInputBuffer


/**
 * getOutputData
 * callback the data if can get OutputBuffer
 */
private byte[] getOutputData() {

	 MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    int index = -1;
    ByteBuffer outputBuffer = null;
	try {
		    index = mCodec.dequeueOutputBuffer(bufferInfo, OUTPUT_TIMEOUT);
			if (index >= 0) {
                    outputBuffer = mCodec.getOutputBuffer(index);
            }
    } catch (IllegalStateException e) {
            // nop
    }

    if(outputBuffer == null) return null;

    int size = bufferInfo.size ;
    log_d("Index: "+ index+ " Time: " + bufferInfo.presentationTimeUs + " size: "+ size);

    byte[] outData = new byte[size];

	outputBuffer.position(0);
    outputBuffer.get(outData);

    mCodec.releaseOutputBuffer(index, false);

    return outData;
}


 /**
 *  getOutputFormat
 */
private MediaFormat getOutputFormat() {
    MediaFormat format = null;
    try {
                format = mCodec.getOutputFormat();
    } catch (IllegalStateException e) {
            // nop
    }
    return format;
}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class VideoEncoder 
