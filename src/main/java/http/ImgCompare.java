package http;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import request.ImageObj;
import request.Images;
import response.ComparedImageData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import commons.Utils;

public class ImgCompare {
	private Configure conf;
	private WebDriver driver;
	private static final org.apache.log4j.Logger LOGGER  = Logger.getLogger(ImgCompare.class); 

	public ImgCompare(Configure configuration) {
		this.conf = configuration;
		createCleanNewImageFolder();
		createCleanUpdateBaseLineImageFolder();
		deleteCreateDIffImageFolder();
	}
	
	private Dimension getDriverDimension(){
		return  driver.manage().window().getSize();
	}
	
	public enum FOLDERS{base,newF,diff};
	
	

	public boolean snap(String pageName, WebDriver driver)
			throws ClientProtocolException, IOException {
		 
		this.driver = driver;
		//Check if baseline image is present or not else no need to compare
		String baseLineImageName = Utils.getImagesNameForFolder(pageName,getDriverDimension(),FOLDERS.base);
		if (!conf.isUpdateBaseLine()) {
			String imagePath = conf.getBaselineImageFolder() + Utils.getImagesNameForFolder(pageName,getDriverDimension(), FOLDERS.base);
			String baseLine64 = Utils.getBase64encodedString(imagePath);
			if (null == baseLine64) {
				Utils.decodeBase64encodedString(
						getBase64ImageOfBrowser(driver, pageName),
						conf.getBaselineImageFolder() + baseLineImageName);
				return true;
			} else {
				return compare(baseLine64,
						getBase64ImageOfBrowser(driver, pageName), pageName);
			}
		} else {
			Utils.decodeBase64encodedString(
					getBase64ImageOfBrowser(driver, pageName),
					conf.getBaselineImageFolder() + baseLineImageName);
			return true;
		}
	}

	private String getBase64ImageOfBrowser(WebDriver driver, String pageName)
			throws IOException {
		File f = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.moveFile(f, new File(conf.getNewImageFolder() + Utils.getImagesNameForFolder(pageName,getDriverDimension(), FOLDERS.newF)));
		return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
	}

	private void createCleanNewImageFolder() {
		// check if newFolder is present
		try {
			File newImageFolder = new File(conf.getNewImageFolder());
			FileUtils.forceMkdir(newImageFolder);
			FileUtils.deleteDirectory(newImageFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Clean it else create new folder
	}

	private void createCleanUpdateBaseLineImageFolder() {
		File baseLineImageFolder = new File(conf.getBaselineImageFolder());
		try {
			FileUtils.forceMkdir(baseLineImageFolder);
			if (conf.isUpdateBaseLine())
				FileUtils.deleteDirectory(baseLineImageFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteCreateDIffImageFolder(){
		File diffImage = new File(conf.getDiffImageFolder());
		if(diffImage.isDirectory()){
			try {
				FileUtils.deleteDirectory(diffImage);
				FileUtils.forceMkdir(diffImage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private boolean compare(String image1, String image2, String pageName)
			throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(conf.getApiEndPoint());
		post.addHeader("Content-Type", "application/json");// app/json
		post.setEntity(new StringEntity(getRequestJson(image1, image2)));
		CloseableHttpResponse resp = httpClient.execute(post);
		HttpEntity responseE = resp.getEntity();
		ObjectMapper mapper = new ObjectMapper();
		String res = EntityUtils.toString(responseE);
		LOGGER.debug(res);
		/*CompareImageData1 imageMeta = mapper.readValue(
				res, CompareImageData1.class);*/
		ComparedImageData imageMeta = mapper.readValue(
				res, ComparedImageData.class);
		Float mismatchPercentage = Float.parseFloat(imageMeta.getMisMatchPercentage());
		if(conf.getMismatchPerc() < mismatchPercentage){
		Utils.decodeBase64encodedString(
				imageMeta.getImageDataUrl().split(",")[1],
				conf.getDiffImageFolder() + Utils.getImagesNameForFolder(pageName,getDriverDimension(), FOLDERS.diff));
		return false;
		}
		else{
			return true;
		}
	}

	private String getRequestJson(String img1_64, String img2_64)
			throws JsonProcessingException {
		ImageObj obj = new ImageObj();
		Images toCompare = new Images();
		toCompare.setBefore("data:image/png;base64," + img1_64);
		toCompare.setAfter("data:image/png;base64," + img2_64);
		obj.setImages(toCompare);
		ObjectMapper mapper = new ObjectMapper();
		String requestText = mapper.writeValueAsString(obj);
		//System.out.println(requestText);
		return requestText;

	}

}
