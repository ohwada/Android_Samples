/**
 * Camera2 Sample
 * take picture
 * 2019-02-01 K.OHWADA
 */

package jp.ohwada.android.camera22;


import android.media.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * class ImageSaver
 * original : https://github.com/googlesamples/android-Camera2Basic/tree/master/Application/src/main/java/com/example/android/camera2basic
 */
public class ImageSaver implements Runnable {


 /**
  * The JPEG image
 */
private final Image mImage;

/**
 * The file we save the image into.
 */
        private final File mFile;

/**
 * constractor
 */
ImageSaver(Image image, File file) {

        mImage = image;
             mFile = file;
} // ImageSaver

/**
 * run
 */
@Override
public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                    try {
                        if (null != output) output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
} // run

} // class ImageSaver
