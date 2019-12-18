/** 
 *  OpenGL eS2.0 Sample
 *  draw Rectangle with Image Texture
 *  2019-10-01 K.OHWADA
 */
package jp.ohwada.android.opengl6;



import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;


/**
 *  class ImageTextureGLSurfaceViewRenderer
 *  reference : https://www.dogrow.net/android/blog13/
 */
public class ImageTextureGLSurfaceViewRenderer implements GLSurfaceView.Renderer
{

    private Context mContext;

    private TextureRectangle mTextureRectangle;


/**
 *  Constructor
 */
    public ImageTextureGLSurfaceViewRenderer(Context context)
    {
        mContext = context;
    }

 

/**
 *  onSurfaceCreated
 *  
initShader();
 and program object
 */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {

//https://www.dogrow.net/android/blog13/

        // disabled: Dithering
        gl.glDisable(GL10.GL_DITHER); 


        // enable Depth Buffer
        gl.glEnable(GL10.GL_DEPTH_TEST); 

        // enable 2D texture
        gl.glEnable(GL10.GL_TEXTURE_2D); 

        // enable Alpha Test (validation / invalidation of pixel by alpha channel)
        gl.glEnable(GL10.GL_ALPHA_TEST); 

        // enable Blend (overlapping images)
        gl.glEnable(GL10.GL_BLEND); 

        // Color Blend (image overlay) Mode
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA); 

            mTextureRectangle = new TextureRectangle();
 
        // load Image Texture
         int textureId = loadImageTexture(R.drawable.droid);
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
 *  loadImageTexture
 */	
	private int loadImageTexture(int res_id) {

		int[] texIds = {-1};
		GLES20.glGenTextures(1, texIds, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        int texId = texIds[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

		// load Bitmap
        Resources res = mContext.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, res_id);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // specify Texture Filter
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		
		bitmap.recycle();

		return texId;
	}


} // class ImageTextureGLSurfaceViewRenderer
