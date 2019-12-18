/** 
 *  OpenGL eS2.0 Sample
 *  draw Rectangle with Texture
 *  2019-10-01 K.OHWADA
 * original : https://github.com/benosteen/opengles-book-samples
 */
package jp.ohwada.android.opengl6;



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
 *  class TextureRectangle
 */
public class TextureRectangle
{


    private final static int BYTES_PER_FLOAT = 4;
    private final static float[] VERTICES_DATA =
    { 
            -0.5f, 0.5f, 0.0f, // Position 0
            0.0f, 0.0f, // TexCoord 0
            -0.5f, -0.5f, 0.0f, // Position 1
            0.0f, 1.0f, // TexCoord 1
            0.5f, -0.5f, 0.0f, // Position 2
            1.0f, 1.0f, // TexCoord 2
            0.5f, 0.5f, 0.0f, // Position 3
            1.0f, 0.0f // TexCoord 3
    };


    private final static int BYTES_PER_SHORT = 2;
    private final static short[] INDICES_DATA =
    { 
            0, 1, 2, 0, 2, 3 
    };


            private final static String VERTEX_SHADER_CODE =
                  "attribute vec4 a_position;   \n"
                + "attribute vec2 a_texCoord;   \n"
                + "varying vec2 v_texCoord;     \n"
                + "void main()                  \n"
                + "{                            \n"
                + "   gl_Position = a_position; \n"
                + "   v_texCoord = a_texCoord;  \n"
                + "}                            \n";


            private final static String FRAGMENT_SHADER_CODE = 
                  "precision mediump float;                            \n"
                + "varying vec2 v_texCoord;                            \n"
                + "uniform sampler2D s_texture;                        \n"
                + "void main()                                         \n"
                + "{                                                   \n"
                + "  gl_FragColor = texture2D( s_texture, v_texCoord );\n"
                + "}                                                   \n";


    // Handle to a program object
    private int mProgramObject;
    
    // Attribute locations
    private int mPositionLoc;
    private int mTexCoordLoc;
    
    // Sampler location
    private int mSamplerLoc;
    
    // Texture handle
    private int mTextureId;
    
    // Additional member variables
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;


/**
 *  Constructor
 */
public TextureRectangle()
    {
        
         int vertice_capacity = VERTICES_DATA.length * BYTES_PER_FLOAT;
        mVertices = ByteBuffer.allocateDirect(vertice_capacity)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(VERTICES_DATA).position(0);

         int index_capacity = INDICES_DATA.length * BYTES_PER_SHORT;
        mIndices = ByteBuffer.allocateDirect(index_capacity)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(INDICES_DATA).position(0);

        initShader();
    }

 

/**
 *  initShader
 *  initialize the shader and program object
 */
    private void initShader()
    {

        // Load the shaders and get a linked program object
        mProgramObject = ESShader.loadProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);

        // Get the attribute locations
        mPositionLoc = GLES20.glGetAttribLocation(mProgramObject, "a_position");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramObject, "a_texCoord" );
        
        // Get the sampler location
        mSamplerLoc = GLES20.glGetUniformLocation ( mProgramObject, "s_texture" );

    }


/**
 *  setTexture
 */
public void  setTexture(int textureId) {
        mTextureId = textureId;
}


/**
 * draw Rectabgle using the shader pair
 */
    public void draw()
    {

        // Use the program object
        GLES20.glUseProgram(mProgramObject);

        // load Vertex position
        mVertices.position(0);
        int VER_SIZE = 3;
        int VER_STRIDE = 5 * 4;
        GLES20.glVertexAttribPointer ( mPositionLoc, VER_SIZE, GLES20.GL_FLOAT, 
                                       false, 
                                       VER_STRIDE, mVertices );

        // load Texture coordinate
        mVertices.position(3);
        int TEX_SIZE = 2;
        int TEX_STRIDE = 5 * 4;
        GLES20.glVertexAttribPointer ( mTexCoordLoc, TEX_SIZE, GLES20.GL_FLOAT,
                                       false, 
                                       TEX_STRIDE, 
                                       mVertices );

        GLES20.glEnableVertexAttribArray ( mPositionLoc );
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

        // Bind the texture
        GLES20.glActiveTexture ( GLES20.GL_TEXTURE0 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, mTextureId );

        // Set the sampler texture unit to 0
        GLES20.glUniform1i ( mSamplerLoc, 0 );

        GLES20.glDrawElements ( GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndices );
    }

    
} // class Rectangle
