package com.pet.datasource.xls;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import com.pet.datasource.Datasource;
import com.pet.datasource.exception.DatasourceException;
import com.pet.datasource.model.Record;

/**
 * Datasource to read xls type of file by Microsoft.
 * Provided file path and other optional option such 
 * as header. If has header is true, then first row
 * will be treated as header.
 * 
 * @date 2014/01/30
 * @author Khairul
 */
public class XLSDatasource extends Datasource {

	public static final String XLS_FILE = "xls.file";
	
	public static final String HAS_HEADER = "has.header";
	
	private String xlsFile;
	
	private boolean hasHeader;
	
	private String[] header;
	
	private HSSFSheet sheet;
	
	private int currentRow;
	
	private InputStream is;
	
	/**
	 * @param extraProps
	 */
	public XLSDatasource(String xlsFile, Properties extraProps) {
		super(extraProps);
		String hasHeaderStr = extraProps.getProperty(HAS_HEADER);
		if(hasHeaderStr != null && !hasHeaderStr.equals(""))
			hasHeader = Boolean.valueOf(hasHeaderStr);
		this.xlsFile = xlsFile;
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#hasNext()
	 */
	@Override
	public boolean hasNext() throws DatasourceException {
		if(record == null)
			record = new Record();
		record.clear();
		boolean hasNext = false;
		HSSFRow row = sheet.getRow(currentRow);
    	if(row != null) {
    		int colSize = row.getPhysicalNumberOfCells();
    		if(header == null) {
		    	header = new String[colSize];
		    	for(int i = 0; i < colSize; i++) {
		    		header[i] = "Column " + i;
		    	}
		    }
    		for(int i = 0; i < colSize; i++) {
    			HSSFCell cell = row.getCell(i);
    			if(cell != null) {
    				String headerName = header[i];
    				String cellValue = cellToString(cell);
    				record.put(headerName, cellValue);
    			}
    		}
    		hasNext = true;
    	}
		currentRow++;
		return hasNext;
	}
	
	private String cellToString(HSSFCell cell) {
		int type;
		String result = null;
		type = cell.getCellType();
		switch (type) {
		case Cell.CELL_TYPE_NUMERIC: // numeric value in Excel
		case Cell.CELL_TYPE_FORMULA: // precomputed value based on formula
			double value = cell.getNumericCellValue();
			result = String.valueOf(value);
			if(result.endsWith(".0"))
				result = result.substring(0, result.length()-2);
			break;
		case Cell.CELL_TYPE_STRING: // String Value in Excel
			result = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BLANK:
			result = "";
			break;
		case Cell.CELL_TYPE_BOOLEAN: // boolean value
			result = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_ERROR:
		default:
			throw new RuntimeException("There is no support for this type of cell");
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#executeQuery(java.lang.String)
	 */
	@Override
	public void executeQuery(String query) throws DatasourceException {
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#connect()
	 */
	@Override
	public void connect() throws DatasourceException {
		if(xlsFile == null || xlsFile.equals(""))
			xlsFile = extraProps.getProperty(XLS_FILE);
		try {
			is = new FileInputStream(xlsFile);
			POIFSFileSystem fs = new POIFSFileSystem(is);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
		    sheet = wb.getSheetAt(0);
		    if(hasHeader) {
		    	HSSFRow row = sheet.getRow(currentRow);
		    	if(row != null) {
		    		int colSize = row.getPhysicalNumberOfCells();
		    		header = new String[colSize];
		    		for(int i = 0; i < colSize; i++) {
		    			HSSFCell cell = row.getCell(i);
		    			if(cell != null) {
		    				header[i] = cell.getStringCellValue(); 
		    			}
		    		}
		    	}
		    	currentRow++;
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#disconnect()
	 */
	@Override
	public void disconnect() throws DatasourceException {
		try {
			if(is != null)
				is.close();
		} catch (IOException e) {
			throw new DatasourceException("Failed to disconnect from datasource : " + xlsFile, e);
		}
	}

}
