package br.nti.bot.vo;

import lombok.Data;

@Data
public class Product {
	
	private String itemName;
	private String itemPrice;

	public Product(String itemName, String itemPrice) {
		this.itemName = itemName;
		this.itemPrice = itemPrice;
	}

}
