/**
 * SSDP Client
 * 2019-08-01 K.OHWADA
 */
package jp.ohwada.android.ssdpclient;


/**
 * class SearchResult
 */
public class SearchResult {

    private static final String LF = "\n";

    private String  ipAddr;

    private String message;

    private String device;

    private String service;

/** 
 * constractor
 */
public SearchResult(String _device, String _service) {
    device = _device;
    service = _service;
}


/** 
 *  setIpAddr
 */
public void setIpAddr(String _ipAddr) {
    ipAddr = _ipAddr;
}


/** 
 *  setMessage
 */
public void setMessage(String _message) {
    message = _message;
}

/** 
 *  getTitle
 */
public String getTitle() {
    return ipAddr;
}

/** 
 *  getMessage
 */
public String getMessage() {
    return message;
}

/** 
 *  getLabel
 */
public String getLabel() {
    String ret = ipAddr + LF;
    if( device != null) {
            ret += device + LF;
    }
    if( service != null) {
            ret += service + LF;
    }
    return ret;
}

} // class class SearchResult
