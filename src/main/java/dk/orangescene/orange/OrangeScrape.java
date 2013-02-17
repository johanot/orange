package dk.orangescene.orange;

import com.google.common.base.Function;
import com.thoughtworks.selenium.Selenium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
        orangeScrape.runDownload();
        orangeScrape.runImport();
        orangeScrape.gotoUserAdmin();
        orangeScrape.sendEmails();

        orangeScrape.tearDownDriver();
    }

    private void sendEmails() {
        System.out.println("Trying to send 50 batch e-mails");

        selenium.selectFrame("content");

        driver.findElement(new By.ByName("batch_count")).sendKeys("50");
        driver.findElement(new By.ByXPath("//form[@name='activate']")).submit();

        selenium.waitForFrameToLoad("content", "30000");

        captureScreenshot();
    }

    private WebElement waitForElementFound(final By by) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(30, TimeUnit.SECONDS)
                .pollingEvery(5, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

       return wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(by);
            }
        });
    }

    private void gotoUserAdmin() {
        System.out.println("Going to UserAdmin-module");

        selenium.selectFrame("relative=top");

        driver.findElements(new By.ByLinkText("User Admin")).get(1).click();

        selenium.waitForFrameToLoad("content", "30000");

        captureScreenshot();
    }

    private void runImport() {
        System.out.println("Importerer RFMDB dump");

        Select s = new Select(driver.findElement(new By.ByXPath("//select")));
        s.selectByVisibleText("Importer downloaded dump");

        driver.findElement(new By.ByXPath("//input[@value='Importer data']")).click();

        selenium.waitForFrameToLoad("content", "30000");

        captureScreenshot();
    }

    private void runDownload() {

        System.out.println("Downloader RFMDB dump");

        selenium.selectFrame("content");

        Select s = new Select(driver.findElement(new By.ByXPath("//select")));
        s.selectByVisibleText("Download RFMDB dump");

        selenium.waitForFrameToLoad("content", "30000");

        driver.findElement(new By.ByXPath("//input[@value='Download RFMDB data']")).click();

        selenium.waitForFrameToLoad("content", "30000");

        captureScreenshot();
    }

    private void tearDownDriver() {
        System.out.println("Exitting..");
        driver.quit();
    }

    private void gotoImportAdmin() {
        System.out.println("Going to RFMDB Import-module");
        driver.findElement(new By.ByLinkText("RFMDB Import")).click();
        selenium.waitForFrameToLoad("content", "30000");
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

        waitForElementFound(new By.ById("username")).sendKeys(user);

        driver.findElement(new By.ById("password")).sendKeys(passwd);

        driver.findElement(new By.ByName("loginform")).submit();

        selenium.waitForPageToLoad("30000");

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
