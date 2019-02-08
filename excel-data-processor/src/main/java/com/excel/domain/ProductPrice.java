package com.excel.domain;

public class ProductPrice {

	String pid;
	
	String product_name;
	
	double price;
	
	String currencies;

	public ProductPrice(){}
	
	public ProductPrice(String pid, String product_name, double price, String currencies) {
		super();
		this.pid = pid;
		this.product_name = product_name;
		this.price = price;
		this.currencies = currencies;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCurrencies() {
		return currencies;
	}

	public void setCurrencies(String currencies) {
		this.currencies = currencies;
	}
	
	
}
