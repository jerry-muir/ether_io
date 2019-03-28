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
package com.cybernian.ether_io.core;

import java.io.IOException; 
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Provides data communication with the Ether IO family of digital I/O
 * Ethernet boards. This class supports the common command set for multiple
 * boards. (IO24, IO24F, IO24TPC and IO72TPC)
 * 
 * @author Gerard L. Muir
 */
public class IO24Core {
	
	/**
	 * The Datagram Socket Timeout value in milliseconds.
	 */
	public int datagramSocketTimeout = 1000; // Default Datagram Socket Timeout value. 

	private static int ETHER_IO_UPD_PORT = 2424; // Fixed IO24 board UDP port.
	private DatagramSocket datagramSocket; // Socket used to send and receive data.
	private InetAddress boardIpAddress; // The target IO24 board IP address.

	/**
	 * Creates a Datagram Socket to communicate with the I/O board at the given IP
	 * address.
	 * 
	 * @param ipAddress
	 *            IP address of the I/O board.
	 * 
	 * @throws IOException
	 *             Thrown if the Datagram Socket fails to be created.
	 * 
	 * @see <code>DatagramSocket</code>
	 */
	public IO24Core(String ipAddress) throws IOException {

		openUDP_Socket(ipAddress);
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
	 *             Thrown if the Datagram Socket fails to be created.
	 * 
	 * @see <code>DatagramSocket</code>
	 */
	public IO24Core(String ipAddress, int datagramSocketTimeout) throws IOException {

		
		this.datagramSocketTimeout = datagramSocketTimeout;
		openUDP_Socket(ipAddress);
	}

	/**
	 * Creates a Datagram Socket to communicate with the I/O board and then
	 * attempts to obtain the current value of A port to verify communications.
	 * 
	 * @param ipAddress
	 *            IP Address of the I/O board.
	 * @throws IOException
	 */
	private void openUDP_Socket(String ipAddress) throws IOException {
		try {
			this.boardIpAddress = InetAddress.getByName(ipAddress);
			// Let the O.S. pick a datagram socket for us to use. This way we can talk to
			// several boards through multiple instances of this class.
			this.datagramSocket = new DatagramSocket();
			// set timeout on socket to allow for interrupts to be detected.
			this.datagramSocket.setSoTimeout(this.datagramSocketTimeout);
			// Try to retrieve a port value to confirm communications.
			byte[] value = this.readPortValue('a');
			if (value.length != 2) {
				this.datagramSocket.close();
				throw new IOException(this.getClass().getSimpleName() + ": Error: Could not get port data.");
			}
		} catch (UnknownHostException e) {
			this.datagramSocket.close();
			throw new IOException("Unknown Host at: " + ipAddress);
		} catch (SocketException e) {
			this.datagramSocket.close();
			throw new IOException("Could not open Socket at: " + this.datagramSocket.getLocalPort());
		} catch (IllegalArgumentException e) {
			// Ignore port letter exception.
			this.datagramSocket.close();
		}
	}
	
	/**
	 * Throws an IllegalArgumentException if the port letter is not valid for this board.
	 * 
	 * @param portLetter
	 *            The port letter to be validated.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public void isPortLetterValid(char portLetter) throws IllegalArgumentException {

		int charValue = (int) Character.toLowerCase(portLetter);

		if (charValue >= (int) 'a' && charValue <= (int) 'c') {
			return;
		} else {
			throw new IllegalArgumentException("IO24Core: Validation error: \"" + portLetter + "\" is not a valid port id.");
		}
	}

	/**
	 * Throws an IllegalArgumentException if the line number is not valid for this board.
	 * 
	 * @param lineNumber
	 *            The line number.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public void isLineNumberValid(int lineNumber) throws IllegalArgumentException {

		if (lineNumber >= 0 && lineNumber <= 23) {
			return;
		} else {
			throw new IllegalArgumentException("IO24Core: Validation error: \"" +lineNumber + "\" is not a valid line number.");
		}
	}

	/**
	 * Closes the datagram socket.
	 */
	public void closeSocket() {

		if (this.datagramSocket != null) {
			this.datagramSocket.close();
		}
	}

	/**
	 * Broadcasts an identify request in order to find a list of I/O boards
	 * on the network.
	 * 
	 * @return A list of cards. Each entry is composed of a MAC address, firmware
	 *         version and IP address bytes.
	 * @throws IOException
	 *             Thrown if an I/O error occurred while creating the datagram
	 *             socket.
	 */
	public static ArrayList<byte[]> identify() throws IOException {
		
		int MAX_TRYS = 3; // Max number of read attempts after time out.
		int RECEIVE_BYTE_ARRAY_SIZE = 16; 		// Composed of:
												// 4 IO24 characters
												// 6 byte MAC address
												// 2 byte Firmware Version
												// 4 byte board IP address (Data that is added by this driver.)

		DatagramPacket packet = null;
		DatagramSocket socket = null;
		boolean toManyTrys = false; // Did we exceed the maxTrys.
		int trys = 0; // Current number of read attempts.
		ArrayList<byte[]> cardList = new ArrayList<byte[]>(); // Return list of IO24 boards on the LAN.

		// Note that we are converting the character to its' integer value. We
		// then cast it as a byte. We use this technique because a normal byte
		// is a signed integer and has a range of -127 to 127 so we can not send
		// anything over 127.
		byte[] data = new byte[] { (byte) (int) (char) 'I',
				                   (byte) (int) (char) 'O',
				                   (byte) (int) (char) '2',
				                   (byte) (int) (char) '4' };

		// Send the ID broadcast message
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(1000); // Allow for interrupts to be detected.
			socket.setBroadcast(true);

			// Get a list of network interfaces to search for I/O boards on.
			Enumeration<NetworkInterface> interfaceList = NetworkInterface.getNetworkInterfaces();
			while (interfaceList.hasMoreElements()) {
				NetworkInterface networkInterface = interfaceList.nextElement();
				List<InterfaceAddress> list = networkInterface.getInterfaceAddresses();
				Iterator<InterfaceAddress> interfaceAddressIterator = list.iterator();
				
				// Send broadcast message to each interface.
				while (interfaceAddressIterator.hasNext()) {
					InterfaceAddress interfaceAddress = interfaceAddressIterator.next();
					if (interfaceAddress.getBroadcast() != null) {
						packet = new DatagramPacket(data, data.length, interfaceAddress.getBroadcast(), IO24Core.ETHER_IO_UPD_PORT);
						socket.send(packet);
					}
				}
			}

			// Listen for the responses, possibly from multiple boards.
			while (!Thread.currentThread().isInterrupted() && !toManyTrys) {
				try {
					byte[] receiveData = new byte[RECEIVE_BYTE_ARRAY_SIZE];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					socket.receive(receivePacket);
					receiveData = receivePacket.getData();
					
					String ipAddress = receivePacket.getAddress().getHostAddress();
					String [] IP_Parts = ipAddress.split("\\.");
					
					for (int i = 0 ; i < IP_Parts.length; i++) {
						receiveData[receiveData.length - IP_Parts.length +i] = (byte) Integer.parseInt(IP_Parts[i], 10);
					}
					
					cardList.add(receiveData);
					
				} catch (IOException ex) {
					// socket timed out, so let's go around again.
					trys++;
					if (trys == MAX_TRYS) {
						toManyTrys = true;
					} else
						continue;
				}
			} // while()
		} catch (UnknownHostException e) {
			throw e;
		} catch (SocketException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (socket != null) {
				socket.close();
			}
		}

		return cardList;
	}

	/**
	 * Listen for return data from a read function. The calling method is
	 * responsible for composing the read command that is to be sent in the data
	 * packet.
	 * 
	 * @param sendData
	 *            The one or two byte data packet to be sent with the appropriate
	 *            read bytes consisting of the port and register id.
	 * @param numReturnDataBytes
	 *            Number of bytes expected to be returned by the function.
	 * @return The byte or bytes of data returned form the command.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public synchronized byte[] readData(byte[] sendData, int numReturnDataBytes) throws IOException {

		// Wait for a response, but not to long.
		int MAX_TRYS = 3; // Max number of read attempts after time out.
		boolean gotResponse = false; // Did we receive a response packet.
		int trys = 0; // Current number of read attempts.

		// Create a packet to receive the response.
		byte[] receiveData = new byte[numReturnDataBytes];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		// Send the read request.
		this.send(sendData);

		while (!Thread.currentThread().isInterrupted() && !gotResponse) {
			try {
				this.datagramSocket.receive(receivePacket);
				gotResponse = true;
				receiveData = receivePacket.getData();
			} catch (SocketTimeoutException ste) {
				// socket timed out, so let's go around again.
				trys++;
				if (trys == MAX_TRYS) {
					throw (ste);
				} else
					continue;
			}
		} // while()

		return receiveData;
	}

	/**
	 * Reads the I/O port direction setting of the specified port. This is a 2 step
	 * process. First a read request is sent, then we will try to read the returned
	 * packet, if any.
	 * 
	 * @param ioPort
	 *            The port letter to read from.
	 * 
	 * @return The I/O port direction register settings.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public byte[] readPortDirection(char ioPort) throws IOException, IllegalArgumentException {

		// Number of bytes returned in the response packet for this read command.
		int BYTES_RETURNED = 3;
		byte[] returnData = new byte[BYTES_RETURNED];

		this.isPortLetterValid(ioPort);

		// Convert the port letter into a port read command and create the request
		// packet.
		byte[] sendData = new byte[] { (byte) (int) '!', (byte) (int) Character.toLowerCase(ioPort) };

		returnData = this.readData(sendData, BYTES_RETURNED);

		return returnData;
	}

	/**
	 * Reads the I/O port pull up setting of the specified port. This is a 2 step
	 * process. First a read request is sent, then we will try to read the returned
	 * packet, if any.
	 * 
	 * @param ioPort
	 *            The port letter to read from.
	 * 
	 * @return The I/O port pull up register settings.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public byte[] readPortPullUp(char ioPort) throws IOException, IllegalArgumentException {

		// Number of bytes returned in the response packet for this read command.
		int BYTES_RETURNED = 3;

		this.isPortLetterValid(ioPort);

		// Convert the port letter into a port read command create the request packet.
		byte[] sendData = new byte[] { (byte) (int) '@', (byte) (int) Character.toLowerCase(ioPort) };

		byte[] returnData = this.readData(sendData, BYTES_RETURNED);
		return returnData;
	}

	/**
	 * Reads the I/O port values of the specified port. This is a 2 step process.
	 * First a read request is sent, then we will try to read the returned packet,
	 * if any.
	 * 
	 * @param ioPort
	 *            The port letter to read from.
	 * 
	 * @return The I/O port value register settings.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public byte[] readPortValue(char ioPort) throws IOException, IllegalArgumentException {

		// Number of bytes returned in the response packet for this read command.
		int BYTES_RETURNED = 2;

		this.isPortLetterValid(ioPort);

		// Convert the port letter into a port read command and create the request
		// packet.
		byte[] sendData = new byte[] { (byte) (int) Character.toLowerCase(ioPort) };

		byte[] returnData = this.readData(sendData, BYTES_RETURNED);

		return returnData;

	}

	/**
	 * Sends a reset command causing all the ports to be set to all inputs or as set
	 * up in the EEPROM and all EEPROM settings to be read and activated. After the
	 * reset command is sent, a delay may be required before sending any other
	 * commands to the unit.
	 * 
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * 
	 */
	public void resetBoard() throws IOException {

		// Hex AA = Decimal 170 and Hex 55 = Decimal 85
		byte[] data = new byte[] { (byte) (int) '\'', (byte) (int) '@', (byte) 0, (byte) 170, (byte) 85 };

		this.send(data);
	}

	/**
	 * Send the specified packet to the target device.
	 * 
	 * @param data
	 *            The packet data to send.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public void send(byte[] data) throws IOException {

		DatagramPacket packet = new DatagramPacket(data, data.length, this.boardIpAddress, IO24Core.ETHER_IO_UPD_PORT);

		this.datagramSocket.send(packet);

	}
	
	/**
	 * Writes an individual I/O port value.
	 * 
	 * @param ioPort
	 *            The port letter.
	 * @param ioLine
	 *            The I/O line number. 0-7
	 * @param value
	 *            The desired I/O line value. 0 or 1
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public void writeIoLine(char ioPort, int ioLine, int value) throws IllegalArgumentException, IOException {
		
		if (value < 0 || value > 1) {
			throw new IllegalArgumentException("IO24Core: " + String.valueOf(value) + " is an invalid io line state.");
		}

		this.isPortLetterValid(ioPort);
		this.isLineNumberValid(ioLine);

		byte[] returnData;
		int finalState = 0;
		returnData = this.readPortValue('a');
		byte currentState = returnData[1];
		if (value == 0) {
			// Bit shift left a 1 value then invert the bit pattern and finally AND the
			// resulting byte.
			finalState = (byte) (currentState & (~(1 << ioLine)));
		} else if (value == 1) {
			// Bit shift left a 1 value then OR the resulting byte.
			finalState = (byte) (currentState | (1 << ioLine));
		}
		this.writePortValue(ioPort, finalState);
	}

	/**
	 * Writes the given value to the specified port. This effects all ports. The any
	 * value over 255 is set as 255.
	 * 
	 * @param ioPort
	 *            The port letter to write to.
	 * @param value
	 *            The port value to write. 0-255
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 * @throws IllegalArgumentException
	 *             Thrown if an invalid port letter is specified.
	 */
	public void writePortValue(char ioPort, int value) throws IOException, IllegalArgumentException {

		this.isPortLetterValid(ioPort);

		byte[] data = new byte[] { (byte) (int) Character.toUpperCase(ioPort), (byte) value };

		this.send(data);

	}

	/**
	 * Writes the I/O port direction to the supplied value for the specified port.
	 * This effects all lines on the port.
	 * 
	 * @param ioPort
	 *            The port letter to write to.
	 * @param value
	 *            The port value to write. 0-255
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public void writePortDirection(char ioPort, int value) throws IOException, Exception {

		this.isPortLetterValid(ioPort);

		byte[] data = new byte[] { (byte) (int) '!', (byte) (int) Character.toUpperCase(ioPort), (byte) value };

		this.send(data);
	}

	/**
	 * Writes the I/O port pull up register to the supplied value for the specified
	 * port. This effects all lines on the port.
	 * 
	 * @param ioPort
	 *            The port letter to write to.
	 * @param value
	 *            The port value to write. 0-255
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public void writePortPullUp(char ioPort, int value) throws IOException, Exception {

		this.isPortLetterValid(ioPort);

		byte[] data = new byte[] { (byte) (int) '@', (byte) (int) Character.toUpperCase(ioPort), (byte) value };

		this.send(data);

	}

	/**
	 * Writes the specified EEPROM memory location with the specified upper and
	 * lower bytes.
	 * 
	 * @param wordAddress
	 *            The memory block address.
	 * @param msb
	 *            The Most Significant Byte value.
	 * @param lsb
	 *            the Least Significant Byte value.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public void writeEEPROM_Word(int wordAddress, int msb, int lsb) throws IOException {

		byte[] data = new byte[] { (byte) (int) '\'', (byte) (int) 'W', (byte) wordAddress, (byte) msb, (byte) lsb };

		this.send(data);
	}

	/**
	 * Reads the EEPROM word at the specified address. This is a 2 step process. First a read request is
	 * sent, then we will try to read the returned packet.
	 * 
	 * @param address
	 *            The word address in the EEPROM memory.
	 * @return The 2 byte word value.
	 * @throws IOException
	 *             Thrown if a datagram socket error occurred.
	 */
	public byte[] readEEPROM_Word(int address) throws IOException {

		int BYTES_RETURNED = 4;

		// Create the request packet.
		byte[] sendData = new byte[] { (byte) (int) '\'', (byte) (int) 'R', (byte) address, (byte) 0, (byte) 0 };

		byte[] returnData = this.readData(sendData, BYTES_RETURNED);

		return returnData;
	}
	
	public int getDatagramSocketTimeout() {
		return datagramSocketTimeout;
	}

	public void setDatagramSocketTimeout(int datagramSocketTimeout) {
		this.datagramSocketTimeout = datagramSocketTimeout;
	}

}
