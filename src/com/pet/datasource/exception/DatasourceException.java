package com.pet.datasource.exception;

/**
 * Class Description	: 
 * Created By			: Khairul Ikhwan
 * Created Date			: Jan 29, 2014
 * Current Version		: 1.0
 * Latest Changes By	: 
 * Latest Changes Date	: 
 */
public class DatasourceException extends Exception {

	private static final long serialVersionUID = 2005140251905173572L;
	
	public DatasourceException() {
		super();
	}
	
	public DatasourceException(String msg) {
		super(msg);
	}
	
	public DatasourceException(String msg, Throwable tw) {
		super(msg, tw);
	}
	
}
