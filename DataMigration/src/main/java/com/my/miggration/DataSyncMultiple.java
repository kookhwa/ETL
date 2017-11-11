package com.my.miggration;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class DataSyncMultiple extends DataSyncBase implements Runnable{
	private final Logger inforLogger = Logger.getLogger(this.getClass().getName());
	private final int threadIndex;
	private final int selectCount;

	public DataSyncMultiple(BasicDataSource sourceDataSource, BasicDataSource targetDataSource, DataSyncSessionParameter sessionParameter, Table table, CountDownLatch beginCountDownLatch, CountDownLatch endCountDownLatch ) {
		super(sourceDataSource, targetDataSource, sessionParameter, table, beginCountDownLatch, endCountDownLatch);
		this.threadIndex = table.getCurrentNum();
		this.selectCount = table.getSelectCount();
	}
	
	
	protected synchronized String buildSelectSql(){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ")
		  .append(this.table.getSourceName())
		  .append(" where ORA_HASH(")
		  .append(this.table.getHashKey())
		  .append(",")
		  .append(selectCount)
		  .append(")=?");
		return sb.toString();
	}
	
	protected synchronized String buildLogSelectSql(){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ")
		  .append(this.table.getSourceName())
		  .append(" where ORA_HASH(")
		  .append(this.table.getHashKey())
		  .append(",")
		  .append(selectCount)
		  .append(")=")
		  .append(this.threadIndex);
		return sb.toString();
	}
	
	protected synchronized String buildInsertSql(List<String> colomnNameList){
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ")
		  .append(this.table.getTargetName())
		  .append("(")
		  .append(StringUtils.join(colomnNameList, ","))
		  .append(")")
		  .append(" VALUES(");
		for(String columnName : colomnNameList){
			sb.append("?,");
		}
		sb.replace(sb.lastIndexOf(","), sb.length(), ")");
		return sb.toString();
	}
	
	protected synchronized void setThreadPrameter(PreparedStatement pstmt)throws Exception{
		pstmt.setInt(1, threadIndex);
	}
	
	protected synchronized int getThreadIndex(){
		return this.threadIndex;
	}

}
