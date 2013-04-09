package edu.osu.currier;

public class Seller implements Comparable<Seller>{
	private String name;
	private String address;
	private double distance;

	public Seller(String n) {
		this.name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAddress(String a) {
		this.address = a;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setDistance(double d) {
		this.distance = d;
	}
	
	public double getDistance() {
		return distance;
	}

	@Override
	public int compareTo(Seller otherSeller) {
		return (int)(this.getDistance() - otherSeller.getDistance());
	}

}
