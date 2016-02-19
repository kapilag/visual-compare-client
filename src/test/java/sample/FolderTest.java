package sample;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import http.Configure;
import http.ImgCompare;
import http.ImgCompare.FOLDERS;

import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import commons.Utils;

public class FolderTest {

	private int port = 4001;
	@Rule
	public WireMockRule compareApi = new WireMockRule(port);
	private String pageName = "hello";
	private String root = System.getProperty("user.dir");
	private String basedir= root+File.separator+"base";
	private String newDir = root+File.separator+"new";
	private String diffDir = root+File.separator+"diff";
	private String testResourcePath_1 = root+"/src/test/resources/".replace("/", File.separator)+"1_button.png";
	private String testResourcePath_2 = root+"/src/test/resources/".replace("/", File.separator)+"2_button.png";
	private String responseSucess = "{\"isSameDimensions\":true,\"dimensionDifference\":{\"width\":0,\"height\":0},\"misMatchPercentage\":\"0.00\",\"analysisTime\":68,\"imageDataUrl\":\"data:image/png;base64,iVBORw0KGgoAA\"}";
	private String responseFailure = "{\"isSameDimensions\":false,\"dimensionDifference\":{\"width\":0,\"height\":0},\"misMatchPercentage\":\"10.00\",\"analysisTime\":68,\"imageDataUrl\":\"data:image/png;base64,iVBORw0KGgoAA\"}";
	private Dimension dimension = new Dimension(100, 100);
	public void createStub(String response) {
		 stubFor(post(urlEqualTo("/compare"))
	                .willReturn(aResponse()
	                    .withHeader("Content-Type", "application/json")
	                    .withBody(response)));
	}
	
	@Before
	public void setup() throws IOException{
		//Delete the base folder 
		FileUtils.deleteDirectory(new File(basedir));
		FileUtils.deleteDirectory(new File(newDir));
		FileUtils.deleteDirectory(new File(diffDir));
		FileUtils.copyFile(new File(root+File.separator+"1_button.png"), new File(testResourcePath_1));
		FileUtils.copyFile(new File(root+File.separator+"2_button.png"), new File(testResourcePath_2));
	}

	/*private class TestWebDriver extends HtmlUnitDriver implements TakesScreenshot{
		public <X> X getScreenshotAs(OutputType<X> target)
				throws WebDriverException {
			// TODO Auto-generated method stub
			return null;
		}
	}*/
	private WebDriver getMockedDriver() throws WebDriverException, IOException{
		FirefoxDriver mockDriver = Mockito.mock(FirefoxDriver.class);// or use chromeDriver
		Options opt = Mockito.mock(Options.class);
		Window window = Mockito.mock(Window.class);
		Mockito.when(((TakesScreenshot) mockDriver).getScreenshotAs(OutputType.FILE)).thenReturn(new File(testResourcePath_1));
		Mockito.when(((TakesScreenshot) mockDriver).getScreenshotAs(OutputType.BASE64)).
		thenReturn(Base64.encodeBase64String(FileUtils.readFileToByteArray(new File(testResourcePath_1))));
		
		Mockito.when(mockDriver.manage()).thenReturn(opt);
		Mockito.when(opt.window()).thenReturn(window);
		Mockito.when(mockDriver.manage().window().getSize()).thenReturn(dimension);
		return mockDriver;
	}
	@Test
	public void verifyIfBaseFolderAndNewFolderNotPresentThenItisCreated() throws ClientProtocolException, IOException {
		createStub(responseSucess);
		Configure conf = new Configure("127.0.0.1", port, 10);
		ImgCompare img = new ImgCompare(conf);
		img.snap(pageName, getMockedDriver());
		File base = new File(basedir);
		File newd = new File(newDir);
		Assert.assertTrue(base.isDirectory());
		Assert.assertTrue(newd.isDirectory());
		//Verify base File is the file Returned by WebDriver
		String baseStringFile = FileUtils.readFileToString(new File(conf.getBaselineImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.base)));
		String newFolderStringFile = FileUtils.readFileToString(new File(conf.getNewImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.newF)));
		Assert.assertEquals(baseStringFile, newFolderStringFile);
	}
	
	@Test
	public void verifyIfBaseFolderPresentAndUpdateBaseLineFlagFalseThenContentsAreNotOverwritten() throws IOException{
		createStub(responseSucess);
		Configure conf = new Configure("127.0.0.1", port, 10);
		ImgCompare img = new ImgCompare(conf);
		WebDriver driver = getMockedDriver();
		FileUtils.copyFile(new File(testResourcePath_2), new File(conf.getBaselineImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.base)));
		img.snap(pageName, driver);
		String baseLineFile = FileUtils.readFileToString(new File(conf.getBaselineImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.base)));
		String newFolderFile = FileUtils.readFileToString(new File(conf.getNewImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.newF)));
		Assert.assertNotEquals(baseLineFile, newFolderFile);
	}
	
	@Test
	public void verifyIfUpdateBaselineFlagisTrueThenImagesAreUpdatedInBaseLine() throws WebDriverException, IOException{
		createStub(responseSucess);
		Configure conf = new Configure("127.0.0.1", port, 10);
		conf.setUpdateBaseLine(true);
		ImgCompare img = new ImgCompare(conf);
		WebDriver driver = getMockedDriver();
		FileUtils.copyFile(new File(testResourcePath_2), new File(conf.getBaselineImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.base)));
		img.snap(pageName, driver);
		String baseLineFile = FileUtils.readFileToString(new File(conf.getBaselineImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.base)));
		String newFolderFile = FileUtils.readFileToString(new File(conf.getNewImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.newF)));
		Assert.assertEquals(baseLineFile, newFolderFile);
	}
	
	@Test
	public void verifyIfMismatchPercentageGreaterThanConfiguredThenDiffImageisCreated() throws IOException{
		createStub(responseFailure);
		Configure conf = new Configure("127.0.0.1", port, 9);
		FileUtils.copyFile(new File(testResourcePath_2), new File(conf.getBaselineImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.base)));
		ImgCompare img = new ImgCompare(conf);
		WebDriver driver = getMockedDriver();
		img.snap(pageName, driver);
		File diffImage = new File(conf.getDiffImageFolder()+Utils.getImagesNameForFolder(pageName, dimension, FOLDERS.diff));
		Assert.assertTrue(diffImage.exists());
	}
	
	@Test
	public void verifyIfImagesAreDifferentAndExpectedMismatchExpectedIsGreater() throws IOException{
		createStub(responseFailure);
		Configure conf = new Configure("127.0.0.1", port, 11);
		FileUtils.copyFile(new File(testResourcePath_2), new File(conf.getBaselineImageFolder()+Utils.getImagesNameForFolder(pageName,dimension, FOLDERS.base)));
		ImgCompare img = new ImgCompare(conf);
		WebDriver driver = getMockedDriver();
		img.snap(pageName, driver);
		File diffImage = new File(conf.getDiffImageFolder()+Utils.getImagesNameForFolder(pageName, dimension, FOLDERS.diff));
		Assert.assertFalse(diffImage.exists());
	}

}
