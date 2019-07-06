/**
 * Camera2 Sample
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera216;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * class ImageUtil
 */ 
public class ImageUtil{


/**
 * Quality of JPEG
 */ 
    private static final int JPEG_QUALITY = 100;


/**
 * saveImageAsJpeg
 */ 
public static boolean saveImageAsJpeg(Image image, File file) {
        boolean is_error = false;
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out);
            } catch (Exception e) {
                e.printStackTrace();
                is_error = true;
            }

            try {
                    if (out != null) out.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
            return ! is_error;
} // saveImageAsJpeg


} // class ImageUtil

