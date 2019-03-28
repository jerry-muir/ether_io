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
package com.cybernian.ether_io.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.cybernian.ether_io.utils.Util;

/**
 * 
 * 
 * @author Gerard L. Muir
 */
public class UtilsTest {
	
	/**
	 * Verify that the correct integer value is returned.
	 */
	@Test
	public void testBinary2int() {
		
		assertEquals (0, Util.binary2int("00000000"));
		assertEquals (0, Util.binary2int("0"));
		assertEquals (15, Util.binary2int("00001111"));
		assertEquals (15, Util.binary2int("1111"));
		assertEquals (240, Util.binary2int("11110000"));
		assertEquals (255, Util.binary2int("11111111"));
		
	}
	
	
	/**
	 * Verify that the correct short value is returned.
	 */
	@Test
	public void testTwoBytesToShort() {
		
		byte byteData1 = 0; byte byteData2 = 0; short returnData = (short) 0;
		assertEquals (returnData, Util.twoBytesToShort(byteData1, byteData2));
		
		byteData1 = (byte) 0; byteData2 = (byte) 255; returnData = (short) 255;
		assertEquals (returnData, Util.twoBytesToShort(byteData1, byteData2));
		
		byteData1 = (byte) 255; byteData2 = (byte) 0; returnData = (short) 65280;
		assertEquals (returnData & 0xffff, Util.twoBytesToShort(byteData1, byteData2));	
		
		byteData1 = (byte) 255; byteData2 = (byte) 255; returnData = (short) 65535;
		assertEquals (returnData & 0xffff, Util.twoBytesToShort(byteData1, byteData2));
		
	}
	
	/**
	 * Verify the correct binary string representation of the provided byte. 
	 */
	@Test
	public void testZeroPaddedString() {
		
		assertEquals("00000000", Util.zeroPaddedString( (byte) 0) );
		assertEquals("00001111", Util.zeroPaddedString( (byte) 15) );
		assertEquals("11110000", Util.zeroPaddedString( (byte) 240) );
		assertEquals("11111111", Util.zeroPaddedString( (byte) 255) );
		
	}
	
	/**
	 * Verify proper formatting of the Identify command bytes.
	 */
	@Test
	public void testParseIdentityBytes() {
									
		byte[] identityBytes = new byte [] {  73, 79, 50, 52,     // Response Identifier
				                               0, 0, 0, 0, 0, 0,  // MAC Address
				                               0, 0,              // Version Number
				                               0, 0, 0, 0 };      // IP Address	
		assertEquals("00:00:00:00:00:00 00 0.0.0.0", Util.parseIdentityBytes(identityBytes));
		
		byte[] identityBytes1 = new byte [] { 73, 79, 50, 52,           // Response Identifier
				                               0, 17, -70, 2, 15, 92,   // MAC Address
				                               2, 2,                    // Version Number
				                               -64, -88, 0, 11 };       // IP Address	
		assertEquals("00:11:ba:02:0f:5c 22 192.168.0.11", Util.parseIdentityBytes(identityBytes1));
		
		// Convert decimal 255 to a byte: (byte) 255 = xFF
		byte[] identityBytes2 = new byte [] { -128, -128, -128, -128,                                                 // Response Identifier
				                             (byte) 255 , (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, // MAC Address
				                              9, 9,                                                                   // Version Number
				                              10, 10, 10, 10 };                                                       // IP Address
		assertEquals("ff:ff:ff:ff:ff:ff 99 10.10.10.10", Util.parseIdentityBytes(identityBytes2));

	}
	
	@Test
	public void testParseHostBytes() {
		
		byte[] hostBytes = new byte [] { (byte) (int) '%', // Response Identifier
				(byte) 0, (byte) 0, (byte) 0,              // Serial Number - Decimal 8421504
				(byte) 0, (byte) 0, (byte) 0, (byte)0,     // IP Address
				0, 0, 0, 0, 0, 0,                          // MAC Address	
				(byte) 0, (byte) 0};                       // Port 0
		assertEquals("0 0.0.0.0 00:00:00:00:00:00 0", Util.parseHostBytes(hostBytes));	
		
		byte[] hostBytes1 = new byte [] { (byte) (int) '%',  // Response Identifier
				(byte) 128, (byte) 128, (byte) 128,          // Serial Number - Decimal 8421504
				(byte) 192, (byte) 168, (byte) 0, (byte)11,  // IP Address
				0, 17, -70, 2, 15, 92,                       // MAC Address	
				(byte) 16, (byte) 247};                      // Port 4343
		assertEquals("8421504 192.168.0.11 00:11:ba:02:0f:5c 4343", Util.parseHostBytes(hostBytes1));
		
		byte[] hostBytes2 = new byte [] { (byte) (int) '%',  // Response Identifier
				(byte) 255, (byte) 255, (byte) 255,          // Serial Number - Decimal 8421504
				(byte) 10, (byte) 10, (byte) 10, (byte)10,  // IP Address
				(byte) 255 , (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, // MAC Address	
				(byte) 255, (byte) 255};                      // Port 4343
		assertEquals("16777215 10.10.10.10 ff:ff:ff:ff:ff:ff 65535", Util.parseHostBytes(hostBytes2));
		
	}
	
}
