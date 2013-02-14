package dk.dds.orange;

import com.thoughtworks.selenium.Selenium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
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

        if (args.length != 2) {
            System.err.println("Syntaks: OrangeScrape user password");
            System.exit(1);
        }

        OrangeScrape orangeScrape = new OrangeScrape();
        orangeScrape.initDriver();
        orangeScrape.login(args[0], args[1]);
		//orangeScrape.saveOverview(args[2]);
        //orangeScrape.downloadFile(args[2]);

        System.exit(0);
    }


    private void initDriver() {
        driver = new FirefoxDriver();
        selenium = new WebDriverBackedSelenium(driver, "http://www.orange-scene.dk/typo3");
    }


    private void login(String user, String passwd) {

        driver.navigate().to("http://www.orange-scene.dk/typo3");
        selenium.waitForPageToLoad("5000");

        driver.findElement(new By.ById("username")).sendKeys(user);

        driver.findElement(new By.ById("password")).sendKeys(passwd);

        driver.findElement(new By.ByName("loginform")).submit();
        selenium.waitForPageToLoad("2000");

        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        // Now you can do whatever you need to do with it, for example copy somewhere
        try {
            FileUtils.copyFile(scrFile, new File("screenshot.png"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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
