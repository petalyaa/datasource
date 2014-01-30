package com.pet.datasource;

import java.util.Properties;

import com.pet.datasource.exception.DatasourceException;
import com.pet.datasource.model.Record;

/**
 * Datasource to connect to. This abstract class is intended
 * to be implement from any type of datasource you want to create.
 * 
 * @date 2014/01/30
 * @author Khairul
 */
public abstract class Datasource {
	
	protected Properties extraProps;
	
	protected boolean connected;
	
	protected Record record;
	
	public Datasource(Properties extraProps) {
		this.extraProps = extraProps;
	}
	
	protected void setConnected(boolean isConnected) {
		this.connected = isConnected;
	}
	
	public boolean isConnected() {
		return this.connected;
	}
	
	public Record getNextRecord() {
		return record;
	}
	
	public abstract boolean hasNext() throws DatasourceException;
	
	public abstract void executeQuery(String query) throws DatasourceException;
	
	public abstract void connect() throws DatasourceException;
	
	public abstract void disconnect() throws DatasourceException;

}
