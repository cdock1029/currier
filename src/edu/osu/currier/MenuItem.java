package edu.osu.currier;

import org.joda.money.Money;

public class MenuItem {
	private String name;
	private String description;
	private Money ammount;
	
	public MenuItem(String n, String d, Money a) {
		name = n;
		description = d;
		ammount = a;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Money getAmmount() {
		return Money.parse(ammount.toString());
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void setDescription(String n) {
		description = n;
	}
	
	public void setAmmount(String a) {
		ammount = Money.parse("USD " + a);
	}
	
}
