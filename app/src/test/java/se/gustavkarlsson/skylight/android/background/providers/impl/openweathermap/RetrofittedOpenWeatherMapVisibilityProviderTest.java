package se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap;


import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
import retrofit2.converter.gson.GsonConverterFactory;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class RetrofittedOpenWeatherMapVisibilityProviderTest {
	private OkHttpClient mockedClient;

	@Before
	public void setUp() throws Exception {
		mockStatic(Log.class);
		mockedClient = mockClient(200, "fixtures/open_weather_map_report.json", "application/json");
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
		RetrofittedOpenWeatherMapVisibilityProvider service = new RetrofittedOpenWeatherMapVisibilityProvider(new Retrofit.Builder()
				.client(mockedClient)
				.baseUrl("http://mocked.com")
				.addConverterFactory(GsonConverterFactory.create())
				.build()
				.create(OpenWeatherMapService.class), "fake-app-id");

		int cloudiness = service.getVisibility(0, 0).getCloudPercentage();

		assertThat(cloudiness).isEqualTo(68);
	}

	@Test
	public void jsonDeserializationWorks() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		String xml = IOUtils.toString(classLoader.getResource("fixtures/open_weather_map_report.json").openStream(), Charset.forName("UTF-8"));

		Gson gson = new Gson();
		OpenWeatherMapWeather weather = gson.fromJson(xml, OpenWeatherMapWeather.class);

		int cloudiness = weather.getClouds().getAll();
		assertThat(cloudiness).isEqualTo(68);
	}
}
