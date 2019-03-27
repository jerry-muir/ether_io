/*
 *  (C) Copyright 2019 Gerard L. Muir
 */
package com.cybernian.ether_io.drivers;

import java.io.IOException;

/**
 * Provides data communication with the Ether IO72TPC digital I/O Ethernet board.
 * 
 * @author Gerard L. Muir
 */
public class IO72TPC extends IO24TPC {

	/**
	 * Creates a Datagram Socket to communicate with the I/O board at the given IP
	 * address.
	 * 
	 * @param ipAddress
	 *            The network IP address of the device to communicate with.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public IO72TPC(String ipAddress) throws IOException {
		super(ipAddress);
	}
	
	/**
	 * Creates a Datagram Socket, with the specified timeout, to communicate with
	 * the I/O board at the given IP address.
	 * 
	 * @param ipAddress
	 *            IP address of the I/O board.
	 * @param datagramSocketTimeout
	 *            The time out to be used by the UPD Socket connection.
	 * 
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * 
	 * @see <code>DatagramSocket</code>
	 */
	public IO72TPC(String ipAddress, int datagramSocketTimeout) throws IOException {
		super(ipAddress, datagramSocketTimeout);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cbt.io24.IO24Core#checkPortLetter(char)
	 */
	@Override
	public void isPortLetterValid(char portLetter) throws IllegalArgumentException {

		int charValue = (int) Character.toLowerCase(portLetter);

		if (charValue >= (int) 'a' && charValue <= (int) 'i') {
			return;
		} else {
			throw new IllegalArgumentException(
					"IO24Core: Validation error: \"" + portLetter + "\" is not a valid port id.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cbt.io24.IO24Core#checkLineNumber(int)
	 */
	@Override
	public void isLineNumberValid(int lineNumber) {

		if (lineNumber >= 0 && lineNumber <= 71) {
			return;
		} else {
			throw new IllegalArgumentException("IO24Core: Validation error: \"" + Integer.toBinaryString(lineNumber)
					+ "\" is not a valid line number.");
		}
	}

}
