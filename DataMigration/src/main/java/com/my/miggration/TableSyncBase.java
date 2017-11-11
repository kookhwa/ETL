package com.my.miggration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class TableSyncBase {
	private final Logger inforLogger = Logger.getLogger(this.getClass().getName());
	private final BasicDataSource sourceDataSource;
	private final BasicDataSource targetDataSource;
	private final String schemaPattern;	
	private final int commitNum;
	private String sourceTbl;
	private String targetTbl;
	private Table etlTable;
	
	public TableSyncBase(BasicDataSource sourceDataSource, BasicDataSource targetDataSource, DataSyncSessionParameter sessionParameter) {
		super();
		this.sourceDataSource = sourceDataSource;
		this.targetDataSource = targetDataSource;
		this.schemaPattern = sessionParameter.getTargetSchema();
		this.commitNum = sessionParameter.getTargetCommitNum();
		this.init();
	}
	
	public void init(){
		Connection conn = null;
		ResultSet rs = null;
		Connection sourceConn = null;
		try {
			sourceConn = sourceDataSource.getConnection();
			conn = targetDataSource.getConnection();
			DatabaseMetaData targetMetaData = conn.getMetaData();
			DatabaseMetaData sourceMetaData = sourceConn.getMetaData();
			
		    this.buildTableList(sourceConn, targetMetaData, sourceMetaData);
			
		} catch (Exception e) {
			inforLogger.error(e);
		}finally{
			try {
				if(rs != null){ rs.close(); }
				if(conn != null){ conn.close(); }
				if(sourceConn != null){ sourceConn.close(); }
			} catch (SQLException e) {
				inforLogger.error(e);
			}
		}
	}
	
	public Table getTable(){
		return this.etlTable;
	}
	
	
	public void truncateData(){
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = targetDataSource.getConnection();
			conn.setAutoCommit(false);
			String sql = "delete from " + targetTbl;
			inforLogger.info(sql);
			stmt = conn.prepareStatement(sql);
			stmt.execute();			
			conn.commit();
		} catch (Exception e) {
			inforLogger.error(e.getMessage());
		}finally{
			try {
				if(stmt != null){ stmt.close(); }
				if(conn != null){ conn.close(); }
			} catch (SQLException e) {
				inforLogger.error(e.getMessage());
			}
		}
		
	}
	
	protected void buildTableList(Connection sourceConn, DatabaseMetaData targetMetaData, DatabaseMetaData sourceMetaData){
		PreparedStatement pstmt = null;
		ResultSet sourceRs = null;
		ResultSet targetRs = null;
		ResultSet sourceRsPk = null;
		ResultSet targetRsPk = null;
		ResultSet cntRs = null;
		String selSql = "select count(*) from ";
		
		List<HashMap<String,String>> sourcePkList = new ArrayList<HashMap<String,String>>();
		List<HashMap<String,String>> targetPkList = new ArrayList<HashMap<String,String>>();
		
		try {
			sourceRs = sourceMetaData.getTables(null, null, "ORACLE%", null);
			targetRs = targetMetaData.getTables(null, this.schemaPattern, "mysql%", new String[] { "TABLE" });			
			
			while(sourceRs.next()){
				String tblNm = sourceRs.getString(3);
				sourceRsPk = sourceMetaData.getPrimaryKeys(null, null, tblNm);
				
				while(sourceRsPk.next()){
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put(sourceRsPk.getString(4), tblNm);
					sourcePkList.add(hm);
					break;
				}
			}
			
			while(targetRs.next()){
				targetTbl = targetRs.getString(3);		
				targetRsPk = targetMetaData.getPrimaryKeys(null, null, targetTbl);
				while(targetRsPk.next()){
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put(targetRsPk.getString(4), targetTbl);
					targetPkList.add(hm);
					break;
				}
			}
			
			for(HashMap<String, String> targetHash : targetPkList){
				for(HashMap<String, String> sourceHash : sourcePkList){
					for(Map.Entry<String, String> targetEntry : targetHash.entrySet()){
						for(Map.Entry<String, String> sourceEntry : sourceHash.entrySet()){
							if(targetEntry.getKey().equals(sourceEntry.getKey())){
								sourceTbl = sourceEntry.getValue();
								pstmt = sourceConn.prepareStatement(selSql + sourceTbl);
								cntRs = pstmt.executeQuery();
								
							    int count = 0;
								
							    // 소스테이블에 데이터가 존재하면 
								if(cntRs.next()){
									count = cntRs.getInt(1);
									etlTable = new Table(sourceTbl, targetTbl, sourceEntry.getKey(), count, commitNum);
								}
							}
						}
					}
				}
			}
			
		} catch(Exception e){
			inforLogger.error("Oracle database is not exist table : " + sourceTbl, e);
		}finally {
			try {
				if(sourceRs != null){ sourceRs.close(); }
				if(targetRs != null){ targetRs.close(); }
				if(sourceRsPk != null){ sourceRsPk.close(); }
				if(targetRsPk != null){ targetRsPk.close(); }
				if(cntRs != null){ cntRs.close(); }
			} catch (SQLException e) {
				inforLogger.error(sourceTbl + ": " + e.getMessage());
			}
		}
	}
	
	
	
	
	

}
