import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import screenshot.ScreenshotUtility;

public class FirstTest {
    WebDriver driver; // Declare WebDriver at class level

    @BeforeClass
    public void setupDriver() {
        // Set up the driver
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @DataProvider(name = "urls")
    public Object[][] urls() {
        return new Object[][]{
                {"https://magento.softwaretestingboard.com/", "HomePage"},
                {"https://magento.softwaretestingboard.com/women/tops-women/jackets-women.html", "WomenJackets"},
                {"https://magento.softwaretestingboard.com/women/bottoms-women/pants-women.html", "WomenPants"},
                {"https://magento.softwaretestingboard.com/men/tops-men/jackets-men.html", "MenJackets"},
                {"https://magento.softwaretestingboard.com/men/bottoms-men/pants-men.html", "MenPants"}
        };
    }

    @Test(dataProvider = "urls")
    public void prepareBaseline(String url, String name) {
        // Navigate to the URL
        driver.get(url);

        // Capture baseline screenshot
        new ScreenshotUtility().prepareBaseline(driver, name);
    }

    @Test
    public void compareImages() {
        // Capture and compare screenshots for HomePage
        String screenshotName = "HomePage";
        new ScreenshotUtility().takePageScreenshotImproved(driver, screenshotName);
        Assert.assertTrue(
                new ScreenshotUtility().areImagesEqual(screenshotName, screenshotName),
                "Image comparison failed for: " + screenshotName
        );
    }

    @AfterClass
    public void tearDown() {
        // Quit the driver if not null
        if (driver != null) {
            driver.quit();
        }
    }
}
