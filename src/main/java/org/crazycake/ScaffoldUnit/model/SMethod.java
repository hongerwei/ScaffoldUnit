package org.crazycake.ScaffoldUnit.model;

import java.util.List;

public class SMethod {
	
	/**
	 * name of method
	 */
	private String n;
	private List<STable> ts;

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public List<STable> getTs() {
		return ts;
	}

	public void setTs(List<STable> ts) {
		this.ts = ts;
	}
	
	
}
