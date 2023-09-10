package com.booklibrary.members;

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

@ManagedBean(name = "membercontroller")
@SessionScoped
public class MemberController implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Member> memberList;
	private MemberDatabaseUtil memberDatabaseUtil;
	private Logger logger = Logger.getLogger(getClass().getName());
	private String searchName;

	public MemberController() throws Exception {
		memberList = new ArrayList<>();
		memberDatabaseUtil = MemberDatabaseUtil.getInstance();
	}

	public List<Member> getMembers() {
		return memberList;
	}

	public void loadMembers() {
		try {
			memberList = memberDatabaseUtil.getMembers();
		} catch (Exception e) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error loading tag table", e);

			// add error message for JSF page
			addErrorMessage(e);
		}
	}

	public String addNewMember(Member newMember) {

		logger.info("Adding new member: " + newMember);

		try {

			// add tag to the database
			memberDatabaseUtil.addMember(newMember);

		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error adding member", exc);

			// add error message for JSF page
			addErrorMessage(exc);

			return null;
		}

		return "add-member-form?faces-redirect=true";
	}

	public String loadMember(int memberId) {

		logger.info("loading tag: " + memberId);

		try {
			// get tag from database
			Member tempMember = memberDatabaseUtil.getMember(memberId);

			// put in the request attribute ... so we can use it on the form page
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.put("member", tempMember);

		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error loading member id:" + memberId, exc);

			// add error message for JSF page
			addErrorMessage(exc);

			return null;
		}

		return "update-member-form.xhtml";
	}

	public String updateMember(Member member) {

		logger.info("updating member: " + member);

		try {

			// update tag in the database
			memberDatabaseUtil.updateMember(member);

		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error updating member: " + member, exc);

			// add error message for JSF page
			addErrorMessage(exc);

			return null;
		}

		return "list-member?faces-redirect=true";
	}

	public String deleteMember(int memberId) {

		logger.info("Deleting tag id: " + memberId);

		try {

			// delete the tag from the database
			memberDatabaseUtil.deleteMember(memberId);

		} catch (Exception exc) {
			// send this to server logs
			logger.log(Level.SEVERE, "Error deleting member with id: " + memberId, exc);

			// add error message for JSF page
			addErrorMessage(exc);

			return null;
		}

		return "list-member";
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	private void addErrorMessage(Exception exc) {
		FacesMessage message = new FacesMessage("Error: " + exc.getMessage());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

}
