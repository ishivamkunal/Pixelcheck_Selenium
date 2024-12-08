package screenshot;

import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenshotUtility {

    private static final String BASELINE_PATH = System.getProperty("user.dir") + "\\src\\images\\baseline\\";
    private static final String SCREENSHOTS_PATH = System.getProperty("user.dir") + "\\src\\images\\screenshots\\";
    private static final String DIFF_IMAGES_PATH = System.getProperty("user.dir") + "\\src\\images\\diffImages\\";

    public boolean areImagesEqual(String baseline, String screenshot) {
        BufferedImage imgBaseline = null;
        BufferedImage imgScreenshot = null;

        try {
            File baselineFile = new File(BASELINE_PATH + baseline + ".png");
            File screenshotFile = new File(SCREENSHOTS_PATH + screenshot + ".png");

            if (!baselineFile.exists() || !screenshotFile.exists()) {
                System.err.println("Baseline or Screenshot image not found: "
                        + baselineFile.getAbsolutePath() + " or " + screenshotFile.getAbsolutePath());
                return false;
            }

            imgBaseline = ImageIO.read(baselineFile);
            imgScreenshot = ImageIO.read(screenshotFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        ImageDiff diff = new ImageDiffer().makeDiff(imgBaseline, imgScreenshot);
        boolean isDifferent = diff.hasDiff();

        if (isDifferent) {
            BufferedImage diffImage = diff.getMarkedImage();
            try {
                File diffFile = new File(DIFF_IMAGES_PATH + baseline + ".png");
                ImageIO.write(diffImage, "png", diffFile);
                System.out.println("Image difference detected. Diff image saved at: " + diffFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Images are identical: " + baseline + " and " + screenshot);
        }

        return !isDifferent;
    }

    public void takePageScreenshotImproved(WebDriver driver, String name) {
        try {
            Screenshot screen = new AShot()
                    .shootingStrategy(ShootingStrategies.viewportPasting(100))
                    .takeScreenshot(driver);

            File file = new File(SCREENSHOTS_PATH + name + ".png");
            ImageIO.write(screen.getImage(), "png", file);
            System.out.println("Screenshot saved at: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareBaseline(WebDriver driver, String name) {
        try {
            Screenshot screen = new AShot()
                    .shootingStrategy(ShootingStrategies.viewportPasting(100))
                    .takeScreenshot(driver);

            File file = new File(BASELINE_PATH + name + ".png");
            ImageIO.write(screen.getImage(), "png", file);
            System.out.println("Baseline image saved at: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
