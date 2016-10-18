package se.gustavkarlsson.aurora_notifier.android.service;


import static org.mockito.Mockito.*;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.services.OpenWeatherMapService;
import se.gustavkarlsson.aurora_notifier.android.services.RetrofittedOpenWeatherMapService;
import se.gustavkarlsson.aurora_notifier.android.services.Weather;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class RetrofittedOpenWeatherMapServiceTest {

	private OkHttpClient mockedClient;

	@Before
	public void setUp() throws Exception {
		mockedClient = mockClient(200, "fixtures/open_weather_map_report.xml", "application/xml");
	}

	private OkHttpClient mockClient(int statusCode, String bodyResourcePath, String mediaType) throws IOException {
		Call mockedCall = mockCall(statusCode, bodyResourcePath, mediaType);
		OkHttpClient mockedClient = mock(OkHttpClient.class);
		when(mockedClient.newCall(any(Request.class))).thenReturn(mockedCall);
		return mockedClient;
	}

	private Call mockCall(int statusCode, String bodyResourcePath, String mediaType) throws IOException {
		ResponseBody body = createResponseBody(bodyResourcePath, mediaType);
		Call call = mock(Call.class);
		when(call.execute()).thenReturn(
				new Response.Builder()
						.request(new Request.Builder().url("http://mocked.com/").build())
						.code(statusCode)
						.protocol(Protocol.HTTP_1_0)
						.body(body)
						.build());
		return call;
	}

	private ResponseBody createResponseBody(String resourcePath, String mediaType) throws IOException {
		byte[] bytes = readResourceToByteArray(resourcePath);
		return ResponseBody.create(MediaType.parse(mediaType), bytes);
	}

	private byte[] readResourceToByteArray(String resourcePath) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		return IOUtils.toByteArray(classLoader.getResource(resourcePath).openStream());
	}

	@Test
	public void parsesCloudinessCorrectly() throws Exception {
		RetrofittedOpenWeatherMapService service = new RetrofittedOpenWeatherMapService(new Retrofit.Builder()
				.client(mockedClient)
				.baseUrl("http://mocked.com")
				.addConverterFactory(SimpleXmlConverterFactory.create())
				.build()
				.create(OpenWeatherMapService.class));
		Timestamped<Weather> weather = service.getWeather(0, 0);
		Assert.assertEquals("68", weather.getValue().getCloudiness());

	}

	@Test
	public void fails2() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		String xml = IOUtils.toString(classLoader.getResource("fixtures/open_weather_map_report.xml").openStream(), Charset.forName("UTF-8"));

		Serializer serializer = new Persister();
		Weather weather = serializer.read(Weather.class, xml);

		Assert.assertEquals("68", weather.getCloudiness());
	}
}
