/*
 *  (C) Copyright 2019 Gerard L. Muir
 */
package com.cybernian.ether_io.drivers;

import java.io.IOException;

import com.cybernian.ether_io.core.IO24Core;

/**
 * Provides data communication with the Ether IO24TPC digital I/O Ethernet board.
 * 
 * @author Gerard L. Muir
 */
public class IO24TPC extends IO24Core {

	/**
	 * Creates a Datagram Socket to communicate with the I/O board at the given IP
	 * address.
	 * 
	 * @param ipAddress
	 *            IP address of the  I/O board.
	 * @throws IOException
	 *             Thrown if the Datagram Socket fails to be created.
	 * 
	 * @see <code>DatagramSocket</code>
	 */
	public IO24TPC(String ipAddress) throws IOException {
		super(ipAddress);
	}
	
	/**
	 * Creates a Datagram Socket, with the specified timeout, to communicate with the I/O board at the given IP
	 * address.
	 * 
	 * @param ipAddress
	 *            IP address of the  I/O board.
	 * @param datagramSocketTimeout
	 * 			The time out to be used by the UPD Socket connection.
	 * 
	 * @throws IOException
	 *             Thrown if the Datagram Socket fails to be created.
	 * 
	 * @see <code>DatagramSocket</code>
	 */
	public IO24TPC(String ipAddress, int datagramSocketTimeout) throws IOException {
		super(ipAddress, datagramSocketTimeout);
	}

	
	/* (non-Javadoc)
	 * @see com.cbt.io24.IO24Core#readPortPullUp(char)
	 */
	@Override
	public byte[] readPortPullUp(char ioPort) throws IOException {

		// Number of bytes returned in the response packet for this read command.
		int BYTES_RETURNED = 3;

		this.isPortLetterValid(ioPort);

		// Convert the port letter into a port read command create the request packet.
		byte[] sendData = new byte[] { (byte) (int) '%', (byte) (int) Character.toLowerCase(ioPort) };

		byte[] returnData = this.readData(sendData, BYTES_RETURNED);
		return returnData;
	}
	
	/* (non-Javadoc)
	 * @see com.cbt.io24.IO24Core#writePortPullUp(char, int)
	 */
	@Override
	public void writePortPullUp(char ioPort, int value) throws IOException {

		this.isPortLetterValid(ioPort);

		byte[] data = new byte[] { (byte) (int) '%', (byte) (int) Character.toUpperCase(ioPort), (byte) value };

		this.send(data);

	}
	
	
	/**
	 * Raises the Pin Value on the specified IO Pin. The corresponding IO Pin must
	 * be set to Output for this command to have effect.
	 * 
	 * @param pinNumber
	 *            0-23 
	 *            Port A Pins correspond to 0 ‐ 7 
	 *            Port B Pins correspond to 8 ‐15 
	 *            Port C Pins correspond to 16 ‐ 23.
	 * @throws IOException I/O exception of some sort has occurred.
	 */
	public void raiseIO_Pin (int pinNumber) throws IOException {
		
		byte[] data = new byte[] { (byte) (int) 'H', (byte) pinNumber };

		this.send(data);
	}
	
	/**
	 * Lowers the Pin Value on the specified IO Pin. The corresponding IO Pin must
	 * be set to Output for this command to have effect.
	 * 
	 * @param pinNumber
	 *            0-23 
	 *            Port A Pins correspond to 0 ‐ 7 
	 *            Port B Pins correspond to 8 ‐15 
	 *            Port C Pins correspond to 16 ‐ 23.
	 * @throws IOException I/O exception of some sort has occurred.
	 */
	public void lowerIO_Pin (int pinNumber) throws IOException{
		
		byte[] data = new byte[] { (byte) (int) 'L', (byte) pinNumber };

		this.send(data);
	}

}
