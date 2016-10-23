package se.gustavkarlsson.aurora_notifier.android.providers.impl;


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
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.providers.Weather;
import se.gustavkarlsson.aurora_notifier.android.providers.services.OpenWeatherMapService;
import se.gustavkarlsson.aurora_notifier.android.providers.services.OpenWeatherMapWeather;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.within;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RetrofittedKpIndexProviderTest {
	private OkHttpClient mockedClient;

	@Before
	public void setUp() throws Exception {
		mockedClient = mockClient(200, "fixtures/kp_index_report.json", "application/json");
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
	public void parsesKpIndexCorrectly() throws Exception {
		RetrofittedKpIndexProvider service = new RetrofittedKpIndexProvider(new Retrofit.Builder()
				.client(mockedClient)
				.baseUrl("http://mocked.com")
				.addConverterFactory(GsonConverterFactory.create())
				.build()
				.create(KpIndexService.class));

		Float kpIndex = service.getKpIndex().getValue();

		assertThat(kpIndex).isCloseTo(1.33F, within(0.01F));
	}
}
