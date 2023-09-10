package com.booklibrary.borrows;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


@ManagedBean(name="borrowcontroller")
@SessionScoped
public class BorrowController {
	
	private List<Borrow> borrowList;
	private BorrowDatabaseUtil borrowDatabaseUtil;
	private Logger logger = Logger.getLogger(getClass().getName());
	
	public BorrowController() throws Exception {
		borrowList = new ArrayList<>();
		borrowDatabaseUtil = BorrowDatabaseUtil.getInstance();
	}
	
	public List<Borrow> getBorrows() {
		return borrowList;
	}
	
	public void loadBorrows() {
		try {
			borrowList = borrowDatabaseUtil.getBorrows();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error loading table", e);
			addErrorMessage(e);
		}
	}
	
	public String addBorrow(Borrow borrow) {
		logger.info("Adding new borrow: " + borrow);

		try {
			// we need to check if the member has an 'active' status, and less than 6 active borrows
			boolean status = borrowDatabaseUtil.isActive(borrow.getMemberId());
			if (status) {
				int count = borrowDatabaseUtil.getCount(borrow.getMemberId());
				if (count > 6) {
					logger.log(Level.SEVERE, "You have too many active borrows");
				} else {
					borrowDatabaseUtil.addBorrow(borrow);

				}
			} else
				logger.log(Level.SEVERE, "The member is inactive!");

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error adding the new borrow!", e);
			addErrorMessage(e);

			return null;
		}

		return "list-borrows?faces-redirect=true";
	}
	
	public String itemReturned(int borrowId) {
		try {
			borrowDatabaseUtil.itemReturned(borrowId);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error", e);
			addErrorMessage(e);

			return null;
		}

		return "list-borrows?faces-redirect=true";
	}
	
	
	/**
	 * Loading a borrow to ExternalContext when updating a borrow
	 * @param borrowId
	 * @return
	 */
	public String loadBorrow(int borrowId) {
		logger.info("loading borrow: " + borrowId);

		try {
			Borrow tempBorrow = borrowDatabaseUtil.getBorrow(borrowId);

			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.put("borrow", tempBorrow);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error laoding borrow ID: " + borrowId, e);
			addErrorMessage(e);
			return null;
		}

		return "update-borrow-form.xhtml";
	}
	
	
	
	private void addErrorMessage(Exception exc) {
		FacesMessage message = new FacesMessage("Error: " + exc.getMessage());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	

}
