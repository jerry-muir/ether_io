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
 * Provides data communication with the Ether IO24 digital I/O Ethernet board.
 * 
 * @author Gerard L. Muir
 */
public class IO24 extends IO24Common {

	/**
	 * Creates a Datagram Socket to communicate with the I/O board at the given IP
	 * address.
	 * 
	 * @param ipAddress
	 *            The network IP address of the device to communicate with.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public IO24(String ipAddress) throws IOException {
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
	 *             Thrown if a datagram socket error occurred.
	 * 
	 * @see <code>DatagramSocket</code>
	 */
	public IO24(String ipAddress, int datagramSocketTimeout) throws IOException {
		super(ipAddress, datagramSocketTimeout);
	}

	/**
	 * Returns the byte that was echoed by the I/O board.
	 * 
	 * @param Byte
	 *            The byte data to be echoed back.
	 * @return The byte that was sent to the I/O board.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public byte echoByte(byte Byte) throws IOException {

		int BYTES_RETURNED = 1; // Number of expected return bytes.
		byte[] echoByte = {};
		
		byte[] data = new byte[] { (byte) (int) '`', Byte };
		
		echoByte = this.readData(data, BYTES_RETURNED);
		
		return echoByte[0];
	}

	/**
	 * Requests the IO24 board send it's host data.
	 * 
	 * @return The host data as seen by the IO24 device containing the following:
	 *            Serial number of the IO24 board,
	 *            IP address of the requesting host device,
	 *            MAC address of the requesting host device,
	 *            UDP port number from the requesting host device.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public byte[] sendHostDataBytes() throws IOException {

		// Number of bytes returned in the response packet for this read command.
		int BYTES_RETURNED = 16;

		// Create the request packet.
		byte[] sendData = new byte[] { (byte) (int) '%' };

		return this.readData(sendData, BYTES_RETURNED);
	}

	/**
	 * Requests the IO24 board to return a space.
	 * 
	 * @return A byte containing a space character.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public byte sendSpace() throws IOException {

		// Number of bytes returned in the response packet for this read command.
		int BYTES_RETURNED = 1;

		// Create the request packet.
		byte[] sendData = new byte[] { (byte) (int) '*' };
		byte[] returnData = this.readData(sendData, BYTES_RETURNED);

		return returnData[0];
	}

}
