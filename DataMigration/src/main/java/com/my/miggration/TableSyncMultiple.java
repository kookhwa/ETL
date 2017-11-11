package com.my.miggration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.apache.commons.dbcp.BasicDataSource;

public class TableSyncMultiple extends TableSyncBase{
	
	public TableSyncMultiple(BasicDataSource sourceDataSource, BasicDataSource targetDataSource, DataSyncSessionParameter sessionParameter) {
		super(sourceDataSource, targetDataSource, sessionParameter);
	}

	protected void buildTableList(Connection sourceConn, DatabaseMetaData targetMetaData, DatabaseMetaData sourceMetaData){
		super.buildTableList(sourceConn, targetMetaData, sourceMetaData);
	}

}
