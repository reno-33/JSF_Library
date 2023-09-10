package com.booklibrary.stock;

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


@ManagedBean(name = "stockcontroller")
@SessionScoped
public class StockController implements Serializable{

	private static final long serialVersionUID = 1L;
		private List<Stock> stockList;
		private StockDatabaseUtil stockDatabaseUtil;
		private Logger logger = Logger.getLogger(getClass().getName());

		public StockController() throws Exception {
			stockList = new ArrayList<>();
			stockDatabaseUtil = StockDatabaseUtil.getInstance();
		}

		public List<Stock> getStockList() {
			return stockList;
		}

		public void loadAllStock() {
			stockList.clear();

			try {
				// get all stocks from database
				stockList = stockDatabaseUtil.getAllStock();
			} catch (Exception e) {
				// send this to server logs
				logger.log(Level.SEVERE, "Error loading stocks table", e);

				// add error message for JSF page
				addErrorMessage(e);
			}

		}

		public String addNewItem(Stock newItem) {

			logger.info("Adding new item: " + newItem);

			try {
				// add a new item to the database
				stockDatabaseUtil.addStock(newItem);

			} catch (Exception e) {
				// send this to server logs
				logger.log(Level.SEVERE, "Error adding new items", e);

				// add error message for JSF page
				addErrorMessage(e);

				return null;
			}

			return "list-keszlet?faces-redirect=true";
		}

		public String loadStock(int stockId) {

			logger.info("loading stock: " + stockId);

			try {
				// get a stock item from database
				Stock tempStock = stockDatabaseUtil.getStock(stockId);

				// put in the request attribute ... so we can use it on the form page
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

				Map<String, Object> requestMap = externalContext.getRequestMap();
				requestMap.put("stocks", tempStock);

			} catch (Exception exc) {
				// send this to server logs
				logger.log(Level.SEVERE, "Error loading stock id:" + stockId, exc);

				// add error message for JSF page
				addErrorMessage(exc);

				return null;
			}

			return "update-keszlet-form.xhtml";
		}

		public String updateStock(Stock tempStock) {

			logger.info("updating stock: " + tempStock);

			try {

				// update keszlet in the database
				stockDatabaseUtil.updateStock(tempStock);

			} catch (Exception exc) {
				// send this to server logs
				logger.log(Level.SEVERE, "Error updating stock: " + tempStock, exc);

				// add error message for JSF page
				addErrorMessage(exc);

				return null;
			}

			return "list-stock?faces-redirect=true";
		}

		public String deleteItem(int stockId) {

			logger.info("Deleting stock with stock id: " + stockId);

			try {
				// delete a stock item from the database
				stockDatabaseUtil.deleteStock(stockId);

			} catch (Exception exc) {
				// send this to server logs
				logger.log(Level.SEVERE, "Error deleting stock id: " + stockId, exc);

				// add error message for JSF page
				addErrorMessage(exc);

				return null;
			}

			return "list-stock";
		}

		private void addErrorMessage(Exception exc) {
			FacesMessage message = new FacesMessage("Error: " + exc.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, message);
		}

	}
