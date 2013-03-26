package edu.osu.currier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuCategory implements Serializable {
	 private long id;
	 private String name;
	 private String descr;
	     
	 private List<MenuItem> itemList = new ArrayList<MenuItem>();
	
	 
	 public MenuCategory(String name, String desc) {
		 this.name = name;
		 this.descr = descr;
	 }
	 
	 public String getName() {
		 return this.name;
	 }
	 
	 public void setName() {
		 this.name = name;
	 }
	 
	 public String getDescr() {
		 return this.descr;
	 }

	public List<MenuItem> getItemList() {
		
		return new ArrayList<MenuItem>(itemList);
	}

	
}
