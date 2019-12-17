/** 
 *  OpenGL eS2.0 Sample
 *  draw Rectangle with Texture
 *  2019-10-01 K.OHWADA
 * original : https://github.com/benosteen/opengles-book-samples
 */
package jp.ohwada.android.opengl5;



//
// Book:      OpenGL(R) ES 2.0 Programming Guide
// Authors:   Aaftab Munshi, Dan Ginsburg, Dave Shreiner
// ISBN-10:   0321502795
// ISBN-13:   9780321502797
// Publisher: Addison-Wesley Professional
// URLs:      http://safari.informit.com/9780321563835
//            http://www.opengles-book.com
//
// Simple_Texture2D
//
//    This is a simple example that draws a quad with a 2D
//    texture image. The purpose of this example is to demonstrate 
//    the basics of 2D texturing
//


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;



/**
 *  class SimpleTexture2DRenderer
 */
public class SimpleTexture2DRenderer implements GLSurfaceView.Renderer
{

    private TextureRectangle mTextureRectangle;


/**
 *  Constructor
 */
    public SimpleTexture2DRenderer(Context context)
    {
        // nop
    }

 

/**
 *  onSurfaceCreated
 *  
initShader();
 and program object
 */
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
            mTextureRectangle = new TextureRectangle();
 
        // load Texture
         int textureId = createSimpleTexture2D ();
        mTextureRectangle.setTexture( textureId);

    }


/**
 *  onSurfaceChanged
 */
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // set ViewPort
        GLES20.glViewport(0, 0, width, height);

    }


/**
 *  onDrawFrame
 */
    public void onDrawFrame(GL10 glUnused)
    {

        // draw Backgrud Color(Gray)
         GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mTextureRectangle.draw();

    }


/**
 *  createSimpleTexture2D
 *  Create a simple 2x2 texture image with four different colors
 */
    private int createSimpleTexture2D( )
    {
        // Texture object handle
        int[] textureIds = new int[1];
        
        // 2x2 Image, 3 bytes per pixel (R, G, B)
        int IMG_WIDTH = 2;
        int IMG_HEIGHT = 2;
        int BYTES_PER_PIXEL = 3;
        byte[] PIXELS = 
            {  
                (byte) 0xff,   0,   0, // Red
                0, (byte) 0xff,   0, // Green
                0,   0, (byte) 0xff, // Blue
                (byte) 0xff, (byte) 0xff,   0  // Yellow
            };

        // initialize Pixel Buffer
        int num_pixel = IMG_WIDTH * IMG_HEIGHT;
        int capacity = num_pixel * BYTES_PER_PIXEL;
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(capacity);
        pixelBuffer.put(PIXELS).position(0);

        // Use tightly packed data
        GLES20.glPixelStorei ( GLES20.GL_UNPACK_ALIGNMENT, 1 );

        //  Generate a texture object
        GLES20.glGenTextures ( 1, textureIds, 0 );

        // Bind the texture object
        int textureId = textureIds[0];
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, textureId );

        //  Load the texture
        GLES20.glTexImage2D ( GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, IMG_WIDTH, IMG_HEIGHT, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, pixelBuffer );

        // Set the filtering mode
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );

        return textureId;        
    }

    
} // class SimpleTexture2DRenderer
