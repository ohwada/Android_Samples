/*
 * Java OSC Sample
 * 2018-05-01 K.OHWADA
 */

// change log
// 2018-05-01 K.OHWADA
// dispatchPacket return result
// true :  dispatch message
// false : No matching OSCListener
// original : https://github.com/hoijui/JavaOSC 

/*
 * Copyright (C) 2003-2014, C. Ramakrishnan / Auracle.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package jp.ohwada.android.oscrecieversample2.util;

import android.util.Log;

import com.illposed.osc.AddressSelector;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Dispatches {@link OSCPacket}s to registered listeners (<i>Method</i>s).
 *
 * @author Chandrasekhar Ramakrishnan
 */
public class OSCPacketDispatcherCustom {

        // debug
	private final static boolean D = true;
    	private final static String TAG = "OSC";
    	private final static String TAG_SUB = "OSCPacketDispatcherCustom";

	private final Map<AddressSelector, OSCListener> selectorToListener;

 	/**
	 * constractor
	 */ 
	public OSCPacketDispatcherCustom() {
		this.selectorToListener = new HashMap<AddressSelector, OSCListener>();
	} // OSCPacketDispatcherCustom

	/**
	 * Adds a listener (<i>Method</i> in OSC speak) that will be notified
	 * of incoming messages that match the selector.
	 * @param addressSelector selects which messages will be forwarded to the listener,
	 *   depending on the message address
	 * @param listener receives messages accepted by the selector
	 */
	public void addListener(final AddressSelector addressSelector, final OSCListener listener) {
		selectorToListener.put(addressSelector, listener);
	}

 	/**
	 * dispatchPacket
	 */ 
	public boolean dispatchPacket(final OSCPacket packet) {
		// log_d("dispatchPacket1");
		return dispatchPacket(packet, null);
	} // dispatchPacket

 	/**
	 * dispatchPacket
	 */ 
	public boolean dispatchPacket(final OSCPacket packet, final Date timestamp) {
		// log_d("dispatchPacket2");
		if (packet instanceof OSCBundle) {
			return dispatchBundle((OSCBundle) packet);
		} else {
			return dispatchMessage((OSCMessage) packet, timestamp);
		} // dispatchPacket
	}

 	/**
	 * dispatchBundle
	 */ 
	private boolean dispatchBundle(final OSCBundle bundle) {
		// log_d("dispatchBundle");
		final Date timestamp = bundle.getTimestamp();
		final List<OSCPacket> packets = bundle.getPackets();
		boolean is_match = false;
		for (final OSCPacket packet : packets) {
					boolean ret = false;dispatchPacket(packet, timestamp);
					if (ret) {
					is_match = true;
					}
		}
		return is_match;
	} // dispatchBundle

 	/**
	 * dispatchMessage
	 */ 
	private boolean  dispatchMessage(final OSCMessage message, final Date time) {

		// log_d("dispatchMessage");
		boolean is_match = false;
		for (final Entry<AddressSelector, OSCListener> addrList : selectorToListener.entrySet()) {
			if (addrList.getKey().matches(message.getAddress())) {
				addrList.getValue().acceptMessage(time, message);
				log_d("acceptMessage");
				is_match = true;
			}
		} // for
		return is_match;
	} // dispatchMessage

private void notifyError() {
} // notifyError

 	/**
	 * write into logcat
	 */ 
	private static void log_d( String msg ) {
	    if (D) Log.d( TAG, TAG_SUB + " " + msg );
	} // log_d

}
