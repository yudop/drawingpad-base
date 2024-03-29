/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.io.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

public class NetUtils {
	/**
	 * Finds a local, non-loopback, IPv4 address
	 * 
	 * @return The first non-loopback IPv4 address found, or <code>null</code>
	 *         if no such addresses found
	 * @throws SocketException
	 *             If there was a problem querying the network interfaces
	 */
	public static InetAddress getLocalAddress() throws SocketException {
		Enumeration<NetworkInterface> ifaces = NetworkInterface
				.getNetworkInterfaces();
		while (ifaces.hasMoreElements()) {
			NetworkInterface iface = ifaces.nextElement();
			Enumeration<InetAddress> addresses = iface.getInetAddresses();

			while (addresses.hasMoreElements()) {
				InetAddress addr = addresses.nextElement();
				if ((addr instanceof Inet4Address) && !addr.isLoopbackAddress()) {
					return addr;
				}
			}
		}
		return null;
	}

	/**
	 * Finds this computer's global IP address
	 * 
	 * @param url
	 *            the url to find the global IP
	 * @return The global IP address, or null if a problem occurred
	 */
	public static Inet4Address getGlobalAddress(String url) {
		try {
			URLConnection uc = new URL(url).openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			return (Inet4Address) InetAddress.getByName(br.readLine());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return the encoded ip
	 */
	public static int encodeIP(int a, int b, int c, int d) {
		int ip = 0;
		ip |= a << 24;
		ip |= b << 16;
		ip |= c << 8;
		ip |= d;
		return ip;
	}

	/**
	 * Encodes an IP as an int
	 * 
	 * @param ip
	 * @return the encoded IP
	 */
	public static int encode(Inet4Address ip) {
		int i = 0;
		byte[] b = ip.getAddress();
		i |= b[0] << 24;
		i |= b[1] << 16;
		i |= b[2] << 8;
		i |= b[3] << 0;

		return i;
	}

	/**
	 * Validate the given IPv4 address.
	 * 
	 * @param address
	 *            the IP address as a String.
	 * 
	 * @return true if a valid IPv4 address, false otherwise
	 */
	public static boolean isValidIPv4(String address) {
		if (address.length() == 0) {
			return false;
		}

		int octet;
		int octets = 0;

		String temp = address + ".";

		int pos;
		int start = 0;
		while (start < temp.length()
				&& (pos = temp.indexOf('.', start)) > start) {
			if (octets == 4) {
				return false;
			}
			try {
				octet = Integer.parseInt(temp.substring(start, pos));
			} catch (NumberFormatException ex) {
				return false;
			}
			if (octet < 0 || octet > 255) {
				return false;
			}
			start = pos + 1;
			octets++;
		}

		return octets == 4;
	}

	public static boolean isValidIPv4WithNetmask(String address) {
		int index = address.indexOf("/");
		String mask = address.substring(index + 1);

		return (index > 0) && isValidIPv4(address.substring(0, index))
				&& (isValidIPv4(mask) || isMaskValue(mask, 32));
	}

	public static boolean isValidIPv6WithNetmask(String address) {
		int index = address.indexOf("/");
		String mask = address.substring(index + 1);

		return (index > 0)
				&& (isValidIPv6(address.substring(0, index)) && (isValidIPv6(mask) || isMaskValue(
						mask, 128)));
	}

	/**
	 * Validate the given IPv6 address.
	 * 
	 * @param address
	 *            the IP address as a String.
	 * 
	 * @return true if a valid IPv4 address, false otherwise
	 */
	public static boolean isValidIPv6(String address) {
		if (address.length() == 0) {
			return false;
		}

		int octet;
		int octets = 0;

		String temp = address + ":";
		boolean doubleColonFound = false;
		int pos;
		int start = 0;
		while (start < temp.length()
				&& (pos = temp.indexOf(':', start)) >= start) {
			if (octets == 8) {
				return false;
			}

			if (start != pos) {
				String value = temp.substring(start, pos);

				if (pos == (temp.length() - 1) && value.indexOf('.') > 0) {
					if (!isValidIPv4(value)) {
						return false;
					}

					octets++; // add an extra one as address covers 2 words.
				} else {
					try {
						octet = Integer
								.parseInt(temp.substring(start, pos), 16);
					} catch (NumberFormatException ex) {
						return false;
					}
					if (octet < 0 || octet > 0xffff) {
						return false;
					}
				}
			} else {
				if (pos != 1 && pos != temp.length() - 1 && doubleColonFound) {
					return false;
				}
				doubleColonFound = true;
			}
			start = pos + 1;
			octets++;
		}

		return octets == 8 || doubleColonFound;
	}

	private static boolean isMaskValue(String component, int size) {
		try {
			int value = Integer.parseInt(component);

			return value >= 0 && value <= size;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	
}
