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
 *  class Cube
 */
public class Cube {

    // debug
    private final static String TAG = "Cube";

    private final int BYTES_PER_FLOAT = 4;
	private final static int NUM_BUF_PER_FACE =  4;


	private final static int RET_ERROR =  -1;


	// Vertex Attrib Name
	private final static String UNIFORM_MODEL_NAME = "u_modelView";
	private final static String UNIFORM_PROJECTION_NAME = "u_proj";
	private final static String ATTRIB_LOCATION_NAME = "a_pos";

	// Vertex Shader Code
	private final static String VERTEX_CODE = 
			"uniform mat4 u_modelView;"+    
			"uniform mat4 u_proj;"+ 
			"attribute vec4 a_pos;"+  
			"void main(){"+       
			"gl_Position = u_proj * u_modelView * a_pos;"+  
			"}";


	// Fragment Attrib Name
		private final static String UNIFORM_LOCATION_NAME = "u_col";

	//  Shader Code
	private final static String FRAGMENT_CODE = 
			"precision mediump float;"+     
			"uniform vec4 u_col;"+      
			"void main(){"+   
			"gl_FragColor=u_col;"+     
			"}";


	// Vertex Buffer (8 vertexs)
	private final static float[] VERTICES = {
			1.0f, 1.0f, 1.0f,
			1.0f, 1.0f,-1.0f,
			-1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f,-1.0f,
			1.0f,-1.0f, 1.0f,
			1.0f,-1.0f,-1.0f,
			-1.0f,-1.0f, 1.0f,
			-1.0f,-1.0f,-1.0f,
		};

		// Index Buffer (6 Faces)
		private final static byte[] INDICES = {
				0,1,2,3,
				2,3,6,7,
				6,7,4,5,
				4,5,0,1,
				1,5,3,7,
				0,2,4,6,
		};


	private int mVertexShaderID;
	private int mFragmentShaderID;

	
	// Handle to send value to shader
	private int mLocModelView;
	private int mLocProj;
	private int mLocPos;
	private int mLocCol;
	

	private FloatBuffer mVertexBuffer;
	private ByteBuffer mIndexBuffer;
	

/** 
 *  Constractor
 */
	public Cube() {

		// initialize the shader
		initShader();
		
		// enable Vertex Array
		GLES20.glEnableVertexAttribArray(mLocPos);
		
		// initialize Buffer
		mVertexBuffer = makeFloatBuffer(VERTICES);		
		mIndexBuffer = makeByteBuffer(INDICES);

	}



/** 
 *   initShader
 *   initialize Shader
 */
	private void initShader() {

		mVertexShaderID = this.compileShader(
				GLES20.GL_VERTEX_SHADER, VERTEX_CODE);
		
		mFragmentShaderID = this.compileShader(
				GLES20.GL_FRAGMENT_SHADER, FRAGMENT_CODE);
		
		// create Program Object
		int programId = GLES20.glCreateProgram();
		GLES20.glAttachShader(programId, mVertexShaderID);
		GLES20.glAttachShader(programId, mFragmentShaderID);
		GLES20.glLinkProgram(programId);
		
		// retrieve Handle to send value to Shader
		mLocModelView = GLES20.glGetUniformLocation(
				programId, UNIFORM_MODEL_NAME);
		mLocProj = GLES20.glGetUniformLocation(
				programId, UNIFORM_PROJECTION_NAME);


		// set Vertices
		mLocPos = GLES20.glGetAttribLocation(
				programId, ATTRIB_LOCATION_NAME);
		mLocCol = GLES20.glGetUniformLocation(
				programId, UNIFORM_LOCATION_NAME);
	
		// use Program Object
		GLES20.glUseProgram(programId);

	}
	

	
/** 
 *   draw
 */
	public void draw(float[]  projMatrix, float[] modelViewMatrix) {


		// send Projection matrix to Shader
		GLES20.glUniformMatrix4fv(mLocProj, 1, false, projMatrix, 0);
		
		
		// send Model View Matrix to Shader
		GLES20.glUniformMatrix4fv(mLocModelView, 1, false, modelViewMatrix, 0);

		
		// Face 0 (Red)
 		drawFace(0, 1.0f, 0.0f, 0.0f, 1.0f);

		// Face 1 (Green)
 		drawFace(1, 0.0f, 1.0f, 0.0f, 1.0f);

		// Face 2 (Blue)
 		drawFace(2, 0.0f, 0.0f, 1.0f, 1.0f);

		// Face 3 (Yellow)
 		drawFace(3, 1.0f, 1.0f, 0.0f, 1.0f);

		// Face 4 (Aqua)
 		drawFace(4, 0.0f, 1.0f, 1.0f, 1.0f);

		// Face 5 (White)
 		drawFace(5, 1.0f, 1.0f, 1.0f, 1.0f);

	}



/** 
 *  drawFace
 */	
private void drawFace(int index, float red, float green, float blue, float alpha) {

		// specify Vertex Buffer
 		int LOC_SIZE = 3;
		int LOC_STRIDE = 0;
		GLES20.glVertexAttribPointer(this.mLocPos, LOC_SIZE,
				GLES20.GL_FLOAT, false, LOC_STRIDE, mVertexBuffer);

		// set Color
		GLES20.glUniform4f(mLocCol, red, green, blue, alpha);

		// set the buffer position to the top of face
		int pos = index * NUM_BUF_PER_FACE;
		mIndexBuffer.position(pos);

		// draw Face
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, NUM_BUF_PER_FACE,
				GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);

	}


/** 
 *  makeFloatBuffer
 *  create FloatBuffer and set the value
 */
	private FloatBuffer makeFloatBuffer(float[] values) {
		// create FloatBuffer
		int capacity = values.length * BYTES_PER_FLOAT;
		FloatBuffer fb = ByteBuffer.allocateDirect(capacity)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		// set the value
		fb.put(values)
				.position(0);
		return fb;
	}

	

/** 
 *  makeByteBuffer
 *  create FloatBuffer and set the value
 */
	private ByteBuffer makeByteBuffer(byte[] values) {
		int capacity = values.length;
		ByteBuffer buf = ByteBuffer.allocateDirect(capacity);
		buf.order(ByteOrder.nativeOrder());
		buf.put(values);
		buf.position(0);
		return buf;
	}
	

/** 
 *  compileShader
 *  compile Shader Source code
 */
	private int compileShader(int type, String code) {
		final int shaderId = GLES20.glCreateShader(type);
		if (shaderId == 0) {
			// failed to create shader area
			log_d("compileShader: cannot Create Shader");
			return RET_ERROR;
		}

		// compile Shader Source code
		GLES20.glShaderSource(shaderId, code);
		GLES20.glCompileShader(shaderId);

		// check if the compilation was successful
		int[] res = new int[1];
		GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, res, 0);
		if (res[0] == GLES20.GL_FALSE) {
			// failed
			log_d("compileShader Failed: " + GLES20.glGetShaderInfoLog(shaderId));
			return RET_ERROR;
		}
		return shaderId;
	}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	   Log.d( TAG, msg );
} 


} // class Cube

