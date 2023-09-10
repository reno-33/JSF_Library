package com.booklibrary.members;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class Member {

	
	private int memberID;
	private String name;
	private String address;
	private String phoneNumber;
	private String idCardNumber;
	private String status;
	
	public Member() {
		
	}

	public Member(int memberID, String name, String address, String phoneNumber, String idCardNumber, String status) {
		super();
		this.memberID = memberID;
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.idCardNumber = idCardNumber;
		this.status = status;
	}

	public int getMemberID() {
		return memberID;
	}

	public void setMemberID(int memberID) {
		this.memberID = memberID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(String idCardNumber) {
		this.idCardNumber = idCardNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	
}
