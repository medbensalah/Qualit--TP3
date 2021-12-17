import java.io.File;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TodoTest {
    private static int scrNum;
    WebDriver driver;
    JavascriptExecutor js;

    @BeforeAll
    public static void initialize() {
        WebDriverManager.chromedriver().setup();
        scrNum = 0;
    }

    @BeforeEach
    public void prepareDriver() {

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

        js = (JavascriptExecutor) driver;

        scrNum++;
    }

    @Test
    public void testCase() throws Exception {
        driver.get("https://todomvc.com");
        chooseTechnology("Backbone.js");
        addTodo("Meet a friend");
        addTodo("Buy meat");
        addTodo("Clean the car");
        removeTodo(ThreadLocalRandom.current().nextInt(1, 3));
        removeTodo(ThreadLocalRandom.current().nextInt(1, 3));
        Thread.sleep(1000);
        assertLeft(1);

        screenShot(driver, "Screenshots\\screenshot_"+scrNum+".png");
    }

    public static void screenShot(WebDriver webdriver, String path) throws Exception {
        TakesScreenshot scr = ((TakesScreenshot) webdriver);
        File scrFile = scr.getScreenshotAs(OutputType.FILE);
        File outFile = new File(path);
        FileUtils.copyFile(scrFile, outFile);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Backbone.js",
            "AngularJS",
            "Mithril",
            "KnockoutJS",
            "Vue.js"
    })
    public void todosTestCase(String tech) throws Exception {
        driver.get("https://todomvc.com");
        chooseTechnology(tech);
        addTodo("Meet a friend");
        addTodo("Buy meat");
        addTodo("Clean the car");
        removeTodo(ThreadLocalRandom.current().nextInt(1, 3));
        removeTodo(ThreadLocalRandom.current().nextInt(1, 3));
        Thread.sleep(1000);
        assertLeft(1);

        screenShot(driver, "Screenshots\\screenshot_"+tech+"_"+scrNum+".png");
    }

    private void chooseTechnology(String tech) {
        WebElement element = driver.findElement(By.linkText(tech));
        element.click();
    }

    private void addTodo(String todo) throws InterruptedException {
        WebElement element = driver.findElement(By.className("new-todo"));
        element.sendKeys(todo);
        element.sendKeys(Keys.RETURN);
        Thread.sleep(1000);
    }

    private void removeTodo(int number) throws InterruptedException {
        WebElement element = driver.findElement(By.cssSelector("li:nth-child(" + number + ") .toggle"));
        element.click();
        Thread.sleep(1000);
    }

    private void assertLeft(int expectedLeft) throws InterruptedException {
        WebElement webElement = driver.findElement(By.xpath("//footer/*/span | //footer/span"));
        if(expectedLeft == 1 ) {
            String test = String.format("$d item left", expectedLeft);
            ExpectedConditions.textToBePresentInElement(webElement, test);
        } else {
            String test = String.format("$d items left", expectedLeft);
            ExpectedConditions.textToBePresentInElement(webElement, test);
        }
    }

    @AfterEach
    public void quitDriver() throws InterruptedException {
        driver.quit();
    }
}