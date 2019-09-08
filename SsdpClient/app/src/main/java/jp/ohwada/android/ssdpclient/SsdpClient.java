/**
 * SSDP Client
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.ssdpclient;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * class SsdpClient
 * send SSDP Discovery command on UDP
 * recieve responses from all devices on MulticastSocket
 * reference : https://github.com/berndverst/android-ssdp
 */
public class SsdpClient {


    // debug
	private final static boolean D = true;
    private final static String TAG = "SSDP";
    private final static String TAG_SUB = "SsdpClient";

/**
  * socket param
 */
    private final static int SOCKET_TIMEOUT = 3000;


 /**
     * UPnP param
     */
    private final static String UPNP_IP = "239.255.255.250";
    private final static int UPNP_PORT = 1900;

    private final static int RECEIVING_DATA_SIZE = 2048; 

    // 5 sec
    private final static long TIMEOUT_RECIVNG = 5000;


 /**
     * Request
     */
    private final static int SERCH_TIMEOUT = 100;
    private final static String ST_ALL = "ssdp:all";

    private final static String SEARCH_PORT = Integer.toString(UPNP_PORT);
    private final static String MX = Integer.toString(SERCH_TIMEOUT);

    private final static String LF = "\n";
    private final static String CRLF = "\r\n";
    private final static String REQUEST =
                        "M-SEARCH * HTTP/1.1" + CRLF 
            + "HOST: " + UPNP_IP + ":" + SEARCH_PORT + CRLF
            + "MAN: \"ssdp:discover\""+ CRLF 
            + "MX: " + MX + CRLF
            + "ST: " + ST_ALL + CRLF 
            + CRLF;



/**
  * interface DiscoveryCallback
 */	
public interface DiscoveryCallback {
    void onResult(List<SearchResult> list);
    // void onError(String error);
}


    private Context mContext;

    private DiscoveryTask mDiscoveryTask;

    private ProgressDialog mProgressDialog;


/**
  * Discovery Callback
 */
    private DiscoveryCallback  mCallback;


/**
 * constractor
 */
    public SsdpClient(Context context) {
        log_d("constractor");
        mContext = context;
        setUpProgressDialog(context);
    }


/** 
 *  cancel
 */
public void cancel() {
        log_d("cancel");
        if ( mDiscoveryTask != null) {
                mDiscoveryTask.cancel(true);
        }
        if( mProgressDialog != null) {
                mProgressDialog.dismiss();
        }
}


/** 
 *  setUpProgressDialog
 */
protected void setUpProgressDialog(Context context) {
        log_d("setUpProgressDialog");
        Resources res = context.getResources();
        String message = res.getString(R.string.progress_dialog_message);

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

} // setUpProgressDialog


/**
 * search 
 */
    public void discovery(DiscoveryCallback callback) {

        log_d("discovery");
        mCallback = callback;

        mDiscoveryTask = new DiscoveryTask();
        mDiscoveryTask.execute();

    }


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/** 
 *  class DiscoveryTask
 */
public  class DiscoveryTask extends AsyncTask<Object, Void, List<SearchResult>> {


/**
 * Socket, Lock
  */ 
    private MulticastSocket mSocket;
    private WifiManager.MulticastLock mMulticastLock;
    private PowerManager.WakeLock mWakeLock;

     private InetAddress mInetAddress;


/** 
 *  onPreExecute
 */
    @Override
    protected void onPreExecute() {
        log_d("onPreExecute");
        mProgressDialog.show();
    }

/** 
 *  doInBackground
 */
    @Override
    protected List<SearchResult> doInBackground(Object[] params) {
        log_d("doInBackground");

        InetSocketAddress inetSocketAddress = 
                    new InetSocketAddress(UPNP_IP, UPNP_PORT);
        mInetAddress = inetSocketAddress.getAddress();
    
        List<SearchResult> list = new ArrayList<SearchResult>();

        mMulticastLock = createMulticastLock(mContext);
        mWakeLock = createWakeLock(mContext);

        mSocket = openSocket( mInetAddress );

        sendRequest(mSocket, mInetAddress);

        HashSet<String> addresses = new HashSet<>();

        long startTime = System.currentTimeMillis();
        long curTime = System.currentTimeMillis();

         // Let's consider all the responses we can get in 1 second
        while (curTime - startTime < TIMEOUT_RECIVNG) {

                    curTime = System.currentTimeMillis();
                    DatagramPacket  packet = reciveResponse(mSocket);
                    String hostAddress = getHostAddress(packet);
                    //log_d("hostAddress= " + hostAddress);

                    if(addresses.contains(hostAddress)) {
                        // ignore if already received
                        continue;
                    }

                    addresses.add(hostAddress);
                    SearchResult searchResult =  createSearchResult(packet);
                    if(searchResult != null) {
                            list.add(searchResult);
                    }  
        } // while

    closeSocket(mInetAddress);
    closeLock();

    return list;
    }

/** 
 *  onPostExecute
 */
        protected void onPostExecute(List<SearchResult> list) {
                log_d("onPostExecute");
                if (mCallback != null ) {
                    mCallback.onResult(list);
                }
                mProgressDialog.dismiss();
        } // onPostExecute

/** 
 *  onCancelled
 */
    @Override
    protected void onCancelled() {
    log_d("onCancelled");
                    mProgressDialog.dismiss();
                    closeSocket(mInetAddress);
                    closeLock();
    }

/** 
 *  createMulticastLock
 */
private WifiManager.MulticastLock createMulticastLock(Context context) {
        log_d("createMulticastLock");
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE );

        WifiManager.MulticastLock lock = wifi.createMulticastLock("Multicast-Lock");;
        if(lock != null) {
                lock.acquire();
        }
    return lock;
}


/** 
 *  createWakeLock
 */
private PowerManager.WakeLock createWakeLock(Context context) {
        log_d("createWakeLock");
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock lock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLock");

        if(lock != null) {
                lock.acquire();
        }
    return lock;
}


/** 
 *  openSocket
 */
private MulticastSocket openSocket(InetAddress inetAddress) {
        log_d("openSocket");
    MulticastSocket socket = null;
    try {
           socket = new MulticastSocket(UPNP_PORT);
          socket.setReuseAddress(true);
            socket.joinGroup(inetAddress);
            socket.setSoTimeout(SOCKET_TIMEOUT);
    } catch (BindException e1) {
                e1.printStackTrace();
    } catch (SocketException e2) {
                e2.printStackTrace();
    } catch (IOException e3) {
                e3.printStackTrace();
    }
    return socket;
}


/** 
 *  closeSocket
 */
private void closeSocket(InetAddress inetAddress) {
    try {
            if(mSocket != null) {
                if(inetAddress != null) {
                    mSocket.leaveGroup(mInetAddress);
                }
                mSocket.close();
                mSocket = null;
            }
    } catch (Exception e) {
        // nop
    }
}


/** 
 *  closeLock
 */
private void closeLock() {
    try {
            if (mMulticastLock != null) {
                mMulticastLock.release();
                mMulticastLock = null;
            }
            if (mWakeLock != null) {
                mWakeLock.release();
                mWakeLock = null;
            }
    } catch (Exception e) {
            // nop
    }
}


/** 
 *  sendRequest
 */
private void sendRequest(MulticastSocket socket, InetAddress inetAddress) {
    log_d("sendRequest");
    try {
                DatagramPacket queryPacket = new DatagramPacket(REQUEST.getBytes(), REQUEST.length(),
                        inetAddress, UPNP_PORT);
                socket.send(queryPacket);
    } catch (IOException e) {
                e.printStackTrace();
    }

}


/** 
 *  reciveResponse
 */
private DatagramPacket reciveResponse(MulticastSocket socket) {
    // log_d("reciveResponse");

        byte[] buffer = new byte[RECEIVING_DATA_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, RECEIVING_DATA_SIZE);

    try {
            socket.receive(packet);
    } catch (IOException e) {
            e.printStackTrace();
    }

    return packet;
}


/** 
 *  getHostAddress
 */
private String getHostAddress(DatagramPacket packet) {
    String address = null;
    try {
            address = packet.getAddress().getHostAddress();
    } catch (Exception e) {
            e.printStackTrace();
    }
    return address;
}


/**
 * createSearchResult
 */
private SearchResult createSearchResult(DatagramPacket packet) {

        String hostAddress = getHostAddress(packet);

        SearchResult searchResult = null;

        try {
                String data = new String(packet.getData(), 0, packet.getLength());
                searchResult = createSearchResult(data);
                searchResult.setIpAddr(hostAddress);
        } catch (Exception e) {
                e.printStackTrace();
        }

        return searchResult;
}


/**
 * createSearchResult
 */ 
private SearchResult createSearchResult(String message) {

    String ST = "ST:";
    String BLANK = "";

    SearchResult searchResult = null;
    String[] lines = message.split(LF);

    for(String line: lines) {
            line = line.trim();
            if( line.startsWith(ST) ) {
line =          line = line.replace(ST, BLANK);
                    searchResult = parseST(line);
                    searchResult.setMessage(message);
            } 
    } // for

   return searchResult;
}



/**
 * parseST
 */ 
private SearchResult parseST(String line) {
        log_d("parseST: " + line);
        String ST_PATTERN1 = "urn:(.*):device:(.*):service:(.*)";
        String ST_PATTERN2 = "urn:(.*):device:(.*):(.*)";
        String ST_PATTERN3 = "urn:(.*):service:(.*):(.*)";
        String ST_PATTERN4 = "upnp:(.*)";

            String domain = null;
            String device = null;
            String service = null;
            String version = null;
    try {
            Pattern p1 = Pattern.compile(ST_PATTERN1);
            Matcher m1 = p1.matcher(line);
            if (m1.find()){
                domain = m1.group(1);
                device = m1.group(2);
                service = m1.group(3);
                version = m1.group(4);
            }
            Pattern p2 = Pattern.compile(ST_PATTERN2);
            Matcher m2 = p2.matcher(line);
            if (m2.find()){
                domain = m2.group(1);
                device = m2.group(2);
                version = m2.group(3);
            }

            Pattern p3 = Pattern.compile(ST_PATTERN3);
            Matcher m3 = p3.matcher(line);
            if (m3.find()){
                domain = m3.group(1);
                service = m3.group(2);
                version = m3.group(3);
            }
            Pattern p4 = Pattern.compile(ST_PATTERN4);
            Matcher m4 = p4.matcher(line);
            if (m4.find()){
                device = m4.group(1);
            }
    } catch (Exception e) {
                e.printStackTrace();
    }
log_d("domain="+ domain + " device="+ device + " service="+ service + " version="+ version);
        SearchResult searchResult = new SearchResult(device, service);
        return searchResult;
} // parseST


} // class DiscoveryTask


} // class SsdpClient
