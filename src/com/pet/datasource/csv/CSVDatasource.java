package com.pet.datasource.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import au.com.bytecode.opencsv.CSVReader;

import com.pet.datasource.Datasource;
import com.pet.datasource.exception.DatasourceException;
import com.pet.datasource.model.Record;

/**
 * Datasource to read CSV file type.
 * Provided file path and other optional option such 
 * as header. If has header is true, then first row
 * will be treated as header.
 * 
 * @date 2014/01/30
 * @author Khairul
 */
public class CSVDatasource extends Datasource {
	
	public static final String CSV_FILE = "csv.file";
	
	public static final String HAS_HEADER = "has.header";
	
	private String[] header;
	
	private boolean isContainHeader;
	
	private CSVReader reader;
	
	private String csvFile;
	
	private Record record;
	
	/**
	 * @param extraProps
	 */
	public CSVDatasource(String csvFile, Properties extraProps) {
		super(extraProps);
		this.csvFile = csvFile;
		String containHeaderStr = extraProps.getProperty(HAS_HEADER);
		if(containHeaderStr != null && !containHeaderStr.equals("")) 
			isContainHeader = Boolean.valueOf(containHeaderStr);
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#getNextRecord()
	 */
	@Override
	public Record getNextRecord() {
		return record;
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#hasNext()
	 */
	@Override
	public boolean hasNext() throws DatasourceException {
		boolean hasNext = false;
		if(record == null)
			record = new Record();
		record.clear();
	    try {
	    	String [] nextLine = null;
			if ((nextLine = reader.readNext()) != null) {
			    if(header == null) {
			    	header = new String[nextLine.length];
			    	for(int i = 0; i < nextLine.length; i++) {
			    		header[i] = "Column " + i;
			    	}
			    }
				for(int i = 0; i < nextLine.length; i++) {
					String columnHeader = header[i];
					String columnValue = nextLine[i].trim();
					record.put(columnHeader, columnValue);
			    }
				hasNext = true;
			}
		} catch (Exception e) {
			throw new DatasourceException("Failed to get next record.", e);
		}
		return hasNext;
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#connect()
	 */
	@Override
	public void connect() throws DatasourceException {
		try {
			if(csvFile == null || csvFile.equals(""))
				csvFile = extraProps.getProperty(CSV_FILE);
			reader = new CSVReader(new FileReader(csvFile));
			setConnected(true);
			if(isContainHeader) { // Fetch header from csv...
				String [] nextLine = null;
				if ((nextLine = reader.readNext()) != null) {
					header = new String[nextLine.length];
				    for(int i = 0; i < nextLine.length; i++) {
				    	header[i] = nextLine[i].trim();
				    }
				}
			}
		} catch (FileNotFoundException e) {
			throw new DatasourceException("Fail to connect to your csv file : " + csvFile, e);
		} catch (IOException e) {
			throw new DatasourceException("Fail to fetch header from csv file : " + csvFile, e);
		}
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#disconnect()
	 */
	@Override
	public void disconnect() throws DatasourceException {
		try {
			if(!isConnected())
				throw new IllegalAccessException("Invalid handler to disconnect");
			if(reader != null)
				reader.close();
		} catch (IOException e) {
			throw new DatasourceException("Fail to disconnect from csv datasource", e);
		} catch (IllegalAccessException e) {
			throw new DatasourceException("Fail to disconnect from csv datasource", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.pet.datasource.Datasource#executeQuery(java.lang.String)
	 */
	@Override
	public void executeQuery(String query) throws DatasourceException {
		throw new DatasourceException("Method not implement yet");
	}

}
