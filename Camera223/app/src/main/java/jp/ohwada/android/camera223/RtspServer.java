/**
 * Camera2 Sample
 * RTSP Server
 *  added for Camera2 API
 * 2019-08-01 K.OHWADA
 */ 
package jp.ohwada.android.camera223;


/*
 * Copyright (C) 2011-2015 GUIGUI Simon, fyhertz@gmail.com
 *
 * This file is part of libstreaming (https://github.com/fyhertz/libstreaming)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// package net.majorkernelpanic.streaming.rtsp;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;


import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.rtsp.UriParser;


/**
 * class RtspServer
 * 
 * Implementation of a subset of the RTSP protocol (RFC 2326).
 * It allows remote control of an android device cameras & microphone.
 * For each connected client, a Session is instantiated.
 * The Session will start or stop streams according to what the client wants.
 * original : https://github.com/fyhertz/libstreaming
 */
// public class RtspServer extends Service {
public class RtspServer  {


    // debug
	private final static boolean D = true;
    private final static String TAG = "Camera2";
    private final static String TAG_SUB = "RtspServer";


	/** The server name that will appear in responses. */
	public static String SERVER_NAME = "MajorKernelPanic RTSP Server";

	/** Port used by default. */
	public static final int DEFAULT_RTSP_PORT = 8086;

	/** Port already in use. */
	public final static int ERROR_BIND_FAILED = 0x00;

	/** A stream could not be started. */
	public final static int ERROR_START_FAILED = 0x01;

	/** Streaming started. */
	public final static int MESSAGE_STREAMING_STARTED = 0X00;
	
	/** Streaming stopped. */
	public final static int MESSAGE_STREAMING_STOPPED = 0X01;
	
	/** Key used in the SharedPreferences to store whether the RTSP server is enabled or not. */
	public final static String KEY_ENABLED = "rtsp_enabled";

	/** Key used in the SharedPreferences for the port used by the RTSP server. */
	public final static String KEY_PORT = "rtsp_port";


	protected SessionBuilder mSessionBuilder;
	protected SharedPreferences mSharedPreferences;
	protected boolean mEnabled = true;	
	protected int mPort = DEFAULT_RTSP_PORT;
	protected WeakHashMap<Session,Object> mSessions = new WeakHashMap<>(2);
	
	private Session mSession;

	private RequestListener mListenerThread;

	private boolean mRestart = false;
	private final LinkedList<CallbackListener> mListeners = new LinkedList<>();

    /** Credentials for Basic Auth */
    private String mUsername;
    private String mPassword;

    private WorkerThread mWorkerThread;
    private ServerSocket mServer = null;

    private boolean isRunning = false;


/**
 * ConnectCallback
 */
	public interface ConnectCallback {
		void onConnect(String clientAddress);
		void onDisConnect();
	}

    private ConnectCallback mConnectCallback;

/**
 * csetConnectCallback
 */ 
    public void setConnectCallback(ConnectCallback callback) {
        mConnectCallback = callback;
    }


/**
 * constractor
 */ 
    public RtspServer() {
        // nop
    }





/**
 * setPreviewFrame
 */ 
    public void setPreviewFrame(byte[] data, long timestamp) {
        if(mSession != null) {
            mSession.setPreviewFrame(data, timestamp);
        }
    }





	/** Be careful: those callbacks won't necessarily be called from the ui thread ! */
	public interface CallbackListener {

/**
 * Called when an error occurs.
 */
		void onError(RtspServer server, Exception e, int error);

/**
 * Called when streaming starts/stops.
 */
		void onMessage(RtspServer server, int message);
	}



	/**
	 * See {@link RtspServer.CallbackListener} to check out what events will be fired once you set up a listener.
	 * @param listener The listener
	 */
	public void addCallbackListener(CallbackListener listener) {
log_d("addCallbackListener");
		synchronized (mListeners) {
			if (!mListeners.isEmpty()) {
				for (CallbackListener cl : mListeners) {
					if (cl == listener) return;
				}
			}
			mListeners.add(listener);			
		}
	}

	/**
	 * Removes the listener.
	 * @param listener The listener
	 */
	public void removeCallbackListener(CallbackListener listener) {
	        log_d("removeCallbackListener");	
		synchronized (mListeners) {
			mListeners.remove(listener);				
		}
	}

	/** Returns the port used by the RTSP server. */	
	public int getPort() {
	        log_d("getPort");	
		return mPort;
	}

	/**
	 * Sets the port for the RTSP server to use.
	 * @param port The port
	 */
	public void setPort(int port) {
	        log_d("setPort: " + port);	
		Editor editor = mSharedPreferences.edit();
		editor.putString(KEY_PORT, String.valueOf(port));
		editor.commit();
	}

    /**
     * Set Basic authorization to access RTSP Stream
     * @param username username
     * @param password password
     */
    public void setAuthorization(String username, String password)
    {
	        log_d("setAuthorization");	
        mUsername = username;
        mPassword = password;
    }

/** 
 * Starts (or restart if needed, if for example the configuration 
 * of the server has been modified) the RTSP server. 
 */
	public void start() {
	        log_d("start");
            isRunning = true;	
		if (mEnabled && mListenerThread == null) {
			try {
				mListenerThread = new RequestListener();
                mListenerThread.start();
			} catch (Exception e) {
                    e.printStackTrace();
				//mListenerThread = null;
			}
		}
		//mRestart = false;
	}

	/** 
	 * Stops the RTSP server but not the Android Service. 
	 * To stop the Android Service you need to call {@link android.content.Context#stopService(Intent)}; 
	 */



/**
 * stop
 */ 
	public void stop() {
	    log_d("stop");	
        isRunning = false;
        try {
            if(mServer !=null) {
				    mServer.close();
            }
            if(mWorkerThread !=null) {
				    mWorkerThread.join();
                    mWorkerThread = null;
            }
		    if (mListenerThread != null) {
				mListenerThread.join();
				mListenerThread = null;
            }
			for ( Session session : mSessions.keySet() ) {
				    if ( session != null && session.isStreaming() ) {
						session.stop();
				    } 
			}
        } catch (Exception e) {
				// nop
        }
}


	/** Returns whether or not the RTSP server is streaming to some client(s). */
	public boolean isStreaming() {
	        log_d(" isStreaming");	
		for ( Session session : mSessions.keySet() ) {
		    if ( session != null && session.isStreaming() ) {
		    	return true;
		    } 
		}
		return false;
	}

/**
 * isEnabled
 */ 	
	public boolean isEnabled() {
	        log_d("isEnabled");	
		return mEnabled;
	}

	/** Returns the bandwidth consumed by the RTSP server in bits per second. */
	public long getBitrate() {
	        log_d("getBitrate");	
		long bitrate = 0;
		for ( Session session : mSessions.keySet() ) {
		    if ( session != null && session.isStreaming() ) {
		    	bitrate += session.getBitrate();
		    } 
		}
		return bitrate;
	}
	
/**
 * postMessage
 */ 
	protected void postMessage(int id) {
	        log_d("postMessage: " + id);	
		synchronized (mListeners) {
			if (!mListeners.isEmpty()) {
				for (CallbackListener cl : mListeners) {
					// cl.onMessage(this, id);
				}
			}			
		}
	}	
	
/**
 * postError
 */ 
	protected void postError(Exception exception, int id) {
		synchronized (mListeners) {
	        log_d("postError: " + id);	
			if (!mListeners.isEmpty()) {
				for (CallbackListener cl : mListeners) {
					// cl.onError(this, exception, id);
				}
			}			
		}
	}

	/** 
	 * By default the RTSP uses {@link UriParser} to parse the URI requested by the client
	 * but you can change that behavior by override this method.
	 * @param uri The uri that the client has requested
	 * @param client The socket associated to the client
	 * @return A proper session
	 */
	protected Session handleRequest(String uri, Socket client) throws IllegalStateException, IOException {
	        log_d("handleRequest: " +  uri);	
		Session session = UriParser.parse(uri);
		session.setOrigin(client.getLocalAddress().getHostAddress());
		if (session.getDestination()==null) {
			session.setDestination(client.getInetAddress().getHostAddress());
		}
		return session;
	}


/**
 * write into logcat
 */ 
private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
} // log_d


/**
 * class RequestListener
 */	
	class RequestListener extends Thread implements Runnable {

		// private final ServerSocket mServer;

/**
 * constractor
 */ 
		public RequestListener() throws IOException {
			log_d("RequestListener");
			try {
				mServer = new ServerSocket(mPort);
				//start();
			} catch (BindException e) {
                e.printStackTrace();
				//log_d("Port already in use !");
				postError(e, ERROR_BIND_FAILED);
				throw e;
			}
		}

/**
 * run
 */ 
		public void run() {
			log_d("RTSP server listening on port " + mServer.getLocalPort());
            Socket client = null;
			while (isRunning) {
			        log_d("waiting");
				    try {
                        // waiting for connect
                        client = mServer.accept();
                        if(client != null) {
                            mWorkerThread = new WorkerThread(client);
                            mWorkerThread.start();
                        }
				    } catch (IOException e) {
                            e.printStackTrace();
				    }
			} // while
			log_d("RTSP server stopped !");
} // run



} // class RequestListener


/**
 * class WorkerThread
 * One thread per client
 */
	class WorkerThread extends Thread implements Runnable {

		private final Socket mClient;
		private final OutputStream mOutput;
		private final BufferedReader mInput;

/**
 * constractor
 * Each client has an associated session
 * private Session mSession;
 */ 
		public WorkerThread(final Socket client) throws IOException {
	        log_d("WorkerThread");	
			mInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
			mOutput = client.getOutputStream();
			mClient = client;
			mSession = new Session();
		}



		public void run() {
	        log_d("WorkerThread run");	
			Request request;
			Response response;
            String clientAddress = mClient.getInetAddress().getHostAddress();
			log_d("Connection from " + clientAddress);

        if (mConnectCallback != null) {
            mConnectCallback.onConnect(clientAddress);
        }

			while (isRunning) {

				request = null;
				response = null;

				// Parse the request
				try {
					request = Request.parseRequest(mInput);
				} catch (SocketException e) {
					// Client has left
					break;
				} catch (Exception e) {
					// We don't understand the request :/
					response = new Response();
					response.status = Response.STATUS_BAD_REQUEST;
				}

				// Do something accordingly like starting the streams, sending a session description
				if (request != null) {
					try {
						response = processRequest(request);
					}
					catch (Exception e) {
						// This alerts the main thread that something has gone wrong in this thread
						postError(e, ERROR_START_FAILED);
String  errorMessage = e.getMessage();
String msg = (errorMessage != null)? errorMessage :"An error occurred";
						log_d(msg);
						e.printStackTrace();
						response = new Response(request);
					}
				}

				// We always send a response
				// The client will receive an "INTERNAL SERVER ERROR" if an exception has been thrown at some point
				try {
					response.send(mOutput);
				} catch (IOException e) {
					log_d("Response was not sent properly");
					break;
				}

			}

			// Streaming stops when client disconnects
			boolean streaming = isStreaming();
			mSession.syncStop();
			if (streaming && !isStreaming()) {
				postMessage(MESSAGE_STREAMING_STOPPED);
			}
			mSession.release();

			try {
				mClient.close();
			} catch (IOException ignore) {}

			log_d("Client disconnected");

		}

/**
 * processRequest
 */
		public Response processRequest(Request request) throws IllegalStateException, IOException {
			log_d("processRequest");
			Response response = new Response(request);

            //Ask for authorization unless this is an OPTIONS request
            if(!isAuthorized(request) && !request.method.equalsIgnoreCase("OPTIONS"))
            {
                response.attributes = "WWW-Authenticate: Basic realm=\""+SERVER_NAME+"\"\r\n";
                response.status = Response.STATUS_UNAUTHORIZED;
            }
            else
            {
			    /* ********************************************************************************** */
			    /* ********************************* Method DESCRIBE ******************************** */
			    /* ********************************************************************************** */
                if (request.method.equalsIgnoreCase("DESCRIBE")) {
                    log_d("method: DESCRIBE" );
                    // Parse the requested URI and configure the session
                    mSession = handleRequest(request.uri, mClient);
                    mSessions.put(mSession, null);
                    mSession.syncConfigure();

                    String requestContent = mSession.getSessionDescription();
                    log_d("requestContent: " + requestContent);
                    String requestAttributes =
                            "Content-Base: " + mClient.getLocalAddress().getHostAddress() + ":" + mClient.getLocalPort() + "/\r\n" +
                                    "Content-Type: application/sdp\r\n";

                    response.attributes = requestAttributes;
                    response.content = requestContent;

                    // If no exception has been thrown, we reply with OK
                    response.status = Response.STATUS_OK;

                }

                /* ********************************************************************************** */
                /* ********************************* Method OPTIONS ********************************* */
                /* ********************************************************************************** */
                else if (request.method.equalsIgnoreCase("OPTIONS")) {
                    log_d("method: OPTIONS" );
                    response.status = Response.STATUS_OK;
                    response.attributes = "Public: DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE\r\n";
                    response.status = Response.STATUS_OK;
                }

                /* ********************************************************************************** */
                /* ********************************** Method SETUP ********************************** */
                /* ********************************************************************************** */
                else if (request.method.equalsIgnoreCase("SETUP")) {
                    log_d("method: SETUP: " + request.uri);
                    Pattern p;
                    Matcher m;
                    int p2, p1, ssrc, trackId, src[];
                    String destination;

                    p = Pattern.compile("trackID=(\\w+)", Pattern.CASE_INSENSITIVE);
                    m = p.matcher(request.uri);

                    if (!m.find()) {
                        response.status = Response.STATUS_BAD_REQUEST;
                        return response;
                    }

                    trackId = Integer.parseInt(m.group(1));

                    if (!mSession.trackExists(trackId)) {
                        response.status = Response.STATUS_NOT_FOUND;
                        return response;
                    }

                    p = Pattern.compile("client_port=(\\d+)(?:-(\\d+))?", Pattern.CASE_INSENSITIVE);
                    m = p.matcher(request.headers.get("transport"));

                    if (!m.find()) {
                        int[] ports = mSession.getTrack(trackId).getDestinationPorts();
                        p1 = ports[0];
                        p2 = ports[1];
                        log_d("p1= " + p1 + " , p2= " + p2 );
                    } else {
                        p1 = Integer.parseInt(m.group(1));
                        if (m.group(2) == null) {
                            p2 = p1+1;
                        } else {
                            p2 = Integer.parseInt(m.group(2));
                        }
                    }

                    ssrc = mSession.getTrack(trackId).getSSRC();
                    src = mSession.getTrack(trackId).getLocalPorts();
                    destination = mSession.getDestination();

                    mSession.getTrack(trackId).setDestinationPorts(p1, p2);

                    boolean streaming = isStreaming();
                    mSession.syncStart(trackId);
                    if (!streaming && isStreaming()) {
                        postMessage(MESSAGE_STREAMING_STARTED);
                    }

                    response.attributes = "Transport: RTP/AVP/UDP;" + (InetAddress.getByName(destination).isMulticastAddress() ? "multicast" : "unicast") +
                            ";destination=" + mSession.getDestination() +
                            ";client_port=" + p1 + "-" + p2 +
                            ";server_port=" + src[0] + "-" + src[1] +
                            ";ssrc=" + Integer.toHexString(ssrc) +
                            ";mode=play\r\n" +
                            "Session: " + "1185d20035702ca" + "\r\n" +
                            "Cache-Control: no-cache\r\n";
                    response.status = Response.STATUS_OK;

                    // If no exception has been thrown, we reply with OK
                    response.status = Response.STATUS_OK;

                }

                /* ********************************************************************************** */
                /* ********************************** Method PLAY *********************************** */
                /* ********************************************************************************** */
                else if (request.method.equalsIgnoreCase("PLAY")) {
                    log_d("method: PLAY: " + request.uri);
                    String requestAttributes = "RTP-Info: ";
                    if (mSession.trackExists(0))
                        requestAttributes += "url=rtsp://" + mClient.getLocalAddress().getHostAddress() + ":" + mClient.getLocalPort() + "/trackID=" + 0 + ";seq=0,";
                    if (mSession.trackExists(1))
                        requestAttributes += "url=rtsp://" + mClient.getLocalAddress().getHostAddress() + ":" + mClient.getLocalPort() + "/trackID=" + 1 + ";seq=0,";
                    requestAttributes = requestAttributes.substring(0, requestAttributes.length() - 1) + "\r\nSession: 1185d20035702ca\r\n";

                    response.attributes = requestAttributes;

                    // If no exception has been thrown, we reply with OK
                    response.status = Response.STATUS_OK;

                }

                /* ********************************************************************************** */
                /* ********************************** Method PAUSE ********************************** */
                /* ********************************************************************************** */
                else if (request.method.equalsIgnoreCase("PAUSE")) {
                    log_d("method: PAUSE" );
                    response.status = Response.STATUS_OK;
                }

                /* ********************************************************************************** */
                /* ********************************* Method TEARDOWN ******************************** */
                /* ********************************************************************************** */
                else if (request.method.equalsIgnoreCase("TEARDOWN")) {
                    log_d("method: TEARDOWN" );
                    response.status = Response.STATUS_OK;
                }

                /* ********************************************************************************** */
                /* ********************************* Unknown method ? ******************************* */
                /* ********************************************************************************** */
                else {
                    log_d("Command unknown: " + request);
                    response.status = Response.STATUS_BAD_REQUEST;
                }
            }
			return response;

		}

        /**
         * Check if the request is authorized
         * @param request
         * @return true or false
         */
        private boolean isAuthorized(Request request)
        {
                    log_d("isAuthorized" );
            String auth = request.headers.get("authorization");
            if(mUsername == null || mPassword == null || mUsername.isEmpty())
                return true;

            if(auth != null && !auth.isEmpty())
            {
                String received = auth.substring(auth.lastIndexOf(" ")+1);
                String local = mUsername+":"+mPassword;
                String localEncoded = Base64.encodeToString(local.getBytes(),Base64.NO_WRAP);
                if(localEncoded.equals(received))
                    return true;
            }

            return false;
        }
} // class WorkerThread


/**
 * class Request
 */
	static class Request {

		// Parse method & uri
		public static final Pattern regexMethod = Pattern.compile("(\\w+) (\\S+) RTSP",Pattern.CASE_INSENSITIVE);
		// Parse a request header
		public static final Pattern rexegHeader = Pattern.compile("(\\S+):(.+)",Pattern.CASE_INSENSITIVE);

		public String method;
		public String uri;
		public HashMap<String,String> headers = new HashMap<>();

		/** Parse the method, uri & headers of a RTSP request */
		public static Request parseRequest(BufferedReader input) throws IOException, IllegalStateException, SocketException {
            log_d("parseRequest");
			Request request = new Request();
			String line;
			Matcher matcher;

			// Parsing request method & uri
			if ((line = input.readLine())==null) {
                throw new SocketException("Client disconnected");
            }
			matcher = regexMethod.matcher(line);
			matcher.find();
			request.method = matcher.group(1);
			request.uri = matcher.group(2);

			// Parsing headers of the request
			while ( (line = input.readLine()) != null && line.length()>3 ) {
				matcher = rexegHeader.matcher(line);
				matcher.find();
				request.headers.put(matcher.group(1).toLowerCase(Locale.US),matcher.group(2));
			}
			if (line==null) throw new SocketException("Client disconnected");

			// It's not an error, it's just easier to follow what's happening in logcat with the request in red
			log_d("method: " + request.method+" "+request.uri);

			return request;
		}
} // class Request


/**
 * class Response
 */
	static class Response {

		// Status code definitions
		public static final String STATUS_OK = "200 OK";
		public static final String STATUS_BAD_REQUEST = "400 Bad Request";
        public static final String STATUS_UNAUTHORIZED = "401 Unauthorized";
		public static final String STATUS_NOT_FOUND = "404 Not Found";
		public static final String STATUS_INTERNAL_SERVER_ERROR = "500 Internal Server Error";

		public String status = STATUS_INTERNAL_SERVER_ERROR;
		public String content = "";
		public String attributes = "";

		private final Request mRequest;


/**
 * constractor
 */ 
		public Response(Request request) {
			this.mRequest = request;
		}

/**
 * constractor
 */ 
		public Response() {
			// Be carefull if you modify the send() method because request might be null !
			mRequest = null;
		}

/**
 * send
 */ 

		public void send(OutputStream output) throws IOException {
log_d("send");
			int seqid = -1;

			try {
				seqid = Integer.parseInt(mRequest.headers.get("cseq").replace(" ",""));
			} catch (Exception e) {
				Log.e(TAG,"Error parsing CSeq: "+(e.getMessage()!=null?e.getMessage():""));
			}

			String response = 	"RTSP/1.0 "+status+"\r\n" +
					"Server: "+SERVER_NAME+"\r\n" +
					(seqid>=0?("Cseq: " + seqid + "\r\n"):"") +
					"Content-Length: " + content.length() + "\r\n" +
					attributes +
					"\r\n" + 
					content;

			log_d("response: " + response.replace("\r", ""));

			output.write(response.getBytes());
		}

} // class Response


} // class RtspServer
