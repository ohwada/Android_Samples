/**
 * Camera2 Sample
 * Motion JPEG Server
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera219;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * class MJpegServer
 * waiting to recieve Request with ServerSocket 
 * return to send Motion JPEG
 * original : https://qiita.com/Yukio-Ichikawa/items/cf93c4851f871003a0f2
 */
public class MJpegServer {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "MJpegServer";

/**
 * Setting param
 */
    public static final int PORT_DEFAULT = 8080;

/**
 * Socket param
 */
    private static final int BIND_TIMEOUT = 500;
    private static final int SO_TIMEOUT = 3000;
    private static final int SLEEP = 100;

/**
 * Response
 */

    private final static String CRLF = "\r\n";
    private final static String BOUNDARY = "camera2mjpegserver1234";
   private static final String BOUNDARY_LINE = "--" + BOUNDARY;

    private static final String BAD_RESPONSE = "HTTP/1.1 400 Bad Request" + CRLF;

    private static final String RESPONSE_HEADER  = 
        "HTTP/1.1 200 OK" + CRLF
        + "Server: CameraMjpegServer"  + CRLF
        + "Connection: close"  + CRLF
        + "Max-Age: 0"  + CRLF
        +"Expires: 0"  + CRLF
        + "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0" + CRLF
        + "Pragma: no-cache" + CRLF
         + "Content-Type: multipart/x-mixed-replace; boundary=" + BOUNDARY + CRLF
        + CRLF
// it seems good to send a boundary line after an empty line
// but not clear Specification
        + BOUNDARY_LINE + CRLF;

    private static final String PART_HEADER_TEMPLATE  = 
            "Content-type: image/jpeg" + CRLF
         + "Content-Length: %s" +  CRLF
         + CRLF;

     private final static String PART__END = CRLF + BOUNDARY_LINE + CRLF;


/**
 * Thread process
 */
    private Thread mLooper;


/**
 * Setting param
 */
    private int mPort = PORT_DEFAULT;

    private boolean isAllIpsAllowed = false;


/**
 * Lock for JPEG Frame
 */ 
    private static ReentrantReadWriteLock mFrameLock = new ReentrantReadWriteLock();


/**
 * JPEG Frame
 */
    private byte [] mFrame;

    private boolean isRunning;

/**
 * constractor
 */
    public MJpegServer(){
        // nop
    }

/**
 * start
 */
    public void start(int port){
        mPort = port;
        isRunning = true;
        startLooper();
    }

/**
 * stop
 */
    public void stop(){
        isRunning = false;
        stopLooper();
    }


/**
 * setAllIpsAllowed
 */ 
    public void setAllIpsAllowed(boolean allowAll) {
            isAllIpsAllowed = allowAll;
    }


/**
 * setFrame
 */
    public void setFrame(byte [] frame){
            try {
            mFrameLock.writeLock().lock();
                    mFrame = frame;
            } finally {
            mFrameLock.writeLock().unlock();
            }
    }

/**
 * getFrame
 */ 
private byte[] getFrame() {
        try {
            mFrameLock.readLock().lock();
            return mFrame;
        } finally {
            mFrameLock.readLock().unlock();
        }
    }


/**
 * startLooper
 */ 
private void startLooper() {
        log_d("startLooper");
        mLooper = new Thread(new Runnable() {
    @Override
    public void run() {
        ServerSocket socket = openServerSocket(mPort);

    // waiting for connection from client
    while (isRunning) {
                Socket clientSocket = acceptSocket(socket);
                if(clientSocket == null ) {
                    continue;
                }
                // ignore access from outside the local net
                InetAddress inetAddress = clientSocket.getInetAddress();
                String clientAddress = inetAddress.getHostAddress();
                boolean isSiteLocalAddress = inetAddress.isSiteLocalAddress();
                if (isAllIpsAllowed || isSiteLocalAddress) {
                    talkToClient(clientSocket, clientAddress);
                } else {
                    log_d("ignore " + clientAddress);
                }
    } // while
    closeServerSocket(socket);
} // run
    }); // Runnable

        mLooper.start();
}

/**
 * stopLooper
 */
 private void stopLooper(){
        try {
            if (mLooper != null){
                mLooper.join();
                mLooper = null;
            }
        } catch (Exception e) {
           // nop
        }
}

/**
 * acceptSocket
 */
private Socket acceptSocket(ServerSocket serverSocket) {
    Socket socket = null;
    try {
            if (serverSocket != null){
                    socket = serverSocket.accept();
            }
    } catch (IOException e) {
            // Timeout waiting for connection
    } 
    return socket;
}


/**
 * openServerSocket
 */
    private ServerSocket openServerSocket(int port) {
        log_d("openServerSocket");
        // create a server socket 
        // and bind it to the specified port
        ServerSocket socket = null;
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.setSoTimeout(BIND_TIMEOUT);
            socket.bind(inetSocketAddress);
        } catch(IOException e){
            e.printStackTrace();
        }
        return socket;
}

/**
 * closeServerSocket
 */
    private void closeServerSocket(ServerSocket socket) {
            log_d("closeServerSocket");
            try{
                if(socket != null) socket.close();
            }
            catch (Exception ex){
                // nop
            }
    }


/**
 * talkToClient
 */
private void talkToClient(final Socket socket, String clientAddress){
        log_d("talkToClient: " + clientAddress);
        Thread clientThread = new Thread(new Runnable() {
                @Override
                public void run() {
                        // send a Bad Request other than GET method
                                if( isMethodGet(socket) ) {
                                        responseForVideo(socket);
                                }else{
                                        sendBadResponse(socket);
                                }

                } // run
            }); // Runnable

        clientThread.start();
}

/**
 * isMethodGet
 */
private boolean isMethodGet(Socket socket) {
String GET = "GET";
    String method = getMethod(socket);
    if (method == null) {
        return false;
    }
    int compare = method.compareToIgnoreCase( GET);
    boolean ret = (compare == 0)? true: false;
    return ret;
}

/**
 * getMethod
 */
private String getMethod(Socket socket) {
        String method = null;

        // parse the request header 
        // and get the requestmethod
        String line = null;
        try {
                    InputStreamReader reader = new InputStreamReader(socket.getInputStream(), "UTF-8");
                    BufferedReader in = new BufferedReader(reader);
                    line = in.readLine();
        } catch(IOException e){
                    // nop
        }

         if (line  == null) {
                        return null;
        }

        // line: GET / HTTP/1.1
        String [] commands = line.split(" ");
        if (commands.length < 2){
                        return null;
        }

        method = commands[0];
        return method;
}


/**
 * sendBadResponse
 */
private void sendBadResponse(Socket socket) {
            BufferedOutputStream out = getOutputStream(socket);
            if(out == null) return;
            sendResponse(out, BAD_RESPONSE);
            closeOutputStream(out);
}


/**
 * responseForVideo
 */
    private void responseForVideo(Socket socket){

            //log_d("responseForVideo");
        try {
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(SO_TIMEOUT);
        }catch(SocketException e){
            e.printStackTrace();
        }

            // Response Header
            BufferedOutputStream out = getOutputStream(socket);
            if(out == null) return;
            sendResponse(out, RESPONSE_HEADER);

            // generate each part of multipart
            // and return it
            while(isRunning){

                byte[] frame = getFrame();

                // Part Header
                String strLength = String.valueOf(frame.length);
               String header = String.format(PART_HEADER_TEMPLATE, strLength);
                sendResponse(out, header);
       
                // JPEG Frame
                sendFrame(out, frame);

                // Part End
                sendResponse(out, PART__END);

                // sleep a little
                sleep(SLEEP);
            }// while

        // close OutputStream
        closeOutputStream(out);

    }// responseForVideo


/**
 *  getOutputStream
 */ 
private BufferedOutputStream getOutputStream(Socket socket) {
        BufferedOutputStream stream = null;
        try {
            stream = new BufferedOutputStream(socket.getOutputStream());
        }catch(Exception e){
            e.printStackTrace();
        }
        return stream;
}


/**
 *  closeOutputStream
 */ 
private void closeOutputStream(OutputStream out) {
        try{
                if(out != null) out.close();
        }catch(Exception e){
            // nop
        }
}


/**
 * sendResponse
 */ 
private void sendResponse(OutputStream out, String response) {

        try {
            byte[] bytes = response.getBytes("US-ASCII");
            out.write(bytes);
        }catch(IOException e){
            e.printStackTrace();
        }
}


/**
 * sendFrame
 */ 
private void sendFrame(OutputStream out, byte[] frame) {
        try {
            out.write(frame, 0, frame.length);
        }catch(IOException e){
            e.printStackTrace();
        }
}


/**
 * sleep
 */ 
private void sleep( int time ){


    try{
        Thread.sleep(time);
    }catch(InterruptedException e){
            // nop
        }
}

/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d

}// class MJpegServer
