package edu.osu.currier;

import java.io.Serializable;

public class MenuItem implements Serializable {
	 private long id;
	 private int imgId;
	 private String name;
	 private String descr;
	 
	public MenuItem(String name, String descr) {
		this.name = name;
		this.descr = descr;
	}
	 
	public String getName() {
		
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return this.descr;
	}
	

	
}
