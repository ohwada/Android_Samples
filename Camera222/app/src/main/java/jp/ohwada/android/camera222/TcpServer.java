/**
 * Camera2 Sample
 * TcpServer 
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera222;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;


/**
 * class TcpServer 
 *  waiting for connection from Client on TCP
 *  sending images from Camera device, once connected
 * original : https://github.com/SIY1121/ScreenCastSample
 */
public class TcpServer implements Runnable {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "TcpServer";

    public final static     int PORT_DEFAULT = 8080;

    /**
     * Callback interface to notify connection status
     */
    public interface ServerCallback {
        void onWait();
        void onConnect(String clientAddress);
        void onDisConnect();
    }

    private ServerCallback mCallback;


    /**
     * Port number  waiting for for connection
     */
    private int mPort = PORT_DEFAULT;


    // Thread waiting for connection
    private Thread serverThread; 

    // Thread for sending
    private HandlerThread senderThread;

// Handler for sending
    private Handler senderHandler; 

    // ServerSocket waiting for connection
    private ServerSocket listener; 

    // Socket of client side
    private Socket clientSocket; 

    // Stream for sending data to the client
    private OutputStream outputStream; 


    /**
     * Flag whether to wait for a connection from the client
     */
    private boolean isRunning = false;



/**
 * setPort
 */ 
    public void setPort(int port) {
        mPort = port;
}


/**
 * setCallback
 */ 
    public void setCallback(ServerCallback callback) {
        mCallback = callback;
}


/**
 * start
 * start threads
 */ 
    public void start() {
        log_d("Start");

        isRunning = true;

        senderThread = new HandlerThread("senderThread");
        senderThread.start();
        senderHandler = new Handler(senderThread.getLooper());

        serverThread = new Thread(this);
        serverThread.start();

} // start


/**
 *  stop
 * stop all threads
 */ 
public void  stop() {
        log_d("stop");
        isRunning = false;
        try {
            if (listener != null) {
                listener.close();
                listener = null;
            }
            if (serverThread != null) {
                serverThread.join();
                serverThread = null;
            }
        } catch (Exception ex) {
            // nop
        }
} //  stop


/**
 *  run Server Thread
 *  accept connection once
 */ 
    public void run() {
        log_d("Server run");

        try{
                listener = new ServerSocket();
                listener.setReuseAddress(true);
                listener.bind(new InetSocketAddress(mPort));
        } catch (IOException e) {
                e.printStackTrace();
        }

        if( !listener.isBound()) return;

        String msg = "Server listening on port " + mPort + " ...";
        log_d(msg);

        if(mCallback != null ) {
                    mCallback.onWait();
        }

        while (isRunning) {
            try {
                    // waiting endless for connection
                    clientSocket = listener.accept();
                    if( clientSocket != null ) {
                        procConnect(clientSocket);
                    }
            } catch (IOException e) {
               // nop
            }
        } // while
} // run


/**
 * procConnect
 */ 
private void procConnect(Socket clientSocket) {
    String clientAddress = clientSocket.getInetAddress().getHostAddress();
	log_d("Connection from " + clientAddress);
    try {
        outputStream = clientSocket.getOutputStream();
    } catch (IOException e) {
               // nop
    }

        // callback when the client is connected
        //  for start encoding
        if(mCallback != null ) {
                    mCallback.onConnect(clientAddress);
        }

}


/**
 * sendData
 */ 
public void sendData(final byte[] array) {
        log_d("sendData");
        if(array == null) return;
        senderHandler.post(new Runnable() {

/**
 * run
 */
            @Override
            public void run() {
                try {
                    if((outputStream != null)&&(array != null)) {
                        outputStream.write(array);
                    }
                } catch (IOException ex) {
                    // consider disconnected
                    // if cannot sent
                    disconnect();
                }

            }
        }); // Runnable

} // sendData


/**
 * disconnect
 * close clientSocket
 */ 
private void disconnect() {
        log_d("disconnect");
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    if( mCallback != null ) {
        mCallback.onDisConnect();
    }
}


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

} // class TcpServer
