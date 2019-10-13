/**
 * MediaCodec and MediaMuxer Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.mediamuxer1;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * class EncodeAndMuxTest
 * 
 * Generate an MP4 file using OpenGL ES drawing commands.  Demonstrates the use of MediaMuxer
 * and MediaCodec with Surface input.
 * <p>
 * This uses various features first available in Android "Jellybean" 4.3 (API 18).  There is
 * no equivalent functionality in previous releases.
 * <p>
 * (This was derived from bits and pieces of CTS tests, and is packaged as such, but is not
 * currently part of CTS.)
 * original : https://bigflake.com/mediacodec/EncodeAndMuxTest.java.txt
 */
public class EncodeAndMuxTest  {

    // debug
    private static final String TAG = "EncodeAndMuxTest";

    //private static final boolean VERBOSE = false;           // lots of logging
    private static final boolean VERBOSE = true;           // lots of logging


    // parameters for the encoder
    public static final int FRAME_RATE = 15;               // 15fps

    private static final int NUM_FRAMES = 30;               // two seconds of video


    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding

    private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames

    private static final int TIMEOUT_USEC = 10000;  // 10 msec


    // RGB color values for generated frames
    private static final int TEST_R0 = 0;
    private static final int TEST_G0 = 136;
    private static final int TEST_B0 = 0;
    private static final int TEST_R1 = 236;
    private static final int TEST_G1 = 50;
    private static final int TEST_B1 = 186;

    // size of a frame, in pixels
    private int mWidth = -1;
    private int mHeight = -1;

    // encoder / muxer state
    private MediaCodec mEncoder;

    // MediaMuxer
    private MediaMuxer mMuxer;
    private int mTrackIndex;
    private boolean mMuxerStarted;

    // allocate one of these up front so we don't need to do it every time
    private MediaCodec.BufferInfo mBufferInfo;


    /**
     * Callback
     */
    public interface Callback {
        void onFinish();
    }

    private Callback mCallback;
    private Context mContext;

    private CodecInputSurfaceTest mInputSurfaceTest;


    /**
     * constractor
     */
    public EncodeAndMuxTest(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
        mInputSurfaceTest = new CodecInputSurfaceTest();
    }


/**
  * Tests encoding of AVC video from a Surface.  The output is saved as an MP4 file.
  */
public  void testEncodeVideoToMp4(int width, int height, int bitRate) {

        mWidth = width;
        mHeight = height;


        try {
            prepareEncoder(width, height, bitRate);

            mInputSurfaceTest.makeCurrent();

            for (int i = 0; i < NUM_FRAMES; i++) {
                // Feed any pending encoder output into the muxer.
                drainEncoder(false);


                // Generate a new frame of input.
                mInputSurfaceTest.publishSurfaceFrame(i);


                // Submit it to the encoder.  The eglSwapBuffers call will block if the input
                // is full, which would be bad if it stayed full until we dequeued an output
                // buffer (which we can't do, since we're stuck here).  So long as we fully drain
                // the encoder before supplying additional input, the system guarantees that we
                // can supply another frame without blocking.
                if (VERBOSE) Log.d(TAG, "sending frame " + i + " to encoder");

            }

            // send end-of-stream to encoder, and drain remaining output
            drainEncoder(true);
        } finally {
            // release encoder, muxer, and input Surface
            releaseEncoder();
        }

        // To test the result, open the file with MediaExtractor, and get the format.  Pass
        // that into the MediaCodec decoder configuration, along with a SurfaceTexture surface,
        // and examine the output with glReadPixels.
}


 /**
  * Configures encoder and muxer state, and prepares the input Surface.
  */
private void prepareEncoder(int width, int height, int bitRate) {

        if (VERBOSE) Log.d(TAG, "prepareEncoder");

        mBufferInfo = new MediaCodec.BufferInfo();



        mEncoder = createMediaCodec(width, height, bitRate);

        Surface surface = mEncoder.createInputSurface();
        mInputSurfaceTest.setup(surface);
        mInputSurfaceTest.setParam(width, height);

        mEncoder.start();

        // Output filename.  
        String outputPath = createOutputFilePath(width, height);
        if (VERBOSE) Log.d(TAG, "output file is " + outputPath);


        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        try {
            mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException ioe) {
            throw new RuntimeException("MediaMuxer creation failed", ioe);
        }

        mTrackIndex = -1;
        mMuxerStarted = false;
}


/**
  * createMediaCodec
 */
private MediaCodec createMediaCodec(int width, int height, int bitRate) {

        MediaFormat format = createMediaFormat(width, height, bitRate);

        MediaCodec codec = null;
        try {
            codec = MediaCodec.createEncoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        return codec;
}


/**
  * createMediaFormat
 */
private MediaFormat createMediaFormat(int width, int height, int bitRate) {
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        return format;
}


/**
  * createOutputFilePath
 */
private String createOutputFilePath(int width, int height) {

        if (VERBOSE) Log.d(TAG, "createOutputFilePath");
        String fileName = "test." + width + "x" + height + ".mp4";
        File dir = mContext.getExternalFilesDir(null);
        File file = new File(dir, fileName );
        String outputPath = file.toString();
        return outputPath;
}


 /**
  * Releases encoder resources.  May be called after partial / failed initialization.
  */
private void releaseEncoder() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        stopMuxer();

        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mInputSurfaceTest != null) {
            mInputSurfaceTest.release();
            mInputSurfaceTest = null;
        }
        if (mCallback != null) {
            mCallback.onFinish();
        }
}


/**
 * stopMuxer
 */ 
private void stopMuxer() {

    try {
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    } catch (Exception ex) {
            // nop
    }
}


/**
  * Extracts all pending data from the encoder.
  * <p>
  * If endOfStream is not set, this returns when there is no more data to drain.  If it
  * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
  * Calling this with endOfStream set should be done once, right before stopping the muxer.
  */
private void drainEncoder(boolean endOfStream) {

        if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
            mEncoder.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        while (true) {
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                Log.d(TAG, "encoder output format changed: " + newFormat);

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    if (VERBOSE) Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer");
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly");
                    } else {
                        if (VERBOSE) Log.d(TAG, "end of stream reached");
                    }
                    break;      // out of while
                }
            }
        } // while

} // drainEncoder


} // class EncodeAndMuxTest

