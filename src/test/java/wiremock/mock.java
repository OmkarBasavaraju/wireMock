package wiremock;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class mock {

	public static WireMockServer wireMockServer;
	public static CloseableHttpClient httpClient;
	public static StringEntity entity;
	static mock mm = new mock();

	@BeforeTest
	public static void setup() {
		wireMockServer = new WireMockServer();
		wireMockServer.start();
		httpClient = HttpClients.createDefault();

	}

	@Test
	private static void Stub() throws ClientProtocolException, IOException {

//		WireMock.configureFor("wiremock.host", 8084);
		stubFor(get(urlEqualTo("/restest"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("Omkar")));
		
		HttpGet request = new HttpGet("http://localhost:8080/restest");
		HttpResponse httpResponse = httpClient.execute(request);
		String responseString = (httpResponse).toString();
		
		String s = httpResponse.getStatusLine().toString();
		System.out.println(s);
		InputStream i = httpResponse.getEntity().getContent();
		String sd = convertInputStreamToString(i);
		verify(getRequestedFor(urlEqualTo("/restest")));
		System.out.println(sd);
		Assert.assertEquals("Omkar", sd);
		
		stubFor(post(urlEqualTo("/restest/wiremock"))
				  .withHeader("Content-Type", equalTo("application/json"))
				  .withRequestBody(containing("\"testing-library\": \"WireMock\""))
				  .withRequestBody(containing("\"creator\": \"Tom Akehurst\""))
				  .withRequestBody(containing("\"website\": \"wiremock.org\""))
				  .willReturn(aResponse()
				  .withStatus(200)));
				
	
		HttpPost request1 = new HttpPost("http://localhost:8080/restest/wiremock");
		mm.entityToString();
		request1.setEntity(entity);
		HttpResponse httpResponse1 = httpClient.execute(request1);
//		String responseString1 = (httpResponse).toString();

System.out.println(entity);
InputStream g = httpResponse1.getEntity().getContent();
System.out.println(convertInputStreamToString(g));
		verify(postRequestedFor(urlEqualTo("/restest/wiremock")));
//				  .withHeader("Content-Type", equalTo("application/json")));
				assertEquals(200, httpResponse1.getStatusLine().getStatusCode());
		wireMockServer.stop();
	}
	
	public static String convertInputStreamToString(InputStream inputStream) {
	    Scanner scanner = new Scanner(inputStream, "UTF-8");
	    String string = scanner.useDelimiter("\\Z").next();
	    scanner.close();
	    return string;
	}
	
	public StringEntity entityToString() throws UnsupportedEncodingException {
	
	InputStream jsonInputStream 
	  = this.getClass().getClassLoader().getResourceAsStream("wiremock.json");
//	System.out.println(jsonInputStream);
	String jsonStringh = mm.convertInputStreamToString(jsonInputStream);
	entity = new StringEntity(jsonStringh);
	return entity;
	}
}
