package org.pet.datasource.xlsx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.pet.datasource.Datasource;
import com.pet.datasource.exception.DatasourceException;
import com.pet.datasource.model.Record;

/**
 * Datasource to read xlsx type of file by Microsoft.
 * Provided file path and other optional option such 
 * as header. If has header is true, then first row
 * will be treated as header.
 * 
 * @date 2014/01/30
 * @author Khairul
 */
public class XLSXDatasource extends Datasource {
	
	public static final String XLSX_FILE = "xlsx.file";
		
	public static final String HAS_HEADER = "has.header";
	
	private String xlsxFile;
	
	private boolean hasHeader;
	
	private Sheet sheet;
	
	private String[] header;
	
	private int currentRow;
	
	private InputStream in;
	
	/**
	 * @param extraProps
	 */
	public XLSXDatasource(String xlsxFile, Properties extraProps) {
		super(extraProps);
		this.xlsxFile = xlsxFile;
		String containHeaderStr = extraProps.getProperty(HAS_HEADER);
		if(containHeaderStr != null && !containHeaderStr.equals("")) 
			hasHeader = Boolean.valueOf(containHeaderStr);
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
		Row row = sheet.getRow(currentRow);
    	if(row != null) {
    		int colSize = row.getPhysicalNumberOfCells();
    		if(header == null) {
		    	header = new String[colSize];
		    	for(int i = 0; i < colSize; i++) {
		    		header[i] = "Column " + i;
		    	}
		    }
    		for(int i = 0; i < colSize; i++) {
    			Cell cell = row.getCell(i);
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
	
	private String cellToString(Cell cell) {
		int type;
		String result = null;
		type = cell.getCellType();
		switch (type) {
		case Cell.CELL_TYPE_NUMERIC: // numeric value in Excel
		case Cell.CELL_TYPE_FORMULA: // precomputed value based on formula
			double value = cell.getNumericCellValue();
			result = String.valueOf(value);
			if (result.endsWith(".0"))
				result = result.substring(0, result.length() - 2);
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
			throw new RuntimeException(
					"There is no support for this type of cell");
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
		if(xlsxFile == null || xlsxFile.equals(""))
			xlsxFile = extraProps.getProperty(XLSX_FILE);
		try {
			in = new FileInputStream(xlsxFile);
			Workbook wb = WorkbookFactory.create(in);
			sheet = wb.getSheetAt(0);
		    if(hasHeader) {
		    	Row row = sheet.getRow(currentRow);
		    	if(row != null) {
		    		int colSize = row.getPhysicalNumberOfCells();
		    		header = new String[colSize];
		    		for(int i = 0; i < colSize; i++) {
		    			Cell cell = row.getCell(i);
		    			if(cell != null) {
		    				header[i] = cell.getStringCellValue(); 
		    			}
		    		}
		    	}
		    	currentRow++;
		    }
		} catch (FileNotFoundException e) {
			throw new DatasourceException("Failed to connect to the datasource : " + xlsxFile, e);
		} catch (IOException e) {
			throw new DatasourceException("Failed to connect to the datasource : " + xlsxFile, e);
		} catch (InvalidFormatException e) {
			throw new DatasourceException("Failed to connect to the datasource : " + xlsxFile, e);
		}
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#disconnect()
	 */
	@Override
	public void disconnect() throws DatasourceException {
		try {
			if(in != null)
				in.close();
		} catch (IOException e) {
			throw new DatasourceException("Failed to disconnect from datasource : " + xlsxFile, e);
		}
	}

}
