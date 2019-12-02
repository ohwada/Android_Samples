/** 
 *  OpenGL and GLSurfaceView Sample
 *  draw Triangle
 *  2019-10-01 K.OHWADA
 * original : https://qiita.com/shunjiro/items/f3bfca727b76350ee23a
 */
package jp.ohwada.android.opengl2;


import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 *  class Triangle
 */
public class Triangle {


    // vertex shader attrib name
    private final static String VERTEX_ATTRIB_NAME = "vPosition";

    // simple shader
    private final static String VERTEX_SHADER_CODE =
            "attribute  vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";


    // fragment color : red
    // color(R,G,B Alpha)
     private final static String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor =vec4(1.0, 0.0, 0.0, 1.0);" +
                    "}";


    // Triangle vertex coordinates
    private final static float TRIANGLE_VERTICES[] = {
        0.0f, 0.5f, 0.0f,//vertice A(x,y,z)
        -0.5f, -0.5f, 0.0f,//vertice B(x,y,z)
        0.5f, -0.5f, 0.0f//vertice C(x,y,z)
    };

    // Vertex Attrib Pointer
    private final static  int POINTER_SIZE = 3;
    private final static int POINTER_STRIDE = 3 *4;

    private  int shaderProgram;


/**
 *  constractor
 */
    public Triangle(){
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);
    }


/**
 *  loadShader
 */
    private int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


/**
 *  draw
 */
    public void draw(){

        int vertice_length = TRIANGLE_VERTICES.length;

        GLES20.glUseProgram(shaderProgram);
        int positionAttrib = GLES20.glGetAttribLocation(shaderProgram, VERTEX_ATTRIB_NAME);
        GLES20.glEnableVertexAttribArray(positionAttrib);

        int buf_capacity = vertice_length* 4;
        ByteBuffer bb = ByteBuffer.allocateDirect(buf_capacity);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(TRIANGLE_VERTICES);
        vertexBuffer.position(0);


        GLES20.glVertexAttribPointer(positionAttrib,  POINTER_SIZE, GLES20.GL_FLOAT, false, POINTER_STRIDE, vertexBuffer);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertice_length);

        GLES20.glDisableVertexAttribArray(positionAttrib);

    }


} // class Triangle
