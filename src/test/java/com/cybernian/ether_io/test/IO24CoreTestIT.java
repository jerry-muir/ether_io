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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cybernian.ether_io.core.IO24Common;
import com.cybernian.ether_io.core.IO24Core;

/**
 * 
 * 
 * @author Gerard L. Muir
 */
public class IO24CoreTestIT {

	private static final int RESET_DELAY = 700; // Time in milliseconds.

	private IO24Common IO24;
	
	static Properties testProperties = new Properties();
	
	@BeforeClass
    public static void oneTimeSetUp() {
		try {
			testProperties.load( new FileInputStream("src/test/java/com/cybernian/ether_io/test/testConfig.properties") );
		} catch (IOException e) {
			fail(e.getMessage());
		}       
    }
	
	@After
	public void oneTimeTearDown() {
		
		// Release socket after all testing is done.
		if (this.IO24 == null) {
			this.IO24.closeSocket();
		}

	}

	/**
	 * Runs before each Test Case method is called.
	 * 
	 * @throws Exception
	 *             Thrown if a datagram socket error occurred.
	 */
	@Before
	public void setUp() throws Exception {
		
		// Use default Either IO board address if no property is specified.
		if (this.IO24 == null) {
			String ipAddress = testProperties.getProperty("boardIP", "10.10.10.10");
			this.IO24 = (IO24Common) new IO24Common(ipAddress);
		}
	}

	/**
	 * Runs after each test method is called.
	 */
	@After
	public void tearDown() {
	}

	
	@Test
	public void testPortDirection() {
		
		byte[] returnData; 
		
		try {
			
			this.IO24.writePortDirection('a', (byte) 0);

			returnData = this.IO24.readPortDirection('a');
			assert ((byte) (int) '!' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'A' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('a', (byte) 255);

			returnData = this.IO24.readPortDirection('a');
			assert ((byte) (int) '!' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'A' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('b', (byte) 0);
			
			returnData = this.IO24.readPortDirection('b');
			assert ((byte) (int) '!' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'B' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('b', (byte) 255);

			returnData = this.IO24.readPortDirection('b');
			assert ((byte) (int) '!' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'B' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('c', (byte) 0);
			
			returnData = this.IO24.readPortDirection('c');
			assert ((byte) (int) '!' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'C' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('c', (byte) 255);

			returnData = this.IO24.readPortDirection('c');
			assert ((byte) (int) '!' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'C' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
		} catch (Exception e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}
	
	@Test
	public void testPortPullUp() {
		
		byte[] returnData; 
		
		try {
			
			this.IO24.writePortDirection('a', (byte) 0);
			this.IO24.writePortPullUp('a', (byte) 0);

			returnData = this.IO24.readPortPullUp('a');
			assert ((byte) (int) '@' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'A' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortPullUp('a', (byte) 255);

			returnData = this.IO24.readPortPullUp('a');
			assert ((byte) (int) '@' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'A' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('b', (byte) 0);
			this.IO24.writePortPullUp('b', (byte) 0);
			
			returnData = this.IO24.readPortPullUp('b');
			assert ((byte) (int) '@' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'B' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('b', (byte) 255);
			this.IO24.writePortPullUp('b', (byte) 255);

			returnData = this.IO24.readPortPullUp('b');
			assert ((byte) (int) '@' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'B' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('c', (byte) 0);
			this.IO24.writePortPullUp('c', (byte) 0);
			
			returnData = this.IO24.readPortPullUp('c');
			assert ((byte) (int) '@' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'C' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortPullUp('c', (byte) 255);

			returnData = this.IO24.readPortPullUp('c');
			assert ((byte) (int) '@' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'C' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
		} catch (Exception e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}
	
	@Test
	public void testPortThreshold() {
		
		byte[] returnData; 
		
		try {
			
			this.IO24.writePortDirection('a', (byte) 0);
			this.IO24.writePortThreshold('a', (byte) 0);

			returnData = this.IO24.readPortThreshold('a');
			assert ((byte) (int) '#' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'A' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortThreshold('a', (byte) 255);

			returnData = this.IO24.readPortThreshold('a');
			assert ((byte) (int) '#' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'A' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('b', (byte) 0);
			this.IO24.writePortThreshold('b', (byte) 0);
			
			returnData = this.IO24.readPortThreshold('b');
			assert ((byte) (int) '#' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'B' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('b', (byte) 255);
			this.IO24.writePortThreshold('b', (byte) 255);

			returnData = this.IO24.readPortThreshold('b');
			assert ((byte) (int) '#' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'B' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('c', (byte) 0);
			this.IO24.writePortThreshold('c', (byte) 0);
			
			returnData = this.IO24.readPortThreshold('c');
			assert ((byte) (int) '#' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'C' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortThreshold('c', (byte) 255);

			returnData = this.IO24.readPortThreshold('c');
			assert ((byte) (int) '#' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'C' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
		} catch (Exception e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}
	
	
	
	@Test
	public void testPortSchmittTrigger() {
		
		byte[] returnData; 
		
		try {
			
			this.IO24.writePortDirection('a', (byte) 0);
			this.IO24.writePortSchmittTrigger('a', (byte) 0);

			returnData = this.IO24.readPortSchmitt('a');
			assert ((byte) (int) '$' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'A' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortSchmittTrigger('a', (byte) 255);

			returnData = this.IO24.readPortSchmitt('a');
			assert ((byte) (int) '$' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'A' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('b', (byte) 0);
			this.IO24.writePortSchmittTrigger('b', (byte) 0);
			
			returnData = this.IO24.readPortSchmitt('b');
			assert ((byte) (int) '$' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'B' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('b', (byte) 255);
			this.IO24.writePortSchmittTrigger('b', (byte) 255);

			returnData = this.IO24.readPortSchmitt('b');
			assert ((byte) (int) '$' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'B' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortDirection('c', (byte) 0);
			this.IO24.writePortSchmittTrigger('c', (byte) 0);
			
			returnData = this.IO24.readPortSchmitt('c');
			assert ((byte) (int) '$' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'C' == ((int) returnData[1] & 0xff));
			assert (0 == ((int) returnData[2] & 0xff));
			
			this.IO24.writePortSchmittTrigger('c', (byte) 255);

			returnData = this.IO24.readPortSchmitt('c');
			assert ((byte) (int) '$' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'C' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
		} catch (Exception e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}
	
	@Test
	public void testPortValue() {
		
		byte[] returnData;
		
		try {
			
			this.IO24.writePortDirection('a', (byte) 0);
			this.IO24.writePortValue('a', (byte) 0);

			returnData = this.IO24.readPortValue('a');
			assert ((byte) (int) 'A' == ((int) returnData[0] & 0xff));
			assert (0 == ((int) returnData[1] & 0xff));
			
			this.IO24.writePortValue('a', (byte) 255);

			returnData = this.IO24.readPortValue('a');
			assert ((byte) (int) 'A' == ((int) returnData[0] & 0xff));
			assert (255 == ((int) returnData[1] & 0xff));
			
			this.IO24.writePortDirection('b', (byte) 0);
			this.IO24.writePortValue('b', (byte) 0);
			
			returnData = this.IO24.readPortValue('b');
			assert ((byte) (int) 'B' == ((int) returnData[0] & 0xff));
			assert (0 == ((int) returnData[1] & 0xff));
			
			this.IO24.writePortValue('b', (byte) 255);

			returnData = this.IO24.readPortValue('b');
			assert ((byte) (int) 'B' == ((int) returnData[0] & 0xff));
			assert (255 == ((int) returnData[1] & 0xff));
			
			this.IO24.writePortDirection('c', (byte) 0);
			this.IO24.writePortValue('c', (byte) 0);
			
			returnData = this.IO24.readPortValue('c');
			assert ((byte) (int) 'C' == ((int) returnData[0] & 0xff));
			assert (0 == ((int) returnData[1] & 0xff));
			
			this.IO24.writePortValue('c', (byte) 255);

			returnData = this.IO24.readPortValue('c');
			assert ((byte) (int) 'C' == ((int) returnData[0] & 0xff));
			assert (255 == ((int) returnData[1] & 0xff));
			
		} catch (Exception e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}
	
	@Test
	public void testReset() {
		
		byte[] returnData; 
		
		try {
			// Write port A direction EEPROM register for inputs.
			this.IO24.writeEnableEEPROM();
			this.IO24.writeEEPROM_Word(8, (byte) 0, (byte) 255);
			
			// Write current port A direction to output.
			this.IO24.writePortDirection('a', (byte) 0);
			
			this.IO24.resetBoard();

			try { // Allow time for the board to reset.
				Thread.sleep(RESET_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			returnData = this.IO24.readPortDirection('a');
			assert ((byte) (int) '!' == ((int) returnData[0] & 0xff));
			assert ((byte) (int) 'A' == ((int) returnData[1] & 0xff));
			assert (255 == ((int) returnData[2] & 0xff));
			
			// Return the Word to it's original value.
			this.IO24.writeEEPROM_Word(8, (byte) 255, (byte) 255);
			this.IO24.writeDisableEEPROM();
			
		} catch (Exception e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}
	
	/**
	 * Verify a response from the read EEPROM command. We will only validate the first 2 bytes of the 4
	 * byte return message. The first 2 return bytes are the response identifier and will be "R" followed by the word
	 * address requested. The next 2 bytes are data which may or may not be programmed.
	 */
	@Test
	public void testReadEEPROM() {
		
		int firstWordAddress = 0; // Register address of the first word to be read.
		int lastWordaddress = 63; // Register address of the last word to be read.
		int expectedNumberOfBytesReturned = 4;
		byte[] returnData;   // Returned data from the read command.
		
		try {
			returnData = this.IO24.readEEPROM_Word(firstWordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(firstWordAddress == ((int) returnData[1] & 0xff));
			assert(expectedNumberOfBytesReturned == returnData.length);
			
			returnData = this.IO24.readEEPROM_Word(lastWordaddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(lastWordaddress == ((int) returnData[1] & 0xff));
			assert(expectedNumberOfBytesReturned == returnData.length);
			
		} catch (IOException e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}
	
	/**
	 */
	@Test
	public void testWriteEEPROM() {
		
		int wordAddress = 48; // First User Data register address.
		int expectedNumberOfBytesReturned = 4;
		byte[] returnData;   // Returned data from the read command.
		
		try {
			
			this.IO24.writeEnableEEPROM();
			this.IO24.writeEEPROM_Word(wordAddress, 255, 255);
			Thread.sleep(50); // Need a little for the write cycle to complete.
			returnData = this.IO24.readEEPROM_Word(wordAddress);

			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(255 == ((int) returnData[2] & 0xff)); 
			assert(255 == ((int) returnData[3] & 0xff));
			assert(expectedNumberOfBytesReturned == returnData.length);
				
			this.IO24.writeEEPROM_Word(wordAddress, 255, 0);
			Thread.sleep(50);
			returnData = this.IO24.readEEPROM_Word(wordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(255 == ((int) returnData[2] & 0xff)); 
			assert(0 == ((int) returnData[3] & 0xff)); 
			assert(expectedNumberOfBytesReturned == returnData.length);
			
			// Write word back to original state.
			this.IO24.writeEEPROM_Word(wordAddress, 255, 255);

			this.IO24.writeDisableEEPROM();
			
		} catch (IOException e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 */
	@Test
	public void testWriteEnableAndDisable() {
		
		int wordAddress = 48; // First User Data register address.
		int expectedNumberOfBytesReturned = 4;
		byte[] returnData;   // Returned data from the read command.
		
		try {
			
			// Make sure the current state has #F in the value register.
			returnData = this.IO24.readEEPROM_Word(wordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(255 == ((int) returnData[2] & 0xff)); // MSB of word.
			assert(255 == ((int) returnData[3] & 0xff)); // LSB of word.
			assert(expectedNumberOfBytesReturned == returnData.length);
			
			// Try to write to a disabled EEPROM.
			this.IO24.writeDisableEEPROM(); // Make sure we are currently disabled.
			this.IO24.writeEEPROM_Word(wordAddress, 0, 0);
			Thread.sleep(50);
			returnData = this.IO24.readEEPROM_Word(wordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(255 == ((int) returnData[2] & 0xff)); // MSB of word.
			assert(255 == ((int) returnData[3] & 0xff)); // LSB of word.
			assert(expectedNumberOfBytesReturned == returnData.length);
			
			// Enable the EEPROM for writing and write.
			this.IO24.writeEnableEEPROM();
			this.IO24.writeEEPROM_Word(wordAddress, 0, 0);
			Thread.sleep(50); // Need a little for the write cycle to complete.
			returnData = this.IO24.readEEPROM_Word(wordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(0 == ((int) returnData[2] & 0xff)); // MSB of word.
			assert(0 == ((int) returnData[3] & 0xff)); // LSB of word.
			assert(expectedNumberOfBytesReturned == returnData.length);
			
			// Rewrite the register to its' original value.
			this.IO24.writeEEPROM_Word(wordAddress, 255, 255);
			Thread.sleep(50); // Need a little for the write cycle to complete.
			returnData = this.IO24.readEEPROM_Word(wordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(255 == ((int) returnData[2] & 0xff)); // MSB of word.
			assert(255 == ((int) returnData[3] & 0xff)); // LSB of word. 
			assert(expectedNumberOfBytesReturned == returnData.length);
			
			// With the EEPROM write disabled, try to change the register. This tests the disable.
			this.IO24.writeDisableEEPROM();
			this.IO24.writeEEPROM_Word(wordAddress, 0, 0);
			Thread.sleep(50); // Need a little for the write cycle to complete.
			returnData = this.IO24.readEEPROM_Word(wordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(255 == ((int) returnData[2] & 0xff)); //// MSB of word.
			assert(255 == ((int) returnData[3] & 0xff)); //// LSB of word. 
			assert(expectedNumberOfBytesReturned == returnData.length);
			
		} catch (IOException e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				this.IO24.writeDisableEEPROM();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 */
	@Test
	public void testEraseEEPROM_Word() {

		int wordAddress = 48; // First User Data register address.
		int expectedNumberOfBytesReturned = 4;
		byte[] returnData;   // Returned data from the read command.
		
		try {
			
			// Make sure the current state has #F in the value register.
			returnData = this.IO24.readEEPROM_Word(wordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(255 == ((int) returnData[2] & 0xff)); // MSB of word
			assert(255 == ((int) returnData[3] & 0xff)); // LSB of word

			assert(expectedNumberOfBytesReturned == returnData.length);
			
			// Enable the EEPROM for writing and write.
			this.IO24.writeEnableEEPROM();
			this.IO24.writeEEPROM_Word(wordAddress, 170, 170);
			Thread.sleep(50); // Need a little for the write cycle to complete.
			returnData = this.IO24.readEEPROM_Word(wordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(170 == ((int) returnData[2] & 0xff));
			assert(170 == ((int) returnData[3] & 0xff)); 
			assert(expectedNumberOfBytesReturned == returnData.length);
			
			// Erase the word.
			this.IO24.eraseEEPROM_Word(wordAddress);
			Thread.sleep(50); // Need a little for the write cycle to complete.
			returnData = this.IO24.readEEPROM_Word(wordAddress);
			
			assert((byte) (int) 'R' == ((int) returnData[0] & 0xff));
			assert(wordAddress == ((int) returnData[1] & 0xff));
			assert(255 == ((int) returnData[2] & 0xff));
			assert(255 == ((int) returnData[3] & 0xff)); 
			assert(expectedNumberOfBytesReturned == returnData.length);
			
			
		} catch (IOException e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				this.IO24.writeDisableEEPROM();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 */
	@Test
	public void testWriteIoLine() {

		int OUTPUT = 0;
		byte[] returnData;
		
		try {
			this.IO24.writePortDirection('a', OUTPUT);
			this.IO24.writePortValue('a', 0);
			returnData = this.IO24.readPortValue('a');
			assert(0 == ((int) returnData[1] & 0xff));
			this.IO24.writeIoLine('a', 0, 1);
			returnData = this.IO24.readPortValue('a');
			assert(1 == ((int) returnData[1] & 0xff));
			this.IO24.writeIoLine('a', 1, 1);
			returnData = this.IO24.readPortValue('a');
			assert(3 == ((int) returnData[1] & 0xff));
			this.IO24.writeIoLine('a', 7, 1);
			returnData = this.IO24.readPortValue('a');
			assert(131 == ((int) returnData[1] & 0xff));
			this.IO24.writeIoLine('a', 1, 1);
			returnData = this.IO24.readPortValue('a');
			assert(131 == ((int) returnData[1] & 0xff));
			this.IO24.writePortValue('a', 255);
			returnData = this.IO24.readPortValue('a');
			assert(255 == ((int) returnData[1] & 0xff));
			this.IO24.writeIoLine('a', 0, 0);
			returnData = this.IO24.readPortValue('a');
			assert(254 == ((int) returnData[1] & 0xff));
			this.IO24.writeIoLine('a', 1, 0);
			returnData = this.IO24.readPortValue('a');
			assert(252 == ((int) returnData[1] & 0xff));
			this.IO24.writeIoLine('a', 7, 0);
			returnData = this.IO24.readPortValue('a');
			assert(124 == ((int) returnData[1] & 0xff));
			this.IO24.writeIoLine('a', 0, 0);
			returnData = this.IO24.readPortValue('a');
			assert(124 == ((int) returnData[1] & 0xff));
		} catch (Exception e) {
			fail("Exception \"" +e.getMessage() +"\" from " +Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}

	/**
	 * Verify that an invalid port identifier character produces and exception.
	 */
	@Test
	public void testBadLineNumber() {
		try {
			this.IO24.isLineNumberValid(24);
			fail ("Invalid port character did not fail!"); // Only reached if we do not throw the exception.
		} catch (Exception e) {
			assertEquals ("IO24Core: Validation error: \"24\" is not a valid line number.", e.getMessage());
		}
	}

	/**
	 * Verify that an invalid port identifier character produces and exception.
	 */
	@Test
	public void testBadPortCharacter() {
		try {
			this.IO24.isPortLetterValid('z');
			fail ("Invalid port character did not fail!"); // Only reached if we do not throw the exception.
		} catch (Exception e) {
			assertEquals ("IO24Core: Validation error: \"z\" is not a valid port id.", e.getMessage());
		}
	}

	/**
	 * Verify a response from the IO24 identify command. We will only validate the first 4 bytes of the 16
	 * byte return message. The first 4 return bytes are the response identifier and will be "IO24". The next
	 * 6 bytes are the MAC address and the next 2 bytes are the firmware version. The last 4 bytes are the IP
	 * address of the board which was added by the driver. The MAC, firmware and IP address will vary
	 * per board and do not need to be validated.
	 */
	@Test
	public void testIdentify() {
		
		ArrayList<byte[]> boardList;
		
		try {
			boardList = IO24Core.identify();
			// "IO24" = Decimal [73, 79, 50, 52}
			assert ( 73 == ( (int) boardList.get(0)[0] & 0xff) );
			assert ( 79 == ( (int) boardList.get(0)[1] & 0xff) );
			assert ( 50 == ( (int) boardList.get(0)[2] & 0xff) );
			assert ( 52 == ( (int) boardList.get(0)[3] & 0xff) );
			assert ( 16 == boardList.get(0).length);
			
		} catch (IOException e) {
			fail("Exception \"" + e.getMessage() + "\" from "
					+ Thread.currentThread().getStackTrace()[1].getMethodName());
		}
	}
	
	
}// Class
