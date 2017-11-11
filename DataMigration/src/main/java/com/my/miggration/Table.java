package com.my.miggration;

public class Table {
	
	private String sourceName;
	private String targetName;
	private String hashKey;
	private int selectCount;	
	private int currentNum;
	
	public Table(String sourceName, String targetName, String hashKey, int count, int commitNum) {
		super();
		this.sourceName = sourceName;
		this.targetName = targetName;
		this.hashKey = hashKey;
		this.selectCount = count / commitNum;
		this.currentNum = this.selectCount;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String name) {
		this.sourceName = name;
	}
	
	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String name) {
		this.targetName = name;
	}
	public String getHashKey() {
		return hashKey;
	}

	public void setHashKey(String hashKey) {
		this.hashKey = hashKey;
	}
	
	
	
	public int getSelectCount() {
		return selectCount;
	}

	public void setSelectCount(int selectCount) {
		this.selectCount = selectCount;
	}

	public int getCurrentNum(){
		return this.currentNum--;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceName == null) ? 0 : sourceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Table other = (Table) obj;
		if (sourceName == null) {
			if (other.sourceName != null)
				return false;
		} else if (!sourceName.equals(other.sourceName))
			return false;
		return true;
	}
	
	

}
