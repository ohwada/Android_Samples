/** 
 *  OpenGL Sample
 *  draw rotating Cube
 *  2019-10-01 K.OHWADA
 * original : http://junkcode.aakaka.com/archives/197
 */
package jp.ohwada.android.opengl4;


import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



/**
 *  class CubeGLSurfaceViewRenderer
 */
public class CubeGLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    // debug
    private final static String TAG = "CubeGLSurfaceViewRenderer";


	private float mAspect;	// Aspect ratio
	private float mAngle; // Model Angle
	

	private float[] mModelView = new float[16];		// Model View Matrix
	private float[] mProj = new float[16];	// Projection Matrix
	
	private Cube mCube;


/** 
 *  onSurfaceCreated
 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		// enable Depth Buffer
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

			mCube = new Cube();

	}


/** 
 *  onSurfaceChanged
 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	
		// set ViewPort
		GLES20.glViewport(0, 0, width, height);
		
		// calc Aspect Ratio
		this.mAspect = (float)width / (float)height;

	}
	
	
/** 
 *  onDrawFrame
 */
	public void onDrawFrame(GL10 gl) {

		//draw Background Color (Gray)
		GLES20.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		createProjectionMatrix();
	
		createModelViewMatrix();

		// change ModelView Angle
		mAngle++;
		rotateModelViewMatrix( mAngle );

		mCube.draw(mProj, mModelView);

	}


/** 
 *  createProjectionMatrix
 */
private void createProjectionMatrix() {

		Matrix.setIdentityM(this.mProj, 0);

		// doPerspective
		float PROJ_FOV = 45.0f; // Field of View
		float PROJ_NEAR = 1.0f;
		float PROJ_FAR = 100.0f;
		doPerspective(this.mProj, PROJ_FOV, this.mAspect, PROJ_NEAR, PROJ_FAR);
}


/** 
 *  doPerspective
 */
	private void doPerspective(float[] out, float fov, float aspect, 
			float near, float far) {

		int OFFSET = 0;
		float top = near * (float)Math.tan(Math.toRadians(fov));
		float bottom = -top;
		float left = bottom * aspect;
		float right = top * aspect;
		Matrix.frustumM(out, OFFSET, left, right, bottom, top, near, far);
	}


/**
 * createModelViewMatrix
 */ 
	private void createModelViewMatrix() {

		Matrix.setIdentityM(this.mModelView, 0);
	
		// set LookAt Matrix
		int LOOK_OFFSET = 0;
		float LOOK_EYE_X = 0.0f; 
		float LOOK_EYE_Y = 0.0f; 
		float LOOK_EYE_Z = 5.0f; 
		float LOOK_CENTER_X = 0.0f;
		float LOOK_CENTER_Y = 0.0f;
		float LOOK_CENTER_Z = 0.0f;
		float LOOK_UP_X = 0.0f;
		float LOOK_UP_Y = 1.0f;
		float LOOK_UP_Z = 0.0f;

		Matrix.setLookAtM(this.mModelView, LOOK_OFFSET ,
				LOOK_EYE_X, LOOK_EYE_Y, LOOK_EYE_Z,
				LOOK_CENTER_X, LOOK_CENTER_Y, LOOK_CENTER_Z,
				LOOK_UP_X, LOOK_UP_Y, LOOK_UP_Z);
}		


/**
 * rotateModelViewMatrix
 */ 
private void rotateModelViewMatrix( float angle ) {

		// rotate Matrix
		int ROT_OFFSET = 0;
		float ROT_X = 1.0f;
		float ROT_Y = 1.0f;
		float ROT_Z = 0.0f;
		Matrix.rotateM(this.mModelView, ROT_OFFSET, angle, ROT_X, ROT_Y, ROT_Z);
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	   Log.d( TAG, msg );
} 


} // class CubeGLSurfaceViewRenderer
