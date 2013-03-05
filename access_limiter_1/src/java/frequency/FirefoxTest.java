package frequency;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FirefoxTest {

	private static final Logger log = Logger.getLogger(FirefoxTest.class);

	private WebDriver driver = new FirefoxDriver();

	private int count = 10000;

	private int intervalMs = 5000;

	public void work() {
		driver.get("http://www.google.com.hk");
		WebElement element1 = driver.findElement(By.name("q"));
		element1.sendKeys("Cheese!");
		element1.submit();
		log.info(driver.getTitle());
		for (int i = 0; i < count; i++) {
			WebElement element = driver.findElement(By.name("q"));
			element.clear();
			element.sendKeys("Cheese!" + i);
			element.submit();
			new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					// return d.getTitle().toLowerCase().startsWith("cheese!");
					if(d.getTitle().contains("Chesser!")){
						return true;
					}else{
						return false;
					}
				}
			});
		}
	}

	public static void main(String[] args) {
		FirefoxTest test = new FirefoxTest();
		test.work();
	}

	// public static void main(String[] args) {
	// // Create a new instance of the Firefox driver
	// // Notice that the remainder of the code relies on the interface,
	// // not the implementation.
	// WebDriver driver = new FirefoxDriver();
	//
	// // And now use this to visit Google
	// driver.get("http://www.google.com");
	// // Alternatively the same thing can be done like this
	// // driver.navigate().to("http://www.google.com");
	//
	// // Find the text input element by its name
	// WebElement element = driver.findElement(By.name("q"));
	// // WebElement element = driver.findElement(By.id("kw"));
	//
	// // Enter something to search for
	// element.sendKeys("Cheese!");
	//
	// // Now submit the form. WebDriver will find the form for us from the
	// element
	// element.submit();
	//
	// // Check the title of the page
	// System.out.println("Page title is: " + driver.getTitle());
	//
	// // Google's search is rendered dynamically with JavaScript.
	// // Wait for the page to load, timeout after 10 seconds
	// (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
	// public Boolean apply(WebDriver d) {
	// // return d.getTitle().toLowerCase().startsWith("cheese!");
	// return true;
	// }
	// });
	//
	// // Should see: "cheese! - Google Search"
	// System.out.println("Page title is: " + driver.getTitle());
	//
	// //Close the browser
	// driver.quit();
	// }
}