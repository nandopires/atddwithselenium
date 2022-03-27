package br.nti.bot.pageobject;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import br.nti.bot.vo.Product;
import lombok.Data;

@Data
public class DrogariaPachecoPageObject {
	
	private static Logger LOG = Logger.getLogger(DrogariaPachecoPageObject.class.getSimpleName());
	
	private String siteUrl;
	
	private String productNameToFilter;
	
	private String searchPath;
	
	private String xpathByDescription;
	
	private Integer timeoutWaitObject;
	
	private WebDriver driver;
	
	public List<Product> getProductsListByName() {
		String searchUrl = String.format("%s%s?q=%s", siteUrl, searchPath, productNameToFilter);
		driver.get(searchUrl);
		
		List<WebElement> items = getProductWebElements();
		return loadProductsList(items);
	}

	private List<Product> loadProductsList(List<WebElement> items) {
		List<Product> productsList = new LinkedList<>();
		
		for (WebElement item : items) {
			loadProduct(productsList, item);
		}
		
		return productsList;
	}

	private void loadProduct(List<Product> productsList, WebElement item) {
		try {
			String itemName = item.findElement(By.className("collection-link")).getText();
			WebElement itemPrice = loadItemPriceElement(productsList, item, itemName);
			String[] itemNameAndPrice = new String[] {itemName, itemPrice.getText()};
			LOG.log(Level.INFO, "Product added -> Name: {0} - Price: {1}", itemNameAndPrice);
		} catch (NoSuchElementException e) {
			LOG.log(Level.SEVERE, "PRODUTO ESGOTADO");
		}
	}

	private WebElement loadItemPriceElement(List<Product> productsList, WebElement item, String itemName) {
		WebElement itemPrice = item.findElement(By.className("valor-por"));
		if (itemPrice.isEnabled()) {
			productsList.add(new Product(itemName, itemPrice.getText()));
		}
		return itemPrice;
	}

	private List<WebElement> getProductWebElements() {
		By byDescPrateleira = By.xpath(xpathByDescription);
		WebElement productCard = new WebDriverWait(driver, timeoutWaitObject)
				.until(ExpectedConditions.elementToBeClickable(byDescPrateleira));
		List<WebElement> items = productCard.findElements(byDescPrateleira);
		return items;
	}

}
