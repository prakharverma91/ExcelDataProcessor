package com.excel.domain;

public class ExchangeRate {

	private String currency;
	
	private double price;
	
	public ExchangeRate(){}

	public ExchangeRate(String currency, double price) {
		super();
		this.currency = currency;
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	

}
