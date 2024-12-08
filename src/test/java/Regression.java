import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import screenshot.ScreenshotUtility;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;

public class Regression {

    ExtentReports report;
    ExtentTest test;
    WebDriver driver;

    @BeforeClass
    public void beforeClass() throws InterruptedException {
        // Initialize ExtentReports and clean directories
        report = new ExtentReports();
        try {
            FileUtils.cleanDirectory(new File(System.getProperty("user.dir") + "\\src\\images\\diffImages"));
            FileUtils.cleanDirectory(new File(System.getProperty("user.dir") + "\\src\\images\\screenshots"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up WebDriver
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @DataProvider(name = "urls")
    public static Object[][] urls() {
        // Provide test URLs and names
        return new Object[][]{
                {"https://magento.softwaretestingboard.com/", "HomePage"},
                {"https://magento.softwaretestingboard.com/women/tops-women/jackets-women.html", "WomenJackets"},
                {"https://magento.softwaretestingboard.com/women/bottoms-women/pants-women.html", "WomenPants"},
                {"https://magento.softwaretestingboard.com/men/tops-men/jackets-men.html", "MenJackets"},
                {"https://magento.softwaretestingboard.com/men/bottoms-men/pants-men.html", "MenPants"}
        };
    }

    @Test(dataProvider = "urls")
    public void regression(String url, String name, Method method, ITestContext context) {
        context.getCurrentXmlTest().addParameter("image", name);
        test = report.createTest(method.getName() + " || " + url);

        // Navigate to the URL
        driver.get(url);

        // Take screenshot and compare images
        new ScreenshotUtility().takePageScreenshotImproved(driver, name);
        Assert.assertTrue(new ScreenshotUtility().areImagesEqual(name, name), "Image comparison failed for: " + name);
    }

    @AfterMethod
    public void afterMethod(ITestResult result, ITestContext context) {
        // Log test result to ExtentReports
        String image = context.getCurrentXmlTest().getParameter("image");

        if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test passed");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            String diffImagePath = System.getProperty("user.dir") + "\\src\\images\\diffImages\\" + image + ".png";
            test.addScreenCaptureFromPath(diffImagePath);
            test.fail("Test failed");
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("Test skipped");
        }
    }

    @AfterClass
    public void afterClass() {
        // Clean up WebDriver and save the report
        driver.quit();
        report.flush();
    }
}
