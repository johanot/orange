package dk.orangescene.orange;

import com.thoughtworks.selenium.Selenium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * User: jot
 * Date: 6/16/12
 * Time: 6:37 PM
 */
public class OrangeScrape {

    private WebDriver driver;
    private Selenium selenium;


    public static void main(String[] args) {

        String username = System.getProperty("orange.username");
        String password = System.getProperty("orange.password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            System.err.println("Required properties: orange.username, orange.password");
            System.exit(1);
        }

        OrangeScrape orangeScrape = new OrangeScrape();
        orangeScrape.initDriver();
        orangeScrape.login(username, password);
		//orangeScrape.saveOverview(args[2]);
        //orangeScrape.downloadFile(args[2]);
    }


    private void initDriver() {

        System.out.println();
        System.out.println("Opretter Firefox driver..");

        driver = new FirefoxDriver();
        selenium = new WebDriverBackedSelenium(driver, "http://www.orange-scene.dk/typo3");
    }


    private void login(String user, String passwd) {

        String loginLink = "http://www.orange-scene.dk/typo3";

        System.out.println("Går til: " + loginLink);
        driver.navigate().to(loginLink);
        selenium.waitForPageToLoad("5000");

        driver.findElement(new By.ById("username")).sendKeys(user);

        driver.findElement(new By.ById("password")).sendKeys(passwd);

        driver.findElement(new By.ByName("loginform")).submit();
        selenium.waitForPageToLoad("2000");

        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        // Now you can do whatever you need to do with it, for example copy somewhere
        try {
            System.out.println("Tager screenshot1.png");
            FileUtils.copyFile(scrFile, new File("screenshot1.png"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        driver.quit();
    }

    private void saveOverview(String event) {

        String eventOverviewLink = "http://www.billetto.dk/da/events/" + event + "/overview";
        driver.navigate().to(eventOverviewLink);

        try {
            PrintStream ps = new PrintStream(event + ".html");
            ps.print(selenium.getHtmlSource());
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void downloadFile(String event) {
        String eventListDownloadLink = "http://www.billetto.dk/da/events/" + event + "/list.csv?listtype=guestlist";
        driver.navigate().to(eventListDownloadLink);
		
		try
		{
			Thread.sleep(10000);

		driver.close();
        driver.quit();

        } catch (InterruptedException e) {   }
    }

}
