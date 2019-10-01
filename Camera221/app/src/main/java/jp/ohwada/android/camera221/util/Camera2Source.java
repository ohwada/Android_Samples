/**
 * Camera2 Sample
 * PreviewCallback for MediaCodec
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera221.util;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;


import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;


import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.ohwada.android.library.ImageFormatConverter;


 /**
  *  class Camera2Source
 * similar to CameraSource of Vision API
 * original : https://github.com/EzequielAdrianM/Camera2Vision
  */
public class Camera2Source extends Camera2Base {


    // debug
    private final static String TAG_SUB = "Camera2Source";


/**
  * for ImageReader
  */
    private final static int IMAGE_READER_MAX_IMAGES = 4;


/**
 * for JPEG
 */ 
    private static final int JPEG_QUALITY = 100;


    /**
     * Callback interface used to supply image data from a photo capture.
     */
    public interface PreviewCallback {
        void onPreviewFrame(byte[] frame, long timestamp);
    }


    /**
     * callback used to supply image data from a photo capture.
     */
    private PreviewCallback mPreviewCallback;


 /**
  * ImageReader 
 */
    private ImageReader mImageReaderYuv;
    private ImageReader mImageReaderJpeg;


/**
   *  Converter
  */
private  static ImageFormatConverter   mConverter;


 /**
  * size of ImageReader 
 */
    private int mImageWidth = 640; // VGA
    private int mImageHeight = 480;


/**
   *  constractor
  */
        public Camera2Source() {
                super();
        }

/*
   *  constractor
  */
        public Camera2Source(Context context) {
                super(context);
        }


/*
   *  setImageSize
   *  MUST set before start
  */
    public void setImageSize(int width, int height) {
        mImageWidth = width;
        mImageHeight = height;
    }


/**
   *  setPreviewCallback
  */
        public void setPreviewCallback(PreviewCallback callback) {
mPreviewCallback = callback;
}


/**
 * stopExtend
 */
@Override
protected void stopExtend() {
    log_d("stopExtend");
       try {
            if (null !=  mImageReaderYuv) {
                        mImageReaderYuv.close();
                        mImageReaderYuv = null;
            }
            if (null !=  mImageReaderJpeg) {
                        mImageReaderJpeg.close();
                        mImageReaderJpeg = null;
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
} // stopExtend


/**
 * setUpExtend
 */
@Override
protected void setUpExtend(CameraCharacteristics characteristics) {

    log_d("setUpExtend");
    //setUpImageReaderJpeg(characteristics);
    setUpImageReaderYuv(characteristics);
    mConverter = new ImageFormatConverter();

} // setUpExtend


/**
  * setUpImageReaderYuv
  */
private void setUpImageReaderYuv(CameraCharacteristics characteristics) {

        log_d("setUpImageReaderYuv");

        // create with desired size
        mImageReaderYuv = ImageReader.newInstance( 
            mImageWidth, 
            mImageHeight, 
            ImageFormat.YUV_420_888, IMAGE_READER_MAX_IMAGES);

        try {
                mImageReaderYuv.setOnImageAvailableListener(mImageYuvListener, mBackgroundHandler);
        } catch (Exception e) {
                e.printStackTrace();
        }

} // setUpImageReaderYuv


/**
  * setUpImageReaderJpeg
  */
private void setUpImageReaderJpeg(CameraCharacteristics characteristics) {

        log_d("setUpImageReaderJpeg");

        StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        Size jpegSize = chooseLargestSize(map, ImageFormat.JPEG);
        log_d("JPEG largest : " +  jpegSize.toString());

        // create with largest size
        mImageReaderJpeg = ImageReader.newInstance( 
            jpegSize.getWidth(), 
            jpegSize.getHeight(), 
            ImageFormat.JPEG, IMAGE_READER_MAX_IMAGES);
        mImageReaderJpeg.setOnImageAvailableListener(mImageJpegListener, mBackgroundHandler);

} // setUpImageReaderJpeg


/**
   * createCameraPreviewSession
  */
@Override
protected void createCameraPreviewSession() {
    log_d("createCameraPreviewSession");
        try {
            // We set up a CaptureRequest.Builder with the output Surface.
            Surface imageReaderSurface = mImageReaderYuv.getSurface();
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mViewSurface);
            mPreviewRequestBuilder.addTarget(imageReaderSurface);
            // Here, we create a CameraCaptureSession for camera preview.
            List<Surface> outputs =  new ArrayList<Surface>();
            outputs.add(mViewSurface);
            outputs.add(imageReaderSurface);


                mCameraDevice.createCaptureSession(outputs, mPreviewSession, mBackgroundHandler);
        } catch (Exception e) {
                        e.printStackTrace();
        }
} //  createCameraPreviewSession



/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

/**
  * ImageJpegListener
 */
private ImageReader.OnImageAvailableListener mImageJpegListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            // nop
        }

    }; // ImageReader.OnImageAvailableListener

/**
  * ImageYuvListener
 */
private ImageReader.OnImageAvailableListener mImageYuvListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            procImageAvailableYuv(reader);
        }

    }; // ImageReader.OnImageAvailableListener


/**
  * procImageAvailableYuv
 */
private void procImageAvailableYuv(ImageReader reader) {

        Image  image = null;
        try {
                image = reader.acquireNextImage();
        } catch (IllegalStateException e) {
                e.printStackTrace();
        }
        if ( image == null) return;

        // YUV420_888 -> YUV420Planar
        byte[] bytes = mConverter.convert(image);

        long timestamp = image.getTimestamp();

        if(mPreviewCallback != null) {
                    // callback for MediaCodec
                    mPreviewCallback.onPreviewFrame(bytes, timestamp);
        }

        image.close();

}


/**
  * convImageYuvToCodecInputByteArray
 */
private byte[] convImageYuvToCodecInputByteArray(Image image) {

        final Image.Plane[] planes = image.getPlanes();
        Image.Plane yPlane = planes[0];
        Image.Plane uPlane = planes[1];
        Image.Plane vPlane = planes[2];

        byte[] bytes = yuvToBuffer(
                yPlane.getBuffer(),
                    uPlane.getBuffer(),
                    vPlane.getBuffer(),
                    yPlane.getPixelStride(),
                    yPlane.getRowStride(),
                    uPlane.getPixelStride(),
                    uPlane.getRowStride(),
                    vPlane.getPixelStride(),
                    vPlane.getRowStride(),
                    image.getWidth(),
                    image.getHeight() );

        return bytes;
}


/**
  * yuvToBuffer
  * thiis is native Library
  * reqire loadLibrary
  * original : https://github.com/get2abhi/Camera2PreviewStreamMediaCodecVideoRecording
 */
public native byte[] yuvToBuffer(ByteBuffer y, ByteBuffer u, ByteBuffer v, int yPixelStride, int yRowStride,
                                     int uPixelStride, int uRowStride, int vPixelStride, int vRowStride, int imgWidth, int imgHeight);


/**
  * convImageYuvToYuvByteArray
 */
private byte[] convImageYuvToYuvByteArray(Image image) {

//https://stackoverflow.com/questions/46403934/how-to-correctly-use-imagereader-with-yuv-420-888-and-mediacodec-to-encode-video
    Image.Plane[] planes = image.getPlanes();
    if (planes.length < 3) return null;
    ByteBuffer bufferY = planes[0].getBuffer();
    ByteBuffer bufferU = planes[1].getBuffer();
    ByteBuffer bufferV = planes[2].getBuffer();
    int lengthY = bufferY.remaining();
    int lengthU = bufferU.remaining();
    int lengthV = bufferV.remaining();
    byte[] dataYUV = new byte[lengthY + lengthU + lengthV];
    bufferY.get(dataYUV, 0, lengthY);
    bufferU.get(dataYUV, lengthY, lengthU);
    bufferV.get(dataYUV, lengthY + lengthU, lengthV);
    return dataYUV;
}


/**
  * convImageYuv420ToNv21ByteArray
 */
private  byte[] convImageYuv420ToNv21ByteArray(Image imageYuv) {

    ByteBuffer buffer0 = imageYuv.getPlanes()[0].getBuffer();
    ByteBuffer buffer2 = imageYuv.getPlanes()[2].getBuffer();
    int buffer0_size = buffer0.remaining();
    int buffer2_size = buffer2.remaining();
   byte[] bytes_nv21 = new byte[buffer0_size + buffer2_size];
    buffer0.get(bytes_nv21, 0, buffer0_size);
    buffer2.get(bytes_nv21, buffer0_size, buffer2_size);

    return bytes_nv21;
}


/**
 * convImageJpegToBitmap
 */ 
private Bitmap convImageJpegToBitmap(Image imageJpeg) {

        // ImageJpeg ->JpegByteArray
        ByteBuffer buffer = imageJpeg.getPlanes()[0].getBuffer();
        int size = buffer.capacity();
        byte[] bytes = new byte[size];
        buffer.get(bytes);

        // JpegByteArray -> Bitmap
        int length = bytes.length;
        Bitmap bitmap = BitmapFactory.decodeByteArray(
            bytes, 0, length, null);
        return bitmap;
}


/**
 * convBitmapToJpegByteArray
 */ 
private byte[] convBitmapToJpegByteArray(Bitmap bitmap) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, baos);
            } catch (Exception e) {
                e.printStackTrace();
            }

        byte[] bytes = baos.toByteArray();
        return bytes;
}


/**
 * resizeBitmap
 */ 
private Bitmap resizeBitmap(Bitmap source, int width, int height) {
    int src_width = source.getWidth() ;
    int src_height = source.getHeight();
    int limit_width = (int)( src_width * 0.8 );
    int limit_height = (int)( src_height * 0.8 );
    if( width > limit_width) {
        return source;
    }
    if( height >  limit_height) {
        return source;
    }

    Bitmap bitmap = Bitmap.createScaledBitmap(
        source, width, height, true );
    int dst_width = bitmap.getWidth() ;
    int dst_height = bitmap.getHeight();
    String msg = "resizeBitmap:" + src_width + "x" +  src_height +" -> " + dst_width + "x" +  dst_height;
    // log_d(msg);
    return bitmap;
}


/**
 * rotateBitmap
 */ 
private Bitmap rotateBitmap(Bitmap source, int degrees  ) {
    if (degrees == 0) {
        return source;
    }

    Matrix matrix = new Matrix();
    matrix.postRotate(degrees);  
    int width = source.getWidth();
    int height = source.getHeight();

    return Bitmap.createBitmap(
        source, 0, 0, width, height, matrix, true);
}


    /**
     * Builder for configuring and creating an associated camera source.
     */
    public static class Builder {

        private Camera2Source mCameraSource;

        /**
         * Creates a camera source builder with the supplied context and detector.  Camera preview
         * images will be streamed to the associated detector upon starting the camera source.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("No context supplied.");
            }
            mCameraSource
            = new Camera2Source(context);

        }

/**
  * setFocusMode
  */
        public Builder setFocusMode(int mode) {
            mCameraSource.setFocusMode(mode);
            return this;
        }

/**
  * setFocusMode
  */
        public Builder setFlashMode(int mode) {
            mCameraSource. setFlashMode(mode);
            return this;
        }

        /**
         * Sets the camera to use (either {@link #CAMERA_FACING_BACK} or
         * {@link #CAMERA_FACING_FRONT}). Default: back facing.
         */
        public Builder setFacing(int facing) {
            mCameraSource.setFacing(facing);
            return this;
        }


/**
   *  setErrorCallback
  */
        public Builder setErrorCallback(ErrorCallback cb) {
            mCameraSource.setErrorCallback(cb);
            return this;
        }


        /**
         * Creates an instance of the camera source.
         */
        public Camera2Source build() {
            return mCameraSource;
        } // build
} // class Builder 


} // class Camera2Source
