package com.welgram.crawler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class TesseractTest {
    private WebDriver driver;
    public final static Logger logger = LoggerFactory.getLogger(TesseractTest.class);

//    @Before
//    public void setUp() {
//        String crawlerDir = PropertyUtil.get("crawler.dir");
//        String driverPath = PropertyUtil.get("chrome.driver.path");
//        String chromeDriverPath = crawlerDir + driverPath;
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
//
//        driver = new ChromeDriver();
//    }
//
//    @After
//    public void close() {
//        driver.close();
//    }


    //이미지 파일에서 텍스트 추출하기
    @Test
    public void getTextFromImageFile() {
        File filePath = new File("C:\\Users\\hayeon\\Pictures\\capture\\capture.png");
        BufferedImage img = null;

        String text = "";
        try {
            img = ImageIO.read(filePath);

            Tesseract instance = new Tesseract();
            instance.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
            instance.setLanguage("kor");

            text = instance.doOCR(img);
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("추출된 문자열 : {}", text);
    }
}
