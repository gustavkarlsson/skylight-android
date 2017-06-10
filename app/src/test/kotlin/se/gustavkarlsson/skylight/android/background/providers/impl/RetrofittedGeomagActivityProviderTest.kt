package se.gustavkarlsson.skylight.android.background.providers.impl


import okhttp3.*
import org.apache.commons.io.IOUtils
import org.assertj.core.api.Java6Assertions.assertThat
import org.assertj.core.api.Java6Assertions.within
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService


@RunWith(RobolectricTestRunner::class)
class RetrofittedGeomagActivityProviderTest {
	lateinit var mockedClient: OkHttpClient

	@Before
	fun setUp() {
		mockedClient = mockClient(200, "fixtures/kp_index_report.json", "application/json")
	}

	private fun mockClient(statusCode: Int, bodyResourcePath: String, mediaType: String): OkHttpClient {
		val mockedCall = mockCall(statusCode, bodyResourcePath, mediaType)
		val mockedClient = mock(OkHttpClient::class.java)
		`when`(mockedClient.newCall(ArgumentMatchers.any(Request::class.java))).thenReturn(mockedCall)
		return mockedClient
	}

	private fun mockCall(statusCode: Int, bodyResourcePath: String, mediaType: String): Call {
		val body = createResponseBody(bodyResourcePath, mediaType)
		val call = mock(Call::class.java)
		`when`(call.execute()).thenReturn(
				Response.Builder()
						.request(Request.Builder().url("http://mocked.com/").build())
						.code(statusCode)
						.protocol(Protocol.HTTP_1_0)
						.body(body)
						.build())
		return call
	}

	private fun createResponseBody(resourcePath: String, mediaType: String): ResponseBody {
		val bytes = readResourceToByteArray(resourcePath)
		return ResponseBody.create(MediaType.parse(mediaType), bytes)
	}

	private fun readResourceToByteArray(resourcePath: String): ByteArray {
		val classLoader = javaClass.classLoader
		return IOUtils.toByteArray(classLoader.getResource(resourcePath).openStream())
	}

	@Test
	fun parsesKpIndexCorrectly() {
		val service = RetrofittedGeomagActivityProvider(Retrofit.Builder()
				.client(mockedClient)
				.baseUrl("http://mocked.com")
				.addConverterFactory(GsonConverterFactory.create())
				.build()
				.create(KpIndexService::class.java))

		val kpIndex = service.getGeomagActivity().kpIndex

		assertThat(kpIndex).isCloseTo(1.33f, within(0.01f))
	}
}
