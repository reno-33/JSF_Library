package com.booklibrary.borrows;

import java.sql.Date;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class Borrow {
	
	private int borrowId;
	private int memberId;
	private int stockId;
	private Date dateOfBorrow;
	private Date dueDate;
	private Date dateOfReturn;
	
	
	public Borrow() {
		
	}


	public Borrow(int borrowId, int memberId, int stockId, Date dateOfBorrow, Date dueDate, Date dateOfReturn) {
		super();
		this.borrowId = borrowId;
		this.memberId = memberId;
		this.stockId = stockId;
		this.dateOfBorrow = dateOfBorrow;
		this.dueDate = dueDate;
		this.dateOfReturn = dateOfReturn;
	}


	public int getBorrowId() {
		return borrowId;
	}


	public void setBorrowId(int borrowId) {
		this.borrowId = borrowId;
	}


	public int getMemberId() {
		return memberId;
	}


	public void setMemberID(int memberId) {
		this.memberId = memberId;
	}


	public int getStockId() {
		return stockId;
	}


	public void setStockId(int stockId) {
		this.stockId = stockId;
	}


	public Date getDateOfBorrow() {
		return dateOfBorrow;
	}


	public void setDateOfBorrow(Date dateOfBorrow) {
		this.dateOfBorrow = dateOfBorrow;
	}


	public Date getDueDate() {
		return dueDate;
	}


	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}


	public Date getDateOfReturn() {
		return dateOfReturn;
	}


	public void setDateOfReturn(Date dateOfReturn) {
		this.dateOfReturn = dateOfReturn;
	}
	
	
}
