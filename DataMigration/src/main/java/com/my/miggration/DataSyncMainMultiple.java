package com.my.miggration;

import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class DataSyncMainMultiple extends DataSyncMainBase{
	private static Logger infoLog = Logger.getLogger(DataSyncMainMultiple.class);

	static final String configFileName = "config.properties";
	
	public DataSyncMainMultiple(String basePath){
		this.basePath = basePath;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/conf/log4j.properties");
			String confFilePath =args[0];
			infoLog.info(confFilePath);
			DataSyncMainBase dataSyncMain = new DataSyncMainMultiple(args[0]);
			if (dataSyncMain.setparameters(confFilePath)) {
				// 쓰레드 호출
				dataSyncMain.syncData();
			}
		} catch (Exception e) {
			infoLog.error(e);
			System.exit(1);
		}
	}
	
	protected Table syncTable(BasicDataSource sourceDataSource, BasicDataSource targetDataSource){
		TableSyncBase tableSync = new TableSyncMultiple(sourceDataSource, targetDataSource, sessionParameter);
		Table table = tableSync.getTable();
		tableSync.truncateData();
		return table;
	}

}
