/**
 * Camera2 Sample
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.camera219;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;


// TODO : remove org.apache.http at API 23
import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.impl.io.HttpRequestParser;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;


/**
 * class SsdpAdvertiser 
 * waiting to recieve Request with MulticastSocket on UDP 
 * return to  send Response to requester
 * original : https://github.com/arktronic/cameraserve
 */
public class SsdpAdvertiser implements Runnable {

    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "SsdpAdvertiser";


/**
  * socket param
 */
    private final static int SOCKET_TIMEOUT = 3000;
    private final static int SOCKET_SLEEP = 700;
    private final static int RECEIVING_DATA_SIZE = 2048; 
    private final static int INPUT_BUF_SIZE = 128;


/**
  * Service Type 
 */
    private final  static String MY_DOMAIN = "android-exsample-com";
    private final  static String MY_SERVICE = "camera-mjpeg";
    private final  static String MY_SERVICE_TYPE = "urn:" + MY_DOMAIN + ":service:" + MY_SERVICE + ":1";

    private final  static String MY_SERVER = "CameraMipegServer/" + BuildConfig.VERSION_NAME ;


/**
  * Recieve
 */
    private final  static String HEADER_MAN = "man";
    private final  static String MAN_DISCOVER = "\"ssdp:discover\"";

    private final  static String ST_SSDP_ALL = "ssdp:all";
    private final  static String METHOD_M_SEARCH  = "m-search";
    private final  static String METHOD_NOTIFY = "notify";
    private final  static String URI_ASTERISK = "*";

    private final  static String ST_UUID_FORMAT = "uuid:%s";


/**
  * Response
 */
    private final static String CRLF = "\r\n";

    private final static String RESPONSE_TEMPLATE = "HTTP/1.1 200 OK" + CRLF
    + "Ext: " + CRLF
    + "Cache-Control: max-age=120, no-cache=\"Ext\"" + CRLF
    + "ST: " + MY_SERVICE_TYPE + CRLF
    + "USN: %s::" + MY_SERVICE_TYPE + CRLF
    + "Server: " + MY_SERVER + CRLF
    + "X-Stream-Location: http://%s:%s/" + CRLF + CRLF;


    /**
     * UPnP param
     */
    private final static String UPNP_IP = "239.255.255.250";
    private final static int UPNP_PORT = 1900;
    private static InetSocketAddress UPNP_SOCKET_ADDRESS = new InetSocketAddress(UPNP_IP, UPNP_PORT);


/**
 * Socket Lock
  */ 
    private  MulticastSocket mMulticastSocket;
    private WifiManager.MulticastLock mMulticastLock;
    private PowerManager.WakeLock mWakeLock;


/**
 * setting param
  */ 
        private String mIpAddress;
        private String mPort;
        private String  mServiceId;

        private boolean isDiscoverable = false;

        private boolean isRunning = false;

/**
 * ststus
  */ 
    private boolean enabled = false;


/**
 * constractor
 */
public SsdpAdvertiser() {
    // nop
}


/**
 * setIpAddres
 */
    public  void setIpAddres(String addr) {
        mIpAddress = addr;
    }

/**
 * setPort
 */
    public  void setPort(String port) {
        mPort = port;
    }

/**
 * setDiscoverable
 */
    public  void setDiscoverable(boolean discoverable) {
        isDiscoverable = discoverable;
    }

/**
 * setServiceId
 */
    public  void setServiceId(String serviceId) {
            mServiceId = serviceId;
}

/**
 * start
 */
    public  void start() {
            isRunning = true;
}


/**
 * stop
 */
    public  void stop() {
            isRunning = false;
            teardown();
}


/**
 * run
 */
    @Override
    public void run() {
        log_d("run");
        try {
            setup();
            runLoop();
        } finally {
            teardown();
        }
    }


/**
 * setup
 */
    private void setup() {

        log_d("setup");
        String TAG_MULTICAST_LOCK = "SSDP-Lock";
        String TAG_WAKE_LOCK = "Multicast-Lock";
        Context ctx = AndroidApplication.getInstance().getApplicationContext();

        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        mMulticastLock = wifiManager.createMulticastLock(TAG_MULTICAST_LOCK);
        PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG_WAKE_LOCK);
    }

/**
 * runLoop
 */
private void runLoop() {
        log_d("runLoop");
        DatagramPacket receivingPacket = new DatagramPacket(new byte[RECEIVING_DATA_SIZE], RECEIVING_DATA_SIZE);

        while (isRunning) {
                    if (ConnectivityChangeReceiver.Changed) {
                        enabled = false;
                        ConnectivityChangeReceiver.Changed = false;
                    }
                    if (!enabled && isDiscoverable ) {
                        mMulticastSocket = openSocket();
                    } else if (enabled && !isDiscoverable) {
                        closeSocket();
                        releaseLock();
                        enabled = false;
                    }
                    if (enabled) {
                        receivePacket(mMulticastSocket, receivingPacket);
                    }
                    sleep(SOCKET_SLEEP);

        } // while
}

/**
 * acquireLock
 */
private void acquireLock() {
    try {
        if (!mMulticastLock.isHeld()) mMulticastLock.acquire();
        if (!mWakeLock.isHeld()) mWakeLock.acquire();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

/**
 * releaseLock
 */
private void releaseLock() {
    try {
        if (mMulticastLock.isHeld()) mMulticastLock.release();
        if (mWakeLock.isHeld()) mWakeLock.release();
    } catch (Exception e) {
                    e.printStackTrace();
    }
}

/**
 * openSocket
 */
private MulticastSocket  openSocket() {
        log_d("openSocket");
        MulticastSocket receivingSocket = null;
        InetSocketAddress inetSocketAddress = 
            new InetSocketAddress(UPNP_IP, UPNP_PORT);
        InetAddress inetAddress = inetSocketAddress.getAddress();

        try {
                    acquireLock();

                    receivingSocket = new MulticastSocket(UPNP_PORT);
                    receivingSocket.setReuseAddress(true);
                    receivingSocket.joinGroup(inetAddress);
                    receivingSocket.setSoTimeout(SOCKET_TIMEOUT);
                   enabled = true;
        } catch (IOException e) {
                    e.printStackTrace();
                    receivingSocket = null;
                    enabled = false;
                    releaseLock();
        } // try
        return receivingSocket;
}


/**
 * closeSocket
 */
private void closeSocket() {
                try {
                    if (mMulticastSocket != null) {
                        mMulticastSocket.leaveGroup(UPNP_SOCKET_ADDRESS.getAddress());
                        mMulticastSocket.close();
                        mMulticastSocket = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
}

/**
 * receivePacket
 */
private void receivePacket(MulticastSocket receivingSocket, DatagramPacket packet) {
                try {
                        receivingSocket.receive(packet);
                        processSsdpPacket(packet);
                } catch (SocketTimeoutException ste) {
                        // continue silently
                } catch (Exception e) {
                    e.printStackTrace();
                }
}

/**
 * processSsdpPacket
 */
private void processSsdpPacket(DatagramPacket packet) {

        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
        HttpMessage message = getHttpMessage(data);
        if (message == null) {
            return;
        }
        Header mandatoryExtensionHeader = message.getFirstHeader("MAN");
        if (mandatoryExtensionHeader == null) {
            return;
        }
        String manHeader = mandatoryExtensionHeader.getValue();
        if( !manHeader.equals(MAN_DISCOVER)) {
                log_d("man:" + manHeader);
                return;
        }

        Header serviceTypeHeader = message.getFirstHeader("ST");
        if (serviceTypeHeader == null) {
            return;
        }
        String stHeader = serviceTypeHeader.getValue();
        String st_uuid = String.format(ST_UUID_FORMAT, mServiceId );
        //log_d("st: " + stHeader );
        if ( stHeader.equals(ST_SSDP_ALL) || stHeader.equals(MY_SERVICE_TYPE) ||
        stHeader.equals(st_uuid) ) {
            sendResponse(packet);
        }
}


/**
 * sendResponse
 */
private void sendResponse(DatagramPacket packet) {

            String response = String.format(RESPONSE_TEMPLATE, mServiceId, mIpAddress, mPort);

            InetAddress packetAddress = packet.getAddress();
            int packetPort = packet.getPort();
            String	hostAddress = packetAddress.getHostAddress();
            log_d("send to " + hostAddress + " : " + packetPort);
            DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.length(), packetAddress, packetPort);

            try {
                DatagramSocket sendingSocket = new DatagramSocket();
                sendingSocket.send(responsePacket);
                sendingSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

}


/**
 * getHttpMessage
 */
    private HttpMessage getHttpMessage(final byte[] data) {
        //log_d("getHttpMessage");
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        final HttpParams httpParams = new BasicHttpParams();
        final AbstractSessionInputBuffer inputBuffer = new AbstractSessionInputBuffer() {
            {
                init( inputStream, INPUT_BUF_SIZE, httpParams );
            }

/**
 * isDataAvailable
 */
            @Override
            public boolean isDataAvailable(int i) throws IOException {
                log_d("isDataAvailable");
                return this.hasBufferedData();
            }

}; // AbstractSessionInputBuffer


/**
 * HttpRequestFactory
 */
        final HttpRequestFactory msearchRequestFactory = new HttpRequestFactory() {

/**
 * newHttpRequest
 */
            @Override
            public HttpRequest newHttpRequest(RequestLine requestLine) {
                //log_d("newHttpRequest");
                String method = requestLine.getMethod();
                if (! checkMethod(method) ) {
                        log_d("Invalid method: " + method);
                        return null;
                }

                String uri = requestLine.getUri();
                if (!uri.equals(URI_ASTERISK)) {
                        log_d("Invalid URI: " + uri);
                        return null;
                }

                return new BasicHttpRequest(requestLine);
            }


/**
 * newHttpRequest
 */
            @Override
            public HttpRequest newHttpRequest(String method, String uri) {   
                log_d("newHttpRequest");
                if (! checkMethod(method) ) {
                        log_d("Invalid method: " + method);
                        return null;
                }
                if (!uri.equals(URI_ASTERISK)){
                      log_d("Invalid URI: " + uri);
                        return null;
                }
                return new BasicHttpRequest(method, uri);
            }

/**
 * checkMethod
 */
    private boolean checkMethod(String method) {
                if ( method.equalsIgnoreCase(METHOD_M_SEARCH) ) {
                        return true;
                }
                if ( method.equalsIgnoreCase(METHOD_NOTIFY) ) {
                        return true;
                }
                return false;
    }


}; // HttpRequestFactory


        HttpRequestParser requestParser = new HttpRequestParser(inputBuffer, null, msearchRequestFactory, httpParams);
        try {
            return requestParser.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


/**
 * teardown
 */
    private void teardown() {
        log_d("teardown");
        releaseLock();
        closeSocket();
    }


/**
 *  sleep
 */
    private void sleep(int msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


/**
 * write into logcat
 */ 
private void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


} // class SsdpAdvertiser 
