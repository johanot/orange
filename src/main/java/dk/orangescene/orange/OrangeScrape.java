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

    private int screenshotNumber = 1;


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
        orangeScrape.gotoImportAdmin();
        //orangeScrape.runDownload();
        //orangeScrape.runImport();
    }

    private void gotoImportAdmin() {
        System.out.println("Going to RFMDB Import-module");
        driver.findElement(new By.ByLinkText("RFMDB Import")).click();
        captureScreenshot();
    }


    private void initDriver() {

        System.out.println();
        System.out.println("Creating Firefox driver..");

        driver = new FirefoxDriver();
        selenium = new WebDriverBackedSelenium(driver, "http://www.orange-scene.dk/typo3");
    }


    private void login(String user, String passwd) {

        String loginLink = "http://www.orange-scene.dk/typo3";

        System.out.println("Opening: " + loginLink);
        driver.navigate().to(loginLink);
        selenium.waitForPageToLoad("5000");

        driver.findElement(new By.ById("username")).sendKeys(user);

        driver.findElement(new By.ById("password")).sendKeys(passwd);

        driver.findElement(new By.ByName("loginform")).submit();
        selenium.waitForPageToLoad("2000");

        captureScreenshot();
    }

    private void captureScreenshot() {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

        String filename = "screenshot" + (++screenshotNumber) + ".png";

        try {
            System.out.println("Capturing " + filename);
            FileUtils.copyFile(scrFile, new File(filename));
        } catch (IOException e) {
            System.err.println("Could not capture screenshot #" + screenshotNumber);
        }
    }

}
