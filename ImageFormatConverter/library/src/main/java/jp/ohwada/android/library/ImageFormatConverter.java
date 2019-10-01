/**
 * Converter
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.library;


import android.media.Image;

import java.nio.ByteBuffer;


/**
  *  class NativeConverter
  *  convert YUV420_888 to YUV420Planar
  *  for the purpose of inputing image data from Camera device into MediaCodec
  * original : https://github.com/get2abhi/Camera2PreviewStreamMediaCodecVideoRecording
  */
public class ImageFormatConverter  {
/**
  * use Native Library
  */
    static{
        System.loadLibrary("native-yuv-to-buffer-lib");
    }


/**
  * convert
  * convert YUV420_888 to YUV420Planar
 */
public byte[] convert(Image image) {

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
  * external link to native code
 */
public native byte[] yuvToBuffer(ByteBuffer y, ByteBuffer u, ByteBuffer v, int yPixelStride, int yRowStride,
                                 int uPixelStride, int uRowStride, int vPixelStride, int vRowStride, int imgWidth, int imgHeight);

} // class ImageFormatConverter
