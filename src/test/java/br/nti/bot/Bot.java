package br.nti.bot;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import br.nti.bot.pageobject.DrogariaPachecoPageObject;
import br.nti.bot.vo.Product;

public class Bot {

	private static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";
	private static final String CHROME_DRIVER_PATH = "<CHROME_DRIVER_PATH>/chromedriver";
	private WebDriver driver;

	@Before
	public void setUp() {
		System.setProperty(WEBDRIVER_CHROME_DRIVER, CHROME_DRIVER_PATH);

		driver = new ChromeDriver();
	}

	@After
	public void tearDown() {
		driver.quit();
	}

	@Test
	public void drogariaPachegoClipping() throws Exception {
		DrogariaPachecoPageObject drogariaPachegoPageObject = new DrogariaPachecoPageObject();
		drogariaPachegoPageObject.setSiteUrl("https://www.drogariaspacheco.com.br/");
		drogariaPachegoPageObject.setSearchPath("pesquisa");
		drogariaPachegoPageObject.setProductNameToFilter("amoxicilina");
		drogariaPachegoPageObject.setDriver(driver);
		drogariaPachegoPageObject.setXpathByDescription("//div[@class='descricao-prateleira']");
		drogariaPachegoPageObject.setTimeoutWaitObject(10);

		List<Product> productsListByName = drogariaPachegoPageObject.getProductsListByName();

		Assert.assertFalse(productsListByName.isEmpty());
	}

}
