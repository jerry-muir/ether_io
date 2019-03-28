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
package com.cybernian.ether_io.utils;

/**
 * Some handy methods to deal with data from the Ether IO family of I/O boards.
 * 
 * @author Gerard L. Muir
 *
 */
public class Util {

	/**
	 * Wrapper method to convert a binary string representation of the I/O port
	 * state to an integer value which can then be stored in a byte.
	 * 
	 * @param binaryString
	 *            The binary string to convert.
	 * @return The integer value of the binary string.
	 */
	public static int binary2int(String binaryString) {

		int integer = Integer.parseInt(binaryString, 2);

		return integer;
	}

	/**
	 * Returns a short containing the value of the combined bytes.
	 * 
	 * @param msb
	 *            Most Significant Byte
	 * @param lsb
	 *            Least Significant Byte
	 * @return Value of the combined bytes.
	 */
	public static int twoBytesToShort(byte msb, byte lsb) {
		int msbByte = (msb & 0xFF) << 8;
		int lsbByte = (lsb & 0xFF);
		int word = (int) (msbByte | lsbByte) & 0xffff;
		return word;
	}

	/**
	 * Returns an 8 bit binary string representation of the supplied byte.
	 * 
	 * @param dataByte
	 *            The byte to be converted in to a binary string.
	 * @return An 8 bit zero padded binary string.
	 * 
	 */
	public static String zeroPaddedString(byte dataByte) {

		int dataInt = dataByte & 0xff;
		String binaryValue = Integer.toBinaryString(dataInt);

		// If the resultant integer string is less than 8 bits, add 0's to the
		// significant bits until it is an 8 bit string. This is because the
		// toBinaryString method only returns a string to the most significant
		// 1's bit.
		// Example: 4 = 100 not 00000100.
		StringBuilder zeroPaddedString = new StringBuilder();
		for (int i = binaryValue.length(); i < 8; i++) {
			zeroPaddedString.append('0');
		}
		zeroPaddedString.append(binaryValue);

		return zeroPaddedString.toString();

	}

	/**
	 * Returns a formatted string containing the board identity data plus the IP address of
	 * the board.
	 * 
	 * @param byteData
	 *            Byte data from the Identify command.
	 * @return board data string.
	 */
	public static String parseIdentityBytes(byte[] byteData) {

		// First 4 bytes are the response identifier which we can ignore.
		// Next 6 bytes are the MAC address
		byte[] macBytes = { 0, 0, 0, 0, 0, 0 };
		for (int i = 0; i <= 5; i++) {
			macBytes[i] = byteData[i + 4];
		}
		String macAddress = buildMAC_Address(macBytes);

		// Next 2 bytes are the board version number given in decimal form.
		StringBuilder firmwareVersionBuilder = new StringBuilder();

		for (int byteCount = 10; byteCount < 12; byteCount++) {
			firmwareVersionBuilder.append(byteData[byteCount] & 0xff);
		}

		// Last 4 bytes are the board IP address.
		byte[] IP_Bytes = { 0, 0, 0, 0 };
		for (int i = 0; i <= 3; i++) {
			IP_Bytes[i] = byteData[i + 12];
		}
		String IP_Address = buildIP_Address(IP_Bytes);

		StringBuilder cardInfo = new StringBuilder().append(macAddress).append(" ")
				.append(firmwareVersionBuilder.toString()).append(" ").append(IP_Address);

		return cardInfo.toString();
	}

	
	/**
	 * Returns a formatted string containing the boards host data.
	 * 
	 * @param byteData
	 *            16 bytes of data from the host data command.
	 * @return Host data elements.
	 */
	public static String parseHostBytes(byte[] byteData) {

		// First 1st byte is the response identifier which we can ignore.

		// Next 3 bytes are the board serial number.

		int byteOne = (byteData[1] & 0xFF) << 16;
		int byteTwo = (byteData[2] & 0xFF) << 8;
		int serialInt = (byteOne | byteTwo | (byteData[3] & 0xFF));
		String serialNumber = Integer.toString(serialInt);

		// Next 4 bytes are the board IP address.
		// IP address is given in decimal form with a "." separator.
		byte[] IP_Bytes = { 0, 0, 0, 0 };
		for (int byteCount = 0; byteCount <= 3; byteCount++) {
			IP_Bytes[byteCount] = byteData[byteCount + 4];
		}
		String IP_Address = buildIP_Address(IP_Bytes);

		// Next 6 bytes are the MAC address
		byte[] macBytes = { 0, 0, 0, 0, 0, 0 };
		for (int byteCount = 0; byteCount <= 5; byteCount++) {
			macBytes[byteCount] = byteData[byteCount + 8];
		}
		String macAddress = buildMAC_Address(macBytes);

		// Next 2 bytes are the board port number given in decimal form.
		StringBuilder portNumberBuilder = new StringBuilder();
		portNumberBuilder.append(twoBytesToShort(byteData[14], byteData[15]));
		String portNumber = portNumberBuilder.toString();

		StringBuilder cardInfo = new StringBuilder().append(serialNumber).append(" ").append(IP_Address).append(" ")
				.append(macAddress).append(" ").append(portNumber);

		return cardInfo.toString();
	}

	/**
	 * Returns a formatted string containing a MAC address.
	 * 
	 * @param byteData
	 *            6 Bytes of MAC address data.
	 * @return MAC address.
	 */
	private static String buildMAC_Address(byte[] byteData) {

		StringBuilder macAddressBuilder = new StringBuilder();
		// MAC address has 6 bytes.
		for (int byteCount = 0; byteCount <= 5; byteCount++) {
			// MAC addresses are given as two digits separated by a colon, therefore
			// prefix the MAC address byte with zero if it is hex value 0-f.
			// The MAC address is given in hex form with a ':' separator.

			if ((byteData[byteCount] & 0xff) <= 15) {
				macAddressBuilder.append("0").append(Integer.toHexString(byteData[byteCount] & 0xff));
			} else {
				macAddressBuilder.append(Integer.toHexString(byteData[byteCount] & 0xff));
			}
			// Add separator.
			if (byteCount < 5)
				macAddressBuilder.append(":");
		}

		return macAddressBuilder.toString();
	}
	
	/**
	 * Returns a formatted string containing an IP address.
	 * 
	 * @param byteData
	 *            4 bytes of IP address data.
	 * @return IP address.
	 */
	private static String buildIP_Address(byte[] byteData) {

		StringBuilder IP_AddressBuilder = new StringBuilder();

		// IP address is given in decimal form with a "." separator.
		for (int byteCount = 0; byteCount <= 3; byteCount++) {
			if (byteCount < 3) {
				IP_AddressBuilder.append(byteData[byteCount] & 0xff).append(".");
			} else {
				IP_AddressBuilder.append(byteData[byteCount] & 0xff);
			}
		}
		return IP_AddressBuilder.toString();
	}
}
