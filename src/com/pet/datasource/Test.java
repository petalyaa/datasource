package com.pet.datasource;

import java.util.Properties;


import com.pet.datasource.csv.CSVDatasource;
import com.pet.datasource.exception.DatasourceException;
import com.pet.datasource.model.Record;
import com.pet.datasource.xls.XLSDatasource;
import com.pet.datasource.xlsx.XLSXDatasource;

/**
 * Class Description	: 
 * Created By			: Khairul Ikhwan
 * Created Date			: Jan 29, 2014
 * Current Version		: 1.0
 * Latest Changes By	: 
 * Latest Changes Date	: 
 */
public class Test {
	
	
	public static void main(String[] args) {
		//String xlsFile = "c:/users/khairul/desktop/test.xls";
		//testXLS(xlsFile);
		
		// String csvFile = "c:/users/khairul/desktop/test.csv";
		// testCSV(csvFile);
		
		String xlsxFile = "c:/users/khairul/desktop/test.xlsx";
		testXLSX(xlsxFile);
	}
	
	public static void testXLSX(String xlsxFile) {
		Properties extraProps = new Properties();
		extraProps.setProperty(CSVDatasource.HAS_HEADER, String.valueOf(true));
		Datasource csvDatasource = new XLSXDatasource(xlsxFile, extraProps);
		try {
			csvDatasource.connect();
			while(csvDatasource.hasNext()) {
				Record rec = csvDatasource.getNextRecord();
				if(rec != null) {
					for(String columnHeader : rec.keySet()) {
						System.err.println(columnHeader + " => " + rec.get(columnHeader));
					}
				}
			}
		} catch (DatasourceException e) {
			e.printStackTrace();
		} finally {
			try {
				csvDatasource.disconnect();
			} catch (DatasourceException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void testCSV(String csvFile) {
		Properties extraProps = new Properties();
		extraProps.setProperty(CSVDatasource.HAS_HEADER, String.valueOf(true));
		Datasource csvDatasource = new CSVDatasource(csvFile, extraProps);
		try {
			csvDatasource.connect();
			while(csvDatasource.hasNext()) {
				Record rec = csvDatasource.getNextRecord();
				if(rec != null) {
					for(String columnHeader : rec.keySet()) {
						System.err.println(columnHeader + " => " + rec.get(columnHeader));
					}
				}
			}
		} catch (DatasourceException e) {
			e.printStackTrace();
		} finally {
			try {
				csvDatasource.disconnect();
			} catch (DatasourceException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void testXLS(String xlsFile) {
		Properties extraProps = new Properties();
		extraProps.setProperty(XLSDatasource.HAS_HEADER, String.valueOf(true));
		Datasource xlsDatasource = new XLSDatasource(xlsFile, extraProps);
		try {
			xlsDatasource.connect();
			while(xlsDatasource.hasNext()) {
				Record rec = xlsDatasource.getNextRecord();
				if(rec != null) {
					for(String columnHeader : rec.keySet()) {
						System.err.println(columnHeader + " => " + rec.get(columnHeader));
					}
				}
			}
		} catch (DatasourceException e) {
			e.printStackTrace();
		} finally {
			try {
				xlsDatasource.disconnect();
			} catch (DatasourceException e) {
				e.printStackTrace();
			}
		}
	}

}
