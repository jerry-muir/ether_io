/*
 *  (C) Copyright 2019 Gerard L. Muir
 */
package com.cybernian.ether_io.core;

import java.io.IOException;

/**
 * Provides data communication for the Ether IO family of digital I/O Ethernet
 * boards. This class support the common command set for IO24, IO24F boards.
 * 
 * @author Gerard L. Muir
 */
public class IO24Common extends IO24Core {

	/**
	 * Creates a Datagram Socket to communicate with the I/O board at the given IP
	 * address.
	 * 
	 * @param ipAddress
	 *            The network IP address of the device to communicate with.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public IO24Common(String ipAddress) throws IOException {
		super(ipAddress);
	}

	/**
	 * Creates a Datagram Socket, with the specified timeout, to communicate with the
	 * I/O board at the given IP address.
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
	public IO24Common(String ipAddress, int datagramSocketTimeout) throws IOException {
		super(ipAddress, datagramSocketTimeout);
	}

	/**
	 * Writes the Port Schmitt Trigger value for the specified port.
	 * 
	 * @param ioPort
	 *            The port letter to read from.
	 * @param value
	 *            The port value to write. 0-255
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public void writePortSchmittTrigger(char ioPort, int value) throws IOException, IllegalArgumentException {

		this.isPortLetterValid(ioPort);

		byte[] data = new byte[] { (byte) (int) '$', (byte) (int) Character.toUpperCase(ioPort), (byte) value };

		this.send(data);
	}

	/**
	 * Writes the Port Threshold value for the specified port.
	 * 
	 * @param ioPort
	 *            The port letter to read from.
	 * @param value
	 *            The port value to write. 0-255
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public void writePortThreshold(char ioPort, int value) throws IOException, IllegalArgumentException {

		this.isPortLetterValid(ioPort);

		byte[] data = new byte[] { (byte) (int) '#', (byte) (int) Character.toUpperCase(ioPort), (byte) value };

		this.send(data);
	}

	/**
	 * Reads the I/O port Schmitt setting for the specified port. This is a 2 step
	 * process. First a read request is sent, then we will try to read the returned
	 * packet.
	 * 
	 * @param ioPort
	 *            The port letter to read from.
	 * 
	 * @return The I/O port Schmitt register settings.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public byte[] readPortSchmitt(char ioPort) throws IOException, IllegalArgumentException {

		// Number of bytes returned in the response packet for this read command.
		int BYTES_RETURNED = 3;

		this.isPortLetterValid(ioPort);

		// Convert the port letter into a port read command and create the request
		// packet.
		byte[] sendData = new byte[] { (byte) (int) '$', (byte) (int) Character.toLowerCase(ioPort) };

		byte[] returnData = this.readData(sendData, BYTES_RETURNED);
		return returnData;
	}

	/**
	 * Reads the I/O port threshold setting for the specified port. This is a 2 step
	 * process. First a read request is sent, then we will try to read the returned
	 * packet.
	 * 
	 * @param ioPort
	 *            The port letter to read from.
	 * 
	 * @return The I/O port threshold register settings.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public byte[] readPortThreshold(char ioPort) throws IOException, IllegalArgumentException {

		// Number of bytes returned in the response packet for this read command.
		int BYTES_RETURNED = 3;

		this.isPortLetterValid(ioPort);

		// Convert the port letter into a port read command create the request packet.
		byte[] sendData = new byte[] { (byte) (int) '#', (byte) (int) Character.toLowerCase(ioPort) };

		byte[] returnData = this.readData(sendData, BYTES_RETURNED);
		return returnData;
	}

	/**
	 * Enables the EEPROM write function. Write enable must be active before any
	 * EEPROM write commands are processed.
	 * 
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public void writeEnableEEPROM() throws IOException {

		byte[] data = new byte[] { (byte) (int) '\'', (byte) (int) '1', (byte) 0, (byte) 170, (byte) 85 };

		this.send(data);
	}

	/**
	 * Disables the EEPROM write function.
	 * 
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * 
	 */
	public void writeDisableEEPROM() throws IOException {

		byte[] data = new byte[] { (byte) (int) '\'', (byte) (int) '0', (byte) 0, (byte) 0, (byte) 0 };

		this.send(data);
	}

	/**
	 * Erase the EEPROM memory at the specified word address. This sets the register value to
	 * #FFFF
	 * 
	 * @param wordAddress
	 *            the location address of the word to be erased. (5-63)
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public void eraseEEPROM_Word(int wordAddress) throws IOException {

		byte[] data = new byte[] { (byte) (int) '\'', (byte) (int) 'E', (byte) wordAddress, (byte) 170, (byte) 85 };

		this.send(data);

	}

}
