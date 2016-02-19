package sample;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class MockServer {
	
	
	private String response = "{\"isSameDimensions\":true,\"dimensionDifference\":{\"width\":0,\"height\":0},\"misMatchPercentage\":\"0.00\",\"analysisTime\":68,\"imageDataUrl\":\"data:image/png;base64,iVBORw0KGgoAA\"}";
	public void createStub() {
		stubFor(get(urlEqualTo("/compare")).withHeader("Accept",
				equalTo("text/json"))
				.willReturn(
						aResponse().withStatus(200)
								.withHeader("Content-Type", "text/json")
								.withBody(response)));
	}

	
	@Test
	public void simpleTest(){
		 WireMockRule compareApi = new WireMockRule(8089);
		System.out.println("hello");
	}

}
