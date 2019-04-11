/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera24;


import android.content.Context;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;


/**
 * Runnable that saves an {@link Image} into the specified {@link File}, and updates
 * {@link android.provider.MediaStore} to include the resulting file.
 * <p/>
 * This can be constructed through an {@link ImageSaverBuilder} as the necessary image and
  * result information becomes available.
 * original : https://github.com/googlesamples/android-Camera2Raw/tree/master/Application/src/main/java/com/example/android/camera2raw
 */
public class ImageSaver implements Runnable {

        // debug
	protected final static boolean D = true;
    	protected final static String TAG = "Camera2";
    	protected final static String TAG_SUB = "ImageSaver";


        /**
         * The image to save.
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        /**
         * The CaptureResult for this image capture.
         */
        private final CaptureResult mCaptureResult;

        /**
         * The CameraCharacteristics for this camera device.
         */
        private final CameraCharacteristics mCharacteristics;

        /**
         * The Context to use when updating MediaStore with the saved images.
         */
        private final Context mContext;

        /**
         * A reference counted wrapper for the ImageReader that owns the given image.
         */
        private final RefCountedAutoCloseable<ImageReader> mReader;

/**
 * constractor
 */
public ImageSaver(Image image, File file, CaptureResult result,
                           CameraCharacteristics characteristics, Context context,
                           RefCountedAutoCloseable<ImageReader> reader) {
            mImage = image;
            mFile = file;
            mCaptureResult = result;
            mCharacteristics = characteristics;
            mContext = context;
            mReader = reader;
}

        @Override
        public void run() {
            boolean success = false;
            int format = mImage.getFormat();
            switch (format) {
                case ImageFormat.JPEG: {
                    // JPEG format
                    ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(mFile);
                        output.write(bytes);
                        success = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        mImage.close();
                        closeOutput(output);
                    }
                    break;
                }
                case ImageFormat.RAW_SENSOR: {
                    // save dng format, when RAW format
                    DngCreator dngCreator = new DngCreator(mCharacteristics, mCaptureResult);
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(mFile);
                        dngCreator.writeImage(output, mImage);
                        success = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        mImage.close();
                        closeOutput(output);
                    }
                    break;
                }
                default: {
                    log_d( "Cannot save image, unexpected image format:" + format);
                    break;
                }
            }

            // Decrement reference count to allow ImageReader to be closed to free up resources.
            mReader.close();

            // If saving the file succeeded, update MediaStore.
            if (success) {
                MediaScannerConnection.scanFile(mContext, new String[]{mFile.getPath()},
                /*mimeTypes*/null, new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        // Do nothing
                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        log_d("Scanned " + path + ":");
                        log_d("-> uri=" + uri);
                    }
                });
            }
        }


    /**
     * Cleanup the given {@link OutputStream}.
     *
     * @param outputStream the stream to close.
     */
public static void closeOutput(OutputStream outputStream) {
        if (null == outputStream) return;
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
} // closeOutput

/**
 * write into logcat
 */ 
private void log_d(String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


 /**
 * Builder class for constructing {@link ImageSaver}s.
  * <p/>
  * This class is thread safe.
  */
public static class ImageSaverBuilder {
            private Image mImage;
            private File mFile;
            private CaptureResult mCaptureResult;
            private CameraCharacteristics mCharacteristics;
            private Context mContext;
            private RefCountedAutoCloseable<ImageReader> mReader;

  /**
  * Construct a new ImageSaverBuilder using the given {@link Context}.
  *
   * @param context a {@link Context} to for accessing the
  *                {@link android.provider.MediaStore}.
  */
public ImageSaverBuilder(final Context context) {
                mContext = context;
}

            public synchronized ImageSaverBuilder setRefCountedReader(
                    RefCountedAutoCloseable<ImageReader> reader) {
                if (reader == null) throw new NullPointerException();

                mReader = reader;
                return this;
            }

            public synchronized ImageSaverBuilder setImage(final Image image) {
                if (image == null) throw new NullPointerException();
                mImage = image;
                return this;
            }

            public synchronized ImageSaverBuilder setFile(final File file) {
                if (file == null) throw new NullPointerException();
                mFile = file;
                return this;
            }

            public synchronized ImageSaverBuilder setResult(final CaptureResult result) {
                if (result == null) throw new NullPointerException();
                mCaptureResult = result;
                return this;
            }

            public synchronized ImageSaverBuilder setCharacteristics(
                    final CameraCharacteristics characteristics) {
                if (characteristics == null) throw new NullPointerException();
                mCharacteristics = characteristics;
                return this;
            }

            public synchronized ImageSaver buildIfComplete() {
                if (!isComplete()) {
                    return null;
                }
                return new ImageSaver(mImage, mFile, mCaptureResult, mCharacteristics, mContext,
                        mReader);
            }

            public synchronized String getSaveLocation() {
                return (mFile == null) ? "Unknown" : mFile.toString();
            } // getSaveLocation

            public synchronized File getSaveFile() {
                return mFile;
            } // getSaveFile

            private boolean isComplete() {
                return mImage != null && mFile != null && mCaptureResult != null
                        && mCharacteristics != null;
            }

        } // class ImageSaverBuilder

} // class ImageSaver

