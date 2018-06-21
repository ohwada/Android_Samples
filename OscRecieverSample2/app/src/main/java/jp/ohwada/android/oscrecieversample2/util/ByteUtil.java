/*
 * Java OSC Sample
 * 2018-05-01 K.OHWADA
 */

package jp.ohwada.android.oscrecieversample2.util;

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
 * toDebugSub
 * format the first num characters as debug
 */ 
public  String toDebugSub(byte[] bytes, int num) {
    int len = bytes.length;
    byte[] new_bytes = sub(bytes, num);
    String msg =  "length= " + len + LF;
    msg +=  toString(new_bytes) + LF;
    for(byte b: new_bytes) {
        msg += toDebugOneByte(b);
    }
    return msg;
} // toDebugSub


/**
 * sub
 * retrieve the first num characters
 */ 
public  byte[] sub(byte[] bytes, int num) {
    int len = bytes.length;
    if ( len <= num ) {
        return bytes;
    } // if
    byte[] new_bytes = new byte[num];
    for(int i = 0; i < num; i++ ) {
        new_bytes[i] = bytes[i];
    } // for
    return new_bytes;

} // sub

/**
 * toDebug
 */ 
public  String toDebug(byte[] bytes) {
    int len = bytes.length;
    String msg =  "length= " + len + LF;
    msg +=  toString(bytes) + LF;
    for(byte b: bytes) {
        msg += toDebugOneByte(b);
    }
    return msg;
} // toDebug

public  String toDebugOneByte(byte b) {
    String msg = toHex(b) + SPACE + toChar(b) + LF;
    return msg;
} // toDebugOneByte

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


} // class ByteUtil
