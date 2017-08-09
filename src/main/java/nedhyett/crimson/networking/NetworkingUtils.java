/*
 * The MIT License
 *
 * Copyright 2017 Ned Hyett.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nedhyett.crimson.networking;

import nedhyett.crimson.logging.CrimsonLog;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Defines some arbitrary networking utilities that wouldn't fit anywhere else.
 *
 * @author Ned Hyett
 */
public class NetworkingUtils {

	/**
	 * Get a list of broadcast addresses for all connected interfaces.
	 *
	 * @return
	 */
	public static ArrayList<InetAddress> getBroadcastAddresses() {
		ArrayList<InetAddress> listOfBroadcasts = new ArrayList<>();
		Enumeration list;
		try {
			list = NetworkInterface.getNetworkInterfaces();
			while(list.hasMoreElements()) {
				NetworkInterface iface = (NetworkInterface) list.nextElement();
				if(iface == null) continue;
				if(!iface.isLoopback() && iface.isUp()) {
					Iterator it = iface.getInterfaceAddresses().iterator();
					while(it.hasNext()) {
						InterfaceAddress address = (InterfaceAddress) it.next();
						if(address == null) continue;
						InetAddress broadcast = address.getBroadcast();
						if(broadcast != null) {
							listOfBroadcasts.add(broadcast);
						}
					}
				}
			}
		} catch(SocketException ex) {
			CrimsonLog.warning("Error while getting network interfaces");
			ex.printStackTrace();
		}

		return listOfBroadcasts;
	}

	/**
	 * Convert a MAC address string into bytes.
	 *
	 * @param macStr
	 *
	 * @return
	 */
	public static byte[] getMacBytes(String macStr) {
		byte[] bytes = new byte[6];
		String[] hex = macStr.split("(\\:|\\-)");
		try {
			for(int i = 0; i < 6; i++) bytes[i] = (byte) Integer.parseInt(hex[i], 16);
		} catch(NumberFormatException e) {
			return null;
		}
		return bytes;
	}

	/**
	 * Send a WOL request to the specified MAC address.
	 *
	 * @param mac
	 */
	public static void sendWOL(String mac) {
		byte[] macBytes = getMacBytes(mac);
		byte[] bytes = new byte[6 + 16 * macBytes.length];
		for(int i = 0; i < 6; i++) bytes[i] = (byte) 0xff;
		for(int i = 6; i < bytes.length; i += macBytes.length)
			System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
		try {
			for(InetAddress addr : getBroadcastAddresses()) {
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr, 9);
				DatagramSocket socket = new DatagramSocket();
				socket.send(packet);
				socket.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
