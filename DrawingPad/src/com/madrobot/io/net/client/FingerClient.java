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
package com.madrobot.io.net.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/***
 * The FingerClient class implements the client side of the Internet Finger
 * Protocol defined in RFC 1288. To finger a host you create a
 * FingerClient instance, connect to the host, query the host, and finally
 * disconnect from the host. If the finger service you want to query is on
 * a non-standard port, connect to the host at that port.
 * Here's a sample use:
 * 
 * <pre>
 * FingerClient finger;
 * 
 * finger = new FingerClient();
 * 
 * try{
 * 	finger.connect(&quot;foo.bar.com&quot;);
 * 	System.out.println(finger.query(&quot;foobar&quot;, false));
 * 	finger.disconnect();
 * } catch(IOException e){
 * 	System.err.println(&quot;Error I/O exception: &quot; + e.getMessage());
 * 	return;
 * }
 * </pre>
 * <p>
 * <p>
 * 
 ***/

public class FingerClient extends SocketClient {
	/***
	 * The default FINGER port. Set to 79 according to RFC 1288.
	 ***/
	public static final int DEFAULT_PORT = 79;

	private static final String __LONG_FLAG = "/W ";

	private transient char[] __buffer = new char[1024];

	/***
	 * The default FingerClient constructor. Initializes the
	 * default port to <code> DEFAULT_PORT </code>.
	 ***/
	public FingerClient() {
		setDefaultPort(DEFAULT_PORT);
	}

	/***
	 * Fingers a user at the connected host and returns the output
	 * as a String. You must first connect to a finger server before
	 * calling this method, and you should disconnect afterward.
	 * <p>
	 * 
	 * @param longOutput
	 *            Set to true if long output is requested, false if not.
	 * @param username
	 *            The name of the user to finger.
	 * @return The result of the finger query.
	 * @exception IOException
	 *                If an I/O error occurs while reading the socket.
	 ***/
	public String query(boolean longOutput, String username) throws IOException {
		int read;
		StringBuilder result = new StringBuilder(__buffer.length);
		BufferedReader input;

		input = new BufferedReader(new InputStreamReader(getInputStream(longOutput, username)));

		try{
			while(true){
				read = input.read(__buffer, 0, __buffer.length);
				if(read <= 0){
					break;
				}
				result.append(__buffer, 0, read);
			}
		} finally{
			input.close();
		}

		return result.toString();
	}

	/***
	 * Fingers the connected host and returns the output
	 * as a String. You must first connect to a finger server before
	 * calling this method, and you should disconnect afterward.
	 * This is equivalent to calling <code> query(longOutput, "") </code>.
	 * <p>
	 * 
	 * @param longOutput
	 *            Set to true if long output is requested, false if not.
	 * @return The result of the finger query.
	 * @exception IOException
	 *                If an I/O error occurs while reading the socket.
	 ***/
	public String query(boolean longOutput) throws IOException {
		return query(longOutput, "");
	}

	/***
	 * Fingers a user and returns the input stream from the network connection
	 * of the finger query. You must first connect to a finger server before
	 * calling this method, and you should disconnect after finishing reading
	 * the stream.
	 * <p>
	 * 
	 * @param longOutput
	 *            Set to true if long output is requested, false if not.
	 * @param username
	 *            The name of the user to finger.
	 * @return The InputStream of the network connection of the finger query.
	 *         Can be read to obtain finger results.
	 * @exception IOException
	 *                If an I/O error during the operation.
	 ***/
	public InputStream getInputStream(boolean longOutput, String username) throws IOException {
		return getInputStream(longOutput, username, null);
	}

	/***
	 * Fingers a user and returns the input stream from the network connection
	 * of the finger query. You must first connect to a finger server before
	 * calling this method, and you should disconnect after finishing reading
	 * the stream.
	 * <p>
	 * 
	 * @param longOutput
	 *            Set to true if long output is requested, false if not.
	 * @param username
	 *            The name of the user to finger.
	 * @param encoding
	 *            the character encoding that should be used for the query,
	 *            null for the platform's default encoding
	 * @return The InputStream of the network connection of the finger query.
	 *         Can be read to obtain finger results.
	 * @exception IOException
	 *                If an I/O error during the operation.
	 ***/
	public InputStream getInputStream(boolean longOutput, String username, String encoding)
			throws IOException {
		DataOutputStream output;
		StringBuilder buffer = new StringBuilder(64);
		if(longOutput){
			buffer.append(__LONG_FLAG);
		}
		buffer.append(username);
		buffer.append(SocketClient.NETASCII_EOL);

		byte[] encodedQuery = (encoding == null ? buffer.toString().getBytes() : buffer.toString()
				.getBytes(encoding));

		output = new DataOutputStream(new BufferedOutputStream(_output_, 1024));
		output.write(encodedQuery, 0, encodedQuery.length);
		output.flush();

		return _input_;
	}

	/***
	 * Fingers the connected host and returns the input stream from
	 * the network connection of the finger query. This is equivalent to
	 * calling getInputStream(longOutput, ""). You must first connect to a
	 * finger server before calling this method, and you should disconnect
	 * after finishing reading the stream.
	 * <p>
	 * 
	 * @param longOutput
	 *            Set to true if long output is requested, false if not.
	 * @return The InputStream of the network connection of the finger query.
	 *         Can be read to obtain finger results.
	 * @exception IOException
	 *                If an I/O error during the operation.
	 ***/
	public InputStream getInputStream(boolean longOutput) throws IOException {
		return getInputStream(longOutput, "");
	}

}
