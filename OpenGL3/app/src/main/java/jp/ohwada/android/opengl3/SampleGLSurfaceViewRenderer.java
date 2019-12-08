/** 
 *  OpenGL ES2.0 Sample
 *  draw Square and rotating Triangle
 *  2019-10-01 K.OHWADA
 * original : https://github.com/JimSeker/opengl/tree/master/HelloOpenGLES20
 */
package jp.ohwada.android.opengl3;


/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;


/**
 * class SampleGLSurfaceViewRenderer
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class SampleGLSurfaceViewRenderer implements GLSurfaceView.Renderer {


    // debug
    private static final String TAG = "SampleGLSurfaceViewRenderer";


    // Bckground Color(Blue)
    float[] BG_COLOR = { 0.4f, 0.4f, 0.4f, 1.0f };


    // Square Color(Blue)
    float[] SQUARE_COLOR = { 0.0f, 0.0f, 1.0f, 1.0f };

// Triangle Color(Red)
    float[] TRIANGLE_COLOR = { 1.0f, 0.0f, 0.0f, 1.0f };


    private Triangle mTriangle;
    private Square   mSquare;


    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];


    // Triangle Rotation
    private float mAngle;
    private boolean isClockwise = true;
    private boolean  isAngleHalf = false;


/**
 *  onSurfaceCreated
 */
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // set Background Frame Color
        GLES20.glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]);

        mTriangle = new Triangle();
        mTriangle.setColor(TRIANGLE_COLOR);

        mSquare   = new Square();
        mSquare.setColor(SQUARE_COLOR);

    }


/**
 *  onDrawFrame
 */
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw square
        mSquare.draw(mMVPMatrix);

        // Create a rotation for the triangle
        createRotationMatrix();

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw triangle
        mTriangle.draw(scratch);
    }


/**
 *  createRotationMatrix
 *  create Rotation for Triangle
 */
private void createRotationMatrix() {

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);


        long TIME_PERIOD = 10000L;   // 10 sec
        float ANGLE_AROUND = 360.0f; // degree
        float ANGLE_FACTOR = ANGLE_AROUND / (float)TIME_PERIOD;
        float ANGLE_MAX = ANGLE_AROUND * 0.95f;

        long time = SystemClock.uptimeMillis() % TIME_PERIOD;
        float angle = ANGLE_FACTOR * ((int) time);

        // reverse when turn around once
        if (  isAngleHalf && (angle > ANGLE_MAX )) {
            isClockwise = ! isClockwise;
             isAngleHalf = false;
        }

        // half circle
        if ( ( angle > 90) && ( angle < 270)) {
             isAngleHalf = true;
        }

        mAngle = angle;
        if ( !isClockwise ) {
             mAngle = ANGLE_AROUND  - angle;
        }

        int ROTATE__OFFSET = 0;
        float ROTATE_X   = 0.0f;
        float ROTATE_Y  = 0.0f;
        float ROTATE_Z = 1.0f;
        Matrix.setRotateM(mRotationMatrix, ROTATE__OFFSET, mAngle, ROTATE_X, ROTATE_Y, ROTATE_Z);
}


/**
 *  onSurfaceChanged
 */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }


    /**
     * loadShader
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
    * checkGlError
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * SampleGLSurfaceViewRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            log_d(glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }


    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }


    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    Log.d( TAG, msg );
} 


} // class SampleGLSurfaceViewRenderer