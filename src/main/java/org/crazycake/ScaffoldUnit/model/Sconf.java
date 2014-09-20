package org.crazycake.ScaffoldUnit.model;

import java.util.List;

/**
 * ScaffoldUnit configuration json file which test class matched.
 * @author alexxiyang (https://github.com/alexxiyang)
 *
 */
public class Sconf {
	
	/**
	 * method list
	 */
	private List<SMethod> ms;

	public List<SMethod> getMs() {
		return ms;
	}

	public void setMs(List<SMethod> ms) {
		this.ms = ms;
	}
	
}
