/** 
 *  OpenGL ES2.0 Sample
 *  draw Images continuously like slot machine
 *  2019-10-01 K.OHWADA
 * original : http://junkcode.aakaka.com/archives/500
 */
package jp.ohwada.android.opengl9;


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;


/** 
 *  class Box
 */
class Box {

    // debug
    private final static String TAG = "Box";

	
	// specify Vertex Attribute Index here
	public static final int ATTRIBUTE_POSITION_LOCATION = 0;
	private static final int ATTRIBUTE_COLOR_LOCATION = 1;
	private static final int ATTRIBUTE_UV0_LOCATION = 2;
	private static final String ATTRIBUTE_POSITION = "a_position";
	private static final String ATTRIBUTE_COLOR = "a_color";
	private static final String ATTRIBUTE_UV0 = "a_uv";
	
	private static final String UNIFORM_MODELVIEWMATRIX = "u_modelViewMatrix";
	private static final String UNIFORM_PROJECTIONMATRIX = "u_projectionMatrix";
	private static final String UNIFORM_COLOR = "u_color";
	private static final String UNIFORM_TEXTURE0 = "u_texture0";
	private static final String VARYING_COLOR = "v_color";
	private static final String VARYING_UV0 = "v_uv0";

	// Shader
	private int mVertexShaderID;	
	private int mFragmentShaderID;
	private int mProgramID;

	// Handle to send value to shader
	private int mLocModelView;
	private int mLocProj;
	private int mLocColor;
	private int mLocTexture;

	// VBO control number
	private int mVertexBufferID;
	private int mIndexBufferID; 


/** 
 *  Constractor
 */	
public Box() {

		this.initShader();

		// enable Vertex Attribute Index 
		GLES20.glEnableVertexAttribArray(ATTRIBUTE_POSITION_LOCATION);

		// enable Vertex Color
		GLES20.glEnableVertexAttribArray(ATTRIBUTE_COLOR_LOCATION);

		// enable Texture coordinates
		GLES20.glEnableVertexAttribArray(ATTRIBUTE_UV0_LOCATION);
        
		this.initVBO();
		
	}


/** 
 *  initVBO
 *  register VBO
 */	
	private void initVBO() {

		// Vertex coordinates + Vertex color
		// create a VBO from the vertex buffer
		float[] VBO_VERTICES = {
				// coordinate(x, y, z)  UV0
				-0.5f,  0.5f,  0.0f, 0.0f, 0.0f,
				-0.5f, -0.5f,  0.0f, 0.0f, 0.888f,
				 0.5f,  0.5f,  0.0f, 1.0f, 0.0f,
				 0.5f, -0.5f,  0.0f, 1.0f, 0.888f,
		};

		FloatBuffer vertexBuffer = this.makeFloatBuffer(VBO_VERTICES);

		// Specify GL_ARRAY_BUFFER for Vertex data
		this.mVertexBufferID = this.makeVBO(
				vertexBuffer, 4, GLES20.GL_ARRAY_BUFFER);

		// create VBO from index buffer
		byte[] VBO_INDICES = {
				0,1,2,3,
		};

		ByteBuffer indexBuffer = this.makeByteBuffer(VBO_INDICES);

		// Use GL_ELEMENT_ARRAY_BUFFER
		// to register Index
		this.mIndexBufferID = this.makeVBO(
				indexBuffer, 1, GLES20.GL_ELEMENT_ARRAY_BUFFER);
	}


/** 
 *  makeVBO
 *  convert from buffer to VBO
 */	
	private int makeVBO(Buffer buffer, int size, int target) {

		int[] hardwareIDContainers = { -1 };

		GLES20.glGenBuffers(1, hardwareIDContainers, 0);

		int hardwareIDContainer = hardwareIDContainers[0];
		GLES20.glBindBuffer(target, 
				hardwareIDContainer);
		GLES20.glBufferData(target,
				buffer.capacity() * size, buffer, GLES20.GL_STATIC_DRAW);

		return hardwareIDContainer;
	}


/** 
 *  initShader
 */	
	private void initShader() {

		// compile Vertex Shader with vertex color
		String VERTEX_CODE = 
				"uniform mat4  " + UNIFORM_MODELVIEWMATRIX + ";"+    
				"uniform mat4 " + UNIFORM_PROJECTIONMATRIX + ";"+
				"uniform vec4 " + UNIFORM_COLOR + ";" +
				"attribute vec4 " + ATTRIBUTE_POSITION + ";"+
				"attribute vec2 " + ATTRIBUTE_UV0 + ";"+
				"varying vec4 " + VARYING_COLOR + ";" +
				"varying vec2 " + VARYING_UV0 + ";" +
				"void main(){"+       
				"    gl_Position = " + UNIFORM_PROJECTIONMATRIX + " * " + UNIFORM_MODELVIEWMATRIX + " * " + ATTRIBUTE_POSITION + ";"+  
				"    " + VARYING_COLOR + " = " + UNIFORM_COLOR + ";" +
				"    " + VARYING_UV0 + " = " + ATTRIBUTE_UV0 + ";" +
				"}";

		this.mVertexShaderID = this.compileShader(
				GLES20.GL_VERTEX_SHADER, VERTEX_CODE);

		// compile Fragment Shader with vertex color
		String  FRAGMENT_CODE = 
				"precision mediump float;"+
				"uniform sampler2D " + UNIFORM_TEXTURE0 + ";\n" +
				"varying vec4  " + VARYING_COLOR + ";" +
				"varying mediump vec2  " + VARYING_UV0 + ";" +
				"void main(){"+
				"    gl_FragColor = texture2D(" + UNIFORM_TEXTURE0 + ", " + VARYING_UV0 + ");" + 
				"}";
		this.mFragmentShaderID = this.compileShader(
				GLES20.GL_FRAGMENT_SHADER,  FRAGMENT_CODE);

		// create Program object
		this.mProgramID = GLES20.glCreateProgram();
		GLES20.glAttachShader(this.mProgramID, this.mVertexShaderID);
		GLES20.glAttachShader(this.mProgramID, this.mFragmentShaderID);


		// associate Vertex Attribute Index with Shader variable
		GLES20.glBindAttribLocation(this.mProgramID, ATTRIBUTE_POSITION_LOCATION, ATTRIBUTE_POSITION);
		GLES20.glBindAttribLocation(this.mProgramID, ATTRIBUTE_UV0_LOCATION, ATTRIBUTE_UV0);

		GLES20.glLinkProgram(this.mProgramID);


		// retrieve Handle to send value to Shader
		this.mLocModelView = GLES20.glGetUniformLocation(
				this.mProgramID, UNIFORM_MODELVIEWMATRIX);
		this.mLocProj = GLES20.glGetUniformLocation(
				this.mProgramID, UNIFORM_PROJECTIONMATRIX);
		this.mLocColor = GLES20.glGetUniformLocation(
				this.mProgramID, UNIFORM_COLOR);
		this.mLocTexture = GLES20.glGetUniformLocation(
				this.mProgramID, UNIFORM_TEXTURE0);

		GLES20.glUseProgram(this.mProgramID);

	}


/** 
 *  draw
 */
public void draw(float[] projMatrix) {

		// send to Shader
		GLES20.glUniformMatrix4fv(this.mLocProj, 1, false, projMatrix, 0);


		// draw with VBO
		// set vertex buffer
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.mVertexBufferID);
		

		// specify Location in the Vertex Attribute Index
		int VERTEX_STRIDE = 5 * 4;	// 5 x 4-bytes
		int POS_SIZE = 3;
 		int POS_OFFSET = 0;
		int UV_SIZE = 2;
 		int UV_OFFSET = 4 * 3;
		GLES20.glVertexAttribPointer(ATTRIBUTE_POSITION_LOCATION, POS_SIZE, GLES20.GL_FLOAT, false, VERTEX_STRIDE, POS_OFFSET);
		GLES20.glVertexAttribPointer(ATTRIBUTE_UV0_LOCATION, UV_SIZE, GLES20.GL_FLOAT, false, VERTEX_STRIDE, UV_OFFSET);


		// specify Index Buffer
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.mIndexBufferID);

}

 
/** 
 * drawTexture
 */
	public void drawTexture(float[] mvm, int texture) {
		

		// send Model View Matrix to Shader
		GLES20.glUniformMatrix4fv(this.mLocModelView, 1, false, mvm, 0);
		
		// bind Texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glUniform1i(this.mLocTexture, 0);

		// draw box
		int BOX_COUNT = 4; 
		int BOX_OFFSET = 0;
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, BOX_COUNT,
				GLES20.GL_UNSIGNED_BYTE, BOX_OFFSET);

	}


/** 
 *  makeFloatBuffer
 *  create FloatBuffer and set the value
 */
	private FloatBuffer makeFloatBuffer(float[] values) {

		// create FloatBuffer
		int capacity = values.length * 4 ;
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
 *  create ByteBuffer and set the value
 */
	private ByteBuffer makeByteBuffer(byte[] values) {

		int capacity = values.length;
		ByteBuffer buf = ByteBuffer.allocateDirect(capacity)
				.order(ByteOrder.nativeOrder());
		buf.put(values)
		.position(0);

		return buf;

	}



 /** 
 *  compileShader
 */
	private int compileShader(int type, String code) {

		 int CODE_EEROR = -1;

		int shaderId = GLES20.glCreateShader(type);
		if (shaderId == 0) {
			// failed to allocate shader area
			log_d("compileShader: failed to allocate shader area");
			return  CODE_EEROR;
		}

		// compile　Shader
		GLES20.glShaderSource(shaderId, code);
		GLES20.glCompileShader(shaderId);


		// check if tcompile successfully
		int[] res = new int[1];
		GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, res, 0);
		if (res[0] == 0) {
			// failed
			String infoLog = GLES20.glGetShaderInfoLog(shaderId);
			log_d("compileShader: " + infoLog);
			return  CODE_EEROR;
		}

		return shaderId;
	}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    Log.d( TAG, msg );
}


} // class Box

