/*
 *  (C) Copyright 2019 Gerard L. Muir
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License. 
 */
package com.cybernian.ether_io.drivers;

import java.io.IOException;

import com.cybernian.ether_io.core.IO24Common;

/**
 * Provides data communication with the Ether IO24R digital I/O Ethernet board.
 * 
 * @author Gerard L. Muir
 */
public class IO24R extends IO24Common {

	/**
	 * Creates a Datagram Socket to communicate to the I/O board at the given IP
	 * address.
	 * 
	 * @param ipAddress
	 *            The network IP address of the device to communicate with.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public IO24R(String ipAddress) throws IOException {
		super(ipAddress);
	}
	
	/**
	 * Creates a Datagram Socket, with the specified timeout, to communicate with the I/O board at the given IP
	 * address.
	 * 
	 * @param ipAddress
	 *            IP address of the I/O board.
	 * @param datagramSocketTimeout
	 * 			The time out to be used by the UPD Socket connection.
	 * 
	 * @throws IOException
	 *              Thrown if a datagram socket error occurred.
	 * 
	 * @see <code>DatagramSocket</code>
	 */
	public IO24R(String ipAddress, int datagramSocketTimeout) throws IOException {
		super(ipAddress, datagramSocketTimeout);
	}

	
	/**
	 * Enables the Serial Port Interface on Port A. This sets the port A direction
	 * value bits appropriately.
	 * 
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public void enablePortA_SPI() throws IOException {

		byte[] data = new byte[] { (byte) (int) 'S', (byte) 1, (byte) (int) 'A' };

		this.send(data);
	}
	
	/**
	 * Disables the Serial Port Interface on Port A. This reverts the port A
	 * direction value bits to their previous values.
	 * 
	 * @throws IOException Thrown if a datagram socket error occurred.
	 */
	public void disablePortA_SPI() throws IOException {

		byte[] data = new byte[] { (byte) (int) 'S', (byte) 0, (byte) (int) 'A' };

		this.send(data);
	}
	
	/**
	 * Sends the supplied byte array over the Port A serial port interface.
	 * 
	 * @param dataBytes
	 *            The date bytes to send out.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public void SPI_Send(byte[] dataBytes) throws IOException {

		byte[] data = new byte[] { (byte) (int) 'S', (byte) (int) 'A', (byte) dataBytes.length };

		for (int i = 0; i < i + dataBytes.length; i++) {
			data[i] = dataBytes[i];
		}

		this.send(data);
	}

}
