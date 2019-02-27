package io.pivotal.pde.sample;

import java.util.ArrayList;
import java.util.Random;

import com.github.javafaker.Faker;

public class Person {


	private String lastName;
	private String firstName;
	private String phone;
	private Address address;
	private String gender;
	private int id;
	private int age;
	
	private ArrayList<Address> pastAddresses = new ArrayList<>(4);
	
	public String getPartitionByZipKey(){
		return String.format("%s|%08d",address.getZip(),id);
	}
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}

	public ArrayList<Address> getPastAddresses() {
		return pastAddresses;
	}
	
	public void addPastAddress(Address addr){
		this.pastAddresses.add(addr);
	}
	
	@Override
	public String toString() {
		return "Person [lastName=" + lastName + ", firstName=" + firstName + ", phone=" + phone + ", address=" + address
				+ ", gender=" + gender + ", id=" + id + ", age=" + age + "]";
	}

	// this method should only be used during deserialization
	protected void setPastAddresses(ArrayList<Address> a){
		pastAddresses = a;
	}
	

	private static Faker faker = new Faker();
	private static Random rand = new Random();

	protected Address newFakeAddress(){
		Address result = new Address();
		result.fake();
		return result;
	}
	
	public void fake(){
		synchronized(Person.faker){
			this.setLastName(faker.name().lastName());
			this.setFirstName(faker.name().firstName());
			this.setPhone(faker.phoneNumber().phoneNumber());
			
			if (rand.nextBoolean())
				this.setGender("F");
			else
				this.setGender("M");
			
			this.setAge(rand.nextInt(100));
			
			this.setAddress(newFakeAddress());
			int addrCount = rand.nextInt(5);
			for(int i=0;i < addrCount; i++){
				this.addPastAddress(newFakeAddress());
			}
		}
	}
	

}
