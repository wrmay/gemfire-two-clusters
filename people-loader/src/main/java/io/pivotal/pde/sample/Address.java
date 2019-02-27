package io.pivotal.pde.sample;

import com.github.javafaker.Faker;

public class Address  {

	private String street;
	private String city;
	private String state;
	private String zip;
	
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

	private static Faker faker = new Faker();
	
	public void fake(){
		synchronized(faker){
			com.github.javafaker.Address fakeAddr = faker.address();
			this.setStreet(fakeAddr.streetAddress());
			this.setCity(fakeAddr.city());
			this.setState(fakeAddr.stateAbbr());
			this.setZip(fakeAddr.zipCode());		
		}
	}
	
	@Override
	public String toString() {
		return "Address [street=" + street + ", city=" + city + ", state=" + state + ", zip=" + zip + "]";
	}
	
}
