package com.ahmedm.scotia;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Exception class to handle any exceptions.
 * @author Mubashir Ahmed
 *
 */
class SudukoException extends Exception {
	private static final long serialVersionUID = 1;
	
	public SudukoException(String msg) {
		super(msg);
	}
}

/**
 * Main class to read the filename and validate the result
 * @author Mubashir Ahmed
 *
 */
public class Suduko {
	private final Validator validator = new Validator();
	private String filename;
	
	// default constructor
	public Suduko() {
		filename = null;
	}
	
	// constructor which takes file name
	public Suduko(String filename) {
		this.filename = filename;
	}
	

	/**
	 * function to verify the solution.
	 * @return true for valid solution otherwise false.
	 * @throws SudukoException
	 */
	public boolean verifySolution() throws SudukoException {
        Scanner s = null;
        String rows[] = new String[50];
        int totalLines = 0;

        if(!validator.validateFileName(filename)) {
        	throw new SudukoException("file:- " + filename + " is invalid");
        }
        
        try {
            s = new Scanner(new BufferedReader(new FileReader(filename)));
            while (s.hasNextLine()) {
                // read the data in String rows
                rows[totalLines++] = s.nextLine();
            }
        } catch (FileNotFoundException e) {
            throw new SudukoException("File: " + filename + " is not found");
        } finally {
        	if(s != null) {
        		s.close();
        	}
        }
        
        // use the Validator to validate the solution
        if(!validator.validateSudukoSolution(rows, totalLines)) {
        	return false;
        }
       
        return true;
    }
	
	// getter method for filename
	public String getFilename() {
		return filename;
	}

	// setter method for filename
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Validator class to validate the solution
	 *
	 */
	private class Validator {
		private final Set<Character> rowSet;
		private final Set<Character> colSet;
		private final Set<Character> gridSet;
		
		public Validator() {
			rowSet = new HashSet<>();
			colSet = new HashSet<>();
			gridSet = new HashSet<>();
		}
		
		/**
		 * validate the file name
		 * @param filename
		 * @return true if it is valid otherwise false
		 */
		public boolean validateFileName(String filename) {
			if(filename == null || filename.isEmpty()) {
				return false;
			}
			return true;
		}
		
		/**
		 * Function to validate the solution, it checks for row, column and grid uniqueness. For fast lookup and uniqueness, HashSet is used. 
		 * Complexity is almost (81 times the hash insertions for rows and column validations) + (81 times hash insertion for grid validation).
		 * 
		 * @param rows - actual data rows
		 * @param totalLines - total number of lines
		 * @return true for valid solution otherwise false
		 * @throws SudukoException
		 */
		public boolean validateSudukoSolution(String rows[], int totalLines) throws SudukoException {
			
			if(validator.isFileEmpty(totalLines)) {
	        	throw new SudukoException("File: " + filename + " is empty");
	        }
	        
	        if(!validator.validateRowsAndColumnsCount(rows, totalLines)) {
	        	throw new SudukoException("File: " + filename + " contains incomplete rows, it should be 9x9");
	        }
			// validate rows and columns uniqueness
			if(!validateRowsAndColumsUniqueness(rows, totalLines)) {
				return false;
			}
			
			// validate the grids
			if(!validateGrid(rows, totalLines)) {
				return false;
			}
			
			return true;
		}
		
		/**
		 * Function to check for the uniqueness of the numbers in a 3x3 grid
		 * @param gridRows - rows
		 * @param startRow - startRow index
		 * @param endRow - endRow index
		 * @param startCol - startColumn index
		 * @param endCol - endColumn index
		 * @return true if it is unique otherwise false
		 */
		private boolean gridCheck(String gridRows[], int startRow, int endRow, int startCol, int endCol) {
			for(int i = startRow; i < endRow; i++) {
				for(int j = startCol; j < endCol; j++) {
					// check whether the digit is already in the gridSet, if yes then return false
					if(gridSet.contains(gridRows[i].charAt(j))) {
						return false;
					}
					gridSet.add(gridRows[i].charAt(j));
				}
			}
			
			gridSet.clear();
			return true;
		}
		
		/**
		 * validate the grids. The total number of grids in a 9x9 suduko is 9 (i.e. 3x3), where each grid is again 3x3
		 * @param rows
		 * @param totalLines
		 * @return true for valid grid otherwise false
		 */
		private boolean validateGrid(String rows[], int totalLines) {
			/** 
	         * Grid check:- In a 9x9 suduko, there are 9, 3x3 grids, i.e. there are 3 rows, each row has 3 grids where each grid is 3x3.
	         */
	        int startRow = 0;
	        int endRow = 3;
	        int startCol = 0;
	        int endCol = 3;
	        for(int i = 0; i < 3; i++) {
	        	for (int j = 0; j < 3; j++) {
	        		if(!gridCheck(rows, startRow, endRow, startCol, endCol)) {
	            		return false;
	            	}
	        		
	        		/*
	        		 * set the column indices of next grid 
	        		 */
	        		startCol = endCol;
	        		endCol += 3;    		
	        	}
	        	
	        	// set the row indices of next grid
	        	startRow = endRow;
	        	endRow += 3;
	        	// reset the column indices
	        	startCol = 0;
	        	endCol = 3;
	        }
	        
	        return true;
		}
		
		/**
		 * validate the uniqueness of rows and columns
		 * @param rows
		 * @param totalLines
		 * @return true if it is valid otherwise false.
		 */
		private boolean validateRowsAndColumsUniqueness(String rows[], int totalLines) {
			/**
	         * logic is, as there are 9x9:- 
	         * 1. verify that each row contains unique number from 1 to 9
	         * 2. verify that the corresponding column also contains unique number from 1 to 9. 
	         * Example:- verify that row[1] contains unique numbers, then the corresponding column[1] also should contains unique digits 
	         */
	        for(int i = 0; i < totalLines; i++) {
	        	for(int j = 0; j < totalLines; j++) {
	                /**
	        		 * first condition is for scanning the row digits, second condition is for scanning the column digit.
	        		 * 1. throw error if the char is non-Numeric.
	        		 * 2. throw error if the digit is already in its corresponding rowSet or colSet
	        		 */
	        		if(!validateRowDigit(rows[i].charAt(j)) || !validateColumnDigit(rows[i].charAt(j)))  {
	        			return false;
	        		} else {
	        			// add the digits into their corresponding HashSet
	        			rowSet.add(rows[i].charAt(j));
	        			colSet.add(rows[j].charAt(i));
	        		}
	        	}
	        	
	        	// reset both the sets as they will be used for the next rows and the next columns
	        	rowSet.clear();
	        	colSet.clear();
	        }
	        
	        return true;
		}
		
		/**
		 * check that the character is numeric, non-zero and the row set should not contain it
		 * @param ch
		 * @param set
		 * @return true if the validation passes otherwise return false
		 */
		private boolean validateRowDigit(char ch) {
			if(!Character.isDigit(ch) || ch == '0' || rowSet.contains(ch)) {
				return false; 
			}
			
			return true;
		}
		
		/**
		 * check that the character is numeric, non-zero and the column set should not contain it
		 * @param ch
		 * @param set
		 * @return true if the validation passes otherwise return false
		 */
		private boolean validateColumnDigit(char ch) {
			if(!Character.isDigit(ch) || ch == '0' || rowSet.contains(ch)) {
				return false; 
			}
			
			return true;
		}
		
		
		/**
		 * check whether the file is empty or not
		 * @param rowCount
		 * @return true if it is empty otherwise false.
		 */
		private boolean isFileEmpty(int rowCount) {
			return rowCount == 0;
		}
		
		/**
		 * verify that the number of rows should be equal to the number of columns
		 * @param rows
		 * @param totalLines
		 * @return true if it is valid otherwise return false.
		 */
		private boolean validateRowsAndColumnsCount(String rows[], int totalLines) {
			// total number of rows should be 9
			if(totalLines != 9) {
				return false;
			}
			
			// each row should contain 9 columns
			for(int i = 0; i < totalLines; i++) {
				if(rows[i].length() != 9) {
					return false;
				}
			}
			
			return true;
		}
	}
}
