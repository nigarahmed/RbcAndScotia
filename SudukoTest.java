package com.ahmedm.scotia;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SudukoTest {
	
	private static Suduko suduko;

	@Before
	public void setup() {
		suduko = new Suduko();
	}
	
	@Test
	public void testValidSudukoResult() throws SudukoException {
		suduko.setFilename("sudukoResult.txt");
		assertTrue(suduko.verifySolution());
	}
	
	@Test(expected=SudukoException.class)
	public void testNonExistentFile() throws SudukoException {
		suduko.setFilename("nonExistentFile.txt");
		assertFalse(suduko.verifySolution());
	}
	
	@Test
	public void testInvalidResultFile() throws SudukoException {
		suduko.setFilename("sudukoInvalidResult.txt");
		assertFalse(suduko.verifySolution());
	}
	
	@Test
	public void testNonNumericCharsResult() throws SudukoException {
		suduko.setFilename("NonNumericChars.txt");
		assertFalse(suduko.verifySolution());
	}
	
	@Test(expected=SudukoException.class)
	public void testEmptyFile() throws SudukoException {
		suduko.setFilename("EmptyFile.txt");
		assertFalse(suduko.verifySolution());
	}
	
	@Test(expected=SudukoException.class)
	public void testMissingRows() throws SudukoException {
		suduko.setFilename("MissingRows.txt");
		assertFalse(suduko.verifySolution());
	}
	
	@Test(expected=SudukoException.class)
	public void testIncompleteRows() throws SudukoException {
		suduko.setFilename("IncompleteRows.txt");
		assertFalse(suduko.verifySolution());
	}
}
