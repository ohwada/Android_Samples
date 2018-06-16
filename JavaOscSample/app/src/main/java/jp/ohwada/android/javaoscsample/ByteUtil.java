

package jp.ohwada.android.javaoscsample;

import java.util.ArrayList;
import java.util.List;

/**
 * class ByteUtil
 */

public class ByteUtil {

    // debug
 	private final static String LF = "\n";

 	private final static String SPACE = " ";

/**
 * toDebug
 */ 
public  String toDebug(byte[] bytes) {
    String msg =  toString(bytes) + LF;
    for(byte b: bytes) {
        msg += toHex(b) + SPACE + toChar(b) + LF;
    }
    return msg;
}

/**
 * toString
 */ 
public String  toString(byte[] bytes) {
    return new String(bytes);
} // toString

/**
 * toHex
 */ 
private String toHex(byte b) {
    return String.format("%02x", b);
} // toHex

/**
 * toChar
 */ 
public static String toChar(byte b) {
    char c = (char)b;
    return String.valueOf(c);
} // toChar

/**
 * class Ascii
 */ 
public class Ascii {
    public byte b;
    public String h;
    public String c;

/**
 * constractor
 */ 
public Ascii() {
    // nothing to do
} // constractor
/**
 * constractor
 */ 
public Ascii(byte _b, String _h, String _c) {
    this.b = _b;
    this.h = _h;
    this.c = _c;
} // constractor

} // class Ascii


} // class ByteUtil
