package org.crazycake.ScaffoldUnit.model;

import java.util.List;

public class STable {
	private String t;
	private List<List<SCol>> rs;
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public List<List<SCol>> getRs() {
		return rs;
	}
	public void setRs(List<List<SCol>> rs) {
		this.rs = rs;
	}
	
	
}
