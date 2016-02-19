import java.io.IOException;

import http.Configure;
import http.ImgCompare;

import org.apache.http.client.ClientProtocolException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class TestClient {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		WebDriver driver = new FirefoxDriver();
		driver.manage().window().setSize(new Dimension(1024, 500));
		//9780387848570,978-1-4471-4736-7
		//driver.get("http://www.springer.com/in/book/9780387848570");
		driver.get("https://wwww.google.com");
		Configure conf = new Configure("127.0.0.1", 3000, 10);
		conf.setUpdateBaseLine(false);
		ImgCompare compare = new ImgCompare(conf);
		System.out.println(compare.snap("LoginPage", driver));
		driver.quit();

	}

}
